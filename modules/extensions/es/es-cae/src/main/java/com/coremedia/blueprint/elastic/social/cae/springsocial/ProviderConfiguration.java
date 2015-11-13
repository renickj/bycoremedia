package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.core.api.tenant.TenantService;
import com.coremedia.objectserver.beans.ContentBeanIdScheme;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Named
public class ProviderConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(ProviderConfiguration.class);
  private static final String FACEBOOK_CLIENT_ID = "facebook.clientId";
  private static final String FACEBOOK_CLIENT_SECRET = "facebook.clientSecret";
  private static final String TWITTER_CONSUMER_KEY = "twitter.consumerKey";
  private static final String TWITTER_CONSUMER_SECRET = "twitter.consumerSecret";
  private static final String TENANT = "tenant";

  @Inject
  private SitesService sitesService;
  @Inject
  private TenantService tenantService;
  @Inject
  private SettingsService settingsService;

  public String getCurrentFacebookClientId(String tenant) {
    return getConnectionSetting(FACEBOOK_CLIENT_ID, tenant);
  }

  public String getCurrentFacebookClientSecret(String tenant) {
    return getConnectionSetting(FACEBOOK_CLIENT_SECRET, tenant);
  }

  public String getCurrentTwitterConsumerKey(String tenant) {
    return getConnectionSetting(TWITTER_CONSUMER_KEY, tenant);
  }

  public String getCurrentTwitterConsumerSecret(String tenant) {
    return getConnectionSetting(TWITTER_CONSUMER_SECRET, tenant);
  }

  private String getConnectionSetting(String keySuffix, String tenant) {
    String currentTenant = tenant;
    if (StringUtils.isBlank(tenant)) {
      currentTenant = tenantService.getCurrent();
    }
    Site site = getCurrentNavigation();
    if(null != site) {
      Map elasticSocialSettings = settingsService.settingAsMap(ElasticSocialPlugin.SETTINGS_STRUCT, String.class, Object.class, site);
      if (currentTenant.equals(elasticSocialSettings.get(TENANT)) && elasticSocialSettings.containsKey(keySuffix)) {
        return (String) elasticSocialSettings.get(keySuffix);
      }
      LOG.info("no provider config found for site '{}', checking fallback configuration for tenant '{}' and provider '{}'", site, currentTenant, keySuffix);
    } else {
      LOG.warn("site not available for request {}", getHttpServletRequest().getRequestURI());
    }

    return null;
  }

  @VisibleForTesting
  protected Site getCurrentNavigation() {
    ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    Object sessionAttribute = sra.getAttribute(SiteHelper.SITE_KEY, ServletRequestAttributes.SCOPE_GLOBAL_SESSION);
    if(sessionAttribute instanceof String) {
      return sitesService.getSite((String) sessionAttribute);
    }
    return null;
  }

  private HttpServletRequest getHttpServletRequest() {
    ServletRequestAttributes sra1 = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return sra1.getRequest();
  }

}
