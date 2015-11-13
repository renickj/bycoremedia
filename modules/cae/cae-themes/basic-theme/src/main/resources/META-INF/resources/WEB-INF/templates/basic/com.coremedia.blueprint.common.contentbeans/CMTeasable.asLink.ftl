<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="openInTab" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->

<#assign cssClass=cm.localParameters().cssClass!"" />
<#assign target=cm.localParameters().openInTab!false?then(' target="_blank"', "") />

<a class="${cssClass}" href="${cm.getLink(self.target!cm.UNDEFINED)}"${target}<@cm.metadata data=[self.content, "properties.teaserTitle"] />>${self.teaserTitle!""}</a>
