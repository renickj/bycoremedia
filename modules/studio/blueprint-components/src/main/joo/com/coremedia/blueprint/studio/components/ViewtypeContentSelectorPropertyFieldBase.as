package com.coremedia.blueprint.studio.components {

import com.coremedia.blueprint.base.components.viewtypes.Viewtypes_properties;
import com.coremedia.blueprint.studio.config.components.viewtypeContentSelectorPropertyField;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.Blob;
import com.coremedia.ui.data.beanFactory;

import ext.Ext;
import ext.XTemplate;

public class ViewtypeContentSelectorPropertyFieldBase extends PathDoctypeContentSelectorPropertyField {

  private var noSelectionBean:Bean;

  private static var template:XTemplate = new XTemplate('<tpl for=".">',
            '<div class="bean-list-chooser-item-wrap {mergedItemClass}">',
            '<div class="bean-list-chooser-item-border">',
            '<div class="bean-list-chooser-item" {description:unsafeQtip}>',
            '<div class="large">',
            '<img width="100%" height="100%" src="{iconUri}" class="{emptySelectionItemClass}"/>',
            '</div>',
            '</div>',
            '<p>{descriptionShort}</p>',
            '</div>',
            '</div>',
            '</tpl>');

  public function ViewtypeContentSelectorPropertyFieldBase(config:viewtypeContentSelectorPropertyField = null) {
    super(config);
  }

  internal function getNoSelectionBean():Bean {
    if (!noSelectionBean) {
      var type:Bean = beanFactory.createLocalBean({name: "CMObject"});
      noSelectionBean = beanFactory.createLocalBean({
        name: "No Selection",
        type: type
      });
    }
    return noSelectionBean;
  }

  public function computeIconURL(name:String, content:Content):String {
    if (content === getNoSelectionBean()) {
      return Ext.BLANK_IMAGE_URL;
    }

    if (content && content.getProperties()) {
      var imageBlob:Blob = content.getProperties().get('icon');
      if (imageBlob) {
        return imageBlob.getUri() + '/rm/box;w=64;h=64';
      }
    }
    return Ext.BLANK_IMAGE_URL;
  }

  public function getDescription(name:String, content:Content):String {
    if (content === getNoSelectionBean()) {
      return "";
    }

    if (content && content.getProperties()) {
      var key:String = name.replace(' ', '') + '_text';
      var description:String = Viewtypes_properties.INSTANCE[key];
      if (!description) {
        description = content.getProperties().get('description');
      }
      if (!description || description.length === 0) {
        description = name;
      }
      return description;
    }
    return "";
  }

  public function getDescriptionCurtailed(name:String, content:Content):String {
    return curtail(getDescription(name, content));
  }

  private static const SHORT_NAME_MAX_LENGTH:int = 15;

  internal static function curtail(s:String):String {
    if (!s) return '';
    if (s.length > SHORT_NAME_MAX_LENGTH) {
      return s.substr(0, SHORT_NAME_MAX_LENGTH - 3) + '...';
    }
    return s;
  }

  public static function getTemplate():XTemplate {
    return template;
  }

  internal function comparator(val1:Bean, val2:Bean):Number {
    if (val1 === getNoSelectionBean()) {
      return -1;
    }
    if (val2 === getNoSelectionBean()) {
      return 1;
    }
    return getDescription(val1.get('name'), val1 as Content).localeCompare(getDescription(val2.get('name'), val2 as Content));
  }

}
}
