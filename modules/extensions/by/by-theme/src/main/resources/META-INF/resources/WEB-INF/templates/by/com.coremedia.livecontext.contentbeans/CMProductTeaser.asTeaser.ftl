<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMProductTeaser" -->

<#if isTemplateSix?has_content && isTemplateSix?is_boolean && isTemplateSix>
	<#assign template = "six"/>
<#elseif isBlogTemplate?has_content && isBlogTemplate?is_boolean && isBlogTemplate>
	<#assign template = "blog"/>
<#else><#-- Default: if no template has been set, we'll fall back to template four -->
<#--In the future, we might add more and need this if: elseifisTemplateFour?has_content && isTemplateFour?is_boolean && isTemplateFour>-->
	<#assign template = "four"/>
</#if>
<#if template == "six">
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": lc.getAspectRatiosForTeaser(),
        "classBox": "col-md-5 col-lg-5 col-sm-5 col-xs-12"
      }/>
    </#if>
	<div class="col-md-7 col-lg-7 col-sm-7 col col-xs-12">
		<h3>${self.teaserTitle!""}</h3>
	    <#if self.teaserText?has_content>
	      <@cm.include self=self.teaserText!cm.UNDEFINED />
	    </#if>
		<#if self.externalId?has_content>
			<a article-tracking href="${cm.getLink(self.target!cm.UNDEFINED)}" target="_blank" >Buy on boots.com</a>
		</#if>
	</div>
<#elseif template == "four">
	<#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": lc.getAspectRatiosForTeaser(),
        "classBox": "col-md-12 col-lg-12 col-sm-12 col-xs-12 col2 imgDisplay"
      }/>
    </#if>
	<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12 col2 vertDisplay">
		<@cm.include self=self.teaserText!cm.UNDEFINED />
		<#if self.externalId?has_content>
			<a article-tracking href="${cm.getLink(self.target!cm.UNDEFINED)}" target="_blank" >Buy on boots.com</a>
		</#if>
	</div>
<#elseif template == "blog">
	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
	<#if self.picture?has_content>
		<@cm.include self=self.picture params={
		"limitAspectRatios": lc.getAspectRatiosForTeaser(),
		"classBox": ""
		}/>
	</#if>
	<h3>${self.teaserTitle!""}</h3>
	<#if self.teaserText?has_content>
		<@cm.include self=self.teaserText!cm.UNDEFINED />
	</#if>
	<#if self.externalId?has_content>
		<a article-tracking href="${cm.getLink(self.target!cm.UNDEFINED)}" target="_blank" >Buy on boots.com</a>
	</#if>
	</div>
</#if>