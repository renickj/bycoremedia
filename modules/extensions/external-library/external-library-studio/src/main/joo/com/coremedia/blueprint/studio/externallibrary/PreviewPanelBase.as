package com.coremedia.blueprint.studio.externallibrary {

import com.coremedia.blueprint.base.components.quickcreate.processing.ProcessingData;
import com.coremedia.blueprint.studio.ExternalLibraryProviderSettings_properties;
import com.coremedia.blueprint.studio.ExternalLibraryStudioPlugin_properties;
import com.coremedia.blueprint.studio.config.externallibrary.previewPanel;
import com.coremedia.blueprint.studio.model.ExternalLibraryDataItem;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;
import com.coremedia.ui.logging.Logger;

import ext.Button;
import ext.Panel;
import ext.data.Record;
import ext.form.Label;
import ext.util.StringUtil;

/**
 * Displays a list of available videos from the external content platform.
 * Filter will be applied here when set.
 */
public class PreviewPanelBase extends Panel {
  private const PREVIEW_HTML:String = 'html';
  private const PREVIEW_VIDEO:String = 'video';

  private var filterValueExpression:ValueExpression;
  private var dataSourceValueExpression:ValueExpression;
  private var selectedValueExpression:ValueExpression;
  private var defaultNameExpression:ValueExpression;
  private var contentTypeExpression:ValueExpression;

  private var previewType:String;

  public function PreviewPanelBase(config:previewPanel) {
    super(config);

    this.filterValueExpression = config.filterValueExpression;
    this.dataSourceValueExpression = config.dataSourceValueExpression;
    this.selectedValueExpression = config.selectedValueExpression;

    this.selectedValueExpression.addChangeListener(selectionChanged);
    this.dataSourceValueExpression.addChangeListener(dataSourceChanged);
    this.filterValueExpression.addChangeListener(resetPreview);

    addListener('afterlayout', initPreview);
  }

  /**
   * The value expression contains the content type that will be created for
   * the current selection.
   * @return
   */
  protected function getContentTypeExpression():ValueExpression {
    if (!contentTypeExpression) {
      contentTypeExpression = ValueExpressionFactory.create('type', beanFactory.createLocalBean());
    }
    return contentTypeExpression;
  }

  /**
   * The value expression contains formatted default name of the selected content.
   * @return
   */
  protected function getDefaultNameExpression():ValueExpression {
    if (!defaultNameExpression) {
      defaultNameExpression = ValueExpressionFactory.create('name', beanFactory.createLocalBean());
    }
    return defaultNameExpression;
  }


  private function initPreview():void {
    this.removeListener('afterlayout', initPreview);
    this.resetPreview();
  }

  /**
   * Fired when the data source has been changed. This means that
   * the preview type may have also changed.
   */
  private function dataSourceChanged(ve:ValueExpression):void {
    var dataSource:Record = ve.getValue();
    if (dataSource) {
      previewType = dataSource.data.previewType;
      if (!previewType) {
        throw new Error('Invalid preview definition for index ' + dataSource.data.index + ': ' + previewType);
      }
    }

    resetPreview();
  }


  /**
   * Fired when an item of the list view has been selected.
   * The selected item will be displayed in the configured preview type.
   */
  private function selectionChanged(ve:ValueExpression):void {
    find('itemId', 'externalDataPreview')[0].el.dom.parentNode.scrollTop = 0;
    find('itemId', 'externalDataPreview')[0].el.dom.parentNode.scrollLeft = 0;

    var selection:Record = ve.getValue();
    if (selection) {
      if (previewType === PREVIEW_HTML) {
        showHTML(selection);
      }
      else if (previewType === PREVIEW_VIDEO) {
        showVideo(selection);
      }

      //set the default name and the content type to create
      defaultNameExpression.setValue(formatDefaultName(selection));
      var contentType:String = dataSourceValueExpression.getValue().data.contentType;
      contentTypeExpression.setValue(contentType);
    }
    else {
      resetPreview();
    }
  }

  /**
   * Formats the default name that should be displayed
   * in the new content dialog.
   * @param record The selected record.
   * @return The formatted name.
   */
  private function formatDefaultName(record:Record):String {
    var name:String = record.data.name;
    if (name) {
      var pattern:RegExp = /\//g;
      name = name.replace(pattern, '-');
      if (name.length > 80) {
        name = name.substring(0, 80);
        name = name.substring(0, name.lastIndexOf(' '));
      }
      return name;
    }
    return null;
  }

  /**
   * Resets the preview to an label that shows
   * that no data is selected.
   */
  private function resetPreview():void {
    getPreviewActionButton().setVisible(false);
    showPreview('', '', '', '<i>' + ExternalLibraryStudioPlugin_properties.INSTANCE.ExternalLibraryWindow_preview_no_selection + '</i>', '', '', null);
  }

  /**
   * Displays the video data item.
   */
  private function showVideo(record:Record):void {
    var template:String = ExternalLibraryProviderSettings_properties.INSTANCE.preview_video_template;
    var url:String = record.data.downloadUrl;
    var html:String = StringUtil.format(template, url);

    var title:String = record.data.name;
    var source:String = dataSourceValueExpression.getValue().data.name;
    var category:String = '';
    var data:String = html;
    var date:String = record.data.createdAt;
    var videoUrl:String = record.data.downloadUrl;
    getPreviewActionButton().setVisible(true);
    showPreview(title, source, category, data, date, videoUrl, null);
  }

  /**
   * Returns the button that invoked additional actions for the preview like the Kaltura console
   * @return
   */
  private function getPreviewActionButton():Button {
    return find('itemId','previewAction')[0] as Button;
  }

  /**
   * The action handler for the
   */
  protected function previewActionHandler():void {
    var urlToOpen:String = "http://kmc.kaltura.com/index.php/kmc#content|manage";
    var wname:String = ExternalLibraryStudioPlugin_properties.INSTANCE.VideoAdminConsole_title;
    var wfeatures:String = "menubar=yes,resizable=yes,scrollbars=yes,status=yes,location=yes";
    window.open(urlToOpen, wname, wfeatures);
  }

  protected function getPreviewActionLabel():String {
    return ExternalLibraryStudioPlugin_properties.INSTANCE.VideoAdminConsole_title;
  }

  /**
   * Displays the video data item.
   */
  private function showHTML(record:Record):void {
    var title:String = record.data.name;
    var source:String = dataSourceValueExpression.getValue().data.name;
    var category:String = '';
    var data:String = record.data.description;
    if (record.data.rawData){
      data = data + "<br/>" + record.data.rawData;
    }
    var date:String = record.data.createdAt;
    var url:String = record.data.downloadUrl;
    var mediaUrl:String = resolveMediaHTML(record.data.rawDataList);
    showPreview(title, source, category, data, date, url, mediaUrl);
  }

  /**
   * Return the HTML of a media item, if the RSS item contains a e.g. video
   * @param rawDataList
   * @return
   */
  private function resolveMediaHTML(rawDataList:Array):String {
    if(rawDataList) {
      for(var i:int = 0; i<rawDataList.length; i++) {
        var dataItem:ExternalLibraryDataItem = new ExternalLibraryDataItem(rawDataList[i]);
        if(dataItem.getType().indexOf('video') != -1) {
          var videoHtml:String = ExternalLibraryProviderSettings_properties.INSTANCE.preview_video_item_template;
          videoHtml = StringUtil.format(videoHtml, dataItem.getValue(), dataItem.getType());
          return videoHtml;
        }
        else if (dataItem.getType().indexOf('image') != -1) {
          var imageHtml:String = ExternalLibraryProviderSettings_properties.INSTANCE.preview_picture_item_template;
          imageHtml = StringUtil.format(imageHtml, dataItem.getValue());
          return imageHtml;

        }
      }
    }
    return null;
  }

  /**
   * Common preview method, used by the different preview types.
   * @param title
   * @param source
   * @param category
   * @param data
   * @param date
   * @param url
   */
  private function showPreview(title:String, source:String, category:String, data:String, date:String, url:String, mediaUrl:String):void {
    var sourceLabel:Label = getLabel('source');
    var titleLabel:Label = getLabel('title');
    var categoryLabel:Label = getLabel('category');
    var dataLabel:Label = getLabel('data');
    var dateLabel:Label = getLabel('date');
    var urlLabel:Label = getLabel('url');
    var mediaData:Label = getLabel('mediaData');

    if (source) {
      source = ExternalLibraryStudioPlugin_properties.INSTANCE.Preview_data_source + ' ' + source;
    }

    if (url) {
      url = ExternalLibraryStudioPlugin_properties.INSTANCE.Preview_data_url + ' ' + url;
    }

    if(mediaUrl) {
      mediaData.setText(mediaUrl, false);
    }
    else{
      mediaData.setText('', false);
    }
    sourceLabel.setText(source, true);
    titleLabel.setText(title, true);
    categoryLabel.setText(category, true);
    dataLabel.setText(data, false);
    dateLabel.setText(date, true);
    urlLabel.setText(url, true);
  }

  private function getLabel(id:String):Label {
    return find('itemId', id)[0] as Label;
  }

  /**
   * Callback handler of the new content dialog. The id of the content is post to
   * the post processing method of the third party REST controller, so that the specific provider
   * can put additional values into the content.
   * @param content The new content that will be filled with 3rd party data afterwards.
   */
  protected function postProcessExternalContent(content:Content, data:ProcessingData, callback:Function):void {
    var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod("externallibrary/postProcess", 'POST');
    var params:* = makeRequestParameters(content);
    remoteServiceMethod.request(params, function(response:RemoteServiceMethodResponse):void {
      var content:Content = response.getResponseJSON().createdContent;
      if(content) {
        data.setContent(content);
      }
      var additionalContent:Array = response.getResponseJSON().additionalContent;
      if(additionalContent) {
        for(var i:int = 0; i<additionalContent.length; i++) {
          data.addAdditionalContent(additionalContent[i]);
        }
      }
      callback.call(null);
    }, function(response:RemoteServiceMethodResponse):void {
      Logger.error('Request failed: ' + response.getError().errorName + '/' + response.getError().errorCode);
      callback.call(null);
    });
  }

  /**
   * Creates a JSON object with the REST parameters to pass to
   * the creation service.
   * @return The JSON object with the REST parameters.
   */
  private function makeRequestParameters(content:Content):Object {
    var url:String = 'externallibrary/postProcess/?';
    var dataSourceRecord:Record = dataSourceValueExpression.getValue();
    var itemRecord:Record = selectedValueExpression.getValue();
    var providerId:int = dataSourceRecord.data.index;
    var preferredSiteId:String = editorContext.getSitesService().getPreferredSiteId();
    var itemId:String = itemRecord.data.id;
    var dataUrl:String = dataSourceRecord.data.dataUrl;
    var providerClass:String = dataSourceRecord.data.providerClass;

    return {
      dataUrl: dataUrl,
      id: itemId,
      capId: content.getId(),
      providerId: providerId,
      preferredSite: preferredSiteId
    }
  }

  protected function disableCreateButton(selection:*):Boolean {
    return !selection;
  }
}
}
