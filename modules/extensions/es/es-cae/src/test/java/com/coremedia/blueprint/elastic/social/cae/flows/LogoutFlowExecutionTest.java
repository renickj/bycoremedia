package com.coremedia.blueprint.elastic.social.cae.flows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.test.MockExternalContext;
import org.springframework.webflow.test.MockFlowBuilderContext;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogoutFlowExecutionTest extends AbstractXmlFlowExecutionTests {

  @Mock
  private FlowUrlHelper flowUrlHelper;

  @Mock
  private LoginHelper loginHelper;

  @Before
  public void init() {
  }

  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createClassPathResource("/com/coremedia/blueprint/es/webflow/com.coremedia.blueprint.elastic.social.cae.flows.Logout.xml", LogoutFlowExecutionTest.class);
  }

  protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
    builderContext.registerBean("loginHelper", loginHelper);
    builderContext.registerBean("webflowUrlHelper", flowUrlHelper);
  }

  @Test
  public void start() {
    MutableAttributeMap input = new LocalAttributeMap();
    String contextPath = "/context_path";
    MockExternalContext context = new MockExternalContext();
    context.setContextPath(contextPath);
    String logoutRedirectUrl = "http://some.host" + contextPath + "/some/other/url";
    when(flowUrlHelper.getLogoutUrl(any(RequestContext.class))).thenReturn(logoutRedirectUrl);

    startFlow(input, context);

    assertCurrentStateEquals("logout");
    assertEquals(logoutRedirectUrl, getFlowScope().get("nextUrl"));
  }
}
