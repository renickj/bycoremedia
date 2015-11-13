package com.coremedia.blueprint.elastic.base;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * Helper bean to compute the configured tenants (from root navigation settings)
 */
@Named
public class TenantHelper {

  public static final String SETTINGS_STRUCT = "elasticSocial";

  @Inject
  private SitesService sitesService;

  @Inject
  private SettingsService settingsService;

  public Collection<String> readTenantsFromContent() {
    final Collection<String> tenants = new HashSet<>();
    for (Site site : sitesService.getSites()) {
      final Map<String, Object> settingsMap = getSettingsAsMap(site.getSiteRootDocument());
      final String tenant = join(settingsMap.get("tenant"));
      if (!StringUtils.isEmpty(tenant)) {
        tenants.add(tenant);
      }
    }
    return Collections.unmodifiableCollection(tenants);
  }

  private Map<String, Object> getSettingsAsMap(final Content rootNavigation) {
    return settingsService.settingAsMap(SETTINGS_STRUCT, String.class, Object.class, rootNavigation);
  }

}
