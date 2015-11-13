package com.coremedia.blueprint.studio.property {
import com.coremedia.blueprint.studio.Blueprint_properties;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.Blob;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.ObjectUtils;

import ext.Ext;

public class ImageLinkListRenderer {
  private static const IMAGE_WIDTH:int = 80;
  private static const IMAGE_HEIGHT:int = 54;
  private static const DEFAULT_CROPPING:String = '/rm/box;width=' + IMAGE_WIDTH + ';height=' + IMAGE_HEIGHT;

  /**
   * Displays the image for each link list item.
   */
  public static function thumbColRenderer(value:String, metaData:*, record:BeanRecord):String {
    var thumbUri:String;
    if (value) {
      thumbUri = value;
    }
    return renderImage(thumbUri);
  }

  /**
   * Builds the actual HTML for the given URI.
   * @param thumbUri The thumbnail URI to be rendered.
   * @return The HTML for the thumbnail or the default image for empty thumbnails.
   */
  public static function renderImage(thumbUri:String, cropping:Boolean=true):String {
    if (thumbUri) {
      thumbUri = '<div class="image-thumb-wrapper"><img src="' + thumbUri +
              (cropping ? DEFAULT_CROPPING : '') + '" style="height:' + IMAGE_HEIGHT + 'px;max-width:' + IMAGE_WIDTH + 'px;"/></div>';
      return thumbUri;
    }

    var tip:String = Blueprint_properties.INSTANCE.ImageLinkList_no_data;
    thumbUri = '<div class="imagelist-icon-no-picture" ext:qtip="' +
            tip + '"><img src="' + Ext.BLANK_IMAGE_URL + '"/></div>';
    return thumbUri;
  }

  public static function renderPicture(content:Content):String {
    return blobPropertyUriResolver(content, 'data');
  }

  public static function renderSymbol(content:Content):String {
    return blobPropertyUriResolver(content, 'icon');
  }

  private static function blobPropertyUriResolver(content:Content, property:String):String {
    var result:String = undefined;
    var blob:Blob = ObjectUtils.getPropertyAt(content, 'properties.'+property) as Blob;
    if (blob && blob.getUri()) {
      result = blob.getUri();
    }
    return result;
  }

  public static function renderCMCollections(content:Content):String {
    return propertyPathLoader(content, 'properties.items');
  }

  public static function renderCMSelectionRules(content:Content):String {
    return propertyPathLoader(content, 'properties.defaultContent');
  }

  public static function renderCMTeasable(content:Content):String {
    return propertyPathLoader(content, 'properties.pictures');
  }



  private static function propertyPathLoader(content:Content, path:String):String {
    var result:String = undefined;
    var contentList:Array = ObjectUtils.getPropertyAt(content, path);
    if (contentList && contentList.length > 0) {
      for (var i:int = 0; i < contentList.length; i++) {
        var uri:String = editorContext.getThumbnailUri(contentList[i]);
        if (uri) {
          result = uri;
          break;
        }
      }
    }
    return result;
  }

}
}