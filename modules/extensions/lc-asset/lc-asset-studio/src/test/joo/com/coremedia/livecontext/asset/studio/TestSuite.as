package com.coremedia.livecontext.asset.studio {

import flexunit.framework.TestSuite;

public class TestSuite {
  public static function suite():flexunit.framework.TestSuite {
    var suite:flexunit.framework.TestSuite = new flexunit.framework.TestSuite();

    suite.addTestSuite(SearchProductImagesTest);
//    suite.addTestSuite(InheritProductLinksTest);
    //execute livecontextAssetPlugin on current view port
    new LivecontextAssetStudioPlugin().init({});

    return suite;
  }
}
}
