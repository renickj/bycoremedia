package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.common.util.Function;
import com.coremedia.rest.validation.Severity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UniqueInSiteStringValidatorTest extends UniqueStringValidatorTestBase {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private SitesService sitesService;

  @Mock
  private Site site1;

  @Mock
  private Site site2;


  @Test
  public void testValidateMultipleContentsInSameSiteWithSameValue() throws Exception {
    Content content1 = contentOfSite(site1);
    Content content2 = contentOfSite(site1);
    String value = "value";
    when(content1.getString(PROPERTY)).thenReturn(value);
    when(content2.getString(PROPERTY)).thenReturn(value);
    map.put(value, content1);
    map.put(value, content2);
    newValidator().validate(content1, issues);

    verify(issues).addIssue(Severity.ERROR, PROPERTY, UniqueInSiteStringValidator.class.getSimpleName(), content2);
  }

  @Test
  public void testValidateMultipleContentsOfDifferentSitesWithSameValue() throws Exception {
    Content content1 = contentOfSite(site1);
    Content content2 = contentOfSite(site2);
    String value = "value";
    when(content1.getString(PROPERTY)).thenReturn(value);
    when(content2.getString(PROPERTY)).thenReturn(value);
    map.put(value, content1);
    map.put(value, content2);
    newValidator().validate(content1, issues);

    verifyZeroInteractions(issues);
  }

  @Test
  public void testValidateMultipleContentsOneWithoutSiteWithSameValue() throws Exception {
    Content content1 = contentOfSite(site1);
    Content content2 = contentOfSite(null);
    String value = "value";
    when(content1.getString(PROPERTY)).thenReturn(value);
    when(content2.getString(PROPERTY)).thenReturn(value);
    map.put(value, content1);
    map.put(value, content2);
    newValidator().validate(content1, issues);

    verifyZeroInteractions(issues);
  }

  @Test
  public void testValidateMultipleContentsWithoutSiteWithSameValue() throws Exception {
    Content content1 = contentOfSite(null);
    Content content2 = contentOfSite(null);
    String value = "value";
    when(content1.getString(PROPERTY)).thenReturn(value);
    when(content2.getString(PROPERTY)).thenReturn(value);
    map.put(value, content1);
    map.put(value, content2);
    newValidator().validate(content1, issues);

    verify(issues).addIssue(Severity.ERROR, PROPERTY, UniqueInSiteStringValidator.class.getSimpleName(), content2);
  }

  // ----------------------------------------------------------------------

  @Override
  UniqueInSiteStringValidator newValidator(String contentType, String property) throws Exception {
    UniqueInSiteStringValidator validator = new UniqueInSiteStringValidator(contentRepository, contentType, property,
            new Function<String, Set<Content>>() {
              @Override
              public Set<Content> apply(String input) {
                return map.get(input);
              }
            }, sitesService);
    validator.afterPropertiesSet();
    return validator;
  }

  private Content contentOfSite(Site site) {
    Content content = mock(Content.class);
    when(sitesService.getContentSiteAspect(content).getSite()).thenReturn(site);
    return content;
  }
}