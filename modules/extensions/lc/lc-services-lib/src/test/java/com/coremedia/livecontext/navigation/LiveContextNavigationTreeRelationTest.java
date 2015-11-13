package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.tree.ChildrenLinkListContentTreeRelation;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextNavigationTreeRelationTest {
  private static final String SITE_ID = "aSiteId";

  @Mock
  private ChildrenLinkListContentTreeRelation childrenLinkListContentTreeRelation;

  @Mock
  private ContentBeanFactory contentBeanFactory;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private LiveContextNavigationFactory navigationFactory;

  @Mock
  private Category testCategory;

  @Mock
  private LiveContextNavigationTreeRelation treeRelation;

  @Mock
  private SitesService sitesService;

  @Mock
  private Site site;

  private LiveContextNavigationTreeRelation testling;

  private LiveContextNavigation testNavigation;

  @Mock
  private QueryService queryService;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    when(contentRepository.getQueryService()).thenReturn(queryService);
    testling = new LiveContextNavigationTreeRelation();
    testling.setContentBasedTreeRelation(childrenLinkListContentTreeRelation);
    testling.setContentBeanFactory(contentBeanFactory);
    testling.setContentRepository(contentRepository);
    testling.setNavigationFactory(navigationFactory);
    testling.setCache(new Cache("test"));

    testNavigation = new LiveContextCategoryNavigation(testCategory, site, treeRelation);
    when(sitesService.getSite(SITE_ID)).thenReturn(site);
  }

  @Test
  public void testGetChildrenOf() throws Exception {
    Category categoryChild1 = mock(Category.class);
    Category categoryChild2 = mock(Category.class);

    List<Category> categoryChildren = new ArrayList<>();
    categoryChildren.add(categoryChild1);
    categoryChildren.add(categoryChild2);

    when(testCategory.getChildren()).thenReturn(categoryChildren);
    when(navigationFactory.createNavigation(categoryChild1, site)).thenReturn(new LiveContextCategoryNavigation(categoryChild1, site, treeRelation));
    when(navigationFactory.createNavigation(categoryChild2, site)).thenReturn(new LiveContextCategoryNavigation(categoryChild2, site, treeRelation));

    Collection<Linkable> childrenOf = testling.getChildrenOf(testNavigation);

    assertEquals(2, childrenOf.size());
    Iterator<Linkable> iterator = childrenOf.iterator();
    LiveContextNavigation firstChild = (LiveContextNavigation) iterator.next();
    LiveContextNavigation secondChild = (LiveContextNavigation) iterator.next();
    assertSame(categoryChild1, firstChild.getCategory());
    assertSame(categoryChild2, secondChild.getCategory());
  }

  @Test
  public void testGetParentOfIsNull() throws Exception {
    mockExternalChannelsForCategory(testCategory, "testGetParentOfIsNull", new ArrayList<Content>());
    when(testCategory.getParent()).thenReturn(null);
    Linkable parent = testling.getParentOf(testNavigation);
    assertNull(parent);
  }

  @Test
  public void testGetParentOfParentOnlyInCMS() throws Exception {

    //no parent Category for child category
    when(testCategory.getParent()).thenReturn(null);

    //Configure Query Service to return a content for this external channel
    Content childContent = mock(Content.class);
    List<Content> foundContent = new ArrayList<>();
    foundContent.add(childContent);
    mockExternalChannelsForCategory(testCategory, "testGetParentOfOneContentFound", foundContent);

    //configure the creation of a bean for an internal channel
    Content channelParent = mock(Content.class);
    when(childrenLinkListContentTreeRelation.getParentOf(childContent)).thenReturn(channelParent);
    CMChannel wrappedExternalChannelParent = mock(CMChannel.class);
    when(contentBeanFactory.createBeanFor(channelParent, Navigation.class)).thenReturn(wrappedExternalChannelParent);

    when(site.getSiteRootDocument()).thenReturn(channelParent);
    when(childrenLinkListContentTreeRelation.pathToRoot(childContent)).thenReturn(Collections.singletonList(channelParent));

    //trigger method and assertions.
    Linkable parent = testling.getParentOf(testNavigation);
    assertSame(wrappedExternalChannelParent, parent);
  }

  @Test
  public void testGetParentOfInternalRepresentationFoundForParentCategory() throws Exception {

    //configure: A Category (representation in ecommerce) exist.
    Category parentCategory = mock(Category.class);
    when(testCategory.getParent()).thenReturn(parentCategory);

    //configure: A Content can be found for the external category
    Content externalNavigationContent = mock(Content.class);
    mockExternalChannelsForCategory(parentCategory, "testGetParentOfParentCategory-parent", Collections.singletonList(externalNavigationContent));

    //configure creation of ContentBean
    CMExternalChannel externalNavigation = mock(CMExternalChannel.class);
    when(contentBeanFactory.createBeanFor(externalNavigationContent, CMExternalChannel.class)).thenReturn(externalNavigation);
    when(contentBeanFactory.createBeanFor(externalNavigationContent, CMNavigation.class)).thenReturn(externalNavigation);

    Content rootChannel = mock(Content.class);
    when(site.getSiteRootDocument()).thenReturn(rootChannel);
    when(childrenLinkListContentTreeRelation.getParentOf(externalNavigationContent)).thenReturn(externalNavigationContent);
    when(childrenLinkListContentTreeRelation.pathToRoot(externalNavigationContent)).thenReturn(Collections.singletonList(rootChannel));

    //trigger method and assertions
    Linkable parentNavigation = testling.getParentOf(testNavigation);
    assertSame(externalNavigation, parentNavigation);
  }

  @Test
  public void testFindExternalChannelFor() throws Exception {

    //configure: A Content can be found for the external category
    Content externalNavigationContent = mock(Content.class);
    mockExternalChannelsForCategory(testCategory, "testGetParentOfParentCategory-parent", Collections.singletonList(externalNavigationContent));

    //configure creation of ContentBean
    CMExternalChannel expectedChannel = mock(CMExternalChannel.class);
    when(contentBeanFactory.createBeanFor(externalNavigationContent, CMExternalChannel.class)).thenReturn(expectedChannel);

    Content rootChannel = mock(Content.class);
    when(site.getSiteRootDocument()).thenReturn(rootChannel);
    when(childrenLinkListContentTreeRelation.pathToRoot(externalNavigationContent)).thenReturn(Collections.singletonList(rootChannel));

    //trigger method and assertions
    CMExternalChannel actualChannel = testling.findExternalChannelFor(testCategory, site);
    assertSame(expectedChannel, actualChannel);
  }

  @Test
  public void testFindExternalChannelForRecursively() throws Exception {
    //configure: A Content can be found for the parent of  an external category
    Category parentCategory = mock(Category.class);
    when(testCategory.getParent()).thenReturn(parentCategory);
    Content parentExternalNavigationContent = mock(Content.class);
    mockExternalChannelsForCategory(parentCategory, "testFindExternalChannelForRecursively-parent", Collections.singletonList(parentExternalNavigationContent));

    //configure for request content for category. No Content found.
    mockExternalChannelsForCategory(testCategory, "testFindExternalChannelForRecursively", new ArrayList<Content>());


    //configure creation of ContentBean
    CMExternalChannel expectedChannel = mock(CMExternalChannel.class);
    when(contentBeanFactory.createBeanFor(parentExternalNavigationContent, CMExternalChannel.class)).thenReturn(expectedChannel);

    Content rootChannel = mock(Content.class);
    when(site.getSiteRootDocument()).thenReturn(rootChannel);
    when(childrenLinkListContentTreeRelation.pathToRoot(parentExternalNavigationContent)).thenReturn(Collections.singletonList(rootChannel));

    //trigger method and assertions
    CMExternalChannel actualChannel = testling.findExternalChannelForRecursively(testCategory, site);
    assertSame(expectedChannel, actualChannel);
  }

  @Test
  public void testGetParentOfNoInternalRepresentationFoundForParent() throws Exception {
    //configure for request content for parent category. No Content found.
    Category parent = mock(Category.class);
    when(testCategory.getParent()).thenReturn(parent);
    mockExternalChannelsForCategory(parent, "testGetParentOfParentCategory-parent", new ArrayList<Content>());

    //configure wrapper for parent category and link category.
    LiveContextNavigation externalNavigation = mock(LiveContextNavigation.class);
    when(navigationFactory.createNavigation(parent, site)).thenReturn(externalNavigation);
    when(externalNavigation.getCategory()).thenReturn(parent);

    //trigger method and assertions.
    Linkable actualExternalNavigation = testling.getParentOf(testNavigation);

    assertTrue(actualExternalNavigation instanceof LiveContextNavigation);
    LiveContextNavigation actualCasted = (LiveContextNavigation) actualExternalNavigation;
    assertSame(parent, actualCasted.getCategory());
  }

  @Test
  public void testPathToRoot() throws Exception {
    Navigation child1 = mock(Navigation.class);
    Navigation child2 = mock(Navigation.class);
    Content rootDocument = mock(Content.class);
    Linkable rootChannel = mock(Linkable.class);
    when(child1.getParentNavigation()).thenReturn(child2);
    when(site.getSiteRootDocument()).thenReturn(rootDocument);
    when(contentBeanFactory.createBeanFor(rootDocument, Linkable.class)).thenReturn(rootChannel);
    List<Linkable> navigations = testling.pathToRoot(child1, site);
    assertEquals(3, navigations.size());
    assertSame(child1, navigations.get(0));
    assertSame(child2, navigations.get(1));
    assertSame(rootChannel, navigations.get(2));
  }

  private void mockExternalChannelsForCategory(Category category, String categoryId, List<Content> foundExternalChannels) {
    when(category.getReference()).thenReturn(categoryId);
    String externalChannelRef = testling.categoryIdToExternalChannelRef(category);
    String query = "TYPE = " + CMExternalChannel.NAME + " : " + CMExternalChannel.EXTERNAL_ID + " = ?0 AND isInProduction AND BELOW ?1 ORDER BY id";
    when(queryService.poseContentQuery(eq(query), eq(externalChannelRef), anyObject())).thenReturn(foundExternalChannels);
    when(queryService.getContentsFulfilling(anyCollection(), eq(query), eq(externalChannelRef), anyObject())).thenReturn(foundExternalChannels);
  }
}
