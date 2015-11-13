<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMLinkable" -->
<#if self.contexts?has_content><@cm.include self=self.contexts[0] view="asSegmentPath"/>/</#if>${self.segment}
