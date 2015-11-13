package com.coremedia.blueprint.studio.externallibrary {

import com.coremedia.blueprint.studio.config.externallibrary.filterPanel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;

import ext.Container;
import ext.Ext;
import ext.IEventObject;
import ext.config.jsonstore;
import ext.data.JsonStore;
import ext.data.Record;
import ext.data.Store;
import ext.form.ComboBox;
import ext.form.Field;
import ext.form.TextField;

/**
 * Displays the filter section of the external content, displaying
 * a content selection combo and a search filter.
 */
public class FilterPanelBase extends Container {

  private var externalDataStore:*;
  public var dataSourceValueExpression:ValueExpression;
  public var filterValueExpression:ValueExpression;

  private var cmdStack:CommandStack;

  private var recordType:Class;

  private var dataIndex:Number;

  public function FilterPanelBase(config:filterPanel) {
    super(config);
    this.filterValueExpression = config.filterValueExpression;
    this.dataSourceValueExpression = config.dataSourceValueExpression;
    this.dataIndex = config.dataIndex || 0;

    this.dataSourceValueExpression.addChangeListener(dataSourceChanged);
    cmdStack = new CommandStack(this);

    addListener('afterlayout', initFilter);
  }

  private function dataSourceChanged():void {
    var textfield:TextField = Ext.getCmp('externalLibrarySearchFilter') as TextField;
    textfield.setDisabled(!this.dataSourceValueExpression.getValue());
  }

  /**
   * After layout 'cos buttons must have been rendered.
   */
  private function initFilter():void {
    removeListener('afterlayout', initFilter);
    Ext.getCmp('externalLibrarySearchFilter').setDisabled(!this.dataSourceValueExpression.getValue());
    cmdStack.reset();

    loadChoices(function():void {
      //sets the initial value after loading
      if (!dataSourceValueExpression.getValue()) {
        var combo:ComboBox = getComboBox() as ComboBox;
        if (combo.getStore().getCount() > 0) {
          var selection:Record = combo.getStore().getAt(dataIndex);
          dataSourceValueExpression.setValue(selection);
          combo.setValue(selection.data.name);
        }
      }
    });
  }

  /**
   * Executes the actual loading of the data, record creation and filling of the store.
   */
  private function loadChoices(callback:Function):void {
    var combo:ComboBox = getComboBox();
    combo.getStore().removeAll(true);
    var remoteBean:RemoteBean = beanFactory.getRemoteBean("externallibrary/sources?" + Ext.urlEncode({preferredSite: editorContext.getSitesService().getPreferredSiteId()}));
    remoteBean.invalidate(function ():void {
      var items:Array = remoteBean.get("items");
      for(var i:int = 0; i<items.length; i++) {
        combo.getStore().add(new recordType({}, items[i]));
      }
      callback.call(null);
    });
  }

  private function getComboBox():ComboBox {
    var combo:ComboBox = Ext.getCmp('externalDataCombo') as ComboBox;
    return combo;
  }

  protected function forward():void {
    cmdStack.execute(cmdStack.getActiveIndex() + 1);
  }

  protected function back():void {
    cmdStack.execute(cmdStack.getActiveIndex() - 1);
  }

  /**
   * Returns the command stack instance for the filter.
   * @return
   */
  public function getCommandStack():CommandStack {
    return cmdStack;
  }

  /**
   * Registers the listeners for the filter components, like the data source combo.
   */
  override protected function initComponent():void {
    super.initComponent();
    var combo:ComboBox = getComboBox() as ComboBox;
    combo.addListener('select', dataSourceSelectionChange);
  }

  protected function getComboStore():Store {
    const fields:Array = [
      {name:'index', mapping:'index'},
      {name:'name', mapping:'name'},
      {name:'dataUrl', mapping:'dataUrl'},
      {name:'providerClass', mapping:'providerClass'},
      {name:'previewType', mapping:'previewType'},
      {name:'contentType', mapping:'contentType'},
      {name:'markAsRead', mapping:'markAsRead'}
    ];

    recordType = BeanRecord.create(fields, false);
    const storeConfig:Object = { fields:recordType };

    return new JsonStore(jsonstore(storeConfig));
  }

  /**
   * Fired when another data source has been selected
   */
  private function dataSourceSelectionChange(combo:ComboBox, record:Record, index:Number):void {
    dataSourceValueExpression.setValue(record);
    filterValueExpression.setValue('');
    this.cmdStack.addCommand(record, '');
  }

  /**
   * Executed when the user presses the enter key of the search area.
   * @param field The field the event was triggered from.
   * @param e The key event.
   */
  protected function applyFilterInput(field:Field, e:IEventObject):void {
    if (e.getKey() === e.ENTER) {
      applyFilter();
      e.stopEvent();
    }
  }

  /**
   * Applies the filter value and creates the corresponding command for the stack.
   */
  public function applyFilter():void {
    var filterField:TextField = Ext.getCmp('externalLibrarySearchFilter') as TextField;
    var combo:ComboBox = getComboBox() as ComboBox;

    var comboRecord:Record = null;
    for (var i:int = 0; i < combo.getStore().getCount(); i++) {
      if (combo.getStore().getAt(i).data.index === combo.getValue()) {
        comboRecord = combo.getStore().getAt(i);
        break;
      }
    }

    var filterString:String = filterField.getValue();
    getCommandStack().addCommand(comboRecord, filterString);
    filterValueExpression.setValue(filterString);
  }
}
}
