package com.coremedia.livecontext.ecommerce.ibm.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class DataMapHelper {

  @Nullable
  @SuppressWarnings("unchecked")
  public static Object getValueForPath(@Nonnull Map<String, Object> map, @Nonnull String path) {
    // TODO verify key each iteration
    Map<String, Object> myMap = map;
    Object value = null;
    if (myMap != null) {
    String[] keys = path.split("\\.");
    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      if (i < keys.length - 1 && !key.matches(".+\\[\\d+\\]")) {
        value = getValueForKey(myMap, key);
        if (value instanceof Map) {
          myMap = (Map<String, Object>) value;
        } else {
          return null;
        }
      } else if (key.matches(".+\\[\\d+\\]")) { // a list entry is expected
        String keyWithoutIndex = key.substring(0, key.lastIndexOf("["));
        int index = Integer.parseInt(key.substring(key.lastIndexOf("[") + 1, key.length() - 1));
        List tmpList = getValueForKey(myMap, keyWithoutIndex, List.class);
        if (tmpList != null && tmpList.size() > index) {
          value = tmpList.get(index);
          if (value instanceof Map) {
            myMap = (Map<String, Object>) value;
          }
        } else {
          return null;
        }
      } else {
        // for the last key ...
        value = myMap.get(key);
      }
    }
    }
    return value;
  }

  @Nullable
  public static <T> T getValueForPath(@Nonnull Map<String, Object> map, @Nonnull String path, @Nonnull Class<T> type) {
    Object valueForPath = getValueForPath(map, path);
    return valueForPath == null ? null : type.cast(valueForPath);
  }

  @Nullable
  @SuppressWarnings("unchecked")
  public static Object getValueForKey(@Nonnull Map<String, Object> map, @Nonnull String key) {
    Object value = null;
    if (map != null) {
      value = map.get(key);
    }
    if (value instanceof List) {
      List<Map<String, Object>> list = (List<Map<String, Object>>) value;
      if (!list.isEmpty()) {
        return list.get(0);
      }
    }
    return value;
  }

  @Nullable
  public static <T> T getValueForKey(@Nonnull Map<String, Object> map, @Nonnull String key, @Nonnull Class<T> type) {
    Object valueForKey = getValueForPath(map, key); // TODO for key!
    return valueForKey == null ? null : type.cast(valueForKey);
  }

}
