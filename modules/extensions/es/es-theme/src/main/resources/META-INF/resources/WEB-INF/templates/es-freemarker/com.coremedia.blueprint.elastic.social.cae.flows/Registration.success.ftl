<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="emailAddress" type="java.lang.String" -->
<#-- @ftlvariable name="flowRequestContext" type="org.springframework.webflow.execution.RequestContext" -->

<#assign registrationAction=self.action />
<div class="cm-box"<@cm.metadata data=[registrationAction.content, "properties.id"]/>>
  <@cm.include self=registrationAction!cm.UNDEFINED view="headline" params={"classHeadline": "cm-box__header"} />
  <@bp.notification type="success" dismissable=false title=bp.getMessage(es.messageKeys.REGISTRATION_TITLE) text=bp.getMessage(es.messageKeys.REGISTRATION_SUCCESS, [emailAddress!]) additionalClasses=["cm-box__content"] />
  <#if flowRequestContext?has_content && flowRequestContext.messageContext?has_content>
    <#list flowRequestContext.messageContext.allMessages![] as message>
      <@bp.notification type="warning" dismissable=true text=message.text!"" additionalClasses=["cm-box__content"] />
    </#list>
  </#if>
</div>
