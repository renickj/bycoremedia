<#-- @ftlvariable name="self" type="com.coremedia.blueprint.ecommerce.contentbeans.CMProduct" -->

<#assign modifier="details" />
<#assign clsModifier="" />
<#if modifier?has_content>
    <#assign clsModifier="cm-carousel__control--${modifier}" />

</#if>
<#assign pictures=self.pictures![] />
<#assign carouselId=bp.generateId("carousel") />

<article class="cm-details cm-details--product"<@cm.metadata self.content />>

  <#-- title -->
  <h1 class="cm-details__headline"<@cm.metadata "properties.productName"/>>${self.productName!""}</h1>

  <#if (pictures?has_content)>
      <#-- pictures -->
      <#if (pictures?size > 1)>
        <#-- partial template to render a list of items as part of a carousel -->
        <div id="${carouselId}" class="cm-details__pictures cm-carousel carousel slide" data-cm-carousel='{"pause":"true", "interval":"6000"}'>
          <#-- Wrapper for slides -->
          <div class="cm-carousel-inner carousel-inner" role="listbox">
            <#list pictures as item>
              <#assign additionalClass="" />
              <#if item_index == 0>
                <#assign additionalClass="item active"/>
              <#else>
                <#assign additionalClass="item"/>
              </#if>
              <div class="cm-details__image ${additionalClass}">
                <@cm.include self=item params={
                  "limitAspectRatios": ["portrait_ratio1x1", "landscape_ratio5x2"],
                  "classBox": "cm-details__picture-box",
                  "classImage": "cm-details__picture"
                }/>
              </div>
            </#list>
          </div>

          <#-- Controls -->
          <div class="cm-carousel__control ${clsModifier}">
            <a class="cm-carousel-control carousel-control left" href="#${carouselId}" role="button" data-slide="prev">
              <span class="glyphicon glyphicon-triangle-left" aria-hidden="true"></span>
              <span class="sr-only"><@bp.message "previous" /></span>
            </a>

            <p class="cm-carousel__pagination">
              <span class="cm-carousel__pagination-index">1</span>
              /
              <span class="cm-carousel__pagination-total">${pictures?size}</span>
            </p>
            <a class="cm-carousel-control carousel-control right" href="#${carouselId}" role="button" data-slide="next">
              <span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span>
              <span class="sr-only"><@bp.message "next" /></span>
            </a>
          </div>
        </div>
      <#-- single item, no slideshow -->
      <#elseif pictures?size == 1>
        <div class="cm-details__pictures">
          <@cm.include self=self.picture params={
          "limitAspectRatios": ["portrait_ratio1x1", "landscape_ratio5x2"],
          "classBox": "cm-details__picture-box",
          "classImage": "cm-details__picture"
          }/>
        </div>
      </#if>
  </#if>

  <#-- product code -->
  <div class="cm-details__code"<@cm.metadata "properties.productCode"/>>
    <span>${bp.getMessage("Product_code")}</span> ${self.productCode!""}
  </div>

  <#-- text -->
  <#if self.longDescription?has_content>
    <div class="cm-details__text cm-richtext"<@cm.metadata "properties.longDescription"/>>
      <@cm.include self=self.longDescription!cm.UNDEFINED />
    </div>
  </#if>

  <#-- downloads -->
  <#if self.downloads?has_content>
    <div class="cm-details__downloads cm-downloads"<@cm.metadata "properties.downloads"/>>
      <h2>${bp.getMessage("download.label")}</h2>
      <ul class="cm-downloads__items">
        <#list self.downloads![] as download>
          <li class="cm-downloads__item">
            <@cm.include self=download view="asLink" params={
              "cssClass": "cm-glyphicon-before"
            }/>
          </li>
        </#list>
      </ul>
    </div>
  </#if>
</article>

<#-- related -->
<#if self.related?has_content>
  <div class="cm-related"<@cm.metadata "properties.related"/>>
    <h2>${bp.getMessage("related.label")}</h2>
    <@cm.include self=bp.getContainer(self.related) view="asMedialist" />
  </div>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
