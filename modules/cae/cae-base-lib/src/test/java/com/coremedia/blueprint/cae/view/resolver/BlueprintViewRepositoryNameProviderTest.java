package com.coremedia.blueprint.cae.view.resolver;

import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlueprintViewRepositoryNameProviderTest {

  private BlueprintViewRepositoryNameProvider blueprintViewRepositoryNameProvider;
  private CMChannel channel;

  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withContentBeanFactory()
          .withContentRepository("classpath:/com/coremedia/testing/contenttest.xml")
          .withDataViewFactory()
          .withIdProvider()
          .withLinkFormatter()
          .withCache()
          .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
          .withBeans("classpath:/framework/spring-test/blueprint-view-repository-name-provider-test.xml")
          .build();

  @Before
  public void setUp() {
    channel = ContentTestCaseHelper.getContentBean(infrastructure, 10);
    blueprintViewRepositoryNameProvider = infrastructure.getBean("blueprintViewRepositoryNameProvider", BlueprintViewRepositoryNameProvider.class);
  }

  @Test
  public void testGetViewRepositoryNames() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("com.coremedia.blueprint.viewrepositorynames", null);
    Map<String, CMLinkable> map = new HashMap<>();
    map.put(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION, channel);
    List<String> repositoryNames = blueprintViewRepositoryNameProvider.getViewRepositoryNames("view", map, null, request);
    Assert.assertEquals("media", repositoryNames.get(0));
    Assert.assertEquals("notMedia", repositoryNames.get(1));
    Assert.assertEquals("againNotMedia", repositoryNames.get(2));
    Assert.assertEquals("basic", repositoryNames.get(3));
    Assert.assertEquals("error", repositoryNames.get(4));
  }
}
