package com.coremedia.blueprint.studio.property {

import com.coremedia.blueprint.studio.Blueprint_properties;
import com.coremedia.blueprint.studio.config.components.imageContextLinkListDialog;
import com.coremedia.blueprint.studio.config.components.imageContextLinkListPropertyField;
import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.common.descriptors.StringPropertyDescriptor;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.premular.fields.LinkListPropertyField;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EncodingUtil;
import com.coremedia.ui.util.EventUtil;

import ext.Element;
import ext.Ext;
import ext.data.Store;
import ext.grid.GridPanel;
import ext.grid.RowSelectionModel;

import js.Event;

/**
 * Base class of the content link list.
 * Handles the access to the struct property that stores the context values.
 */
public class ImageContextLinkListPropertyFieldBase extends LinkListPropertyField {
  private static const LABEL_CSS:String = 'context-link-list-label';
  private static const DEFAULT_LABEL_CSS:String = 'context-link-list-default-label';
  private static const CONTEXT_STRUCT_PROPERTY:String = 'contextvalues';

  private var structPropertyExpression:ValueExpression;
  private var propertyValueExpression:ValueExpression;
  private var bindTo:ValueExpression;
  private var structPropertyName:String;
  private var emptyText:String;
  private var quote:Boolean;
  private var labelCls:String;

  public function ImageContextLinkListPropertyFieldBase(config:imageContextLinkListPropertyField) {
    super(config);
    emptyText = config.emptyText;
    quote = config.quote;
    labelCls = config.labelCls || LABEL_CSS;
    bindTo = config.bindTo;
    structPropertyName = config.structPropertyName;
    structPropertyExpression = config.bindTo.extendBy('properties.' + config.structPropertyName + '.' + CONTEXT_STRUCT_PROPERTY);

    //listen on property change events to trigger struct cleanups.
    propertyValueExpression = config.bindTo.extendBy('properties.' + config.propertyName);
    propertyValueExpression.addChangeListener(cleanUpStruct);

    (bindTo.getValue() as Content).addValueChangeListener(valueChanged);
  }

  private function valueChanged(event:PropertyChangeEvent):void {
    var p:String = event.property;
    if(p === "checkedIn" || p == "checkOut") {
      if(event.oldValue !== event.newValue) {
        reload();
      }
    }
  }

  /**
   * Force refresh on content state change.
   */
  private function reload():void {
    if(getGridPanel()) {
      var store:Store = getGridPanel().getStore();
      for(var i:int = 0;i <store.getCount(); i++) {
        store.getAt(i).data.html = undefined;
        store.getAt(i).commit(false);
      }
    }
  }

  /**
   * Handler for the label click.
   * We use the event's target to determine the content that has been clicked on.
   */
  private function labelClicked(event:Event):void {
    var labelContainer:Element = Ext.fly(event.target);
    var contentId:* = labelContainer.getAttribute("data-content-id");
    event.preventDefault();
    event.stopPropagation();
    var mayWrite:* = session.getConnection().getContentRepository().getAccessControl().mayWrite(bindTo.getValue());
    if(mayWrite) {
      applyContextValue(contentId);
    }
  }

  /**
   * Displays the context read from struct.
   */
  protected function contextColumnRenderer(value:*, metaData:*, record:BeanRecord):String {
    var id:Number = IdHelper.parseContentId(record.getBean());
    record.data.id = ''+id;
    var c:Content = record.getBean() as Content;
    if (!record.data.html) {
      EventUtil.invokeLater(function ():void {//not invoke later will result in JS error about className and also the reload will not be applied
        c.load(function ():void {
          ValueExpressionFactory.createFromFunction(function ():Boolean {
            var mayWrite:Boolean = session.getConnection().getContentRepository().getAccessControl().mayWrite(bindTo.getValue());
            if (mayWrite === undefined) {
              return undefined;
            }

            return mayWrite;
          }).loadValue(function (mayWrite:Boolean):void {
            record.data.html = formatNameColumnHTML(c, mayWrite);
            structPropertyExpression.loadValue(function (struct:Array):void {
              if (struct && struct.get('' + id)) {
                record.data.html = formatNameColumnHTML(c, mayWrite, struct.get('' + id));
              }
              record.commit(false);
            });
          });
        });

        record.commit(false);
      });
    }
    else {
      EventUtil.invokeLater(function():void {
        var componentId:String = getComponentId(bindTo.getValue(), c);
        var elem:Element = Ext.get(componentId);
        if(elem) {
          elem.removeAllListeners();
          elem.addListener('click', labelClicked);
        }
      });
    }

    return record.data.html;
  }

  /**
   * Creates the HTML that is rendered into the name column of the link list.
   * @param content The content to render
   * @param value The optional value that has been set for the list item.
   * @return The HTML that contains the link for opening the edit dialog.
   */
  private function formatNameColumnHTML(content:Content, mayWrite:Boolean, value:String = undefined):String {
    var componentId:String = getComponentId(bindTo.getValue(), content);
    var label:String = value || emptyText || Blueprint_properties.INSTANCE.ContextLinkList_link_label;
    label = EncodingUtil.encodeForHTML(label);
    var cls:String = DEFAULT_LABEL_CSS;
    var quoteString:String = '';
    if (value && quote) {
      quoteString = '"';
    }
    if (value) {
      cls = labelCls;
    }

    var idAttribute:String = ' id="' + componentId + '"';
    var style:String = '';
    if(!mayWrite) {
      style = ' style="cursor:default !important;" ';
    }
    var html:String = '<div>' + content.getName() + '</div><div ' + style +
            'data-content-id="' + IdHelper.parseContentId(content) + '" ' +
            'class="' + cls + '"' + idAttribute + '>' + quoteString + label + quoteString + '</div>';
    return html;
  }

  /**
   * Ensures that a global unique element id is generated
   * @param premularContent the content of the active premular
   * @param selectedItemContent the selected content item of the link list
   * @return a unique HTML id
   */
  private function getComponentId(premularContent:Content, selectedItemContent:Content):String {
    return IdHelper.parseContentId(premularContent) + "-" + propertyName + "-item-" + IdHelper.parseContentId(selectedItemContent);
  }

  /**
   * Method is invoked when the link of the link list is clicked.
   * A dialog opens where the user can input a new context depending value.
   */
  public function applyContextValue(id:String):void {
    var documentContent:Content = bindTo.getValue();
    var ve:ValueExpression = ValueExpressionFactory.create('properties.' + structPropertyName
    + '.' + CONTEXT_STRUCT_PROPERTY + '.' + id, documentContent);
    var grid:GridPanel = getGridPanel();
    var record:BeanRecord = (grid.getSelectionModel() as RowSelectionModel).getSelected() as BeanRecord;
    var dialog:ImageContextLinkListDialog = new ImageContextLinkListDialog(imageContextLinkListDialog({record:record,
      contextValueExpression:ve, callback:dialogCallback}));
    dialog.show();
  }

  /**
   * The callback invoked when the ok button is pressed.
   */
  private function dialogCallback(contextValueExpression:ValueExpression, record:BeanRecord, value:String):void {
    contextValueExpression.setValue(value);
    record.data.html = undefined;
    record.commit(false);
  }

  /**
   * Returns the wrapped grid panel.
   */
  private function getGridPanel():GridPanel {
    return find('itemId', 'linkGrid')[0];
  }

  /**
   * Ensures that no deprecated values are stored in the struct.
   * The method is triggered for every value change so that the struct is only
   * modified when the content is changed too.
   */
  private function cleanUpStruct():void {
    var c:Content = bindTo.getValue();
    c.invalidate(function():void {
      var store:Store = getGridPanel().getStore();
      var structExpression:ValueExpression = ValueExpressionFactory.create('properties.' + structPropertyName
      + '.' + CONTEXT_STRUCT_PROPERTY, bindTo.getValue());
      var struct:Struct = structExpression.getValue();
      if(struct) {
        var descrs:Array = struct.getType().getDescriptors();
        for(var i:int = 0; i<descrs.length; i++) {
          var descr:CapPropertyDescriptor = descrs[i];
          if(descr as StringPropertyDescriptor) {
            var name:String = descr.name;
            if(parseInt(name) != NaN) {
              if(!isInStore(store, parseInt(name))) {
                struct.getType().removeProperty(name);
              }
            }
          }
        }
      }
    });
  }

  /**
   * Checks if the record with the given name is in the store.
   * @param store The link list store to check.
   * @param name The name of the item to lookup.
   */
  private function isInStore(store:Store, id:Number):Boolean {
    for(var i:int = 0;i <store.getCount(); i++) {
      var c:Content = (store.getAt(i) as BeanRecord).getBean() as Content;
      if(IdHelper.parseContentId(c) === id) {
        return true;
      }
    }
    return false;
  }


  override protected function onDestroy():void {
    super.onDestroy();
    propertyValueExpression.removeChangeListener(cleanUpStruct);
    (bindTo.getValue() as Content).addValueChangeListener(valueChanged);

    var items:Array = propertyValueExpression.getValue();
    for (var i:int = 0; i < items.length; i++) {
      var c:Content = items[i];
      var componentId:String = getComponentId(bindTo.getValue(), c);
      var elem:Element = Ext.get(componentId);
      if (elem) {
        elem.removeAllListeners();
      }
    }
  }
}
}
