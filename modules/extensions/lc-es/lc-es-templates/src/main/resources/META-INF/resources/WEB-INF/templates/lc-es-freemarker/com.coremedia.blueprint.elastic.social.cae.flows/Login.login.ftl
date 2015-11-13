<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState" -->

<#assign loginAction=self.loginAction!cm.UNDEFINED />

<div class="cm-box"<@cm.metadata data=[loginAction.content!"", "properties.id"] />>

  <@cm.include self=loginAction view="headline" params={"classHeadline": "cm-box__header cm-headline--small"} />

  <h3 class="cm-box__header cm-heading3 cm-heading3--boxed"><@bp.message es.messageKeys.LOGIN_SIGN_IN /></h3>

  <div class="cm-box__content">

    <div class="cm-collection cm-collection--masonry cm-collection--tiles-login" data-masonry-options='{ "isInitLayout": false, "columnWidth": ".cm-collection--masonry > .cm-collection--masonry__grid-sizer", "gutter": ".cm-collection--masonry > .cm-collection--masonry__grid-gutter", "itemSelector": ".cm-collection--masonry > .cm-collection__item" }'>
      <@cm.include self=self view="loginForm" params={
        "classContainer": "cm-collection__item"
      } />

      <@cm.include self=self view="socialmedia" params={
        "classContainer": "cm-collection__item"
      } />
      <div class="cm-collection--masonry__grid-sizer"></div>
      <div class="cm-collection--masonry__grid-gutter"></div>
    </div>
  </div>
</div>

<@cm.include self=self view="signUp" />
