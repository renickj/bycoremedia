package com.coremedia.blueprint.studio.struct.editor {

import com.coremedia.blueprint.studio.struct.config.booleanEditor;
import com.coremedia.blueprint.studio.struct.config.contentTypeEditor;
import com.coremedia.blueprint.studio.struct.config.linkEditor;
import com.coremedia.blueprint.studio.struct.config.minMaxEditor;
import com.coremedia.blueprint.studio.struct.config.numberEditor;
import com.coremedia.blueprint.studio.struct.config.structInputField;
import com.coremedia.blueprint.studio.struct.config.textAreaEditor;
import com.coremedia.blueprint.studio.struct.config.textEditor;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Container;
import ext.Panel;
import ext.form.Checkbox;
import ext.tree.TreeNode;

/**
 * Base class of the struct property editor, creates the global value expressions for it
 * and implements the toolbar actions.
 */
public class StructInputFieldBase extends Panel {
  private static const CAP_CONTENT_TYPE_PREFIX:String = 'coremedia:///cap/contenttype/';
  private static const CAP_CONTENT_PREFIX:String = 'coremedia:///cap/content/';

  private var selectedNodeExpression:ValueExpression;
  private var structHandler:StructHandler;
  private var activeNode:TreeNode;
  private var activeModel:ElementModel;
  private var bindTo:ValueExpression;
  private var forceReadOnlyValueExpression:ValueExpression;

  public function StructInputFieldBase(config:structInputField) {
    this.structHandler = config.structHandler;
    this.selectedNodeExpression = config.selectedNodeExpression;
    this.selectedNodeExpression.addChangeListener(renderInputFields);
    this.structHandler.getModusExpression().addChangeListener(modusChanged);
    this.bindTo = config.bindTo;
    this.forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
    super(config);
  }

  /**
   * Re-render the input fields if the input modus has ben changed.
   * @param ve
   */
  private function modusChanged(ve:ValueExpression):void {
    renderInputFields();
  }

  /**
   * Creates the input fields for the values of the selected
   * node or removes input fields if node is null.
   */
  private function renderInputFields():void {
    var fields:Container = getInputFieldContainer();
    activeNode = selectedNodeExpression.getValue() as TreeNode;
    if (activeNode) {
      activeModel = structHandler.getData(activeNode);
      fields.removeAll(true);
      initFields();
    }
    else {
      fields.removeAll(true);
    }
    fields.doLayout(false, true);
  }

  /**
   * Adds the concrete fields depending on the node type.
   */
  private function initFields():void {
    var fieldContainer:Container = getInputFieldContainer();
    var type:int = activeModel.getType();

      if (type === ElementModel.ELEMENT_INT_PROPERTY) {
          fieldContainer.add(createTextField(ElementModel.NAME_PROPERTY,false));
          fieldContainer.add(createNumberField(ElementModel.VALUE_PROPERTY));
      }
      else if (type === ElementModel.ELEMENT_STRING_PROPERTY ) {
          fieldContainer.add(createTextField(ElementModel.NAME_PROPERTY,false));
          fieldContainer.add(createTextAreaField(ElementModel.VALUE_PROPERTY));
      }
      else if(type === ElementModel.ELEMENT_STRUCT_PROPERTY) {
      fieldContainer.add(createTextField(ElementModel.NAME_PROPERTY,false));
    }
    else if(type === ElementModel.ELEMENT_BOOLEAN_PROPERTY) {
      fieldContainer.add(createTextField(ElementModel.NAME_PROPERTY,false));
      if(structHandler.getModus() == StructHandler.MODUS_FORMATTED) {
        fieldContainer.add(createBooleanField(ElementModel.VALUE_PROPERTY));
      }
      else {
        fieldContainer.add(createTextAreaField(ElementModel.VALUE_PROPERTY));
      }
    }
    else if(type === ElementModel.ELEMENT_LINK_PROPERTY) {
      fieldContainer.add(createTextField(ElementModel.NAME_PROPERTY,false));
      var linkEditor:LinkEditor = null;
      var linkTypeVE:ValueExpression = getLinkTypeValueExpression();
      //link list
      if(structHandler.getModus() == StructHandler.MODUS_FORMATTED && (!activeModel.getHRef() || isCapContentId(activeModel.getHRef()))) {
        linkEditor = createLinkField(ElementModel.HREF_PROPERTY,getHrefValueExpression(), linkTypeVE);
        fieldContainer.add(linkEditor);
      } else {
        fieldContainer.add(createTextField(ElementModel.HREF_PROPERTY, true, 'href'));
      }

      //content type combo
      if(structHandler.getModus() == StructHandler.MODUS_FORMATTED  && (!activeModel.getLinkType() ||isCapContentTypeId(activeModel.getLinkType()))) {
        fieldContainer.add(createContentTypeField(ElementModel.LINK_TYPE_PROPERTY,linkTypeVE, linkEditor));
      } else {
        fieldContainer.add(createTextField(ElementModel.LINK_TYPE_PROPERTY,false));
      }
    }
    else if (type == ElementModel.ELEMENT_STRING) {
      fieldContainer.add(createTextAreaField(ElementModel.VALUE_PROPERTY));
    }
    else if(type == ElementModel.ELEMENT_BOOLEAN) {
      if(structHandler.getModus() == StructHandler.MODUS_FORMATTED) {
        fieldContainer.add(createBooleanField(ElementModel.VALUE_PROPERTY));
      }
      else {
        fieldContainer.add(createTextAreaField(ElementModel.VALUE_PROPERTY));
      }
    }
    else if (type === ElementModel.ELEMENT_INT) {
      fieldContainer.add(createNumberField(ElementModel.VALUE_PROPERTY));
    }
    else if(type === ElementModel.ELEMENT_LINK) {
      if(structHandler.getModus() == StructHandler.MODUS_FORMATTED && (!activeModel.getHRef() || isCapContentId(activeModel.getHRef()))) {
        linkTypeVE = ValueExpressionFactory.create(ElementModel.LINK_TYPE_PROPERTY, beanFactory.createLocalBean());
        var parent:ElementModel = structHandler.getData(activeNode.parentNode as TreeNode);
        linkTypeVE.setValue(parent.getRawLinkTypeName());
        fieldContainer.add(createLinkField(ElementModel.HREF_PROPERTY,getHrefValueExpression(), linkTypeVE));
      } else {
        fieldContainer.add(createTextField(ElementModel.HREF_PROPERTY, true, 'href'));
      }
    }
    else if (type === ElementModel.ELEMENT_LINK_LIST_PROPERTY) {
      fieldContainer.add(createTextField(ElementModel.NAME_PROPERTY,false));
//      fieldContainer.add(createMinMaxField());
      if(structHandler.getModus() == StructHandler.MODUS_FORMATTED) {
        fieldContainer.add(createContentTypeField(ElementModel.LINK_TYPE_PROPERTY,getLinkTypeValueExpression(), null));
      } else {
        fieldContainer.add(createTextField(ElementModel.LINK_TYPE_PROPERTY,false));
      }
    }
    else if (type === ElementModel.ELEMENT_BOOLEAN_LIST_PROPERTY ||
      type == ElementModel.ELEMENT_STRUCT_LIST_PROPERTY ||
      type == ElementModel.ELEMENT_STRING_LIST_PROPERTY ||
      type == ElementModel.ELEMENT_INT_LIST_PROPERTY ) {
      fieldContainer.add(createTextField(ElementModel.NAME_PROPERTY,false));
//      fieldContainer.add(createMinMaxField());
    }
    else {
//      fieldContainer.add(lnew Label(label({text:Blueprint_properties.INSTANCE.Struct_no_attributes})));
    }
    fieldContainer.doLayout(false, true);
  }


  /**
   * Returns true if the given string is a valid content id.
   * @param id The cap content id
   * @return True, if the id has a valid format.
   */
  private static function isCapContentTypeId(id:String):Boolean {
    if (!id || id.indexOf(CAP_CONTENT_TYPE_PREFIX) === -1) {
      return false;
    }
    var numericId:* = parseInt(id.substr(id.lastIndexOf('/') + 1, id.length));
    return typeof numericId == "number";
  }

  /**
   * Creates the value expression wrapper to transform the selected value to the value that will
   * be stored for the ElementModel.
   * @return
   */
  private function getLinkTypeValueExpression():ValueExpression {
    var ve:ValueExpression = ValueExpressionFactory.create(ElementModel.LINK_TYPE_PROPERTY, beanFactory.createLocalBean());
    if(activeModel.getLinkType()) {
      ve.setValue(activeModel.getRawLinkTypeName());
    }
    ve.addChangeListener(linkTypeChanged);
    return ve;
  }

  /**
   * Converts the selected link type and stored it into the element model.
   * @param ve
   */
  private function linkTypeChanged(ve:ValueExpression):void {
    var ltExpression:ValueExpression = ValueExpressionFactory.create(ElementModel.LINK_TYPE_PROPERTY, activeModel);
    var value:String = ve.getValue();
    var convertedValue:String = getContentTypeCapId(value);
    ltExpression.setValue(convertedValue);
    inputChanged(ltExpression);
  }

  /**
   * Creates the wrapper value expression that is used for the input field. It registers
   * a separate change listener to re-format the selected value corresponding to the orignal struct format.
   * @return
   */
  private function getHrefValueExpression():ValueExpression {
    var ve:ValueExpression = ValueExpressionFactory.create(ElementModel.HREF_PROPERTY, beanFactory.createLocalBean());
    if(activeModel.getHRef()) {
      ve.setValue([ContentUtil.getContent(activeModel.getHRef())]);
    }
    else {
      ve.setValue([]);
    }
    ve.addChangeListener(refChanged);
    return ve;
  }

  private function refChanged(ve:ValueExpression):void {
    var refExpression:ValueExpression = ValueExpressionFactory.create(ElementModel.HREF_PROPERTY, activeModel);
    var selection:Array = ve.getValue();
    if(selection && selection.length > 0) {
      var selectedContent:Content = selection[0] as Content;
      refExpression.setValue(selectedContent.getId());
    }
    else {
      refExpression.setValue(null);
    }
    inputChanged(refExpression);
  }

  private function addReadOnlyPlugin(config:Object):Object {
    config.forceReadOnlyValueExpression = forceReadOnlyValueExpression;
    return config;
  }

  /**
   * Creates a text property editor for the selected node and field.
   * @param propertyName The property name, used to find the matching label.
   * @param labelProperty The optional label property that will be used instead of the property name.
   * @return
   */
  private function createTextField(propertyName:String, allowBlank:Boolean, labelProperty:String = undefined):TextEditor {
    var ve:ValueExpression = ValueExpressionFactory.create(propertyName, activeModel);
    ve.addChangeListener(inputChanged);
    if(labelProperty) {
      propertyName = labelProperty;
    }
    var tf:TextEditor = new TextEditor(textEditor(addReadOnlyPlugin({
      bindTo:ve,
      allowBlank:allowBlank,
      propertyName:propertyName,
      cls:'string-property-field'
    })));
    tf.addListener('destroy', function():void {
      ve.removeChangeListener(inputChanged);
    });
    return tf;
  }

  /**
   * Creates a checkbox property editor for the selected node and field.
   * @param propertyName The property name to find the label for.
   * @param s The optional label to overwrite the property label.
   * @return
   */
  private function createBooleanField(propertyName:String):BooleanEditor {
    var ve:ValueExpression = ValueExpressionFactory.create(propertyName, activeModel);
    var handler:Function = function(checkbox:Checkbox, checked:Boolean):void {
      ve.setValue(''+checked);
    };
    ve.addChangeListener(inputChanged);
    var tf:BooleanEditor = new BooleanEditor(booleanEditor(addReadOnlyPlugin({
      bindTo:ve,
      checkboxHandler:handler,
      propertyName:propertyName,
      cls:'string-property-field'
    })));
    tf.addListener('destroy', function():void {
      ve.removeChangeListener(inputChanged);
    });
    return tf;
  }

  /**
   * Creates a text property editor for the selected node and field.
   * @param propertyName
   * @return
   */
  private function createNumberField(propertyName:String):NumberEditor {
    var ve:ValueExpression = ValueExpressionFactory.create(propertyName, activeModel);
    ve.addChangeListener(inputChanged);

    var tf:NumberEditor = new NumberEditor(numberEditor(addReadOnlyPlugin({
      bindTo:ve,
      propertyName:propertyName,
      cls:'string-property-field'
    })));
    tf.addListener('destroy', function():void {
      ve.removeChangeListener(inputChanged);
    });
    return tf;
  }

  /**
   * Creates a text property editor for the selected node and field.
   * @param propertyName
   * @return
   */
  private function createMinMaxField():MinMaxEditor {
    var max:ValueExpression = ValueExpressionFactory.create(ElementModel.MAX_PROPERTY, activeModel);
    max.addChangeListener(inputChanged);
    var min:ValueExpression = ValueExpressionFactory.create(ElementModel.MIN_PROPERTY, activeModel);
    min.addChangeListener(inputChanged);
    var tf:MinMaxEditor = new MinMaxEditor(minMaxEditor(addReadOnlyPlugin({
      bindToMin:min,
      bindToMax:max,
      cls:'string-property-field'
    })));
    tf.addListener('destroy', function():void {
      max.removeChangeListener(inputChanged);
      min.removeChangeListener(inputChanged);
    });
    return tf;
  }

  /**
   * Creates a text property editor for the selected node and field.
   * @param propertyName The property name to find the label for.
   * @return
   */
  private function createTextAreaField(propertyName:String):TextAreaEditor {
    var ve:ValueExpression = ValueExpressionFactory.create(propertyName, activeModel);
    ve.addChangeListener(inputChanged);
    var tf:TextAreaEditor = new TextAreaEditor(textAreaEditor(addReadOnlyPlugin({
      bindTo:ve,
      propertyName:propertyName,
      cls:'string-property-field'
    })));
    tf.addListener('destroy', function():void {
      ve.removeChangeListener(inputChanged);
    });
    return tf;
  }

  /**
   * Creates a text property editor for the selected node and field.
   * @param propertyName  The property name to find the label for.
   * @param ve The value expression to use for the input field.
   * @return
   */
  private function createContentTypeField(propertyName:String, ve:ValueExpression, linkEditor:LinkEditor):ContentTypeEditor {
    var tf:ContentTypeEditor = new ContentTypeEditor(contentTypeEditor(addReadOnlyPlugin({
      bindTo:ve,
      propertyName:propertyName,
      node:activeNode,
      structHandler:structHandler,
      cls:'string-property-field',
      linkEditor:linkEditor
    })));
    return tf;
  }

  /**
   * Creates a text property editor for the selected node and field.
   * @param propertyName The property name to find the label for.
   * @param ve The value expression used for the link
   * @return
   */
  private function createLinkField(propertyName:String, ve:ValueExpression, linkTypeValueExpression:ValueExpression):LinkEditor {
    var tf:LinkEditor = new LinkEditor(linkEditor(addReadOnlyPlugin({
      bindTo:ve,
      propertyName:propertyName,
      linkTypeValueExpression:linkTypeValueExpression,
      cls:'string-property-field'
    })));

    return tf;
  }

  /**
   * Invoked when the user has entered some data.
   */
  private function inputChanged(ve:ValueExpression):void {
    activeModel.refresh(activeNode, structHandler.getModus());
    structHandler.doSave();
  }

  /**
   * Returns the field set the input fields are rendered into.
   * @return
   */
  private function getInputFieldContainer():Container {
    return this.find('itemId', 'inputFieldSet')[0] as Container;
  }

  /**
   * Formats the CAP id for the content type name.
   * @param linkType The type to format the name for.
   * @return
   */
  private static function getContentTypeCapId(linkType:String):String {
    return CAP_CONTENT_TYPE_PREFIX + linkType;
  }


  /**
   * Returns true if the given string is a valid content id.
   * @param id The cap content id
   * @return True, if the id has a valid format.
   */
  private static function isCapContentId(id:String):Boolean {
    if (!id || id.indexOf(CAP_CONTENT_PREFIX) === -1) {
      return false;
    }
    var numericId:* = parseInt(id.substr(id.lastIndexOf('/') + 1, id.length));
    return typeof numericId == "number";
  }
}
}
