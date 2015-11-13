<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="highlightingMap" type="java.util.Map" -->
<#-- @ftlvariable name="isLast" type="java.lang.Boolean" -->

<#assign cssClasses=cm.localParameters().islast!false?then(" is-last", "") />
<#assign highlightedItem=(highlightingMap.get(self))!{} />
<#assign teaserLength=bp.setting(cmpage, "teaser.max.length", 200)/>
<#assign htmlDescription=bp.truncateHighlightedText((highlightedItem["htmlDescription"][0])!self.htmlDescription!"", teaserLength) />
<#assign teaserText=bp.truncateHighlightedText((highlightedItem["teaserText"][0])!self.teaserText!"", teaserLength) />

<div class="cm-search__item ${cssClasses}"<@cm.metadata self.content />>
  <a href="${cm.getLink(self.data!self.target)}">
    <#-- image -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
        "limitAspectRatios": ["landscape_ratio4x3"],
        "classBox": "cm-search__picture-box",
        "classImage": "cm-search__picture",
        "metadata": ["properties.pictures"]
      }/>
    </#if>
    <div class="cm-search__caption">
      <#-- teaserTitle -->
      <h3<@cm.metadata "properties.teaserTitle" />>
        <@cm.unescape (highlightedItem["teaserTitle"][0])!self.teaserTitle />
        <@cm.include self=self view="infos" />
      </h3>
      <#-- htmlDescription or teaserText -->
      <#if htmlDescription?has_content>
        <p<@cm.metadata "properties.htmlDescription" />>
          <@bp.renderWithLineBreaks htmlDescription />
        </p>
      <#elseif teaserText?has_content>
        <p<@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks teaserText />
        </p>
      </#if>
    </div>
  </a>
</div>