<#-- @ftlvariable name="self" type="com.boots.cms.beauty.contentbeans.CMArticle" -->
<#if self.related?has_content>
  <#assign classArticleRelated="row" />
</#if>

<#assign templateClass = "blogtemplate"/>

<section class="inspiration">
    <div class="container">
        <div class="inspiration-holder">
			<article class="${templateClass!""}">
				<@cm.include self=self view="[]" params={"isBlogTemplate": true,"isTemplateSix": false,"isTemplateThree": false,"isTemplateFour": false} />
				<section>
					<p>Posted on <b>${self.modificationDate!self.creationDate!""}</b></p>
					 <@cm.include self=self.detailText!cm.UNDEFINED />
					<#list self.related as related>
							<div class= ${classArticleRelated!""}>
								<@cm.include self=related view="asTeaser" params={"isBlogTemplate": true,"isTemplateSix": true,"isTemplateThree": false,"isTemplateFour": false}/>
							</div>
					</#list>
                </section>
			</article>
		</div>
    </div>
</section>