package com.coremedia.blueprint.cae.search.solr;

import org.apache.solr.common.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides access to search engine specific String formatting
 */
public final class SolrSearchFormatHelper {

  private SolrSearchFormatHelper() {
  }

  public static String fromPastToValue(String value) {
    StringBuilder builder = new StringBuilder(SolrQueryBuilder.OPENING_BRACKET);
    builder.append(SolrQueryBuilder.ANY_VALUE_TO);
    builder.append(value);
    builder.append(SolrQueryBuilder.CLOSING_BRACKET);
    return builder.toString();
  }

  public static String fromValueIntoFuture(String value) {
    StringBuilder builder = new StringBuilder(SolrQueryBuilder.OPENING_BRACKET);
    builder.append(value);
    builder.append(SolrQueryBuilder.TO_ANY_VALUE);
    builder.append(SolrQueryBuilder.CLOSING_BRACKET);
    return builder.toString();
  }

  public static String calendarToString(Calendar calendar) {
    return DateUtil.getThreadLocalDateFormat().format(calendar.getTime());
  }

  public static String dateToString(Date date) {
    return DateUtil.getThreadLocalDateFormat().format(date);
  }

}
