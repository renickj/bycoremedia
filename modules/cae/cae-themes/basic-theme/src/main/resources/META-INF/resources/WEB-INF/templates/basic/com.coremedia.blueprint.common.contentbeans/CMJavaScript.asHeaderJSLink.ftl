<#-- This template is used in <head/> if the javascript has some ieExpression -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMJavaScript" -->

<#if self.ieExpression?has_content>
    <!--[if ${self.ieExpression}]><script src="${cm.getLink(self)}"<@cm.metadata self.content />></script><![endif]-->
</#if>
