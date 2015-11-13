package com.coremedia.blueprint.studio.util {
import com.coremedia.ui.data.error.RemoteError;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;
import com.coremedia.ui.logging.Logger;

public class AjaxUtil {

  /**
   * Default error message when the RemoteServiceMethodResponse failed.
   * @param response The RemoteServiceMethodResponse of the failed request.
   */
  public static function onErrorMethodResponse(response:RemoteServiceMethodResponse):void {
    Logger.error('Request failed: ' + response.getError().errorName + '/' + response.getError().errorCode);
  }

  /**
   * Default error message with the given error
   * @param error the given remote error
   */
  public static function onError(error:RemoteError):void {
    Logger.error('Request failed: ' + error.errorName + '/' + error.errorCode);
  }

}
}