<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="classContainer" type="java.lang.String" -->

<#assign classContainer=cm.localParameters().classContainer!"" />
<#assign forgotPasswordAction=self.passwordResetAction!cm.UNDEFINED />
<#assign forgotPasswordUrl=cm.getLink(forgotPasswordAction, {"next": nextUrl})/>
<#assign formAction=cm.getLink(self)/>

<form method="post" action="${formAction}" class="cm-form ${classContainer}" data-cm-form--login="">
  <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
  <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
  <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
  <input type="hidden" name="_eventId_submit"/>
  <@bp.notificationFromSpring path="loginForm" dismissable=false additionalClasses=["cm-form__notification"] />
  <fieldset class="cm-form__fieldset cm-fieldset">
    <#assign labelName=bp.getMessage(es.messageKeys.LOGIN_NAME_LABEL)>
    <@bp.fieldFromSpring path="loginForm.name" additionalClasses=["cm-fieldset__item"] labelText=labelName inputPlaceholder=labelName inputAttr={"required":""} />
    <#assign labelPassword=bp.getMessage(es.messageKeys.LOGIN_PASSWORD_LABEL) />
    <@bp.fieldFromSpring path="loginForm.password" additionalClasses=["cm-fieldset__item"] inputType="password" labelText=labelPassword inputPlaceholder=labelPassword inputAttr={"required":""} />
    <#if forgotPasswordUrl?has_content>
      <div class="cm-fieldset__item cm-field"><a href="${forgotPasswordUrl!""}" class="cm-field__value--link"><@bp.message es.messageKeys.LOGIN_FORGOT_PASSWORD /></a></div>
    </#if>
    <div class="cm-button-group cm-button-group--default">
      <@bp.button text=bp.getMessage(es.messageKeys.LOGIN_SIGN_IN) attr={"type": "submit", "classes": ["cm-button-group__button"]} />
    </div>
  </fieldset>
</form>
