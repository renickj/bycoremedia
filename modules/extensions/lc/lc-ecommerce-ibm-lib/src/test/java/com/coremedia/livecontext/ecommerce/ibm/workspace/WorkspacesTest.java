package com.coremedia.livecontext.ecommerce.ibm.workspace;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WorkspacesTest extends AbstractServiceTest {

  public static final String BEAN_NAME_WORKSPACE_SERVICE = "workspaceService";
  public static final String BEAN_NAME_CATALOG_SERVICE = "catalogServiceBod";

  WorkspaceService workspaceService;
  CatalogService catalogService;

  @Before
  public void setup() {
    super.setup();
    workspaceService = infrastructure.getBean(BEAN_NAME_WORKSPACE_SERVICE, WorkspaceServiceImpl.class);
    catalogService = infrastructure.getBean(BEAN_NAME_CATALOG_SERVICE, CatalogServiceImpl.class);
  }

  @Betamax(tape = "wt_testFindAllWorkspaces", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindAllWorkspaces() throws Exception {
    Workspace workspace = findWorkspace("Anniversary");
    assertTrue("segment id has wrong format", workspace.getId().startsWith("ibm:///catalog/workspace/"));
  }

  @Test
  public void testFindTestContentInWorkspace() {

    if (!"*".equals(System.getProperties().get("betamax.ignoreHosts"))) return;

    Workspace workspace = findWorkspace("Anniversary");

    StoreContext storeContext = testConfig.getStoreContext();
    StoreContextHelper.setWorkspaceId(storeContext, workspace.getExternalTechId());
    StoreContextHelper.setCurrentContext(storeContext);

    Category category0 = catalogService.findCategoryById(CommerceIdHelper.formatCategoryId("PC_ForTheCook"));
    assertNotNull("category \"PC_ForTheCook\" not found", category0);
    List<Category> subCategories = catalogService.findSubCategories(category0);
    Category category1 = null;
    for (Category c : subCategories) {
      if ("PC_Anniversary".equals(c.getExternalId())) {
        category1 = c;
        break;
      }
    }
    assertNotNull("category \"PC_Anniversary\" not found", category1);
    List<Product> products = catalogService.findProductsByCategory(category1);
    assertNotNull(products);
    assertTrue(products.size() > 0);
    Product product = null;
    for (Product p : products) {
      if ("PC_COOKING_HAT".equals(p.getExternalId())) {
        product = p;
      }
    }
    assertNotNull("product \"PC_COOKING_HAT\" not found", product);
  }

  private Workspace findWorkspace(String name) {

    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(null);
    UserContextHelper.setCurrentContext(userContext);

    List<Workspace> workspaces = workspaceService.findAllWorkspaces();
    assertNotNull(workspaces);
    assertTrue(workspaces.size() > 0);
    Workspace workspace = null;
    for (Workspace w : workspaces) {
      if (w.getName().startsWith(name)) {
        workspace = w;
      }
    }
    assertNotNull("workspace \"" + name + "...\" not found", workspace);
    return workspace;
  }

}
