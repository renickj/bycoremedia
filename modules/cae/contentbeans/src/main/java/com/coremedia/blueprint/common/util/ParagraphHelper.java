package com.coremedia.blueprint.common.util;

import com.coremedia.blueprint.common.util.pagination.Paginator;
import com.coremedia.blueprint.common.util.pagination.PagingRule;
import com.coremedia.blueprint.common.util.pagination.PagingRuleType;
import com.coremedia.xml.Markup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.List;

public final class ParagraphHelper {
  private static final Log LOG = LogFactory.getLog(ParagraphHelper.class);

  public static final int DEFAULT_PARAGRAPH_PAGING_UNITS = 1;

  private ParagraphHelper() {
  }

  public static List<Markup> createParagraphs(Markup xml) {
    return ParagraphHelper.createParagraphs(xml, DEFAULT_PARAGRAPH_PAGING_UNITS);
  }

  public static List<Markup> createParagraphs(Markup xml, int pagingUnits) {
    return createParagraphs(xml, pagingUnits, PagingRuleType.DelimitingBlockCountRule);
  }

  public static List<Markup> createParagraphs(Markup xml, int pagingUnits, String pagingRuleTypeName) {
    if (pagingRuleTypeName == null || pagingRuleTypeName.equalsIgnoreCase("")) {
      return createParagraphs(xml, pagingUnits);
    }

    return createParagraphs(xml, pagingUnits, PagingRuleType.valueOf(pagingRuleTypeName));
  }

  public static List<Markup> createParagraphs(Markup xml, int pagingUnits, PagingRuleType pagingRuleType) {
    List<Markup> result = Collections.emptyList();
    try {
      Paginator paginator = pagingRuleType.getPaginator();
      PagingRule pagingRule = pagingRuleType.getPagingRule();
      pagingRule.setPagingUnitsNumber(pagingUnits);
      paginator.setPagingRule(pagingRule);
      result = paginator.split(xml);
    } catch (InstantiationException e) {
      LOG.error("cannot instantiate corresponding paging rule type", e);
    } catch (IllegalAccessException e) {
      LOG.error("cannot access corresponding paging rule type", e);
    }
    return result;
  }
}
