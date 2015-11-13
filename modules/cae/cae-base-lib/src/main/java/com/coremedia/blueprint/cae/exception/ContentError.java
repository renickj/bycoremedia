package com.coremedia.blueprint.cae.exception;

public class ContentError {
  private final String message;
  private final Exception wrappedException;

  /**
   * Creates a new ContentError instance.
   *
   * @param message An error message
   */

  public ContentError(String message) {
    this.message = message;
    this.wrappedException = null;
  }

  public ContentError(String message, Exception wrappedException) {
    this.message = message;
    this.wrappedException = wrappedException;
  }

  public String getMessage() {
    return message;
  }

  public Exception getWrappedException() {
    return wrappedException;
  }
}
