package com.coremedia.blueprint.studio.upload.dialog {

import com.coremedia.blueprint.studio.upload.FileWrapper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

/**
 * Container list observable to handle the status update
 * when the upload file list is changed.
 */
public class FileContainersObservable {
  private var containers:Array = [];

  private var validityExpression:ValueExpression;

  public function FileContainersObservable() {
    validityExpression = ValueExpressionFactory.create('valid', beanFactory.createLocalBean());
    validityExpression.setValue(false);
  }

  public function getFiles():Array {
    var files:Array = [];
    for (var i:int = 0; i < size(); i++) {
      var wrapper:FileWrapper = getAt(i).getFile();
      files.push(wrapper);
    }
    return files;
  }

  public function getValidityExpression():ValueExpression {
    return validityExpression;
  }

  public function add(fc:FileContainer):void {
    containers.push(fc);
    ValueExpressionFactory.create(FileWrapper.NAME_PROPERTY, fc.getFile()).addChangeListener(validate);
    validate();
  }

  public function remove(fc:FileContainer):void {
    fc.destroy();
    for (var i:int = 0; i < containers.length; i++) {
      if (containers[i].getId() === fc.getId()) {
        containers = containers.slice(0, i).concat(containers.slice(i + 1));
        break;
      }
    }
    validate();
  }

  public function getAt(index:Number):FileContainer {
    return containers[index];
  }

  /**
   * Returns the amount of containers.
   * @return
   */
  public function size():Number {
    return containers.length;
  }

  /**
   * Returns true if the container list is empty.
   * @return
   */
  public function isEmpty():Boolean {
    return containers.length === 0;
  }

  private function validate():void {
    if(isEmpty()) {
      validityExpression.setValue(true);
    }
    for (var i:int = 0; i < containers.length; i++) {
      var fc:FileContainer = containers[i];
      if (isEmptyString(fc.getFile().getName())) {
        validityExpression.setValue(true);
        return;
      }
    }
    validityExpression.setValue(false);
  }

  private static function isEmptyString(value:String):Boolean {
    return !value || value.length === 0;
  }
}
}
