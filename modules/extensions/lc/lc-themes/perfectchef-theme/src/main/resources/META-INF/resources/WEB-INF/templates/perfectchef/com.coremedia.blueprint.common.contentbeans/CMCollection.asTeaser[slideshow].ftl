<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<@bp.optionalFrame title=self.teaserTitle!"" classFrame="cm-frame--teaser" attrTitle={"metadata": (metadata![]) + [self.content, "properties.teaserTitle"]}>
  <div class="cm-collection cm-collection--slideshow" data-cm-slideshow='{ "container": "> .cm-collection--slideshow__slides", "item": "> .cm-collection__item", "timeout": "5000", "prev": "> .cm-collection--slideshow__prev", "next": "> .cm-collection--slideshow__next" }'<@cm.metadata self.content />>
    <div class="cm-collection--slideshow__slides"<@cm.metadata "properties.items" />>
      <#assign items=self.items![] />
      <#list items as item>
        <@cm.include self=item view="asListItem" />
      </#list>
      <#if (items?size > 1)>
        <div class="cm-collection--slideshow__prev cm-direction-arrow cm-direction-arrow--left"></div>
        <div class="cm-collection--slideshow__next cm-direction-arrow cm-direction-arrow--right"></div>
      </#if>
    </div>
  </div>
</@bp.optionalFrame>