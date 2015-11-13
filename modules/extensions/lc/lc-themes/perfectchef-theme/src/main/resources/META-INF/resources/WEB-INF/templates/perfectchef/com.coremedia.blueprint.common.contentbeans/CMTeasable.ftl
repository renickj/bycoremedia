<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<div class="cm-box"<@cm.metadata self.content/>>
  <@cm.include self=self view="headline" params={"classHeadline": "cm-box__header"} />
  <#if self.detailText?has_content>
    <div class="cm-box__content"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>
</div>
