<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.view.CollectionUnboxed" -->

<#if (self.view?has_content && self.view == "asPreview")>
  <@cm.include self=self view="asPreview" />
<#else>
  <#list self.delegate.items as item>
    <@cm.include self=item view=self.view!cm.UNDEFINED params={"metadata":[self.delegate.content, "properties.items"]}/>
  </#list>
</#if>