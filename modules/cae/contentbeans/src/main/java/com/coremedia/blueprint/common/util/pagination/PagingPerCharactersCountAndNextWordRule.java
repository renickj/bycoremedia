package com.coremedia.blueprint.common.util.pagination;

import org.springframework.util.Assert;

class PagingPerCharactersCountAndNextWordRule implements PagingRule {
  private static final int DEFAULT_MAX_CHARACTERS_PER_PAGE = 2000;
  private Paginator paginator;
  private int maxCharactersPerPage = DEFAULT_MAX_CHARACTERS_PER_PAGE;

  @Override
  public void setPaginator(Paginator paginator) {
    this.paginator = paginator;
  }

  @Override
  public void setPagingUnitsNumber(int pagingUnitsNumber) {
    maxCharactersPerPage = pagingUnitsNumber;
  }

  @Override
  public boolean match(String localName) {
    Assert.notNull(paginator);
    return (paginator.getCharacterCounter() > maxCharactersPerPage);
  }

  @Override
  public int getPagingUnitsNumber() {
    return maxCharactersPerPage;
  }

}