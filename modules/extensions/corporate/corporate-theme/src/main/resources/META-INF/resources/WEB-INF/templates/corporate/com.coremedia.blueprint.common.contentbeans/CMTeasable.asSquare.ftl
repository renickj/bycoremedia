<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign ctaDisabled=bp.setting(self, "callToActionDisabled", false) />
<#assign ctaLabel=bp.setting(self, "callToActionCustomText", "") />

<div class="cm-square ${cssClasses}"<@cm.metadata self.content />>
  <div class="cm-square__wrapper">
    <@bp.optionalLink href="${link}">
      <#-- picture -->
      <#if self.picture?has_content>
        <@cm.include self=self.picture params={
          "limitAspectRatios": [ "portrait_ratio1x1" ],
          "classBox": "cm-square__picture-box",
          "classImage": "cm-square__picture",
          "metadata": ["properties.pictures"]
        }/>
        <div class="cm-square__dimmer"></div>
      <#else>
        <#assign buttonColor="" />
        <div class="cm-square__picture-box" <@cm.metadata "properties.pictures" />>
          <div class="cm-square__picture"></div>
        </div>
      </#if>
     
      <div class="cm-square__caption caption">
        <#-- teaser title -->
        <#if self.teaserTitle?has_content>  
          <h3 class="cm-square__headline" <@cm.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""}</span>
          </h3>
        </#if>
        <#-- teaser text -->
        <#if self.teaserText?has_content>
          <p class="cm-square__text" <@cm.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "square.max.length", 115)) />
          </p>
        </#if>
        <#-- custom call-to-action button -->
        <#if link?has_content>
          <#if (!ctaDisabled && ctaLabel != "")>
            <button class="cm-square__button cm-button cm-button--white btn btn-default">
            ${ctaLabel}
            </button>
          <#-- default call-to-action button -->
          <#elseif (!ctaDisabled)>
            <button class="cm-square__button cm-button cm-button--white btn btn-default">
            ${bp.getMessage("button_read_more")}
            </button>
          </#if>
        </#if>
      </div> 
    </@bp.optionalLink>
  </div>
</div>
