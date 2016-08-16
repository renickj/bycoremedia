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
					<p>Posted on <b>${self.modificationDate!self.creationDate!""}</b></p>
					<div>
					 <@cm.include self=self.detailText!cm.UNDEFINED />
					</div>
					<div class="row">
						<div class="col-md-6 col-lg-6 col-sm-12 col-xs-12">
							<#if self.related[0]?has_content && self.related[0].class?contains("CMVideoImpl")>
								<@cm.include self=self.related[0] view="asTeaser" params={"isTemplateSix": false,"isTemplateThree": false,"isTemplateFour": true} />
							</#if>
							<div class="prodimages">
								<#--<div class="row">
						          <h4>Get the look</h4>
						        </div>-->
								<#list self.related as related>	
								<#assign classType = related.class/>
									<#if !classType?contains("CMVideoImpl")>
										<div class= ${classArticleRelated!""}>
											<@cm.include self=related view="asTeaser" params={"isTemplateSix": false,"isTemplateThree": false,"isTemplateFour": true} />
										</div>
									</#if>
								</#list>
							</div>
						</div>
		 				<div class="col-md-6 col-lg-6 col-sm-12 col-xs-12">
		 					 <@cm.include self=self.productDesc!cm.UNDEFINED />
		 				</div>
		 				<div class="col-md-6 col-lg-6 col-sm-12 col-xs-12">
			 				<div class = "prodimagesmobile">
				 				<#--<div class="row">
						          <h4>Get the look</h4>
						        </div>-->
								<#list self.related as related>	
									<#assign classType = related.class/>
										<#if !classType?contains("CMVideoImpl")>
											<div class= ${classArticleRelated!""}>
												<@cm.include self=related view="asTeaser" params={"isTemplateSix": false,"isTemplateThree": false,"isTemplateFour": true} />
											</div>
										</#if>
								</#list>
							</div>
						</div>
					</div>
				</section>
			</article>
        </div>
    </div>
</section>