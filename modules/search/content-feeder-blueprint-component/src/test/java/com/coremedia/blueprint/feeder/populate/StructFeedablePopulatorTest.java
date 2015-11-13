package com.coremedia.blueprint.feeder.populate;

import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.feeder.MutableFeedable;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StructFeedablePopulatorTest {
  private StructFeedablePopulator structFeedablePopulator;
  private Content content;
  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withContentRepository("classpath:/com/coremedia/testing/contenttest.xml")
          .build();

  @Before
  public void setUp() throws Exception {

    structFeedablePopulator = new StructFeedablePopulator();
    structFeedablePopulator.setSolrFieldName("textbody");
    List<String> propertyNames = Arrays.asList("settings", "localSettings");
    structFeedablePopulator.setPropertyNames(propertyNames);
    content = ContentTestCaseHelper.getContent(infrastructure, 4);
  }

  @Test
  public void testPopulate() throws Exception {

    MutableFeedable mutableFeedable = new MutableFeedableImpl() {
      @Override
      public void setStringElement(String s, String s1) {
        assertEquals("unexpected field", "textbody", s);
        assertEquals("unexpected struct", "booleanProperty stringProperty testString integerProperty dateProperty 2010-01-01T10:00:23-10:00 doubleProperty 2.3 linkProperty structProperty ", s1);
      }
    };
    structFeedablePopulator.populate(mutableFeedable, content);
  }
}
