<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->
<#-- @ftlvariable name="classCollection" type="java.lang.String" -->
<#-- @ftlvariable name="viewItems" type="java.lang.String" -->

<@bp.optionalFrame title=self.title!"" attrTitle={"metadata": [self.content, "properties.title"]}>
  <div class="cm-collection cm-collection--masonry ${classCollection!"cm-collection--default"} cm-js-masonry" data-masonry-options='{ "isInitLayout": false, "columnWidth": ".cm-collection--masonry > .cm-collection--masonry__grid-sizer", "gutter": ".cm-collection--masonry > .cm-collection--masonry__grid-gutter", "itemSelector": ".cm-collection--masonry > .cm-collection__item" }'<@cm.metadata data=[self.content, "properties.items"] />>
    <#list self.items![] as item>
      <@cm.include self=item view="asListItem" params={"viewItem": viewItems!"asTeaser"} />
    </#list>
    <span class="cm-collection--masonry__grid-sizer"></span>
    <span class="cm-collection--masonry__grid-gutter"></span>
  </div>
</@bp.optionalFrame>