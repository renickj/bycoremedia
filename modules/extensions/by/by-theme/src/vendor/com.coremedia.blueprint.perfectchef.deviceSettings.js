/**

* CoreMedia Namespace

*/

var coremedia = (function (module) {

return module;

}(coremedia || {}));

coremedia.blueprint = (function (module) {

return module;

}(coremedia.blueprint || {}));

coremedia.blueprint.perfectchef = (function (module) {

return module;

}(coremedia.blueprint.perfectchef || {}));

/**

*

*/

coremedia.blueprint.perfectchef.deviceSettings = function (module) {

var $ = coremedia.blueprint.$;

var $document = $(document);

var applyDropdown = function ($target) {

$target.findAndSelf(".cm-placement-header .cm-navigation.cm-dropdown > li > .cm-dropdown-button--open").removeClass("icon-menu-next");

$target.findAndSelf(".cm-placement-header .cm-navigation.cm-dropdown > li > .cm-dropdown-button--close").removeClass("icon-menu-back");

$target.findAndSelf(".cm-placement-header .cm-navigation.cm-dropdown > li > .cm-dropdown-button").addClass("icon-menu");

};

var removeDropdown = function ($target) {

$target.findAndSelf(".cm-placement-header .cm-navigation.cm-dropdown > li > .cm-dropdown-button").removeClass("icon-menu");

$target.findAndSelf(".cm-placement-header .cm-navigation.cm-dropdown > li > .cm-dropdown-button--open").addClass("icon-menu-next");

$target.findAndSelf(".cm-placement-header .cm-navigation.cm-dropdown > li > .cm-dropdown-button--close").addClass("icon-menu-back");

};

var applyDisableImageMapDefaultTarget = function ($target) {

$target.findAndSelf(".cm-imagemap").each(function () {

var $imagemap = $(this);

var $imagemapLink = $imagemap.find(".cm-imagemap__link");

var $imagemapAreas = $imagemap.find(".cm-imagemap__areas");

if ($imagemapAreas.find(".cm-imagemap__area").size() > 0) {

$imagemapLink.data("href", $imagemapLink.attr("href"));

$imagemapLink.removeAttr("href");

}

});

};

var removeDisableImageMapDefaultTarget = function ($target) {

$target.findAndSelf(".cm-imagemap").each(function () {

var $imagemap = $(this);

var $imagemapLink = $imagemap.find(".cm-imagemap__link");

var $imagemapAreas = $imagemap.find(".cm-imagemap__areas");

if ($imagemapAreas.find(".cm-imagemap__area").size() > 0) {

$imagemapLink.attr("href", $imagemapLink.data("href"));

$imagemapLink.removeData("href");

}

});

};

var applyDisablePopupCart = function ($target) {

$target.findAndSelf(".cm-icon--cart[data-cm-popup-control]").each(function () {

var $popup = $(this);

var identifier = "cm-popup-control";

$popup.data(identifier, $.extend({}, $popup.data(identifier), {disabled: true}));

});

};

var removeDisablePopupCart = function ($target) {

$target.findAndSelf(".cm-icon--cart[data-cm-popup-control]").each(function () {

var $popup = $(this);

var identifier = "cm-popup-control";

$popup.data(identifier, $.extend({}, $popup.data(identifier), {disabled: false}));

});

};

var applyDisableImageMap = function ($target) {

$target.findAndSelf(".cm-imagemap").each(function () {

var $imagemap = $(this);

var $imagemapImage = $imagemap.find(".cm-imagemap__image");

$imagemapImage.attr("useMap", "");

});

};

var removeDisableImageMap = function ($target) {

$target.findAndSelf(".cm-imagemap").each(function () {

var $imagemap = $(this);

var $imagemapImage = $imagemap.find(".cm-imagemap__image");

var $imagemapAreas = $imagemap.find(".cm-imagemap__areas");

$imagemapImage.attr("useMap", "#" + $imagemapAreas.attr("name"));

});

};

var DYNAMIC_SLIDESHOW_IDENTIFIER = "cm-dynamic-slideshow";

var DYNAMIC_SLIDESHOW_ELEMENTS_SELECTOR = ".cm-teaser";

var applyDynamicSlideshow = function ($target) {

$target.findAndSelf("#cm-page #cm-placement-sidebar, #cm-page .cm-box--related").each(function () {

var $sidebar = $(this);

var $dynamicSlideshow = $("<div></div>").addClass("cm-collection").addClass("cm-collection--slideshow");

$sidebar.find(DYNAMIC_SLIDESHOW_ELEMENTS_SELECTOR).each(function () {

var $teaser = $(this);

// teaser may not be inside cycle-sentinel

if ($teaser.parents(".cycle-sentinel").length == 0) {

$teaser.clone().appendTo($dynamicSlideshow);

}

});

var $arrow = $("<div></div>");

$arrow.addClass("cm-direction-arrow");

var $arrowLeft = $arrow.clone();

$arrowLeft.addClass("cm-direction-arrow--left").addClass("cm-collection--slideshow__prev");

var $arrowRight = $arrow.clone();

$arrowRight.addClass("cm-direction-arrow--right").addClass("cm-collection--slideshow__next");

$dynamicSlideshow.append($arrowLeft).append($arrowRight);

$sidebar.hide();

// sidebar needs to be replaced while related content can be inserted after all

var $added;

if ($sidebar.is("#cm-placement-sidebar")) {

$added = $("<div></div>").attr("class", $sidebar.attr("class"));

$added.append($dynamicSlideshow);

} else {

$added = $dynamicSlideshow;

}

$added.insertAfter($sidebar);

$sidebar.data(DYNAMIC_SLIDESHOW_IDENTIFIER, $added);

$dynamicSlideshow.cycle({

log: false,

slides: "> " + DYNAMIC_SLIDESHOW_ELEMENTS_SELECTOR,

timeout: 1000,

// 600 for mobile, 1000 for desktop

speed: (coremedia.blueprint.deviceDetector.getLastDevice().type == coremedia.blueprint.perfectchef.DEVICE_DESKTOP) ? 1000 : 600,

pauseOnHover: true,

fx: "scrollHorz",

swipe: true,

prev: "> .cm-collection--slideshow__prev",

next: "> .cm-collection--slideshow__next"

});

$document.trigger(coremedia.blueprint.basic.EVENT_NODE_APPENDED, [$added]);

});

};

var removeDynamicSlideshow = function ($target) {

$target.findAndSelf("#cm-page #cm-placement-sidebar, #cm-page .cm-box--related").each(function () {

var $sidebar = $(this);

var $added = $sidebar.data(DYNAMIC_SLIDESHOW_IDENTIFIER);

var $dynamicSlideshow;

if ($sidebar.is("#cm-placement-sidebar")) {

$dynamicSlideshow = $added.find(".cm-collection");

} else {

$dynamicSlideshow = $added;

}

if ($dynamicSlideshow !== undefined) {

$dynamicSlideshow.cycle("destroy");

}

if ($added !== undefined) {

$added.remove();

}

$sidebar.show();

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

};

var devices = {

"mobile": {

applySettings: function ($target) {

applyDropdown($target);

applyDisableImageMap($target);

applyDisablePopupCart($target);

applyDynamicSlideshow($target);

},

removeSettings: function ($target) {

removeDynamicSlideshow($target);

removeDisablePopupCart($target);

removeDisableImageMap($target);

removeDropdown($target);

},

"portrait": {

applySettings: function ($target) {

},

removeSettings: function ($target) {

}

},

"landscape": {

applySettings: function ($target) {

},

removeSettings: function ($target) {

}

}

},

"tablet": {

applySettings: function ($target) {

// footer navigation does not act as dropdown menu

$target.findAndSelf(".cm-navigation--footer").removeClass("cm-dropdown");

applyDropdown($target);

applyDisableImageMapDefaultTarget($target);

applyDynamicSlideshow($target);

// change height of marketing spot items

coremedia.blueprint.perfectchef.setMarketingSpotItemsHeight();

},

removeSettings: function ($target) {

// remove height of marketing spot items

coremedia.blueprint.perfectchef.unsetMarketingSpotItemsHeight();

removeDynamicSlideshow($target);

removeDisableImageMapDefaultTarget($target);

removeDropdown($target);

$target.findAndSelf(".cm-navigation--footer").addClass("cm-dropdown");

},

"portrait": {

applySettings: function ($target) {

},

removeSettings: function ($target) {

}

},

"landscape": {

applySettings: function ($target) {

},

removeSettings: function ($target) {

}

}

},

"desktop": {

applySettings: function ($target) {

// show info text for certain icons

$target.findAndSelf(".cm-icon--user-details .cm-icon__info").removeClass("cm-visuallyhidden");

$target.findAndSelf(".cm-icon--login .cm-icon__info").removeClass("cm-visuallyhidden");

$target.findAndSelf(".cm-icon--button-top .cm-icon__info").removeClass("cm-visuallyhidden");

// all dropdown functionality is removed from navigations

$target.findAndSelf(".cm-navigation").removeClass("cm-dropdown");

// find header icon for main navigation

var $headerNavigationIcon = $target.findAndSelf(".cm-placement-header .cm-icon--navigation");

// header navigation is placed below the navigation icon

var $headerNavigation = $headerNavigationIcon.find(".cm-navigation");

// it no longer acts as icon...

$headerNavigationIcon.removeClass("cm-icon");

$headerNavigation.removeClass("cm-icon__symbol");

// ...and navigation will be transformed into mega-menu

$headerNavigation.addClass("mega-menu");

// change width of mega-menu items

coremedia.blueprint.perfectchef.setMegaMenuItemsWidth();

// change height of marketing spot items

coremedia.blueprint.perfectchef.setMarketingSpotItemsHeight();

// find header icon for search

var $headerSearchIcon = $target.findAndSelf(".cm-placement-header .cm-icon--search");

// search form is placed below the search icon

var $headerSearchForm = $headerSearchIcon.find(".cm-search-form");

// it no longer acts as icon...

$headerSearchIcon.removeClass("cm-icon");

// submit button in search form has a different icon for desktop

$headerSearchForm.find(".cm-search-form__button > i").removeClass("icon-arrow-right").addClass("icon-search");

// attach form to search icon without being wrapped into popup

$headerSearchForm.appendTo(".cm-search");

applyDisableImageMapDefaultTarget($target);

},

removeSettings: function ($target) {

removeDisableImageMapDefaultTarget($target);

// find header icon for search

var $headerSearchIcon = $target.findAndSelf(".cm-placement-header .cm-icon--search");

// search form is placed below the search icon

var $headerSearchForm = $headerSearchIcon.find(".cm-search-form");

$headerSearchForm.appendTo(".cm-popup--search");

$headerSearchForm.find(".cm-search-form__button > i").removeClass("icon-search").addClass("icon-arrow-right");

$headerSearchIcon.addClass("cm-icon");

// remove height of marketing spot items

coremedia.blueprint.perfectchef.unsetMarketingSpotItemsHeight();

// remove width of mega-menu items

coremedia.blueprint.perfectchef.unsetMegaMenuItemsWidth();

// find header icon for main navigation

var $headerNavigationIcon = $target.findAndSelf(".cm-placement-header .cm-icon--navigation");

// header navigation is placed below the navigation icon

var $headerNavigation = $headerNavigationIcon.find(".cm-navigation");

$headerNavigation.removeClass("mega-menu");

$headerNavigation.addClass("cm-icon__symbol");

$headerNavigationIcon.addClass("cm-icon");

$target.findAndSelf(".cm-navigation").addClass("cm-dropdown");

$target.findAndSelf(".cm-icon--button-top .cm-icon__info").addClass("cm-visuallyhidden");

$target.findAndSelf(".cm-icon--login .cm-icon__info").addClass("cm-visuallyhidden");

$target.findAndSelf(".cm-icon--user-details .cm-icon__info").addClass("cm-visuallyhidden");

},

"portrait": {

applySettings: function ($target) {

},

removeSettings: function ($target) {

}

},

"landscape": {

applySettings: function ($target) {

},

removeSettings: function ($target) {

}

}

}

};

/**

*

* @param device

* @param orientation

* @param $target

*/

module.removeSettings = function (device, orientation, $target) {

if (devices[device] !== undefined) {

if (devices[device][orientation] !== undefined) {

devices[device][orientation].removeSettings($target);

} else {

devices[device].removeSettings($target);

}

}

};

/**

*

* @param device

* @param orientation

* @param $target

*/

module.applySettings = function (device, orientation, $target) {

if (devices[device] !== undefined) {

if (devices[device][orientation] !== undefined) {

devices[device][orientation].applySettings($target);

} else {

devices[device].applySettings($target);

}

}

};

return module;

}(coremedia.blueprint.perfectchef.deviceSettings || {});

coremedia.blueprint.$(function () {

var $ = coremedia.blueprint.$;

var $document = $(document);

// update images and masonry on device change

$document.on(coremedia.blueprint.deviceDetector.EVENT_DEVICE_CHANGED, function (event, newDevice, oldDevice) {

coremedia.blueprint.perfectchef.deviceSettings.removeSettings(oldDevice.type, oldDevice.orientation, $document);

if (oldDevice.type !== newDevice.type) {

coremedia.blueprint.perfectchef.deviceSettings.removeSettings(oldDevice.type, undefined, $document);

coremedia.blueprint.perfectchef.deviceSettings.applySettings(newDevice.type, undefined, $document);

}

coremedia.blueprint.perfectchef.deviceSettings.applySettings(newDevice.type, newDevice.orientation, $document);

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

// device settings need to be reapplied if DOM changes

$document.on(coremedia.blueprint.basic.EVENT_NODE_APPENDED, function (event, $node) {

var device = coremedia.blueprint.deviceDetector.getLastDevice();

coremedia.blueprint.perfectchef.deviceSettings.applySettings(device.type, device.orientation, $node);

});

});
