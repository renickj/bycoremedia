<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="flowExecutionKey" scope="request" type="java.lang.String" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->

<#--
 Form styles and PBE not yet applied, because feature is not used
 -->
<div class="cm-box"<@cm.metadata data=[(self.loginAction.content)!"", "properties.id"] />>
  <@cm.include self=self.action!cm.UNDEFINED view="headline" params={"classHeadline": "cm-box__header cm-headline--small"} />

  <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.PASSWORD_EXPIRED /></h3>
  <div class="cm-box__content">
  <#assign title><@bp.message es.messageKeys.CONFIRM_PASSWORD_RESET_TITLE /></#assign>
  <@bp.notification type="success" dismissable=false title=title text="" />
    <form method="post" class="cm-form" data-cm-form--reset="">
        <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
        <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
        <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
        <input type="hidden" name="_eventId_submit"/>

    <@bp.notificationFromSpring path="passwordExpired" dismissable=false additionalClasses=["cm-form__notification"] />
        <fieldset class="cm-form__fieldset cm-fieldset">
            <div class="cm-fieldset__item cm-field">
            <@bp.notificationFromSpring path="passwordExpired.currentPassword" dismissable=false additionalClasses=["cm-field__notification"] />
          <#assign labelCurrentPassword=bp.getMessage(es.messageKeys.CONFIRM_PASSWORD_RESET_CURRENT_PASSWORD_LABEL) />
          <@bp.fieldFromSpring path="passwordExpired.currentPassword" additionalClasses=["cm-fieldset__item"] inputType="password" labelText=labelCurrentPassword inputPlaceholder=labelCurrentPassword inputAttr={"required":""} />
            </div>
            <div class="cm-fieldset__item cm-field">
            <@bp.notificationFromSpring path="passwordExpired.password" dismissable=false additionalClasses=["cm-field__notification"] />
          <#assign labelPassword=bp.getMessage(es.messageKeys.CONFIRM_PASSWORD_RESET_PASSWORD_LABEL) />
          <@bp.fieldFromSpring path="passwordExpired.password" additionalClasses=["cm-fieldset__item"] inputType="password" labelText=labelPassword inputPlaceholder=labelPassword inputAttr={"required":""} />
            </div>
            <div class="cm-fieldset__item cm-field">
            <@bp.notificationFromSpring path="passwordExpired.confirmPassword" dismissable=false additionalClasses=["cm-field__notification"] />
          <#assign labelConfirmPassword=bp.getMessage(es.messageKeys.CONFIRM_PASSWORD_RESET_CONFIRM_PASSWORD_LABEL) />
          <@bp.fieldFromSpring path="passwordExpired.confirmPassword" additionalClasses=["cm-fieldset__item"] inputType="password" labelText=labelConfirmPassword inputPlaceholder=labelConfirmPassword inputAttr={"required":""} />
            </div>
            <div class="cm-button-group cm-button-group--default">
            <@bp.button text="Reset" attr={"type": "submit", "classes": ["cm-button-group__button"]} />
            </div>
        </fieldset>
    </form>
  </div>
</div>