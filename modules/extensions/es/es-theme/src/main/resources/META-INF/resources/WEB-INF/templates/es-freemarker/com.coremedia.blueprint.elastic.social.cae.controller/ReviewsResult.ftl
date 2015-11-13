<#-- @ftlvariable name="self" type="com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult" -->

<#if self.isEnabled()>
  <#assign reviewsId=bp.generateId("cm-reviews-") />
  <div class="cm-reviews" id="${reviewsId}" data-cm-refreshable-fragment='{"url": "${cm.getLink(self)}"}'>

    <#-- review summary -->
    <#assign numberOfOnlineReviews=self.getNumberOfOnlineReviews()!0 />
    <#if (numberOfOnlineReviews > 0)>
      <div class="cm-reviews__average-rating cm-ratings-average" itemscope="itemscope" itemtype="http://data-vocabulary.org/Review-aggregate">
  
        <#assign averageRating=(self.getAverageRating())!0 />
        <#assign averageRatingRounded=averageRating?round />
  
        <div class="cm-ratings-average__header">
          <button class="cm-ratings-average__switch" data-cm-switch-average-rating=""></button>
  
          <span class="cm-ratings-average__rating cm-rating">
            <#list es.getReviewMaxRating()..1 as currentRating>
              <#assign classRatingIndicator="" />
              <#if currentRating == averageRatingRounded>
                <#assign classRatingIndicator=" cm-rating-indicator--active" />
              </#if>
                <div class="cm-rating__option cm-rating-indicator${classRatingIndicator}">${currentRating}</div>
            </#list>
          </span>
          <span class="cm-ratings-average__text" itemprop="rating" itemscope="itemscope" itemtype="http://data-vocabulary.org/Rating">
            <@bp.message es.messageKeys.REVIEWS_AVERAGE_SYMBOL /> <span itemprop="average">${averageRating?string("0.##")}</span> <@bp.message es.messageKeys.REVIEWS_AVERAGE_OUT_OF /> <span itemprop="best">${es.getReviewMaxRating()}</span>
          </span>
          <span class="cm-ratings-average__votes">
            <#assign ratingsLabel="" />
            <#if (numberOfOnlineReviews == 1)>
              <#assign ratingsLabel=bp.getMessage(es.messageKeys.REVIEWS_AVERAGE_RATINGS_SINGULAR) />
            <#else>
              <#assign ratingsLabel=bp.getMessage(es.messageKeys.REVIEWS_AVERAGE_RATINGS) />
            </#if>
            (<span itemprop="votes">${numberOfOnlineReviews}</span> ${ratingsLabel})
          </span>
        </div>
  
        <table class="cm-ratings-average__details cm-rating-statistics">
          <#assign maxNumber=0 />
          <#list es.getReviewMaxRating()..1 as currentRating>
            <#assign currentNumber=(self.getNumberOfOnlineReviewsFor(currentRating)!0) />
            <#if (currentNumber > maxNumber)>
              <#assign maxNumber=currentNumber />
            </#if>
          </#list>
          <#list es.getReviewMaxRating()..1 as currentRating>
            <#assign currentNumber=(self.getNumberOfOnlineReviewsFor(currentRating))!0 />
            <#assign percentage=0 />
            <#if (maxNumber > 0)>
              <#assign percentage=(currentNumber * 100 / maxNumber) />
            </#if>
            <#assign indicatorLabel="" />
            <#if (currentRating == 1)>
              <#assign indicatorLabel=bp.getMessage(es.messageKeys.REVIEWS_AVERAGE_INDICATOR_SINGULAR) />
            <#else>
              <#assign indicatorLabel=bp.getMessage(es.messageKeys.REVIEWS_AVERAGE_INDICATOR) />
            </#if>
            <tr class="cm-rating-statistic">
              <td class="cm-rating-statistic__column">${currentRating} ${indicatorLabel}</td>
              <td class="cm-rating-statistic__column cm-rating-statistic__column--rating-bar"><div class="cm-rating-bar"><div class="cm-rating-bar__filled" style="width: ${percentage}%;"></div></div></td>
              <td class="cm-rating-statistic__column">${currentNumber}</td>
            </tr>
          </#list>
        </table>
  
        <#-- hidden elements -->
        <span itemprop="count" class="cm-visuallyhidden">${numberOfOnlineReviews}</span>
        <#if self.target?has_content>
          <span itemprop="itemreviewed" class="cm-visuallyhidden">${self.target!""}</span>
        </#if>
      </div>
    <#else>
      <#if !self.isReadOnly()>
        <h3 class="cm-reviews__title cm-heading3">
          <#assign numberOfReviews=self.getNumberOfOnlineReviews()!0 />
          <#switch numberOfReviews>
            <#case 0>
              <@bp.message es.messageKeys.REVIEWS_NO_REVIEWS />
              <#break>
            <#case 1>
              <@bp.message es.messageKeys.REVIEWS_HEADLINE_SINGULAR />
              <#break>
            <#default>
              <@bp.message key=es.messageKeys.REVIEWS_HEADLINE args=[numberOfReviews] />
          </#switch>
        </h3>
      </#if>
    </#if>
  
    <#-- write a review -->
    <#if self.isWritingContributionsAllowed()>
      <#-- output of dynamic, non-review specific information -->
      <@bp.notification type="inactive" text="" dismissable=false additionalClasses=["cm-reviews__notification"] attr={"data-cm-notification": '{"path": ""}'} />
      <#if (es.hasUserWrittenReview(self.getTarget()))!false>
        <div class="cm-reviews__toolbar cm-toolbar cm-toolbar--reviews">
          <@bp.button text=bp.getMessage(es.messageKeys.REVIEWS_WRITE) attr={"data-cm-button--review": '{"disabled": true}', "classes": ["cm-button--disabled"], "disabled": ""} />
        </div>
        <@bp.notification type="info" text=bp.getMessage(es.messageKeys.REVIEW_FORM_ALREADY_REVIEWED) dismissable=false additionalClasses=["cm-reviews__notification"] attr={"data-cm-reviews-notification-type": "ALREADY_REVIEWED"} />
      <#else>
        <div class="cm-reviews__toolbar cm-toolbar cm-toolbar--reviews">
          <@bp.button text=bp.getMessage(es.messageKeys.REVIEWS_WRITE) attr={"data-cm-button--review": ""} />
        </div>
        <@cm.include self=self view="reviewForm" />
      </#if>
    <#elseif self.isWritingContributionsEnabled() && es.isAnonymousUser()>
      <@bp.notification type="info" text=bp.getMessage(es.messageKeys.REVIEW_FORM_NOT_LOGGED_IN) dismissable=false additionalClasses=["cm-reviews__notification"] attr={"data-cm-reviews-notification-type": "LOGIN_REQUIRED"} />
      <#assign loginFlow=es.getLogin() />
      <#if (loginFlow != cm.UNDEFINED)>
        <div class="cm-reviews__toolbar cm-toolbar cm-toolbar--reviews">
          <@cm.include self=loginFlow view="asButtonGroup" />
        </div>
      </#if>
    </#if>
  
    <#assign reviews=self.reviews />
      <#if reviews?has_content>
          <ul class="cm-collection cm-collection--reviews cm-reviews__list">
            <#list reviews as wrapper>
              <@cm.include self=wrapper!cm.UNDEFINED view="asListItem" params={"reviewsResult": self} />
            </#list>
          </ul>
      </#if>
  </div>
</#if>
