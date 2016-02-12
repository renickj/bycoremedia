<#-- @ftlvariable name="self" type="com.boots.cms.beauty.contentbeans.CMArticle" -->
<#if self.related?has_content>
  <#assign classArticleRelated="row" />
</#if>

<#assign templateClass = "temp4"/>

<section class="inspiration">
    <div class="container">
        <div class="inspiration-holder">
			<article class="${templateClass!""}">
				<@cm.include self=self view="[]" params={"isTemplateSix": false,"isTemplateThree": false,"isTemplateFour": true} />
				<section class="prdList">
					 <@cm.include self=self.detailText!cm.UNDEFINED />
					<div class="row">
						<div class="prodimages">
							<#list self.related as related>	
								<div class= ${classArticleRelated!""}>
									<@cm.include self=related view="asTeaser" params={"isTemplateSix": false,"isTemplateThree": false,"isTemplateFour": true} />
								</div>
							</#list>
						</div>
		 				<div class="col-md-6 col-lg-6 col-sm-12 col-xs-12">
		 					 <@cm.include self=self.productInfo!cm.UNDEFINED />
		 				</div>
					</div>
				</section>
			</article>
        </div>
    </div>
</section>