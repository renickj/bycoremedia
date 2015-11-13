package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.ContentSiteAspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.SpringCommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static com.coremedia.rest.validation.Severity.ERROR;
import static com.coremedia.rest.validation.Severity.WARN;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalChannelExternalIdValidatorTest extends AbstractCatalogLinkValidatorTest {

  @Test
  public void testEmptyPropertyValue() throws Exception {
    testling.emptyPropertyValue(issues);
    verify(issues, times(1)).addIssue(eq(ERROR), eq(PROPERTY_NAME), eq(CODE_ISSUE_CATEGORY_EMPTY));
  }

  @Test
  public void testEmptyExternalIdForOtherPages() throws Exception {
    when(externalChannel.getStruct("localSettings")).thenReturn(localSettingsStruct);
    when(localSettingsStruct.get("catalog")).thenReturn(false);

    testling.emptyPropertyValue(issues);
    verify(issues, times(1)).addIssue(eq(ERROR), eq(PROPERTY_NAME), eq(CODE_ISSUE_CATEGORY_EMPTY));
  }

  @Test
  public void testEmptyHomepagePropertyValue() throws Exception {
    when(externalChannel.getString(PROPERTY_NAME)).thenReturn("   ");
    when(site.getSiteRootDocument()).thenReturn(externalChannel);
    when(externalChannel.getStruct("localSettings")).thenReturn(localSettingsStruct);
    when(localSettingsStruct.get("catalog")).thenReturn(false);

    testling.validate(externalChannel, issues);
    verify(issues, never()).addIssue(any(Severity.class), anyString(), anyString());
  }

  @Test
  public void testInvalidStoreContext() throws Exception {
    testling.invalidStoreContext(issues);
    verify(issues, times(1)).addIssue(eq(WARN), eq(PROPERTY_NAME), eq(CODE_ISSUE_CONTEXT_INVALID));
  }

  @Test
  public void testStoreContextNotFound() throws Exception {
    testling.storeContextNotFound(issues);
    verify(issues, times(1)).addIssue(eq(WARN), eq(PROPERTY_NAME), eq(CODE_ISSUE_CONTEXT_INVALID));
  }

  @Test
  public void testInvalidExternalId() throws Exception {
    testling.validate(externalChannel, issues);
    verify(issues, times(1)).addIssue(eq(WARN), eq(PROPERTY_NAME), eq(CODE_ISSUE_ID_INVALID), anyString(), anyString());
  }

  @Test
  public void testInvalidExternalIdForOtherPages() throws Exception {
    when(externalChannel.getStruct("localSettings")).thenReturn(localSettingsStruct);
    when(localSettingsStruct.get("catalog")).thenReturn(false);
    testling.validate(externalChannel, issues);
    verify(issues, never()).addIssue(any(Severity.class), eq(PROPERTY_NAME), eq(CODE_ISSUE_ID_INVALID));
  }

  @Test(expected = IllegalStateException.class)
  public void validateUniquenessNoSiteFound() {
    when(sitesService.getContentSiteAspect(externalChannel).getSite()).thenReturn(null);
    testling.validate(externalChannel, issues);
  }

  @Test
  public void validateUniquenessBlankExternalId() {
    when(externalChannel.getString(PROPERTY_NAME)).thenReturn("   ");
    testling.validate(externalChannel, issues);
    verify(issues, times(1)).addIssue(eq(ERROR), eq(PROPERTY_NAME), eq(CODE_ISSUE_CATEGORY_EMPTY));
  }

  @Test
  public void validateUniquenessNoDuplicateFound() {
    when(cache.get(testling.new DuplicateExternalIdCacheKey(externalChannel, rootChannel))).thenReturn(null);
    testling.validate(externalChannel, issues);
    verify(issues, never()).addIssue(any(Severity.class), anyString(), anyString());
  }

  @Test
  public void validateUniquenessDuplicateFound() {
    testling.validate(externalChannel, issues);
    verify(issues, times(1)).addIssue(eq(ERROR), eq(PROPERTY_NAME), eq(CODE_ISSUE_DUPLICATE_EXTERNAL_ID), anyString(), anyObject());
  }

  @Test
  public void validateUniquenessOtherPageWithDuplicateId() {
    when(externalChannel.getStruct("localSettings")).thenReturn(localSettingsStruct);
    when(localSettingsStruct.get("catalog")).thenReturn(false);

    testling.validate(externalChannel, issues);
    verify(issues, times(1)).addIssue(eq(ERROR), eq(PROPERTY_NAME), eq(CODE_ISSUE_DUPLICATE_EXTERNAL_ID), anyString(), anyObject());
  }

  @Test
  public void validateUniquenessOtherPageWithNoDuplicateId() {
    when(externalChannel.getStruct("localSettings")).thenReturn(localSettingsStruct);
    when(localSettingsStruct.get("catalog")).thenReturn(false);
    when(cache.get(testling.new DuplicateExternalIdCacheKey(externalChannel, rootChannel))).thenReturn(null);

    testling.validate(externalChannel, issues);
    verify(issues, never()).addIssue(any(Severity.class), anyString(), anyString());
  }

  @Test
  public void validOnlyInWorkspace() {

    commerceConnection = new BaseCommerceConnection();
    Commerce.setCurrentConnection(commerceConnection);
    ((BaseCommerceConnection)commerceConnection).setWorkspaceService(workspaceService);
    ((BaseCommerceConnection)commerceConnection).setIdProvider(new BaseCommerceIdProvider("vendor"));

    final String WORKSPACE_1 = "workspace1";
    final String WORKSPACE_2 = "workspace2";
    ArrayList<Workspace> workspaces = new ArrayList<>();
    Workspace workspace1 = mock(Workspace.class);
    Workspace workspace2 = mock(Workspace.class);
    when(workspace1.getName()).thenReturn(WORKSPACE_1);
    when(workspace1.getExternalTechId()).thenReturn(WORKSPACE_1);
    when(workspace2.getName()).thenReturn(WORKSPACE_2);
    when(workspace2.getExternalTechId()).thenReturn(WORKSPACE_2);
    workspaces.add(workspace1);
    workspaces.add(workspace2);
    when(workspaceService.findAllWorkspaces()).thenReturn(workspaces);
    when(externalChannel.getString(PROPERTY_NAME)).thenReturn("ibm:///catalog/category/buxtehude");

    StoreContext currentContext = StoreContextHelper.createContext("myConfigId", "10001", "aurora", "10001", "en_US", "USD");
    currentContext.setWorkspaceId(WORKSPACE_1);
    commerceConnection.setStoreContext(currentContext);

    CommerceBeanFactory commerceBeanFactory = new SpringCommerceBeanFactory() {
      @Override
      public CommerceBean loadBeanFor(@Nonnull String id, @Nonnull StoreContext context) throws CommerceException {
        if (WORKSPACE_1.equals(context.getWorkspaceId())) {
          return mock(CommerceBean.class);
        }
        return null;
      }
    };

    ((BaseCommerceConnection)commerceConnection).setCommerceBeanFactory(commerceBeanFactory);

    testling.validate(externalChannel, issues);
    verify(issues, times(1)).addIssue(eq(WARN), eq(PROPERTY_NAME),
            eq(ExternalChannelExternalIdValidator.CODE_ISSUE_ID_VALID_ONLY_IN_A_WORKSPACE),
            any(), any(), eq(WORKSPACE_1));
  }

  @Before
  public void setupDefault() {
    super.init();

    Commerce.setCurrentConnection(commerceConnection);

    testling = new ExternalChannelExternalIdValidator();
    testling.setPropertyName(PROPERTY_NAME);
    testling.setCache(cache);
    testling.setSitesService(sitesService);

    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);

    when(externalChannel.getId()).thenReturn(EXTERNAL_ID);
    when(externalChannel.isInProduction()).thenReturn(true);
    when(externalChannel.getString(PROPERTY_NAME)).thenReturn(REFERENCE_ID);
    when(externalChannel.getType()).thenReturn(externalChannelType);
    when(externalChannelType.isSubtypeOf(CMExternalChannel.NAME)).thenReturn(true);
    when(defaultCategory.getExternalId()).thenReturn(EXTERNAL_ID);
    when(cache.get(testling.new DuplicateExternalIdCacheKey(externalChannel, rootChannel))).thenReturn(duplicate);
    when(sitesService.getContentSiteAspect(externalChannel)).thenReturn(contentSiteAspect);
    when(contentSiteAspect.getSite()).thenReturn(site);
    when(site.getSiteRootDocument()).thenReturn(rootChannel);
    when(workspaceService.findAllWorkspaces()).thenReturn(new ArrayList<Workspace>());


    when(rootChannel.getId()).thenReturn(ROOT_CHANNEL_ID);
    when(rootChannel.getType()).thenReturn(rootChannelType);
    when(rootChannel.getType().isSubtypeOf(CMExternalChannel.NAME)).thenReturn(false);
  }

  private ExternalChannelExternalIdValidator testling;

  @Mock
  private Issues issues;

  @Mock
  private Cache cache;

  @Mock
  private Commerce commerce;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private Content rootChannel;

  @Mock
  private ContentType rootChannelType;

  @Mock
  private Content externalChannel;

  @Mock
  private ContentType externalChannelType;

  @Mock
  private Content duplicate;

  @Mock
  private WorkspaceService workspaceService;

  @Mock
  private CommerceIdProvider commerceIdProvider;

  @Mock
  private Category defaultCategory;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private SitesService sitesService;

  @Mock
  private Site site;

  @Mock
  private ContentSiteAspect contentSiteAspect;

  @Mock
  private Struct localSettingsStruct;

  private static final String CODE_ISSUE_CATEGORY_EMPTY = "externalChannelEmptyCategory";
  private static final String CODE_ISSUE_ID_INVALID = "externalChannelInvalidId";
  private static final String CODE_ISSUE_DUPLICATE_EXTERNAL_ID = "externalChannelInvalidDuplicate";
  private static final String CODE_ISSUE_CONTEXT_INVALID = "externalChannelInvalidStoreContext";
  private static final String PROPERTY_NAME = "Hyperspace";
  private static final String EXTERNAL_ID = "By-products of Designer People";
  private static final String ROOT_CHANNEL_ID = "42";
  private static final String REFERENCE_ID = "vendor:///catalog/product/foo";

}
