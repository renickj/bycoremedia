<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#-- todo: simplify variables with ?then() -->
<#assign index=cm.localParameters().index!0 />
<#assign hasImage=self.picture?has_content />
<#assign hasEvenIndex=(index % 2 == 0) />
<#assign additionalVariantCssClass="" />
<#assign additionalButtonCssClass="" />
<#assign additionalNoImageCssClass="" />
<#assign additionalImgCssClass=""/>
<#assign additionalTextCssClass=""/>
<#assign ctaDisabled=bp.setting(self, "callToActionDisabled", false) />
<#assign ctaLabel=bp.setting(self, "callToActionCustomText", "") />
<#assign additionalCarouselClass=""/>
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#if !hasEvenIndex>
  <#assign additionalVariantCssClass="cm-teasable--alternative" />
<#else>
  <#assign additionalButtonCssClass="cm-button--white" />
</#if>
<#if !hasImage>
  <#assign additionalNoImageCssClass="cm-teasable--no-image" />
</#if>
<#if !hasEvenIndex && hasImage>
  <#assign additionalImgCssClass="col-sm-6"/>
  <#assign additionalTextCssClass="col-sm-6"/>
</#if>
<#if hasEvenIndex && hasImage>
  <#assign additionalImgCssClass="col-sm-6 col-sm-push-6"/>
  <#assign additionalTextCssClass="col-sm-6 col-sm-pull-6"/>
</#if>

<div class="cm-teasable ${additionalVariantCssClass} ${additionalNoImageCssClass} row ${additionalClass!""}"<@cm.metadata self.content />>
  <#if hasImage>
    <div class="col-xs-12 ${additionalImgCssClass}">
      <@bp.optionalLink href=link>
        <#-- picture -->
        <@cm.include self=self.picture params={
          "limitAspectRatios": [ "portrait_ratio1x1", "landscape_ratio16x9" ],
          "classBox": "cm-teasable__picture-box",
          "classImage": "cm-teasable__picture",
          "metadata": ["properties.pictures"]
        }/>
      </@bp.optionalLink>
    </div>
  </#if>
  <div class="col-xs-12 ${additionalTextCssClass}">
    <div class="cm-teasable__text-content-box">
      <div class="cm-teasable__text-content">
        <#-- headline -->
        <@bp.optionalLink href="${link}">
          <h3 class="cm-teasable__headline"<@cm.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""}</span>
          </h3>
        </@bp.optionalLink>
        <#-- teaser text -->
        <#assign truncatedTeaserText=bp.truncateText(self.teaserText!"", bp.setting(cmpage, "teaser.max.length", 140)) />
        <#if truncatedTeaserText?has_content>
          <p class="cm-teasable__text"<@cm.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks truncatedTeaserText />
          </p>
        </#if>
        <#-- custom call-to-action button -->
        <#if link?has_content>
          <#if (!ctaDisabled && ctaLabel != "")>
            <a class="cm-teasable__button cm-button ${additionalButtonCssClass} btn btn-default" href="${link}">
              ${ctaLabel}
            </a>
          <#-- default call-to-action button -->
          <#elseif (!ctaDisabled)>
            <a class="cm-teasable__button cm-button ${additionalButtonCssClass} btn btn-default" href="${link}">
              ${bp.getMessage("button_read_more")}
            </a>
          </#if>
        </#if>
      </div>
    </div>
  </div>
</div>
