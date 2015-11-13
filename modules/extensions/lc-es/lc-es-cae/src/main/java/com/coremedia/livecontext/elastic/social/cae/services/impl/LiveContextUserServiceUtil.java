package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import org.apache.commons.lang3.StringUtils;

public class LiveContextUserServiceUtil {

  /**
   * Resolves the actual error code from the exception.
   * e.g. for IBM a number is expected here.
   * @param e The remote exception that contains the error details.
   */
  public static String resolveErrorMessage(CommerceRemoteException e) {
    String code = e.getRemoteError().getErrorCode();
    if(StringUtils.isEmpty(code)) {
      code = e.getRemoteError().getErrorKey();
    }

    int id = 0;
    try {
      id = Integer.parseInt(code);
      return "commerce.error." + id;
    }
    catch (NumberFormatException nfe) {
      //ignore;
    }
    return code;
  }

}
