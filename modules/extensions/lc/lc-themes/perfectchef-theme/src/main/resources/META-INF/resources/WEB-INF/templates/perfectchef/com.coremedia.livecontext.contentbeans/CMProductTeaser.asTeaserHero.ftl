<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMProductTeaser" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teaser cm-teaser--product cm-teaser--hero"<@cm.metadata (metadata![]) + [self.content] />>
  <@bp.optionalLink href="${cm.getLink(self.target!(cm.UNDEFINED))}">
    <#assign pictureParams={
      "limitAspectRatios": lc.getAspectRatiosForTeaserHero(),
      "classBox": "cm-teaser__content cm-aspect-ratio-box",
      "classImage": "cm-aspect-ratio-box__content"
    } />
    <#if self.picture?has_content>
      <@cm.include self=self.picture params=pictureParams + {"metadata": ["properties.pictures"]}/>
    <#else>
      <@cm.include self=(self.product.catalogPicture)!cm.UNDEFINED params=pictureParams />
    </#if>
    <#if self.teaserTitle?has_content>
      <h2 class="cm-teaser__title cm-heading2 cm-heading2--boxed"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
    </#if>
    <@cm.include self=self.product!cm.UNDEFINED view="info" params={
      "classBox": "cm-teaser__info",
      "classPrice": "cm-price--teaser"
    } />
  </@bp.optionalLink>

  <#-- shop now button and quickinfo -->
  <#if self.isShopNowEnabled(cmpage.context)>
    <#assign quickInfoId=bp.generateId("cm-quickinfo-") />
    <#-- button -->
    <div class="cm-teaser__button-group cm-button-group cm-button-group--overlay">
      <@bp.button text=bp.getMessage("shop_now") attr={
        "classes": ["cm-button-group__button", "cm-button--primary", "cm-button--shadow"],
        "data-cm-button--quickinfo": '{"target": "${quickInfoId!""}"}'
      } />
    </div>
    <#-- quickinfo -->
    <@cm.include self=self view="asQuickInfo" params={
      "quickInfoId": quickInfoId!"",
      "quickInfoGroup": "product-teasers",
      "quickInfoModal": true,
      "classQuickInfo": "cm-teaser__quickinfo",
      "metadata": ["properties.target"],
      "overlay": {
        "displayTitle": true,
        "displayShortText": true,
        "displayPicture": true,
        "displayDefaultPrice": true,
        "displayDiscountedPrice": true,
        "displayOutOfStockLink": true
      }
    }
    />
  </#if>
</div>
