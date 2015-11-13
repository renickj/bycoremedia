package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMAbstractCode;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link com.coremedia.blueprint.cae.contentbeans.CodeResourcesImpl}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CodeResourcesImplTest.CodeResourcesTestConfiguration.class)
public class CodeResourcesImplTest {

  private CMContext context;
  private CMAbstractCode code30;
  private CMAbstractCode code32;
  private CMAbstractCode code34;
  private CMAbstractCode code36;
  private CMAbstractCode code38;
  private CMAbstractCode code40;
  private CMAbstractCode code42;
  private CMAbstractCode code50;

  private CodeResources jsTestling;
  private CodeResources cssTestling;

  @Inject
  private ContentRepository contentRepository;
  @Inject
  private ContentBeanFactory contentBeanFactory;

  @Before
  public void setup() {
    context = getContentBean(4);

    //CSS
    code30 = getContentBean(30);
    code32 = getContentBean(32);
    code34 = getContentBean(34);
    code36 = getContentBean(36);
    code38 = getContentBean(38);
    code40 = getContentBean(40);
    code42 = getContentBean(42);

    //JavaScript
    code50 = getContentBean(50);

    jsTestling = new CodeResourcesImpl(context, CMNavigationBase.JAVA_SCRIPT, true);
    cssTestling = new CodeResourcesImpl(context, CMNavigationBase.CSS, true);
  }

  private <T> T getContentBean(int id) {
    Content content = contentRepository.getContent(IdHelper.formatContentId(id));
    return (T) contentBeanFactory.createBeanFor(content);
  }

  //--- test lists -----------------------------------------------------------------------------------------------------

  @Test
  public void testOneLinkedCode() {
    //Only one JavaScript is linked to the Navigation.
    List<CMAbstractCode> expected = Arrays.asList(code50);
    List<?> actual = jsTestling.getLinkTargetList();
    assertEquals("list does not match", expected, actual);
  }

  @Test
  public void testMultipleLinkedCodes() {
    List<CMAbstractCode> expected = Arrays.asList(code40, code34, code38, code32, code30, code36, code42);
    List<?> actual = cssTestling.getLinkTargetList();
    assertEquals("list does not match", expected, actual);
  }

  //--- test hashes ----------------------------------------------------------------------------------------------------

  @Test
  public void testOneLinkedCodeHash() {
    String actual = jsTestling.getETag();
    //todo better check whether the hash changes after adding/removing code resources:
    assertEquals("hash does not match", "d78a184e0fc58b061968d171f40aee16", actual);
  }

  @Test
  public void testMultipleLinkedCodesHash() {
    String actual = cssTestling.getETag();
    assertEquals("hash does not match", "2b5a8aba1e15624022e47f0fa92c3202", actual);
  }

  /**
   * Test that a cyclic link does not break recursion
   */
  @Test
  public void testRecursion() {
    CMContext recursiveContext = getContentBean(666);
    CMAbstractCode code44 = getContentBean(44);
    CMAbstractCode code46 = getContentBean(46);
    List<CMAbstractCode> expected = Arrays.asList(code46, code44);

    CodeResources recursiveTestling = new CodeResourcesImpl(recursiveContext, CMNavigationBase.CSS, true);

    assertEquals("list does not match", expected, recursiveTestling.getLinkTargetList());
  }

  //====================================================================================================================

  @Configuration
  @Import(XmlRepoConfiguration.class)
  @ImportResource(value = {
          "classpath:/com/coremedia/cap/common/xml/uapi-xml-services.xml",
          "classpath:/framework/spring/blueprint-contentbeans.xml"
  },
          reader = com.coremedia.springframework.component.ResourceAwareXmlBeanDefinitionReader.class)
  static class CodeResourcesTestConfiguration {

    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/blueprint/cae/contentbeans/coderesources/content.xml");
    }
  }

}
