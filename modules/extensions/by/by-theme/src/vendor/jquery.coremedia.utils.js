(function ($) {
/**
* Same as jQuery.find but also includes the target(s) on which findAndSelf was called.
*
* @param selector
* @returns {jQuery}
*/
$.fn.findAndSelf = function (selector) {
return this.find("*").addBack().filter(selector);
};
})(coremedia.blueprint.$);