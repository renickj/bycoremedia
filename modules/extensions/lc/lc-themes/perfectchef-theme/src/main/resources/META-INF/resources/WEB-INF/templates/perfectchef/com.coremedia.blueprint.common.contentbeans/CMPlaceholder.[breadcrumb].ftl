<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->

<@cm.include self=cmpage.navigation!cm.UNDEFINED view="asBreadcrumb"
  params={
    "classBreadcrumb": "cm-placement-header__breadcrumb",
    "metadata": [self.content, "properties.id"]
  } />
