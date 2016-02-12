<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMProductTeaser" -->

<#if isTemplateSix?has_content && isTemplateSix?is_boolean && isTemplateSix>
	<#assign isTemplateSix = true/>
</#if>
<#if isTemplateFour?has_content && isTemplateFour?is_boolean && isTemplateFour>
	<#assign isTemplateFour = true/>
</#if>
<#if isTemplateSix>
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": lc.getAspectRatiosForTeaser(),
        "classBox": "col-md-5 col-lg-5 col-sm-5 col-xs-12"
      }/>
    </#if>
	<div class="col-md-7 col-lg-7 col-sm-7 col col-xs-12">
		<h3>${self.teaserTitle!""}</h3>
	    <#if self.teaserText?has_content>
	      <p>${self.teaserText}</p>
	    </#if>
		<#if self.externalId?has_content>
			<a article-tracking href="${self.externalId!""}" target="_blank" class="btn primary">Add to basket</a>
	  	</#if>
	</div>
<#elseif isTemplateFour>
	<#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": lc.getAspectRatiosForTeaser(),
        "classBox": "col-md-12 col-lg-12 col-sm-12 col-xs-12 col2 imgDisplay"
      }/>
    </#if>
	<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12 col2 vertDisplay">
		<p><#escape x as x?html>${self.teaserText?substring(102,self.teaserText?length-10)!""}</#escape></p>
		<#if self.bootsUrl?has_content>
			<a article-tracking href="${self.bootsUrl}" target="_blank" class="btn primary">Buy On boots.com</a>
	  	</#if>
	</div>
</#if>