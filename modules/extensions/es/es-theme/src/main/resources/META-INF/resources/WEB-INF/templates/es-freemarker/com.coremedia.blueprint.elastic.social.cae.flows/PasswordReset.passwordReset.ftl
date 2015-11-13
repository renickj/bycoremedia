<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->

<#assign passwordResetAction=self.action />

<div class="cm-box"<@cm.metadata data=[(passwordResetAction.content)!"", "properties.id"]/>>

  <@cm.include self=passwordResetAction!cm.UNDEFINED view="headline" params={"classHeadline": "cm-box__header"} />

  <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.PASSWORD_RESET_TITLE /></h3>

  <div class="cm-box__content">

    <form method="post" class="cm-form" data-cm-form--forgot="">
      <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
      <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
      <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
      <input type="hidden" name="_eventId_submit"/>
      <fieldset class="cm-form__fieldset cm-fieldset">
        <#assign labelEmail=bp.getMessage(es.messageKeys.PASSWORD_RESET_EMAIL_LABEL) />
        <@bp.fieldFromSpring path="passwordReset.emailAddress" labelText=labelEmail inputPlaceholder=labelEmail inputAttr={"required":""} additionalClasses=["cm-fieldset__item"]  />
        <div class="cm-button-group cm-button-group--default">
          <@bp.button text=bp.getMessage(es.messageKeys.PASSWORD_RESET_BUTTON) attr={"type": "submit", "classes": ["cm-button-group__button"]} />
        </div>
      </fieldset>
    </form>
  </div>
</div>
