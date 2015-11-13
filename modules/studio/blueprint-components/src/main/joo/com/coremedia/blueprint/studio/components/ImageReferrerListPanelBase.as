package com.coremedia.blueprint.studio.components {

import com.coremedia.blueprint.studio.property.ImageLinkListRenderer;
import com.coremedia.cms.editor.sdk.columns.grid.StyleClassAddingRenderer;
import com.coremedia.cms.editor.sdk.columns.grid.TypeIconColumnBase;
import com.coremedia.cms.editor.sdk.config.referrerListPanel;
import com.coremedia.cms.editor.sdk.premular.ReferrerListPanel;
import com.coremedia.ui.store.BeanRecord;

import ext.XTemplate;

public class ImageReferrerListPanelBase extends ReferrerListPanel {
  public function ImageReferrerListPanelBase(config:referrerListPanel) {
    super(config);
  }

  protected static function getContentItemColumnTemplate():XTemplate {
    var thumbnailTemplateHtml:String = ImageLinkListRenderer.thumbColRenderer(
            '{thumbnailUrl}', {}, new BeanRecord(null, null));
    var nameTemplateHtml:String = StyleClassAddingRenderer.makeRenderer('nameClass')(
            '{name}', {}, new BeanRecord(null, null));

    var typeIconTemplate:XTemplate = TypeIconColumnBase.getXTemplate(false);

    return new XTemplate(
            '<div class="content-images-wrapper">' + thumbnailTemplateHtml,
            '<div class="content-item-icon">',
            typeIconTemplate.html,
            '</div></div>',
            '<div class="table-cell-name"><span class="table-cell-name-span">',
            nameTemplateHtml,
            '</span></div>');
  }
}
}
