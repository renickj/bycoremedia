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
 *  CoreMedia Blueprint Javascript Framework Extension for Perfectchef
 */
coremedia.blueprint.perfectchef = function (module) {

  var $ = coremedia.blueprint.$;
  /**
   * @type {number} stores the last navigation width in order die identify if mega-menu width adjustments need to be recalculated.
   */
  var lastNavWidth = 0;

  /**
   * @type {string} specifies the selector for mega menu for adjustment of its children's widths to fit the whole space
   */
  var megaMenuSelector = ".mega-menu > li > ul";

  /**
   * @type {string} specifies the selector for marketing spot items for equalization of heights
   */
  var marketingSpotItemSelector = ".cm-collection--marketingspot .cm-collection__item .cm-teaser--text";

  /**
   * @type {string} defines the internal name for the mobile device
   */
  module.DEVICE_MOBILE = "mobile";

  /**
   * @type {string} defines the internal name of the tablet device
   */
  module.DEVICE_TABLET = "tablet";

  /**
   * @type {string} defines the internal name of the desktop device
   */
  module.DEVICE_DESKTOP = "desktop";

  /**
   * private function to adjust the size of non-adaptive content
   *
   * @param {jQuery} $content jQuery wrapped dom element to be adjusted
   * @param {number} baseRatio ratio that the content should be adjusted to
   * @param {number} boxRatio ratio the content currently has
   * @param {boolean} allowOverflow defines overflow behaviour of the adjustment:
   *                  if true content will be cut to fit the box after proper resizing
   *                  if false content will not be cut after proper resizing creating horizontal or veritical borders
   */
  var adjustNonAdaptiveContent = function ($content, baseRatio, boxRatio, allowOverflow) {
    var adjustment;

    // detect if a horizontal repositioning is needed
    if ((allowOverflow && baseRatio > boxRatio) || (!allowOverflow && baseRatio <= boxRatio)) {
      // horizontal repositioning is needed
      adjustment = (1 - baseRatio / boxRatio) / 2;
      $content.addClass("cm-non-adaptive-content--fit-height");
      // adjust positioning to the left to match the expected result using percentage (for responsive layout)
      $content.css("margin-top", "");
      $content.css("margin-left", (adjustment * 100) + "%");
    } else {
      // vertical repositioning is needed
      adjustment = ((1 / boxRatio - 1 / baseRatio) / 2);
      $content.removeClass("cm-non-adaptive-content--fit-height");
      // adjust positioning to the top to match the expected result using percentage (for responsive layout)
      $content.css("margin-top", (adjustment * 100) + "%");
      $content.css("margin-left", "");
    }
  };

  /**
   * set width of each item in the mega-menu depending to screen-size
   */
  module.setMegaMenuItemsWidth = function () {
    var $navMegaMenu = $(megaMenuSelector);
    // detect width of navigation
    var currentNavWidth = $navMegaMenu.width();
    // set width only if changed
    if (currentNavWidth != lastNavWidth) {
      // detect how much space each navigation item has keeping taking border of 1px for each element into account
      var $children = $navMegaMenu.children("li");
      var $childrenExceptLast = $children.not(":last");
      var $lastChild = $children.last();
      var numChildren = $children.length;
      var singleWidth = Math.floor(currentNavWidth / numChildren - 1);
      // calculate how much width is left for the last element after rounding the base width down
      var restWidth = Math.floor(currentNavWidth - numChildren * singleWidth);
      // adjust width for all elements exept the last element
      $childrenExceptLast.css("width", Math.floor(currentNavWidth / numChildren - 1));
      // last element gets rest space
      $lastChild.css("width", (singleWidth + restWidth));
      // Manual calculation of sub-menu-width to allow precise alignment below navigation bar
      $childrenExceptLast.children("ul").css("min-width", 2 * singleWidth - 1);
      // last element's sub-menu-width needs different calculation
      $lastChild.children("ul").css("min-width", 2 * singleWidth + restWidth - 2);

      // save currentNavWidth as lastNavWidth
      lastNavWidth = currentNavWidth;
    }
  };

  /**
   * unset width of each item in the mega-menu
   */
  module.unsetMegaMenuItemsWidth = function () {
    var $navMegaMenu = $(megaMenuSelector);
    var $children = $navMegaMenu.children("li");
    // reset mega-menu-items widths
    $children.css("width", "");
    // reset mega-menu-items sub-menu widths
    $children.children("ul").css("min-width", "");

    // reset lastNavWidth so setMegaMenuItemsWidth recalculates if used again
    lastNavWidth = 0;
  };

  /**
   * equalize height of each item in marketing spot
   */
  module.setMarketingSpotItemsHeight = function () {
    // first unset all heights, otherwise the height can only decrease
    module.unsetMarketingSpotItemsHeight();
    var $marketingSpotItems = $(marketingSpotItemSelector);

    // calculate biggest height by iterating over all marketing spot items
    var biggest = 0;
    $marketingSpotItems.each(function () {
      var current = $(this).height();
      if (current > biggest) {
        biggest = current;
      }
    });

    // calculate new height based on biggest height for each element
    $marketingSpotItems.each(function () {
      var diff = biggest - $(this).height();
      $(this).css({
        "height": biggest + "px",
        "padding-top": (diff / 2) + "px"
      });
    });
  };

  /**
   * unset height of each item in the marketing spot
   */
  module.unsetMarketingSpotItemsHeight = function () {
    var $marketingSpotItems = $(marketingSpotItemSelector);
    $marketingSpotItems.css({
        "height": "",
        "padding-top": ""
      });
  };

  /**
   * update a single given non-adaptive image
   * @param {Image} image dom node
   */
  module.updateNonAdaptiveImage = function (image) {
    var $image = $(image);
    var config = $.extend({overflow: false}, $image.data("cm-non-adaptive-content"));
    var $box = $image.parent();

    var baseImage = new Image();
    baseImage.src = image.src;

    var baseRatio = baseImage.width / baseImage.height;
    var boxRatio = $box.width() / $box.height();

    adjustNonAdaptiveContent($image, baseRatio, boxRatio, config.overflow);
  };

  /**
   * update a single given non-adaptive video
   * @param {HTMLVideoElement} video
   */
  module.updateNonAdaptiveVideo = function (video) {
    var $video = $(video);
    var config = $.extend({overflow: false}, $video.data("cm-non-adaptive-content"));
    var $box = $video.parent();

    var baseRatio = $video.width() / $video.height();
    if ($video.is("video")) {
      baseRatio = video.videoWidth / video.videoHeight;
    }
    var boxRatio = $box.width() / $box.height();

    adjustNonAdaptiveContent($video, baseRatio, boxRatio, config.overflow);
  };

  /**
   * updates non adaptive images and videos for the whole page
   */
  module.updateNonAdaptiveContents = function () {
    $(document.body).find("img[data-cm-non-adaptive-content]").each(function () {
      module.updateNonAdaptiveImage(this);
    });
    $(document.body).find("video[data-cm-non-adaptive-content]").each(function () {
      module.updateNonAdaptiveVideo(this);
    });
  };

  /**
   * updates all masonry grids on the site regarding their order in DOM. Deepest grid are
   * updated first, so parent grids can adjust to children's dimensions.
   */
  module.updateMasonry = function () {
    // find all grids and layout them in reversed order regarding the depth inside DOM
    var $grids = $(".cm-js-masonry").sort(function (a, b) {
      return $(a).parents().length < $(b).parents().length;
    });
    $grids.masonry("layout");
  };

  /**
   * updates a control element for a shopping cart
   * @param {String} control the control element
   */
  module.updateCartControl = function (control) {
    var $control = $(control);
    var config = $.extend({symbol: undefined, badge: undefined, cart: undefined}, $control.data("cm-cart-control"));

    // only apply cart control if configuration is sufficient
    if (config.symbol !== undefined && config.badge !== undefined && config.cart !== undefined) {
      // read config of the attached cart
      var cartConfig = $.extend({itemCount: 0}, $control.find(config.cart).data("cm-cart"));

      // find dom element representing the cart symbol
      var $symbol = $control.find(config.symbol);

      // update cart symbol based on item count
      if (cartConfig.itemCount > 0) {
        $symbol.removeClass("icon-cart-empty");
        $symbol.addClass("icon-cart-with-items");
      } else {
        $symbol.addClass("icon-cart-empty");
        $symbol.removeClass("icon-cart-with-items");
      }
      // add item count to cart badge
      $control.find(config.badge).html(cartConfig.itemCount);
    }
  };

  return module;
}(coremedia.blueprint.perfectchef || {});

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {

  var $ = coremedia.blueprint.$;
  var $window = $(window);
  var $document = $(document);

  // init device detection
  coremedia.blueprint.deviceDetector.init();

  // initialize masonry collections that are not initialized automatically
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({isInitLayout: true}, "masonry-options", function ($target, config) {
    if (!config.isInitLayout) {
      $target.masonry(config);
      $target.masonry("unbindResize");
      $target.masonry("layout");
    }
  });

  // move to top button
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var selector = ".cm-icon--button-top";

    $target.findAndSelf(selector).click(function () {
      $("html, body").animate({scrollTop: 0}, "slow");
      return false;
    });
  });

  // assign accordion-item functionality
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var selector = ".cm-accordion-item";
    $target.findAndSelf(selector).each(function () {
      var $item = $(this);
      var $accordion = $item.closest(".cm-collection--accordion");
      var $itemHeader = $item.find(".cm-accordion-item__header").first();

      $itemHeader.on("click", function () {
        coremedia.blueprint.basic.accordion.change($accordion, $item);
      });
    });
  });

  // init slideshows
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var identifier = "cm-slideshow";
    var selector = "[data-" + identifier + "]";
    $target.findAndSelf(selector).each(function () {
      var $slider = $(this);

      var defaultConfig = {
        itemSelector: undefined, // DEPRECATED
        container: undefined,
        item: undefined,
        timeout: 5000,
        prev: undefined,
        next: undefined
      };
      var config = $.extend(defaultConfig, $slider.data(identifier));

      // backward compatibility
      if (config.itemSelector !== undefined) {
        var classes = config.itemSelector.split(">", 2);
        if (classes.length == 2) {
          config.container = "> " + classes[0].trim();
          config.item = "> " + classes[1].trim();
        }
      }

      if (config.container !== undefined && config.item !== undefined) {
        // move quickinfos of imagemap outside slideshow container because they will hide the overflow causing quickinfo
        // to be cut if it too large. This also avoids the creation of additional dom elements
        var $slidesContainer = $slider.find(config.container);
        var $imageMapQuickInfos = $slidesContainer.find(config.item + " " + ".cm-imagemap__quickinfo");
        $imageMapQuickInfos.insertAfter($slidesContainer);

        var cycleConfig = {
          log: false,
          slides: config.item,
          timeout: config.timeout,
          // 600 for mobile, 1000 for desktop
          speed: (coremedia.blueprint.deviceDetector.getLastDevice().type == coremedia.blueprint.perfectchef.DEVICE_DESKTOP) ? 1000 : 600,
          pauseOnHover: true,
          fx: "scrollHorz",
          swipe: true
        };
        if (config.prev !== undefined) {
          cycleConfig.prev = config.prev;
        }
        if (config.next !== undefined) {
          cycleConfig.next = config.next;
        }

        // detect if current slide has an active quickinfo
        var hasActiveSlideActiveQuickInfo = function () {
          var result = false;
          var $areas = $slider.find(".cycle-slide-active .cm-imagemap__areas");
          $areas.find(".cm-imagemap__hotzone").each(function () {
            var $button = $(this);
            var config = $.extend({target: undefined}, $button.data("cm-button--quickinfo"));
            result = result || $("#" + config.target).is(".cm-quickinfo--active:not(.cm-quickinfo--main)");
          });
          return result;
        };
        // hide all quickinfos
        var hideAllQuickinfos = function () {
          $slider.children(".cm-quickinfo").css({"visibility": "hidden", "position": "absolute"});
        };
        // show quickinfos on active slide
        var showQuickinfosInActiveSlide = function () {
          // show quickinfos for active slide only
          var $areas = $slider.find(".cycle-slide-active .cm-imagemap__areas");
          $areas.find(".cm-imagemap__hotzone").each(function () {
            var $button = $(this);
            var config = $.extend({target: undefined}, $button.data("cm-button--quickinfo"));
            var $quickinfo = $("#" + config.target);
            $quickinfo.css({"position": "", "visibility": ""});
          });
          var areasConfig = $.extend({quickInfoMainId: undefined}, $areas.data("cm-areas"));
          if (areasConfig.quickInfoMainId !== undefined) {
            $("#" + areasConfig.quickInfoMainId).css({"position": "", "visibility": ""});
          }
        };
        // pause auto cycling
        var pause = function () {
          $slidesContainer.cycle("pause");
        };
        // resume auto cycling
        var resume = function () {
          $slidesContainer.cycle("resume");
        };

        /*
         * Behaviour of slideshow dependend on quickinfo state
         * 1) current slide stops auto cycling if quickinfo is opened (ignoring main quickinfo)
         * 2) current slide resumes auto cycling if quickinfo is closed (ignoring main quickinfo)
         * 3) if manual interactions are performed (prev/next) auto cycling is resumed
         * 4) if slide with open quickinfo becomes active slide auto cycling is stopped
         */

        // implements 1) + 2)
        $slider.find(".cm-quickinfo:not(.cm-quickinfo--main)").on(coremedia.blueprint.quickInfo.EVENT_QUICKINFO_CHANGED, function () {
          if (hasActiveSlideActiveQuickInfo()) {
            pause();
          } else {
            resume();
          }
        });

        // implements 3)
        $slidesContainer.on("cycle-prev", resume);
        $slidesContainer.on("cycle-next", resume);

        // implements 4)
        $slidesContainer.on("cycle-after", function () {
          // if slideshow contains an active quickinfo stop auto cycling
          if (hasActiveSlideActiveQuickInfo()) {
            pause();
          }
        });
        $slidesContainer.on("cycle-initialized", function () {
          hideAllQuickinfos();
          showQuickinfosInActiveSlide();
          $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
        });
        $slidesContainer.on("cycle-before", function () {
          hideAllQuickinfos();
          $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
        });
        $slidesContainer.on("cycle-after", function () {
          showQuickinfosInActiveSlide();
          // on tablet slideshow varies in height if imagemap is attached
          $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
        });
        // always show arrows on touch enabled devices
        if ("ontouchstart" in document.documentElement) {
          $slider.addClass("cm-collection--slideshow-touch");
        }

        $slidesContainer.cycle(cycleConfig);
      }
    });
  });

  // lightbox-gallery
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-lightbox--gallery", function ($target) {
    $target.magnificPopup({
      gallery: {enabled: true},
      delegate: ":not(.cycle-sentinel) a",
      type: "image"
    });
  });

  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-product-assets", function ($target) {
    var $slideshow = $target.find(".cm-product-assets__slideshow");
    var $carousel = $target.find(".cm-product-assets__carousel");
    // TODO improve performance
    var $slideshows = $target.find(".cm-product-assets__slideshow, .cm-product-assets__carousel");

    $slideshow.cycle({
      slides: "> .cm-lightbox",
      timeout: 0,
      prev: "> .cm-direction-arrow--left",
      next: "> .cm-direction-arrow--right",
      fx: "fade",
      swipe: true,
      log: false
    });

    // sometimes cycle carousel plugin does not remove opacity of elements
    $carousel.on("cycle-initialized", function () {
      $(this).find(".cycle-slide").css("opacity", 1);
    });
    $carousel.cycle({
      slides: "> .cycle-slide",
      timeout: 0,
      fx: "carousel",
      carouselVisible: 4,
      carouselFluid: true,
      allowWrap: false
    });

    $slideshows.each(function () {
      $(this).cycle()
              .on("cycle-initialized", function () {
                $(document).trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
              }).on('cycle-next cycle-prev', function (e, opts) {
                // advance the other slideshow
                $slideshows.not(this).cycle('goto', opts.currSlide);
              });
    });
    $carousel.find('.cycle-slide').click(function () {
      var index = $carousel.data('cycle.API').getSlideIndex(this);
      $slideshows.cycle('goto', index);
    });
  });

  // init popups
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var identifier = "cm-popup-control";
    var classButtonActive = "cm-popup-button--active";
    var baseConfig = {
      button: undefined,
      popup: undefined
    };

    var selector = "[data-" + identifier + "]";
    $target.findAndSelf(selector).each(function () {
      var $this = $(this);
      var config = $.extend(baseConfig, $this.data(identifier));

      if (config.button !== undefined && config.popup !== undefined) {
        var $button = $this.find(config.button);
        var $popup = $this.find(config.popup);

        // bind button state to popup state
        $popup.on(coremedia.blueprint.basic.popup.EVENT_POPUP_CHANGED, function (event, opened) {
          if (opened) {
            $button.addClass(classButtonActive);
          } else {
            $button.removeClass(classButtonActive);
          }
        });
        $button.on("click", function () {
          // check if popup control is not disabled
          if (!($.extend({disabled: false}, $this.data(identifier)).disabled)) {
            // Toggle popup state
            coremedia.blueprint.basic.popup.toggle($popup);
            return false;
          }
        });
      }
    });
  });

  // close all popups if clicked outside popup or ESC is pressed
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var identifierPopup = ".cm-popup";
    var $body = $target.findAndSelf("body");

    //outside
    $body.on("click", function (event) {
      if ($(event.target).closest(identifierPopup).length === 0) {
        coremedia.blueprint.basic.popup.close($body.find(identifierPopup));
      }
    });
    // ESC
    $body.on("keydown", function (event) {
      if (event.keyCode === 27) {
        coremedia.blueprint.basic.popup.close($body.find(identifierPopup));
      }
    });
  });

  // initialize cart control (popup + cart indicator icon)
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var identifier = "cm-cart-control";
    var baseConfig = {cart: undefined};

    var selector = "[data-" + identifier + "]";
    $target.findAndSelf(selector).each(function () {
      var $control = $(this);
      var config = $.extend(baseConfig, $control.data(identifier));

      if (config.cart !== undefined) {
        $control.find(config.cart).on("cartUpdated", function () {
          coremedia.blueprint.perfectchef.updateCartControl($control);
        });
      }
      coremedia.blueprint.perfectchef.updateCartControl($control);
    });
  });

  // initialize remove from cart buttons
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-cart", function ($target) {
    var identifier = "cm-cart-remove-item";
    var selector = "[data-" + identifier + "]";
    $target.findAndSelf(selector).each(function () {
      var $button = $(this);
      var buttonConfig = $.extend({
        id: undefined,
        link: undefined,
        cart: undefined,
        item: undefined,
        quantity: 0
      }, $button.data(identifier));
      var $cart = $button.closest(buttonConfig.cart);
      var cartConfig = $.extend({token: undefined}, $cart.data("cm-cart"));

      if (buttonConfig.id !== undefined && buttonConfig.link !== undefined && cartConfig.token !== undefined) {

        //button clicked
        $button.on("click", function (e) {
          // don't let the add-to-cart button trigger the teaser link
          e.preventDefault();

          if (!$button.hasClass(identifier + "--disabled")) {
            var url = buttonConfig.link;
            coremedia.blueprint.basic.ajax({
              type: "POST",
              url: url,
              data: {
                orderItemId: buttonConfig.id,
                action: "removeOrderItem",
                _CSRFToken: cartConfig.token
              },
              dataType: 'text'
            }).done(function () {
              $(".cm-icon--cart").each(function () {
                coremedia.blueprint.basic.refreshFragment($(this));
              });
            });
          }
        });
      }
    });
  });

  // add to cart functionality
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var identifier = "cm-cart-add-item";
    var selector = "[data-" + identifier + "]";
    $target.findAndSelf(selector).each(function () {
      var $button = $(this);
      var buttonConfig = $.extend({id: undefined, link: undefined, cart: undefined}, $button.data(identifier));

      if (buttonConfig.id !== undefined && buttonConfig.link !== undefined) {

        //button clicked
        $button.on("click", function (e) {
          // don't let the add-to-cart button trigger the teaser link
          e.preventDefault();

          var $cart = $(buttonConfig.cart);
          var cartConfig = $.extend({token: undefined}, $cart.data("cm-cart"));
          var url = buttonConfig.link;
          var $icon = $button.find("i");

          if (!$button.hasClass("cm-button--loading")) {

            //disable button and show spinner
            $button.addClass("cm-button--loading");
            $icon.removeClass("icon-checkmark").removeClass("icon-warning");

            // send add-to-cart call
            coremedia.blueprint.basic.ajax({
              type: "POST",
              url: url,
              data: {
                externalTechId: buttonConfig.id,
                action: "addOrderItem",
                _CSRFToken: cartConfig.token
              },
              dataType: 'text'
            }).done(function () {
              //show success icon
              $icon.addClass("icon-checkmark");
              window.setTimeout(function () {
                $icon.fadeOut(400, function () {
                  $icon.removeClass("icon-checkmark").removeAttr("style");
                });
              }, 1500);
              //refresh cart
              $(".cm-icon--cart").each(function () {
                coremedia.blueprint.basic.refreshFragment($(this));
              });
            }).fail(function () {
              $icon.addClass("icon-warning");
            }).complete(function () {
              $button.removeClass("cm-button--loading");
            });
          }
        });
      }
    });
  });

  // initialize search form
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var baseConfig = {urlSuggestions: undefined, minLength: undefined};
    $target.findAndSelf(".cm-search-form").each(function () {
      var $search = $(this);
      var config = $.extend(baseConfig, $search.data("cm-search"));
      var $popupSuggestions = $(this).find(".cm-popup--search-suggestions");
      var $listSuggestions = $(this).find(".cm-search-suggestions");
      var $suggestion = $listSuggestions.find(".cm-search-suggestions__item").clone();
      var $prototypeSuggestion = $suggestion.clone();
      var lastQuery = undefined;

      // remove the sample suggestion from dom
      $suggestion.remove();
      $search.find(".search_input").bind("input", function () {
        var $input = $(this);
        var value = $input.val();
        coremedia.blueprint.basic.popup.close($popupSuggestions);
        // only show suggestions if the search text has the minimum length
        if (value.length >= config.minLength) {
          // clear suggestions
          $listSuggestions.html("");
          // save last query
          lastQuery = value;
          coremedia.blueprint.basic.ajax({
            url: config.urlSuggestions,
            dataType: "json",
            data: {
              type: "json",
              query: value
            }
          }).done(function (data) {
            // in case ajax calls earlier ajax calls receive their result later, only show most recent results
            if (lastQuery == value) {
              var classNonEmpty = "cm-search-suggestions--non-empty";
              $listSuggestions.removeClass(classNonEmpty);
              // transform search suggestions into dom elements
              $.map(data, function (item) {
                $listSuggestions.addClass(classNonEmpty);
                var $suggestion = $prototypeSuggestion.clone();
                $listSuggestions.append($suggestion);
                $suggestion.html("<b>" + value + "</b>" + item.label.substr(value.length));
                // attribute must exist, otherwise selector will not match
                $suggestion.attr("data-cm-search-suggestion", "");
                // set attribute for jquery (not visible in dom)
                $suggestion.data("cm-search-suggestion", {
                  form: ".cm-search-form",
                  target: ".search_input",
                  value: item.value,
                  popup: ".cm-popup--search-suggestions"
                });
                coremedia.blueprint.nodeDecorationService.decorateNode($suggestion);
              });
              // show search suggestions
              coremedia.blueprint.basic.popup.open($popupSuggestions);
              // set focus back to input element
              $input.focus();
              $document.trigger(coremedia.blueprint.basic.EVENT_NODE_APPENDED, [$suggestion]);
            }
          });
        }
      });
    });
  });

  // update tabs in wcs (e.g. pdp)
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".tab_container", function ($target) {
    $target.on("click", function () {
      $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
    });
  });

  // initially updates new non-adaptive boxes
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var identifier = "cm-non-adaptive-content";
    var selector = "[data-" + identifier + "]";
    var imageSelector = "img" + selector;
    $target.findAndSelf(imageSelector).each(function () {
      // consider using images loaded plugin
      $(this).on("load", function () {
        coremedia.blueprint.perfectchef.updateNonAdaptiveImage(this);
      });
      // load is not always triggered (e.g. if image is cached or event is attached too late), so at least execute once
      coremedia.blueprint.perfectchef.updateNonAdaptiveImage(this);
    });
    var videoSelector = "iframe" + selector + ", video" + selector;
    $target.findAndSelf(videoSelector).each(function () {
      coremedia.blueprint.perfectchef.updateNonAdaptiveVideo(this);
    });
  });

  // equalize line heights for all cm-text elements, so even multi column layouts appear print-like
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var baseLine = $(".cm-text p:first").css("line-height");
    if (baseLine !== undefined) {
      baseLine = baseLine.replace("px", "");
      var selector = ".cm-text img";
      $target.findAndSelf(selector).each(function () {
        var $image = $(this);
        var addPercent = 0;
        if ($image.css("float") == "none") {
          var width = $image.width();
          var height = $image.height();

          var overhead = height - baseLine * Math.floor(height / baseLine);
          if (overhead > 0) {
            var add = baseLine - overhead;
            addPercent = add / width * 100;
          }
        }
        $image.css("margin-bottom", addPercent + "%");
      });
    }
  });

  // initializes search suggestions
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    // read configuration
    var baseConfig = {form: undefined, target: undefined, value: undefined, popup: undefined};
    var identifier = "cm-search-suggestion";
    var selector = "[data-" + identifier + "]";

    $target.findAndSelf(selector).each(function () {
      var $suggestion = $(this);
      var config = $.extend(baseConfig, $suggestion.data(identifier));
      var $popup = $(config.popup);
      // when clicking search suggestions form should be filled with the suggestion and be submitted
      $suggestion.bind("click", function () {
        coremedia.blueprint.basic.popup.close($popup);
        $(config.target).val(config.value);
        $(config.form).submit();
      });
    });
  });

  // init html5 videos
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var baseConfig = {flash: undefined};
    var identifier = "cm-video--html5";
    var selector = "[data-" + identifier + "]";
    $target.findAndSelf(selector).each(function () {
      var $video = $(this);
      var config = $.extend(baseConfig, $video.data(identifier));
      //noinspection JSUnusedGlobalSymbols
      var me = new MediaElement(
              this,
              {
                plugins: ["flash"],
                pluginPath: "", // needs to be empty
                flashName: config.flash,
                success: function (mediaElement) {
                  // delegate to own event, so the implementation does not rely on MediaElement Plugin
                  // additionally youtube/vimeo/.. players could be able to trigger videoEnded event
                  mediaElement.addEventListener("ended", function () {
                    $video.trigger("videoEnded");
                  }, false);
                  mediaElement.addEventListener("canplay", function () {
                    $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
                  }, false);
                }
              });
      $video.on("webkitendfullscreen", function () {
        $video.trigger("videoEndFullScreen");
      });
      $video.on("videoStart", function () {
        coremedia.blueprint.perfectchef.updateNonAdaptiveVideo($video[0]);
        me.play();
      });
    });
  });

  // init youtube videos
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {

    var identifier = "cm-video--youtube";
    var selector = "." + identifier;

    // defines if the youtube api is attached
    var youtubeApiAttached = false;
    // defines if the youtube api is loaded
    var youtubeApiLoaded = false;
    // defines an array of functions to be triggered as soon as the youtube api is ready
    var playerApiQueue = [];

    $target.findAndSelf(selector).each(function () {

      var video = this;
      var $video = $(video);

      if (!youtubeApiAttached) {
        youtubeApiAttached = true;

        // attach youtube api
        var tag = document.createElement("script");

        tag.src = "https://www.youtube.com/iframe_api";
        var firstScriptTag = document.getElementsByTagName("script")[0];
        if (firstScriptTag) {
          firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
        }
      }

      window.onYouTubeIframeAPIReady = function () {
        youtubeApiLoaded = true;

        // trigger all functions in playerApiQueue
        while (playerApiQueue.length > 0) {
          var f = playerApiQueue.pop();
          f();
        }
      };

      var initPlayer = function () {
        var playOnReady;
        var player = new YT.Player(video, {
          events: {
            "onReady": function () {
              if (playOnReady) {
                player.playVideo();
                playOnReady = false;
              }
              $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
            },
            "onStateChange": function (event) {
              if (event.data == YT.PlayerState.ENDED) {
                $video.trigger("videoEnded");
              }
            }
          }
        });

        $video.on("videoStart", function () {
          if (player.playVideo && typeof player.playVideo === "function") {
            player.playVideo();
          } else {
            playOnReady = true;
          }
        });
      };

      if (youtubeApiLoaded) {
        initPlayer();
      } else {
        playerApiQueue.push(initPlayer);
      }
    });
  });

  // init vimeo videos
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {

    // Helper function for sending a message to a player
    var postHelper = function (node, url, action, value) {
      var data = { method: action };
      if (value) {
        data.value = value;
      }

      node.contentWindow.postMessage(JSON.stringify(data), url);
    };

    var baseConfig = {playerId: undefined};
    var identifier = "cm-video--vimeo";
    // vimeo video is and must be iframe
    var selector = "iframe." + identifier;

    $target.findAndSelf(selector).each(function () {
      var $video = $(this);
      var protocol = "http";
      if (window.location.href.match(/^https:(.+)/)) {
        protocol = "https";
      }
      var url = protocol + ":" + $video.attr("src").split("?")[0];

      $window.on("message", function (e) {
        // config always needs to be fresh
        var config = $.extend(baseConfig, $video.data(identifier));
        var data = JSON.parse(e.originalEvent.data);

        if (data["player_id"] == config.playerId) {
          switch (data.event) {
            case 'ready':
              $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
              // activate finish event
              postHelper($video[0], url, "addEventListener", "finish");
              break;
            case 'finish':
              $video.trigger("videoEnded");
              break;
          }
        }
      });

      $video.on("videoStart", function () {
        postHelper($video[0], url, "play");
      });
    });
  });

  // handle video teasers
  coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {
    var baseConfig = {
      preview: undefined,
      play: undefined,
      player: undefined,
      backlightTimeout: 200,
      features: ['backlight']
    };
    var identifier = "cm-teaser--video";
    var selector = "[data-" + identifier + "]";

    $target.findAndSelf(selector).each(function () {
      var $videoTeaser = $(this);
      var config = $.extend(baseConfig, $videoTeaser.data(identifier));
      var $preview = $videoTeaser.find(config.preview);
      var $play = $videoTeaser.find(config.play);
      var $player = $videoTeaser.find(config.player);
      $play.bind("click", function () {
        $preview.addClass("cm-hidden");
        $player.removeClass("cm-hidden");

        var selector = ".cm-video";
        $player.findAndSelf(selector).each(function () {
          var $video = $(this);
          var replacePlayerWithStillImage = function () {
            $player.addClass("cm-hidden");
            $preview.removeClass("cm-hidden");
            // window might have changed while video player was active, e.g. portrait->landscape
            $(document).trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
          };
          $video.on("videoEnded", replacePlayerWithStillImage);
          // when playback is canceled on mobiles, videoEnded is not triggered...
          $video.on("videoEndFullScreen", replacePlayerWithStillImage);
          $video.trigger("videoStart");
        });

        return false;
      });
    });
  });

  // adjust layout if richtext images (which currently are not adaptive) are loaded
  var updateTimer = 0;
  coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-text img", function ($target) {
    $target.on("load", function () {
      if (updateTimer !== 0) {
        clearTimeout(updateTimer);
      }
      updateTimer = setTimeout(function () {
        $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
      }, 100);
    });
  });

  // --- EVENTS --------------------------------------------------------------------------------------------------------

  // show/hide "move to top" button
  $window.scroll(function () {
    // display icon after scrolling 1/3 of the document height
    var $buttonTop = $(".cm-icon--button-top");
    if ($window.scrollTop() > $document.height() / 3) {
      $buttonTop.removeClass("cm-hidden");
    } else {
      $buttonTop.addClass("cm-hidden");
    }
  });

  // masonry has to be informed if nodes have appended
  $document.on(coremedia.blueprint.basic.EVENT_NODE_APPENDED, function (event, $node) {
    // check if inside masonry collection
    $node.closest(".cm-js-masonry").each(function () {
      $(this).masonry("reloadItems");
      $document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);
    });
  });

  // Synchronizes the layout process. Only one layout event at a time is allowed
  var isLayoutInProgress = false;
  // trigger all functions that should recalculate if the layout has changed
  var layout = function () {
    coremedia.blueprint.perfectchef.updateNonAdaptiveContents();
    coremedia.blueprint.perfectchef.updateMasonry();
    // only on desktop
    if (coremedia.blueprint.deviceDetector.getLastDevice().type == coremedia.blueprint.perfectchef.DEVICE_DESKTOP) {
      coremedia.blueprint.perfectchef.setMegaMenuItemsWidth();
    }
    // on desktop and tablet
    if (coremedia.blueprint.deviceDetector.getLastDevice().type == coremedia.blueprint.perfectchef.DEVICE_DESKTOP
            || coremedia.blueprint.deviceDetector.getLastDevice().type == coremedia.blueprint.perfectchef.DEVICE_TABLET) {
      coremedia.blueprint.perfectchef.setMarketingSpotItemsHeight();
    }
    isLayoutInProgress = false;
  };

  $document.on(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED, function () {
    if (!isLayoutInProgress) {
      setTimeout(layout, 500);
    }
    isLayoutInProgress = true;
  });
});
