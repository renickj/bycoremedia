<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign ctaDisabled=bp.setting(self, "callToActionDisabled", false) />
<#assign ctaLabel=bp.setting(self, "callToActionCustomText", "") />
<#assign additionalCarouselClass=""/>

<div class="cm-superhero ${additionalClass!""}"<@cm.metadata self.content /> data-cm-module="superhero">
  <#-- picture -->
  <#if self.picture?has_content>
      <@cm.include self=self.picture view="asBackgroundImage"
      params={
      "classBox": "cm-superhero__image",
      "metadata": ["properties.pictures"]
      }/>
      <div class="cm-superhero__dimmer"></div>
  <#else>
    <div class="cm-superhero__image"></div>
  </#if>

  <#if (self.teaserTitle?has_content || self.teaserText?has_content)>
    <#-- with banderole -->
    <div class="cm-superhero__banderole row">
      <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
        <#-- headline -->
        <@bp.optionalLink href="${link}">
          <h1 class="cm-superhero__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        </@bp.optionalLink>
        <#-- teaser text -->
        <p class="cm-superhero__text"<@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "superhero.max.length", 140)) />
        </p>
        <#-- custom call-to-action button -->
        <#if link?has_content>
          <#if (!ctaDisabled && ctaLabel != "")>
          <div class="cm-superhero__button">
            <a class="cm-button cm-button--white btn" href="${link}" role="button">
              ${ctaLabel}
            </a>
          </div>
          <#-- default call-to-action button -->
          <#elseif (!ctaDisabled)>
            <div class="cm-superhero__button">
              <a class="cm-button cm-button--white btn" href="${link}" role="button">
              ${bp.getMessage("button_read_more")}
            </a>
          </div>
          </#if>
        </#if>
      </div>
    </div>
  <#-- button without banderole -->
  <#elseif link?has_content>
    <#-- custom call-to-action button -->
    <#if (!ctaDisabled && ctaLabel != "")>
      <div class="cm-superhero__cta">
        <a class="cm-button cm-button--white btn" href="${link}" role="button">
        ${ctaLabel}
        </a>
      </div>
    <#-- default call-to-action button -->
    <#elseif (!ctaDisabled)>
      <div class="cm-superhero__button">
        <a class="cm-button cm-button--white btn" href="${link}" role="button">
        ${bp.getMessage("button_read_more")}
        </a>
      </div>
    </#if>
  </#if>
</div>
