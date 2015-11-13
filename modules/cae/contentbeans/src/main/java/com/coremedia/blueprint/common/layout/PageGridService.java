package com.coremedia.blueprint.common.layout;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;

public interface PageGridService {

  /**
   * Returns a PageGrid for the given navigation context
   */
  PageGrid getContentBackedPageGrid(CMNavigation navigationContext);
}
