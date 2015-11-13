package com.coremedia.blueprint.analytics.elastic.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        /*
         we should include component-es-alx-rest-extension.xml here, but then we'll run into JMX trouble
         */
        "classpath:/com/coremedia/blueprint/analytics/elastic/rest/EsAlxRestApplicationContextTest.xml",
        "classpath:/META-INF/coremedia/es-alx-studio-component-context.xml",
        "classpath:/com/coremedia/blueprint/base/settings/impl/bpbase-settings-services.xml",
        "classpath:/com/coremedia/blueprint/base/navigation/context/bpbase-default-contextstrategy.xml",
        "classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
        "classpath:/com/coremedia/cap/common/xml/uapi-xml-services.xml"
})
@Configuration
@PropertySource(name = "testProperties", value = {"classpath:/com/coremedia/blueprint/analytics/elastic/rest/es-alx-rest-test.properties"})
public class EsAlxRestApplicationContextTest {

  @Test
  public void canLoadApplicationContext() {
    // if control flow ends up here, we're done
  }

}
