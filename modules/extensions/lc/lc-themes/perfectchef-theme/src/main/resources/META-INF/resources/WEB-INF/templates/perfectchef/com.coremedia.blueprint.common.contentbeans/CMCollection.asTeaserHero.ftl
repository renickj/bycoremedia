<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#if (self.items?size > 0)>
  <@bp.optionalFrame title=self.teaserTitle!"" classFrame="cm-frame--teaser" attrTitle={"metadata": [self.content, "properties.teaserTitle"]}>
    <#list self.items![] as item>
      <@cm.include self=item view="asTeaserHero" params={"metadata": (metadata![]) + [self.content, "properties.items"]} />
    </#list>
  </@bp.optionalFrame>
</#if>