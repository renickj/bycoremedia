<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#assign items=self.items![] />
<#assign numberOfItems=items?size />
<#assign itemsPerRow=3 />

<div class="cm-container cm-container--text"<@cm.metadata data=bp.getContainerMetadata(self) />>
<#if (numberOfItems > 0)>
    <div class="cm-row--full-height row">
      <#list items as item>
        <#-- add new row -->
        <@corp.renderNewBootstrapRow item_index itemsPerRow "cm-row--full-height " />
        <#-- render the items as claim teaser -->
        <#assign offsetClass=corp.getBootstrapOffsetClass(item_index, numberOfItems, itemsPerRow, " col-sm-") />
        <div class="cm-col--full-height col-xs-12 col-sm-4${offsetClass}">
          <@cm.include self=item view="asText" params={"islast": item?is_last} />
        </div>
      </#list>
    </div>
  </#if>
</div>
