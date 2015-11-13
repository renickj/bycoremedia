package com.coremedia.livecontext.fragment.resolver;


import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.FragmentParametersFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class})
public class ContentSeoSegmentExternalReferenceResolverTest {

  @Mock
  private Site site;

  @Mock
  private Content siteRootFolder;

  @Mock
  private Content linkable;

  @Mock
  private Content navigation;

  @Mock
  private Content siteRoot;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private Locale locale;

  @Before
  public void beforeEachTest() {
    when(site.getSiteRootFolder()).thenReturn(siteRootFolder);
    when(siteRootFolder.getRepository()).thenReturn(contentRepository);

    when(siteRootFolder.getRepository()).thenReturn(contentRepository);
    when(contentRepository.getContent("coremedia:///cap/content/5678")).thenReturn(linkable);
    when(contentRepository.getContent("coremedia:///cap/content/1234")).thenReturn(navigation);
  }

  @Test
  public void testSeoSegmentExternalReferenceNoInfixDelimiter() throws Exception {
    ContentSeoSegmentExternalReferenceResolver testling = createTestling();

    String ref = "cm-seosegment:the-perfect-dinner";
    FragmentParameters params = parametersFor(site, locale, ref);
    assertFalse(testling.include(params));
  }

  @Test
  public void testSeoSegmentExternalReferenceNoIds() throws Exception {
    ContentSeoSegmentExternalReferenceResolver testling = createTestling();

    String ref = "cm-seosegment:the-perfect-dinner--";
    FragmentParameters params = parametersFor(site, locale, ref);
    assertFalse(testling.include(params));
  }

  @Test
  public void testSeoSegmentExternalReferenceTooManyIds() throws Exception {
    ContentSeoSegmentExternalReferenceResolver testling = createTestling();

    String ref = "cm-seosegment:the-perfect-dinner--1234-5678-5678";
    FragmentParameters params = parametersFor(site, locale, ref);
    assertFalse(testling.include(params));
  }

  @Test
  public void testSeoSegmentExternalReferenceBadIds() throws Exception {
    ContentSeoSegmentExternalReferenceResolver testling = createTestling();

    String ref = "cm-seosegment:the-perfect-dinner--123-5678";
    FragmentParameters params = parametersFor(site, locale, ref);
    assertFalse(testling.include(params));

    String ref2 = "cm-seosegment:the-perfect-dinner--1234-567";
    FragmentParameters params2 = parametersFor(site, locale, ref2);
    assertFalse(testling.include(params2));

    String ref3 = "cm-seosegment:the-perfect-dinner--0-0";
    FragmentParameters params3 = parametersFor(site, locale, ref3);
    assertFalse(testling.include(params3));
  }

  @Test
  public void testSeoSegmentExternalReferenceTwoIdsResolver() throws Exception {
    ContentSeoSegmentExternalReferenceResolver testling = createTestling();

    String ref = "cm-seosegment:the-perfect-dinner--1234-5678";
    FragmentParameters params = parametersFor(site, locale, ref);
    assertTrue(testling.include(params));
    LinkableAndNavigation linkableAndNavigation = testling.resolveExternalRef(params, site);
    assertEquals(linkable, linkableAndNavigation.getLinkable());
    assertEquals(navigation, linkableAndNavigation.getNavigation());
  }

  @Test
  public void testSeoSegmentExternalReferenceOneIdResolver() throws Exception {
    ContentSeoSegmentExternalReferenceResolver testling = createTestling();

    String ref = "cm-seosegment:the-perfect-dinner--5678";
    FragmentParameters params = parametersFor(site, locale, ref);
    assertTrue(testling.include(params));
    LinkableAndNavigation linkableAndNavigation = testling.resolveExternalRef(params, site);
    assertEquals(linkable, linkableAndNavigation.getLinkable());
    assertNull(linkableAndNavigation.getNavigation());
  }

  @Test
  public void testSeoSegmentExternalReferenceTwoIdsNoTextResolver() throws Exception {
    ContentSeoSegmentExternalReferenceResolver testling = createTestling();

    String ref = "cm-seosegment:--1234-5678";
    FragmentParameters params = parametersFor(site, locale, ref);
    assertTrue(testling.include(params));
    LinkableAndNavigation linkableAndNavigation = testling.resolveExternalRef(params, site);
    assertEquals(linkable, linkableAndNavigation.getLinkable());
    assertEquals(navigation, linkableAndNavigation.getNavigation());
  }

  @Test
  public void testSeoSegmentExternalReferenceOneIdNoTextResolver() throws Exception {
    ContentSeoSegmentExternalReferenceResolver testling = createTestling();

    String ref = "cm-seosegment:--5678";
    FragmentParameters params = parametersFor(site, locale, ref);
    assertTrue(testling.include(params));
    LinkableAndNavigation linkableAndNavigation = testling.resolveExternalRef(params, site);
    assertEquals(linkable, linkableAndNavigation.getLinkable());
    assertNull(linkableAndNavigation.getNavigation());
  }


  // --- internal ---------------------------------------------------

  private ContentSeoSegmentExternalReferenceResolver createTestling() {
    ContentSeoSegmentExternalReferenceResolver testling = new ContentSeoSegmentExternalReferenceResolver();
    testling.setContentRepository(contentRepository);
    return testling;
  }


  private FragmentParameters parametersFor(Site site, Locale locale, String ref) {
    String url = "http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;parameter=noLinkRewrite;placement=header;environment=site:site2";
    FragmentParameters params = FragmentParametersFactory.create(url);
    params.setExternalReference(ref);
    return params;
  }
}
