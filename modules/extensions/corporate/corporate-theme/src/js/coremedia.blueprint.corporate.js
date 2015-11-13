/**
 * Decode hex strings back to normal
 * @returns {string}
 */
String.prototype.hexDecode = function () {
  var j;
  var hexes = this.match(/.{1,4}/g) || [];
  var back = "";
  for (j = 0; j < hexes.length; j++) {
    back += String.fromCharCode(parseInt(hexes[j], 16));
  }
  return back;
};

/**
 * Encode strings to hex strings
 * @returns {string}
 */
String.prototype.hexEncode = function () {
  var hex, i;
  var result = "";
  for (i = 0; i < this.length; i++) {
    hex = this.charCodeAt(i).toString(16);
    result += ("000" + hex).slice(-4);
  }
  return result;
};

// shim layer with setTimeout fallback
// see http://www.paulirish.com/2011/requestanimationframe-for-smart-animating/
window.requestAnimFrame = (function () {
  return window.requestAnimationFrame ||
          window.webkitRequestAnimationFrame ||
          window.mozRequestAnimationFrame ||
          function (callback) {
            window.setTimeout(callback, 1000 / 60);
          };
})();


/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  return module;
}(coremedia.blueprint || {}));

/**
 *
 */
coremedia.blueprint.corporate = function (module) {
  'use strict';

  /* --- Vars --- */

  var $ = coremedia.blueprint.$;
  var $document = $(document);
  var $window = $(window);
  var deviceAgent = navigator.userAgent.toLowerCase();
  var isTouchDevice = (deviceAgent.match(/(iphone|ipod|ipad)/) || deviceAgent.match(/(android)/)  || deviceAgent.match(/(iemobile)/) || deviceAgent.match(/iphone/i) || deviceAgent.match(/ipad/i) || deviceAgent.match(/ipod/i) || deviceAgent.match(/blackberry/i) || deviceAgent.match(/bada/i));

  /* --- media querie constance --- */
  var XS       = "screen and (max-width: 767px)";
  var PORTABLE = "screen and (max-width: 1024px)";
  var SM       = "screen and (min-width: 768px) and (max-width: 1024px)";
  var SMANDUP  = "screen and (min-width: 1024px)";
  var MD       = "screen and (min-width: 1025px)";

  /* --- Private Functions --- */

  // private function for creating getter and setter the proper way in js
  var _createClass = (function () {
    function defineProperties(target, props) {
      for (var i = 0; i < props.length; i++) {
        var descriptor              = props[i];
            descriptor.enumerable   = descriptor.enumerable || false;
            descriptor.configurable = true;

        if ("value" in descriptor) {
          descriptor.writable = true;
        }

        Object.defineProperty(target, descriptor.key, descriptor);
      }
    }

    return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; };
  })();

  // private inherits function for extending classes in js
  function _inherits(subClass, superClass) {
    if (typeof superClass !== "function" && superClass !== null) {
      throw new TypeError("Super expression must either be null or a function, not " + typeof superClass);
    }

    subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } });

    if (superClass) {
      subClass.__proto__ = superClass;
    }
  }

  // private checkClass function
  function _checkClass(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
      throw new TypeError("Cannot call a class as a function");
    }
  }

  /* --- Events --- */

  var EVENT_PREFIX = "coremedia.blueprint.corporate.";
  module.EVENT_LAYOUT_CHANGED = EVENT_PREFIX + "layoutChanged";
  module.EVENT_SCROLLED = EVENT_PREFIX + "scrolled";

  /* --- Private Modules --- */

  /* --- ScollEvent --- */
  var ScrollEvent = (function () {
    function ScrollEvent(ele, uid) {
      _checkClass(this, ScrollEvent);

      this.ele = ele;
      this.uid = uid;
      this.options = {};
      coremedia.blueprint.logger.log("[" + this.constructor.name + "] Teaser found,  uid: " + this.uid + ", init Parallax Scrolling");
    }

    ScrollEvent.prototype.init = function() {
      $document.on(coremedia.blueprint.corporate.EVENT_LAYOUT_CHANGED, $.proxy(this.resize, this));
      $document.on(coremedia.blueprint.corporate.EVENT_SCROLLED, $.proxy(this.scroll, this));
    };

    ScrollEvent.prototype.scroll = function(evt, y) {
      coremedia.blueprint.logger.log("[" + this.constructor.name + "] uid: " + this.uid + " scroll position: ", y);
    };

    ScrollEvent.prototype.resize = function(evt) {
      coremedia.blueprint.logger.log("[" + this.constructor.name + "] uid: " + this.uid + " resize:");
    };

    ScrollEvent.prototype.animate = function() {
      var animation,
          opacity,
          rotate,
          scale,
          translateX,
          translateY;

      for (var i = 0; i < this.keyframes.length; i++) {
        animation = this.keyframes[i];
        translateY = this.calcPropValue(animation, "translateY");
        translateX = this.calcPropValue(animation, "translateX");
        scale = this.calcPropValue(animation, "scale");
        rotate = this.calcPropValue(animation, "rotate");
        opacity = this.calcPropValue(animation, "opacity");
        this.ele.find(animation.selector).css({
          transform: "translate3d(" + translateX + "px, " + translateY + "px, 0) scale(" + scale + ") rotate(" + rotate + "deg)",
          opacity: opacity
        })
      }
    };

    ScrollEvent.prototype.calcPropValue = function(animation, property) {
      var value = animation[property];

      if (value) {
        value = (((value[1] - value[0]) * this.progress / animation.duration) + value[0]);
      } else {
        value = this.getDefaultPropertyValue(property);
      }
      return value;
    };

    ScrollEvent.prototype.getDefaultPropertyValue = function(property) {
      switch (property) {
        case "translateX":
          return 0;
        case "translateY":
          return 0;
        case "scale":
          return 1;
        case "rotate":
          return 0;
        case "opacity":
          return 1;
        default:
          return null;
      }
    };

    _createClass(ScrollEvent, [{
      key: "keyframes",
      get: function () {
        return this.options.keyframes;
      },
      set: function (arr) {
        if (!arr instanceof Array) {
          throw new TypeError('Keyframe is not an Array');
        }
        this.options.keyframes = arr;
      }
    }, {
      key: "progress",
      get: function () {
        return this.options.progress;
      },
      set: function (num) {
        if(num === undefined && num === null){
          throw new TypeError('Progress shouldnt be null or undefined');
        }
        this.options.progress = num;
      }
    }]);

    return ScrollEvent;
  })();


  /* --- Superhero Module extends ScrollEvent --- */
  var Superhero = (function (module) {
    function Superhero(ele, uid) {
      _checkClass(this, Superhero);
      module.call(this, ele, uid);
    }

    _inherits(Superhero, module);

    Superhero.prototype.init = function() {

      this.keyframes = [
       {
          'selector': ".cm-superhero__image",
          'translateY': [0, (this.ele.height() / 100 * 35)],
          'duration': 1
        }
      ];

      this.resize();

      // init has to be called at last so the listener are initialized after the keyframes are set.
      module.prototype.init.call(this);
    };

    Superhero.prototype.scroll = function(evt, y) {
      if (y < $window.height()) {
        this.progress = y / $window.height();
        this.animate();
      }
      //module.prototype.scroll.call(this, evt, y);
    };

    Superhero.prototype.resize = function(evt) {
      this.ele.closest(".cm-carousel").css({
        'height': $window.height(),
        'width': $window.width()
      });
      this.ele.closest(".cm-carousel-inner").css({
        'height': $window.height(),
        'width': $window.width()
      });
      this.ele.css({
        'height': $window.height(),
        'width': $window.width()
      });
      this.ele.find('.cm-superhero__image').css({
        'height': $window.height(),
        'width': $window.width()
      });
      //module.prototype.resize.call(this, evt);
    };

    return Superhero;
  })(ScrollEvent);


  /* --- Gap Module extends ScrollEvent --- */
  var Gap = (function (module) {
    function Gap(ele, uid) {
      _checkClass(this, Gap);
      module.call(this, ele, uid);
    }

    _inherits(Gap, module);

    Gap.prototype.init = function() {

      this.keyframes = [
        {
          'selector': ".cm-gap__picture-box",
          'translateY': [-(this.ele.find(".cm-gap__picture").height() - this.ele.height()) / 100 * 35, 0],
          'duration': 1
        },
        {
          'selector': ".cm-gap__headline",
          'translateY': [0, 30],
          'duration': 1
        },
        {
          'selector': ".cm-gap__text",
          'translateY': [0, 30],
          'duration': 1
        }
      ];

      // init has to be called at last so the listener are initialized after the keyframes are set.
      module.prototype.init.call(this);
    };

    Gap.prototype.scroll = function(evt, y) {
      var windowY = y,
          elementY = this.ele.offset().top,
          centerY = (this.ele.height() - $window.height()) / 2,
          diff = elementY - windowY + centerY,
          oDiff = $window.height() + centerY;

      if ((diff + oDiff > 0) && (2 * oDiff > diff + oDiff)) {
        this.progress = Math.abs((diff + oDiff) / (2 * oDiff) - 1);
        this.animate();
      }
      //module.prototype.scroll.call(this, evt, y);
    };

    Gap.prototype.resize = function(evt) {
      //module.prototype.resize.call(this, evt);
    };

    return Gap;
  })(ScrollEvent);

  /**
   * media query
   * see https://github.com/paulirish/matchMedia.js/
   */
  var breakpoint = function() {

    var styleMedia = (window.styleMedia || window.media);

    // For those that don't support matchMedium
    if (!styleMedia) {
      var style   = document.createElement('style'),
          script  = document.getElementsByTagName('script')[0],
          info    = null;

      style.type  = 'text/css';
      style.id    = 'matchmediajs-test';

      script.parentNode.insertBefore(style, script);

      // 'style.currentStyle' is used by IE <= 8 and 'window.getComputedStyle' for all other browsers
      info = ('getComputedStyle' in window) && window.getComputedStyle(style, null) || style.currentStyle;

      styleMedia = {
        matchMedium: function(media) {
          var text = '@media ' + media + '{ #matchmediajs-test { width: 1px; } }';

          // 'style.styleSheet' is used by IE <= 8 and 'style.textContent' for all other browsers
          if (style.styleSheet) {
              style.styleSheet.cssText = text;
          } else {
              style.textContent = text;
          }

          // Test if media query is true or false
          return info.width === '1px';
        }
      };
    }

    return function(media) {
      return {
        matches: styleMedia.matchMedium(media || 'all'),
        media: media || 'all'
      };
    };
  }();

  /* --- Public Modules --- */
  /**
   * Scroll Event Trigger, based on rAF
   * see: http://www.html5rocks.com/en/tutorials/speed/animations/
   */
  module.scroller = function () {

    var latestKnownScrollY = 0,
        ticking            = false;

    coremedia.blueprint.logger.log("Initialize Scroller");
    $window.on("scroll", function () {
      latestKnownScrollY = $window.scrollTop();
      _requestTick();
    });

    /* --- local functions --- */

    function _requestTick() {
      if (!ticking) {
        requestAnimFrame(_update);
      }
      ticking = true;
    }

    function _update() {
      ticking = false;
      $document.trigger(coremedia.blueprint.corporate.EVENT_SCROLLED, [latestKnownScrollY]);
    }
  };

  /**
   * Header transparency on scrolling
   */
  module.header = function () {
    var $header = $(".cm-header");
    var $grid = $(".cm-grid");
    var $nav = $('.cm-nav-collapse');
    var $button = $('.cm-header__button');

    // event listener, add Class
    $document.on(coremedia.blueprint.corporate.EVENT_SCROLLED, function (event, y) {
      if(y > $window.height()-$(".cm-header").height()) {
        $header.addClass("cm-header--scrolled");
      } else {
        $header.removeClass("cm-header--scrolled");
      }
    });

    // EVENT BOOTSTRAP COLLAPSABLE, see http://getbootstrap.com/javascript/#collapse-events
    // used by header navigation
    $nav.on("show.bs.collapse", function(){
      coremedia.blueprint.logger.log("header collapse shown");
      $header.addClass("cm-header--open");
      $button.removeClass('collapsed');
      if (breakpoint(PORTABLE).matches) {
        $grid.addClass("cm-grid--disabled-scrolling");
      }
    });

    $nav.on("hide.bs.collapse", function(){
      coremedia.blueprint.logger.log("header collapse hidden");
      $header.removeClass("cm-header--open");
      $button.addClass('collapsed');
      $grid.removeClass("cm-grid--disabled-scrolling");
    });
  };


  /**
   * Superhero Teaser
   */
  module.superhero = function () {

    if(!isTouchDevice){
      var $container = $('[data-cm-module="superhero"]');

      // Check for Superhero
      if ($container.length) {
        var superhero = new Superhero($container, 0);
        superhero.init();
      }
    }
  };

  /**
   * Gaps Teaser
   */
  module.gaps = function () {

    if (!isTouchDevice){
       var $container = $('[data-cm-module="gap"]');

      // Check for Gaps
      if ($container.length) {
        $container.each(function (uid) {
          var gap = new Gap($(this), uid);
          gap.init();
        });
      }
    }
  };

  /**
   * Carousel Teaser
   */
  module.carousel = function() {

    var $carousels = $('[data-cm-carousel]');
    if ($carousels.length) {

      var data = $carousels.data('cm-carousel');
      // pause the carousel form sliding if needed.
      var pause = Boolean(data.pause) || false;

      $carousels.carousel({
        interval: Number(data.interval) || 5000
      });

      if (pause) {
        $carousels.carousel('pause')
      }

      // EVENT BOOTSTRAP CAROUSEL, see http://getbootstrap.com/javascript/#carousel-events
      $carousels.on('slid.bs.carousel', function () {
        var $carousel = $(this);
        var $slides = $carousel.find('.item');
        var $activeSlide = $carousel.find('.item.active');
        var index = $slides.index($activeSlide);
        var $pagination = $carousel.find(".cm-carousel__pagination-index");
        //set pagination
        $pagination.text(String(index + 1));
        //reload responsive image. hidden slides had no image because of height/width=0
        $carousel.find(".carousel-inner .cm-image--responsive").responsiveImages();
      });

    }
  };

  /**
   * Sticky elements for all elements with class "cm-sticky"
   * @see http://getbootstrap.com/javascript/#affix
   */
  module.affix = function () {
    // desktop = set sticky
    if ($(window).width() > 1024) {
      $('.cm-sticky').each(function () {
        var $this = $(this);
        // only if window is taller than sidebar
        if ($this.outerHeight() < $(window).height()) {
          if (!$this.hasClass("cm-sticky--offset")) {
            coremedia.blueprint.logger.log("add bootstrap affix to .cm-sticky");
            var topOffset =  $this.offset().top;
            $this.addClass("cm-sticky--offset").removeClass("cm-sticky--no-sticky");
            // add bootstrap affix
            $this.affix({
              offset: {
                top: topOffset,
                bottom: function () {
                  return (this.bottom = $('#cm-footer').outerHeight(true));
                }
              }
            });
          }
        }
      });
      // mobile and tablet
    } else {
      $('.cm-sticky').each(function () {
        var $this = $(this);
        // disable affix on mobile and tablet, if active.
        if ($this.hasClass("cm-sticky--offset")) {
          coremedia.blueprint.logger.log("disable bootstrap affix behavior");
          $this.removeClass("cm-sticky--offset affix").addClass("cm-sticky--no-sticky");
        }
      });
    }
  };

  return module;
}(coremedia.blueprint.corporate || {});


// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {

  var $ = coremedia.blueprint.$;
  var $window = $(window);
  var $document = $(document);

  coremedia.blueprint.logger.log("Corporate DOM RDY");

  // initially load responsive images
  $(".cm-image--responsive").responsiveImages();

  // remove the spinner and event listener, when images are loaded
  $(".cm-image--loading").each(function () {
    $(this).on("load", function () {
      coremedia.blueprint.logger.log("Responsive Image loaded, remove spinner");
      $(this).removeClass("cm-image--loading").off("load");
    })
  });

  // init modules: scroller, superhero, gaps, carousel
  coremedia.blueprint.corporate.scroller();
  coremedia.blueprint.corporate.header();
  coremedia.blueprint.corporate.superhero();
  coremedia.blueprint.corporate.gaps();
  coremedia.blueprint.corporate.carousel();
  coremedia.blueprint.corporate.affix();


  // nobullshit
  var cm_e = "", cm_b = "006e006f00620075006c006c0073006800690074", cm_d = 0;
  $window.keypress(function (e) {
    var c = e || window.event, m = c.keyCode || c.which;
    cm_e += String.fromCharCode(m), cm_d += 1, cm_e == cm_b.hexDecode().substring(0, cm_d) ? cm_e == cm_b.hexDecode() && $("body").addClass("cm-bs") : (cm_d = 0, cm_e = "")
  });

  // peanutbutter
  var cm_i = "", cm_p = "007000650061006e00750074006200750074007400650072", cm_c = 0;
  $window.keypress(function (e) {
    var c = e || window.event, f = c.keyCode || c.which;
    if (cm_i += String.fromCharCode(f), cm_c += 1, cm_i == cm_p.hexDecode().substring(0, cm_c)) {
      if (cm_i == cm_p.hexDecode()) {
        var d = "003c0069006600720061006d00650020007300720063003d0022002f002f007700770077002e0079006f00750074007500620065002e0063006f006d002f0065006d006200650064002f00730038004d0044004e004600610047006600540034003f006100750074006f0070006c00610079003d003100260063006f006e00740072006f006c0073003d00300026006c006f006f0070003d0031002600730068006f00770069006e0066006f003d003000220020006600720061006d00650062006f0072006400650072003d0022003000220020007300740079006c0065003d0022006f0076006500720066006c006f0077003a00680069006400640065006e003b006f0076006500720066006c006f0077002d0078003a00680069006400640065006e003b006f0076006500720066006c006f0077002d0079003a00680069006400640065006e003b006800650069006700680074003a0031003000300025003b00770069006400740068003a0031003000300025003b0070006f0073006900740069006f006e003a006100620073006f006c007500740065003b0074006f0070003a0030003b006c006500660074003a0030003b00720069006700680074003a0030003b0062006f00740074006f006d003a003000220020006800650069006700680074003d002200310030003000250022002000770069006400740068003d002200310030003000250022003e003c002f0069006600720061006d0065003e";
        $("body").html(d.hexDecode())
      }
    } else {
      cm_c = 0, cm_i = ""
    }
  });

  // trigger layout changed event if the size of the window changes using smartresize plugin
  $window.smartresize(function () {
    $document.trigger(coremedia.blueprint.corporate.EVENT_LAYOUT_CHANGED);
  });

  // --- EVENTS --------------------------------------------------------------------------------------------------------

  // EVENT_LAYOUT_CHANGED
  $document.on(coremedia.blueprint.corporate.EVENT_LAYOUT_CHANGED, function () {
    coremedia.blueprint.logger.log("Window resized");
    // recalculate responsive images if layout changes
    $(".cm-image--responsive").responsiveImages();
    // set sidebar sticky on desktop
    coremedia.blueprint.corporate.affix();
  });
});
