<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign ctaDisabled=bp.setting(self, "callToActionDisabled", false) />
<#assign ctaLabel=bp.setting(self, "callToActionCustomText", "") />

<div class="cm-text thumbnail ${cssClasses}"<@cm.metadata self.content />>
  <#-- headline -->
  <@bp.optionalLink href="${link}">
    <h3 class="cm-text__headline"<@cm.metadata "properties.teaserTitle" />>
      <span>${self.teaserTitle!""}</span>
    </h3>
  </@bp.optionalLink>
  <#-- teaser text, 9 lines ~ 600 chars -->
  <p class="cm-text__text"<@cm.metadata "properties.teaserText" />>
    <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "text.max.length", 600)) />
  </p>
  <#-- custom call-to-action button -->
  <#if link?has_content>
    <#if (!ctaDisabled && ctaLabel != "")>
      <a class="cm-text__button cm-button cm-button--primay btn" href="${link}">
      ${ctaLabel}
      </a>
    <#-- default call-to-action button -->
    <#elseif (!ctaDisabled)>
      <a class="cm-text__button cm-button cm-button--primay btn" href="${link}">
      ${bp.getMessage("button_read_more")}
      </a>
    </#if>
  </#if>
</div>
