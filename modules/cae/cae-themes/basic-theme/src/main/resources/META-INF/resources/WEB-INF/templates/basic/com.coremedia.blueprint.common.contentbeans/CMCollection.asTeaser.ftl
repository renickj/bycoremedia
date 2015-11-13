<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->

<div class="cm-collection"<@cm.metadata self.content/>>
    <#-- headline -->
    <h2<@cm.metadata "properties.teaserTitle"/>>${self.teaserTitle}</h2>
    <#-- items -->
    <ul<@cm.metadata "properties.items"/>>
      <#list self.items![] as item>
        <li><@cm.include self=item view="asLink"/></li>
      </#list>
    </ul>
</div>
