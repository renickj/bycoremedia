package com.coremedia.ecommerce.studio.components.repository {
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewConstants;
import com.coremedia.ecommerce.studio.AbstractCatalogStudioTest;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.beanFactory;

import ext.Viewport;

public class CatalogRepositoryListTest extends AbstractCatalogStudioTest {
  private var category:Category;
  private var store:Store;
  private var product:Product;
  private var viewport:Viewport;

  override public function setUp():void {
    super.setUp();
    category = beanFactory.getRemoteBean("livecontext/category/HeliosSiteId/NO_WS/Fruit") as Category;
    store = beanFactory.getRemoteBean("livecontext/store/HeliosSiteId/NO_WS") as Store;
    product = beanFactory.getRemoteBean("livecontext/product/HeliosSiteId/NO_WS/" + ORANGES_EXTERNAL_ID) as Product;

    viewport = new CatalogRepositoryListTestView();
  }

  override public function tearDown():void {
    super.tearDown();
    viewport.destroy();
  }

  public function testCatalogListType():void {
    var catalogList:CatalogRepositoryList = viewport.get(CollectionViewConstants.LIST_VIEW) as CatalogRepositoryList;
    assertEquals(CatalogHelper.getInstance().getTypeLabel(category), ECommerceStudioPlugin_properties.INSTANCE.Category_label);
    assertEquals(CatalogHelper.getInstance().getTypeLabel(product), ECommerceStudioPlugin_properties.INSTANCE.Product_label);
  }

  public function testCatalogListTypeCls():void {
    var catalogList:CatalogRepositoryList = viewport.get(CollectionViewConstants.LIST_VIEW) as CatalogRepositoryList;
    assertEquals(CatalogHelper.getInstance().getTypeCls(category), ECommerceStudioPlugin_properties.INSTANCE.Category_icon);
    assertEquals(CatalogHelper.getInstance().getTypeCls(product), ECommerceStudioPlugin_properties.INSTANCE.Product_icon);
  }
}
}