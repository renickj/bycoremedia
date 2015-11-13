package com.coremedia.ecommerce.studio.rest;

import com.coremedia.rest.cap.exception.ParameterizedException;
import com.coremedia.util.Util;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


/**
 * Exception to transport a given error code and message to the REST client.
 */
public class CatalogRestException extends ParameterizedException {
  private static Map<String,String> errorNames = new HashMap<>();

  private static String fetchErrorName(String errorCode) {
    Class aClass = CatalogRestErrorCodes.class;
    return Util.getConstantName(aClass, "LIVECONTEXT_ERROR_", errorCode);
  }

  /**
   * Returns a human-readable error name of this exception.
   *
   * @param errorCode code to translate
   * @return a human-readable error name of this exception
   */
  public static synchronized String getErrorName(String errorCode) {
    String name = errorNames.get(errorCode);
    if(name == null) {
      name = fetchErrorName(errorCode);
      errorNames.put(errorCode, name);
    }
    return name;
  }

  public CatalogRestException(Response.Status status, String errorCode, String message) {
    super(status, errorCode, getErrorName(errorCode), message);
  }
}