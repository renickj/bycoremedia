package com.coremedia.blueprint.personalization.editorplugin.taxonomy {

import com.coremedia.blueprint.personalization.editorplugin.config.percentageTaxonomyCondition;
import com.coremedia.personalization.ui.util.SelectionRuleHelper;

import ext.Spacer;
import ext.config.spacer;

/**
 * A Condition specialized for editing <i>taxonomy conditions</i> with values from 0 - 100. The keyword represents the
 * taxonomy name and the value field represents the percentage of that taxonomy.
 *
 * @xtype com.coremedia.blueprint.studio.taxonomy.condition.PercentageTaxonomyCondition
 */
public class PercentageTaxonomyCondition extends AbstractTaxonomyCondition {

  private static const OPERATORS:Array = [
    SelectionRuleHelper.OP_EQUAL,
    SelectionRuleHelper.OP_GREATER_THAN,
    SelectionRuleHelper.OP_GREATER_THAN_OR_EQUAL,
    SelectionRuleHelper.OP_LESS_THAN,
    SelectionRuleHelper.OP_LESS_THAN_OR_EQUAL
  ];

  public function PercentageTaxonomyCondition(config:percentageTaxonomyCondition) {
    super(config);

    add(new Spacer(spacer({width:5})));
    addKeywordField();
    addTaxonomyButton();
    initOpSelector(null, config['operatorNames'], config['operatorEmptyText'], config['operator'],
            OPERATORS, DEFAULT_OPERATOR_DISPLAY_NAMES);
    addValueField(config);

  }
}
}
