<#ftl encoding="UTF-8">
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->
<#-- @ftlvariable name="imageMapId" type="java.lang.String" -->

<#-- if imageMapId is not given, generate new id -->
<#assign imageMapId=imageMapId!(bp.generateId("cm-map-")) />
<#assign quickInfoMainId=imageMapId + "-quickinfo--main" />

<#assign imageMapAreas=self.imageMapAreas />
<#assign croppingDisabled=(self.picture.disableCropping)!false />
<#if !croppingDisabled>
  <#assign imageMapAreas=bp.responsiveImageMapAreas(self) />
</#if>

  <#--imagemap with areas -->
<map <@bp.renderAttr { "name": imageMapId, "classes": ["cm-imagemap__areas"] } /> <@cm.dataAttribute name="data-cm-areas" data={"quickInfoMainId": quickInfoMainId} />>
  <#if imageMapAreas?has_content>
    <#list imageMapAreas![] as imageMapArea>
      <#if imageMapArea?has_content>
        <#assign quickInfoId=bp.generateId("cm-quickinfo-")/>

        <#if croppingDisabled>
          <#-- TODO: test image without cropping, rly working? -->
          <#assign dataCoords=imageMapArea.coords/>
        <#else>
          <#assign dataCoords=bp.responsiveImageMapAreaData(imageMapArea.coords)/>
        </#if>

        <#if imageMapArea.linkedContent?has_content>
          <#assign linkedContent=imageMapArea.linkedContent />
          <#assign link=cm.getLink(linkedContent!cm.UNDEFINED) />

          <#-- hot zones as areas -->
          <area shape="${imageMapArea.shape}"
                coords="0,0,0,0"
                <@cm.dataAttribute name="data-coords" data=dataCoords />
                href="${link}"
                class="cm-imagemap__area"
                data-quickinfo="${quickInfoId}"
                alt="${imageMapArea.alt!""}" />
          <#-- quickinfo marker-->
          <#if imageMapArea.displayAsInlineOverlay!false>
            <#assign classOverlay="" />
            <#assign theme=imageMapArea.inlineOverlayTheme!"" />
            <#-- only allow valid themes -->
            <#if (["dark", "light", "dark-on-light", "light-on-dark"]?seq_contains(theme))>
              <#assign classOverlay="cm-overlay--theme-" + theme />
            </#if>
            <div class="cm-imagemap__hotzone cm-imagemap__hotzone--text" data-quickinfo="${quickInfoId}">
              <@cm.include self=linkedContent!cm.UNDEFINED view="asOverlay" params={
                "classOverlay": classOverlay,
                "metadata": ["properties.localSettings"],
                "overlay": bp.setting(self, "overlay", {})
              } />
            </div>
          <#else>
            <@bp.button baseClass="" iconClass="icon-imagemap" iconText=(linkedContent.teaserTitle!bp.getMessage("button_quickinfo")) attr={"class": "cm-imagemap__hotzone cm-imagemap__hotzone--icon", "data-cm-button--quickinfo": '{"target": "${quickInfoId!""}"}'} />
          </#if>
          <#-- include quickinfo popup -->
          <@cm.include self=linkedContent view="asQuickInfo" params={
            "classQuickInfo": "cm-imagemap__quickinfo cm-quickinfo--compact",
            "metadata": ["properties.localSettings"],
            "quickInfoId": quickInfoId,
            "quickInfoGroup": imageMapId,
            "overlay": bp.setting(self, "overlay", {})
          } />
        </#if>
      </#if>
    </#list>
  <#else>
    <area shape="default" href="${cm.getLink(self.target!cm.UNDEFINED)}" class="cm-imagemap__area" />
  </#if>
  <#-- add main target as quickinfo and button -->
  <#if self.target?has_content>
    <div class="cm-imagemap__button-group cm-button-group cm-button-group--overlay">
      <@bp.button href=cm.getLink(self.target!cm.UNDEFINED) text=bp.getMessage("button_more_info") attr={"classes": ["cm-button-group__button"], "metadata": ["properties.target", self.target.content]} />
    </div>
    <@cm.include self=self.target!cm.UNDEFINED view="asQuickInfo" params={
      "classQuickInfo": "cm-imagemap__quickinfo cm-quickinfo--main cm-quickinfo--compact",
      "metadata": ["properties.target"],
      "quickInfoId": quickInfoMainId,
      "quickInfoGroup": imageMapId,
      "overlay": {
        "displayTitle": true,
        "displayShortText": true
      }
    } />
  </#if>
</map>
