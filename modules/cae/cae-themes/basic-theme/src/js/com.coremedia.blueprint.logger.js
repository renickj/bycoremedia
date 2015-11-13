var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
  return module;
}(coremedia.blueprint || {}));

/**
 * JavaScript Console Logger
 *
 * is disabled by default.
 *
 * usage:
 * 1) enable logging:
 * coremedia.blueprint.logger.enable();
 *
 * 2) print to log:
 * coremedia.blueprint.logger.log("log this");
 *
 * 3) disable logging:
 * coremedia.blueprint.logger.disable();
 */
coremedia.blueprint.logger = function (module) {

  var $ = coremedia.blueprint.$;

  var c = console;
  var noop = function () {};

  ["log", "info", "warn", "error"].forEach(function (method) {
    if (Function.prototype.apply && window.console) {
      // check if method is a function
      if (typeof console[method] !== "function") {
        // if functionality is an object (e.g. IE9)
        if (Function.prototype.bind && typeof console[method] == "object") {
          // bind call to function
          c[method] = this.bind(console[method], console);
        } else {
          // no operation
          c[method] = noop;
        }
      }
    }
  }, Function.prototype.call);

  /**
   * Default settings for logger
   * @type {{enabled: boolean, prefix: string}}
   */
  var settings = {
    enabled: false,
    prefix: '[CoreMedia] '
  };

  /**
   * Prints a log to the JavaScript console.
   *
   * @param {...*} message Messages to print
   */
  module.log = function (message) {
    if (settings.enabled) {
      // copy arguments so they can be modified
      var newArguments = Array.prototype.slice.call(arguments, 0);
      // add prefix as first argument to arguments
      newArguments.unshift(settings.prefix);
      try {
        // apply log function to arguments
        c.log.apply(this, newArguments);
      } catch (e) {
        // fallback if anything goes wrong
        c.log(Array.prototype.join.call(newArguments, ' '));
      }
    }
  };

  /**
   * Enables logging to JavaScript console.
   */
  module.enable = function () {
    if (!settings.enabled) {
      settings.enabled = true;
      module.log("Logging enabled");
    }
    // enable logger of cycle2
    if (typeof $.fn.cycle !== "undefined") {
      $.fn.cycle.defaults.log = true;
    }
  };

  /**
   * Disables logging to JavaScript console.
   */
  module.disable = function () {
    if (settings.enabled) {
      module.log("Logging disabled");
      settings.enabled = false;
    }
    // disable logger of cycle2
    if (typeof $.fn.cycle !== "undefined") {
      $.fn.cycle.defaults.log = false;
    }
  };

  return module;
}(coremedia.blueprint.logger || {});

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.blueprint.$(function () {

  var $ = coremedia.blueprint.$;

  // enable logging if developerMode is active
  if ($('[data-cm-developer-mode]').length) {
    coremedia.blueprint.logger.enable();
  }
});
