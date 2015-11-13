<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<div class="cm-squarelist"<@cm.metadata self.content />>
  <@bp.optionalLink href="${link}">
    <#-- picture -->
    <#if self.picture?has_content>
    <div class="cm-squarelist__box">
      <@cm.include self=self.picture params={
      "limitAspectRatios": [ "portrait_ratio1x1" ],
      "classBox": "cm-squarelist__picture-box",
      "classImage": "cm-squarelist__picture",
      "metadata": ["properties.pictures"]
      }/>
      <div class="cm-squarelist__dimmer"></div>
    </div>
    <#else>
      <div class="cm-squarelist__picture-box"<@cm.metadata "properties.pictures" />>
        <div class="cm-squarelist__picture cm-image--missing"></div>
      </div>
    </#if>

    <#-- headline -->
    <#if self.teaserTitle?has_content>
      <h3 class="cm-squarelist__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h3>
    </#if>
  </@bp.optionalLink>
</div>
