<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMProductTeaser" -->
<#-- @ftlvariable name="classQuickInfo" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="quickInfoId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoModal" type="java.lang.Boolean" -->
<#-- @ftlvariable name="quickInfoGroup" type="java.lang.String" -->
<#-- @ftlvariable name="overlay" type="java.util.Map" -->

<#-- if overlay configuration is not set explicitly assert false for each key not set -->
<#assign overlay={
  "displayTitle": false,
  "displayShortText": false,
  "displayPicture": false,
  "displayDefaultPrice": false,
  "displayDiscountedPrice": false,
  "displayOutOfStockLink": false
} + overlay!{} />
<#assign quickInfoData={} />
<#if quickInfoModal?has_content && quickInfoModal?is_boolean>
  <#assign quickInfoData=quickInfoData + {"modal": quickInfoModal!false} />
</#if>
<#if quickInfoGroup?has_content>
  <#assign quickInfoData=quickInfoData + {"group": quickInfoGroup!""} />
</#if>

<div id="${quickInfoId!bp.generateId("quickinfo")}" class="cm-quickinfo ${classQuickInfo!""}" <@cm.dataAttribute name="data-cm-quickinfo" data=quickInfoData /><@cm.metadata (metadata![]) + [self.content] />>

  <#-- image -->
  <#if overlay.displayPicture>
    <div class="cm-quickinfo__property cm-quickinfo__property--image">
      <a href="${cm.getLink(self.target!(cm.UNDEFINED))}">
      <#assign pictureParams={
        "limitAspectRatios": [ "landscape_ratio4x3" ],
        "classBox": "cm-quickinfo__image cm-aspect-ratio-box",
        "classImage": "cm-aspect-ratio-box__content"
      } />
      <#if self.picture?has_content>
        <@cm.include self=self.picture!cm.UNDEFINED params=pictureParams + {"metadata": ["properties.pictures"]} />
      <#else>
        <@cm.include self=(self.product.catalogPicture)!cm.UNDEFINED params=pictureParams />
      </#if>
      </a>
    </div>
  </#if>

  <#-- title -->
  <#assign showTitle=self.teaserTitle?has_content && overlay.displayTitle />
  <#assign showTeaserText=self.teaserText?has_content && overlay.displayShortText />
  <#if showTitle || showTeaserText>
    <div class="cm-quickinfo__property cm-quickinfo__property--general">
      <#-- teaserTitle -->
      <#if showTitle>
        <h5 class="cm-quickinfo__title cm-heading5"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle}</h5>
      </#if>
      <#-- teaserText -->
      <#if showTeaserText>
        <div class="cm-quickinfo__text"<@cm.metadata "properties.teaserText" />><@cm.include self=self.teaserText!cm.UNDEFINED /></div>
      </#if>
    </div>
  </#if>

  <#-- price -->
  <#if overlay.displayDefaultPrice || overlay.displayDiscountedPrice>
    <div class="cm-quickinfo__property cm-quickinfo__property--pricing">
      <@cm.include self=self.product!cm.UNDEFINED view="pricing" params={"showListPrice": overlay.displayDefaultPrice, "showOfferPrice": overlay.displayDiscountedPrice, "classListPrice": "cm-price--quickinfo cm-quickinfo__listprice", "classOfferPrice": "cm-price--quickinfo cm-quickinfo__offerprice"} />
    </div>
  </#if>

  <#if (self.product?has_content && self.product.isAvailable()) || overlay.displayOutOfStockLink>
      <#-- add-to-cart button -->
      <div class="cm-quickinfo__property cm-quickinfo__property--controls cm-button-group cm-button-group--linked-large">
        <@lc.addToCartButton product=self.product!cm.UNDEFINED withLink=cm.getLink(self.target!(cm.UNDEFINED)) enableShopNow=self.isShopNowEnabled(cmpage.context) attr={"classes": ["cm-button-group__button", "cm-button--linked-large"]} />
      </div>
  </#if>

  <#-- close button -->
  <@bp.button baseClass="" iconClass="cm-icon__symbol icon-close" iconText=bp.getMessage("button_close") attr={"class": "cm-quickinfo__close cm-icon"}/>
</div>
