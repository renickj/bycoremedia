<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
{ "id":"${self.contentId}", "type":"teaser", "viewTypeName":"${self.viewTypeName!""}",
"teaserTitle":"${self.teaserTitle!""}", "teaserText":"<#if self.teaserText?has_content><@cm.include self=self.teaserText view='plainJSON'/></#if>"
<#if self.target?has_content>,"targetSegmentPath":"<@cm.include self=self.target view="asSegmentPath"/>"</#if>
<#if self.picture?has_content>,"picture":<@cm.include self=self.picture view="asJSONPicture"/></#if>
<#if self.subjectTaxonomy?has_content>
,"tags":[
	<#list self.subjectTaxonomy as tag>
		"${tag.value}"
		<#if tag_has_next>,</#if>
	</#list>
]
</#if>
,"masterVersion":"${self.masterVersion}"
}
