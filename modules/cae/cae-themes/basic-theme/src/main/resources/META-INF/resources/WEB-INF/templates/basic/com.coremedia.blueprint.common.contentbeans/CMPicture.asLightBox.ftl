<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->

<#assign title=self.title!"" />
<#-- get uncropped image as fullImage -->
<#-- todo: add setting for lightbox image aspectRatio and size -->
<#assign fullImageLink=bp.uncroppedImageLink(self) />

<div class="cm-lightbox ${classBox}">
  <a href="${fullImageLink}" title="${title}">
    <@cm.include self=self params={
    "limitAspectRatios": limitAspectRatios,
    "classBox": "cm-aspect-ratio-box",
    "classImage": "cm-aspect-ratio-box__content"
    }/>
  </a>
</div>
