<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMArticle" -->
<#-- @ftlvariable name="templateClass" type="java.lang.String" -->

<#if isBlogTemplate?has_content && isBlogTemplate?is_boolean && isBlogTemplate>
	<#assign isBlogTemplate = true/>
</#if>
<#if isTemplateThree?has_content && isTemplateThree?is_boolean && isTemplateThree>
	<#assign isTemplateThree = true/>
</#if>
<#if isTemplateSix?has_content && isTemplateSix?is_boolean && isTemplateSix>
	<#assign isTemplateSix = true/>
</#if>
<#if isTemplateFour?has_content && isTemplateFour?is_boolean && isTemplateFour>
	<#assign isTemplateFour = true/>
</#if>


<header>
	<h1>${self.title!""}</h1>
	<#if isTemplateThree || isTemplateSix || isTemplateFour || isBlogTemplate>
		<#list self.heroItems as heroItem>
			<@cm.include self=heroItem view="asHeader" />
		  </#list>
	</#if>
	
</header>
			

