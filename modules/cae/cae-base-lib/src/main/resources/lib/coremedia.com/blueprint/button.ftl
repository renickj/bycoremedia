<#ftl strip_whitespace=true>
<#import "util.ftl" as util>

<#--
 * Renders an icon and text as <a> tag. Not to be used outside of this template, use #macro button instead.
 -->
<#macro _linkWithIconAndText href baseClass icon text iconClass iconText="" textClass="" attr={}>
  <#local hasText=text?is_string && text?has_content />
  <#local hasIcon=icon?is_boolean && icon />
  <#local iconClasses=[] />
  <#local textClasses=[] />
  <#if baseClass?has_content>
    <#local buttonClasses=[baseClass] />
    <#-- add modifier to determine if text and icons exists -->
    <#if hasText>
      <#local buttonClasses=buttonClasses + [baseClass + "--with-text"] />
    </#if>
    <#if hasIcon>
      <#local buttonClasses=buttonClasses + [baseClass + "--with-icon"] />
    </#if>
    <#local attr=util.extendSequenceInMap(attr, "classes", buttonClasses) />
    <#local iconClasses=iconClasses + [baseClass + "__icon"] />
    <#local textClasses=textClasses + [baseClass + "__text"] />
  </#if>
  <#if iconClass?has_content>
    <#local iconClasses=iconClasses + [iconClass] />
  </#if>
  <#if textClass?has_content>
    <#local textClasses=textClasses + [textClass] />
  </#if>
<a href="${href}"<@util.renderAttr attr, ["href"] />>
  <#if hasIcon><i<@util.renderAttr attr={"classes": iconClasses} />><#if !hasText><span class="cm-visuallyhidden">${iconText}</span></#if></i></#if>
  <#if hasText><span<@util.renderAttr attr={"classes": textClasses} />>${text}</span></#if>
</a>
</#macro>

<#--
 * Renders an icon as <a> tag. Not to be used outside of this template, use #macro button instead.
 -->
<#macro _linkWithIcon href baseClass iconClass iconText="" attr={}>
  <@_linkWithIconAndText baseClass=baseClass href=href icon=true text="" iconClass=iconClass iconText=iconText attr=attr />
</#macro>

<#--
 * Renders text as <a> tag. Not to be used outside of this template, use #macro button instead.
 -->
<#macro _linkWithText href baseClass text textClass="" attr={}>
  <@_linkWithIconAndText baseClass=baseClass href=href icon=false text=text iconClass="" textClass=textClass attr=attr />
</#macro>

<#--
 * Renders an icon and text as <button> tag. Not to be used outside of this template, use #macro button instead.
 -->
<#macro _buttonWithIconAndText baseClass icon text iconClass="" iconText="" textClass="" attr={}>
  <#local hasText=text?is_string && text?has_content />
  <#local hasIcon=icon?is_boolean && icon />
  <#local attr={"type": "button"} + attr />
  <#local iconClasses=[] />
  <#local textClasses=[] />
  <#if baseClass?has_content>
    <#local buttonClasses=[baseClass] />
    <#-- add modifier to determine if text and icons exists -->
    <#if hasText>
      <#local buttonClasses=buttonClasses + [baseClass + "--with-text"] />
    </#if>
    <#if hasIcon>
      <#local buttonClasses=buttonClasses + [baseClass + "--with-icon"] />
    </#if>
    <#local attr=util.extendSequenceInMap(attr, "classes", buttonClasses) />
    <#local iconClasses=iconClasses + [baseClass + "__icon"] />
    <#local textClasses=textClasses + [baseClass + "__text"] />
  </#if>
  <#if iconClass?has_content>
    <#local iconClasses=iconClasses + [iconClass] />
  </#if>
  <#if textClass?has_content>
    <#local textClasses=textClasses + [textClass] />
  </#if>
<button<@util.renderAttr attr />>
  <#if hasIcon><i<@util.renderAttr attr={"classes": iconClasses} />><#if !hasText><span class="cm-visuallyhidden">${iconText}</span></#if></i></#if>
  <#if hasText><span<@util.renderAttr attr={"classes": textClasses} />>${text}</span></#if>
</button>
</#macro>

<#--
 * Renders an icon as <button> tag. Not to be used outside of this template, use #macro button instead.
 -->
<#macro _buttonWithIcon baseClass iconClass iconText="" attr={}>
  <@_buttonWithIconAndText baseClass=baseClass icon=true text="" iconClass=iconClass iconText=iconText attr=attr />
</#macro>

<#--
 * Renders text as <button> tag. Not to be used outside of this template, use #macro button instead.
 -->
<#macro _buttonWithText baseClass text textClass="" attr={}>
  <@_buttonWithIconAndText baseClass=baseClass icon=false text=text textClass=textClass attr=attr />
</#macro>

<#--
 * Renders a button based on given parameters
 *
 * @param text (optional) if the button should contain text, this defines the text to be rendered
 * @param href (optional) if the button should be a link, this defines the href
 *                        If href is empty <button> tag will be used, otherwise <a> tag
 * @param baseClass (optional) if the button and its elements should have a baseClass attached this defines the base
 *                  class (default: cm-button). If empty not baseClasses will be attached
 * @param iconClass (optional) defines a class attached to the icon element of the button (defining the icon)
 * @param iconText (optional) defines the fallback text for the icon if icon cannot be displayed
 * @param textClass (optional) defines a class attached to the text element of the button
 * @param attr (optional) additional attributes to be rendered with the button element
 -->
<#macro button text="" href="" baseClass="cm-button" iconClass="" iconText="" textClass="" attr={}>
  <#if href?has_content>
    <#if iconClass?has_content && text?has_content>
      <@_linkWithIconAndText href=href baseClass=baseClass icon=true text=text iconClass=iconClass iconText=iconText textClass=textClass attr=attr />
    <#elseif iconClass?has_content>
      <@_linkWithIcon href=href baseClass=baseClass iconClass=iconClass iconText=iconText attr=attr />
    <#else>
      <@_linkWithText href=href baseClass=baseClass text=text textClass=textClass attr=attr />
    </#if>
  <#else>
    <#if iconClass?has_content && text?has_content>
      <@_buttonWithIconAndText baseClass=baseClass icon=true text=text iconClass=iconClass iconText=iconText textClass=textClass attr=attr />
    <#elseif iconClass?has_content>
      <@_buttonWithIcon baseClass=baseClass iconClass=iconClass iconText=iconText attr=attr />
    <#else>
      <@_buttonWithText baseClass=baseClass text=text textClass=textClass attr=attr />
    </#if>
  </#if>
</#macro>
