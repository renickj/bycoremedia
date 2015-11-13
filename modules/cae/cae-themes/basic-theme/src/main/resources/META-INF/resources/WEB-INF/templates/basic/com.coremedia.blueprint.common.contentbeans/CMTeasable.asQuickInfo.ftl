<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="classQuickInfo" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="quickInfoId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoModal" type="java.lang.Boolean" -->
<#-- @ftlvariable name="quickInfoGroup" type="java.lang.String" -->
<#-- @ftlvariable name="overlay" type="java.util.Map" -->

<#-- if overlay configuration is not set explicitly assert false for each key not set -->
<#assign overlay={
  "displayTitle": false,
  "displayShortText": false,
  "displayPicture": false
} + overlay!{} />
<#assign quickInfoData={} />
<#if quickInfoModal?has_content && quickInfoModal?is_boolean>
  <#assign quickInfoData=quickInfoData + {"modal": quickInfoModal!false} />
</#if>
<#if quickInfoGroup?has_content>
  <#assign quickInfoData=quickInfoData + {"group": quickInfoGroup!""} />
</#if>

<div id="${quickInfoId!bp.generateId("quickinfo")}" class="cm-quickinfo ${classQuickInfo!""}" <@cm.dataAttribute name="data-cm-quickinfo" data=quickInfoData /><@cm.metadata data=(metadata![])+[self.content]/>>

  <#-- image -->
  <#if overlay.displayPicture>
    <div class="cm-quickinfo__property cm-quickinfo__property--image">
      <@cm.include self=self.picture!cm.UNDEFINED params={
        "limitAspectRatios": [ "landscape_ratio4x3" ],
        "classBox": "cm-quickinfo__image cm-aspect-ratio-box",
        "classImage": "cm-aspect-ratio-box__content",
        "metadata": ["properties.pictures"]
      }/>
    </div>
  </#if>
  <#-- title -->
  <#assign showTitle=self.teaserTitle?has_content && overlay.displayTitle />
  <#assign showTeaserText=self.teaserText?has_content && overlay.displayShortText />
  <#if showTitle || showTeaserText>
    <div class="cm-quickinfo__property cm-quickinfo__property--general">
        <#-- title -->
      <#if showTitle>
        <h5 class="cm-quickinfo__title cm-heading5"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle}</h5>
      </#if>

    <#-- teaserText -->
      <#if showTeaserText>
        <div class="cm-quickinfo__text"<@cm.metadata "properties.teaserText" />><@cm.include self=self.teaserText!cm.UNDEFINED /></div>
      </#if>
    </div>
  </#if>

  <div class="cm-quickinfo__property cm-quickinfo__property--controls cm-button-group cm-button-group--linked-large">
    <@bp.button text=bp.getMessage("button_read_more") href=cm.getLink(self) attr={"classes": ["cm-button-group__button", "cm-button--linked-large"]} />
  </div>

  <@bp.button baseClass="" iconClass="cm-icon__symbol icon-close" iconText=bp.getMessage("button_close") attr={"class": "cm-quickinfo__close cm-icon"}/>
</div>
