<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMArticle" -->

<#if self.related?has_content>
  <#assign classArticleRelated="row" />
</#if>

<#assign templateClass = "template3"/>

<section class="inspiration">
    <div class="container">
        <div class="inspiration-holder">
			<article class="${templateClass!""}">
				<@cm.include self=self view="[]" params={"isTemplateThree": true,"isTemplateSix": false,"isTemplateFour": false} />
				<section>
					 <@cm.include self=self.detailText!cm.UNDEFINED />
					 <#if self.related?has_content>
							<div class= ${classArticleRelated!""}>
								<#list self.related as related>
										<@cm.include self=related view="asTeaser" />
								</#list>
							</div>
					 </#if>
					<@cm.include self=self.productDesc!cm.UNDEFINED />
				</section>
			</div>
    	</div>
</section>