<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- NOTE: Usage of the <#escape directive causes the url attributes NOT to be escaped -->
{ "id":"${self.contentId}", "type":"picture", "viewTypeName":"${self.viewTypeName!""}",
"teaserTitle":"${self.teaserTitle!""}", "teaserText":"<#if self.teaserText?has_content><#escape x as x?html><@cm.include self=self.teaserText view='plainJSON'/></#escape></#if>",
<#assign ars=bp.responsiveImageLinksData(self, limitAspectRatios![]) />

"imageurls":<#if ars?has_content>[${ars}]</#if>
}
