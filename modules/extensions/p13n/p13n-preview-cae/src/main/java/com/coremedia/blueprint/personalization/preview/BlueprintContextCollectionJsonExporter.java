package com.coremedia.blueprint.personalization.preview;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.PropertyProvider;
import com.coremedia.personalization.context.collector.SystemDateTimeSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Exports the context collection into a Json representation.
 */
public class BlueprintContextCollectionJsonExporter {

  private static final Logger LOG = LoggerFactory.getLogger(BlueprintContextCollectionJsonExporter.class);

  private static final String DEFAULT_SEGMENT_CONTEXT_NAME = "segment";
  private static final Gson GSON = new GsonBuilder().create();

  private final FastDateFormat timeFormat = FastDateFormat.getInstance("HH:mm:ss");
  private final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");
  private final FastDateFormat dateTimeFormat = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss");

  private ContextCollection contextCollection;
  private ContentRepository contentRepository;
  private List<String> segmentContexts = Arrays.asList(DEFAULT_SEGMENT_CONTEXT_NAME);

  @Required
  public void setContextCollection(ContextCollection contextCollection) {
    if (contextCollection == null) {
      throw new IllegalArgumentException("supplied 'contextCollection' must not be null");
    }
    this.contextCollection = contextCollection;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    if (contentRepository == null) {
      throw new IllegalArgumentException("supplied 'contentRepository' must not be null");
    }
    this.contentRepository = contentRepository;
  }

  public void setSegmentContexts(List<String> segmentContexts) {
    if (segmentContexts == null) {
      throw new IllegalArgumentException("supplied 'segmentContexts' must not be null");
    }
    this.segmentContexts = segmentContexts;
  }

  private FastDateFormat selectFormat(String targetFormat) {
    if(SystemDateTimeSource.TIME_OF_DAY_PROPERTY.equals(targetFormat)) {
      return timeFormat;
    }
    if(SystemDateTimeSource.DATE_PROPERTY.equals(targetFormat)) {
      return dateFormat;
    }
    return dateTimeFormat;
  }

  /**
   * creates a JS array representation of the profiles.
   * Example:
   * <pre>
   * [
   * [ "profileA", "key1", "value1" ],
   * [ "profileA", "key2", "value2" ],
   * [ "profileA", "key3", "value3" ],
   * [ "profileB", "key1", "value1" ],
   * [ "profileB", "key2", "value2" ]
   * ]
   * </pre>
   *
   * @return a json representation of this context
   */
  public String getContextCollectionAsJson() {
    final List<String[]> data = new ArrayList<>();
    for (String contextName : contextCollection.getContextNames()) {
      final PropertyProvider context = contextCollection.getContext(contextName, PropertyProvider.class);
      if (context != null) {
        for (String propertyName : context.getPropertyNames()) {
          final Object value = context.getProperty(propertyName);

          // ignore null values
          if(value != null) {
            final String[] strings = toStrings(contextName, propertyName, value);
            if(strings.length > 0) {
              data.add(strings);
            }
          }
        }
      }
    }
    return GSON.toJson(data);
  }

  private String[] toStrings(final String contextName, final String propertyName, final Object value) {
    // meta properties contain __ (see, e.g.com.coremedia.personalization.search.solr.SolrScoredKeys)
    if(!propertyName.contains("__")) {

      final boolean isSegmentProperty = segmentContexts.contains(contextName);
      // every segment value different from "true" or true means "segment does not apply"
      if (!isSegmentProperty || value.toString().equals(Boolean.TRUE.toString())) {

        final String contentName = convertIdToContentName(propertyName);

        final ContextInformationHolder contextInformationHolder = new ContextInformationHolder(contextName, contentName, value);

        if(isSegmentProperty){
          contextInformationHolder.setValue("");
        } else {
          convertCalendarValue(contextInformationHolder);
        }
        return toStrings(contextInformationHolder);
      }

    }
    return new String[0];
  }

  /**
   * Converts a property mame (either a formatted content id or its numeric value) into the corresponding content's name
   *
   * @param propertyName the property name to convert
   * @return The content name.
   */
  private String convertIdToContentName(String propertyName) {
    try {
      final Content content = contentRepository.getContent(propertyName);
      if(content != null) {
        return content.getName();
      }
    } catch (Exception e) {
      LOG.debug("unable to get content name for content with id {}: {}", propertyName, e.getMessage());
    }
    return propertyName;
  }

  private void convertCalendarValue(ContextInformationHolder contextInformationHolder) {
    Object value = contextInformationHolder.getValue();

    if (value instanceof Calendar) {
      final Calendar calendar = ((Calendar)value);
      final String propertyName = contextInformationHolder.getPropertyName();
      final FastDateFormat fastDateFormat = selectFormat(propertyName);
      value = fastDateFormat.format(calendar);
    }
    contextInformationHolder.setValue(value);
  }

  private String[] toStrings(ContextInformationHolder contextInformationHolder) {
    final String propertyName = contextInformationHolder.getPropertyName();
    final Object propertyValue = contextInformationHolder.getValue();
    if (propertyName != null && propertyValue != null) {
      return new String[]{
              contextInformationHolder.getContextName(),
              propertyName,
              propertyValue.toString()
      };
    }
    return new String[0];
  }

}
