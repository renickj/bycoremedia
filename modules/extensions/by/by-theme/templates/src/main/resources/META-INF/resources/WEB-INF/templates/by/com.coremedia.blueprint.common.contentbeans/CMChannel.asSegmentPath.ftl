<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->
<#if self.parentNavigation?has_content><@cm.include self=self.parentNavigation view="asSegmentPath"/></#if>/${self.segment}
