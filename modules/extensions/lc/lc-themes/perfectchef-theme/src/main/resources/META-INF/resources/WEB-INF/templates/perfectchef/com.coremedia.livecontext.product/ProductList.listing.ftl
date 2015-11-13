<#-- @ftlvariable name="self" type="com.coremedia.livecontext.product.ProductList" -->
<#-- @ftlvariable name="rootChannel" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->

<#if self.category?has_content && (cmpage.navigation.rootNavigation)?has_content>
  <#-- in blueprint rootNavigation declared as Navigation is instance of CMChannel -->
  <#assign rootChannel=cmpage.navigation.rootNavigation />

  <div class="cm-category">
    <#if self.isProductCategory()>
      <#assign categorySeoSegment=self.category.seoSegment>
      <#assign steps=self.steps>
      <#assign start=self.start>
      <#assign numProducts=self.totalProductCount>

      <div class="cm-collection cm-collection--masonry cm-collection--productlisting cm-js-masonry"
           data-masonry-options='{
               "isInitLayout": false,
               "columnWidth": ".cm-collection--productlisting > .cm-collection--masonry__grid-sizer",
               "gutter": ".cm-collection--productlisting > .cm-collection--masonry__grid-gutter",
               "itemSelector": ".cm-collection--masonry > .cm-collection__item"
           }'>
        <#if self.loadedProducts?has_content>
          <#list self.loadedProducts![] as product>
            <@cm.include self=product view="asListItem" params={
            "viewItem": "asCategoryItem"
            } />
          </#list>
        </#if>
        <div class="cm-collection--masonry__grid-sizer"></div>
        <div class="cm-collection--masonry__grid-gutter"></div>
      </div>
    </#if>
  </div>
</#if>
