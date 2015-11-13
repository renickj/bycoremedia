<#-- @ftlvariable name="pViewOnly" type="java.lang.String" -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="userDetails" type="com.coremedia.blueprint.elastic.social.cae.flows.UserDetails" -->
<#-- @ftlvariable name="actionHandler" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="explicitInterests" type="com.coremedia.blueprint.personalization.forms.PersonalizationForm" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#assign actionHandler=self.action />

<div class="cm-box"<@cm.metadata data=[(self.action.content)!"", "properties.id"] />>

  <@cm.include self=actionHandler!cm.UNDEFINED view="headline" params={"classHeadline": "cm-box__header"} />

  <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.USER_DETAILS_PERSONAL_DETAILS /></h3>

  <#if userDetails?has_content>
    <div class="cm-box__content">

      <form method="post" enctype="multipart/form-data" class="cm-form">
        <input type="hidden" name="_CSRFToken" value="${_CSRFToken}"/>
        <input type="hidden" name="execution" value="${flowExecutionKey}"/>
        <input type="hidden" name="_eventId_saveUser"/>
        <@bp.notificationFromSpring path="userDetails" dismissable=false />

        <fieldset class="cm-form__fieldset cm-fieldset">

          <#--
          <@spring.bind "userDetails" />

          <div class="cm-fieldset__item cm-field cm-field--detail">-->
            <#--<label for="username" class="cm-field__name"><@bp.message es.messageKeys.USER_DETAILS_USERNAME/></label>-->
            <#--<@spring.formInput path="userDetails.username" attr={"class": "cm-field__value"}/>-->
            <#--<div class="cm-field__notification"><#list spring.status.errorMessages as error><b>${error}</b><br /></#list></div>-->
          <#--</div>-->
          <#if userDetails.viewOwnProfile || userDetails.preview>

            <@bp.fieldFromSpring path="userDetails.givenname" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_GIVENNAME) inputAttr={"required": ""} additionalClasses=["cm-fieldset__item", "cm-field--detail"]>
              <@bp.notificationFromSpring path="userDetails.givenname" dismissable=true additionalClasses=["cm-field__notification"] />
            </@bp.fieldFromSpring>

            <@bp.fieldFromSpring path="userDetails.surname" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_SURNAME) inputAttr={"required": ""} additionalClasses=["cm-fieldset__item", "cm-field--detail"]>
              <@bp.notificationFromSpring path="userDetails.surname" dismissable=true additionalClasses=["cm-field__notification"] />
            </@bp.fieldFromSpring>

            <@bp.fieldFromSpring path="userDetails.emailAddress" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_EMAIL_ADDRESS) inputAttr={"required": ""} additionalClasses=["cm-fieldset__item", "cm-field--detail"]>
              <@bp.notificationFromSpring path="userDetails.emailAddress" dismissable=true additionalClasses=["cm-field__notification"] />
            </@bp.fieldFromSpring>

            <@bp.fieldFromSpring path="userDetails.receiveCommentReplyEmails" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_RECEIVE_COMMENT_REPLY_EMAILS) inputType="checkbox" additionalClasses=["cm-fieldset__item", "cm-field--detail"]>
              <@bp.notificationFromSpring path="userDetails.receiveCommentReplyEmails" dismissable=true additionalClasses=["cm-field__notification"] />
            </@bp.fieldFromSpring>
          </#if>

          <#--
          <div class="cm-fieldset__item cm-field cm-field--detail">
            <@spring.bind "userDetails.localeLanguage"/>
            <label for="localeLanguage"><@bp.message es.messageKeys.USER_DETAILS_LOCALE_LANGUAGE/></label>
            <@spring.formSingleSelect("loginForm.name", locales) />
            <#list spring.status.errorMessages as error><b>${error}</b><br /></#list>--
            <select name="" value>
              <option value=""><@bp.message es.messageKeys.USER_DETAILS_CHOOSE/></form:option>
              <options items="${locales}" itemLabel="displayLanguage"/>
            </select>
          </div>
          <div class="cm-fieldset__item cm-field cm-field--detail">
            <@spring.bind "userDetails.timeZoneId"/>
            <label for="timeZoneId"><@bp.message es.messageKeys.USER_DETAILS_TIME_ZONE/></label>
            <@spring.formSingleSelect("loginForm.name", locales) />
            <#list spring.status.errorMessages as error><b>${error}</b><br /></#list>

            <form:select id="timeZoneId" path="timeZoneId">
              <form:option value=""><@bp.message es.messageKeys.USER_DETAILS_CHOOSE/></form:option>
              <form:options items="${timeZones}" itemLabel="ID" itemValue="ID"/>
            </form:select>
          </div>

          <#if userDetails.profileImageId??>
            <div class="cm-fieldset__item cm-field cm-field--detail">
              <label id="profileImageLabel" class="profileImage"><@bp.message es.messageKeys.USER_DETAILS_PROFILE_IMAGE/></label>
              <#assign link><@cae.link target="/elastic/image/${userDetails.profileImageId}/${elasticSocialConfiguration.userImageWidth}/${elasticSocialConfiguration.userImageHeight}" /></#assign>
              <img id="profileImage" src="${link}" title="" alt="userimage"/>
            </div>

            <div class="fieldwrapper checkbox">
              <label for="deleteProfileImage"><@bp.message es.messageKeys.USER_DETAILS_DELETE_PROFILE_IMAGE/></label>
              <@spring.formCheckbox "userDetails.deleteProfileImage"/>
              <#list spring.status.errorMessages as error><b>${error}</b><br /></#list>
            </div>
          </#if>

          <div class="cm-fieldset__item cm-field cm-field--detail">
            <@spring.bind "userDetails.imageFile"/>
            <label for="imageFile"><@bp.message es.messageKeys.USER_DETAILS_IMAGE_FILE/></label>
            <input type="file" accept="image/*" name="${spring.status.expression}" value="${spring.status.value?default('')}"/>
            <#list spring.status.errorMessages as error><b>${error}</b><br /></#list>

          <#--
            <form:errors path="profileImageId" cssClass="notification error" element="div"/>
            <label for="imageFile"><@bp.message es.messageKeys.USER_DETAILS_ADD_NEW_IMAGE/></label>
            <input id="imageFile" name="imageFile" type="file" value="" accept="image/*"/>
          </div>-->

          <div class="cm-fieldset__item cm-field cm-field--detail">
            <span class="cm-field__value"><@bp.message es.messageKeys.USER_DETAILS_CHANGE_PASSWORD /></span>
          </div>

          <@bp.fieldFromSpring path="userDetails.password" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_PASSWORD) inputType="password" additionalClasses=["cm-fieldset__item", "cm-field--detail"]>
            <@bp.notificationFromSpring path="userDetails.password" dismissable=true additionalClasses=["cm-field__notification"] />
          </@bp.fieldFromSpring>

          <@bp.fieldFromSpring path="userDetails.newPassword" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_NEW_PASSWORD) inputType="password" additionalClasses=["cm-fieldset__item", "cm-field--detail"]>
            <@bp.notificationFromSpring path="userDetails.newPassword" dismissable=true additionalClasses=["cm-field__notification"] />
          </@bp.fieldFromSpring>

          <@bp.fieldFromSpring path="userDetails.newPasswordRepeat" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_NEW_PASSWORD_REPEAT) inputType="password" additionalClasses=["cm-fieldset__item", "cm-field--detail"]>
            <@bp.notificationFromSpring path="userDetails.newPasswordRepeat" dismissable=true additionalClasses=["cm-field__notification"] />
          </@bp.fieldFromSpring>

        <#--<@bp.button text=bp.getMessage(es.messageKeys.USER_DETAILS_DELETE_PROFILE) href="#" params={"class": "cm-button", "data-cm-button--confirm": "{\"message\":\"" + bp.getMessage(es.messageKeys.USER_DETAILS_DELETE_QUESTION) + "\"}", "id": "deleteUser", "name": "_eventId_deleteUser"} />-->
          <div class="cm-fieldset__item cm-button-group cm-button-group--default">
            <@bp.button text=bp.getMessage(es.messageKeys.USER_DETAILS_SAVE_PROFILE) attr={"type": "submit", "id": "saveUser", "classes": ["cm-button-group__button"]} />
            <#--
            ignored for now as it clashes with required attribute if input fields are empty
            if activated _eventId_cancel + _eventId_saveUser will be set both!
            hidden field must be removed and save button needs name property _eventId_saveUser again
            this will also prevent UI tests from working
            <@bp.button text=bp.getMessage(es.messageKeys.USER_DETAILS_CANCEL) attr={"name": "_eventId_cancel", "id": "cancel", "classes": ["cm-button-group__button"]} />
            -->
          </div>
        </fieldset>
      </form>
    </div>
  <#else>
    <@bp.notification type="error" dismissable=false text=bp.getMessage(es.messageKeys.USER_DETAILS_NO_USER_FOUND) additionalClasses=["cm-box__content"] />
  </#if>
</div>