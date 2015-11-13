<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#-- Iterate over each row -->
<#if cmpage.pageGrid?has_content>
  <div class="cm-grid ${self.pageGrid.cssClassName!""} cm-js-masonry" data-masonry-options='{ "isInitLayout": false, "columnWidth": ".cm-grid > .cm-grid__sizer", "gutter": ".cm-grid > .cm-grid__gutter", "itemSelector": ".cm-grid > .cm-grid__item" }'>

  <#list cmpage.pageGrid.rows![] as row>

    <#-- Iterate over each placement -->
    <#list row.placements![] as placement>

      <#-- do not render header and footer placements -->
      <#if !placement.name.toLowerCase().equals("header") && !placement.name.toLowerCase().equals("footer")>
        <@cm.include self=placement/>
      <#else>
        <!-- ## Not rendered: ${placement.name} ## -->
      </#if>
    </#list>

  </#list>

  <div class="cm-grid__gutter"></div>
  <div class="cm-grid__sizer"></div>
  </div>
</#if>
