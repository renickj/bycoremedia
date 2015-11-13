<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="cmpage.content" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->

<#if (cmpage.navigation.rootNavigation)?has_content>
  <#-- in blueprint rootNavigation declared as Navigation is instance of CMChannel -->
  <#assign rootChannel=cmpage.navigation.rootNavigation />

  <nav id="cm-navigation" class="cm-placement-header__item cm-icon cm-icon--${self.id!""}"<@cm.metadata data=[self.content, "properties.id", {'cm_highlightStrategy': 'CSS'}] />>
    <ul class="cm-icon__symbol cm-navigation cm-dropdown" data-dropdown-menus="ul" data-dropdown-items="li" data-dropdown-class-button-open="icon-menu-next" data-dropdown-class-button-close="icon-menu-back">
      <@cm.include self=rootChannel view="asLinkListItem" params={"maxDepth": 5} />
    </ul>
  </nav>
</#if>
