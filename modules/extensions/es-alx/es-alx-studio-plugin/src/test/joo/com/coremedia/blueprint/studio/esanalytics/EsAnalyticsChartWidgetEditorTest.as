package com.coremedia.blueprint.studio.esanalytics {

import com.coremedia.blueprint.studio.config.esanalytics.esAnalyticsChartWidgetEditor;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

import ext.ComponentMgr;
import ext.data.Store;
import ext.form.ComboBox;

import flexunit.framework.TestCase;

public class EsAnalyticsChartWidgetEditorTest extends TestCase {
  private static const FIELD_LABEL:String = "Site";
  private static const VALUE_FIELD:String = "id";
  private static const STORE_KEY_01:String = "test-1";
  private static const STORE_VALUE_01:String = "test-1-" + new Date();
  private static const STORE_KEY_02:String = "test-2";
  private static const STORE_VALUE_02:String = "test-2-" + new Date();

  override public function setUp():void {
    super.setUp();
    prepareLocalBeans();
  }

  public function testWidgetEditMode():void {

    // create widget instance rendered to div 'test'
    // rendering is necessary because bindPlugins load stores only when component is rendered
    var esAlxWidgetEditor:EsAnalyticsChartWidgetEditor = ComponentMgr.create(
            new esAnalyticsChartWidgetEditor({renderTo: 'test'})
    ) as EsAnalyticsChartWidgetEditor;

    // the combobox provides the selection of root channels aka Sites
    var combo:ComboBox = esAlxWidgetEditor.find("xtype", "com.coremedia.ui.config.localComboBox")[0];
    var comboFieldLabel:String = combo.fieldLabel;
    var comboStore:Store = combo.getStore();


    // assertion of the ALX widget editor, combobox and fields
    assertNotUndefined(esAlxWidgetEditor);
    assertNotUndefined(combo);
    assertNotUndefined(comboFieldLabel);
    assertEquals(FIELD_LABEL, comboFieldLabel);
    assertEquals(VALUE_FIELD, combo.valueField);

    // assertion of the combobox store filled by the local bean properties
    assertNotUndefined(comboStore);
    assertEquals(2, comboStore.getCount());
    assertEquals(0, comboStore.find("id", STORE_KEY_01));
    assertEquals(0, comboStore.find("value", STORE_VALUE_01));
    assertEquals(1, comboStore.find("id", STORE_KEY_02));
    assertEquals(1, comboStore.find("value", STORE_VALUE_02));

  }

  // the ALX widget editor  combobox  is gathering the information about the selectable root channels aka site pages
  // via rest service URL query, this prepared local bean returns always the same bean with the needed properties
  private function prepareLocalBeans():void {
    // make sure that beanFactory singleton is properly initialized
    BeanFactoryImpl.initBeanFactory();

    // string is the provided service url
    beanFactory.getRemoteBean = function (str:String):Bean {
      return beanFactory.createLocalBean(
              {"rootChannels": [
                // we provide dummy channels with the fields queried by 'bindListPlugin'
                beanFactory.createLocalBean({id: STORE_KEY_01, name: STORE_VALUE_01}),
                beanFactory.createLocalBean({id: STORE_KEY_02, name: STORE_VALUE_02})
              ]}
      );
    }
  }
}
}
