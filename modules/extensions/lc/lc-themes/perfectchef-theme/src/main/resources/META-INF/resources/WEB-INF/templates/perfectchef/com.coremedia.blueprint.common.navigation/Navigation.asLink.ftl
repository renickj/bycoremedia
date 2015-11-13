<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->

<#-- same as CMTeasable.asLink but without metadata for LiveContextNavigation links -->
<#assign cssClass=cm.localParameters().cssClass!"" />
<#assign target=cm.localParameters().openInTab!false?then(' target="_blank"', "") />

<a class="${cssClass}" href="${cm.getLink(self!cm.UNDEFINED)}"${target}>${self.title!""}</a>
