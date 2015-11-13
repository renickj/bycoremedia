<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->

<ul class="cm-navigation cm-navigation--footer cm-dropdown" data-dropdown-menus="ul" data-dropdown-items="li" data-dropdown-class-button-open="icon-menu-next" data-dropdown-class-button-close="icon-menu-back"<@cm.metadata data=[self.content, "properties.items"] />>
  <#list self.items![] as item>
    <@cm.include self=item view="asLinkListItem" />
  </#list>
</ul>
