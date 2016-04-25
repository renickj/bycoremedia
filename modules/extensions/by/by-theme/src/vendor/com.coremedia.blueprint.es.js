/* --- create own namespace in javascript for own stuff ------------------------------------------------------------- */

var coremedia = (function (module) {

return module;

}(coremedia || {}));

coremedia.blueprint = (function (module) {

return module;

}(coremedia.blueprint || {}));

coremedia.blueprint.es = function (module) {

var $ = coremedia.blueprint.$;

var $document = $(document);

var EVENT_PREFIX = "coremedia.es.";

module.EVENT_FORM_CLOSE = EVENT_PREFIX + "formClose";

module.EVENT_FORM_SUBMIT = EVENT_PREFIX + "formSubmit";

module.EVENT_MODEL_INFO = EVENT_PREFIX + "modelInfo";

module.EVENT_TOGGLE_AVERAGE_RATING = EVENT_PREFIX + "toggleAverageRating";

var NOTIFICATION_TYPES = ["info", "error", "warning", "success"];

var NOTIFICATION_IDENTIFIER = "cm-notification";

/**

* Decorates given container with notifications based on list of messages given.

* Messages need to have the following structure:

* {type: {String}, path: {undefined|String}, text: {string}}

*

* @param container the node to be decorated

* @param messages the messages to apply.

*/

module.addNotifications = function (container, messages) {

var $container = $(container);

var $notificationByPath = {};

// create a list of notification hooks by path

var selector = "[data-" + NOTIFICATION_IDENTIFIER + "]";

$container.findAndSelf(selector).each(function () {

var $this = $(this);

var config = $.extend({path: ""}, $this.data(NOTIFICATION_IDENTIFIER));

$notificationByPath[config.path] = $this;

});

// iterate over all given messages

for (var i = 0; i < messages.length; i++) {

var message = $.extend({type: "info", path: undefined, text: ""}, messages[i]);

if (message.path === undefined) {

message.path = "";

}

// find notification in map

var $notification = $notificationByPath[message.path];

if ($notification !== undefined) {

// assign information to notification and make it visible

$notification.find("." + NOTIFICATION_IDENTIFIER + "__text").html(message.text);

if (NOTIFICATION_TYPES.indexOf(message.type) > -1) {

$notification.addClass(NOTIFICATION_IDENTIFIER + "--" + message.type);

}

$notification.removeClass(NOTIFICATION_IDENTIFIER + "--inactive");

}

}

};

/**

* Clears all notifications from the given container

*

* @param container the node to be cleared

*/

module.clearNotifications = function (container) {

var $container = $(container);

var $notifications = $container.find("[data-" + NOTIFICATION_IDENTIFIER + "]");

for (var i = 0; i < NOTIFICATION_TYPES.length - 1; i++) {

$notifications.removeClass(NOTIFICATION_IDENTIFIER + "--" + NOTIFICATION_TYPES[i]);

}

$notifications.addClass(NOTIFICATION_IDENTIFIER + "--inactive");

};

// apply confirm functionality to all elements rendered with necessary information

coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({message: undefined}, "cm-button--confirm", function ($target, config) {

if (config.message !== undefined) {

$target.bind("click", function () {

return confirm(config.message);

});

}

});

var FORM_IDENTIFIER = "cm-form";

/**

* Starts a form submit (prevent double submitting)

* If submitting is done without page reload (e.g. ajax) formSubmitEnd has to be called once finished.

*

* @returns {boolean} TRUE if start was successfull

*/

module.formSubmitStart = function (form) {

var $form = $(form);

var result = $form.hasClass(FORM_IDENTIFIER + "--progress");

$form.addClass(FORM_IDENTIFIER + "--progress");

return !result;

};

/**

* Ends a form submit (prevent double submitting)

* Only used if submitting is done without page reload (e.g. ajax)

*

* @returns {boolean} TRUE if end was successfull

*/

module.formSubmitEnd = function (form) {

var $form = $(form);

var result = $form.hasClass(FORM_IDENTIFIER + "--progress");

$form.removeClass(FORM_IDENTIFIER + "--progress");

return result;

};

// TODO refactor

$("#timezone").val(coremedia.blueprint.basic.timezone.determine_timezone().name());

var ES_AJAX_FORM_IDENTIFIER = "cm-es-ajax-form";

// activate es ajax forms

coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({}, ES_AJAX_FORM_IDENTIFIER, function ($form) {

$form.on("submit", function (ev) {

ev.preventDefault();

if (module.formSubmitStart($form)) {

coremedia.blueprint.es.clearNotifications($form);

$form.trigger(module.EVENT_FORM_SUBMIT);

$.ajax({

type: $form.attr("method"),

url: $form.attr("action"),

data: $form.serialize(),

headers: {'X-Requested-With': 'XMLHttpRequest'},

xhrFields: { withCredentials: true },

dataType: "json"

}).done(function (result) {

result = $.extend({success: false, messages: [], id: undefined}, result);

if (result.success) {

$form.trigger(module.EVENT_MODEL_INFO, [result]);

} else {

coremedia.blueprint.es.addNotifications($form, result.messages);

}

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

}).fail(function () {

// @TODO if more messages cannot be localized a concept is needed for providing resource bundles on client side

coremedia.blueprint.es.addNotifications($form, [{type: "error", "text": "Due to an internal error, comment could not be posted."}]);

}).always(function () {

module.formSubmitEnd($form);

});

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

}

});

});

// activate cancel functionality for es forms

coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({}, "cm-button--cancel", function ($button) {

$button.on("click", function () {

$button.trigger(module.EVENT_FORM_CLOSE);

});

});

var COMMENTS_IDENTIFIER = "cm-comments";

var NEW_COMMENT_IDENTIFIER = "cm-new-comment";

// activate write a comment functionality for buttons (not the submit button, just for displaying the form)

coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({replyTo: undefined, quote: {author: undefined, date: undefined, text: undefined}}, "cm-button--comment", function ($commentButton, config) {

$commentButton.on("click", function () {

var $comments = $commentButton.closest("." + COMMENTS_IDENTIFIER);

// deactivate all active buttons due to form element being reused

$comments.find(".cm-toolbar--comments").removeClass("cm-toolbar--inactive");

var $toolbar = $commentButton.closest(".cm-toolbar--comments");

$toolbar.addClass("cm-toolbar--inactive");

var $container = $comments.find("." + COMMENTS_IDENTIFIER + "__new-comment");

// reset form

$container.find("." + NEW_COMMENT_IDENTIFIER + "__form").each(function () { this.reset(); module.clearNotifications(this); });

$container.addClass(NEW_COMMENT_IDENTIFIER + "--active");

var $replyToField = $container.find("[name='replyTo']");

var $commentField = $container.find("[name='comment']");

var commentField = $commentField[0];

$replyToField.val(config.replyTo || "");

if (config.quote.text !== undefined) {

$commentField.val("[quote author='" + config.quote.author.replace("'", "\\''") + "' date='" + config.quote.date.replace("'", "\\''") + "']" + config.quote.text + "[/quote]\n");

}

$toolbar.after($container);

$commentField.focus();

// function exists in non IE browsers

if (commentField.setSelectionRange) {

// non IE

var len = $commentField.val().length;

commentField.setSelectionRange(len, len);

} else {

// IE

$commentField.val($commentField.val());

}

commentField.scrollTop = commentField.scrollHeight;

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

});

// activate functionality for new comment form

coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector("." + NEW_COMMENT_IDENTIFIER, function ($newCommentWidget) {

// catch es ajax form events

$newCommentWidget.findAndSelf("form." + NEW_COMMENT_IDENTIFIER + "__form").each(function () {

var $newCommentForm = $(this);

var $commentsWidget = $newCommentForm.closest("." + COMMENTS_IDENTIFIER);

$newCommentForm.on(module.EVENT_FORM_SUBMIT, function () {

coremedia.blueprint.es.clearNotifications($commentsWidget);

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

$newCommentForm.on(module.EVENT_MODEL_INFO, function (event, handlerInfo) {

if (handlerInfo.success) {

coremedia.blueprint.basic.refreshFragment($commentsWidget, function ($commentsWidgetRefreshed) {

if (handlerInfo.id !== undefined) {

var $comment = $commentsWidgetRefreshed.find("[data-cm-comment-id='" + handlerInfo.id + "']");

coremedia.blueprint.es.addNotifications($comment, handlerInfo.messages);

$("html, body").animate({

scrollTop: $comment.offset().top

}, 500);

} else {

// fallback if no id is provided

coremedia.blueprint.es.addNotifications($commentsWidgetRefreshed, handlerInfo.messages);

}

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

}

});

});

// activate cancel functionality for comment form

$newCommentWidget.on(module.EVENT_FORM_CLOSE, function () {

$newCommentWidget.removeClass(NEW_COMMENT_IDENTIFIER + "--active");

$newCommentWidget.closest("." + COMMENTS_IDENTIFIER).find(".cm-toolbar--comments").removeClass("cm-toolbar--inactive");

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

});

var REVIEWS_IDENTIFIER = "cm-reviews";

var NEW_REVIEW_IDENTIFIER = "cm-new-review";

// activate write a comment functionality for buttons (not the submit button, just for displaying the form)

coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({disabled: false}, "cm-button--review", function ($reviewButton, config) {

if (!config.disabled) {

$reviewButton.on("click", function () {

var $reviews = $reviewButton.closest("." + REVIEWS_IDENTIFIER);

// deactivate all active buttons due to form element being reused

$reviews.find(".cm-toolbar--reviews").removeClass("cm-toolbar--inactive");

var $toolbar = $reviewButton.closest(".cm-toolbar--reviews");

$toolbar.addClass("cm-toolbar--inactive");

var $container = $reviews.find("." + REVIEWS_IDENTIFIER + "__new-review");

// reset form

$container.find("." + NEW_REVIEW_IDENTIFIER + "__form").each(function () {

this.reset();

module.clearNotifications(this);

});

$container.addClass(NEW_REVIEW_IDENTIFIER + "--active");

var $reviewField = $container.find("[name='review']");

$toolbar.after($container);

$reviewField.focus();

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

}

});

// activate functionality for new review form

coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector("." + NEW_REVIEW_IDENTIFIER, function ($newReviewWidget) {

// catch form submit for review functionality and replace it with ajax call

$newReviewWidget.findAndSelf("form." + NEW_REVIEW_IDENTIFIER + "__form").each(function () {

var $newReviewForm = $(this);

var $reviewsWidget = $newReviewForm.closest("." + REVIEWS_IDENTIFIER);

$newReviewForm.on(module.EVENT_FORM_SUBMIT, function () {

coremedia.blueprint.es.clearNotifications($reviewsWidget);

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

$newReviewForm.on(module.EVENT_MODEL_INFO, function (event, modelInfo) {

if (modelInfo.success) {

coremedia.blueprint.basic.refreshFragment($reviewsWidget, function ($reviewsWidgetRefreshed) {

if (modelInfo.id !== undefined) {

var $review = $reviewsWidgetRefreshed.find("[data-cm-review-id='" + modelInfo.id + "']");

coremedia.blueprint.es.addNotifications($review, modelInfo.messages);

$("html, body").animate({

scrollTop: $review.offset().top

}, 500);

} else {

// fallback if no id is provided

coremedia.blueprint.es.addNotifications($reviewsWidgetRefreshed, modelInfo.messages);

}

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

}

});

});

$newReviewWidget.on(module.EVENT_FORM_CLOSE, function () {

$newReviewWidget.removeClass(NEW_REVIEW_IDENTIFIER + "--active");

$newReviewWidget.closest("." + REVIEWS_IDENTIFIER).find(".cm-toolbar--reviews").removeClass("cm-toolbar--inactive");

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

});

// initialize reviews widget

coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-ratings-average", function ($target) {

$target.on(module.EVENT_TOGGLE_AVERAGE_RATING, function() {

$target.toggleClass("cm-ratings-average--active");

});

});

coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({}, "cm-switch-average-rating", function ($target) {

$target.on("click", function () {

$target.trigger(module.EVENT_TOGGLE_AVERAGE_RATING);

});

});

return module;

}(coremedia.blueprint.es || {});
s