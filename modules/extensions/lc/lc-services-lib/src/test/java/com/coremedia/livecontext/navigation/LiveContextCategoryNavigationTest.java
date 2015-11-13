package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextCategoryNavigationTest {
  private static final String SITE_ID = "aSiteId";

  @Mock
  private Category category;

  @Mock
  private LiveContextNavigationTreeRelation treeRelation;

  @Mock
  private CMNavigation rootNavigation;

  @Mock
  private Site site;

  private LiveContextCategoryNavigation testling;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    when(site.getId()).thenReturn(SITE_ID);
    testling = new LiveContextCategoryNavigation(category, site, treeRelation);
    //Category must have an ID because it is used in equals
    when(category.getId()).thenReturn("anyID");
  }

  @Test (expected = IllegalArgumentException.class)
  public void testConstructorParametersCategoryNull() {
    testling = new LiveContextCategoryNavigation(null, site, treeRelation);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testConstructorParametersTreeRelationNull() {
    testling = new LiveContextCategoryNavigation(category, site, null);
  }

  @Test
  public void testGetCategory() throws Exception {
    Category actual = testling.getCategory();
    assertSame("Category must be the one which is set by constructor", category, actual);
  }

  @Test
  public void testGetChildren() throws Exception {
    List<Linkable> children = new ArrayList<>();
    List<Linkable> otherChildren = new ArrayList<>();
    Navigation navigation1 = mock(Navigation.class);
    otherChildren.add(navigation1);
    Navigation navigation2 = mock(Navigation.class);
    otherChildren.add(navigation2);


    when(treeRelation.getChildrenOf(testling)).thenReturn(children, otherChildren);
    List<? extends Linkable> actualChildren = testling.getChildren();
    assertNotNull(actualChildren);
    assertEquals("No Children returned from mock, so list is expected to be empty.", 0, actualChildren.size());

    actualChildren = testling.getChildren();
    assertNotNull(actualChildren);
    assertEquals("Two children were added before so two children must exist", 2, actualChildren.size());
  }

  @Test
  public void testGetChildrenNullByTreeRelation() throws Exception {
    when(treeRelation.getChildrenOf(testling)).thenReturn(null);
    List<? extends Linkable> actualChildren = testling.getChildren();
    assertNotNull(actualChildren);
    assertEquals("No Children exist so the list should be empty", 0, actualChildren.size());
  }

  @Test
  public void testGetParentNavigation2() throws Exception {
    Navigation parentCalculatedByTreeRelation = mock(Navigation.class);
    when(treeRelation.getParentOf(testling)).thenReturn(parentCalculatedByTreeRelation);
    Navigation parentNavigation = testling.getParentNavigation();
    assertSame(parentCalculatedByTreeRelation, parentNavigation);
  }

  @Test
  public void testGetContext() throws Exception {
    CMExternalChannel contextCalculatedByTreeRelation = mock(CMExternalChannel.class);
    when(treeRelation.findExternalChannelForRecursively(category, site)).thenReturn(contextCalculatedByTreeRelation);
    CMContext context = testling.getContext();
    assertSame(contextCalculatedByTreeRelation, context);
  }

  @Test
  public void testGetParentNavigation2IsNull() throws Exception {
    when(treeRelation.getParentOf(testling)).thenReturn(null);
    Navigation parentNavigation = testling.getParentNavigation();
    assertNull(parentNavigation);
  }

  @Test
  public void testGetRootNavigation() throws Exception {
    List<Linkable> navigations = new ArrayList<>();
    navigations.add(testling);
    navigations.add(rootNavigation);
    when(treeRelation.pathToRoot(testling, site)).thenReturn(navigations);
    CMNavigation result = testling.getRootNavigation();
    assertSame("Wrong root navigation", rootNavigation, result);
  }

  @Test
  public void testGetRootNavigationNoRootFound() throws Exception {
    List<Linkable> navigations = new ArrayList<>();
    when(treeRelation.pathToRoot(testling, null)).thenReturn(navigations);
    assertNull(testling.getRootNavigation());
  }

  @Test
  public void testGetNavigationPathList() throws Exception {
    Navigation navigation1 = new LiveContextCategoryNavigation(category, site, treeRelation);
    List<Linkable> pathToRoot = new ArrayList<>();
    pathToRoot.add(testling);
    pathToRoot.add(navigation1);

    when(treeRelation.pathToRoot(testling, site)).thenReturn(pathToRoot);

    List<? extends Linkable> navigationPathList = testling.getNavigationPathList();
    assertEquals(2, navigationPathList.size());
    assertSame(navigation1, navigationPathList.get(0));
    assertSame(testling, navigationPathList.get(1));
  }

  @Test
  public void testIsHidden() throws Exception {
    assertFalse(testling.isHidden());
  }

  @Test
  public void testGetVisibleChildren() throws Exception {
    LiveContextCategoryNavigation testlingSpy = spy(testling);
    doReturn(new ArrayList<Linkable>()).when(testlingSpy).getChildren();
    testlingSpy.getVisibleChildren();
    verify(testlingSpy, times(1)).getChildren();
  }

  @Test
  public void testIsHiddenInSitemap() throws Exception {
    assertFalse(testling.isHiddenInSitemap());
  }

  @Test
  public void testGetSitemapChildren() throws Exception {
    LiveContextCategoryNavigation testlingSpy = spy(testling);
    doReturn(new ArrayList<Linkable>()).when(testlingSpy).getChildren();
    testlingSpy.getSitemapChildren();
    verify(testlingSpy, times(1)).getChildren();
  }

  @Test
  public void testGetTitle() throws Exception {
    String categoryName = "name";
    when(category.getName()).thenReturn(categoryName);
    String actual = testling.getTitle();
    assertEquals(categoryName, actual);
  }

  @Test
  public void testGetSegment() throws Exception {
    String anySeoSegment = "anySeoSegment";
    when(category.getSeoSegment()).thenReturn(anySeoSegment);
    String actual = testling.getSegment();
    assertEquals(anySeoSegment, actual);
  }

  @Test
  public void testIsRoot() throws Exception {
    testling.isRoot();
  }

  @Test
  public void testGetLocale() throws Exception {
    Locale expected = Locale.getDefault();
    when(category.getLocale()).thenReturn(expected);
    Locale actual = testling.getLocale();
    assertSame(expected, actual);
  }

  @Test
  public void testGetViewTypeName() throws Exception {
    assertNull(testling.getViewTypeName());
  }
}
