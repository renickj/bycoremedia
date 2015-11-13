describe("fragment hrefs", function () {

  var $ = coremedia.blueprint.$;

  beforeEach(function () {
    loadFixtures("fragment hrefs.html");
    coremedia.blueprint.basic.renderFragmentHrefs($(document.body));
  });

  it("should have replaced all occurences of $nextUrl$", function () {
    $("a[data-href]").each(function () {
      var $this = $(this);
      expect($this.attr("href").search(/\$nextUrl\$/g)).toBe(-1);
    });
  });
});
describe("toggle functionality", function () {

  beforeEach(function () {
    loadFixtures("toggles.html");
    $(".toggle").each(function () {
      coremedia.blueprint.basic.toggle.init(this);
    });
  });

  it("should init a toggle with on", function () {
    var $toggle1 = $("#toggle1");
    expect(coremedia.blueprint.basic.toggle.getState($toggle1)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
  });

  it("should toggle a toggle by functionality", function () {
    var $toggle1 = $("#toggle1");
    var $toggle2 = $("#toggle2");
    var $toggle3 = $("#toggle3");
    coremedia.blueprint.basic.toggle.toggle($toggle1);
    expect(coremedia.blueprint.basic.toggle.getState($toggle1)).toBe(coremedia.blueprint.basic.toggle.STATE_OFF);
    expect(coremedia.blueprint.basic.toggle.getState($toggle2)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
    expect(coremedia.blueprint.basic.toggle.getState($toggle3)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
    coremedia.blueprint.basic.toggle.toggle($toggle1);
    expect(coremedia.blueprint.basic.toggle.getState($toggle1)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
    expect(coremedia.blueprint.basic.toggle.getState($toggle2)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
    expect(coremedia.blueprint.basic.toggle.getState($toggle3)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
  });

  it("should toggle a toggle if someone clicks the toggle button", function () {
    var $toggle1 = $("#toggle1");
    var $toggle2 = $("#toggle2");
    var $toggle3 = $("#toggle3");
    $toggle1.find(".toggle-button").trigger("click");
    expect(coremedia.blueprint.basic.toggle.getState($toggle1)).toBe(coremedia.blueprint.basic.toggle.STATE_OFF);
    expect(coremedia.blueprint.basic.toggle.getState($toggle2)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
    expect(coremedia.blueprint.basic.toggle.getState($toggle3)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
    $toggle1.find(".toggle-button").trigger("click");
    expect(coremedia.blueprint.basic.toggle.getState($toggle1)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
    expect(coremedia.blueprint.basic.toggle.getState($toggle2)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
    expect(coremedia.blueprint.basic.toggle.getState($toggle3)).toBe(coremedia.blueprint.basic.toggle.STATE_ON);
  });

});
