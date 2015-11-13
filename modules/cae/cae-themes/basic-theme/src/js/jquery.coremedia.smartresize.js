/**
 * Smart Resize Plugin
 * Fires the resize event only after a treshhold of 200ms
 * see: http://www.paulirish.com/2009/throttled-smartresize-jquery-event-handler/  *
 *
 * Version 1.0
 * Updated 05/23/2013
 *
 * Copyright (c) 2013 CoreMedia AG
 *
 * Usage:
 * $(window).smartresize(function(){
 *   // code that takes it easy...
 * });
 *
 */
(function ($, sr) {

  // debouncing function from John Hann
  // http://unscriptable.com/index.php/2009/03/20/debouncing-javascript-methods/
  var debounce = function (func, threshold, execAsap) {
    var timeout;

    return function debounced() {
      var obj = this;

      function delayed() {
        if (!execAsap) {
          func.apply(obj, arguments);
        }
        timeout = null;
      }

      if (timeout) {
        clearTimeout(timeout);
      }
      else if (execAsap) {
        func.apply(obj, arguments);
      }

      timeout = setTimeout(delayed, threshold || 200);
    };
  };
  // smartresize
  $.fn[sr] = function (fn) {
    return fn ? this.bind('resize', debounce(fn)) : this.trigger(sr);
  };

})(coremedia.blueprint.$, 'smartresize');
