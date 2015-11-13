package com.coremedia.blueprint.studio.upload {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.results.impl.BulkOperationResultItemImpl;

public class XliffBulkOperationResultItem extends BulkOperationResultItemImpl {
  public function XliffBulkOperationResultItem(resultCode:String, content:Content, property:String) {
    super(resultCode, content, impediment);
    this['property'] = property;
  }

  public native function get property():String;
}
}
