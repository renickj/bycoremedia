<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->
{
"id":"${self.contentId}", "type":"channel", "viewTypeName":"${self.viewTypeName!""}",
"teaserTitle":"${self.teaserTitle!""}", "teaserText":"<#if self.teaserText?has_content><@cm.include self=self.teaserText view='plainJSON'/></#if>"
<#if self.target?has_content>,"targetSegmentPath":"<@cm.include self=self.target view="asSegmentPath"/>"</#if>
<#if self.picture?has_content>,"picture":<@cm.include self=self.picture view="asJSONPicture"/></#if>
<#assign config=self.getSettingMap("config")!cm.UNDEFINED/><#if config!=cm.UNDEFINED>,"config":<@cm.include self=config view='plainJSON'/></#if>
}