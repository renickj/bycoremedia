<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="crop" type="java.lang.String" -->
<#-- @ftlvariable name="additionalAttr" type="java.util.Map" -->

<div class="${classBox!""}"<@cm.metadata (metadata![]) + [self.content]/>>
  <#if self.data?has_content>
    <#assign imageLink="" />
    <#assign classResponsive="" />
    <#-- additionalAttr used by imagemaps -->
    <#assign attributes=additionalAttr!{} />

    <#-- decide if responsiveImage functionality is to be used or uncropped image will be shown -->
    <#if self.disableCropping>
      <#-- A) Cropping disabled, display image in full size -->
      <#assign imageLink=bp.uncroppedImageLink(self) />
    <#else>
      <#-- B) display responsive image -->
      <#assign classResponsive="cm-image--responsive" />
      <#assign attributes += {"data-cm-responsive-image": bp.responsiveImageLinksData(self, limitAspectRatios![])!""} />
    </#if>

    <#-- alt is the content name by default -->
    <#assign alt=(bp.getMessage("Image_alt") +" "+ self.content.name)!"" />
    <#-- if alt property is set, use it as alt -->
    <#if self.alt?has_content>
      <#assign alt=self.alt />
    </#if>

    <#-- title (and copyright) -->
    <#assign title=self.title!"" />
    <#if self.copyright?has_content>
      <#assign title += title?has_content?then(" ", "") + "(© " + self.copyright + ")" />
    </#if>

    <#-- add all attributes to the map -->
    <#assign attributes += {"src": imageLink, "alt": alt, "title": title} />

    <img class="cm-image cm-image--loading ${classImage!""} ${classResponsive!""}" <@bp.renderAttr attributes />
      <@cm.metadata data=["properties.data" + crop?has_content?then(".", "") + crop!""] />
    />
  </#if>
</div>
