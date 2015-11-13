<#-- This template is used if cae.use.local.resources or cae.developer.mode are set to true or ieExpression is set. -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCSS" -->

<#assign cssLink=self.dataUrl/>
<#if !cssLink?has_content>
    <#assign cssLink=cm.getLink(self)/>
</#if>
<#if self.ieExpression?has_content>
    <!--[if ${self.ieExpression}]><link rel="stylesheet" href="${cssLink}"<@cm.metadata self.content />/><![endif]-->
<#else>
    <link rel="stylesheet" href="${cssLink}"<@cm.metadata self.content />/>
</#if>
