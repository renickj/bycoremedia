package com.coremedia.blueprint.elastic.social.rest;

import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.cap.content.Content;
import com.coremedia.elastic.core.api.models.UnresolvableReferenceException;
import com.coremedia.rest.linking.Linker;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.HashMap;

@Named
public class ContentWithSiteJsonSerializer extends JsonSerializer<ContentWithSite> {
  private static final Logger LOG = LoggerFactory.getLogger(ContentWithSiteJsonSerializer.class);

  @Inject
  private Linker linker;

  @Override
  public void serialize(ContentWithSite value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
    HashMap<String, Object> serializedObject = new HashMap<>();
    try {
      Content content = value.getContent();
      serializedObject.put("$Ref", linker.link(content).toString());
    } catch (UnresolvableReferenceException e) {
      LOG.warn("Could not resolve target reference: {}", e.toString());
    }

    jgen.writeObject(serializedObject);
  }

  @Override
  public Class<ContentWithSite> handledType() {
    return ContentWithSite.class;
  }
}
