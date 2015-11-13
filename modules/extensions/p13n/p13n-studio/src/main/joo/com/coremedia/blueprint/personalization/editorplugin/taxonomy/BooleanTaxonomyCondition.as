package com.coremedia.blueprint.personalization.editorplugin.taxonomy {

import com.coremedia.blueprint.personalization.editorplugin.config.booleanTaxonomyCondition;
import com.coremedia.personalization.ui.Personalization_properties;
import com.coremedia.personalization.ui.util.SelectionRuleHelper;

import ext.Spacer;
import ext.config.spacer;

/**
 * A Condition specialized for editing <i>taxonomy conditions</i> with two possible values: 1 (taxonomy active) and
 * !1 (taxonomy not active). The keyword represents the taxonomy name. The value is set implicit by setting the operator
 * (contains =1; contains not !=1)
 *
 * @xtype com.coremedia.blueprint.studio.taxonomy.condition.BooleanTaxonomyCondition
 */
public class BooleanTaxonomyCondition extends AbstractTaxonomyCondition {


  private static const NO_VALUE_OPERATORS:Array = [
    SelectionRuleHelper.OP_EQUAL,
    SelectionRuleHelper.OP_NOTEQUAL
  ];

  protected static const NO_VALUE_OPERATOR_DISPLAY_NAMES:* = {};
  {
    NO_VALUE_OPERATOR_DISPLAY_NAMES[SelectionRuleHelper.OP_EQUAL] = Personalization_properties.INSTANCE.p13n_op_contains;
    NO_VALUE_OPERATOR_DISPLAY_NAMES[SelectionRuleHelper.OP_NOTEQUAL] = Personalization_properties.INSTANCE.p13n_op_contains_not;
  }

  public function BooleanTaxonomyCondition(config:booleanTaxonomyCondition) {
    super(config);
    // the default value is 100 (which means 100% -> 1)
    config.valueText = "100";

    add(new Spacer(spacer({width:5})));
    initOpSelector(null, config['operatorNames'], config['operatorEmptyText'], config['operator'],
            NO_VALUE_OPERATORS, NO_VALUE_OPERATOR_DISPLAY_NAMES);
    addKeywordField();
    addTaxonomyButton();

    // the value field needs to be added, but should be hidden
    addValueField(config, false);

  }
}
}
