<#ftl strip_whitespace=true>
<#escape x as x?html>
<#-- @ftlvariable name="elasticSocialFreemarkerFacade" type="com.coremedia.blueprint.elastic.social.cae.tags.ElasticSocialFreemarkerFacade" -->

<#function getElasticSocialConfiguration page>
  <#return elasticSocialFreemarkerFacade.getElasticSocialConfiguration(page)>
</#function>

<#function isLoginAction bean>
  <#return elasticSocialFreemarkerFacade.isLoginAction(bean)>
</#function>

<#function getCurrentUser>
  <#return elasticSocialFreemarkerFacade.getCurrentUser()>
</#function>

<#function isAnonymousUser>
  <#return elasticSocialFreemarkerFacade.isAnonymousUser()>
</#function>

<#function isAnonymous communityUser>
  <#return elasticSocialFreemarkerFacade.isAnonymous(communityUser)>
</#function>

<#function getCurrentTenant>
  <#return elasticSocialFreemarkerFacade.getCurrentTenant()>
</#function>

<#function hasComplaintForCurrentUser id collection>
  <#return elasticSocialFreemarkerFacade.hasComplaintForCurrentUser(id, collection)>
</#function>

<#function getCommentsResult target>
  <#return elasticSocialFreemarkerFacade.getCommentsResult(target)>
</#function>

<#function getReviewsResult target>
  <#return elasticSocialFreemarkerFacade.getReviewsResult(target)>
</#function>

<#function hasUserWrittenReview target>
  <#return elasticSocialFreemarkerFacade.hasUserWrittenReview(target)>
</#function>

  <#function hasUserRated target>
    <#return elasticSocialFreemarkerFacade.hasUserRated(target)>
  </#function>

<#function getCommentView comment>
  <#local isPreview=cm.isPreviewCae() />
  <#local currentUser=getCurrentUser() />

  <#local view="default" />
  <#switch comment.state>
    <#case "NEW">
      <#local view="undecided" />
      <#if (!isPreview) && currentUser?has_content && comment.author?has_content && (currentUser.id != comment.author.id)>
        <#local view="deleted" />
      </#if>
      <#break>
    <#case "NEW_ONLINE">
    <#case "APPROVED">
      <#-- nothing to do -->
      <#break>
    <#case "REJECTED">
      <#local view="rejected" />
      <#-- only applied for live if user != author, which would already be filtered if no subcomments exist -->
      <#if (!isPreview) && currentUser?has_content && comment.author?has_content && (currentUser.id != comment.author.id)>
        <#local view="deleted" />
      </#if>
      <#break>
    <#case "IGNORED">
      <#-- only applied if in preview or if user != author, which would already be filtered of no subcomments exist -->
      <#if isPreview || (currentUser?has_content && comment.author?has_content && (currentUser.id != comment.author.id))>
        <#local view="deleted" />
      </#if>
      <#break>
  </#switch>

  <#return view />
</#function>

<#function getReviewView review>
  <#-- TODO -->
  <#return getCommentView(review) />
</#function>

<#function getMaxRating>
  <#return 5 />
</#function>

<#function getReviewMaxRating>
  <#return 5 />
</#function>

<#function getLogin>
  <#return bp.setting(cmpage!cm.UNDEFINED, "flowLogin", cm.UNDEFINED) />
</#function>

<#--
  @param value {boolean} The complain value
  @param id {String} The DHTML id prefix for this component
  @param collection {String}
  @param itemId {String}
  @param navigationId {String}
  @param customClass {String} (optional, defaults to empty string)
-->
<#macro complaining value id collection itemId navigationId customClass="">
<span id="complainTag_${id}" class="complaint">
  <#if value??>
      <a class="enabled complaint button ${customClass}" id="complainAnchor_${id}" style="display:none"
         onclick="c_${id}.complain(complainerId, true);"><@bp.message "comment-complaint"/></a>
      <a class="enabled uncomplaint button ${customClass}" id="uncomplainAnchor_${id}"
         onclick="c_${id}.complain(complainerId, false);"><@bp.message "comment-uncomplaint"/></a>
  <#else>
      <a class="enabled complaint button ${customClass}" id="complainAnchor_${id}"
         onclick="c_${id}.complain(complainerId, true);"><@bp.message "comment-complaint"/></a>
      <a class="enabled uncomplaint button ${customClass}" id="uncomplainAnchor_${id}" style="display:none"
         onclick="c_${id}.complain(complainerId, false);"><@bp.message "comment-uncomplaint"/></a>
  </#if>
</span>
<#local complainUrl=cm.getLink("/elastic/social/complaint")/>
<#noescape>
<#escape x as x?js_string>
<script type="text/javascript">
  var c_${id} = new com.coremedia.rating.HtmlComplaintControl(
          '${complainUrl}', '${id}', '${collection}', '${itemId}', '${navigationId}',
          'complainAnchor_${id}', 'uncomplainAnchor_${id}');
  var complainerId = '${elasticSocialFreemarkerFacade.getCurrentGuid()}';
</script>
</#escape>
</#noescape>
</#macro>
</#escape>

<#assign messageKeys=elasticSocialFreemarkerFacade.getElasticSocialMessageKeys()/>