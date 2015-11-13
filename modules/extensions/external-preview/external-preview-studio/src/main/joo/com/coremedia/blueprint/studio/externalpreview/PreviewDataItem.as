package com.coremedia.blueprint.studio.externalpreview {
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;

import net.jangaroo.net.URIUtils;

public class PreviewDataItem {
  private var content:Content;
  private var active:Boolean = false;
  private var preview:Boolean = true;

  public function PreviewDataItem(content:Content) {
    this.content = content;
    this.preview = !StudioUtil.isExcludedDocumentTypeWithoutPreview(content);
  }

  public function setActive(b:Boolean):void {
    active = b;
  }

  public function asJSON():Object {
    var name:String = content.getName();
    var previewUrl:String = "";
    if (!URIUtils.parse(content.getPreviewUrl()).isAbsolute) {
      previewUrl = ExternalPreviewStudioPluginBase.CONTENT_PREVIEW_URL_PREFIX + content.getPreviewUrl();
    } else {
      previewUrl = content.getPreviewUrl();
    }
    return {
      active:active,
      modificationDate:content.get('modificationDate'),
      name:name,
      preview:this.preview,
      id: IdHelper.parseContentId(content),
      previewUrl: previewUrl,
      lifecycleStatus: content.getLifecycleStatus()
    }
  }

  public function getContentId():int {
    return IdHelper.parseContentId(content);
  }
}
}