package com.coremedia.ecommerce.studio.components.tree {
import com.coremedia.cms.editor.sdk.EditorContextImpl;
import com.coremedia.ecommerce.studio.AbstractCatalogStudioTest;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeModel;
import com.coremedia.ui.models.NodeChildren;
import com.coremedia.ui.models.TreeModel;

public class CatalogTreeModelTest extends AbstractCatalogStudioTest {
  private static const STORE_CONTEXT_ID:String = "HeliosSiteId/NO_WS";
  private static const STORE_ID:String = "livecontext/store/" + STORE_CONTEXT_ID;
  private static const CATALOG_ID:String = "livecontext/catalog/" + STORE_CONTEXT_ID;
  private static const MARKETING_ID:String = "livecontext/marketing/" + STORE_CONTEXT_ID;
  private static const TOP_CATEGORY_ID:String = "livecontext/category/" + STORE_CONTEXT_ID + "/Grocery";
  private static const LEAF_CATEGORY_ID:String = "livecontext/category/" + STORE_CONTEXT_ID + "/Fruit";
  private static const STORE_NAME:String = "PerfectChefESite";
  private static const TOP_CATEGORY_EXTERNAL_ID:String = "Grocery";
  private static const LEAF_CATEGORY_EXTERNAL_ID:String = "Grocery Fruit";

  private var catalogTreeModel:TreeModel;

  override public function setUp():void {
    super.setUp();

    EditorContextImpl.initEditorContext();

    //noinspection BadExpressionStatementJS
    catalogTreeModel = new CatalogTreeModel();
  }

  override public function tearDown():void {
    super.tearDown();
  }

  public function testGetStoreText():void {
    waitUntil("wait for store text",
      function():Boolean {
        return catalogTreeModel.getText(STORE_ID);
      },
      function():void {
        assertEquals(catalogTreeModel.getText(STORE_ID), STORE_NAME);
      }
    );
  }

  public function testGetTopCategoryText():void {
    waitUntil("wait for the top category loaded",
      function():Boolean {
        return catalogTreeModel.getText(TOP_CATEGORY_ID);
      },
      function():void {
        assertEquals(catalogTreeModel.getText(TOP_CATEGORY_ID), TOP_CATEGORY_EXTERNAL_ID);
      }
    );
  }

  public function testGetLeafCategoryText():void {
    waitUntil("wait for the leaf category loaded",
      function():Boolean {
        return catalogTreeModel.getText(LEAF_CATEGORY_ID);
      },
      function():void {
        assertEquals(catalogTreeModel.getText(LEAF_CATEGORY_ID), LEAF_CATEGORY_EXTERNAL_ID);
      }
    );
  }

  public function testGetTopCategoryIdPath():void {
    waitUntil("wait for the top categories loaded",
      function():Boolean {
        var idPaths:Array = catalogTreeModel.getIdPath(TOP_CATEGORY_ID) as Array;
        return idPaths && idPaths.length === 3;
      },
      function():void {
        var idPaths:Array = catalogTreeModel.getIdPath(TOP_CATEGORY_ID) as Array;
        assertEquals(idPaths[0], STORE_ID);
        assertEquals(idPaths[1], CATALOG_ID);
        assertEquals(idPaths[2], TOP_CATEGORY_ID);
      }
    );
  }

  public function testGetLeafCategoryIdPath():void {
    waitUntil("wait for leaf category id path",
      function():Boolean {
        return catalogTreeModel.getIdPath(LEAF_CATEGORY_ID);
      },
      function():void {
        var idPaths:Array = catalogTreeModel.getIdPath(LEAF_CATEGORY_ID) as Array;
        assertEquals(idPaths.length, 4);
        assertEquals(idPaths[0], STORE_ID);
        assertEquals(idPaths[1], CATALOG_ID);
        assertEquals(idPaths[2], TOP_CATEGORY_ID);
        assertEquals(idPaths[3], LEAF_CATEGORY_ID);
      }
    );
  }

  public function testGetTopCategoryChildren():void {
    waitUntil("wait for top category children",
      function():Boolean {
        return catalogTreeModel.getChildren(TOP_CATEGORY_ID);
      },
      function():void {
        var nodeChildren:NodeChildren = catalogTreeModel.getChildren(TOP_CATEGORY_ID);
        assertEquals(nodeChildren.getChildIds().length, 2);
      }

    );

  }

  public function testGetLeafCategoryChildren():void {
    waitUntil("wait for leaf category children",
      function():Boolean {
        return catalogTreeModel.getChildren(LEAF_CATEGORY_ID);
      },
      function():void {
        var nodeChildren:NodeChildren = catalogTreeModel.getChildren(LEAF_CATEGORY_ID);
        assertEquals(nodeChildren.getChildIds().length, 0);
      }
    );
  }

  public function testGetStoreChildren():void {
    waitUntil("wait for store children",
      function():Boolean {
        return catalogTreeModel.getChildren(STORE_ID);
      },
      function():void {
        var topLevelIds:Array = catalogTreeModel.getChildren(STORE_ID).getChildIds();
        assertEquals(topLevelIds.length, 2);
        assertEquals(topLevelIds[0], MARKETING_ID);
        assertEquals(topLevelIds[1], CATALOG_ID);
      }
    );
  }

  public function testGetMarketingSpotsText():void {
    waitUntil("wait for tree to be build",
      function():Boolean {
        return catalogTreeModel.getText(MARKETING_ID);
      },
      function():void {
        assertEquals(catalogTreeModel.getText(MARKETING_ID), ECommerceStudioPlugin_properties.INSTANCE.StoreTree_marketing_root);
      }
    );
  }

  public function testGetRootId():void {
    waitUntil("wait for root id to be loaded",
      function ():Boolean {
        return catalogTreeModel.getRootId() === STORE_ID;
      },
      function ():void {
        assertEquals(catalogTreeModel.getRootId(), STORE_ID);
      }
    );
  }

  public function testGetStoreIdPath():void {
    waitUntil("wait for store id path",
      function():Boolean {
        return catalogTreeModel.getIdPath(STORE_ID);
      },
      function():void {
        var idPaths:Array = catalogTreeModel.getIdPath(STORE_ID);
        assertEquals(idPaths.length, 1);
        assertEquals(idPaths[0], STORE_ID);
      }
    );
  }
}
}