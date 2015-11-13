<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.data!cm.UNDEFINED) />
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
        <div class="cm-square__picture-box" <@cm.metadata "properties.pictures" />>
          <div class="cm-square__picture"></div>
        </div>
      </#if>
     
      <div class="cm-square__caption caption">
        <#-- teaser title -->
        <h3 class="cm-square__headline" <@cm.metadata "properties.teaserTitle" />>
          <span>
            <#if link?has_content>
              <i class="glyphicon glyphicon-download" aria-hidden="true"></i>
            </#if>
            ${self.teaserTitle!""}
          </span>
        </h3>
        <#-- teaser text -->
        <p class="cm-square__text" <@cm.metadata "properties.teaserText" />>
          <@cm.include self=self view="infos" /><br/>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "square.max.length", 115)) />
        </p>
        <#-- custom call-to-action button -->
        <#if link?has_content>
          <#if (!ctaDisabled && ctaLabel != "")>
            <button class="cm-square__button cm-button cm-button--white btn btn-default">
            ${ctaLabel}
            </button>
          <#-- default call-to-action button -->
          <#elseif (!ctaDisabled)>
            <button class="cm-square__button cm-button cm-button--white btn btn-default">
            ${bp.getMessage("button_download")}
            </button>
          </#if>
        </#if>
      </div> 
    </@bp.optionalLink>
  </div>
</div>
