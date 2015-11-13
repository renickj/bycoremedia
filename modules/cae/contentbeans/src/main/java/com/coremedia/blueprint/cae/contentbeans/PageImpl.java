package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.multisite.SitesService;

import java.util.List;

public class PageImpl extends AbstractPageImpl implements Page {

  /**
   * Do not call this constructor yourself, this is only for {@link com.coremedia.objectserver.dataviews.DataView} usage,
   * which will afterwards call {@link #assumeIdentity(Object)} with the originating uncached bean instance.
   */
  public PageImpl() {
    super();
  }

  public PageImpl(Navigation navigation, Object content, boolean developerMode, SitesService sitesService, Cache cache) {
    super(navigation, content, developerMode, sitesService,cache);
  }

  @Override
  public Blob getFavicon() {
    CMNavigation rootNavi = getNavigation().getRootNavigation();
    return rootNavi.getFavicon();
  }

  /**
   * @return the CSS contents for this page.
   */
  @Override
  public List<?> getCss() {
    return getCache().get(new CodeResourcesCacheKey(getContext(), CMNavigationBase.CSS, isDeveloperMode())).getLinkTargetList();
  }

  /**
   * @return the JS contents for this page.
   */
  @Override
  public List<?> getJavaScript() {
    return getCache().get(new CodeResourcesCacheKey(getContext(), CMNavigationBase.JAVA_SCRIPT, isDeveloperMode())).getLinkTargetList();
  }

}
