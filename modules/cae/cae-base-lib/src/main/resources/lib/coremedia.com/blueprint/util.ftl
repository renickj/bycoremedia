<#ftl strip_whitespace=true>
<#import "/spring.ftl" as spring>
<#-- @ftlvariable name="cmpage" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#--
 * Renders a map of attributes as html attribute, e.g.:
 * map: {"class": "button", "id": "button1"} will be rendered as: class="button" id="button1"
 *
 * There are reserved keys in attr:
 * "metadata": if metadata shall be rendered it contains the value of param data for cm.metadata
 * "classes": contains a sequence of classes attached to class attribute. If there are classes set in "class" attribute
 * these classes will be merged with classes delivered in "classes"
 *
 * @param attr the map of attributes
 * @param ignore a list of keys that will be ignored
 -->
<#macro renderAttr attr={} ignore=[]>
  <#if attr?keys?seq_contains("classes") && !ignore?seq_contains("classes")>
    <#local classes=attr["classes"] />
    <#if attr?keys?seq_contains("class")>
      <#local classes=classes + attr["class"]?replace("  ", " ")?split(" ") />
    </#if>
    <#local attr=attr + {"class": classes?join(" ")} />
  </#if>
  <#local ignore=ignore + ["classes"] />
  <#list attr?keys as key><#if !ignore?seq_contains(key)><#switch key><#case "metadata"><@cm.metadata data=attr[key] /><#break><#default> ${key}="${attr[key]}"<#break></#switch></#if></#list>
</#macro>

<#--
 * Renders a <time/> element  in locale format
 * See http://freemarker.org/docs/ref_builtins_date.html#ref_builtin_string_for_date
 *
 * @param contentDate @see java.util.Date
 * @param cssClass (optional) string
 -->
<#macro renderDate contentDate cssClass="">
  <#if contentDate?has_content>
    <time class="${cssClass}" datetime="${contentDate?datetime?string.iso}"<@cm.metadata "properties.externallyDisplayedDate" />>${contentDate?date?string.medium}</time>
  </#if>
</#macro>

<#--
 * Translates a message key into a localized message
 * Use message instead of spring.message/spring.messageArgs to avoid rendering of exceptions
 *
 * @param key @see spring.message#key
 * @param args @see spring.messageArgs#args
 * @param highlightErrors specifies if errors should be highlighted, default: true (for macro variant)
 -->
<#macro message key args=[] highlightErrors=true>
<#compress>
  <#if key?has_content>
    <#local messageText><@spring.messageText key key/></#local>
    <#if messageText?has_content && messageText != key>
      <#if args?has_content>
        <#local messageText><@spring.messageArgs key args/></#local>
      </#if>
      ${messageText}
    <#elseif ((cmpage.developerMode)!false) && highlightErrors>
      <span title="key '${key}' is missing" class="cm-preview-missing-key">[---${key}---]</span>
    <#else>
      [---${key}---]
    </#if>
  </#if>
</#compress>
</#macro>

<#--
 * Translates a message key into a localized message
 * Use getMessage instead of spring.message/spring.messageArgs to avoid rendering of exceptions
 *
 * @param key @see spring.message#key
 * @param args @see spring.messageArgs#args
 * @param highlightErrors specifies if errors should be highlighted, default: false (function variant)
 -->
<#function getMessage key args=[] highlightErrors=false>
  <#local result><@message key=key args=args highlightErrors=highlightErrors /></#local>
  <#return result />
</#function>

<#function extendSequenceInMap map={} key="" extendBy=[]>
  <#local newSequence=extendBy />
  <#if map?keys?seq_contains(key) && map[key]?is_sequence>
    <#local newSequence=map[key] + extendBy />
  </#if>
  <#return map + {key: newSequence} />
</#function>

<#--
 * Renders nested content inside a link if href is not empty.
 *
 * @param href The href attribute of the link
 * @param attr (optional) additional attributes for link tag
 * @nested (optional) nested content will be rendered inside the link
 -->
<#macro optionalLink href attr={}>
  <#if href?has_content><a href="${href}"<@renderAttr attr />></#if>
  <#nested />
  <#if href?has_content></a></#if>
</#macro>

<#--
 * Renders nested content inside a frame if its title is not empty.
 *
 * @param title The title of the frame
 * @param attr additional attributes for the container representing the frame
 * @param attr additional attributes for the container representing the frame title
 * @nested (optional) nested content will be rendered inside the frame
 -->
<#macro optionalFrame title="" classFrame="" attr={} attrTitle={}>
  <#if title?has_content>
    <div class="cm-frame ${classFrame}"<@renderAttr attr />>
      <h2 class="cm-frame__title"<@renderAttr attrTitle />>${title}</h2>
  </#if>
    <#nested>
  <#if title?has_content>
    </div>
  </#if>
</#macro>

<#--
 * Renders given text with line breaks as <br/>
 *
 * @param text String with line breaks
 -->
<#macro renderWithLineBreaks text>
  <@cm.unescape text=text?trim?replace("\n\n", "<br/>")?replace("\n", "<br/>") />
</#macro>
