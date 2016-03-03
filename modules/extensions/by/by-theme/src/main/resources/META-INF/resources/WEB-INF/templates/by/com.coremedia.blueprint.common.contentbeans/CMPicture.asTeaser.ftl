<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
  <@cm.include self=self params={
    "limitAspectRatios": lc.getAspectRatiosForTeaser()
  }/>
  <#if self.detailText?has_content>
    <@cm.include self=self.detailText!cm.UNDEFINED />
  <#elseif self.teaserTitle?has_content>
    <@cm.include self=self.teaserText!cm.UNDEFINED />
  </#if>
</div>

