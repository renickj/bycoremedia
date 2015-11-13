package com.coremedia.blueprint.common.util.pagination;

public enum PagingRuleType {
  DelimitingBlockCountRule(StAXBlockElementPaginator.class, DelimitingPagingPerBlockCountRule.class),
  BlockCountRule(StAXBlockElementPaginator.class, PagingPerBlockCountRule.class),
  CharactersCountAndNextParagraphRule(StAXBlockElementPaginator.class, PagingPerCharactersCountAndNextParagraphRule.class),
  CharactersCountAndNextBlockRule(StAXBlockElementPaginator.class, PagingPerCharactersCountAndNextBlockRule.class),
  CharactersCountAndNextWordRule(StAXPlainTextPaginator.class, PagingPerCharactersCountAndNextWordRule.class);

  private Class paginatorClazz;
  private Class paginationRuleClazz;

  PagingRuleType(Class paginatorClazz, Class paginationRuleClazz) {

    this.paginatorClazz = paginatorClazz;
    this.paginationRuleClazz = paginationRuleClazz;
  }

  public Paginator getPaginator() throws IllegalAccessException, InstantiationException {
    Paginator paginator = (Paginator) paginatorClazz.newInstance();
    paginator.setPagingRule(getPagingRule());
    return paginator;
  }

  public PagingRule getPagingRule() throws IllegalAccessException, InstantiationException {
    return (PagingRule) paginationRuleClazz.newInstance();
  }
}
