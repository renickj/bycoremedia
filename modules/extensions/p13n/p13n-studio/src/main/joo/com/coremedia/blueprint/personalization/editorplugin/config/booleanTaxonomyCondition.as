package com.coremedia.blueprint.personalization.editorplugin.config {


/**
 * A Condition specialized for editing <i>taxonomy conditions</i> with two possible values: 1 (taxonomy active) and
 * !1 (taxonomy not active). The keyword represents the taxonomy name. The value is set implicit by setting the operator
 * (contains =1; contains not !=1).
 *
 * @see com.coremedia.blueprint.studio.taxonomy.condition.BooleanTaxonomyCondition
 */
[ExtConfig(target="com.coremedia.blueprint.personalization.editorplugin.taxonomy.BooleanTaxonomyCondition", xtype)]
public dynamic class booleanTaxonomyCondition extends abstractTaxonomyCondition {

  public static native function get xtype():String;

  public function booleanTaxonomyCondition(config:Object = null) {
    super(config || {});
  }

}
}