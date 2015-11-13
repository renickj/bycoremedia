<#-- @ftlvariable name="flowExecutionKey" scope="request" type="java.lang.String" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->

<#--
 Form styles and PBE not yet applied, because feature is not used
 -->
<#assign title><@bp.message es.messageKeys.CONFIRM_PASSWORD_RESET_TITLE /></#assign>
<@bp.notification type="success" dismissable=false title=title text="" />

<form method="post" class="cm-form" data-cm-form--reset="">
  <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
  <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
  <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
  <input type="hidden" name="_eventId_submit"/>

  <@bp.notificationFromSpring path="passwordReset" dismissable=false additionalClasses=["cm-form__notification"] />
  <fieldset class="cm-form__fieldset cm-fieldset">
    <div class="cm-fieldset__item cm-field">
      <@bp.notificationFromSpring path="passwordReset.currentPassword" dismissable=false additionalClasses=["cm-field__notification"] />
      <#assign labelCurrentPassword=bp.getMessage(es.messageKeys.CONFIRM_PASSWORD_RESET_CURRENT_PASSWORD_LABEL) />
      <@bp.fieldFromSpring path="passwordReset.currentPassword" additionalClasses=["cm-fieldset__item"] inputType="password" labelText=labelCurrentPassword inputPlaceholder=labelCurrentPassword inputAttr={"required":""} />
    </div>
    <div class="cm-fieldset__item cm-field">
      <@bp.notificationFromSpring path="passwordReset.password" dismissable=false additionalClasses=["cm-field__notification"] />
      <#assign labelPassword=bp.getMessage(es.messageKeys.CONFIRM_PASSWORD_RESET_PASSWORD_LABEL) />
      <@bp.fieldFromSpring path="passwordReset.password" additionalClasses=["cm-fieldset__item"] inputType="password" labelText=labelPassword inputPlaceholder=labelPassword inputAttr={"required":""} />
    </div>
    <div class="cm-fieldset__item cm-field">
      <@bp.notificationFromSpring path="passwordReset.confirmPassword" dismissable=false additionalClasses=["cm-field__notification"] />
      <#assign labelConfirmPassword=bp.getMessage(es.messageKeys.CONFIRM_PASSWORD_RESET_CONFIRM_PASSWORD_LABEL) />
      <@bp.fieldFromSpring path="passwordReset.confirmPassword" additionalClasses=["cm-fieldset__item"] inputType="password" labelText=labelConfirmPassword inputPlaceholder=labelConfirmPassword inputAttr={"required":""} />
    </div>
    <div class="cm-button-group cm-button-group--default">
      <@bp.button text="Reset" attr={"type": "submit", "classes": ["cm-button-group__button"]} />
    </div>
  </fieldset>
</form>
