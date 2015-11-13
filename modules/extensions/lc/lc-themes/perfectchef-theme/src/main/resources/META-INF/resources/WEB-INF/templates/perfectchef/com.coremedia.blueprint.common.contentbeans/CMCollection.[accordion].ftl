<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->

<ul class="cm-collection cm-collection--accordion js-accordion"<@cm.metadata data=[self.content, "properties.items"] />>
<#list self.items![] as item>
  <@cm.include self=item view="asAccordionItem" />
</#list>
</ul>