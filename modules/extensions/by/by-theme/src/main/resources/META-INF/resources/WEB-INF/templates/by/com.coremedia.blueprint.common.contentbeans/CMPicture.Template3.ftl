<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->


<@cm.include self=self params={
    "limitAspectRatios": lc.getAspectRatiosForTeaser(),
    "classBox": "cm-aspect-ratio-box",
    "classImage": "cm-aspect-ratio-box__content"
  }/>


