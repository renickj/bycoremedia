package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.xmlrepo.XmlRepoConfiguration;
import com.coremedia.cap.xmlrepo.XmlUapiConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(XmlRepoConfiguration.class)
class BlobMetadataRestTestConfiguration {

  @Bean
  public XmlUapiConfig xmlUapiConfig() {
    return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml")  ;
  }
}
