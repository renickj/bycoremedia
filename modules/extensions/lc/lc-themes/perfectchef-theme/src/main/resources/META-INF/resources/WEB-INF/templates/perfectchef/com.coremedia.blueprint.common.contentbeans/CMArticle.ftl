<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMArticle" -->
<#-- @ftlvariable name="isSingleColumn" type="java.lang.Boolean" -->


<#if self.related?has_content>
  <#assign classArticleRelated="cm-box--has-related" />
</#if>

<div class="cm-clearfix">
  <div class="cm-box ${classArticleRelated!""}"<@cm.metadata self.content/>>

    <@cm.include self=self!cm.UNDEFINED view="headline" params={"classHeadline": "cm-box__header"} />

    <div class="cm-box__content cm-text"<@cm.metadata "properties.title" />>
      <h2>${self.title!""}</h2>
    </div>

    <#assign classText="" />
    <#if isSingleColumn?has_content && isSingleColumn?is_boolean && isSingleColumn>
      <#assign classText="cm-text--single-column" />
    </#if>
    <div class="cm-box__content">
      <div class="cm-text ${classText!""}"<@cm.metadata "properties.detailText"/>>
        <@cm.include self=self.detailText!cm.UNDEFINED />
      </div>
    </div>

    <#-- hook for extensions (e.g. elastic social comments) -->
    <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
  </div>

  <#if self.related?has_content>
    <div class="cm-box cm-box--related"<@cm.metadata data=[self.content, "properties.related"] />>
      <#list self.related as related>
        <@cm.include self=related view="asTeaser" />
      </#list>
    </div>
  </#if>

</div>
