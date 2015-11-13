package com.coremedia.blueprint.studio.topicpages.administration {
import com.coremedia.blueprint.studio.topicpages.config.taxonomyCombo;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.components.LocalComboBox;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.data.Record;
import ext.form.ComboBox;

/**
 * The base class of the taxonomy combo.
 * The taxonomy combo displays all available taxonomies, global and site depending ones.
 */
public class TaxonomyComboBase extends LocalComboBox {
  private var taxonomiesExpression:ValueExpression;
  private var selectionExpression:ValueExpression;

  public function TaxonomyComboBase(config:taxonomyCombo) {
    super(config);
    this.selectionExpression = config.selectionExpression;
  }


  override protected function afterRender():void {
    super.afterRender();
    addListener('select', valueSelected);
  }

  /**
   * The selection listener method for the combo box.
   * @param combo
   * @param record
   */
  private function valueSelected(combo:ComboBox, record:Record):void {
    var id:String = record.data.id;
    var content:Content = ContentUtil.getContent(id);
    selectValue(content);
  }

  /**
   * Returns the value expression that contains the all taxonomies.
   * @return
   */
  protected function getTaxonomiesExpression():ValueExpression {
    if(!taxonomiesExpression) {
      taxonomiesExpression = ValueExpressionFactory.create('items', beanFactory.createLocalBean());
      var remoteBean:RemoteBean = beanFactory.getRemoteBean('topicpages/taxonomies');
      remoteBean.invalidate(function():void {
        var values:Array = remoteBean.get('items');
        taxonomiesExpression.setValue(values);
        if(values.length > 0) {
          var taxonomyFolder:Content = values[0];
          taxonomyFolder.load(function():void {
            ValueExpressionFactory.create(ContentPropertyNames.PATH, taxonomyFolder).loadValue(function():void {
              selectValue(taxonomyFolder);
            });
          });

        }
      });
    }
    return taxonomiesExpression;
  }

  /**
   * Applies the given content as selection.
   * @param content
   */
  private function selectValue(content:Content):void {

    selectionExpression.setValue(content);
    setValue(formatDisplayNameInternal(content));
  }

  protected static function formatDisplayName(ignored:String, content:Content):String {
    return formatDisplayNameInternal(content);
  }

  protected static function formatDisplayNameInternal(content:Content):String {
    var site:String = editorContext.getSitesService().getSiteNameFor(content);
    if(site) {
      return content.getName() + ' (' + site + ')';
    }
    return content.getName();
  }
}
}