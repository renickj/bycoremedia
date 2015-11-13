<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMMarketingSpot" -->

<#if self.items?has_content>
  <#-- there is at least 1 marketing spot, so zero does not need to be defined -->
  <#assign classMarketingSpot="cm-collection--marketingspot-multi" />
  <#switch self.items?size>
    <#case 1>
      <#assign classMarketingSpot="cm-collection--marketingspot-single" />
      <#break>
    <#case 2>
      <#assign classMarketingSpot="cm-collection--marketingspot-two" />
      <#break>
  </#switch>
  <div class="cm-collection cm-collection--marketingspot ${classMarketingSpot}"<@cm.metadata self.content />>
    <#list self.items as item>
      <@cm.include self=item view="asListItem"/>
    </#list>
  </div>
</#if>