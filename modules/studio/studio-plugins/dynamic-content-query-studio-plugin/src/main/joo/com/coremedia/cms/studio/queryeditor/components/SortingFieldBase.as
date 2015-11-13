package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.common.impl.StructRemoteBeanImpl;
import com.coremedia.cms.editor.sdk.premular.PropertyFieldGroup;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.cms.studio.queryeditor.QueryEditorSettings_properties;
import com.coremedia.cms.studio.queryeditor.config.sortingField;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;

import ext.form.ComboBox;

public class SortingFieldBase extends PropertyFieldGroup {

  protected const FIELDS:String = "sortingFields";
  protected const SELECTED_FIELD:String = "selectedSortingField";
  protected const DIRECTIONS:String = "sortingDirections";
  protected const SELECTED_DIRECTION:String = "selectedSortingDirection";

  private var queryValueExpression:ValueExpression;
  private var sortingValueExpression:ValueExpression;
  private var readOnlyValueExpression:ValueExpression;
  private var propertyName:String;
  private var dcqe:ContentQueryEditor;
  private var localModel:Bean;

  private var fieldsComboBox:ComboBox;
  private var directionComboBox:ComboBox;
  private var commonModel:Bean;

  /*
   * @param assistantFormattedExpression serialized sorting expression for the assistant view
   * @return serialized sorting expression for the expert view
   */
  public static function translateAssistantToExpertFormat(assistantFormattedExpression:String):String {
    //assistant and expert formats are equal
    return assistantFormattedExpression;
  }


  public function SortingFieldBase(config:sortingField = null) {
    super(config);
    queryValueExpression = config.query;
    propertyName = config.propertyName;
    sortingValueExpression = queryValueExpression.extendBy(propertyName);
    readOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression(config.bindTo,
      config.forceReadOnlyValueExpression);
  }

  override protected function afterRender():void {
    super.afterRender();
    listenToModelChanges();
  }

  private function listenToModelChanges():void {
    var appliedConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;

    //listen to selected field change
    getLocalModel().addPropertyChangeListener(SELECTED_FIELD, onSelectedFieldChange);
    //listen to selected direction change
    getLocalModel().addPropertyChangeListener(SELECTED_DIRECTION, onSelectedDirectionChange);

    //listen to remote bean changes
    sortingValueExpression.addChangeListener(updateLocalModel);
    //it's important to populate local model with remote values first
    updateLocalModel(sortingValueExpression);

    //listen to applied conditions change
    getCommonModel().addPropertyChangeListener(appliedConditionsKey, onAppliedChange);
    updateFields(); //initial state

    readOnlyValueExpression.addChangeListener(enableOrDisable);
  }

  private function enableOrDisable(valueExpression:ValueExpression):void {
    var isCheckedOutByOther:Boolean = valueExpression.getValue();
    if (isCheckedOutByOther) {
      getFieldsComboBox().disable();
      getDirectionComboBox().disable();
    }
  }

  private function onAppliedChange():void {
    updateFields();
  }

  /**
   * Fired when the sort combo box is changed.
   * So sorting is only persisted in combination with the sorting order.
   * Therefore we have to apply the sort order too if no direction is already given.
   * This ensures that the actual struct property is written.
   * Otherwise the user may first change the sort order (after opening the document) and
   * the change won't result in an automatic checkout. Therefore the change won't be persisted.
   */
  private function onSelectedFieldChange():void {
    updateDirections();

    var direction:String = getLocalModel().get(SELECTED_DIRECTION);
    //check if the direction is already given, if not apply the first value as default.
    if(!direction) {
      direction = QueryEditorSettings_properties.INSTANCE.sort_direction_asc;
    }

    //applying a concrete direction value here will ensure that the struct property is updated.
    updateRemoteBeanAndSerializedExpression(direction);
  }

  private function onSelectedDirectionChange(e:PropertyChangeEvent):void {
    updateRemoteBeanAndSerializedExpression(e.newValue);
  }

  private function updateFields():void {
    if (readOnlyValueExpression.getValue()) {
      getFieldsComboBox().disable();
      getDirectionComboBox().disable();
    }

    if (!readOnlyValueExpression.getValue()) {
      getFieldsComboBox().enable();
      getDirectionComboBox().enable();
    }
  }


  /**
   * It may look like this method produces the same results regardless of the selected field, but it will trigger UI
   * update of the direction combo box, which will contain different texts for different fields selected.
   */
  private function updateDirections():void {
    var directions:Array = [];
    directions.push(beanFactory.createLocalBean({'name':'asc'}));
    directions.push(beanFactory.createLocalBean({'name':'desc'}));

    if (readOnlyValueExpression.getValue()) {
      getDirectionComboBox().disable();
      getFieldsComboBox().disable();
    }

    getLocalModel().set(DIRECTIONS, directions);

    //if none of the fields are selected, directions should be disabled
    if (!readOnlyValueExpression.getValue()) {
      getDirectionComboBox().enable();
      getFieldsComboBox().enable();
    }
  }

  /**
   * Updates the sorting part of the query in the remote bean and a serialized value expression used in the advanced
   * mode.
   *
   * @param direction sorting direction value
   */
  private function updateRemoteBeanAndSerializedExpression(direction:String):void {
    var field:String = getLocalModel().get(SELECTED_FIELD);
    var oldSerializedValue:String = sortingValueExpression.getValue();
    var newSerializedValue:String = serialize(field, direction);

    if (oldSerializedValue != newSerializedValue) {
      if (newSerializedValue) {
        sortingValueExpression.setValue(newSerializedValue);
        StudioUtil.reloadPreview();
      } else if (!field && !direction) {
        queryValueExpression.loadValue(function (fq:StructRemoteBeanImpl):void {
          fq.getType().removeProperty(propertyName);
        });
      }
    }
  }

  private function updateLocalModel(source:ValueExpression):void {
    var value:* = source.getValue();
    var fieldAndDirection:Object;
    var oldSerializedValue:String = serialize(
      getLocalModel().get(SELECTED_FIELD),
      getLocalModel().get(SELECTED_DIRECTION)
    );

    if (value && oldSerializedValue != value) {
      fieldAndDirection = deserialize(value);
      getLocalModel().set(SELECTED_FIELD, fieldAndDirection.field);
      getLocalModel().set(SELECTED_DIRECTION, fieldAndDirection.direction);
    }

    if(!value) {
      getLocalModel().removePropertyChangeListener(SELECTED_FIELD, onSelectedFieldChange);
      getLocalModel().removePropertyChangeListener(SELECTED_DIRECTION, onSelectedDirectionChange);
      getLocalModel().set(SELECTED_DIRECTION, null);
      getLocalModel().set(SELECTED_FIELD, null);
      getLocalModel().set(DIRECTIONS,[]);
      getLocalModel().addPropertyChangeListener(SELECTED_FIELD, onSelectedFieldChange);
      getLocalModel().addPropertyChangeListener(SELECTED_DIRECTION, onSelectedDirectionChange);
      getFieldsComboBox().setValue(null);
      getDirectionComboBox().setValue(null);
    }
  }

  /**
   * Translate to the text representation stored in the query remote bean. Since that's already a SOLR syntax, it is
   * also used to be edited in the advanced mode.
   *
   * @param field selected sorting field
   * @param direction selected sorting direction
   * @return text representation
   */
  private static function serialize(field:String, direction:String):String {
    var sortingExpression :String = undefined;
    if (field && direction) {
      sortingExpression = field + ' ' + direction;
    }
    return sortingExpression;
  }

  private static function deserialize(value:String):Object {
    var split:Array;
    if (value) {
      split = value.split(' ');
    } else {
      split = [];
    }
    if (split.length > 1) { // if split fails it will contain one item
      return {
        field:split[0],
        direction:split[1]
      };
    } else {
      return {
        field:undefined,
        direction:undefined
      };
    }

  }

  /**
   * Don't make this methods static. Although IDEA suggest it, this will result in bean errors.
   * Hellwhaddaiknow...
   */
  protected function getFieldText(field:String):String {
    return QueryEditorSettings_properties.INSTANCE['sort_direction_' + field] || field;
  }

  /**
   * Don't make this methods static. Although IDEA suggest it, this will result in bean errors.
   */
  protected function getDirectionText(direction:String):String {
    return QueryEditorSettings_properties.INSTANCE['sort_direction_' + direction] || direction;
  }

  /*
   * Initializes template properties object used to create local bean.
   */
  private function createProperties():Object {
    var props:Object = {};

    props[FIELDS] = [];
    props[SELECTED_FIELD] = "";
    props[DIRECTIONS] = [];
    props[SELECTED_DIRECTION] = "";

    var sortingColumns:String = QueryEditorSettings_properties.INSTANCE.sort_directions;
    var columnNames:Array = sortingColumns.split(",");
    for(var i:int = 0; i<columnNames.length; i++) {
      var colName:String = columnNames[i];
      var locName:String = QueryEditorSettings_properties.INSTANCE['sort_direction_'+colName];
      props[FIELDS].push({name:colName,localizedName:locName});
    }
    return props;
  }

  protected function getLocalModel():Bean {
    if (!localModel) {
      localModel = beanFactory.createLocalBean(createProperties());
    }
    return localModel;
  }

  private function getFieldsComboBox():ComboBox {
    if (!fieldsComboBox) {
      fieldsComboBox = ComboBox(find('itemId', 'sortingFields')[0]);
    }
    return fieldsComboBox;
  }

  private function getDirectionComboBox():ComboBox {
    if (!directionComboBox) {
      directionComboBox = ComboBox(find('itemId', 'sortingDirections')[0]);
    }
    return directionComboBox;
  }


  private function getContentQueryEditor():ContentQueryEditor {
    if (!dcqe) {
      dcqe = this.findParentByType(ContentQueryEditor) as ContentQueryEditor;
    }
    return dcqe;
  }

  private function getCommonModel():Bean {
    if (!commonModel) {
      commonModel = getContentQueryEditor().getCommonModel();
    }
    return commonModel;
  }

  public override function destroy():void {
    var appliedConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;

    readOnlyValueExpression.removeChangeListener(enableOrDisable);
    sortingValueExpression.removeChangeListener(updateLocalModel);
    commonModel && commonModel.removePropertyChangeListener(appliedConditionsKey, onAppliedChange);
    getLocalModel().removePropertyChangeListener(SELECTED_FIELD, onSelectedFieldChange);
    getLocalModel().removePropertyChangeListener(SELECTED_DIRECTION, onSelectedDirectionChange);
    super.destroy();
  }

}
}
