<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classVideo" type="java.lang.String" -->

<#if self.dataUrl?has_content>
  <#-- For URLs such as http://www.youtube.com/watch?v=EgfcwxLr5vA&feature=plcp
                        http://youtu.be/EgfcwxLr5vA
                        https://www.youtube.com/v/EgfcwxLr5vA
                     or http://www.youtube.com/embed/EgfcwxLr5vA?rel=0 -->
  <#assign res = self.dataUrl?matches("(?:https?:)?\\/\\/(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))(.+)") >
  <#if res>
    <#-- the captured Youtube video ID and additional URL parameters,
         must replace first '&' character with '?' if there's none yet.
    -->
    <#assign captured = res[0]?groups[1]>
    <#if !captured?contains('?')>
      <#assign captured = captured?replace('&', '?', 'f') >
    </#if>
    <#if captured?contains('?')>
      <#assign video = captured + "&wmode=opaque&enablejsapi=1"/>
    <#else>
      <#assign video = captured + "?wmode=opaque&enablejsapi=1"/>
    </#if>

    <div class="${classVideo}">
      <iframe src="//www.youtube.com/embed/${video}" class="cm-video cm-video--youtube" frameborder="0" width="100%" height="100%" webkitAllowFullScreen="" mozallowfullscreen="" allowFullScreen="">
        <@bp.notification type="warn" text=bp.getMessage("error_iframe_not_available") dismissable=true />
      </iframe>
    </div>
  </#if>
</#if>
