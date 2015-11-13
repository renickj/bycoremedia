package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataMapTransformationHelperTest {

  @Test
  public void testFormatParentCatGroupIdWithStoreContext() throws Exception {

    List<Map<String, Object>> outerList = new ArrayList<>();
    HashMap<String, Object> map = new HashMap<>();
    List<String > innerList = new ArrayList<>();
    innerList.add("10051_10031");
    innerList.add("10051_10051");
    innerList.add("10061_10032");
    map.put("parentCatalogGroupID", innerList);
    outerList.add(map);

    CommerceConnection connection = new BaseCommerceConnection();
    connection.setStoreContext(StoreContextHelper.createContext("configId", "storeId", "storeName", "10051", "en", "USD"));
    Commerce.setCurrentConnection(connection);

    DataMapTransformationHelper.formatParentCatGroupId(outerList);

    List<String> transformedParentCategoryIds = (List<String>) DataMapHelper.getValueForPath(map, "parentCatalogGroupID");
    assertNotNull(transformedParentCategoryIds);
    assertTrue(transformedParentCategoryIds.size() == 2);
    assertEquals("10031", transformedParentCategoryIds.get(0));
    assertEquals("10051", transformedParentCategoryIds.get(1));
  }

  @Test
  public void testFormatParentCatGroupIdWithoutStoreContext() throws Exception {

    List<Map<String, Object>> outerList = new ArrayList<>();
    HashMap<String, Object> map = new HashMap<>();
    List<String > innerList = new ArrayList<>();
    innerList.add("10051_10031");
    innerList.add("10051_10051");
    innerList.add("10061_10032");
    map.put("parentCatalogGroupID", innerList);
    outerList.add(map);

    Commerce.setCurrentConnection(new BaseCommerceConnection());


    DataMapTransformationHelper.formatParentCatGroupId(outerList);

    List<String> transformedParentCategoryIds = (List<String>) DataMapHelper.getValueForPath(map, "parentCatalogGroupID");
    assertNotNull(transformedParentCategoryIds);
    assertTrue(transformedParentCategoryIds.size() == 3);
    assertEquals("10031", transformedParentCategoryIds.get(0));
    assertEquals("10051", transformedParentCategoryIds.get(1));
    assertEquals("10032", transformedParentCategoryIds.get(2));
  }

}