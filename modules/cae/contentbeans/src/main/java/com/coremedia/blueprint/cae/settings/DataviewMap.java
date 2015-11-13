package com.coremedia.blueprint.cae.settings;

import com.coremedia.objectserver.dataviews.AssumesIdentity;
import com.coremedia.objectserver.dataviews.DataViewFactory;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Wraps maps created by ContentBeanFactory.createBeanMapFor(Struct struct) in order to
 * use dataviews instead of contentbeans.
 */
final class DataviewMap extends AbstractMap<String, Object> {
  private Map<String, Object> delegateMap;
  private DataViewFactory dataViewFactory;


  // --- construction -----------------------------------------------

  public static Map<String, Object> createDataviewMap(Map<String, Object> delegate, DataViewFactory dataViewFactory) {
    return Collections.unmodifiableMap(new DataviewMap(delegate, dataViewFactory));
  }

  // private: Do not reveal the mutable instance
  private DataviewMap(Map<String, Object> delegateMap, DataViewFactory dataViewFactory) {
    this.delegateMap = delegateMap;
    this.dataViewFactory = dataViewFactory;
  }


  // --- Map --------------------------------------------------------

  @Override
  public Object get(Object key) {
    return dataviewValue(delegateMap.get(key));
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    Set<Entry<String, Object>> result = new HashSet<>();
    for (Entry<String, Object> entry : delegateMap.entrySet()) {
      result.add(new DataviewMapEntry(entry));
    }
    return result;
  }


  // --- internal ---------------------------------------------------

  private Object dataviewValue(Object value) {
    if (value instanceof Map) {
      return createDataviewMap((Map<String, Object>) value, dataViewFactory);
    } else if (value instanceof AssumesIdentity && dataViewFactory!=null) {
      // if the result is a dataview, load a cached implementation
      return dataViewFactory.loadCached(value, null);
    } else {
      return value;
    }
  }

  private class DataviewMapEntry implements Entry<String, Object> {
    private Entry<String, Object> delegateEntry;

    public DataviewMapEntry(Entry<String, Object> delegateEntry) {
      this.delegateEntry = delegateEntry;
    }

    @Override
    public String getKey() {
      return delegateEntry.getKey();
    }

    @Override
    public Object getValue() {
      return dataviewValue(delegateEntry.getValue());
    }

    @Override
    public Object setValue(Object value) {
      throw new UnsupportedOperationException("Immutable");
    }
  }
}
