package com.coremedia.blueprint.common.util.pagination;

import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.List;

public class StAXPaginatorTest {

  @Test
  public void testOneBlockPerPageRule() throws Exception {
    InputStream stream = StAXPaginatorTest.class.getResourceAsStream("embedded_paragraphs.xml");
    Markup sourceMarkup = MarkupFactory.fromInputSource(new InputSource(stream));
    Paginator paginator = PagingRuleType.BlockCountRule.getPaginator();
    PagingRule rule = PagingRuleType.BlockCountRule.getPagingRule();
    rule.setPagingUnitsNumber(1);
    paginator.setPagingRule(rule);
    List<Markup> markups = paginator.split(sourceMarkup);
    Assert.assertEquals(5, markups.size());
  }

  @Test
  public void testTwoBlocksPerPageRule() throws Exception {
    InputStream stream = StAXPaginatorTest.class.getResourceAsStream("embedded_paragraphs.xml");
    Markup sourceMarkup = MarkupFactory.fromInputSource(new InputSource(stream));
    Paginator paginator = PagingRuleType.BlockCountRule.getPaginator();
    PagingRule rule = PagingRuleType.BlockCountRule.getPagingRule();
    rule.setPagingUnitsNumber(2);
    paginator.setPagingRule(rule);
    List<Markup> markups = paginator.split(sourceMarkup);
    Assert.assertEquals(3, markups.size());
  }

  @Test
  public void testCharactersCountAndNextParagraphRule() throws Exception {
    InputStream stream = StAXPaginatorTest.class.getResourceAsStream("embedded_paragraphs.xml");
    Markup sourceMarkup = MarkupFactory.fromInputSource(new InputSource(stream));
    Paginator paginator = PagingRuleType.CharactersCountAndNextParagraphRule.getPaginator();
    PagingRule rule = PagingRuleType.CharactersCountAndNextParagraphRule.getPagingRule();
    rule.setPagingUnitsNumber(1000);
    paginator.setPagingRule(rule);
    List<Markup> markups = paginator.split(sourceMarkup);
    Assert.assertEquals(2, markups.size());
  }

  @Test
  public void testCharactersCountAndNextBlockRule() throws Exception {
    InputStream stream = StAXPaginatorTest.class.getResourceAsStream("complex_markup.xml");
    Markup sourceMarkup = MarkupFactory.fromInputSource(new InputSource(stream));
    Paginator paginator = PagingRuleType.CharactersCountAndNextBlockRule.getPaginator();
    PagingRule rule = PagingRuleType.CharactersCountAndNextBlockRule.getPagingRule();
    rule.setPagingUnitsNumber(10);
    paginator.setPagingRule(rule);
    List<Markup> markups = paginator.split(sourceMarkup);
    Assert.assertEquals(11, markups.size());
  }

  @Test
  public void testCharactersCountAndNextWordRule() throws Exception {
    InputStream stream = StAXPaginatorTest.class.getResourceAsStream("embedded_paragraphs.xml");
    Markup sourceMarkup = MarkupFactory.fromInputSource(new InputSource(stream));
    Paginator paginator = PagingRuleType.CharactersCountAndNextWordRule.getPaginator();
    PagingRule rule = PagingRuleType.CharactersCountAndNextWordRule.getPagingRule();
    rule.setPagingUnitsNumber(1000);
    paginator.setPagingRule(rule);
    List<Markup> markups = paginator.split(sourceMarkup);
    //for (Markup markup : markups)
    //    System.out.println("markup " + markup);
  }

  @Test
  public void testComplexMarkupTest() throws Exception {
    InputStream stream = StAXPaginatorTest.class.getResourceAsStream("complex_markup.xml");
    Markup sourceMarkup = MarkupFactory.fromInputSource(new InputSource(stream));
    Paginator paginator = PagingRuleType.BlockCountRule.getPaginator();
    PagingRule rule = PagingRuleType.BlockCountRule.getPagingRule();
    rule.setPagingUnitsNumber(1);
    paginator.setPagingRule(rule);
    List<Markup> markups = paginator.split(sourceMarkup);
    Assert.assertEquals(12, markups.size());
  }
}