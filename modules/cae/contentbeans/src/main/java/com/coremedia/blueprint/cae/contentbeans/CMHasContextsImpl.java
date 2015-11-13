package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.objectserver.dataviews.DataViewFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Generated extension class for immutable beans of document type "CMHasContexts".
 */
public class CMHasContextsImpl extends CMHasContextsBase {
  private static final Log LOG = LogFactory.getLog(CMHasContextsImpl.class);

  private DataViewFactory dataViewFactory;

  public DataViewFactory getDataViewFactory() {
    return dataViewFactory;
  }

  @Required
  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }
}
