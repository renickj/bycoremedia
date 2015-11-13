<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#assign items=self.items![] />
<#assign numberOfItems=items?size />
<#assign itemsPerRow=4 />

<div class="cm-container cm-container--squarelist"<@cm.metadata data=bp.getContainerMetadata(self) />>
<#if (numberOfItems > 0)>
  <div class="row">
    <#list items as item>
      <#-- add new row -->
      <@corp.renderNewBootstrapRow item_index itemsPerRow />
      <#-- render the items as claim teaser -->
      <div class="col-xs-6 col-sm-3">
        <@cm.include self=item view="asSquarelist" />
      </div>
    </#list>
  </div>
</#if>
</div>
