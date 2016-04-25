/* --- create own namespace in javascript for own stuff ------------------------------------------------------------- */
var coremedia = (function (module) {
return module;
}(coremedia || {}));
coremedia.blueprint = (function (module) {
// map jQuery/$ into our namespace. Use it with coremedia.blueprint.$ instead of jQuery or $
module.$ = $.noConflict(true);
return module;
}(coremedia.blueprint || {}));