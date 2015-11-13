package com.coremedia.livecontext.navigation;

/**
 * Exception thrown if the external id of an external navigation content could not be resolved.
 */
public class InvalidExternalNavigationIdException extends RuntimeException {

  public InvalidExternalNavigationIdException(String msg) {
    super("invalid external id: " + msg);
  }

  public InvalidExternalNavigationIdException(Throwable cause) {
    super(cause);
  }
}
