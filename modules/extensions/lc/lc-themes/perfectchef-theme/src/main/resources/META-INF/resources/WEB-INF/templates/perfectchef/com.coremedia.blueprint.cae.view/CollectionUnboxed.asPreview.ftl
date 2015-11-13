<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.view.CollectionUnboxed" -->

<#assign fragmentViews={
  "inPlacementMain": "Preview_Label_Unboxed_In_Placement_Main",
  "inPlacementSidebar": "Preview_Label_Unboxed_In_Placement_Sidebar",
  "inCollection": "Preview_Label_Unboxed_In_Collection"
} />

<#if self.delegate?has_content>
  <div>
    <#list fragmentViews?keys as key>
      <#assign value=fragmentViews[key]/>
      <#assign generatedId=bp.generateId("toggle")/>

      <div class="toggle-item cm-preview-item" data-id="${generatedId}">
        <a href="#" class="toggle-button cm-preview-item__headline"><@bp.message value /></a>
        <div class="toggle-container cm-preview-item__container">
          <div class="cm-preview-content cm-clearfix">
            <#switch key>
              <#case "inPlacementMain">
                <div class="content cm-placement-main"<@cm.metadata data=[self.delegate.content, "properties.items"] />>
                  <#list self.delegate.items as item>
                    <@cm.include self=item />
                  </#list>
                </div>
                <#break>
              <#case "inPlacementSidebar">
                <div class="content cm-placement-sidebar"<@cm.metadata data=[self.delegate.content, "properties.items"] />>
                  <#list self.delegate.items as item>
                    <@cm.include self=item view="asTeaser" />
                  </#list>
                </div>
                <#break>
              <#case "inCollection">
                <@cm.include self=self.delegate!cm.UNDEFINED view="asMasonry[]" params={"classCollection": "content"} />
                <#break>
            </#switch>
          </div>
        </div>
      </div>
    </#list>
  </div>
</#if>
