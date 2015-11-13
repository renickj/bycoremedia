<#-- @ftlvariable name="self" type="com.coremedia.objectserver.view.ViewException" -->
<#-- @ftlvariable name="errorIcon" type="java.lang.String" -->
<#-- @ftlvariable name="compact" type="java.lang.Boolean" -->

<#escape x as x?html>
<#if !errorIcon?has_content>
  <#assign errorIcon="bug" />
</#if>
<#if !compact??>
  <#assign compact=true />
</#if>
<#assign boxId=bp.generateId("cae-rendererror-background") />
<#assign stackTrace=bp.getStackTraceAsString(self)/>

<style type="text/css">
    .cae-rendererror-icon {
        cursor: pointer;
        min-height: 16px;
        min-width: 16px;
        padding-left: 18px;
        font-size: 11px;
        border: none;
    }

    .cae-rendererror-icon--bug {
        background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAALbSURBVDjLfZHrM5RhGMb9A33uq7ERq7XsOry7y2qlPqRFDsVk0c4wZZgRckjJeWMQRhkju5HN6V1nERa7atJMw0zMNLUow1SOUby7mNmrdzWJWn245nnumee+7vt3PRYALI6SZy8fnt08kenu0eoW4E666v9+c6gQDQgYB2thJwGPNrfOmBJfK0GTSxT/qfP3/xqcNk3s4SX9rt1VbgZBs+tq9N1zSv98vp5fwzWG3BAUHGkg7CLWPToIw97KJLHBb3QBT+kMXq0zMrQJ0M63IbUoAuIozk2zBjSnyL3FFcImYt2HPAvVlBx97+pRMpoH1n1bRPT6oXmsEk7Fp+BYYA+HPCY9tYPYoDn32WlOo6eSh8bxUuQ+lyK9MwTJnZEQVhJgFdhBWn8Z3v42uv0NaM4dmhP8Bpc6oZJYuqTyh/JNMTJ7wpGo8oPkiRfyO4IxOXId1cOFcMixgyDUuu0QAq/e+RVRywUh54KcqEBGdxgSSF9IakUIb/DD24FIrOpaoO6PBSuDCWaazaZdsnXcoQyIR1xDaFMAigbjEN8sRpjCC0F1F9A3EIdlOofdzWlMtgfDN5sN28QTxpPxDNjEWv0J0O0BZ+uaSoqyoRRIHnsjUOGDqu4ETLRehGG5G4bPJVib6YHioRDiVPvjph5GtOXtfQN+uYuMU8RCdk8KguRiFHelobVBjJX3JAzz2dDe42JnlcSE/IxxvFoUaPYbuTK2hpFkiZqRClSRUnxUp2N7qQ7U9FVoZU7Qz6VgffYZBkuJxddlxLF/DExySGdqOLfsMag4j290cPpPSdj6EPJLOgmNUoo5TTnac9mlZg1MypJxx+a0Jdj+Wrk3fUt3hUbg7J3UbAyoLx3Q5rAWNVn2TLMG9HoL1MoMttfUMCzRGSy1HJAKuz+msDBWj6F0mxazBi8LOSsvZI7UaB6boidRA5lM9GfYYfiOLUU3Ueo0a0qdwqAGk61GfwIga508Gu46TQAAAABJRU5ErkJggg==') no-repeat top left;
    }

    .cae-rendererror-icon--info {
        background: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAKcSURBVDjLpZPLa9RXHMU/d0ysZEwmMQqZiTaP0agoaKGJUiwIxU0hUjtUQaIuXHSVbRVc+R8ICj5WvrCldJquhVqalIbOohuZxjDVxDSP0RgzyST9zdzvvffrQkh8tBs9yy9fPhw45xhV5X1U8+Yhc3U0LcEdVxdOVq20OA0ooQjhpnfhzuDZTx6++m9edfDFlZGMtXKxI6HJnrZGGtauAWAhcgwVnnB/enkGo/25859l3wIcvpzP2EhuHNpWF9/dWs/UnKW4EOGDkqhbQyqxjsKzMgM/P1ymhlO5C4ezK4DeS/c7RdzQoa3x1PaWenJjJZwT9rQ1gSp/js1jYoZdyfX8M1/mp7uFaTR8mrt29FEMQILr62jQ1I5kA8OF59jIItVA78dJertTiBNs1ZKfLNG+MUHX1oaURtIHEAOw3p/Y197MWHEJEUGCxwfHj8MTZIcnsGKxzrIURYzPLnJgbxvG2hMrKdjItjbV11CYKeG8R7ygIdB3sBMFhkem0RAAQ3Fuka7UZtRHrasOqhYNilOwrkrwnhCU/ON5/q04vHV48ThxOCuoAbxnBQB+am65QnO8FqMxNCjBe14mpHhxBBGCWBLxD3iyWMaYMLUKsO7WYH6Stk1xCAGccmR/Ozs/bKJuXS39R/YgIjgROloSDA39Deit1SZWotsjD8pfp5ONqZ6uTfyWn+T7X0f59t5fqDhUA4ry0fYtjJcWeZQvTBu4/VqRuk9/l9Fy5cbnX+6Od26s58HjWWaflwkusKGxjm1bmhkvLXHvh1+WMbWncgPfZN+qcvex6xnUXkzvSiYP7EvTvH4toDxdqDD4+ygT+cKMMbH+3MCZ7H9uAaDnqytpVX8cDScJlRY0YIwpAjcNcuePgXP/P6Z30QuoP4J7WbYhuQAAAABJRU5ErkJggg==') no-repeat top left;
    }

    .cae-rendererror-background {
        position: fixed;
        top: 0;
        left: 0;
        display: none;
        width: 100%;
        height: 100%;
        z-index: 998;
        background-color: rgba(32, 0, 0, 0.5);
    }

    .cae-rendererror-box {
        width: 80%;
        max-width: 700px;
        margin: 5% auto;
        z-index: 999;
        background: #fff;
        padding: 10px;
        border: 3px solid #800000;
        box-shadow: 0 0 20px #000;
        font-family: Arial, Helvetica, sans-serif;
        font-size: 12px;
    }

    .cae-rendererror-box button {
        float: right;
    }

    table.cae-rendererror {
        position: relative;
        border-collapse: collapse;
    }

    table.cae-rendererror td {
        font-weight: bold;
        vertical-align: top;
        word-break: break-all;
        padding-bottom: 10px;
    }

    .cae-rendererror-label {
        width: 75px;
    }

    .cae-rendererror-stacktrace p {
        font-weight: bold;
    }

    .cae-rendererror-stacktrace textarea {
        display: block;
        font-family: "Courier New", sans-serif;
        font-size: 10px;
        width: 100%;
        height: 310px;
        -moz-box-sizing: border-box;
        box-sizing: border-box;
        overflow-y: scroll;
        white-space: pre;
        word-wrap: normal;
    }
</style>

<script>
  var coremedia = (function (module) {
    return module;
  }(coremedia || {}));
  coremedia.blueprint = (function (module) {
    return module;
  }(coremedia.blueprint || {}));
  coremedia.blueprint.error = (function (module) {
    if (typeof module.boxes === "undefined") {
      module.boxes = [];
    }
    module.registerBox = function (id) {
      module.boxes[module.boxes.length] = id;
    };
    module.showBox = function (id) {
      var modal = document.getElementById(id);
      // move modal div to end of dom to ensure that it will be rendered as overlay
      document.body.appendChild(modal);
      modal.style.display = "block";
    };
    module.hideBox = function (id) {
      document.getElementById(id).style.display = "none";
    };
    module.hideAllBoxes = function () {
      for (var i = 0; i < module.boxes.length; i++) {
        module.hideBox(module.boxes[i]);
      }
    };
    return module;
  }(coremedia.blueprint.error || {}));

  document.onkeydown = function(e) {
    e = e || window.event;
    if (e.keyCode === 27) {
      coremedia.blueprint.error.hideAllBoxes();
    }
  };
  coremedia.blueprint.error.registerBox("${boxId}");
  // log debug output to javascript console
  var noop = function () {};
  var console = window.console || {};
  console.groupCollapsed = console.groupCollapsed || noop;
  console.warn = console.warn || noop;
  console.groupEnd = console.groupEnd || noop;
  // escape xml for better readability
  var dummy = document.createElement("textarea");
  <#-- messagesAsList is always xml escaped, so no injection possible -->
  dummy.innerHTML = "<@cm.unescape text=(self.messagesAsList!"")?js_string />";
  console.groupCollapsed("CoreMedia Error:\n" + dummy.value);
  console.warn("model: ${self.bean!""}");
  console.warn("view name: ${self.viewName!""}");
  console.warn("view: ${self.view!""}");
  console.groupEnd();
</script>

<#assign buttonText="" />
<#if !compact>
  <#assign buttonText="Details" />
  <table class="cae-rendererror">
      <tr class="cae-rendererror-model">
          <td class="cae-rendererror-label">model</td>
          <td>${self.bean!""}</td>
      </tr>
      <tr class="cae-rendererror-cause">
          <td>cause</td>
          <td><#noescape>${self.messagesAsHtml!""}</#noescape></td>
      </tr>
  </table>
</#if>
<button class="cae-rendererror-icon cae-rendererror-icon--${errorIcon!""}" title="Click for more information like stack traces etc." onclick="coremedia.blueprint.error.showBox('${boxId}');">${buttonText}</button>

<div class="cae-rendererror-bug" title="Click for more information like stack traces etc." onclick="coremedia.blueprint.error.showBox('${boxId}');"></div>
<div id="${boxId}" class="cae-rendererror-background">
    <div class="cae-rendererror-box">
        <button onclick="coremedia.blueprint.error.hideBox('${boxId}');">Close</button>
        <table class="cae-rendererror">
            <tr class="cae-rendererror-model">
                <td class="cae-rendererror-label">model</td>
                <td>${self.bean!""}</td>
            </tr>
            <tr class="cae-rendererror-hierarchy">
                <td class="cae-rendererror-label">class hierarchy</td>
                <td>
                  <#list self.getHierarchy() as hierachy>
                      ${hierachy}
                  </#list>
                </td>
            </tr>
            <tr class="cae-rendererror-viewname">
                <td>view-name</td>
                <td>${self.viewName!""}</td>
            </tr>
            <tr class="cae-rendererror-view">
                <td>view</td>
                <td>${self.view!""}</td>
            </tr>
            <tr class="cae-rendererror-cause">
                <td>cause</td>
                <td><#noescape>${self.messagesAsHtml!""}</#noescape></td>
            </tr>
        </table>
        <div class="cae-rendererror-stacktrace">
            <label for="cae-rendererror-text">Full Stack Trace:</label>
            <textarea id="cae-rendererror-text" readonly onclick="this.select();">${stackTrace!}</textarea>
        </div>
    </div>
</div>
</#escape>
