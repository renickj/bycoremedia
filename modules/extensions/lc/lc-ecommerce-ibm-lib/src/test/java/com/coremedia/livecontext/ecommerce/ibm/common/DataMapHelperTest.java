package com.coremedia.livecontext.ecommerce.ibm.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DataMapHelperTest {

  @Test
  public void testSimpleValue() throws Exception {
    Map m = new HashMap();
    m.put("testEntry", "testValue");
    assertEquals("testValue", DataMapHelper.getValueForKey(m, "testEntry"));
  }

  @Test
  public void testPathInnerMap() throws Exception {
    Map outerMap = new HashMap(), innerMap = new HashMap();
    innerMap.put("targetValue", "42");
    outerMap.put("nestedMap", innerMap);

    assertEquals("42", DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "nestedMap.targetValue"), "42");
  }

  @Test
  public void testPathInnerList() throws Exception {
    Map outerMap = new HashMap();
    List innerList = new ArrayList();

    innerList.add("zero");
    innerList.add("one");
    innerList.add("two");
    outerMap.put("innerList", innerList);

    assertEquals("zero", DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "innerList[0]"));
    assertEquals("one", DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "innerList[1]"));
    assertEquals("two", DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "innerList[2]"));
  }

  @Test
  public void testPathWithComplexInnerList() throws Exception {
    Map outerMap = new HashMap(), mapInList = new HashMap();
    List innerList = new ArrayList();

    mapInList.put("key1", "value1");
    mapInList.put("key2", "value2");
    innerList.add(Collections.emptyMap());
    innerList.add(mapInList);
    outerMap.put("aList", innerList);

    assertEquals(innerList, DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "aList"));
    assertEquals(Collections.emptyMap(), DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "aList[0]"));
    assertEquals(mapInList, DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "aList[1]"));
    assertEquals("value2", DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "aList[1].key2"));
  }

  @Test
  public void testNotExistingArray() throws Exception {
    assertNull(DataMapHelper.getValueForKey(new HashMap<String, Object>(), "MyList[0]"));
  }

  @Test
  public void testArrayIndexOutOfBounds() throws Exception {
    Map outerMap = new HashMap(), mapInList = new HashMap();
    List innerList = new ArrayList();

    mapInList.put("key1", "value1");
    mapInList.put("key2", "value2");
    innerList.add(Collections.emptyMap());
    innerList.add(mapInList);
    outerMap.put("aList", innerList);

    assertEquals("value2", DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "aList[1].key2"));
    assertNull(DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "aList[2]"));
    assertNull(DataMapHelper.getValueForPath((Map<String, Object>) outerMap, (String) "aList[2].key2"));

  }
}