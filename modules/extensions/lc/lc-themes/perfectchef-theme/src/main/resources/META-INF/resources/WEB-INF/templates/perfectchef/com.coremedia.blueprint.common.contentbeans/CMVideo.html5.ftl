<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classVideo" type="java.lang.String" -->
<#-- @ftlvariable name="hideControls" type="java.lang.Boolean" -->

<#assign linkVideo=self.dataUrl!"" />
<#if hideControls == false>
    <#assign controls>controls="controls"</#assign>
</#if>

<#if !linkVideo?has_content && self.data?has_content>
  <#assign linkVideo=cm.getLink(self.data) />

  <video ${controls!""} src="${linkVideo}" class="cm-video cm-video--html5 ${classVideo!""} cm-non-adaptive-content" cm-non-adaptive-content='{"overflow": "false"}' data-cm-video--html5='{"flash": "${cm.getLink((bp.setting(cmpage, "mediaelementplayer_flash").data)!cm.UNDEFINED)}"}'>
  <#-- TODO localize -->
    <@bp.notification type="info" text="Video is not supported" dismissable=true />
  </video>
</#if>
