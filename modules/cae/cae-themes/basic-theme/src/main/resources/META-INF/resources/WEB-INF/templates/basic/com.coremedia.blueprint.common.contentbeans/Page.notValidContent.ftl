<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<@cm.include self=self view="error" params={
    "errorHeadline": bp.getMessage("error.invalidContent.headline"),
    "errorText": bp.getMessage("error.invalidContent.text")
  } />