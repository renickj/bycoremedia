package com.coremedia.blueprint.studio.upload {
import com.coremedia.cap.content.impl.BulkOperationResultBuilder;
import com.coremedia.cap.content.results.BulkOperationResult;
import com.coremedia.cap.content.results.BulkOperationResultItem;
import com.coremedia.ui.data.error.RemoteError;

public class XliffBulkOperationResultBuilder extends BulkOperationResultBuilder {
  override protected function makeResultItem(resultItem:Object):BulkOperationResultItem {
    return new XliffBulkOperationResultItem(resultItem['category'], resultItem['content'], resultItem['property']);
  }

  override protected function makeResultObject(error:RemoteError, successful:Boolean,
                                               resultItems:Array):BulkOperationResult {
    return new XliffBulkOperationResult(error, successful, resultItems);
  }
}
}
