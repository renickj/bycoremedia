(function (name) {
  var scripts = document.getElementsByTagName("script");

  for (var i = scripts.length - 1; i >= 0; i--) {
    var src = scripts[i].src;
    var l = src.length;
    var length = name.length;
    if (src.substr(l - length) == name) {
    // set a global property here
      jasmine.getFixtures().fixturesPath = src.substr(0, l - length);

    }
  }
})("configureFixtures.js");