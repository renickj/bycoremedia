<#-- @ftlvariable name="self" type="com.boots.cms.beauty.contentbeans.CMArticle" -->
<#if self.related?has_content>
  <#assign classArticleRelated="row" />
</#if>

<#assign templateClass = "template6"/>

<section class="inspiration">
    <div class="container">
        <div class="inspiration-holder">
			<article class="${templateClass!""}">
				<@cm.include self=self view="[]" params={"isTemplateSix": true,"isTemplateThree": false,"isTemplateFour": false} />
				<section>
					 <@cm.include self=self.detailText!cm.UNDEFINED />
				</section>
				<#list self.related as related>	
						<div class= ${classArticleRelated!""}>
							<@cm.include self=related view="asTeaser" />
						</div>
				</#list>
			</article>
		</div>
    </div>
</section>