<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSitemap" -->

<#assign maxDepth=bp.setting(self, "sitemap_depth", 4)?number />

<#if self.root?has_content>
  <div class="cm-box">
    <#-- headline -->
    <@cm.include self=self view="headline" params={"classHeadline": "cm-box__header"} />
    <#-- items/tree of navigation with default max depth of 4 (like navigation - root) -->
    <div class="cm-box__content">
      <@cm.include self=self.root view="asLinkList" params={
        "maxDepth": maxDepth,
        "cssClass": "cm-collection cm-collection--sitemap"
      } />
    </div>
  </div>
</#if>
