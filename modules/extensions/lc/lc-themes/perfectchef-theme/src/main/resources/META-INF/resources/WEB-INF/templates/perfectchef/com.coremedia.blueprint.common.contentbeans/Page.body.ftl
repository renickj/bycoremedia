<#include "../includes/slider.ftl" />
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->
<#-- @ftlvariable name="self.content" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->

<body id="top"<@cm.metadata data=sliderMetadata />>

<div class="cm-skiplinks cm-visuallyhidden">
    <ul>
        <li><a href="#cm-placement-main"><@bp.message "Skiplinks_Content" /></a></li>
        <li><a href="#cm-search"><@bp.message "Skiplinks_Search" /></a></li>
        <li><a href="#cm-navigation"><@bp.message "Skiplinks_Navigation" /></a></li>
    </ul>
</div>

<#if self.pageGrid?has_content>
  <div id="cm-page" class="cm-grid ${self.pageGrid.cssClassName!""} cm-js-masonry" data-masonry-options='{ "isInitLayout": false, "columnWidth": ".cm-grid > .cm-grid__sizer", "gutter": ".cm-grid > .cm-grid__gutter", "itemSelector": ".cm-grid > .cm-grid__item" }'>
  <#-- Iterator over each row -->
    <#list self.pageGrid.rows![] as row>
    <#-- Iterate over each placement-->
      <#list row.placements![] as placement>
        <@cm.include self=placement/>
      </#list>
    </#list>
    <div class="cm-grid__gutter"></div>
    <div class="cm-grid__sizer"></div>
  </div>
</#if>

<#if cmpage.developerMode>
  <div class="cm-preview-developer-mode" data-cm-developer-mode="true">
    <i class="icon-wrench" title="You're in Developer Mode"></i>
  </div>
</#if>

<a href="#top" class="cm-icon cm-icon--button-top cm-hidden">
  <i class="cm-icon__symbol icon-arrow-up"></i>
  <span class="cm-icon__info cm-visuallyhidden"><@bp.message "button_top" /></span>
</a>

<@cm.include self=self view="bodyEnd"/>

</body>