<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->

{ "id":"${self.contentId}", "type":"collection", "viewTypeName":"${self.viewTypeName!""}", "items" : [

<#list self.items![] as item>
{
<#if item.picture?has_content>"picture":<@cm.include self=item.picture view="asJSONPicture"/></#if>
<#if item.subjectTaxonomy?has_content>
,"tags":[
		<#list item.subjectTaxonomy as tag>
				{ "value" :"${tag.value}","id" : "${tag.contentId}"}
			<#if tag_has_next>,</#if>
		</#list>
]
</#if>
}
<#if item_has_next>,</#if>
</#list>

]}