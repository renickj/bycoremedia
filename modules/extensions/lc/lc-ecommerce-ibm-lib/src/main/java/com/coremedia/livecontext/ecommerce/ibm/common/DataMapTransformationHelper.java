package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.rits.cloning.Cloner;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates convenience methods to transform data maps
 * retrieved by the good old BOD handlers for categories and products
 * to the new leading search handler format.
 */
public class DataMapTransformationHelper {
  private static final Map<String, String> bodKeyMappings = new HashMap<>();

  static {
    bodKeyMappings.put("productType", "catalogEntryTypeCode");
    bodKeyMappings.put("xcatentry_seoSegment", "seo_token_ntk");
    bodKeyMappings.put("xcatgroup_seoSegment", "seo_token_ntk");
    bodKeyMappings.put("parentProductID", "parentCatalogEntryID");
    bodKeyMappings.put("parentCategoryID", "parentCatalogGroupID");
  }

  /**
   * Transforms a map of product data retrieved by the BOD service to the search handler format.
   * @param bodResponseMap The BOD handler based format.
   * @return The search handler based format.
   */
  public static Map<String, Object> transformProductBodMap(Map<String, Object> bodResponseMap) {
    Map<String, Object> mapToUnify = deepCloneMap(bodResponseMap);
    transformKeysStartLowerCase(mapToUnify);
    unifyProductWrapperKeys(mapToUnify);
    return mapToUnify;
  }

  /**
   * Transforms a map of category data retrieved by the BOD service to the search handler format.
   * @param bodResponseMap The BOD handler based format.
   * @return The search handler based format.
   */
  public static Map<String, Object> transformCategoryBodMap(Map<String, Object> bodResponseMap) {
    Map<String, Object> mapToUnify = deepCloneMap(bodResponseMap);
    transformKeysStartLowerCase(mapToUnify);
    unifyCategoryWrapperKeys(mapToUnify);
    return mapToUnify;
  }

  /**
   * Returns a deep copy of a map.
   * @param productsMap A product map.
   * @return A deep copy of the given product map.
   */
  private static Map<String, Object> deepCloneMap(Map<String, Object> productsMap) {
    Cloner cloner = new Cloner();
    return cloner.deepClone(productsMap);
  }

  /**
   * Brings all map keys to lower case.
   * @param map The orignial map which will be modified.
   */
  private static void transformKeysStartLowerCase(Map<String, Object> map) {
    // clone in order to read from the clone and modify the original
    Map<String, Object> mapToRead = deepCloneMap(map);
    for (Map.Entry<String, Object> mapEntry : mapToRead.entrySet()) {
      String currentKey = mapEntry.getKey();
      if (Character.isUpperCase(currentKey.charAt(0))) {
        // replace first character of map key with the one in lower case
        // append the remaining key untouched
        currentKey = Character.toLowerCase(currentKey.charAt(0)) +
                StringUtils.right(currentKey, currentKey.length() - 1);
        // rename, i.e. replace in map under new key
        map.put(currentKey, map.remove(mapEntry.getKey()));
      }
      // need to operate on the original value of the map
      // in order to proceed
      Object value = map.get(currentKey);
      if (value instanceof Collection) {
        transformKeysStartLowerCase((Collection) value);
      } else if (value instanceof Map) {
        transformKeysStartLowerCase((Map) value);
      }
    }
  }

  private static void transformKeysStartLowerCase(Collection<?> collection) {
    for (Object collectionEntry : collection) {
      if (collectionEntry instanceof Map) {
        transformKeysStartLowerCase((Map) collectionEntry);
      } // else it is a primitive value which does not have a key, i.e. nothing to do
    }
  }

  /**
   * Replaces mpa keys to match search handler based format.
   * @param mapList List of catalog entry or catalog group data.
   */
  private static void replaceKeys(List<Map<String, Object>> mapList) {
    for (Map<String, Object> entryMap : mapList) {
      for (Map.Entry<String, String> entry: bodKeyMappings.entrySet()) {
        if (entryMap.containsKey(entry.getKey())) {
          entryMap.put(bodKeyMappings.get(entry.getKey()), entryMap.remove(entry.getKey()));
        }
      }
    }
  }

  /**
   * Unifies the catalog entry data by replacing map keys, replacing attribute keys, replacing sku keys and
   * formatting the catalog group id if required.
   * @param productWrapper The catalog entry wrapper retrieved by the wrapper service.
   */
  private static void unifyProductWrapperKeys(Map<String, Object> productWrapper) {
    //noinspection unchecked
    List<Map<String, Object>> catalogEntryView = getCatalogEntryView(productWrapper);
    replaceKeys(catalogEntryView);
    replaceProductAttributeKeys(catalogEntryView);
    replaceSkus(catalogEntryView);
    formatParentCatGroupId(catalogEntryView);
  }

  /**
   * Unifies catalog group data by replacing map keys and formatting the catalog group id if required.
   * @param categoryWrapper The catalog group wrapper retrieved by the wrapper service.
   */
  private static void unifyCategoryWrapperKeys(Map<String, Object> categoryWrapper) {
    //noinspection unchecked
    List<Map<String, Object>> catalogGroupView = getCatalogGroupView(categoryWrapper);
    replaceKeys(catalogGroupView);
    formatParentCatGroupId(catalogGroupView);
  }

  /**
   * Formats the value of <code>parentCatalogGroupID</code> if required. Removes the catalog ids from a list of
   * tuples of catalog_id and category_id, e.g. <code>10051_10031 -> 10031</code>.
   * Only values will be accepted in the result list where the catalog id matches the current catalog id from
   * the current store context.
   *
   * @param mapList The list containing the parent category ids within the current catalog
   */
  public static void formatParentCatGroupId(List<Map<String, Object>> mapList) {
    for (Map<String, Object> listEntry : mapList) {
      // parent catalog group ids
      // when retrieved by the search servers, it is a list and the entries have the format catalogId_category_id
      StoreContext storeContext = StoreContextHelper.getCurrentContext();
      String currentCatalogId = null;
      if (storeContext != null) {
        // try to get current catalogId from StoreContext to filter parentCatalogGroupID correctly
        currentCatalogId = storeContext.getCatalogId();
      }
      Object origParentCategoryIds = DataMapHelper.getValueForPath(listEntry, "parentCatalogGroupID");
      List<String> parentCategoryIdList = new ArrayList<>();
      if (origParentCategoryIds instanceof List) {
        // create new list copy to avoid  ConcurrentModificationException
        //noinspection unchecked
        for (String origParentCatString : (List<String>) origParentCategoryIds) {
          String parentCategoryId = filterByCatalogId(currentCatalogId, origParentCatString);
          if (parentCategoryId != null) {
            parentCategoryIdList.add(parentCategoryId);
          }
        }
        listEntry.put("parentCatalogGroupID", parentCategoryIdList);
      } else if (origParentCategoryIds instanceof String) {
        String parent = filterByCatalogId(currentCatalogId, (String) origParentCategoryIds);
        if (parent != null) {
          listEntry.put("parentCatalogGroupID", Collections.singletonList(parent));
        }
      }
    }
  }

  private static String filterByCatalogId(String catalogId, String catalogIdAndCategoryId) {
    if (catalogIdAndCategoryId != null && catalogIdAndCategoryId.matches(".+_.+")) {
      String[] catalogAndCategoryIdSplit = catalogIdAndCategoryId.split("_");
      if (catalogId == null || catalogAndCategoryIdSplit.length > 0 && catalogId.equals(catalogAndCategoryIdSplit[0])) {
        return catalogAndCategoryIdSplit[1];
      } else {
        return null;
      }
    }
    return catalogIdAndCategoryId;
  }

  /**
   * Replaces the key <code>sKUUniqueID</code> with <code>uniqueID</code> (<code>sKUUniqueID -> uniqueID</code>)
   * @param mapList The list containing the catalog entry or catalog group data.
   */
  private static void replaceSkus(List<Map<String, Object>> mapList) {
    for (Map<String, Object> listEntry : mapList) {
      List<Map<String, Object>> sKUs = (List<Map<String, Object>>) DataMapHelper.getValueForKey(listEntry, "sKUs", List.class);
      if (sKUs != null) {
        for (Map<String, Object> sKU : sKUs) {
          if (sKU.containsKey("sKUUniqueID")) {
            sKU.put("uniqueID", sKU.remove("sKUUniqueID"));
          }
        }
      }
    }
  }

  /**
   * Replaces all <code>values</code> keys in <code>attributes.values</code> (<code>attributes.values.values -> attributes.values.value</code>).
   * @param mapList The map containing the catalog entry data.
   */
  private static void replaceProductAttributeKeys(List<Map<String, Object>> mapList) {
    for (Map<String, Object> listEntry : mapList) {
      List<Map<String, Object>> attributes = (List<Map<String, Object>>) DataMapHelper.getValueForKey(listEntry, "attributes", List.class);
      if (attributes != null) {
        for (Map<String, Object> attribute : attributes) {
          //rename inner values only
          List<Map<String, Object>> values = DataMapHelper.getValueForKey(attribute, "values", List.class);
          if (values != null && values.size() > 0) {
            for (Map<String, Object> value : values) {
              if (value.containsKey("values")) {
                value.put("value", value.remove("values"));
              }
            }
          }
        }
      }
    }
  }

  /**
   * Returns the catalog entry view section of a given map.
   *
   * @param productsMap The product map retrieved by the commerce server.
   * @return The sub map containing the catalog entry view section or null if not available.
   */
  private static List getCatalogEntryView(Map<String, Object> productsMap) {
    return DataMapHelper.getValueForKey(productsMap, "catalogEntryView", List.class);
  }

  /**
   * Returns the catalog group view section of a given map.
   *
   * @param categoriesWrapper The categories map retrieved by the commerce server.
   * @return The sub map containing the catalog group view section or null if not available.
   */
  private static List getCatalogGroupView(Map<String, Object> categoriesWrapper) {
    return DataMapHelper.getValueForKey(categoriesWrapper, "catalogGroupView", List.class);
  }

}
