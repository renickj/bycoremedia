<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign ctaDisabled=bp.setting(self, "callToActionDisabled", false) />
<#assign ctaLabel=bp.setting(self, "callToActionCustomText", "") />
<#assign additionalCarouselClass=""/>

<div class="cm-hero ${additionalClass!""}"<@cm.metadata self.content />>
  <#-- picture -->
  <#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": ["landscape_ratio4x3","landscape_ratio16x9"],
        "classBox": "cm-hero__picture-box",
        "classImage": "cm-hero__picture",
        "metadata": ["properties.pictures"]
      }/>
      <div class="cm-hero__dimmer"></div>
  <#else>
    <div class="cm-hero__picture-box" <@cm.metadata "properties.pictures" />>
      <div class="cm-hero__picture"></div>
    </div>
  </#if>

  <#if self.teaserTitle?has_content>
    <#-- with banderole -->
    <div class="cm-hero__banderole row">
      <div class="col-xs-10 col-xs-push-1">
        <#-- headline -->
        <@bp.optionalLink href="${link}">
          <h1 class="cm-hero__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        </@bp.optionalLink>
        <#-- teaser text -->
        <p class="cm-hero__text"<@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "hero.max.length", 140)) />
        </p>
        <#-- custom call-to-action button -->
        <#if link?has_content>
          <#if (!ctaDisabled && ctaLabel != "")>
          <div class="cm-hero__button">
            <a class="cm-button cm-button--white btn" href="${link}" role="button">
              ${ctaLabel}
            </a>
          </div>
          <#-- default call-to-action button -->
          <#elseif (!ctaDisabled)>
            <div class="cm-hero__button">
              <a class="cm-button cm-button--white btn" href="${link}" role="button">
              ${bp.getMessage("button_read_more")}
            </a>
          </div>
          </#if>
        </#if>
      </div>
    </div>
  </#if>
</div>
