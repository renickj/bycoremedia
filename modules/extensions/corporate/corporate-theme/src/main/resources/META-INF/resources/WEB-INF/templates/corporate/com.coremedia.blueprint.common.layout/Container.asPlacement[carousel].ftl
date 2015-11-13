<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->

<#assign index=cm.localParameters().index!0 />

<#assign items=self.items![] />
<#if items?has_content>
  <div class="cm-container cm-container--carousel"<@cm.metadata data=bp.getContainerMetadata(self) />>
    <@cm.include self=self view="asSlideshow" params={"modifier": "default", "items": items, "viewItems": "asTeaser", "index": index} />
  </div>
</#if>
