<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMArticle" -->
<#-- @ftlvariable name="templateClass" type="java.lang.String" -->

<#assign templateClass = ""/>
<#if self.related?has_content>
  <#assign classArticleRelated="row" />
</#if>

<#assign isVideoContent = false/>

<#if isTemplateOne?has_content && isTemplateOne?is_boolean && isTemplateOne>
	<#assign isVideoContent = true/>
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
	<#if isTemplateThree || isTemplateSix || isTemplateFour>
		<#list self.heroItems as heroItem>
			<@cm.include self=heroItem view="asHeader" />
		  </#list>
	</#if>
	
</header>
			

