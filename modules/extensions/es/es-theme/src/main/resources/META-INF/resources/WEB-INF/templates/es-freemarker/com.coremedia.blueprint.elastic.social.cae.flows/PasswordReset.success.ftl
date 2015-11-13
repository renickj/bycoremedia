<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="loginAction" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#-- @ftlvariable name="emailAddress" type="java.lang.String" -->

<#assign passwordResetAction=self.action!cm.UNDEFINED />

<#assign loginAction=bp.setting(passwordResetAction, "flow.login", cm.UNDEFINED)/>
<#assign loginFlow=bp.substitute(loginAction.id!"", loginAction)!cm.UNDEFINED />
<#assign loginUrl=cm.getLink(loginFlow, {"next": nextUrl!""})/>

<div class="cm-box"<@cm.metadata data=[passwordResetAction.content!"", "properties.id"]/>>
  <@cm.include self=passwordResetAction view="headline" params={"classHeadline": "cm-box__header"} />
  <@bp.notification type="success" dismissable=false title=bp.getMessage(es.messageKeys.PASSWORD_RESET_TITLE) text=bp.getMessage(es.messageKeys.PASSWORD_RESET_SUCCESS, [emailAddress!""]) additionalClasses=["cm-box__content"] />

  <div class="cm-fieldset__item cm-field"><a href="${loginUrl!""}" class="cm-field__value--link">${loginAction.title!""}</a></div>

  <#-- We need this for calculating the correct next URL with the renderFragmentHrefs() function -->
  <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
</div>
