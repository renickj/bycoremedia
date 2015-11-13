package com.coremedia.blueprint.elastic.social.cae.flows;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
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

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActivateRegistrationFlowExecutionTest extends AbstractXmlFlowExecutionTests {
  private static final String INVALID_KEY_REDIRECTION_URL = "/blueprint/servlet/segment/bla/blubb.html";
  private MutableAttributeMap input = new LocalAttributeMap();
  private MockExternalContext context = new MockExternalContext();

  @Mock
  private RegistrationHelper registrationHelper;

  @Mock
  private FlowUrlHelper webflowUrlHelper;

  @Mock
  private RequestContext requestContext;

  @Before
  public void init() {
  }

  protected FlowDefinitionResource getResource(FlowDefinitionResourceFactory resourceFactory) {
    return resourceFactory.createClassPathResource("/com/coremedia/blueprint/es/webflow/com.coremedia.blueprint.elastic.social.cae.flows.ActivateRegistration.xml",
            ActivateRegistrationFlowExecutionTest.class);
  }

  protected void configureFlowBuilderContext(MockFlowBuilderContext builderContext) {
    builderContext.registerBean("registrationHelper", registrationHelper);
    builderContext.registerBean("webflowUrlHelper", webflowUrlHelper);
  }

  @Test
  public void success() {
    // Arbitrary key copied from registration mail
    final String ACTIVATION_KEY = "567169af5469c7b691d1ce0d77a9e4beed75b917";
    input.put("activationKey", ACTIVATION_KEY);
    when(registrationHelper.activate(eq(ACTIVATION_KEY), Matchers.<RequestContext>any(RequestContext.class))).thenReturn(true);

    startAndAssertFlowOutcome("activateRegistrationSuccess");
  }

  private void startAndAssertRedirection(String outcome, String externalRedirectUrl) {
    startAndAssertFlowOutcome(outcome);
    assertTrue(context.getExternalRedirectRequested());
    assertEquals(externalRedirectUrl, context.getExternalRedirectUrl());
  }

  private void startAndAssertFlowOutcome(String outcome) {
    when(webflowUrlHelper.getRootPageUrl(Matchers.<RequestContext>any(RequestContext.class))).thenReturn(INVALID_KEY_REDIRECTION_URL);
    startFlow(input, context);
    assertFlowExecutionEnded();
    assertFlowExecutionOutcomeEquals(outcome);
  }

  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  @Test
  public void failWithMalformedKey() {
    final String badKey = "knalliava231qa";
    input.put("activationKey", badKey);
    when(registrationHelper.activate(badKey, requestContext)).thenReturn(false);

    startAndAssertFlowOutcome("activateRegistrationFailure");
  }

  @Test
  public void failWithEmptyKey() {
    startAndAssertRedirection("invalid", "serverRelative:" + INVALID_KEY_REDIRECTION_URL);
  }
}