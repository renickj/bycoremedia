package com.coremedia.blueprint.cae.settings;

import com.coremedia.blueprint.base.settings.SettingsFinder;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.dataviews.DataViewHelper;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CMLinkableBeanSettingsFinder implements SettingsFinder {
  private ContentBeanFactory contentBeanFactory;
  private DataViewFactory dataViewFactory;


  // --- construct and configure ------------------------------------

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }


  // --- SettingsFinder ---------------------------------------------

  @Override
  public Object setting(Object bean, String name, SettingsService settingsService) {
    if (!(bean instanceof CMLinkable)) {
      return null;
    }
    CMLinkable linkable = (CMLinkable) bean;

    // Delegate down to UAPI level
    Object setting = settingsService.setting(name, Object.class, linkable.getContent());

    // Back to beans
    Object contentBeanedResult = toContentBeans(setting);
    // If the source bean is a dataview, return a dataviewed result.
    return DataViewHelper.isDataView(bean) ? toDataViews(contentBeanedResult) : contentBeanedResult;
  }


  // --- internal ---------------------------------------------------

  /**
   * Cast any Content results back into the ContentBean domain.
   */
  private Object toContentBeans(Object value) {
    if (value instanceof Content) {
      return contentBeanFactory.createBeanFor((Content) value);
    }
    if (value instanceof Struct) {
      return contentBeanFactory.createBeanMapFor((Struct)value);
    }
    if (value instanceof List) {
      List list = (List)value;
      ArrayList<Object> result = new ArrayList<>(list.size());
      for (Object item : list) {
        result.add(toContentBeans(item));
      }
      return result;
    }
    return value;
  }

  /**
   * Cast any contentbeaned results into dataviews.
   *
   * @param contentBeanValue result of toContentBeans
   * @return result with any content values as dataviews
   */
  private Object toDataViews(Object contentBeanValue) {
    if (contentBeanValue instanceof ContentBean) {
      return dataViewFactory.loadCached(contentBeanValue, null);
    }
    if (contentBeanValue instanceof Map) {
      // avoid to return an unordered HashMap (see CMS-905) by calling:
      // return dataViewFactory.loadAllCached((Map)contentBeanValue, null);
      //
      // instead we create our own LinkedHashMap here
      Map<Object,Object> result = new LinkedHashMap<>();
      Map<Object,Object> m = (Map)contentBeanValue;
      for(Map.Entry<Object,Object> entry : m.entrySet()) {
        Object key = entry.getKey();
        Object value = entry.getValue();
        result.put(dataViewFactory.load(key, null, true),
                   dataViewFactory.load(value, null, true));
      }
      return result;
    }
    if (contentBeanValue instanceof List) {
      return dataViewFactory.loadAllCached((List)contentBeanValue, null);
    }
    return contentBeanValue;
  }
}
