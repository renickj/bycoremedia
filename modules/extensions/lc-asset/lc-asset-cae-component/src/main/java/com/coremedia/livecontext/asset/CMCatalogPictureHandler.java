package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.blueprint.cae.handlers.TransformedBlobHandler;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.objectserver.web.HandlerHelper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;

@RequestMapping
public class CMCatalogPictureHandler extends HandlerBase {

  private static final Logger LOG = LoggerFactory.getLogger(CMCatalogPictureHandler.class);

  public static final String FORMAT_KEY_THUMBNAIL = "thumbnail";
  public static final String FORMAT_KEY_FULL = "full";

  private AssetService assetService;
  private LiveContextSiteResolver siteResolver;
  private ContentBeanFactory contentBeanFactory;
  private AssetManagementLicenseInspector licenseInspector;
  private SettingsService settingsService;
  private Map<String, String> pictureFormats;

  private static final String URI_PREFIX = AssetService.URI_PREFIX;

  public static final String STORE_ID = "storeId";
  public static final String LOCALE = "locale";
  public static final String PART_NUMBER = "partNumber";
  private static final String FORMAT_NAME = "formatName";

  /**
   * URI Pattern for transformed blobs.
   * e.g. /catalogimage/10202/en_US/full/PC_SHIRT.jpg
   */
  public static final String URI_PATTERN =
          "/" + URI_PREFIX +
                  "/{" + STORE_ID + "}" +
                  "/{" + LOCALE + "}" +
                  "/{" + FORMAT_NAME + "}" +
                  "/{" + PART_NUMBER + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";


  @RequestMapping(value = URI_PATTERN)
  public ModelAndView handleRequestWidthHeight(@PathVariable(STORE_ID) String storeId,
                                               @PathVariable(LOCALE) String locale,
                                               @PathVariable(FORMAT_NAME) String formatName,
                                               @PathVariable(PART_NUMBER) String partNumber,
                                               HttpServletRequest request) throws IOException {
    Site site = siteResolver.findSiteFor(storeId, LocaleUtils.toLocale(locale));
    if (site == null) {
      //Site not found
      return HandlerHelper.notFound();
    }

    Content catalogPictureObject = null;
    if (licenseInspector.isFeatureActive()){
      catalogPictureObject = findCatalogPictureFor(partNumber, site);
    } else {
      //return configured default picture, if asset management is not licensed
      if(null != assetService) {
        catalogPictureObject = assetService.getDefaultPicture(site);
      }
    }

    if (catalogPictureObject == null) {
      //Picture not found
      return HandlerHelper.notFound();
    }

    String pictureFormat = pictureFormats.get(formatName);
    if (pictureFormat == null) {
      //format not found
      return HandlerHelper.notFound();
    }

    //picture format value consists of <transformation segment>/<width>/<height>
    String[] split = pictureFormat.split("/");
    String transformationName = split[0];
    String width = split[1];
    String height = split[2];

    //redirect
    request.setAttribute(ViewUtils.PARAMETERS, ImmutableMap.<String, Object>of(
            TransformedBlobHandler.WIDTH_SEGMENT, width,
            TransformedBlobHandler.HEIGHT_SEGMENT, height,
            TransformedBlobHandler.TRANSFORMATION_SEGMENT, transformationName
    ));

    CMPicture catalogPicture = contentBeanFactory.createBeanFor(catalogPictureObject, CMPicture.class);
    return HandlerHelper.redirectTo(catalogPicture.getTransformedData(transformationName));
  }

  private Content findCatalogPictureFor(String externalId, Site site) {
    if(null != assetService) {
      List<Content> pictureList = assetService.findPictures(externalId);
      if (pictureList.size() > 1) {
        LOG.debug("More than one CMPicture found for the catalog object with the id " + externalId + " in the site " + site.getName());
      }
      if(!pictureList.isEmpty()) {
        return pictureList.get(0);
      }
    }
    return null;
  }

  public LiveContextSiteResolver getSiteResolver() {
    return siteResolver;
  }

  @Required
  public void setSiteResolver(LiveContextSiteResolver siteResolver) {
    this.siteResolver = siteResolver;
  }

  public ContentBeanFactory getContentBeanFactory() {
    return contentBeanFactory;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  public SettingsService getSettingsService() {
    return settingsService;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setPictureFormats(Map<String, String> pictureFormats) {
    this.pictureFormats = pictureFormats;
  }

  @Autowired(required = false)
  public void setAssetService(AssetService assetService) {
    this.assetService = assetService;
  }

  @Required
  public void setLicenseInspector(AssetManagementLicenseInspector licenseInspector) {
    this.licenseInspector = licenseInspector;
  }
}
