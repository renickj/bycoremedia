<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<@cm.include self=(self.disclaimers.linkTooYoungPolicy)!cm.UNDEFINED />

<#assign registrationAction=self.action />
<div class="cm-box" <@cm.metadata data=[registrationAction.content, "properties.id"]/>>
  <form method="post" class="cm-box__content cm-form">
    <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
    <input type="hidden" name="_eventId_submit"/>
    <fieldset class="cm-form__fieldset cm-fieldset">
      <@bp.fieldFromSpring path="registration.acceptTooYoungPolicy" labelText=bp.getMessage(es.messageKeys.REGISTRATION_ACCEPT_TOO_YOUNG_POLICY_LABEL) additionalClasses=["cm-fieldset__item"] inputType="checkbox">
        <@bp.notificationFromSpring path="registration.acceptTooYoungPolicy" dismissable=true additionalClasses=["cm-field__notification"] />
      </@bp.fieldFromSpring>
      <div class="cm-button-group cm-button-group--default">
        <@bp.button text=bp.getMessage(es.messageKeys.REGISTRATION_TITLE) attr={"type": "submit", "classes": ["cm-button-group__button"]} />
      </div>
    </fieldset>
  </form>
</div>
