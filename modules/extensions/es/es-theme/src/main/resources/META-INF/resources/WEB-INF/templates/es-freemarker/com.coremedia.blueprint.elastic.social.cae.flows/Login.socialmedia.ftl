<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->
<#-- @ftlvariable name="classContainer" type="java.lang.String" -->

<#assign classContainer=cm.localParameters().classContainer!"" />

<#assign loginAction=self.loginAction!cm.UNDEFINED />
<#assign loginFlow=bp.substitute(loginAction.id!"", loginAction)!cm.UNDEFINED />
<#assign loginUrl=cm.getLink(loginFlow, {
  "absolute": true
}) />

<#assign registrationAction=self.registrationAction!cm.UNDEFINED />
<#assign registrationFlow=bp.substitute(registrationAction.id!"", registrationAction)!cm.UNDEFINED />
<#assign registerUrl=cm.getLink(registrationFlow, {
  "absolute": true
}) />

<#assign elasticSocialConfiguration=es.getElasticSocialConfiguration(cmpage) />
<#if elasticSocialConfiguration?has_content>
  <#if elasticSocialConfiguration.twitterAuthenticationEnabled || elasticSocialConfiguration.facebookAuthenticationEnabled>
    <div class="socialmedia ${classContainer}">
      <#assign tenant=es.getCurrentTenant() />
      <#if elasticSocialConfiguration.facebookAuthenticationEnabled>
        <#assign facebookUrl=cm.getLink('/connect/facebook_' + tenant)/>
        <form action="${facebookUrl!""}" method="post" class="cm-form-facebook">
          <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
          <input type="hidden" name="registerUrl" value="${loginUrl!""}"/>
          <input type="hidden" name="loginUrl" value="${loginUrl!""}"/>
          <input type="hidden" name="scope" value="email"/>
          <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
          <input type="hidden" name="forceRegister" value="false"/>
          <@bp.button text=bp.getMessage(es.messageKeys.LOGIN_WITH_FACEBOOK) iconClass="icon-facebook-social-full" attr={"type": "submit", "id": "facebookConnect"} />
        </form>
      </#if>

      <#if elasticSocialConfiguration.twitterAuthenticationEnabled>
        <#assign twitterUrl=cm.getLink('/connect/twitter_' + tenant)/>
        <form action="${twitterUrl!""}" method="post" class="cm-form-twitter">
          <input type="hidden" name="nextUrl" value="${nextUrl!""}"/>
          <input type="hidden" name="registerUrl" value="${loginUrl!""}"/>
          <input type="hidden" name="loginUrl" value="${loginUrl!""}"/>
          <input type="hidden" name="_CSRFToken" value="${_CSRFToken!""}"/>
          <input type="hidden" name="forceRegister" value="false"/>
          <@bp.button text=bp.getMessage(es.messageKeys.LOGIN_WITH_TWITTER) iconClass="icon-twitter-social-full" attr={"type": "submit", "id": "twitterConnect"} />
        </form>
      </#if>
    </div>
  </#if>
</#if>
