package com.coremedia.livecontext.ecommerce.ibm.common;

import java.util.List;

/**
 * Pojo generated from the json response from erroneous REST calls.
 */
public class WcServiceErrors {

  private List<WcServiceError> errors;

  public List<WcServiceError> getErrors() {
    return errors;
  }

  public void setErrors(List<WcServiceError> errors) {
    this.errors = errors;
  }
}
