package com.coremedia.ecommerce.studio.model {
import com.coremedia.ecommerce.studio.AbstractCatalogTest;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.beanFactory;

public class CatalogTest extends AbstractCatalogModelTest {

  private var catalog:Catalog;

  override public function setUp():void {
    super.setUp();
    catalog = beanFactory.getRemoteBean("livecontext/catalog/HeliosSiteId/NO_WS") as Catalog;
  }

  public function testCatalog():void {
    (catalog as RemoteBean).load(addAsync(function ():void {
      assertEquals(3, catalog.getTopCategories().length);
    }, 500));
  }
}
}