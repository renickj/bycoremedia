package com.coremedia.livecontext.assets;

import com.coremedia.livecontext.asset.ProductAssetsHandler;
import com.coremedia.livecontext.ecommerce.catalog.AxisFilter;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProductAssetsHandlerTest {

  @Test
  public void testParseAttributesFromCSL1() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a=1,b=2,c=3");
    assertNotNull(filters);
    assertTrue(filters.size() == 3);
    assertEquals(((AxisFilter)filters.get(1)).getName(), "b");
    assertEquals(((AxisFilter)filters.get(1)).getValue(), "2");
  }

  @Test
  public void testParseAttributesFromCSL2() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a=1");
    assertNotNull(filters);
    assertTrue(filters.size() == 1);
    assertEquals(((AxisFilter) filters.get(0)).getName(), "a");
    assertEquals(((AxisFilter) filters.get(0)).getValue(), "1");
  }

  @Test
  public void testParseAttributesFromCSL3() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a=");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

  @Test
  public void testParseAttributesFromCSL4() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a=,b=,c=");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

  @Test
  public void testParseAttributesFromCSL5() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

  @Test
  public void testParseAttributesFromCSL6() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromCSL("a");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

  @Test
  public void testParseAttributesFromSSL1() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;1;b;2;c;3");
    assertNotNull(filters);
    assertTrue(filters.size() == 3);
    assertEquals(((AxisFilter) filters.get(1)).getName(), "b");
    assertEquals(((AxisFilter) filters.get(1)).getValue(), "2");
  }

  @Test
  public void testParseAttributesFromSSL2() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;1");
    assertNotNull(filters);
    assertTrue(filters.size() == 1);
    assertEquals(((AxisFilter) filters.get(0)).getName(), "a");
    assertEquals(((AxisFilter) filters.get(0)).getValue(), "1");
  }

  @Test
  public void testParseAttributesFromSSL3() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

  @Test
  public void testParseAttributesFromSSL4() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;;b;;c;");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

  @Test
  public void testParseAttributesFromSSL5() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

  @Test
  public void testParseAttributesFromSSL6() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

  @Test
  public void testParseAttributesFromSSL7() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;1;b;2;c;3;");
    assertNotNull(filters);
    assertTrue(filters.size() == 3);
    assertEquals(((AxisFilter)filters.get(2)).getName(), "c");
    assertEquals(((AxisFilter)filters.get(2)).getValue(), "3");
  }

  @Test
  public void testParseAttributesFromSSL8() {
    List<VariantFilter> filters = ProductAssetsHandler.parseAttributesFromSSL("a;;b;;c;;");
    assertNotNull(filters);
    assertTrue(filters.size() == 0);
  }

}
