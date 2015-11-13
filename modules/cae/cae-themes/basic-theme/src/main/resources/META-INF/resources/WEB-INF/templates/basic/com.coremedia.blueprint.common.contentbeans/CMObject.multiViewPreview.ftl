<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMObject" -->
<#-- @ftlvariable name="fragmentViews" type="java.util.Map" -->
<#-- @ftlvariable name="additionalAttr" type="java.util.Map" -->

  <div<@bp.renderAttr additionalAttr!{} /><@cm.metadata self.content/>>
    <#-- iterate over all views as requested by the including template -->
    <#list (fragmentViews!{})?keys as key>
      <#assign value=fragmentViews[key]/>
      <#assign generatedId=bp.generateId("toggle")/>

      <div class="toggle-item cm-preview-item" data-id="${generatedId}">
        <a href="#" class="toggle-button cm-preview-item__headline"><@bp.message value /></a>
        <div class="toggle-container cm-preview-item__container">
          <div class="cm-preview-content cm-clearfix">
            <#if key =="asListItem">
              <ol class="list">
                <@cm.include self=self view=key />
              </ol>
            <#else>
              <div class="content">
                <#if key == "DEFAULT">
                  <@cm.include self=self />
                <#else>
                  <@cm.include self=self view=key />
                </#if>
              </div>
            </#if>
          </div>
        </div>
      </div>
    </#list>
  </div>