<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->

<div class="cm-collection"<@cm.metadata self.content/>>
    <#-- headline -->
    <h2<@cm.metadata "properties.title"/>>${self.title}</h2>
    <#-- items -->
    <div<@cm.metadata "properties.items"/>>
      <#list self.items![] as item>
        <@cm.include self=item />
      </#list>
    </div>
    <#-- hook for extensions at bottom of a detailview -->
    <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
</div>
