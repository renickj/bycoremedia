<#ftl strip_whitespace=true>
<#escape x as x?html>
<#import "/spring.ftl" as spring>
<#import "util.ftl" as util>

<#--
 * Renders a notification element
 *
 * @param type the type of the notification
 * @param dismissable specifies if the dismiss button is shown
 * @param baseClass (optional) base class of the notification, the type will be added to the notification as an additional
 *                         class relying on the base class (e.g. baseclass--error)
 * @param additionalClasses (optional) sequence of additional css classes to be attached to container element
 * @param title (optional) title to display
 * @param text (optional) plain text to show (html will be escaped)
 * @param attr (optional) additional attributes for notification tag
 * @nested (optional) nested content will be rendered between the text and dismiss output
 -->
<#macro notification type dismissable baseClass="cm-notification" additionalClasses=[] title="" text="" attr={}>
  <#local classes=[baseClass, baseClass + "--" + type] + additionalClasses />
  <#local attr=util.extendSequenceInMap(attr, "classes", classes) />
  <div<@util.renderAttr attr=attr />>
    <#if title?has_content>
      <span class="${baseClass}__headline">${title}</span>
    </#if>
    <span class="${baseClass}__text">
    <#if text?has_content>${text}</#if>
    <#nested />
    </span>
    <#if dismissable?is_boolean && dismissable>
      <div class="${baseClass}__dismiss"></div>
    </#if>
  </div>
</#macro>

<#--
 * Renders a notification element associated to spring forms.
 * The text will automatically be determinated.
 *
 * @param path the name of the field to bind to
 * @param dismissable @see notification#dismissable
 * @param class @see notification#baseClass
 * @param class @see notification#additionalClasses
 * @param ignoreIfEmpty (optional) specifies if the notification will not be rendered if spring error messages are empty
 * @param type (optional) @see notification#type, for spring notifications the default is "error"
 * @param title (optional) @see notification#title
 * @param bindPath (optional) false prevents the rebinding of the path, e.g. if you already know that the path is bound
 * @param attr (optional) @see notification#attr
 * @nested (optional) nested content will be rendered between the text and dismiss output
 -->
<#macro notificationFromSpring path dismissable baseClass="cm-notification" additionalClasses=[] ignoreIfEmpty=true type="error" title="" bindPath=true attr={}>
  <#if bindPath><@spring.bind path=path /></#if>
  <#local text="" />
  <#if spring.status.error>
    <#local text=spring.status.getErrorMessagesAsString("\n") />
  </#if>
  <#if !ignoreIfEmpty?is_boolean || !ignoreIfEmpty || text?has_content>
    <@notification type=type dismissable=dismissable baseClass=baseClass additionalClasses=additionalClasses title=title attr=attr><#noescape>${text?html?replace("\n", "<br />")}</#noescape><#nested /></@notification>
  </#if>
</#macro>
</#escape>