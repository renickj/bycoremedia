<#-- @ftlvariable name="self" type="com.coremedia.livecontext.context.ProductInSite" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#if self.product?has_content && (cmpage.navigation.rootNavigation)?has_content>
  <#if self.product.listPrice?has_content && self.product.currency?has_content && self.product.locale?has_content>
    <#assign listPriceFormatted=lc.formatPrice(self.product.listPrice, self.product.currency, self.product.locale)/>
  </#if>
  <div class="cm-teaser cm-teaser--product"<@cm.metadata metadata![] />>
    <@bp.optionalLink href="${cm.getLink(self)}">
      <@cm.include self=(self.product.catalogPicture)!cm.UNDEFINED params={
        "limitAspectRatios": lc.getAspectRatiosForTeaser(),
        "classBox": "cm-teaser__content cm-aspect-ratio-box",
        "classImage": "cm-aspect-ratio-box__content"
      } />
      <@cm.include self=self.product!cm.UNDEFINED view="info" params={
        "classBox": "cm-teaser__info",
        "classPrice": "cm-price--teaser"
      } />
    </@bp.optionalLink>
  </div>
</#if>