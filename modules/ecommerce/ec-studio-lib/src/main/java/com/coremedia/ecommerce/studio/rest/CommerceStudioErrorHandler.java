package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteError;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 * Dispatches and throws studio rest errors for incoming {@link com.coremedia.livecontext.ecommerce.common.CommerceException}
 */
public class CommerceStudioErrorHandler {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceStudioErrorHandler.class);

  public static void handleCommerceException(CommerceException ex) throws CatalogRestException {
    if (ex instanceof CommerceRemoteException) {
      CommerceRemoteError remoteError = ((CommerceRemoteException) ex).getRemoteError();
      LOG.debug("CommerceRemoteException: " + remoteError.getErrorMessage(), ex);
      throw new CatalogRestException(Response.Status.INTERNAL_SERVER_ERROR, CatalogRestErrorCodes.CATALOG_INTERNAL_ERROR, remoteError.getErrorMessage());
    }
    //more to come
    else {
      LOG.debug("CommerceException", ex.getMessage());
      CatalogRestException catalogRestException = new CatalogRestException(Response.Status.SERVICE_UNAVAILABLE, CatalogRestErrorCodes.CATALOG_UNAVAILABLE, ex.getMessage());
      if (catalogRestException.getCause() == null) {
        catalogRestException.initCause(ex);
      }
      throw catalogRestException;
    }
  }
}
