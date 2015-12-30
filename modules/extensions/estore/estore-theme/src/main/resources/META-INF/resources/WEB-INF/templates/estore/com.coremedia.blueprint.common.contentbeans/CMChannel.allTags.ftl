<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->
{
<#if self.subjectTaxonomy?has_content>
"tags":[
	<#list self.subjectTaxonomy as tag>
		"${tag.value}"
		<#if tag_has_next>,</#if>
	</#list>
]
</#if>
}