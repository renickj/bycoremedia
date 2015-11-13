package com.coremedia.blueprint.cae.settings;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.xmlrepo.XmlUapiConfig;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CMLinkableBeanSettingsFinderTest.CMLinkableBeanSettingsFinderTestConfiguration .class)
public class CMLinkableBeanSettingsFinderTest {
  private static final String CONTENT_REPOSITORY_URL = "classpath:/com/coremedia/blueprint/cae/settings/settings.xml";

  @Inject
  private SettingsService settingsService;
  @Inject
  private ContentRepository contentRepository;
  @Inject
  private ContentBeanFactory contentBeanFactory;

  private CMLinkable linkable;


  // --- Setup ------------------------------------------------------

  @Before
  public void setup() {
    linkable = contentBeanFactory.createBeanFor(contentRepository.getContent(IdHelper.formatContentId(124)), CMLinkable.class);
  }


  // --- Tests ------------------------------------------------------

  @Test
  public void testSimpleDelegation() {
    String localValue = settingsService.setting("stringProperty", String.class, linkable);
    assertEquals("unexpected first bean value", "testString124", localValue);
  }

  @Test
  public void testContentBeanLink() {
    CMLinkable target = settingsService.setting("linkProperty", CMLinkable.class, linkable);
    assertNotNull("No linkable", target);
    assertEquals("unexpected linkable", 124, target.getContentId());
  }

  @Test
  public void testContentBeanLinkList() {
    List<? extends CMLinkable> target = settingsService.settingAsList("linkListProperty", CMLinkable.class, linkable);
    assertNotNull("No linkable", target);
    assertFalse("empty", target.isEmpty());
    assertEquals("unexpected linkable", 124, target.get(0).getContentId());
  }

  @Test
  public void testStructAsMap() {
    Map<String, Object> value = settingsService.settingAsMap("structProperty", String.class, Object.class, linkable);
    assertNotNull("No map", value);
  }

  @Test
  public void testBeanProxy() {
    LinkablePropertyProxyTest proxy = settingsService.createProxy(LinkablePropertyProxyTest.class, linkable);
    CMLinkable a124 = proxy.getLinkProperty();
    assertNotNull("no bean from proxy", a124);
    assertEquals("wrong bean from proxy", 124, a124.getContentId());
  }

  @Test
  public void testBeanListProxy() {
    LinkableListPropertyProxyTest proxy = settingsService.createProxy(LinkableListPropertyProxyTest.class, linkable);
    List<? extends CMLinkable> a124List = proxy.getLinkListProperty();
    assertNotNull("no list from proxy", a124List);
    assertFalse("empty list from proxy", a124List.isEmpty());
    assertEquals("wrong bean in list from proxy", 124, a124List.get(0).getContentId());
  }


  // --- internal ---------------------------------------------------

  private interface LinkablePropertyProxyTest {
    CMLinkable getLinkProperty();
  }

  private interface LinkableListPropertyProxyTest {
    List<? extends CMLinkable> getLinkListProperty();
  }

  @Configuration
  @Import(XmlRepoConfiguration.class)
  @ImportResource(value = {
          "classpath:/com/coremedia/cap/common/xml/uapi-xml-services.xml",
          "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
          "classpath:/framework/spring/blueprint-contentbeans.xml",
          "classpath:/framework/spring/blueprint-contentbeans-settings.xml"
  },
          reader = com.coremedia.springframework.component.ResourceAwareXmlBeanDefinitionReader.class)
  static class CMLinkableBeanSettingsFinderTestConfiguration {

    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig(CONTENT_REPOSITORY_URL)  ;
    }
  }

}
