package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.CommerceRemoteError;

/**
 * Pojo generated from the json response from erroneous REST calls.
 */
public class WcServiceError implements CommerceRemoteError {

  private String errorCode;
  private String errorKey;
  private String errorMessage;
  private String errorParameters;

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  @Override
  public String getErrorKey() {
    return errorKey;
  }

  public void setErrorKey(String errorKey) {
    this.errorKey = errorKey;
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @Override
  public String getErrorParameters() {
    return errorParameters;
  }

  public void setErrorParameters(String errorParameters) {
    this.errorParameters = errorParameters;
  }
}
