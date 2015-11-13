package com.coremedia.blueprint.personalization.editorplugin.taxonomy {
import com.coremedia.blueprint.personalization.editorplugin.config.taxonomyProperty;
import com.coremedia.blueprint.studio.config.taxonomy.openTaxonomyChooserAction;
import com.coremedia.blueprint.studio.config.taxonomy.taxonomySearchField;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.action.OpenTaxonomyChooserAction;
import com.coremedia.blueprint.studio.taxonomy.selection.TaxonomySearchField;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.personalization.ui.Personalization_properties;
import com.coremedia.personalization.ui.util.SelectionRuleHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Button;
import ext.Container;
import ext.Ext;
import ext.Spacer;
import ext.config.button;
import ext.config.container;
import ext.config.displayfield;
import ext.config.label;
import ext.config.spacer;
import ext.config.textfield;
import ext.form.DisplayField;
import ext.form.Label;
import ext.form.TextField;

import net.jangaroo.ext.create;

public class TaxonomyProperty extends Container {

  private var keywordField:TaxonomySearchField;
  private var valueField:TextField;
  public var taxonomyId:int;
  private var equalsLabel:Label;
  private var percentageLabel:Label;
  private var deleteButton:Button;

  // the internal prefix used for keywords. See class comment.
  private var propertyPrefix:String;

  //active selection
  private var taxonomySelectionExpr:ValueExpression = ValueExpressionFactory.create('taxonomy', beanFactory.createLocalBean({taxonomy: []}));

  public function TaxonomyProperty(config:taxonomyProperty) {
    super(container(Ext.apply(config, {
      /* obligatory configuration. overrides supplied properties */
      layout: "hbox",
      cls: "p13n-personaUI-dynamic-property-container",
      margins: "0 0",
      layoutConfig: {
        flex: 200
      }
    }, {
      /* default configuration. may be overridden by supplied properties */
      height: 25
    })));


    // store the keyword prefix
    propertyPrefix = config['propertyPrefix'];
    if (propertyPrefix == null) {
      throw new Error(Personalization_properties.INSTANCE.p13n_error_propertyPrefix);
    }

    if (propertyPrefix != null && propertyPrefix.charAt(propertyPrefix.length - 1) == '.') {
      // remove the '.' at the end of the prefix
      propertyPrefix = propertyPrefix.substring(0, propertyPrefix.length - 1);
    }

    add(new Spacer(spacer({width:5})));
    keywordField = new TaxonomySearchField(taxonomySearchField({searchResultExpression:taxonomySelectionExpr,
      itemId:'taxKeyword',
      taxonomyId:TaxonomyConditionUtil.getTaxonomyId4Chooser(propertyPrefix), width:350}));
    add(keywordField);

    var openChooserAction:OpenTaxonomyChooserAction = new OpenTaxonomyChooserAction(openTaxonomyChooserAction({
      taxonomyId:TaxonomyConditionUtil.getTaxonomyId4Chooser(propertyPrefix),
      singleSelection:true,
      tooltip:TaxonomyStudioPlugin_properties.INSTANCE.Taxonomy_action_tooltip,
      propertyValueExpression:taxonomySelectionExpr,
      cls:"taxonomies"
    }));
    var selectButton:* = create(button, {
      margins:'3 0 0 4',
      width:18,
      tooltip:TaxonomyStudioPlugin_properties.INSTANCE.Taxonomy_action_tooltip,
      baseAction:openChooserAction
    });
    selectButton.addListener('afterrender', function ():void {
      selectButton.getEl().setStyle('cursor', 'pointer');
    });
    add(selectButton);

    add(new Spacer(spacer({width:'5'})));
    equalsLabel = new Label(label({text:'='}));
    add(equalsLabel);


    valueField = new TextField(textfield({
      flex: 1,
      margins: '0 5 0 5',
      emptyText: config['valueEmptyText'] != null ? config['valueEmptyText'] : 'empty',
      allowBlank: false,
      enableKeyEvents: true
    }));
    add(valueField);

    percentageLabel = new Label(label({text:'%'}));
    add(percentageLabel);
    add(new Spacer(spacer({width:'5'})));

    valueField.addListener('keyup', function():void {
      fireSelfEvent('modified');
    });
    valueField.setValue(config['valueText']);

    if (config['suffixText'] != null) {
      add(new DisplayField(displayfield({
        value: config['suffixText'],
        margins: "1 5 0 5"
      })));
    }

    deleteButton = new Button(button({
      width: 20,
      iconCls: "remove",
      handler: function():void {
        fireSelfEvent('p13n-destroyDynamicField');
      }
    }));
    add(deleteButton);
  }

  private function fireSelfEvent(event:String):void {
    fireEvent(event, this);
  }

  public function setSelection(content:Content):void {
    if(content) {
      taxonomyId = IdHelper.parseContentId(content);//use property
      taxonomySelectionExpr.setValue(content);
      keywordField.setValue(TaxonomyUtil.getTaxonomyName(content));
    }
    taxonomySelectionExpr.addChangeListener(taxonomiesSelected);
  }


  /**
   * Invoked after the taxonomy chooser has been closed.
   * @param expr The value expression that contains the selection.
   */
  private function taxonomiesSelected(expr:ValueExpression):void {
    if (expr.getValue()) {
      var selection:TaxonomyNodeList = expr.getValue() as TaxonomyNodeList;
      if (selection) {
        var leafRef:String = selection.getLeafRef();
        var taxonomy1:Content = ContentUtil.getContent(leafRef);
        taxonomy1.load(function ():void {
          taxonomySelectionExpr.setValue(taxonomy1); //this will trigger it self, but a different cast will apply then!
        });
      }
      else if(expr.getValue() as Content) {//will be executed after the second trigger event and finally sets the value.
        var taxonomy:Content = expr.getValue() as Content;
        keywordField.setValue(TaxonomyUtil.getTaxonomyName(taxonomy));
        fireEvent('modified', this);
      }
    }
    else {  //handle resetting
      keywordField.setValue(null);
      taxonomySelectionExpr.setValue(null);
      fireEvent('modified', this);
    }
  }

  public function getTaxonomyId():int {
    return this.taxonomyId;
  }

  //-----------------------

  public function getPropertyName():String {
    var taxonomy:Content = this.taxonomySelectionExpr.getValue() as Content;
    return TaxonomyConditionUtil.formatPropertyName(propertyPrefix + '!', taxonomy);
  }

  public function getPropertyValue():String {
    const v:String = valueField.getValue();
    return v ? TaxonomyConditionUtil.formatPropertyValue4Store(v) : SelectionRuleHelper.EMPTY_VALUE;
  }

  public function setPropertyValue(value:String):void {
    valueField.setValue(value == SelectionRuleHelper.EMPTY_VALUE ? null : TaxonomyConditionUtil.formatPropertyValue4Textfield(value));
  }
}
}
