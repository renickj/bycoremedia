package com.coremedia.ecommerce.studio {

import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryListTest;
import com.coremedia.ecommerce.studio.components.tree.CatalogTreeModelTest;
import com.coremedia.ecommerce.studio.forms.CommerceCatalogObjectsSelectFormTest;

import flexunit.framework.TestSuite;

public class TestSuite {
  public static function suite():flexunit.framework.TestSuite {
    var suite:flexunit.framework.TestSuite = new flexunit.framework.TestSuite();
    suite.addTestSuite(CatalogTreeModelTest);
    suite.addTestSuite(CatalogRepositoryListTest);
    suite.addTestSuite(CommerceCatalogObjectsSelectFormTest);
    return suite;
  }
}
}
