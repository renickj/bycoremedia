<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Product" -->
<#-- @ftlvariable name="orientation" type="java.lang.String" -->
<#-- @ftlvariable name="types" type="java.lang.String" -->

<div class="cm-product-assets" data-cm-refreshable-fragment='{"url": "${cm.getLink(self, 'asAssets')}"}'>
  <#if (types == 'all' || types == 'visuals')>
    <#assign visuals=bp.createBeansFor(self.visuals) />
    <#if orientation?has_content && visuals?has_content>

    <#-- set image aspect ratio -->
      <#assign classLightbox="" />
      <#assign limitAspectRatios=["portrait_ratio1x1"] />
      <#if (orientation == 'landscape') >
        <#assign classLightbox="cm-lightbox--landscape" />
        <#assign limitAspectRatios=["landscape_ratio4x3"] />
      <#elseif (orientation == 'portrait') >
        <#assign classLightbox="cm-lightbox--portrait" />
        <#assign limitAspectRatios=["portrait_ratio3x4"] />
      </#if>

    <#-- slideshow with large images -->
        <div class="cm-product-assets__slideshow cm-collection--slideshow cm-slideshow--carousel cm-lightbox--gallery">
        <#-- large image with link to lightBoxed image -->
          <#list visuals![] as visual>
            <@cm.include self=visual!cm.UNDEFINED view="asLightBox" params={
              "classBox": classLightbox,
              "limitAspectRatios": limitAspectRatios
            } />
          </#list>
        <#-- controls to navigate thru images -->
          <#if (visuals?size > 1)>
              <div class="cm-collection--slideshow__prev cm-direction-arrow cm-direction-arrow--left"></div>
              <div class="cm-collection--slideshow__next cm-direction-arrow cm-direction-arrow--right"></div>
          </#if>
        </div>

    <#-- this is the selector slideshow -->
      <#if (visuals?size > 1)>
          <div class="cm-product-assets__carousel cm-collection--slideshow cm-slideshow--carousel-chooser">
            <#list visuals![] as visual>
                <div class="cycle-slide">
                  <@cm.include self=visual!cm.UNDEFINED view="asPlainTeaser" params={
                  "limitAspectRatios": ["portrait_ratio1x1"],
                  "classBox": "cm-aspect-ratio-box",
                  "classImage": "cm-aspect-ratio-box__content"
                  } />
                </div>
            </#list>
          <#-- add empty slides to get a responsive layout for 4 slides -->
            <#if (visuals?size == 2 )>
                <div class="cycle-slide cycle-slide-disabled"></div>
                <div class="cycle-slide cycle-slide-disabled"></div>
            <#elseif (visuals?size == 3 )>
                <div class="cycle-slide cycle-slide-disabled"></div>
            </#if>
          </div>
      </#if>
    </#if>
  </#if>

  <#-- render download list -->
  <#if (types == 'all' || types == 'downloads') >
    <#assign downloads=bp.createBeansFor(self.downloads) />
    <#if (downloads?size > 0)>
      <div class="cm-product-assets__downloads cm-product-assets-downloads">
        <h3 class="cm-product-assets-downloads__title cm-heading3">${bp.getMessage("product_assets_downloads")}</h3>
        <ul class="cm-product-assets-downloads__list">
          <#list downloads![] as download>
            <@cm.include self=download view="asLinkListItem" params={"classItem": "cm-product-assets-downloads__item"} />
          </#list>
        </ul>
      </div>
    </#if>
  </#if>

</div>