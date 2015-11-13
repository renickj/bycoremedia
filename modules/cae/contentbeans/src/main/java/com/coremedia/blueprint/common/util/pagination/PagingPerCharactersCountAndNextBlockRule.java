package com.coremedia.blueprint.common.util.pagination;

import org.springframework.util.Assert;

class PagingPerCharactersCountAndNextBlockRule implements PagingRule {

  private static final int DEFAULT_MAX_CHARACTERS_PER_PAGE = 2000;
  private static final String DEFAULT_PAGE_BLOCK_TAGS = "p table pre blockquote ul ol";
  private Paginator paginator;
  private String blocktags;
  private int maxCharactersPerPage = DEFAULT_MAX_CHARACTERS_PER_PAGE;

  @Override
  public void setPaginator(Paginator pagingFilter) {
    this.paginator = pagingFilter;
    blocktags = DEFAULT_PAGE_BLOCK_TAGS;
  }

  public void setBlockTags(String blocktags) {
    this.blocktags = blocktags;
  }

  @Override
  public void setPagingUnitsNumber(int pagingUnitsNumber) {
    maxCharactersPerPage = pagingUnitsNumber;
  }

  @Override
  public int getPagingUnitsNumber() {
    return maxCharactersPerPage;
  }

  @Override
  public boolean match(String localName) {
    Assert.notNull(paginator);
    return (blocktags.contains(localName))
            && (paginator.getCharacterCounter() > maxCharactersPerPage);
  }


}
