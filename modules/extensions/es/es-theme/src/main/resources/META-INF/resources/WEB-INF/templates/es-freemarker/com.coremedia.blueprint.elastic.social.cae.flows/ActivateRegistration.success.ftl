<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="flowRequestContext" type="org.springframework.webflow.execution.RequestContext" -->

<#if flowRequestContext?has_content && flowRequestContext.messageContext?has_content>
  <#assign title=bp.getMessage(es.messageKeys.ACTIVATE_REGISTRATION_SUCCESS_TITLE) />
  <#list flowRequestContext.messageContext.allMessages![] as message>
    <@bp.notification type="success" dismissable=false title=title text=message.text!"" attr={
      "metadata": [(self.action.content)!"", "properties.id"]
    }/>
  </#list>
</#if>
