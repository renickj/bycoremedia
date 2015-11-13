package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.blueprint.cae.search.SegmentResolver;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.xmlrepo.XmlUapiConfig;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.FragmentParametersFactory;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, SegmentPathResolverTest.LocalConfig.class})
public class SegmentPathResolverTest {

  private static final String CONTENT_REPOSITORY_URL = "classpath:/com/coremedia/livecontext/fragment/resolver/segmentpath-test-content.xml";

  @Inject
  private SegmentPathResolver testling;
  @Inject
  private SegmentResolver segmentResolver;
  @Inject
  private ContentRepository contentRepository;
  @Inject
  private ContentBeanFactory contentBeanFactory;

  private Site site;


  // --- Setup ------------------------------------------------------

  @Before
  public void beforeEachTest() {
    site = mock(Site.class);
  }


  // --- tests ------------------------------------------------------

  @Test
  public void testInclude() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!achannel!asubchannel!alinkable");
    assertTrue(testling.include(parameters));
  }

  @Test
  public void testEmpty() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    assertNull(can);
  }

  @Test
  public void testRoot() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!root");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    checkResult(can, 124, 124);
  }

  @Test
  public void testSubchannel() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!root!level1!level2");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    checkResult(can, 130, 130);
  }

  @Test
  public void testContentInRoot() {
    mockSegmentResolver(124, "doc", 4);
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!root!doc");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    checkResult(can, 124, 4);
  }

  @Test
  public void testContentInSubchannel() {
    mockSegmentResolver(130, "doc", 4);
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!root!level1!level2!doc");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    checkResult(can, 130, 4);
  }

  @Test
  public void testContentNotInChannel() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!root!level1!doc");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    assertNull(can);
  }

  @Test
  public void testContentOnly() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!doc");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    assertNull(can);
  }

  @Test
  public void testNotAbsoluteSegmentPath() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:root!level1!doc");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    assertNull(can);
  }

  @Test
  public void testRootPath() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    assertNull(can);
  }

  @Ignore  // Known shortcoming.
  @Test
  public void testEvilSegment() {
    FragmentParameters parameters = createFragmentParameters();
    parameters.setExternalReference("cm-segmentpath:!root!level1!bses!Dokument");
    LinkableAndNavigation can = testling.resolveExternalRef(parameters, site);
    checkResult(can, 128, 8);
  }


  // --- internal ---------------------------------------------------

  private void mockSegmentResolver(int contextId, String segment, int linkableId) {
    Content content = contentRepository.getContent(IdHelper.formatContentId(linkableId));
    CMLinkable linkable = contentBeanFactory.createBeanFor(content, CMLinkable.class);

    when(segmentResolver.resolveSegment(contextId, segment, CMLinkable.class)).thenReturn(linkable);
  }

  private static FragmentParameters createFragmentParameters() {
    FragmentParameters parameters = FragmentParametersFactory.create("http://localhost:40081/blueprint/servlet/service/fragment/10001/en-US/params;view=asTeaser");
    parameters.setView("asTeaser");
    return parameters;
  }

  private static void checkResult(LinkableAndNavigation result, int expectedNavigation, int expectedContent) {
    assertEquals(expectedNavigation, IdHelper.parseContentId(result.getNavigation().getId()));
    assertEquals(expectedContent, IdHelper.parseContentId(result.getLinkable().getId()));
  }

  @Configuration
  @ComponentScan(basePackages = "com.coremedia.livecontext.ecommerce.ibm.configuration")
  @ImportResource(value = {
          "classpath:/framework/spring/blueprint-contentbeans.xml",
          "classpath:/META-INF/coremedia/livecontext-resolver.xml",
          "classpath:/framework/spring/livecontext-services.xml"
  },
          reader = com.coremedia.springframework.component.ResourceAwareXmlBeanDefinitionReader.class)
  public static class LocalConfig {

    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(SegmentPathResolverTest.CONTENT_REPOSITORY_URL);
    }


    @Bean
    public static BeanPostProcessor segmentResolverReplacer() {
      return new BeanPostProcessor() {
        @Override
        public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
          return o;
        }

        @Override
        public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
          if("segmentResolver".equals(s)) {
            return mock(SegmentResolver.class);
          }
          return o;
        }
      };
    }

    @Bean
    AssetUrlProvider assetUrlProvider() {
      return mock(AssetUrlProvider.class);
    }

  }
}
