<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#if self.items?has_content>
  <div class="cm-container cm-container--hero"<@cm.metadata data=bp.getContainerMetadata(self) />>
    <@cm.include self=self view="asSlideshow" params={"modifier": "hero", "items": self.items, "viewItems": "asHero"} />
  </div>
</#if>
