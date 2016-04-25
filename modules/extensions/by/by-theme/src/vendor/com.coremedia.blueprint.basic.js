/**

* CoreMedia Namespace

*/

var coremedia = (function (module) {

return module;

}(coremedia || {}));

coremedia.blueprint = (function (module) {

return module;

}(coremedia.blueprint || {}));

/**

* CoreMedia Blueprint Javascript Framework

* including following functions

*

* - infiniteScroll

* - toggle

* - dropdown

* - accordion

*/

coremedia.blueprint.basic = function (module) {

var $ = coremedia.blueprint.$;

var $document = $(document);

// Events

var EVENT_PREFIX = "coremedia.blueprint.basic.";

module.EVENT_LAYOUT_CHANGED = EVENT_PREFIX + "layoutChanged";

module.EVENT_NODE_APPENDED = EVENT_PREFIX + "nodeAppended";

/**

* Redirects the user to given redirectUrl if the redirectUrl is not part of the current

* url to prevent infinite loops.

*

* @param redirectUrl

*/

module.redirectTo = function(redirectUrl) {

// prevent infinite loop of redirects

if (window.location.href.indexOf(redirectUrl) < 0) {

window.location.href = redirectUrl + "?next=" + encodeURI(window.location.href);

}

};

/**

* Replace "$nextUrl$" in all data-href and store as href attribute.

* Assumes that if the page contains a form with a nextUrl hidden input field, the form is already loaded.

*

* @param {jQuery} $target

*/

module.renderFragmentHrefs = function ($target) {

var nextUrl;

if (window.location.pathname.match(/^\/dynamic\//) || window.location.pathname.match(/^\/blueprint\/servlet\/dynamic\//)) {

// we are inside a web flow, try to find "nextUrl" hidden input field value, else leave nextUrl blank

nextUrl = $('input:hidden[name="nextUrl"]').val() || "";

} else {

// for all other pages, take the current page as the next page after login

nextUrl = window.location.href;

//remove current scheme in case the scheme is changed before the redirect

nextUrl = nextUrl.replace(/^(http|https):(.+)/, "$2");

}

var selector = "a[data-href]";

$target.findAndSelf(selector).each(function () {

var $this = $(this);

$this.attr("href", $this.data("href").replace(/\$nextUrl\$/g, encodeURIComponent(nextUrl)));

});

};

/**

* Extend jQuery Ajax Function

*

* @param {object} options

* @returns $.ajax()

*/

module.ajax = function (options) {

/* always set xhr headers for CORS */

var cmOptions = {

headers: {'X-Requested-With': 'XMLHttpRequest'},

xhrFields: { withCredentials: true },

global: false,

url: undefined

};

options = $.extend({}, cmOptions, options);

// IE9 does not support CORS w/ credentials, so make sure the host matches the current host

var isIE9 = /MSIE (9.\d+);/.test(navigator.userAgent);

if (isIE9 && options.url !== undefined) {

options.url = options.url.replace(/\/\/([^/]+)\/(.+)/, "//" + window.location.host + "/$2");

// set Origin header if not present and url is absolute

var isAbsolute = new RegExp("^([a-z]+://|//)");

if (options.headers["Origin"] === undefined && isAbsolute.test(options.url)) {

options.headers["Origin"] = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ":" + window.location.port: "");

}

}

return $.ajax(options);

};

module.refreshFragment = function ($target, callback, requestParams) {

var config = $.extend({"url": undefined}, $target.data("cm-refreshable-fragment"));

if (config.url !== undefined) {

//$target.html("");

requestParams = requestParams || {};

coremedia.blueprint.basic.ajax({

type: "GET",

url: config.url,

data: requestParams,

dataType: "text"

}).done(function (html) {

var $html = $(html);

$target.replaceWith($html);

coremedia.blueprint.nodeDecorationService.decorateNode($html);

$document.trigger(coremedia.blueprint.basic.EVENT_NODE_APPENDED, [$html]);

if (callback !== undefined) {

callback($html);

}

});

}

};

return module;

}(coremedia.blueprint.basic || {});

/**

* Generic infinite scroll functionality

*/

coremedia.blueprint.basic.infiniteScroll = function (module) {

var $ = coremedia.blueprint.$;

/**

* Setup a new scrollbox.

*

* @scrollbox {object} scrollbox to be used (must contain child-element with class "scrollwrapper")

* @hasNext {function()} callback function called to determine if there are more items to be loaded

* @addData {function(function())} callback function called if there are items to add. has a callback as param indicating that data is added

* @additionalSpace {number} defines the additionalSpace to be added to the scrollbox to indicate that there is "more" in pixels

*/

module.init = function (scrollbox, hasNext, addData, additionalSpace) {

scrollbox.overflow = "overlay";

/**

* Refresh the infinite scroll

* If hasNext() function returns true infinite scroll functionality is added else removed

*

* @param {object} scrollbox

*/

function refresh(scrollbox) {

var $scrollbox = $(scrollbox);

// set dimensions of scrollwrapper(s) inside scrollbox

$scrollbox.find(".scrollwrapper").each(function () {

var $this = $(this);

// save old scrolling position

var backup = this.scrollTop;

// by default height is set to "auto" indicating that there is no more content

$this.height("auto");

// if callback returns that there is more content

if (hasNext()) {

// extend height by additional space configured

$this.height($this.height() + additionalSpace);

}

// restore old scrolling position

this.scrollTop = backup;

});

}

// by default loading of data by scrolling is not locked

var loadLock = false;

// bind trigger to scroll event of scrollbox

$(scrollbox).on("scroll", function () {

// only perform checks if loading of data is not locked

if (!loadLock) {

// detect if scrollBox is scrolled down to the bottom of the wrapper (only react in that case)

if (hasNext() && (this.scrollHeight - this.scrollTop) === $(this).height()) {

// lock loading of data

loadLock = true;

// trigger given callback function

addData(function () {

// refresh scrollbox

refresh(scrollbox);

// release the lock

loadLock = false;

});

}

}

});

// initiate scrollbox by refreshing

refresh(scrollbox);

};

return module;

}(coremedia.blueprint.basic.infiniteScroll || {});

/**

* Toggle (open/close) an element with class "toggle-container" by clicking on an element

* with class "toggle-button" inside a container. The state will be stored in sessionStorage,

* if available in browser and unique data-id is set.

*

* By default, this function is bind to all dom elements with class "toggle-item" and is used in fragmented preview.

*

* Example:

*

* <div id="example" data-id="example">

* <a href="#" class="toggle-button">Headline</a>

* <div class="toggle-container">Content</div>

* </div>

*

* <script>

* coremedia.blueprint.basic.toggle.init("#example");

* </script>

*/

coremedia.blueprint.basic.toggle = function (module) {

var $ = coremedia.blueprint.$;

/* Defines that the toggle is on */

module.STATE_ON = "on";

/* Defines that the toggle is off */

module.STATE_OFF = "off";

/**

* Returns the state of a toggleItem base on the visibility of the

* toggleContainer element.

*

* @param {object} toggleItem

* @returns {string} "on" or "off"

*/

module.getState = function (toggleItem) {

// if toggle-container is visible state is on otherwise off

return $(toggleItem).find(".toggle-container:first").hasClass("toggle-container-off")

? module.STATE_OFF

: module.STATE_ON;

};

/**

* Sets the toggle on

*

* @param {object} toggleItem

*/

module.on = function (toggleItem) {

var $toggleItem = $(toggleItem);

$toggleItem.find(".toggle-button:first").removeClass("toggle-off");

$toggleItem.find(".toggle-container:first").removeClass("toggle-container-off");

$toggleItem.trigger("toggleStateChanged", [module.STATE_ON]);

};

/**

* Sets the toggle off

*

* @param {object} toggleItem

*/

module.off = function (toggleItem) {

var $toggleItem = $(toggleItem);

$toggleItem.find(".toggle-button:first").addClass("toggle-off");

$toggleItem.find(".toggle-container:first").addClass("toggle-container-off");

$toggleItem.trigger("toggleStateChanged", [module.STATE_OFF]);

};

/**

* If the toggle is on set the toggle off otherwise on.

*

* @param {object} toggleItem

*/

module.toggle = function (toggleItem) {

if (module.getState(toggleItem) === module.STATE_ON) {

module.off(toggleItem);

} else {

module.on(toggleItem);

}

};

/**

* Initializes the toggleItem, binds handlers and sets its state base on the session.

*

* @param {object} toggleItem

*/

module.init = function (toggleItem) {

// check if browser supported sessionStorage

var storageEnabled = typeof(Storage) !== "undefined";

var $toggleItem = $(toggleItem);

// only safe state if toggleItem has an id and storage is supported

var useStorage = storageEnabled && $toggleItem.data("id") !== undefined;

if (useStorage) {

var state = sessionStorage.getItem($toggleItem.data("id"));

if (state === module.STATE_ON) {

module.on(toggleItem);

}

if (state === module.STATE_OFF) {

module.off(toggleItem);

}

}

// bind click-listener

$toggleItem.find(".toggle-button").bind("click", function () {

module.toggle(toggleItem);

return false;

});

// bind toggleState-listener

$toggleItem.bind("toggleStateChanged", function (event, newState) {

if (useStorage) {

sessionStorage.setItem($toggleItem.data("id"), newState);

}

$(document).trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

};

return module;

}(coremedia.blueprint.basic.toggle || {});

/**

*

*/

coremedia.blueprint.basic.dropdown = function (module) {

var $ = coremedia.blueprint.$;

var classMain = "cm-dropdown";

var classMenu = "cm-dropdown-menu";

var classMenuOpened = "cm-dropdown-menu--active";

var classMenuSubOpened = "cm-dropdown-menu--opened";

var classItem = "cm-dropdown-item";

var classItemLeaf = "cm-dropdown-item--leaf"; // new: defines, that the menu item is a leaf (has no submenus)

var classButton = "cm-dropdown-button";

var classButtonOpen = "cm-dropdown-button--open";

var classButtonClose = "cm-dropdown-button--close";

var classMenuLevel = "cm-dropdown-menu--level";

var classMenuMinLevel = "cm-dropdown-menu--min-level";

var classItemLevel = "cm-dropdown-item--level";

var classItemMinLevel = "cm-dropdown-item--min-level";

var classButtonLevel = "cm-dropdown-button--level";

var classButtonMinLevel = "cm-dropdown-button--min-level";

var EVENT_PREFIX = "coremedia.blueprint.basic.dropdown.";

module.EVENT_DROPDOWN_CHANGED = EVENT_PREFIX + "dropdownChanged";

/**

* Sets the state of an menu or menu item

*

* @param {object} item menu or menu item

* @param {string} state "opened", "sub-opened" or ""

*/

module.setState = function (item, state) {

var $item = $(item);

if (state == "opened" || state === "sub-opened") {

$item.addClass(classMenuSubOpened);

}

if (state == "sub-opened") {

$item.removeClass(classMenuOpened);

}

if (state === "opened") {

$item.addClass(classMenuOpened);

}

if (state === "") {

$item.removeClass(classMenuOpened);

$item.removeClass(classMenuSubOpened);

}

};

/**

* Opens the delivered menu.

*

* @param menu The menu to be opened

*/

module.open = function (menu) {

var $menu = $(menu);

var $root = $(menu).closest("." + classMain);

var additionalClassButtonOpen = $root.data("dropdown-class-button-open");

if (additionalClassButtonOpen === undefined) {

additionalClassButtonOpen = "";

}

var additionalClassButtonClose = $root.data("dropdown-class-button-close");

if (additionalClassButtonClose === undefined) {

additionalClassButtonClose = "";

}

// Full reset

// remove open or sub-open from all menus

$root.find("." + classMenu).each(function () {

module.setState(this, "");

});

var $items = $root.find("." + classItem);

// remove open or sub-open from all menu items

$items.each(function () {

module.setState(this, "");

});

// add is-leaf to all items

$items.addClass(classItemLeaf);

// set open for all openclose buttons having submenu (there can be more than one dropdown-menu-openclose per menu)

$items.has("." + classMenu).find("." + classButton + ":first").each(function () {

var $item = $(this).parent(":first");

// indicate that item is no leaf

$item.removeClass(classItemLeaf);

$item.children("." + classButton).each(function () {

var $this = $(this);

$this.removeClass(additionalClassButtonClose);

$this.removeClass(classButtonClose);

$this.addClass(classButtonOpen);

$this.addClass(additionalClassButtonOpen);

});

});

module.setState(menu, "opened");

// set sub-opened to all parent menus

$menu.parents("." + classMenu).each(function () {

module.setState(this, "sub-opened");

});

// set sub-opened to all parent menu items

// set close to openclose buttons of menu item

$menu.parents("." + classItem).each(function () {

module.setState(this, "sub-opened");

$(this).find("." + classButton + ":first").each(function () {

$(this).parent(":first").children("." + classButton).each(function () {

var $this = $(this);

$this.removeClass(additionalClassButtonOpen);

$this.removeClass(classButtonOpen);

$this.addClass(classButtonClose);

$this.addClass(additionalClassButtonClose);

});

});

});

// set opened to parent menu item if menu is not the root menu

if (!$menu.hasClass(classMain)) {

module.setState($menu.parent(":first"), "opened");

}

$root.trigger(module.EVENT_DROPDOWN_CHANGED, [menu]);

};

/**

* Closes the delivered menu.

*

* @param {object} menu The menu to be closed

*/

module.close = function (menu) {

var parent = menu.parents("." + classMenu + ":first");

// closing a menu is the same as opening the parent menu

module.open(parent);

};

/**

* Initializes a dropdown menu

*

* @param {object} menu The menu to be initialized

*/

module.init = function (menu) {

var $menu = $(menu);

// the root menu itsself is a dropdown-menu

$menu.addClass(classMenu);

// add classes for menu and items if selectors are specified

var selectorMenus = $menu.data("dropdown-menus");

if (typeof selectorMenus !== "undefined") {

$menu.find(selectorMenus).addClass(classMenu);

}

var selectorItems = $menu.data("dropdown-items");

if (typeof selectorItems !== "undefined") {

$menu.find(selectorItems).addClass(classItem);

}

// every menu items get an openclose button (initialized with no action to be performed)

$menu.find("." + classItem).prepend("<button class=\"" + classButton + "\"></button>");

// recursively add levels

var addLevel = function (menu, level) {

var $menu = $(menu);

$menu.addClass(classMenuLevel + level);

for (var i = 1; i <= level; i++) {

$menu.addClass(classMenuMinLevel + i);

}

var $items = $menu.children("." + classItem);

$items.each(function () {

var $item = $(this);

$item.addClass(classItemLevel + level);

for (var i = 1; i <= level; i++) {

$item.addClass(classItemMinLevel + i);

}

// min 0, max 1

$item.children("." + classButton).each(function () {

var $button = $(this);

$button.addClass(classButtonLevel + level);

for (var i = 1; i <= level; i++) {

$button.addClass(classButtonMinLevel + i);

}

});

$item.children("." + classMenu).each(function () {

addLevel(this, level + 1);

});

});

};

addLevel(menu, 1);

// open the menu to be initialized

module.open(menu);

// bind click-listener to openclose button

$menu.find("." + classButton).bind("click", function () {

var $this = $(this);

var $parent = $(this).closest("." + classItem).find("." + classMenu + ":first");

if ($this.hasClass(classButtonOpen)) {

module.open($parent);

} else if ($this.hasClass(classButtonClose)) {

module.close($parent);

}

return true;

});

// bind delegation from empty link to openclose button

$menu.find("." + classItem + " > a").each(function () {

var $this = $(this);

if (!$this.attr("href")) {

$this.bind("click", function () {

$this.closest("." + classItem).find("." + classButton + ":first").trigger("click");

return false;

});

}

});

};

return module;

}(coremedia.blueprint.basic.dropdown || {});

/**

* Accordion functionality

*/

coremedia.blueprint.basic.accordion = function (module) {

var $ = coremedia.blueprint.$;

var $document = $(document);

// class name definitions

var classAccordionItem = "cm-accordion-item";

var classAccordionItemHeader = classAccordionItem + "__header";

var classAccordionItemContent = classAccordionItem + "__content";

var classAccordionItemHeaderActive = classAccordionItemHeader + "--active";

var classAccordionItemContentActive = classAccordionItemContent + "--active";

// prefix/namespace for events in this module

var EVENT_PREFIX = "coremedia.blueprint.basic.accordion.";

/**

* @type {string} Name for the event to be triggered if accordion has changed

*/

module.EVENT_ACCORDION_CHANGED = EVENT_PREFIX + "accordionChanged";

/**

* Changes the active item of the given accordion to the given item

* @param {jQuery} $accordion the accordion to change

* @param {jQuery} $activeItem the item to be active

*/

module.change = function ($accordion, $activeItem) {

$accordion.find(".cm-accordion-item").not($activeItem).each(function () {

var $item = $(this);

$item.find("." + classAccordionItemHeader).first().removeClass(classAccordionItemHeaderActive);

$item.find("." + classAccordionItemContent).first().removeClass(classAccordionItemContentActive);

});

$activeItem.find("." + classAccordionItemHeader).first().addClass(classAccordionItemHeaderActive);

$activeItem.find("." + classAccordionItemContent).first().addClass(classAccordionItemContentActive);

$accordion.trigger(module.EVENT_ACCORDION_CHANGED, [$activeItem]);

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

};

return module;

}(coremedia.blueprint.basic.accordion || {});

/**

* Popup functionality

*/

coremedia.blueprint.basic.popup = function (module) {

// identifier/class name definitions

var identifier = "cm-popup";

var classPopupActive = identifier + "--active";

// prefix/namespace for events in this module

var EVENT_PREFIX = "coremedia.blueprint.basic.popup.";

/**

* @type {string} name of the event to be triggered if popup has changed

*/

module.EVENT_POPUP_CHANGED = EVENT_PREFIX + "popupChanged";

/**

* Opens the given popup

* @param {jQuery} $popup popup to be opened

*/

module.open = function ($popup) {

$popup.addClass(classPopupActive);

$popup.trigger(module.EVENT_POPUP_CHANGED, [true]);

};

/**

* Closes the given popup

* @param {jQuery} $popup popup to be closed

*/

module.close = function ($popup) {

$popup.removeClass(classPopupActive);

$popup.trigger(module.EVENT_POPUP_CHANGED, [false]);

};

/**

* Opens the popup if it is closed and closes the popup if it is opened

* @param $popup popup to be toggled

*/

module.toggle = function ($popup) {

if ($popup.hasClass(classPopupActive)) {

module.close($popup);

} else {

module.open($popup);

}

};

return module;

}(coremedia.blueprint.basic.popup || {});

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------

coremedia.blueprint.$(function () {

var $ = coremedia.blueprint.$;

var $window = $(window);

var $document = $(document);

// append to dom ready (will be executed after all dom ready functions have finished)

$(function () {

coremedia.blueprint.nodeDecorationService.decorateNode(document);

});

// load all dynamic fragments. The special header X-Requested-With is needed by the CAE to identify

// the request as an Ajax request

coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {

var identifier = "cm-fragment";

var selector = "[data-" + identifier + "]";

$target.findAndSelf(selector).each(function () {

var $fragment = $(this);

var url = $(this).data(identifier);

coremedia.blueprint.basic.ajax({

url: url,

dataType: "text"

}).done(function (html) {

var $html = $(html);

$fragment.replaceWith($html);

coremedia.blueprint.nodeDecorationService.decorateNode($html);

$document.trigger(coremedia.blueprint.basic.EVENT_NODE_APPENDED, [$html]);

});

});

});

// this will substitute all data-hrefs rendered by ESI

coremedia.blueprint.nodeDecorationService.addNodeDecorator(coremedia.blueprint.basic.renderFragmentHrefs);

// init all toggleItems

coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {

var selector = ".toggle-item";

$target.findAndSelf(selector).each(function (index, toggleItem) {

coremedia.blueprint.basic.toggle.init(toggleItem);

});

});

// initializes the drop down menu

coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {

var selector = ".cm-dropdown";

$target.findAndSelf(selector).each(function () {

$(this).on(coremedia.blueprint.basic.dropdown.EVENT_DROPDOWN_CHANGED, function () {

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

coremedia.blueprint.basic.dropdown.init(this);

});

});

// adds removes spinner if an image has finished loading

coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-image--loading", function ($target) {

$target.on("load", function() {

$target.removeClass("cm-image--loading");

})

});

// initializes responsive images

coremedia.blueprint.nodeDecorationService.addNodeDecoratorBySelector(".cm-image--responsive", function ($target) {

$target.responsiveImages();

});

// handle closing of notification box

coremedia.blueprint.nodeDecorationService.addNodeDecorator(function ($target) {

var selector = ".cm-notification__dismiss";

$target.findAndSelf(selector).click(function () {

$(this).closest(".cm-notification").fadeOut();

});

});

// add readmore functionality if text is too long

coremedia.blueprint.nodeDecorationService.addNodeDecoratorByData({lines: undefined}, "cm-readmore", function ($target, config, identifier) {

// read the line height for the given target

var lineHeight = $target.css("line-height");

// only proceed if config is valid and lineHeight could be retrieved

if (config.lines !== undefined && lineHeight !== undefined) {

var $wrapper = $target.find("." + identifier + "__wrapper");

var $buttonbar = $target.find("." + identifier + "__buttonbar");

var $buttonMore = $buttonbar.find("." + identifier + "__button-more");

var $buttonLess = $buttonbar.find("." + identifier + "__button-less");

// calculate line height in px

if (lineHeight.indexOf("px") > -1) {

// line height is already in px, just remove the unit

lineHeight = lineHeight.replace("px", "");

} else {

// line height is relative to font-size, calculate line height by multiplying its value with font-size

lineHeight = lineHeight * $target.css("font-size").replace("px", "");

}

var maxHeight = Math.floor(lineHeight * config.lines);

// enable readmore functionality if text without the readmore button exceeds the maximum height

// it would make no sense to add a readmore button if it would take more space as rendering the full text

if ($wrapper.height() - 2 * $buttonbar.height() > maxHeight) {

$target.addClass(identifier + "--enabled");

// default without any action by the user ist the non expanded (less) version

$target.addClass(identifier + "--less");

$wrapper.css("max-height", maxHeight);

$buttonMore.on("click", function () {

$target.removeClass(identifier + "--less");

$target.addClass(identifier + "--more");

$wrapper.css("max-height", "");

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

$buttonLess.on("click", function () {

$target.removeClass(identifier + "--more");

$target.addClass(identifier + "--less");

$wrapper.css("max-height", maxHeight);

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

$buttonLess.on("click", function () {

$target.removeClass(identifier + "--more");

$target.addClass(identifier + "--less");

$wrapper.css("max-height", maxHeight);

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

}

}

});

// trigger layout changed event if the size of the window changes using smartresize plugin

$window.smartresize(function () {

$document.trigger(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED);

});

// --- EVENTS --------------------------------------------------------------------------------------------------------

// recalculate responsive images if layout changes

// recalculate hotzones if layout changes

$document.on(coremedia.blueprint.basic.EVENT_LAYOUT_CHANGED, function () {

$(".cm-image--responsive").responsiveImages();

$(".cm-imagemap").each(function () {

coremedia.blueprint.imagemap.update($(this));

});

});

});
