<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->
<#-- @ftlvariable name="nextUrl" type="java.lang.String" -->

<#assign registrationAction=self.registrationAction!cm.UNDEFINED />
<#assign registrationFlow=bp.substitute(registrationAction.id!"", registrationAction)!cm.UNDEFINED />

<#if registrationFlow?has_content>
  <#assign loginAction=self.loginAction!cm.UNDEFINED />
  <div class="cm-box"<@cm.metadata data=[loginAction.content!"", "properties.id"] />>
    <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.LOGIN_SIGN_UP /></h3>
    <div class="cm-box__content">
      <div class="cm-text">
        <#if loginAction.detailText?has_content>
          <@cm.include self=loginAction.detailText />
        </#if>
      </div>
      <div class="cm-button-group cm-button-group--default">
        <@bp.button text=bp.getMessage(es.messageKeys.LOGIN_SIGN_UP_BUTTON) href=cm.getLink(registrationFlow, {"next": nextUrl}) attr={"classes": ["cm-button-group__button"], "data-cm-button--signup": ""} />
      </div>
    </div>
  </div>
</#if>
