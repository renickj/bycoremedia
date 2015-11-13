package com.coremedia.ecommerce.studio.components.link {
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.config.catalogLinkPropertyFieldDropArea;
import com.coremedia.ecommerce.studio.dragdrop.CatalogLinkDropTarget;
import com.coremedia.ui.components.IconLabel;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Element;
import ext.QuickTip;
import ext.QuickTips;

/**
 * A container that reacts to content drop events by adding the content object to the link list property field.
 */
public class CatalogLinkPropertyFieldDropAreaBase extends IconLabel {

  private var catalogLinkDropTarget:CatalogLinkDropTarget;
  private var multiple:Boolean;
  private var createStructFunction:Function;
  private var openLinkSources:Function;
  private var readOnlyExpression:ValueExpression;
  private var propertyExpression:ValueExpression;

  /**
   * Create the container.
   * @param config the config object
   */
  public function CatalogLinkPropertyFieldDropAreaBase(config:catalogLinkPropertyFieldDropArea) {
    super(config);
    multiple = config.multiple;
    createStructFunction = config.createStructFunction;
    openLinkSources = config.openLinkSources;
  }

  internal static const COMPUTED_IMG_MARGIN:Number = 14;
  protected static const DROP_TARGET_TOOLTIP_TEXT:String = ECommerceStudioPlugin_properties.INSTANCE.Products_Link_empty_text;

  public native function get bindTo():ValueExpression;
  public native function get propertyName():String;
  public native function get linkType():String;
  public native function get maxCardinality():int;
  public native function get droppingRowValueExpression():ValueExpression;
  public native function get forceReadOnlyValueExpression():ValueExpression;

  override protected function onRender(ct:Element, position:Element):void {
    super.onRender(ct, position);

    catalogLinkDropTarget = new CatalogLinkDropTarget(this, bindTo.extendBy('properties').extendBy(propertyName), linkType,
            forceReadOnlyValueExpression, multiple, createStructFunction);

    trace("droppingRowValueExpression="+droppingRowValueExpression.getValue());

    getEl().setStyle("cursor", "pointer");
  }

  override protected function afterRender():void {
    super.afterRender();
    mon(getEl(), 'click', openCollectionView);
    mon(getEl(), 'mouseover', showDropTargetTip);
  }

  override protected function onDisable():void {
    super.onDisable();
    getEl().setStyle("cursor", "default");
  }

  override protected function onEnable():void {
    super.onEnable();
    getEl().setStyle("cursor", "pointer");
  }

  /*
   * Create drop target for this component.
   */
  private function openCollectionView():void {
    if(!disabled) {
      openLinkSources();
    }
  }

  /*
   * Create quicktip is necessary.
   */
  private function getDropContainerEl():Element {
    var currentElement:* = this.getEl();
    return currentElement;
  }

  public function showDropTargetTip():void {
    var currentQuickTip:QuickTip = QuickTips.getQuickTip() as QuickTip;

    var currentLabel:Element = getDropContainerEl().child('.icon-label-text');
    var currentImg:Element = getDropContainerEl().first('img');

    var currentComputedSpace:Number = (currentImg.getComputedWidth() + getDropContainerEl().getPadding('lr')) + COMPUTED_IMG_MARGIN;
    var currentWidth:Number = (getDropContainerEl().getComputedWidth()) - currentComputedSpace;

    var currentLabelWidth:Number = currentLabel.getComputedWidth();

    if (currentLabelWidth > currentWidth){
      currentQuickTip.register({
        text: DROP_TARGET_TOOLTIP_TEXT,
        target: currentLabel
      });
      currentQuickTip.show();
    } else
      currentQuickTip.unregister(currentLabel);
    currentQuickTip.hide();
  }

  override protected function beforeDestroy():void {
    catalogLinkDropTarget && catalogLinkDropTarget.unreg();
    super.beforeDestroy();
  }

  internal function getReadOnlyExpression(config:*):ValueExpression {
    if (!readOnlyExpression) {
      readOnlyExpression = ValueExpressionFactory.createFromFunction(CatalogLinkFieldBase.getReadOnlyFunction(config));
    }
    return readOnlyExpression;
  }

  internal function getPropertyExpression(config:catalogLinkPropertyFieldDropArea):ValueExpression {
    if (!propertyExpression) {
      if (config.bindTo) {
        propertyExpression = config.bindTo.extendBy('properties').extendBy(config.propertyName);
      } else {
        propertyExpression = ValueExpressionFactory.create(config.propertyName, config.model);
      }
    }
    return propertyExpression;
  }

}
}
