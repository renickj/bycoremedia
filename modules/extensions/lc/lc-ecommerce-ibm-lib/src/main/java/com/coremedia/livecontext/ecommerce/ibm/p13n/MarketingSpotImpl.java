package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.MarketingImage;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingText;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Model class for marketing spots.
 * The marketing spot data are based on two different REST handlers providing data. Thus, this model class has to
 * distinguish between the different data formats retrieved by the handlers. The data format can be
 * identified by the property <code>resourceName</code> which is unique and provided by each REST handler.
 *
 **/
public class MarketingSpotImpl extends AbstractIbmCommerceBean implements MarketingSpot {

  private static final Logger LOG = LoggerFactory.getLogger(MarketingSpotImpl.class);

  public static final String CONTENT_FORMAT_FILE = "File";
  public static final String CONTENT_FORMAT_TEXT = "Text";

  private Map<String, Object> delegate;
  private WcMarketingSpotWrapperService marketingSpotWrapperService;

  protected Map<String, Object> getDelegate() {
    if (delegate == null) {
      //noinspection unchecked
      delegate = (Map<String, Object>) getCommerceCache().get(new MarketingSpotCacheKey(getId(), getContext(), UserContextHelper.getCurrentContext(),
              getMarketingSpotWrapperService(), getCommerceCache()));
      if (delegate == null) {
        throw new NotFoundException(getId() + " (marketing spot not found in catalog)");
      }
    }
    return delegate;
  }

  @Override
  public void load() throws CommerceException {
    getDelegate();
  }

  @Override
  public void setDelegate(Object delegate) {
    //noinspection unchecked
    this.delegate = (Map<String, Object>) delegate;
  }

  public WcMarketingSpotWrapperService getMarketingSpotWrapperService() {
    return marketingSpotWrapperService;
  }

  @Required
  public void setMarketingSpotWrapperService(WcMarketingSpotWrapperService marketingSpotWrapperService) {
    this.marketingSpotWrapperService = marketingSpotWrapperService;
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatMarketingSpotId(getExternalId());
  }

  @Override
  public String getExternalId() {
    return getName();
  }

  @Override
  public String getName() {
    switch (getResourceName()) {
      case "spot": return DataMapHelper.getValueForPath(getDelegate(), "MarketingSpot[0].spotName", String.class);
      case "espot": return DataMapHelper.getValueForPath(getDelegate(), "MarketingSpotData[0].eSpotName", String.class);
    }
    return null;
  }

  @Override
  public String getDescription() {
    switch (getResourceName()) {
      case "spot": return DataMapHelper.getValueForPath(getDelegate(), "MarketingSpot[0].description", String.class);
      case "espot": return getName();
    }
    return null;
  }

  @Override
  public String getExternalTechId() {
    return getId();
  }

  protected String getResourceName() {
    return DataMapHelper.getValueForKey(getDelegate(), "resourceName", String.class);
  }

  @Override
  public List<CommerceObject> getEntities() {
    List<CommerceObject> result = new ArrayList<>();
    // noinspection unchecked
    List<Map<String, Object>> activities =
            DataMapHelper.getValueForPath(getDataMap(), "MarketingSpotData[0].baseMarketingSpotActivityData", List.class);
    if (activities != null) {
      for (Map<String, Object> activity : activities) {
        String baseMarketingSpotDataType = (String) activity.get("baseMarketingSpotDataType");
        switch (baseMarketingSpotDataType) {
          case "CatalogEntry":
            result.add(readCatalogEntry(activity));
            break;
          case "CatalogGroup":
            result.add(readCatalogGroup(activity));
            break;
          case "MarketingContent":
            CommerceObject marketingContent = readMarketingContent(activity);
            if (marketingContent != null) {
              result.add(marketingContent);
            }
            break;
        }
      }
    }
    return result;
  }

  protected CommerceBean readCatalogEntry(Map<String, Object> activity) {
    String productId = (String) activity.get("productId");
    if (productId != null) {
      return getCatalogService().findProductById(CommerceIdHelper.formatProductTechId(productId));
    }
    return null;
  }

  protected CommerceBean readCatalogGroup(Map<String, Object> activity) {
    String categoryId = (String) activity.get("categoryId");
    if (categoryId != null) {

      return getCatalogService().findCategoryById(CommerceIdHelper.formatCategoryTechId(categoryId));
    }
    return null;
  }

  protected CommerceObject readMarketingContent(Map<String, Object> activity) {
    String contentFormatName = DataMapHelper.getValueForPath(activity, "contentFormatName", String.class);
    switch (!StringUtils.isEmpty(contentFormatName) ? contentFormatName : "") {
      case CONTENT_FORMAT_FILE:
        return getMarketingImage(activity);
      case CONTENT_FORMAT_TEXT:
        return getMarketingText(activity);
      default:
        LOG.warn("Unknown Marketing Content Format: " + contentFormatName);
        return null;
    }
  }

  protected MarketingImage getMarketingImage(Map<String, Object> activity) {
    String name = (String) DataMapHelper.getValueForPath(activity, "attachmentDescription.attachmentName");
    String shortText = (String) DataMapHelper.getValueForPath(activity, "attachmentDescription.attachmentShortDescription");
    Locale currentLocale = StoreContextHelper.getLocale(StoreContextHelper.getCurrentContext());
    String currentLanguageId = "-1";
    if (currentLocale != null) {
      String value = ((CatalogServiceImpl) getCatalogService()).getLanguageId(currentLocale);
      if (value != null && !value.isEmpty()) {
        currentLanguageId = value;
      }
    }
    String attachmentAssetPath = null;
    List attachments = (List) activity.get("attachmentAsset");
    if (attachments != null) {
      for (Object a : attachments) {
        Map attachment = (Map) a;
        List languageList = (List) attachment.get("attachmentAssetLanguage");
        if (languageList != null && !languageList.isEmpty() && languageList.get(0).equals(currentLanguageId)) {
          attachmentAssetPath = (String) attachment.get("attachmentAssetPath");
        }
      }
    }
    if (attachmentAssetPath == null && attachments != null && !attachments.isEmpty()) {
      Map attachment = (Map) attachments.get(0);
      attachmentAssetPath = (String) attachment.get("attachmentAssetPath");
    }
    String thumbnailUrl = null;
    if (attachmentAssetPath != null) {
      thumbnailUrl = getWcsAssetUrl(attachmentAssetPath);
    }

    return new MarketingImage(name, shortText, thumbnailUrl);
  }

  protected MarketingText getMarketingText(Map<String, Object> activity) {
    String text = (String) DataMapHelper.getValueForPath(activity, "marketingContentDescription.marketingText");
    if (text == null) {
      //"makingText" is a typo by IBM in fep7...
      text = (String) DataMapHelper.getValueForPath(activity, "marketingContentDescription.maketingText");
    }
    return new MarketingText(text);
  }

  protected String getWcsAssetUrl(String suffix) {
    if (suffix != null && !suffix.isEmpty()) {
      try {
        if (suffix.startsWith("http")) {
          URL assetUrl = new URL(suffix);
          return assetUrl.toExternalForm();
        } else {
          URL baseUrl = new URL(((CatalogServiceImpl) getCatalogService()).getWcsAssetsUrl());
          URL assetUrl = new URL(baseUrl, suffix);
          return assetUrl.toExternalForm();
        }
      } catch (MalformedURLException e) {
        LOG.warn("Cannot assemble default image url for marketing content: " + suffix + "(" + e.getMessage() + ")");
      }
    }
    return null;
  }

  @Nonnull
  protected Map<String, Object> getDataMap() {
    return getDelegate() != null ? getDelegate() : Collections.<String, Object>emptyMap();
  }

}
