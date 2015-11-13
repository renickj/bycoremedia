package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.asset.license.AssetManagementLicenseInspector;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class AssetServiceImpl implements AssetService {

  public static final String CONFIG_KEY_DEFAULT_PICTURE = "livecontext.default.product.picture";

  private static final Logger LOG = LoggerFactory.getLogger(AssetServiceImpl.class);
  private SitesService sitesService;
  private SettingsService settingsService;
  private AssetChanges assetChanges;

  private AssetSearchService assetSearchService;
  private AssetValidationService assetValidationService;

  @Inject
  @Named("lcAssetManagementLicenseInspector")
  private AssetManagementLicenseInspector licenseInspector;

  @Override
  public CatalogPicture getCatalogPicture(String url) {

    String externalId = parsePartNumberFromUrl(url);
    if (externalId == null) {
      // Not a CMS URL
      return new CatalogPicture(url, null);
    }

    // Make absolute and replace {cmsHost}
    String imageUrl = getAssetUrlProvider().getImageUrl(url);

    List<Content> cmPictures = findPictures(externalId);
    if (cmPictures.isEmpty()) {
      return new CatalogPicture(imageUrl, null);
    }

    return new CatalogPicture(imageUrl, cmPictures.get(0));
  }

  @Override
  @Nonnull
  public List<Content> findPictures(@Nonnull String externalId) {

    List<Content> references = Collections.emptyList();

    Site site = getSite();
    if (site != null) {
      if (licenseInspector.isFeatureActive()) {
        references = findAssets("CMPicture", externalId, site);
      }

      if (references.isEmpty()) {
        Content defaultPicture = getDefaultPicture(site);
        if (defaultPicture != null) {
          return Collections.singletonList(defaultPicture);
        }
      }
    }
    return references;
  }

  @Override
  @Nonnull
  public List<Content> findVisuals(@Nullable String externalId) {
    Site site = getSite();
    if (site != null && licenseInspector.isFeatureActive() && externalId != null) {
      List<Content> references = findAssets("CMVisual", externalId, site);
      if (references.isEmpty()) {
        Content defaultPicture = getDefaultPicture(site);
        if (defaultPicture != null) {
          return Collections.singletonList(defaultPicture);
        }
      }
      return references;
    }
    return Collections.emptyList();
  }

  @Override
  @Nonnull
  public List<Content> findDownloads(@Nullable String externalId) {
    List<Content> references = Collections.emptyList();
    Site site = getSite();
    if (site != null && licenseInspector.isFeatureActive() && externalId != null) {
      references = findAssets("CMDownload", externalId, site);
    }
    return references;
  }

  @Nonnull
  List<Content> findAssets(@Nonnull String contentType, @Nonnull String externalId, @Nonnull Site site) {

    if (assetSearchService == null) {
      LOG.error("assetSearchService is not set, cannot find assets for {} in site {}", externalId, site.getName());
      return Collections.emptyList();
    }

    Collection<Content> changedAssets = assetChanges.get(externalId, site);

    List<Content> indexedAssets = assetSearchService.searchAssets(contentType, externalId, site);

    //merge indexed assets with changed assets
    List<Content> assets = new ArrayList<>(indexedAssets);
    if (changedAssets != null) {
      for (Content changedContent: changedAssets) {
        //filter the documents of the given contentType
        if (!changedContent.isDestroyed() && changedContent.getType().isSubtypeOf(contentType)) {
          if (!assets.contains(changedContent)) {
            assets.add(changedContent);
          }
        }
      }
    }

    //check now if the assets are up-to-date
    for (int i = assets.size() - 1; i >= 0; i--) {
      Content asset = assets.get(i);
      if (!assetChanges.isUpToDate(asset, externalId, site)) {
        assets.remove(asset);
      }
    }

    if (assetValidationService != null) {
      //filter validity
      assets = assetValidationService.filterAssets(assets);
    }

    //sort by the name of the content
    Collections.sort(assets, new Comparator<Content>() {
      @Override
      public int compare(Content content1, Content content2) {
        return content1.getName().compareTo(content2.getName());
      }
    });


    if (assets.isEmpty()) {
      //try to load assets from parent Product, if partNumber belongs to ProductVariant
      assets = findFallbackForProductVariant(externalId, site, contentType);
    }

    return assets;
  }

  private List<Content> findFallbackForProductVariant(String externalId, Site site, String contentType) {
    List<Content> assets = Collections.emptyList();
    StoreContext storeContextForSite = getStoreContextProvider().findContextBySite(site);

    if (storeContextForSite != null) {

      getStoreContextProvider().setCurrentContext(storeContextForSite);

      Product product = getCatalogService().findProductById(Commerce.getCurrentConnection().getIdProvider().formatProductId(externalId));

      if (product != null && product instanceof ProductVariant) {
        Product parentProduct = ((ProductVariant) product).getParent();
        if (parentProduct != null) {
          //noinspection unchecked
          assets = findAssets(contentType, parentProduct.getExternalId(), site);
        }
      }
    }
    return assets;
  }

  private Site getSite() {
    if (Commerce.getCurrentConnection() != null) {
      String siteId = Commerce.getCurrentConnection().getStoreContext().getSiteId();
      return sitesService.getSite(siteId);
    }
    return null;
  }

  @Override
  public Content getDefaultPicture(@Nonnull Site site) {
    return settingsService.setting(CONFIG_KEY_DEFAULT_PICTURE, Content.class, site.getSiteRootDocument());
  }

  static List<String> getSubTypesOf(String contentType, ContentRepository contentRepository) {
    ContentType type = contentRepository.getContentType(contentType);
    if (type == null) {
      throw new IllegalStateException("The configured content type '" + contentType + "' does not exist.");
    }

    Set<ContentType> subtypes = type.getSubtypes();
    List<String> escapedContentTypes = new ArrayList<>(subtypes.size());
    for (ContentType subtype : subtypes) {
      escapedContentTypes.add(escapeLiteralForSearch(subtype.getName()));
    }
    return escapedContentTypes;
  }

  @Nonnull
  private static String escapeLiteralForSearch(@Nonnull String literal) {
    return '"' + CharMatcher.is('"').replaceFrom(literal, "\\\"") + '"';
  }

  private String parsePartNumberFromUrl(String urlStr) {
    if (StringUtils.isBlank(urlStr) || !urlStr.contains(URI_PREFIX)) {
      return null;
    }
    int index = urlStr.lastIndexOf(".");
    if (index >= 0) {
      String fileName = urlStr.substring(0, index);
      index = fileName.lastIndexOf('/');
      if (index >= 0) {
        return fileName.substring(index + 1);
      }
    }
    return null;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  public CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setAssetChanges(AssetChanges assetChanges) {
    this.assetChanges = assetChanges;
  }

  @SuppressWarnings("unused")
  public void setAssetValidationService(AssetValidationService assetValidationService) {
    this.assetValidationService = assetValidationService;
  }

  @SuppressWarnings("unused")
  public void setAssetSearchService(AssetSearchService assetSearchService) {
    this.assetSearchService = assetSearchService;
  }

  @Nonnull
  @Override
  public AssetService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, AssetService.class);
  }

  public AssetUrlProvider getAssetUrlProvider() {
    return Commerce.getCurrentConnection().getAssetUrlProvider();
  }
}
