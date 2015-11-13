<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-imagemap cm-teaser cm-teaser--hero" <@cm.metadata (metadata![]) + [self.content] /> data-cm-imagemap='{"coordsBaseWidth": "${bp.IMAGE_TRANSFORMATION_BASE_WIDTH}"}'>
<#-- generate unique id for imagemap -->
<#assign imageMapId=bp.generateId("cm-map-")/>

<#-- imagemap does not make sense without image set -->
<#if self.picture?has_content>
    <div class="cm-imagemap__wrapper">
      <@bp.optionalLink href="${cm.getLink(self.target!cm.UNDEFINED)}" attr={"class": "cm-imagemap__link"}>
      <#-- include image -->
        <@cm.include self=self.picture params={
          "limitAspectRatios": lc.getAspectRatiosForTeaserHero(),
          "classBox": "cm-teaser__content cm-aspect-ratio-box",
          "classImage": "cm-imagemap__image cm-aspect-ratio-box__content cm-notselectable",
          "metadata": ["properties.pictures"],
          "additionalAttr": {"useMap": "#" + imageMapId!"", "unselectable": "on"}
        }/>
      <#-- headline -->
        <#if self.teaserTitle?has_content>
            <h2 class="cm-imagemap__title cm-heading2 cm-heading2--boxed cm-teaser__title"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h2>
        </#if>
      </@bp.optionalLink>

    <#-- include imagemap -->
      <@cm.include self=self view="areas" params={"imageMapId": imageMapId}/>
    </div>
</#if>
</div>
