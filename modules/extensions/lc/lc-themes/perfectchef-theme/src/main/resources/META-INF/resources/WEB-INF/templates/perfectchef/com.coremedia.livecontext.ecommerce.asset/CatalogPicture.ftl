<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.asset.CatalogPicture" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.lang.String" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->
<#assign limitAspectRatios=cm.localParameters().limitAspectRatios![] />
<#assign classBox=cm.localParameters().classBox!"" />
<#assign classImage=cm.localParameters().classImage!"" />

<#if self.picture?has_content>
  <@cm.include self=bp.createBeanFor(self.picture) params={
    "limitAspectRatios": limitAspectRatios,
    "classBox": classBox,
    "classImage": classImage
  }/>
<#else>
  <div class="cm-aspect-ratio-box ${classBox}">
    <img class="cm-aspect-ratio-box__content cm-non-adaptive-content ${classImage}" data-cm-non-adaptive-content='{"overflow": "true"}' src="${self.url!""}" />
  </div>
</#if>