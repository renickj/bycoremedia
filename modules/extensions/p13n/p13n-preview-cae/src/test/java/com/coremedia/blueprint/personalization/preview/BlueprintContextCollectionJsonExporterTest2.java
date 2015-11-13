package com.coremedia.blueprint.personalization.preview;

import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.ContextCollectionImpl;
import com.coremedia.personalization.context.PropertyProvider;
import com.coremedia.personalization.context.collector.SystemDateTimeSource;
import org.junit.Test;

import java.util.Calendar;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlueprintContextCollectionJsonExporterTest2 {

  // expected example: "[["system","timeOfDay","15:08:26"],["system","date","2013-01-11"],["system","dayOfWeek","6"],["system","dateTime","2013-01-11T15:08:26"]]"
  static final Pattern pattern = Pattern.compile("\\[\\[\"system\",\"timeOfDay\",\"\\d\\d:\\d\\d:\\d\\d\"],\\[\"system\",\"date\",\"\\d{4}-\\d\\d-\\d\\d\"\\],\\[\"system\",\"dayOfWeek\",\"\\d\"\\],\\[\"system\",\"dateTime\",\"\\d{4}-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\"\\]\\]");

  @Test
  public void retest_PERSO_343() {
    // the error is in 'timeOfDay' time representation
    assertFalse(pattern.matcher("[[\"system\",\"timeOfDay\",\"1970-01-09T15:07:58\"],[\"system\",\"date\",\"2013-01-11\"],[\"system\",\"dayOfWeek\",\"6\"],[\"system\",\"dateTime\",\"2013-01-11T15:07:58\"]]").matches());

    // now let's check it
    BlueprintContextCollectionJsonExporter blueprintContextCollectionJsonExporter = new BlueprintContextCollectionJsonExporter();
    ContextCollection cc = new ContextCollectionImpl();
    SystemDateTimeSource systemDateTimeSource = new SystemDateTimeSource();
    systemDateTimeSource.setContextName("system");
    systemDateTimeSource.preHandle(null, null, cc);
    blueprintContextCollectionJsonExporter.setContextCollection(cc);
    final String contextCollectionAsJson = blueprintContextCollectionJsonExporter.getContextCollectionAsJson();
    assertTrue(contextCollectionAsJson, pattern.matcher(contextCollectionAsJson).matches());

    PropertyProvider propertyProvider = (PropertyProvider) cc.getContext("system");
    Calendar calendar = (Calendar) propertyProvider.getProperty(SystemDateTimeSource.TIME_OF_DAY_PROPERTY);

    // now modification of a calendar instance contained in this context lead to a different output
    calendar.set(Calendar.YEAR, 888);

    // so let's check again if the pattern matches
    assertTrue(contextCollectionAsJson, pattern.matcher(blueprintContextCollectionJsonExporter.getContextCollectionAsJson()).matches());
  }
}
