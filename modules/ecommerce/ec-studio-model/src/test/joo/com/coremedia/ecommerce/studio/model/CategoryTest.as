package com.coremedia.ecommerce.studio.model {
import com.coremedia.ecommerce.studio.AbstractCatalogTest;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.beanFactory;

public class CategoryTest extends AbstractCatalogModelTest {

  private var topCategory:Category;
  private var leafCategory:Category;

  override public function setUp():void {
    super.setUp();
    topCategory = beanFactory.getRemoteBean("livecontext/category/HeliosSiteId/NO_WS/Grocery") as Category;
    leafCategory = beanFactory.getRemoteBean("livecontext/category/HeliosSiteId/NO_WS/Fruit") as Category;
  }

  public function testTopCategory():void {
    (topCategory as RemoteBean).load(addAsync(function ():void {
      assertEquals("Grocery", topCategory.getName());
      assertTrue(topCategory.getId().indexOf("ibm:///catalog/category/Grocery") == 0);
      assertEquals(2, topCategory.getChildren().length);
      assertNull(topCategory.getParent());
    }, 500));
  }

  public function testLeafCategory():void {
    (leafCategory as RemoteBean).load(addAsync(function ():void {
      assertEquals("Fruit", leafCategory.getName());
      assertTrue(leafCategory.getId().indexOf("ibm:///catalog/category/Fruit") == 0);
      assertEquals(3, leafCategory.getChildren().length);
      assertNotNull(leafCategory.getParent());
    }, 500));
  }
}
}