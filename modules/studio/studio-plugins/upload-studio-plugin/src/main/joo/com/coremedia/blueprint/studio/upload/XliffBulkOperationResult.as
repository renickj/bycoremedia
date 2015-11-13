package com.coremedia.blueprint.studio.upload {
import com.coremedia.cap.content.results.impl.BulkOperationResultImpl;
import com.coremedia.ui.data.error.RemoteError;

public class XliffBulkOperationResult extends BulkOperationResultImpl {
  public function XliffBulkOperationResult(error:RemoteError, successful:Boolean,
                                           results:Array) {
    super(error, successful, results);
  }
}
}
