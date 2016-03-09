<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMArticle" -->
{ "id":"${self.contentId}", "type":"article", "viewTypeName":"${self.viewTypeName!""}",
"teaserTitle":"${self.teaserTitle!""}", "teaserText":"<#if self.teaserText?has_content><@cm.include self=self.teaserText view='plainJSON'/></#if>"
<#if self.target?has_content>,"targetSegmentPath":"<@cm.include self=self.target view="asSegmentPath"/>"</#if>
<#if self.picture?has_content>,"teaserPicture":<@cm.include self=self.picture view="asJSONPicture"/></#if>
<#if self.thumbnails?has_content>,"homepagePicture":<@cm.include self=self.thumbnails view="asJSONPicture"/></#if>
<#if self.subjectTaxonomy?has_content>
,"tags":[
	<#list self.subjectTaxonomy as tag>
			{ "value" :"${tag.value}","id" : "${tag.contentId}"}
		<#if tag_has_next>,</#if>
	</#list>
]
</#if>
}
