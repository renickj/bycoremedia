package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class NavigationResolverTest {
  private static final String CONTENT_REPOSITORY_URL = "classpath:/com/coremedia/blueprint/cae/handlers/navigationresolver-test-content.xml";

  private static TestInfrastructureBuilder.Infrastructure infrastructure;

  private NavigationResolver testling;

  // --- Setup ------------------------------------------------------

  @BeforeClass
  public static void setUpStatic() {
    infrastructure = TestInfrastructureBuilder
            .create()
            .withLinkFormatter()
            .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
            .withBeans("classpath:/framework/spring/blueprint-handlers.xml")
            .withContentRepository(CONTENT_REPOSITORY_URL)
            .build();
  }

  @Before
  public void setup() {
    testling = infrastructure.getBean("navigationResolver", NavigationResolver.class);
  }


  // --- tests ------------------------------------------------------

  @Test
  public void resolveContentInRoot() {
    Content content = infrastructure.getContentRepository().getContent(IdHelper.formatContentId(4));
    CMLinkable contentBean = infrastructure.getContentBeanFactory().createBeanFor(content, CMLinkable.class);
    List<String> navigationPath = Arrays.asList("root");
    Navigation navigation = testling.getNavigation(contentBean, navigationPath);
    checkNavigation(navigation, 124);
  }

  @Test
  public void resolveContentInSubchannel() {
    Content content = infrastructure.getContentRepository().getContent(IdHelper.formatContentId(4));
    CMLinkable contentBean = infrastructure.getContentBeanFactory().createBeanFor(content, CMLinkable.class);
    List<String> navigationPath = Arrays.asList("root", "level1", "level2");
    Navigation navigation = testling.getNavigation(contentBean, navigationPath);
    checkNavigation(navigation, 130);
  }

  @Test
  public void resolveContentInWrongChannel() {
    Content content = infrastructure.getContentRepository().getContent(IdHelper.formatContentId(4));
    CMLinkable contentBean = infrastructure.getContentBeanFactory().createBeanFor(content, CMLinkable.class);
    List<String> navigationPath = Arrays.asList("root", "level1");
    Navigation navigation = testling.getNavigation(contentBean, navigationPath);
    assertNull(navigation);
  }

  @Test
  public void resolveContentInNoSuchChannel() {
    Content content = infrastructure.getContentRepository().getContent(IdHelper.formatContentId(4));
    CMLinkable contentBean = infrastructure.getContentBeanFactory().createBeanFor(content, CMLinkable.class);
    List<String> navigationPath = Arrays.asList("root", "no", "such", "channel");
    Navigation navigation = testling.getNavigation(contentBean, navigationPath);
    assertNull(navigation);
  }

  @Test
  public void resolveRoot() {
    Content content = infrastructure.getContentRepository().getContent(IdHelper.formatContentId(124));
    CMLinkable contentBean = infrastructure.getContentBeanFactory().createBeanFor(content, CMLinkable.class);
    List<String> navigationPath = Arrays.asList("root");
    Navigation navigation = testling.getNavigation(contentBean, navigationPath);
    checkNavigation(navigation, 124);
  }

  @Test
  public void resolveSubchannel() {
    Content content = infrastructure.getContentRepository().getContent(IdHelper.formatContentId(130));
    CMLinkable contentBean = infrastructure.getContentBeanFactory().createBeanFor(content, CMLinkable.class);
    List<String> navigationPath = Arrays.asList("root", "level1", "level2");
    Navigation navigation = testling.getNavigation(contentBean, navigationPath);
    checkNavigation(navigation, 130);
  }

  @Test
  public void resolveSubchannelWithoutSelf() {
    Content content = infrastructure.getContentRepository().getContent(IdHelper.formatContentId(130));
    CMLinkable contentBean = infrastructure.getContentBeanFactory().createBeanFor(content, CMLinkable.class);
    List<String> navigationPath = Arrays.asList("root", "level1");
    Navigation navigation = testling.getNavigation(contentBean, navigationPath);
    assertNull(navigation);
  }


  // --- internal ---------------------------------------------------

  private void checkNavigation(Navigation navigation, int id) {
    assertNotNull(navigation);
    assertTrue(navigation instanceof CMNavigation);
    CMNavigation cmn = (CMNavigation)navigation;
    assertEquals(id, cmn.getContentId());
  }

}
