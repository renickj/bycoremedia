<#-- Same as CMPicture.ftl but with additional infos like copyright and detailText -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->

<div class="cm-image-box cm-image-box--details"<@cm.metadata self.content/>>
  <#-- image -->
  <@cm.include self=self params={
    "limitAspectRatios": limitAspectRatios![],
    "classBox": "cm-image-box__image"
  }/>

  <div class="cm-image-box__infos">
    <#-- copyright -->
    <#if self.copyright?has_content>
      <div<@cm.metadata "properties.copyright"/> class="cm-image-box__copyright">${self.copyright}</div>
    </#if>
    <#-- description -->
    <#if self.detailText?has_content>
      <div<@cm.metadata "properties.detailText"/> class="cm-image-box__description"><@cm.include self=self.detailText /></div>
    </#if>
  </div>
</div>
