/* --- create own namespace in javascript for own stuff ------------------------------------------------------------- */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  return module;
}(coremedia.blueprint || {}));

/**
 *
 */
coremedia.blueprint.nodeDecorationService = function (module) {

  var $ = coremedia.blueprint.$;

  /**
   * specifies functionalities to be applied if applyFunctionalityHandlers is called
   * @type {Array}
   */
  var nodeDecorators = [];

  /**
   * Adds a node decorator to list of node decorators
   *
   * @param {function(jQuery)} nodeDecorator
   */
  module.addNodeDecorator = function (nodeDecorator) {
    nodeDecorators.push(nodeDecorator);
  };

  /**
   * Adds a node decorator and already performs selection tasks
   *
   * @param {String} selector
   * @param {function(jQuery)} handler
   */
  module.addNodeDecoratorBySelector = function (selector, handler) {
    nodeDecorators.push(function ($target) {
      $target.findAndSelf(selector).each(function () {
        handler($(this));
      });
    });
  };

  /**
   * Adds a node decorator and already performs selection and configuration tasks
   *
   * @param {object} baseConfig
   * @param {String} identifier
   * @param {function(jQuery, object, String)} handler
   */
  module.addNodeDecoratorByData = function (baseConfig, identifier, handler) {
    nodeDecorators.push(function ($target) {
      var selector = "[data-" + identifier + "]";
      $target.findAndSelf(selector).each(function () {
        var $this = $(this);
        var config = $.extend({}, baseConfig, $this.data(identifier));
        handler($this, config, identifier);
      });
    });
  };

  /**
   * Applies node decorators to target node
   *
   * @param {object|jQuery} node can be plain DOM-Node or JQuery Wrapped DOM-Node
   */
  module.decorateNode = function (node) {
    var $target;
    if (node instanceof $) {
      $target = node;
    } else {
      $target = $(node);
    }
    nodeDecorators.forEach(function (functionality) {
      functionality($target);
    });
  };

  return module;
}(coremedia.blueprint.nodeDecorationService || {});
