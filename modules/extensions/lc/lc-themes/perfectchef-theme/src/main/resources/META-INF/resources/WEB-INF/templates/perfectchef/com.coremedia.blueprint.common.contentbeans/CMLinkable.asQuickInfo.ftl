<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMLinkable" -->
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

<div id="${quickInfoId!bp.generateId("quickinfo")}" class="cm-quickinfo ${classQuickInfo!""}" <@cm.dataAttribute name="data-cm-quickinfo" data=quickInfoData /><@cm.metadata (metadata![]) + [self.content]/>>

  <#-- title -->
  <#assign showTitle=self.title?has_content && overlay.displayTitle />
  <#if showTitle>
    <div class="cm-quickinfo__property cm-quickinfo__property--general">
      <#-- title -->
      <h5 class="cm-quickinfo__title cm-heading5"<@cm.metadata "properties.title" />>${self.title}</h5>
    </div>
  </#if>

  <div class="cm-quickinfo__property cm-quickinfo__property--controls cm-button-group cm-button-group--linked-large">
    <@bp.button text=bp.getMessage("button_read_more") href=cm.getLink(self) attr={"classes": ["cm-button-group__button", "cm-button--linked-large"]} />
  </div>

  <@bp.button baseClass="" iconClass="cm-icon__symbol icon-close" iconText=bp.getMessage("button_close") attr={"class": "cm-quickinfo__close cm-icon"}/>
</div>
