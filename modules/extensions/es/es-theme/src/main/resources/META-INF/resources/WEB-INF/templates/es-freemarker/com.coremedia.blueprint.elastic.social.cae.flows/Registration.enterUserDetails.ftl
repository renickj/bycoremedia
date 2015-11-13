<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="registrationFlow" type="com.coremedia.blueprint.elastic.social.cae.flows.Registration" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<#assign termsOfUseLink=cm.getLink(self.disclaimers.linkTermsOfUse!cm.UNDEFINED)/>
<#assign privacyPolicyLink=cm.getLink(self.disclaimers.linkPrivacyPolicy!cm.UNDEFINED)/>
<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage)/>
<#assign registrationAction=self.registrationAction!cm.UNDEFINED />
<#assign registrationFlow=bp.substitute(registrationAction.id!"", registrationAction)!cm.UNDEFINED />

<div class="cm-box"<@cm.metadata data=[registrationAction.content!"", "properties.id"]/>>

  <@cm.include self=registrationAction view="headline" params={"classHeadline": "cm-box__header"} />

  <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.REGISTRATION_REGISTER /></h3>

  <div class="cm-box__content">

    <form method="post" enctype="multipart/form-data" class="cm-form" data-cm-form--registration="">
      <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
      <@bp.hiddenFieldFromSpring path="registration.timeZoneId" attr={"name": "timeZoneId", "id": "timezone"} />
      <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
      <input type="hidden" name="_eventId_submit"/>
      <@bp.notificationFromSpring path="registration" dismissable=false additionalClasses=["cm-form__notification"] />
      <fieldset class="cm-form__fieldset cm-fieldset">
        <@bp.fieldFromSpring path="registration.username" inputPlaceholder=bp.getMessage(es.messageKeys.REGISTRATION_USERNAME_LABEL) additionalClasses=["cm-fieldset__item"] inputAttr={"required":""} />
        <div class="cm-fieldset__item cm-field-group">
          <@bp.fieldFromSpring path="registration.givenname" inputPlaceholder=bp.getMessage(es.messageKeys.REGISTRATION_GIVENNAME_LABEL) additionalClasses=["cm-field-group__item"] inputAttr={"required":""} />
          <@bp.fieldFromSpring path="registration.surname" inputPlaceholder=bp.getMessage(es.messageKeys.REGISTRATION_SURNAME_LABEL) additionalClasses=["cm-field-group__item"] inputAttr={"required":""} />
        </div>
        <div class="cm-fieldset__item cm-field-group">
          <@bp.fieldFromSpring path="registration.emailAddress" inputPlaceholder=bp.getMessage(es.messageKeys.REGISTRATION_EMAIL_ADDRESS_LABEL) additionalClasses=["cm-field-group__item"] inputType="email" inputAttr={"required":""} />
          <@bp.fieldFromSpring path="registration.birthdate" inputPlaceholder=bp.getMessage(es.messageKeys.REGISTRATION_BIRTHDATE_LABEL) additionalClasses=["cm-field-group__item"] inputType="date" inputAttr={"required":""} />
        </div>
        <#if !(registrationFlow.registeringWithProvider!false)>
          <div class="cm-fieldset__item cm-field-group">
            <@bp.fieldFromSpring path="registration.password" inputPlaceholder=bp.getMessage(es.messageKeys.REGISTRATION_PASSWORD_LABEL) additionalClasses=["cm-field-group__item"] inputType="password" inputAttr={"required":""} />
            <@bp.fieldFromSpring path="registration.confirmPassword" inputPlaceholder=bp.getMessage(es.messageKeys.REGISTRATION_CONFIRM_PASSWORD_LABEL) additionalClasses=["cm-field-group__item"] inputType="password" inputAttr={"required":""} />
          </div>
        </#if>
        <#--
        <div>
          <@spring.bind "registration.profileImage"/>
          <label for="imageFile"><@bp.message es.messageKeys.REGISTRATION_IMAGE_FILE_LABEL /></label>
          <input name="imageFile" type="file" value="" accept="image/*"/>
        </div>
        <#if registration?has_content && registration.profileImage?has_content>
            <#assign imageUrl=cm.getLink(registration.profileImage)/>
            <div>
                <img src="${imageUrl!""}" alt="userimage"/><br />
                <@spring.formCheckbox bp.getMessage(es.messageKeys.REGISTRATION_DELETE_PROFILE_IMAGE) />
                <label for="deleteProfileImage"><@bp.message es.messageKeys.REGISTRATION_DELETE_PROFILE_IMAGE /></label>
            </div>
        </#if>
        -->
        <#assign privacyPolicy><a href="${privacyPolicyLink!""}" target="_blank"><@bp.message es.messageKeys.REGISTRATION_LINK_PRIVACY_POLICY_LABEL /></a></#assign>
        <#assign termsOfUse><a href="${termsOfUseLink}" target="_blank"><@bp.message es.messageKeys.REGISTRATION_LINK_TERMS_OF_USE_LABEL /></a></#assign>

        <#-- TODO consider better solution -->
        <#assign output><@bp.fieldFromSpring path="registration.acceptTermsOfUse" additionalClasses=["cm-fieldset__item"] labelText=bp.getMessage(es.messageKeys.REGISTRATION_ACCEPT_TERMS_OF_USE_LABEL, ["#privacyPolicy#", "#termsOfUse#"]) inputType="checkbox">
          <@bp.notificationFromSpring path="registration.acceptTermsOfUse" additionalClasses=["cm-field__notification"] dismissable=true />
        </@bp.fieldFromSpring></#assign>
        <@cm.unescape output?replace("#privacyPolicy#", privacyPolicy)?replace("#termsOfUse#", termsOfUse) />

        <#if elasticSocialConfiguration.captchaForRegistrationRequired!false>
          <script src="http://www.google.com/recaptcha/api/challenge?k=${elasticSocialConfiguration.captchaPublicKey!""}"></script>
        </#if>

        <div class="cm-button-group cm-button-group--default">
          <@bp.button text=bp.getMessage(es.messageKeys.REGISTRATION_TITLE) attr={"type": "submit", "classes": ["cm-button-group__button"]} />
        </div>
      </fieldset>
    </form>
  </div>
</div>
