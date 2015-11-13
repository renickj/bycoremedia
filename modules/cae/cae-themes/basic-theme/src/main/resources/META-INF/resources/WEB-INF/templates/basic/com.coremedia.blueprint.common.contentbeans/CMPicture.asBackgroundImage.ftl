<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="${classBox!""}"<@cm.metadata (metadata![]) + [self.content]/>>
  <#if self.data?has_content>
    <#assign imageLink="" />
    <#assign classResponsive="" />

    <#-- decide if responsiveImage functionality is to be used or uncropped image will be shown -->
    <#if self.disableCropping>
      <#-- A) Cropping disabled, display image in full size -->
      <#assign imageLink=bp.uncroppedImageLink(self) />
      <#-- add all attributes to the map -->
      <#if imageLink?has_content>
        <#assign attributes={"style": "background-image: url(${imageLink})"} />
      </#if>
    <#else>
      <#-- B) display responsive image -->
      <#assign classResponsive="cm-image--responsive" />
      <#assign attributes={"data-cm-responsive-image": bp.responsiveImageLinksData(self, limitAspectRatios![])!""} />
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
    <#assign attributes += {"alt": alt, "title": title} />

    <div class="cm-image cm-image--background ${classImage!""} ${classResponsive!""}" <@bp.renderAttr attributes />
      <@cm.metadata data=["properties.data"] />>
    </div>
  </#if>
</div>
