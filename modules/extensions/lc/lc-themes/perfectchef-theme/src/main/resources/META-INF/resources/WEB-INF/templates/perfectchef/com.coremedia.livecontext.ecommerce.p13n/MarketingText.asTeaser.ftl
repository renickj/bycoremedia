<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingText" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teaser cm-teaser--text"<@cm.metadata metadata![] />>
    <div class="cm-teaser__content">
      <span><@cm.unescape (self.text)!"" /></span>
    </div>
</div>
