  <#-- TODO: this is a temp file and will be deleted before release -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->

<style>
  #page {
      max-width: none;
  }

  .temp-image-list td {
    vertical-align: top;
  }

  .ratio20x31 li {
    width: 646px;
    padding-bottom: 10px;
  }

  .ratio1x1 li {
    width: 1000px;
    padding-bottom: 10px;
  }
</style>
<table class="temp-image-list">
    <tr>
        <td>
            <h1>Bilder in 2:3,1 und 646x1000px</h1>
            <ul class="ratio20x31">
              <#list self.items![] as item>
                <li>
                  <@cm.include self=item params={
                    "limitAspectRatios": ["portrait_ratio20x31"],
                    "classBox": "cm-aspect-ratio-box",
                    "classImage": "cm-aspect-ratio-box__content"
                  } />
                </li>
              </#list>
            </ul>
        </td>
        <td>
            <h1>Bilder in 1:1 und 1000x1000px</h1>
            <ul>
              <#list self.items![] as item>
                <li>
                  <@cm.include self=item params={
                    "limitAspectRatios": ["portrait_ratio1x1"],
                    "classBox": "cm-aspect-ratio-box",
                    "classImage": "cm-aspect-ratio-box__content"
                  } />
                </li>
              </#list>
            </ul>
        </td>
    </tr>
</table>

