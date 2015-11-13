package com.coremedia.blueprint.cae.layout;

import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridService;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.blueprint.common.layout.PageGridService;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.services.validation.ValidationService;

public class PageGridServiceImpl implements PageGridService {

  private ContentBackedPageGridService contentBackedPageGridService;
  private ValidationService<Linkable> validationService;

  public void setContentBackedPageGridService(ContentBackedPageGridService contentBackedPageGridService) {
    this.contentBackedPageGridService = contentBackedPageGridService;
  }

  public void setValidationService(ValidationService<Linkable> validationService) {
    this.validationService = validationService;
  }

  @Override
  public PageGrid getContentBackedPageGrid(CMNavigation navigationContext) {
    return new PageGridImpl(navigationContext, contentBackedPageGridService, validationService);
  }
}
