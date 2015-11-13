<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#if (self.items?has_content)>
  <div class="cm-container cm-container--gap"<@cm.metadata data=bp.getContainerMetadata(self) />>
    <#-- render the first item as gap -->
    <@cm.include self=self.items?first view="asGap" />
  </div>
</#if>
