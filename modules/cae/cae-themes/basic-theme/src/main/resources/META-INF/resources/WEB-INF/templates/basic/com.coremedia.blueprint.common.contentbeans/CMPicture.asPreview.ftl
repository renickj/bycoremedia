<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<#if self.data?has_content>
  <#-- display the image uncropped in original size -->
  <#if self.disableCropping>
      <div class="toggle-item cm-preview-item" data-id="${bp.generateId("toggle")}">
          <a href="#" class="toggle-button cm-preview-item__headline">
            <@bp.message "Image_originalSize" />
          </a>
          <div class="toggle-container cm-preview-item__container">
            <@cm.include self=self view="details" />
          </div>
      </div>

  <#else>
    <#-- image as teaser -->
    <div class="toggle-item cm-preview-item" data-id="${bp.generateId("toggle")}">
        <a href="#" class="toggle-button cm-preview-item__headline">
          <@bp.message "Image_teaser" />
        </a>
        <div class="toggle-container cm-preview-item__container">
            <div class="cm-preview-content cm-clearfix">
                <div class="content">
                  <@cm.include self=self view="asTeaser" />
                </div>
            </div>
        </div>
    </div>
    <#-- display every aspect ratio as preview item -->
    <#assign allAspectRatios=bp.setting(cmpage, "responsiveImageSettings") />
    <#assign maxPreviewImageWidth=bp.setting(cmpage, "maxPreviewImageWidth", "400") />
    <#list allAspectRatios?keys as ratio>
        <div class="toggle-item cm-preview-item" data-id="${bp.generateId("toggle")}">
            <a href="#" class="toggle-button cm-preview-item__headline">
              <@bp.message "Image_"+ratio />
            </a>
            <div class="toggle-container cm-preview-item__container">
              <div class="cm-image-box cm-image-box--preview" style="max-width: ${maxPreviewImageWidth}px;">
                <@cm.include self=self params={
                  "limitAspectRatios": [ratio],
                  "crop": ratio,
                  "classBox": "cm-image-box__image"
                }/>
              </div>
            </div>
        </div>
    </#list>
  </#if>
</#if>
