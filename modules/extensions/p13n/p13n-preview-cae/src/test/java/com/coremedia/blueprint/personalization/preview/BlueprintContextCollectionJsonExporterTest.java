package com.coremedia.blueprint.personalization.preview;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.content.ContentRepository;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.ContextCollectionImpl;
import com.coremedia.personalization.context.MapPropertyMaintainer;
import com.coremedia.personalization.context.PropertyProfile;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Test for the {@link BlueprintContextCollectionJsonExporter}
 * that formats a given {@link com.coremedia.personalization.context.ContextCollection} as JS array
 */
public class BlueprintContextCollectionJsonExporterTest {

  private static final String TAXONOMY_42 = IdHelper.formatContentId(42);
  private static final String TAXONOMY_84 = IdHelper.formatContentId(84);
  private static final String TAXONOMY_42_NAME = "fourtytwo";
  private static final String TAXONOMY_84_NAME = "eightyfour";

  private final ContextCollection segmentContexts = new ContextCollectionImpl();
  private final PropertyProfile propertySegment = new PropertyProfile();
  private final ContentRepository repository = Mockito.mock(ContentRepository.class);
  private final Content content_42 = Mockito.mock(Content.class);
  private final Content content_84 = Mockito.mock(Content.class);
  private final Content content_616 = Mockito.mock(Content.class);

  {
    Mockito.when(repository.getContent(TAXONOMY_42)).thenReturn(content_42);
    Mockito.when(content_42.getName()).thenReturn(TAXONOMY_42_NAME);
    Mockito.when(repository.getContent(TAXONOMY_84)).thenReturn(content_84);
    Mockito.when(content_84.getName()).thenReturn(TAXONOMY_84_NAME);
    Mockito.when(repository.getContent(IdHelper.formatContentId(616))).thenReturn(content_616);
    Mockito.when(content_616.getName()).thenReturn("TheSegmentTitle");
  }

  private BlueprintContextCollectionJsonExporter exporter;

  @Before
  public void setup(){
    exporter = new BlueprintContextCollectionJsonExporter();
    exporter.setContentRepository(repository);
    exporter.setContextCollection(segmentContexts);
  }

  // Segments assigned to the current user profile should be saved by their segment title without the true value in the JSON
  @Test
  public void testSegmentTrue() {
    propertySegment.setProperty(IdHelper.formatContentId(616), true);
    final String contextName = "segments";
    segmentContexts.setContext(contextName, propertySegment);
    exporter.setSegmentContexts(Arrays.asList(contextName));

    Assert.assertEquals("[[\"segments\",\"TheSegmentTitle\",\"\"]]", exporter.getContextCollectionAsJson());
  }

  // Segments which are not assigned to the current user profile should be removed and not saved in the JSON
  @Test
  public void testSegmentFalse() {
    propertySegment.setProperty("content:616", false);
    final String contextName = "segments";
    segmentContexts.setContext(contextName, propertySegment);
    exporter.setSegmentContexts(Arrays.asList(contextName));

    Assert.assertEquals("[]", exporter.getContextCollectionAsJson());
  }

  @Test
  public void testGetJsArray() {
    ContextCollection contexts = new ContextCollectionImpl();
    PropertyProfile p1 = new PropertyProfile();
    p1.setProperty("key11", true);
    PropertyProfile p2 = new PropertyProfile();
    p2.setProperty("key21", 1.1d);
    p2.setProperty("key22", 2.1d);
    // simple objects shall be ignored without error
    // as only PropertyProfiles are supported
    Object p3 = new Object();

    // empty profiles shall be ignored
    PropertyProfile p4 = new PropertyProfile();

    MapPropertyMaintainer p5 = new MapPropertyMaintainer();
    Calendar now = Calendar.getInstance();
    p5.setProperty("key51", now);
    p5.setProperty("url","http://www.coremedia.com/cm7");
    p5.setProperty("attack","<script>alert('attack')</script>");

    contexts.setContext("p1", p1);
    contexts.setContext("p2", p2);
    contexts.setContext("p3", p3);
    contexts.setContext("p4", p4);
    contexts.setContext("p5", p5);

    PropertyProfile taxonomies = new PropertyProfile();
    taxonomies.setProperty(TAXONOMY_42,42);
    taxonomies.setProperty(TAXONOMY_84,84);
    contexts.setContext("taxonomies", taxonomies);

    exporter.setContextCollection(contexts);
    String currentDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).format(now.getTime());
    final String contextCollectionAsJson = exporter.getContextCollectionAsJson();
    Assert.assertTrue(contextCollectionAsJson, contextCollectionAsJson.contains("[\"p1\",\"key11\",\"true\"]"));
    Assert.assertTrue(contextCollectionAsJson, contextCollectionAsJson.contains("[\"p2\",\"key22\",\"2.1\"]"));
    Assert.assertTrue(contextCollectionAsJson, contextCollectionAsJson.contains("[\"p2\",\"key21\",\"1.1\"]"));
    Assert.assertTrue(contextCollectionAsJson, contextCollectionAsJson.contains("[\"p5\",\"key51\",\"" + currentDate + "\"]"));
    Assert.assertTrue(contextCollectionAsJson, contextCollectionAsJson.contains("[\"p5\",\"url\",\"http://www.coremedia.com/cm7\"]"));
    Assert.assertTrue(contextCollectionAsJson, contextCollectionAsJson.contains("[\"p5\",\"attack\",\"\\u003cscript\\u003ealert(\\u0027attack\\u0027)\\u003c/script\\u003e\"]"));
    Assert.assertTrue(contextCollectionAsJson, contextCollectionAsJson.contains("[\"taxonomies\",\"" + TAXONOMY_42_NAME + "\",\"42\"]"));
    Assert.assertTrue(contextCollectionAsJson, contextCollectionAsJson.contains("[\"taxonomies\",\"" + TAXONOMY_84_NAME + "\",\"84\"]"));
  }

}
