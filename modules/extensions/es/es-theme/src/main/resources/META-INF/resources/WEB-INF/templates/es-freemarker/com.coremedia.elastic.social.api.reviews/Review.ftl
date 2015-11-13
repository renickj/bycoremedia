<#-- @ftlvariable name="self" type="com.coremedia.elastic.social.api.reviews.Review" -->
<#-- @ftlvariable name="reviewView" type="java.lang.String" -->
<#-- @ftlvariable name="reviewingAllowed" type="java.lang.Boolean" -->

<#assign reviewView=es.getReviewView(self) />

<div class="cm-review" data-cm-review-id="${self.id}" itemscope="itemscope" itemtype="http://data-vocabulary.org/Review" <@cm.metadata self/>>

  <#if (self.target)?has_content>
    <span itemprop="itemreviewed" class="cm-visuallyhidden">${self.target}</span>
  </#if>
  <#assign strAuthorName=self.authorName!"" />
  <#assign title=self.title!"" />
  <#assign rating=self.rating!"" />
  <#assign strDate=self.creationDate?datetime?string.long_short />
  <#assign strDateTechnical=self.creationDate?string("yyyy-MM-dd") />
  <#if !strAuthorName?has_content>
    <#if !self.author?has_content || es.isAnonymous(self.author)>
      <#assign strAuthorName=bp.getMessage(es.messageKeys.REVIEW_AUTHOR_ANONYMOUS) />
    <#else>
      <#assign strAuthorName=(self.author.name)!"" />
    </#if>
  </#if>

  <#if ["default", "undecided", "rejected"]?seq_contains(reviewView)>
    <div class="cm-review__header">
      <span class="cm-review__rating cm-rating" itemprop="rating">
        <#list es.getReviewMaxRating()..1 as currentRating>
          <#assign classRatingIndicator="" />
          <#if currentRating == rating>
            <#assign classRatingIndicator=" cm-rating-indicator--active" />
          </#if>
          <div class="cm-rating__option cm-rating-indicator${classRatingIndicator}">${currentRating}</div>
        </#list>
        <meta itemprop="value" content="${rating}" property="" />
        <meta itemprop="best" content="${es.getReviewMaxRating()}" property="" />
      </span>
      <span class="cm-review__author-date">
        <@bp.message es.messageKeys.REVIEW_AUTHOR_BY /> <span class="cm-review__author" itemprop="reviewer">${strAuthorName!""}</span> <time class="cm-review__date" itemprop="dtreviewed" datetime="${strDateTechnical}">${strDate}</time>
      </span>
    </div>
  </#if>

  <#-- output of review specific information -->
  <#-- At least one dynamic notification is rendered -->
  <#if ["undecided"]?seq_contains(reviewView)>
    <@bp.notification type="info" text=bp.getMessage(es.messageKeys.REVIEW_APPROVAL_UNDECIDED) dismissable=false additionalClasses=["cm-review__notification"] attr={"data-cm-notification": '{"path": ""}', "data-cm-contribution-notification-type": "UNDECIDED"} />
  <#elseif ["rejected"]?seq_contains(reviewView)>
    <@bp.notification type="warning" text=bp.getMessage(es.messageKeys.REVIEW_APPROVAL_REJECTED) dismissable=false additionalClasses=["cm-review__notification"] attr={"data-cm-notification": '{"path": ""}', "data-cm-contribution-notification-type": "REJECTED"} />
  <#elseif ["deleted"]?seq_contains(reviewView)>
    <#-- not in use -->
    <@bp.notification type="info" text=bp.getMessage(es.messageKeys.REVIEW_DELETED) dismissable=false additionalClasses=["cm-review__notification"] attr={"data-cm-notification": '{"path": ""}'} />
  <#else>
    <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-review__notification"] attr={"data-cm-notification": '{"path": ""}'} />
  </#if>
  <#if ["default", "undecided", "rejected"]?seq_contains(reviewView)>
    <div class="cm-review__title" itemprop="summary">${title!""}</div>
    <div class="cm-review__text cm-readmore" data-cm-readmore='{"lines": 5, "text": "${bp.getMessage(es.messageKeys.REVIEW_MORE)}"}'>
      <div class="cm-readmore__wrapper" itemprop="description">
        <@cm.unescape self.textAsHtml />
      </div>
      <div class="cm-readmore__buttonbar">
        <@bp.button baseClass="" text=bp.getMessage(es.messageKeys.REVIEW_MORE) attr={"class": "cm-readmore__button-more"} />
        <@bp.button baseClass="" text=bp.getMessage(es.messageKeys.REVIEW_LESS) attr={"class": "cm-readmore__button-less"} />
      </div>
    </div>
  </#if>

  <#--
  <#if ["default", "undecided"]?seq_contains(reviewView) && reviewingAllowed!false>
    <div class="cm-review__toolbar cm-toolbar cm-toolbar--reviews">
      <@bp.button text=bp.getMessage(es.messageKeys.REVIEW_FORM_LABEL_REPLY) iconClass="icon-pencil" attr={"data-cm-button--review": '{"replyTo": "${self.id}"}'} />
      <@bp.button text=bp.getMessage(es.messageKeys.REVIEW_FORM_LABEL_QUOTE) iconClass="icon-quotes" attr={"data-cm-button--review": '{"replyTo": "${self.id}", "quote": {"author": "${(strAuthorName!"")?json_string}", "date": "${strDate?json_string}", "text": "${self.text?json_string}"}}'} />
    </div>
  </#if>-->
</div>
