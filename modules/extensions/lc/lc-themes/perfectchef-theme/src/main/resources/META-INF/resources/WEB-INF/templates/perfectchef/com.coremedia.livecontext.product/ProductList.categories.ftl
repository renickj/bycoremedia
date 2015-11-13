<#-- @ftlvariable name="self" type="com.coremedia.livecontext.product.ProductList" -->

<#if self.category?has_content>
<div class="cm-category">

  <#if self.hasCategories()&&  self.navigation?has_content>
      <div class="cm-collection cm-collection--masonry cm-collection--categories cm-js-masonry" data-masonry-options='{ "isInitLayout": false, "columnWidth": ".cm-collection--categories > .cm-collection--masonry__grid-sizer", "gutter": ".cm-collection--categories > .cm-collection--masonry__grid-gutter", "itemSelector": ".cm-collection--masonry > .cm-collection__item" }'>
        <#list self.subCategoriesInSite![] as child>
          <@cm.include self=child view="asListItem" />
        </#list>
          <div class="cm-collection--masonry__grid-sizer"></div>
          <div class="cm-collection--masonry__grid-gutter"></div>
      </div>
  </#if>

</div>
</#if>