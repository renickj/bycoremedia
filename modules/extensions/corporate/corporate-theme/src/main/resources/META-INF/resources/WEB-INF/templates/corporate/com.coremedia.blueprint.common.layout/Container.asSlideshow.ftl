<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="modifier" type="java.lang.String" -->
<#-- @ftlvariable name="items" type="java.util.List" -->
<#-- @ftlvariable name="viewItems" type="java.lang.String" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->

<#assign modifier=cm.localParameters().modifier!"" />
<#assign index=cm.localParameters().index!0 />
<#assign hasEvenIndex=(index % 2 == 0) />

<#assign clsModifier="" />
<#if modifier?has_content>
  <#assign clsModifier="cm-carousel__control--${modifier}" />
</#if>

<#assign additionalVariantCssClass="" />
<#if !hasEvenIndex>
  <#assign additionalVariantCssClass=" cm-carousel--alternative" />
</#if>

<#assign items=cm.localParameters().items![] />
<#assign carouselId=bp.generateId("carousel") />

<#-- slideshow with controls -->
<#if (items?size > 1)>
  <#-- partial template to render a list of items as part of a carousel -->
  <div id="${carouselId}" class="cm-carousel carousel slide${additionalVariantCssClass}" data-cm-carousel='{"interval":"6000"}'>
    <#-- Wrapper for slides -->
    <div class="cm-carousel-inner carousel-inner" role="listbox">
      <#list items as item>
        <#assign additionalClass="" />
        <#if item_index == 0>
          <#assign additionalClass="item active"/>
        <#else>
          <#assign additionalClass="item"/>
        </#if>
        <@cm.include self=item view=viewItems params={"index": index, "additionalClass": additionalClass}/>
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
        <span class="cm-carousel__pagination-total">${items?size}</span>
      </p>
      <a class="cm-carousel-control carousel-control right" href="#${carouselId}" role="button" data-slide="next">
        <span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span>
        <span class="sr-only"><@bp.message "next" /></span>
      </a>
    </div>
  </div>
<#-- single item, no slideshow -->
<#elseif items?size == 1>
  <@cm.include self=items?first view=viewItems params={"index": index} />
</#if>
