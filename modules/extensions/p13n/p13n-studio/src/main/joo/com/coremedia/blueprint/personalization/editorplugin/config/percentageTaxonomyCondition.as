package com.coremedia.blueprint.personalization.editorplugin.config {


/**
 * A Condition specialized for editing <i>taxonomy conditions</i> with values from 0 - 100. The keyword represents the
 * taxonomy name and the value field represents the percentage of that taxonomy.
 *
 * @see com.coremedia.blueprint.studio.taxonomy.condition.PercentageTaxonomyCondition
 */
[ExtConfig(target="com.coremedia.blueprint.personalization.editorplugin.taxonomy.PercentageTaxonomyCondition", xtype)]
public dynamic class percentageTaxonomyCondition extends abstractTaxonomyCondition {

  public static native function get xtype():String;

  public function percentageTaxonomyCondition(config:Object = null) {
    super(config || {});
  }

}
}