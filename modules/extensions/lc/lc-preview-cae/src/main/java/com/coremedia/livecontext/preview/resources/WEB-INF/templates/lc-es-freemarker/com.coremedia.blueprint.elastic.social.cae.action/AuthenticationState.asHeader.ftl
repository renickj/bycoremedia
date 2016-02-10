<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="profileAction" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- @ftlvariable name="loginLink" type="java.lang.String" -->
<#-- @ftlvariable name="logoutLink" type="java.lang.String" -->

<#assign loginAction=self.loginAction />
<#assign logoutAction=self.logoutAction />
<#assign profileAction=self.profileAction />

<#if loginAction?has_content && logoutAction?has_content && profileAction?has_content>
  <#assign logoutLink=cm.getLink(logoutAction)/>
  <#if self.authenticated>
    <div class="cm-placement-header__item cm-icon cm-icon--logout"<@cm.metadata data=[logoutAction.content, "properties.id"] />>
      <a href="${logoutLink}" title="<@bp.message es.messageKeys.LOGOUT_TITLE />">
        <i class="cm-icon__symbol icon-profile-unlocked-alternative"></i>
        <span class="cm-icon__info cm-visuallyhidden"><@bp.message es.messageKeys.LOGOUT_TITLE /></span>
      </a>
    </div>
    <div class="cm-placement-header__item cm-icon cm-icon--user-details"<@cm.metadata data=[profileAction.content, "properties.id"] />>
      <a href="${cm.getLink(profileAction)}" title="<@bp.message es.messageKeys.USER_DETAILS_TITLE />">
        <i class="cm-icon__symbol icon-profile-unlocked"></i>
        <span class="cm-icon__info cm-visuallyhidden"><@bp.message es.messageKeys.USER_DETAILS_TITLE /></span>
      </a>
    </div>
  <#else>
    <div class="cm-placement-header__item cm-icon cm-icon--login"<@cm.metadata data=[loginAction.content, "properties.id"] />>
      <#assign loginLink=cm.getLink(self, {"next": "$nextUrl$", "absolute": true, "scheme": lc.getSecureScheme()})/>
      <a data-href="${loginLink}" title="<@bp.message es.messageKeys.LOGIN_TITLE />">
        <i class="cm-icon__symbol icon-profile-locked"></i>
        <span class="cm-icon__info cm-visuallyhidden"><@bp.message es.messageKeys.LOGIN_TITLE /></span>
      </a>
    </div>
  </#if>
</#if>