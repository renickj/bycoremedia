package com.coremedia.ecommerce.studio.model {
import com.coremedia.ecommerce.studio.AbstractCatalogTest;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.beanFactory;

public class ProductTest extends AbstractCatalogModelTest {

  private var product:Product;
  private var leafCategory:Category;

  override public function setUp():void {
    super.setUp();
    product = beanFactory.getRemoteBean("livecontext/product/HeliosSiteId/NO_WS/" + ORANGES_EXTERNAL_ID) as Product;
    leafCategory = beanFactory.getRemoteBean("livecontext/category/HeliosSiteId/NO_WS/Fruit") as Category;
  }

  public function testProduct():void {
    (product as RemoteBean).load(addAsync(function ():void {
      assertEquals(ORANGES_NAME, product.getName());
      assertEquals(ORANGES_EXTERNAL_ID, product.getExternalId());
      assertTrue(product.getId().indexOf(ORANGES_ID) == 0);
      assertEquals(leafCategory, product.getCategory());
    }, 500));
  }
}
}