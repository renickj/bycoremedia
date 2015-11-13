package com.coremedia.blueprint.elastic.social.rest;

import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.cap.content.Content;
import com.coremedia.rest.linking.Linker;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ContentWithSiteJsonSerializerTest {
  @InjectMocks
  private ContentWithSiteJsonSerializer serializer = new ContentWithSiteJsonSerializer();

  @Mock
  private ContentWithSite contentWithSite;

  @Mock
  private JsonGenerator jsonGenerator;

  @Mock
  private SerializerProvider serializerProvider;

  @Mock
  private Linker linker;

  @Mock
  private Content content;

  @Test
  public void serialize() throws IOException {
    URI uri = URI.create("xyz");
    HashMap<String, Object> serializedObject = new HashMap<>();
    serializedObject.put("$Ref", uri.toString());
    when(contentWithSite.getContent()).thenReturn(content);
    when(linker.link(content)).thenReturn(uri);
    serializer.serialize(contentWithSite, jsonGenerator, serializerProvider);

    verify(linker).link(content);
    verify(jsonGenerator).writeObject(serializedObject);
  }

  @Test
  public void handledType() {
    Class<ContentWithSite> type = serializer.handledType();
    assertEquals(ContentWithSite.class, type);
  }
}
