package com.coremedia.blueprint.elastic.base;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.testing.XmlRepoConfiguration;
import com.coremedia.blueprint.base.testing.XmlUapiConfig;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.multisite.impl.SitesServiceImpl;
import com.coremedia.elastic.core.test.Injection;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TenantInitializerTest.LocalConfig.class,
        com.coremedia.elastic.core.impl.tenant.TenantConfiguration.class,
        XmlRepoConfiguration.class})
public class TenantHelperTest {

  @Inject
  private SettingsService settingsService;

  @Inject
  private SitesService sitesService;


  @Test
  public void testReadTenantsFromContent() throws Exception {
    final TenantHelper tenantHelper = new TenantHelper();
    Injection.inject(tenantHelper, settingsService);
    Injection.inject(tenantHelper, sitesService);
    final Collection<String> strings = tenantHelper.readTenantsFromContent();
    assertEquals(2, strings.size());
    assertThat(strings, CoreMatchers.hasItems("tenant", "testTenant"));
  }

  @Configuration
  @ImportResource(value = {"classpath:META-INF/coremedia/component-elastic-worker.xml",
          "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml"},
          reader = com.coremedia.springframework.component.ResourceAwareXmlBeanDefinitionReader.class)
  public static class LocalConfig {
    @Bean
    public XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml");
    }
    @Bean
    public SitesService sitesService() {
      return new SitesServiceImpl();
    }

  }
}
