<#-- @ftlvariable name="pViewOnly" type="java.lang.String" -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.action.webflow.WebflowActionState" -->
<#-- @ftlvariable name="flowExecutionKey" type="java.lang.String" -->
<#-- @ftlvariable name="userDetails" type="com.coremedia.blueprint.elastic.social.cae.flows.UserDetails" -->
<#-- @ftlvariable name="actionHandler" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="elasticSocialConfiguration" type="com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration" -->
<#-- @ftlvariable name="explicitInterests" type="com.coremedia.blueprint.personalization.forms.PersonalizationForm" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#-- content property of cmpage is CMAction -->
<#-- @ftlvariable name="actionContent" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#assign actionContent=cmpage.content />
<#assign itemId=bp.id((actionContent.content.id)!) />

<#assign actionHandler=self.action />

<div class="cm-box"<@cm.metadata data=[(self.action.content)!"", "properties.id"] />>

  <@cm.include self=actionHandler!cm.UNDEFINED view="headline" params={"classHeadline": "cm-box__header"} />

  <#if userDetails?has_content>

    <#if userDetails.viewOwnProfile>
      <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.USER_DETAILS_PERSONAL_DETAILS /></h3>
    </#if>
    <#if !userDetails.viewOwnProfile>
      <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.USER_DETAILS_TITLE /></h3>
    </#if>

    <div class="cm-box__content">
      <div class="cm-form">
        <fieldset class="cm-form__fieldset cm-fieldset">
          <#if userDetails.viewOwnProfile && userDetails.preModerationChanged>
            <div><@bp.message es.messageKeys.USER_DETAILS_CHANGES_FOR_PREMODERATION /></div>
          </#if>

          <@bp.fieldFromSpring path="userDetails.username" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_USERNAME) inputAttr={"disabled": ""} additionalClasses=["cm-fieldset__item cm-field--detail"] />

          <#if userDetails.viewOwnProfile || userDetails.preview>
            <@bp.fieldFromSpring path="userDetails.givenname" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_GIVENNAME) inputAttr={"disabled": ""} additionalClasses=["cm-fieldset__item cm-field--detail"] />

            <@bp.fieldFromSpring path="userDetails.surname" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_SURNAME) inputAttr={"disabled": ""} additionalClasses=["cm-fieldset__item cm-field--detail"] />

            <@bp.fieldFromSpring path="userDetails.emailAddress" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_EMAIL_ADDRESS) inputAttr={"disabled": ""} additionalClasses=["cm-fieldset__item cm-field--detail"] />

            <@bp.fieldFromSpring path="userDetails.receiveCommentReplyEmails" labelText=bp.getMessage(es.messageKeys.USER_DETAILS_RECEIVE_COMMENT_REPLY_EMAILS) inputType="checkbox" inputAttr={"disabled": ""} additionalClasses=["cm-fieldset__item cm-field--detail"] />
          </#if>

          <#--<div class="fieldwrapper">-->
            <#--<label for="localeLanguage"><@bp.message es.messageKeys.USER_DETAILS_LOCALE_LANGUAGE/></label>-->
            <#--<#if userDetails.localizedLocale?has_content>-->
              <#--<input disabled value="${userDetails.localizedLocale.displayLanguage}" id="localeLanguage" type="text">-->
            <#--</#if>-->
          <#--</div>-->
          <#--<div class="fieldwrapper">-->
            <#--<label for="timeZone"><@bp.message es.messageKeys.USER_DETAILS_TIME_ZONE/></label>-->
            <#--<#if userDetails.timeZoneId?has_content>-->
              <#--<input disabled value="${userDetails.timeZoneId}" id="timeZone" type="text">-->
            <#--</#if>-->
          <#--</div>-->

          <#--<#if userDetails.profileImageId?has_content>
            <div class="fieldwrapper">
              <label for="profileImage" class="profileImage"><@bp.message es.messageKeys.USER_DETAILS_PROFILE_IMAGE/></label>
              <#assign link=cm.getLink("/elastic/image/${userDetails.profileImageId}/${elasticSocialConfiguration.userImageWidth}/${elasticSocialConfiguration.userImageHeight}")/>
              <#assign userImageLinkPopup=cm.getLink("/elastic/image/${userDetails.profileImageId}", false)/>
              <a href="${userImageLinkPopup}?transform=false" class="lightbox lightboxHover">
                <img id="profileImage" src="${link}" title="${userDetails.username}" alt="userimage"/>
              </a>
            </div>
          </#if>-->

          <#if userDetails.viewOwnProfile>
            <form method="post" enctype="multipart/form-data">
              <input type="hidden" name="execution" value="${flowExecutionKey!""}"/>
              <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
              <input type="hidden" name="_eventId_editUser"/>
              <div class="cm-button-group cm-button-group--default">
                <@bp.button text=bp.getMessage(es.messageKeys.USER_DETAILS_EDIT_PROFILE) attr={"type": "submit", "id": "editUser", "classes": ["cm-button-group__button"]} />
              </div>
            </form>
          </#if>

          <#--
          <#if elasticSocialConfiguration?has_content && elasticSocialConfiguration.complainingEnabled && !userDetails.viewOwnProfile
                && (es.isAnonymousUser() || (!es.isAnonymousUser() && !userDetails.viewOwnProfile))>
            <#assign navigationId=bp.id((cmpage.navigation.content.id)!) />
            <@es.complaining id=userDetails.id collection="users"
            value=es.hasComplaintForCurrentUser(userDetails.id, "users")
            itemId=itemId navigationId=navigationId />
          </#if>
          -->
          </fieldset>
      </div>

      <#if userDetails.viewOwnProfile
                    && elasticSocialConfiguration?has_content
                    && ((elasticSocialConfiguration.twitterAuthenticationEnabled && !userDetails.connectedWithTwitter)
                    || (elasticSocialConfiguration.facebookAuthenticationEnabled && !userDetails.connectedWithFacebook))>
      </div>

      <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.USER_DETAILS_EXTERNAL_ACCOUNT /></h3>

      <div class="cm-box__content">

        <div class="cm-form">
          <fieldset class="cm-form__fieldset cm-fieldset">
            <#if elasticSocialConfiguration.facebookAuthenticationEnabled && !userDetails.connectedWithFacebook>
              <#assign facebookUrl=cm.getLink("/signin/facebook")/>
              <form action="${facebookUrl}" method="POST">
                <@bp.button text=bp.getMessage(es.messageKeys.USER_DETAILS_CONNECT_WITH_FACEBOOK) attr={"type": "submit", "id": "facebookConnect"} />
              </form>
            </#if>
            <#if elasticSocialConfiguration.twitterAuthenticationEnabled && !userDetails.connectedWithTwitter>
              <#assign twitterUrl=cm.getLink("/signin/twitter")/>
              <form action="${twitterUrl}" method="POST">
                <@bp.button text=bp.getMessage(es.messageKeys.USER_DETAILS_CONNECT_WITH_TWITTER) attr={"type": "submit", "id": "twitterConnect"} />
              </form>
            </#if>
          </fieldset>
        </div>
      </#if>

      <#--
      <h3 class="cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.USER_DETAILS_LOGGING/></h3>

      <div class="cm-form">
        <fieldset class="cm-form__fieldset cm-fieldset">
          <div class="cm-fieldset__item cm-field cm-field--detail">
            <label id="lastLoginDate" class="cm-field__name"><@bp.message es.messageKeys.USER_DETAILS_LAST_LOGIN_DATE/></label>
            <span class="cm-field__value">${userDetails.lastLoginDate!?datetime?string.long_full}</span>
          </div>
          <div class="cm-fieldset__item cm-field cm-field--detail">
            <label id="registrationDate" class="cm-field__name"><@bp.message es.messageKeys.USER_DETAILS_REGISTRATION_DATE/></label>
            <span class="cm-field__value">${userDetails.registrationDate!?datetime?string.long_full}</span>
          </div>
          <div class="cm-fieldset__item cm-field cm-field--detail">
            <label id="numberOfLogins" class="cm-field__name"><@bp.message es.messageKeys.USER_DETAILS_NUMBER_OF_LOGINS/></label>
            <span class="cm-field__value">${userDetails.numberOfLogins}</span>
          </div>
          <div class="cm-fieldset__item cm-field cm-field--detail">
            <label id="numberOfComments" class="cm-field__name"><@bp.message es.messageKeys.USER_DETAILS_NUMBER_OF_COMMENTS/></label>
            <span class="cm-field__value">${userDetails.numberOfComments}</span>
          </div>
          <div class="cm-fieldset__item cm-field cm-field--detail">
            <label id="numberOfRatings" class="cm-field__name"><@bp.message es.messageKeys.USER_DETAILS_NUMBER_OF_RATINGS/></label>
            <span class="cm-field__value">${userDetails.numberOfRatings}</span>
          </div>
          <div class="cm-fieldset__item cm-field cm-field--detail">
            <label id="numberOfLikes" class="cm-field__name"><@bp.message es.messageKeys.USER_DETAILS_NUMBER_OF_LIKES/></label>
            <span class="cm-field__value">${userDetails.numberOfLikes}</span>
          </div>
        </fieldset>
      </div>
      <#if userDetails.viewOwnProfile>
        <@cm.include self=self view="showPersonalizationForm"/>
      </#if>
      -->
    </div>
  <#else>
    <@bp.notification type="error" dismissable=false text=bp.getMessage(es.messageKeys.USER_DETAILS_NO_USER_FOUND) additionalClasses=["cm-box__content"] />
  </#if>
</div>