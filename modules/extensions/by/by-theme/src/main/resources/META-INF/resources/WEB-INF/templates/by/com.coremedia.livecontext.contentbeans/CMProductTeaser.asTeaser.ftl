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
	      <@cm.include self=self.teaserText!cm.UNDEFINED />
	    </#if>
		<a article-tracking href="${self.externalId!""}" target="_blank" >Buy on boots.com</a>
	</div>
<#elseif isTemplateFour>
       <div class="row">
          <h4>Get the look</h4>
       </div>
	<#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": lc.getAspectRatiosForTeaser(),
        "classBox": "col-md-12 col-lg-12 col-sm-12 col-xs-12 col2 imgDisplay"
      }/>
    </#if>
	<div class="col-md-12 col-lg-12 col-sm-12 col-xs-12 col2 vertDisplay">
		<@cm.include self=self.teaserText!cm.UNDEFINED />
		<a article-tracking href="${self.externalId!""}" target="_blank" >Buy on boots.com</a>
	</div>
</#if>