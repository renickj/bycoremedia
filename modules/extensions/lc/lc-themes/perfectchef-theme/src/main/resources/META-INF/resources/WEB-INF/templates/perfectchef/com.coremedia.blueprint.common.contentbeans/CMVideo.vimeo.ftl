<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classVideo" type="java.lang.String" -->

<#assign playerId=bp.generateId("cm-video-") />

<#if self.dataUrl?has_content>
  <#assign dataUrl=self.dataUrl?split("/") />
  <#assign length=dataUrl?size />
  <#assign videoId=dataUrl[length - 1] />

  <div class="cm-non-adaptive-content ${classVideo}" data-cm-non-adaptive-content='{"overflow": "false"}'>
    <iframe src="//player.vimeo.com/video/${videoId}?title=0&amp;byline=0&amp;portrait=0&amp;badge=0&amp;api=1&amp;player_id=${playerId}" class="cm-video cm-video--vimeo" data-cm-video--vimeo='{"playerId": "${playerId}"}' frameborder="0" width="100%" height="100%" webkitAllowFullScreen="" mozallowfullscreen="" allowFullScreen="">
      <@bp.notification type="warn" text=bp.getMessage("error_iframe_not_available") dismissable=true />
    </iframe>
  </div>
</#if>