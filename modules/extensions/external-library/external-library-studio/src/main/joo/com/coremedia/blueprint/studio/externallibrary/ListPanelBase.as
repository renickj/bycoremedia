package com.coremedia.blueprint.studio.externallibrary {

import com.coremedia.blueprint.studio.ExternalLibraryStudioPlugin_properties;
import com.coremedia.blueprint.studio.config.externallibrary.listPanel;
import com.coremedia.blueprint.studio.util.AjaxUtil;
import com.coremedia.blueprint.studio.util.DateUtil;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EventUtil;

import ext.config.arraystore;
import ext.data.ArrayStore;
import ext.data.Record;
import ext.grid.GridPanel;
import ext.grid.RowSelectionModel;

/**
 * Displays a list of available videos from the externallibrary platform.
 * Filter will be applied here when set.
 */
public class ListPanelBase extends GridPanel {
  private static const READ_MARKER:Array = [];

  private var filterValueExpression:ValueExpression;
  private var dataSourceValueExpression:ValueExpression;
  private var selectedValueExpression:ValueExpression;

  private var listStore:ArrayStore;
  private var dataModel:Array;

  public function ListPanelBase(config:listPanel) {
    this.dataSourceValueExpression = config.dataSourceValueExpression;
    this.filterValueExpression = config.filterValueExpression;
    this.selectedValueExpression = config.selectedValueExpression;
    this.dataSourceValueExpression.addChangeListener(dataSourceChanged);
    this.filterValueExpression.addChangeListener(filterChanged);

    super(config);

    addListener('afterlayout', addListeners);
  }

  /**
   * Common listeners that are registered after layout.
   */
  private function addListeners():void {
    removeListener('afterlayout', addListeners);
    (getSelectionModel() as RowSelectionModel).addListener('rowselect', onSelect);
  }

  /**
   * Fired when a new data source has been selected.
   * The existing list will be resetted and the list will
   * be reloaded using the new data source, defined in the
   * record passed in the given value expression.
   * @param ve The data source value expression, contains the active external data selection.
   */
  private function dataSourceChanged(ve:ValueExpression):void {
    var record:Record = ve.getValue();
    if (record) {
      var index:int = record.data.index;
      reload(index, null);
    }
    else {
      reload(null, null);
    }
  }

  /**
   * Triggerd when the user has defined a new search.
   * @param ve
   */
  private function filterChanged(ve:ValueExpression):void {
    var record:Record = dataSourceValueExpression.getValue();
    var filter:String = ve.getValue();
    if (record) {
      var index:int = record.data.index;
      reload(index, filter);
    }
  }

  /**
   * The event handler for the list, applies
   * the selected record to the selection value expression.
   */
  private function onSelect():void {
    var record:Record = (getSelectionModel() as RowSelectionModel).getSelected();
    READ_MARKER.push(record.data.id);
    record.data.index = listStore.indexOf(record);
    if ((listStore.getCount() - 1) === record.data.index) {
      record.data.index = -1;
    }
    record.commit(false);
    selectedValueExpression.setValue(record);
  }

  /**
   * Executes the rest call that requests the active entry list for the given data source.
   * @param index index used by the REST service to read the entries from.
   * @param filter The filter string or null if not set.
   */
  protected function reload(index:int, filter:String):void {
    getView()['emptyText'] = ExternalLibraryStudioPlugin_properties.INSTANCE.ExternalLibraryWindow_list_loading;
      dataLoaded(null);
      var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod("externallibrary/items", 'GET');
      remoteServiceMethod.request(makeRequestParameters(index, editorContext.getSitesService().getPreferredSiteId(), filter), dataLoaded, AjaxUtil.onErrorMethodResponse);
  }

  /**
   * Builds the request parameters for the data list.
   * @param index The index to use.
   * @param preferredSite The site to make the lookup for.
   * @param filter The filter string to filter the data list result.
   * @return
   */
  private static function makeRequestParameters(index:int, preferredSite:String, filter:String):Object {
    return {
      index:index,
      preferredSite:preferredSite,
      filter:filter
    };
  }

  /**
   * The success response handler of the feed entry request.
   * We use an array store here to we push each json response
   * to the array that represents our data store.
   * @param response
   */
  private function dataLoaded(response:RemoteServiceMethodResponse):void {
    var records:Array = [];

    if (response) {
      records = response.getResponseJSON().items;
    }
    if (response && records.length === 0) {
      getView()['emptyText'] = ExternalLibraryStudioPlugin_properties.INSTANCE.ExternalLibraryWindow_list_empty;
    }
    if (response && response.getResponseJSON().errorMessage) {
      var msg:String = ExternalLibraryStudioPlugin_properties.INSTANCE.ExternalLibraryWindow_list_error + ' ' + response.getResponseJSON().errorMessage;
      getView()['emptyText'] = msg;
    }

    var dataSourceRecord:Record = dataSourceValueExpression.getValue();
    dataModel = [];
    records.forEach(function (record:*):void {
      var dataRecord:Array = [];

      dataRecord.push(record.adminTags);
      dataRecord.push(record.categories);
      dataRecord.push(record.description);
      dataRecord.push(record.downloadUrl);
      dataRecord.push(record.id);
      dataRecord.push(record.name);
      dataRecord.push(record.referenceId);
      dataRecord.push(record.searchText);
      dataRecord.push(record.status);
      dataRecord.push(record.tags);
      dataRecord.push(record.thumbnailUri);
      dataRecord.push(record.type);
      dataRecord.push(record.version);
      dataRecord.push(record.license);
      dataRecord.push(record.userId);
      dataRecord.push(record.groupId);
      dataRecord.push(record.duration);
      dataRecord.push(DateUtil.formatDateTime(record.createdAt));
      dataRecord.push(record.publicationDate);
      dataRecord.push(record.startDate);
      dataRecord.push(record.updatedAt);
      dataRecord.push(record.endDate);
      dataRecord.push(record.votes);
      dataRecord.push(record.totalRank);
      dataRecord.push(record.moderationCount);
      dataRecord.push(record.moderationStatus);
      dataRecord.push(dataSourceRecord.data.name);
      dataRecord.push(record.rawData);
      dataRecord.push(record.rawDataList);

      dataModel.push(dataRecord);
    });
    listStore.loadData(dataModel);

    //pre-select first item after loading.
    EventUtil.invokeLater(function ():void {
      if (records.length > 0) {
        (getSelectionModel() as RowSelectionModel).selectFirstRow();
      }
    });
  }

  /**
   * Creates the store for the grid panel
   * @return
   */
  protected function getExternalDataStore():ArrayStore {
    if (!listStore) {
      // create the actual data store...
      listStore = new ArrayStore(arraystore({
        fields:[
          {name:'adminTags'},
          {name:'categories'},
          {name:'adminTags'},
          {name:'categories'},
          {name:'description'},
          {name:'downloadUrl'},
          {name:'id'},
          {name:'name'},
          {name:'referenceId'},
          {name:'searchText'},
          {name:'status'},
          {name:'tags'},
          {name:'thumbnailUri'},
          {name:'type'},
          {name:'version'},
          {name:'license'},
          {name:'userId'},
          {name:'groupId'},
          {name:'duration'},
          {name:'createdAt'},
          {name:'publicationDate'},
          {name:'startDate'},
          {name:'updatedAt'},
          {name:'endDate'},
          {name:'votes'},
          {name:'totalRank'},
          {name:'moderationCount'},
          {name:'moderationStatus'},
          {name:'dataUrl'},
          {name:'rawData'},
          {name:'rawDataList'}
        ]
      }));
      dataModel = [];
      //...and load the data
      listStore.loadData(dataModel);
    }
    return listStore;
  }

  /**
   * Rendered for the title column, remove the bold format of
   * an entry is this was already selected.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function renderTitle(value:*, metaData:*, record:BeanRecord):String {
    if (isUnread(record.data.id) && dataSourceValueExpression.getValue().data.markAsRead) {
      return '<b>' + value + '<b/>';
    }

    return value;
  }

  /**
   * Returns true if the given id was already selected by the user.
   * @param id
   * @return
   */
  private function isUnread(id:*):Boolean {
    for (var i:int = 0; i < READ_MARKER.length; i++) {
      if (READ_MARKER[i] === id) {
        return false;
      }
    }
    return true;
  }
}
}
