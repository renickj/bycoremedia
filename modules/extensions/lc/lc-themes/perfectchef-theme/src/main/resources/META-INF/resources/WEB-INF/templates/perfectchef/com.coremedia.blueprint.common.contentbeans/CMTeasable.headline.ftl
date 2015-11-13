<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="classHeadline" type="java.lang.String" -->

<#if self.pictures?has_content && (self.pictures?size > 1)>
  <div class="cm-headline cm-collection--slideshow ${classHeadline!""}" data-cm-slideshow='{ "container": "> .cm-collection--slideshow__slides", "item": "> .cm-collection__item", "timeout": "5000", "prev": "> .cm-collection--slideshow__prev", "next": "> .cm-collection--slideshow__next" }'>
    <div class="cm-headline__image cm-collection--slideshow__slides">
      <#list self.pictures as picture>
        <@cm.include self=picture params={
          "limitAspectRatios": [ "landscape_ratio4x1", "landscape_ratio8x3" ],
          "classBox": "cm-collection__item cm-aspect-ratio-box",
          "classImage": "cm-aspect-ratio-box__content",
          "metadata": ["properties.pictures"]
        }/>
      </#list>
      <div class="cm-collection--slideshow__prev cm-direction-arrow cm-direction-arrow--left"></div>
      <div class="cm-collection--slideshow__next cm-direction-arrow cm-direction-arrow--right"></div>
    </div>
    <#if self.teaserTitle?has_content>
      <h2 class="cm-headline__title cm-heading2 cm-heading2--boxed" <@cm.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</h2>
    </#if>
  </div>
<#elseif self.picture?has_content>
  <div class="cm-headline ${classHeadline!""}">
    <@cm.include self=self.picture params={
      "limitAspectRatios": [ "landscape_ratio4x1", "landscape_ratio8x3" ],
      "classBox": "cm-headline__image cm-aspect-ratio-box",
      "classImage": "cm-aspect-ratio-box__content",
      "metadata": ["properties.pictures"]
    }/>
    <#if self.teaserTitle?has_content>
      <h2 class="cm-headline__title cm-heading2 cm-heading2--boxed" <@cm.metadata "properties.teaserTitle"/>>${self.teaserTitle!""}</h2>
    </#if>
  </div>
<#else>
  <div class="cm-headline ${classHeadline!""}">
    <#if self.teaserTitle?has_content>
      <h2 class="cm-headline__title cm-heading2 cm-heading2--boxed" <@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
    </#if>
  </div>
</#if>