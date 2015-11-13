<#ftl strip_whitespace=true>
<#-- @ftlvariable name="liveContextFreemarkerFacade" type="com.coremedia.livecontext.web.taglib.LiveContextFreemarkerFacade" -->

<#--
 * Renders an addToCart button
 * If product has only one variant the button has add to cart functionality
 * otherwise its just a link to the product detail page
 *
 * @param product the product that needs to be added to cart
 * @param alwaysShow (optional) if true, forces the button to be shown even if product is not available
 * @param alwaysClickable (optional) always attached add to cart functionality even if there is more than one variant
 * @param withLink (optional) link to product detail page
 * @param enableShopNow (optional, default:true) enable "addToCart" functionality. If "false", only show the "Details" button
 -->
<#macro addToCartButton product alwaysShow=false alwaysClickable=false enableShopNow=true withLink="" attr={}>
  <#local numberOfVariants=(product.variants?size)!0 />
  <#local hasSingleSKU=(numberOfVariants == 1) />
  <#local isProductAvailable=(product.isAvailable())!false />

  <#-- variant 1) unavailable -->
  <#local buttonLabel=bp.getMessage("cart_unavailable") />
  <#local buttonData={} />
  <#local buttonClasses=[] />
  <#local iconClass="" />

  <#-- variant 2) available -->
  <#if (alwaysShow || isProductAvailable)>
    <#local buttonLabel=bp.getMessage("cart_view_variants") />
    <#local buttonClasses=buttonClasses + ["cm-button--primary"] />

  <#-- variant 3) available with one sku -->
    <#if (enableShopNow && (alwaysClickable || hasSingleSKU))>
      <#local cart=bp.substitute("cart", product)!cm.UNDEFINED />
      <#local buttonLabel=bp.getMessage("cart_add_item") />
      <#local buttonData={"data-cm-cart-add-item": '{"id": "${product.externalTechId!""}", "link": "${cm.getLink(cart, "ajax")}", "cart": ".cm-cart" }'} />
      <#local iconClass="icon-none" />
    </#if>
  </#if>

  <#local attr=bp.extendSequenceInMap(attr, "classes", buttonClasses) />

  <#local link="" />
  <#if (withLink?has_content && ((!alwaysClickable && !hasSingleSKU) || !enableShopNow))>
    <#local link=withLink>
  </#if>
  <@bp.button text=buttonLabel href=link baseClass="cm-button" iconClass=iconClass attr=(attr + buttonData) />
</#macro>

<#--Format Utils-->
<#function formatPrice amount currency locale>
  <#return liveContextFreemarkerFacade.formatPrice(amount, currency, locale)>
</#function>

<#function createProductInSite product>
  <#return liveContextFreemarkerFacade.createProductInSite(product)/>
</#function>

<#function getAspectRatiosForTeaser>
  <#return ["landscape_ratio4x3", "landscape_ratio8x3", "landscape_ratio4x1", "portrait_ratio20x31", "portrait_ratio1x1", "portrait_ratio3x4", "landscape_ratio16x9"] />
</#function>

<#function getAspectRatiosForTeaserHero>
  <#return ["portrait_ratio3x4", "landscape_ratio2x1", "portrait_ratio1x1", "landscape_ratio16x9", "landscape_ratio5x2"] />
</#function>

<#function fragmentContext>
  <#return liveContextFreemarkerFacade.fragmentContext()>
</#function>

<#function getSecureScheme>
  <#return liveContextFreemarkerFacade.getSecureScheme() />
</#function>
