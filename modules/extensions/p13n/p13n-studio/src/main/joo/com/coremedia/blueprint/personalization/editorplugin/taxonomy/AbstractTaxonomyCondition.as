package com.coremedia.blueprint.personalization.editorplugin.taxonomy {

import com.coremedia.blueprint.personalization.editorplugin.config.abstractTaxonomyCondition;
import com.coremedia.blueprint.studio.config.taxonomy.openTaxonomyChooserAction;
import com.coremedia.blueprint.studio.config.taxonomy.taxonomySearchField;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.action.OpenTaxonomyChooserAction;
import com.coremedia.blueprint.studio.taxonomy.selection.TaxonomySearchField;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.personalization.ui.Personalization_properties;
import com.coremedia.personalization.ui.condition.AbstractCondition;
import com.coremedia.personalization.ui.util.SelectionRuleHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Button;
import ext.Ext;
import ext.Template;
import ext.config.button;
import ext.config.displayfield;
import ext.config.textfield;
import ext.form.DisplayField;
import ext.form.TextField;
import ext.form.VTypes;

/**
 * A Condition specialized for editing <i>taxonomy conditions</i>. A taxonomy condition consists of a linked taxonomy
 * (as Keyword), a comparison operator, and a value field (percentage).
 *
 * @xtype com.coremedia.blueprint.studio.taxonomy.condition.AbstractTaxonomyCondition
 */
public class AbstractTaxonomyCondition extends AbstractCondition {

  {
    // introduce new vtypes for keywords and values
    VTypes['keywordConditionKeywordVal'] = /^[a-zA-Z_][a-zA-Z_0-9\.]*$/;
    VTypes['keywordConditionKeywordMask'] = /^[a-zA-Z_0-9\.]/;
    VTypes['keywordConditionKeywordText'] = Personalization_properties.INSTANCE.p13n_error_keywordText;
    VTypes['keywordConditionKeyword'] = function (v:*):* {
      return VTypes['keywordConditionKeywordVal'].test(v);
    };
    VTypes['keywordConditionValueVal'] = /^\d+(\.\d+)?$/;
    VTypes['keywordConditionValueMask'] = /^[0-9\.]/;
    VTypes['keywordConditionValueText'] = Personalization_properties.INSTANCE.p13n_error_valueText;
    VTypes['keywordConditionValue'] = function (v:*):* {
      return VTypes['keywordConditionValueVal'].test(v);
    };
  }

  private static const VALUE_EMPTY_TEXT:String = Personalization_properties.INSTANCE.p13n_op_value;

  //
  // ui components
  //
  private var keywordField:TextField;
  private var valueField:TextField;

  // the internal prefix used for keywords. See class comment.
  private var propertyPrefix:String;


  //active selection
  private var taxonomySelectionExpr:ValueExpression = ValueExpressionFactory.create('taxonomy', beanFactory.createLocalBean({taxonomy:[]}));

  /**
   * Creates a new TaxonomyCondition.
   *
   * @cfg {String} conditionName name to be used for this condition instance in the condition combox
   * @cfg {String} propertyPrefix prefix of context properties mapped to this condition instance. The characters
   * following the prefix in a property name are assumed to represent the keyword
   * @cfg {Boolean} isDefault set to true if this condition is to be the default condition of the condition panel. The
   * first condition in the list of the registered conditions with the default flag set is used as the default
   * @cfg {String} keywordEmptyText the text to be shown in the keyword field if it is empty. Defaults to <i>keyword</i>
   * @cfg {String} keywordText the text to place into the keyword field. Defaults to <i>null</i>
   * @cfg {String} keywordVType the validation type of the keyword field. See below
   * @cfg {String} operatorEmptyText the text to be shown if no operator is selected. Default to <i>operator</i>
   * @cfg {Object} operatorNames user-presentable names of the operators. See below
   * @cfg {String} operator the operator to select initially. See below
   * @cfg {String} valueEmptyText the text to be shown in the value field if it is empty. Defaults to <i>value</i>
   * @cfg {String} valueText the text to place into the value field
   * @cfg {String} valueVType the validation type of the value field. See below
   * @cfg {String} suffixText the text to be shown after the value field. Defaults to <i>null</i>
   *
   * The property prefix is used to transform keyword properties to and from a user-presentable form. In a typical scenario,
   * keyword properties in a profile will use a common prefix to identify them as keywords, e.g. 'keyword'. This prefix
   * shouldn't be shown to users of the UI. If the propertyPrefix property is set to the internally used prefix, this condition
   * component will remove the prefix (including the '.' separator) from the keyword property before it is displayed, and
   * add it to the value in the keyword field when it is read via getPropertyName.
   *
   * The default validation types are:
   * <ul>
   * <li>For the keyword field: <code>/^[a-zA-Z_][a-zA-Z_0-9\.]*$/</code>.</li>
   * <li>For the value field: <code>/^\d+(\.\d+)?$/</code>.</li>
   * </ul>
   *
   * The <b>operators</b> offered by this component are:
   *
   * <ul>
   * <li>'lt'</li> less than
   * <li>'le'</li> less than or equals
   * <li>'eq'</li> equals
   * <li>'ge'</li> greater than or equals
   * <li>'gt'</li> greater than
   * </ul>
   *
   * The names used for the available operators can be overridden by a dictionary supplied via
   * the <b>operatorNames</b> property. The available operators and their default names are:
   *
   * <ul>
   * <li>'lt': 'less'</li>
   * <li>'le': 'less or equal'</li>
   * <li>'eq': 'equal'</li>
   * <li>'ge': 'greater or equal'</li>
   * <li>'gt': 'greater'</li>
   * </ul>
   *
   * You may override an arbitrary subset of these values.
   *
   * @param config configuration of this instance
   */
  public function AbstractTaxonomyCondition(config:abstractTaxonomyCondition) {
    super(Ext.apply(config, {
      /* obligatory configuration. overrides supplied properties */
      layout:"hbox",
      layoutConfig:{
        flex:1
      }
    }, {
      /* default configuration. may be overridden by supplied properties */
      height:26,
      autoWidth:true
    }));

    // store the keyword prefix
    propertyPrefix = config.propertyPrefix;
    if (propertyPrefix == null) {
      throw new Error(Personalization_properties.INSTANCE.p13n_error_propertyPrefix);
    }

    if (propertyPrefix != null && propertyPrefix.charAt(propertyPrefix.length - 1) == '.') {
      // remove the '.' at the end of the prefix
      propertyPrefix = propertyPrefix.substring(0, propertyPrefix.length - 1);
    }

    addEvents({
      /**
       * @event modified
       * Fires after the data represented by this component was modified. Intended to be used for
       * automatically saving changes.
       * @param {Component} this
       */
      modified:true});

    taxonomySelectionExpr.addChangeListener(taxonomiesSelected);
  }

  /**
   * Adds the field that contains the keyword.
   */
  public function addKeywordField():void {
    keywordField = new TaxonomySearchField(taxonomySearchField({
      allowBlank: false,
      searchResultExpression:taxonomySelectionExpr,
      taxonomyId:TaxonomyConditionUtil.getTaxonomyId4Chooser(propertyPrefix),
      flex: 40,
      cls:'force-ellipsis combo-text-field'
    }));
    add(keywordField);
  }

  /**
   * Adds the button that opens the TaxonomyChooser.
   */
  public function addTaxonomyButton():void {
    var openChooserAction:OpenTaxonomyChooserAction = new OpenTaxonomyChooserAction(openTaxonomyChooserAction({
      taxonomyId:TaxonomyConditionUtil.getTaxonomyId4Chooser(propertyPrefix),
      singleSelection:true,
      tooltip:TaxonomyStudioPlugin_properties.INSTANCE.Taxonomy_action_tooltip,
      propertyValueExpression:taxonomySelectionExpr
    }));

    var btn:Button = new Button(new button({
      iconCls: 'add-taxonomies-icon',
      margins: '0 3 0 3',
      buttonSelector: "a",
      tooltip: TaxonomyStudioPlugin_properties.INSTANCE.Taxonomy_action_tooltip,
      baseAction: openChooserAction,
      template: new Template([
        '<div id="{4}" class="{2} icon-button-xs">',
        '<em class="wrapper-btn-xs"><a href="#">&nbsp;</a></em>',
        '</div>']).compile()
      }));
      btn.addListener('afterrender', function ():void {
      btn.getEl().setStyle('cursor', 'pointer');
    });
    add(btn);
  }

  /**
   * Adds the input field that contains the taxonomy value (1 or 0 at BooleanTaxonomyCondition, oder 0 - 100 at
   * PercentageTaxonomyCondition).
   * @param config
   * @param visible <code>false</code> to render this value field hidden
   */
  public function addValueField(config:abstractTaxonomyCondition, visible:Boolean = true):void {

    valueField = new TextField(textfield({
      flex: 20,
      margins:'0 3 0 3',
      emptyText:config['valueEmptyText'] != null ? config['valueEmptyText'] : VALUE_EMPTY_TEXT,
      allowBlank:false,
      vtype:config['valueVType'] != null ? config['valueVType'] : 'keywordConditionValue',
      enableKeyEvents:true,
      hidden:!visible
    }));
    add(valueField);

    valueField.addListener('keyup', function ():void {
      fireEvent('modified');
    });
    valueField.setValue(config['valueText']);

    if (config['suffixText'] != null) {
      add(new DisplayField(displayfield({
        value:config['suffixText'],
        margins: "1 3 0 3"
      })));
    }
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
        var taxonomy:Content = ContentUtil.getContent(leafRef);
        taxonomy.load(function ():void {
          taxonomySelectionExpr.setValue(taxonomy); //this will trigger it self, but a different cast will apply then!
        });
      }
      else if (expr.getValue() as Content) {//will be executed after the second trigger event and finally sets the value.
        var taxonomy1:Content = expr.getValue() as Content;
        fireEvent('modified', this);
        keywordField.setValue(TaxonomyUtil.getTaxonomyName(taxonomy1));
      }
    }
    else {
      keywordField.setValue("");
      fireEvent('modified', this);
    }
  }

  public override function getPropertyName():String {
    var taxonomy:Content = this.taxonomySelectionExpr.getValue() as Content;
    return TaxonomyConditionUtil.formatPropertyName(propertyPrefix + '.', taxonomy);
  }

  public override function setPropertyName(name:String):void {
    var taxonomy:Content = TaxonomyConditionUtil.getTaxonomyContent(name);
    if (taxonomy) {
      taxonomy.invalidate(function ():void {
        taxonomySelectionExpr.removeChangeListener(taxonomiesSelected);
        taxonomySelectionExpr.setValue(taxonomy);
        keywordField.setValue(TaxonomyUtil.getTaxonomyName(taxonomy));
        taxonomySelectionExpr.addChangeListener(taxonomiesSelected);
      });
    }
  }

  public override function getPropertyValue():String {
    const v:String = valueField.getValue();
    return v ? TaxonomyConditionUtil.formatPropertyValue4Store(v) : SelectionRuleHelper.EMPTY_VALUE;
  }

  public override function setPropertyValue(value:String):void {
    valueField.setValue(value == SelectionRuleHelper.EMPTY_VALUE ? null : TaxonomyConditionUtil.formatPropertyValue4Textfield(value));
  }
}
}
