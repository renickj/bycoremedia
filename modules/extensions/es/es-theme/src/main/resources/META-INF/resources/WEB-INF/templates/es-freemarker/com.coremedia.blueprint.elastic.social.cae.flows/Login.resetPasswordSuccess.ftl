<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="title" type="java.lang.String" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->

<#if !title?has_content>
  <#assign title=bp.getMessage(es.messageKeys.CONFIRM_PASSWORD_RESET_SUCCESS_TITLE) />
</#if>
<#if !text?has_content>
  <#assign text=bp.getMessage(es.messageKeys.CONFIRM_PASSWORD_RESET_SUCCESS) />
</#if>

<div class="cm-box">

  <@cm.include self=self.loginAction!cm.UNDEFINED view="headline" params={"classHeadline": "cm-box__header cm-headline--small"} />

  <@bp.notification type="success" dismissable=false title=title!"" text=text!"" additionalClasses=["cm-box__content"] attr={
    "metadata": [(self.action.content)!"", "properties.id"]
  } />
</div>