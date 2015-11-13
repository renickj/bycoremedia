package com.coremedia.blueprint.studio.util {
import joo.ResourceBundleAwareClassLoader;

/**
 * Common date utilities such as date formatting and date compare.
 */
public class DateUtil {

  public static const DE_DATETIME_FORMAT:String = 'd.m.y H:i';
  public static const EN_DATETIME_FORMAT:String = 'm/d/y h:i A';

  /**
   * Formats the given date to the long date and time format
   */
  public static function formatDateTime(date:Date):String {
    if (!date) {
      return '-';
    }
    var locale:String = ResourceBundleAwareClassLoader.INSTANCE.getLocale();
    var pattern:String = EN_DATETIME_FORMAT;
    if (locale.toLowerCase() === 'de') {
      pattern = DE_DATETIME_FORMAT;
    }
    var dateString:String = date.format(pattern);
    return dateString;
  }
}
}
