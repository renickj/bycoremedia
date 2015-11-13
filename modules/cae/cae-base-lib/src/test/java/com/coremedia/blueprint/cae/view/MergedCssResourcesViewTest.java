package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.cae.contentbeans.CodeResourcesImpl;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.UnsupportedEncodingException;

import static com.coremedia.cae.testing.TestInfrastructureBuilder.Infrastructure;
import static com.coremedia.cae.testing.TestInfrastructureBuilder.create;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link com.coremedia.blueprint.cae.view.CodeResourcesView}
 */
public class MergedCssResourcesViewTest {

  private static final String NAVIGATION_ID = "4";
  private CodeResources codeResources;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private Infrastructure infrastructure;

  @Before
  public void setup() {
    infrastructure = create()
                .withContentBeanFactory()
                .withContentRepository("classpath:/com/coremedia/blueprint/cae/view/mergedcodeview/content.xml")
                .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
                .withBeans("classpath:/com/coremedia/cache/cache-services.xml")
                .withBeans("spring/test/dummy-views.xml")
                .build();

    CMContext navigation = ContentTestCaseHelper.getContentBean(infrastructure, NAVIGATION_ID);
    codeResources = new CodeResourcesImpl(navigation,"css",false);

    response = new MockHttpServletResponse();
    request = new MockHttpServletRequest();
  }

  @Test
  public void testManagedResources() throws UnsupportedEncodingException {

    CodeResourcesView testling = infrastructure.getBean("mergedCssResourcesView",CodeResourcesView.class);

    testling.render(codeResources, "css", request, response);

    String expected = ".my-custom-class-34{content:css code id 34}\n" +
            ".my-custom-class-32{content:css code id 32}\n" +
            ".my-custom-class-30{content:css code id 30}\n";

    assertEquals("Output does not match", expected, response.getContentAsString());

  }
}
