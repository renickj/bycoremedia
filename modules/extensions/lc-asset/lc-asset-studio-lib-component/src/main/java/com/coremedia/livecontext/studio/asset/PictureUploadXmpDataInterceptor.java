package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.NoCommerceConnectionAvailable;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.asset.util.AssetHelper;
import com.coremedia.livecontext.asset.util.XmpImageMetadataExtractor;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extracts product codes from XMP/IPTC Data and stores product references to struct property.
 */
public class PictureUploadXmpDataInterceptor extends ContentWriteInterceptorBase {

  private static final Logger LOG = LoggerFactory.getLogger(PictureUploadXmpDataInterceptor.class);
  private static final String ASSET_PRODUCT_IDS_ATTRIBUTE_NAME = "defaultProductIds";

  private String imageProperty;
  private CommerceConnectionInitializer commerceConnectionInitializer;
  private ContentRepository contentRepository;

  @Override
  public void intercept(ContentWriteRequest request) {
    Map<String, Object> properties = request.getProperties();
    if (!properties.containsKey(imageProperty)) {
      //not my turf
      return;
    }

    Content parent = request.getParent();
    if (parent != null) {
      try {
        commerceConnectionInitializer.init(parent);
      } catch (NoCommerceConnectionAvailable ex){
        if (LOG.isDebugEnabled()) {
          LOG.debug("No Commerce Connection. The product metadata will not be extracted");
        }
        return;
      }
    }

    if(Commerce.getCurrentConnection() == null) {
      return;
    }

    CommerceIdProvider idProvider = Commerce.getCurrentConnection().getIdProvider();
    CatalogService catalogService = Commerce.getCurrentConnection().getCatalogService();
    if (idProvider == null || catalogService == null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("No id provider/catalog service for the commerce connection " + Commerce.getCurrentConnection() +
                " The product metadata will not be extracted");
      }
      return;
    }

    Object value = properties.get(imageProperty);
    if (value instanceof Blob) {
      Blob blob = (Blob) value;

      List<String> productIds = new ArrayList<>();
      Iterable<String> xmpIds;
      Object assetProductIds = request.getAttribute(ASSET_PRODUCT_IDS_ATTRIBUTE_NAME);
      if (assetProductIds != null) {
        xmpIds = (Iterable) assetProductIds;
      } else {
        xmpIds = XmpImageMetadataExtractor.extractInventoryInfo(blob.getInputStream());
      }
      for (String externalId : xmpIds) {
        Product product = retrieveProductOrVariant(externalId);
        if (product != null) {
          productIds.add(product.getId());
        } else if (LOG.isDebugEnabled()) {
          LOG.debug("Product id " + externalId + " could not be found in catalog. XMP data not persisted.");
        }
      }

      properties.put(AssetHelper.STRUCT_PROPERTY_NAME, AssetHelper.updateCMPictureForExternalIds(request.getEntity(), productIds, contentRepository));
    } else if (value == null) {
      // delete blob action
      Struct result = AssetHelper.updateCMPictureOnBlobDelete(request.getEntity());
      if (result != null) {
        properties.put(AssetHelper.STRUCT_PROPERTY_NAME, result);
      }
    }
  }

  Product retrieveProductOrVariant(String externalId) {
    CommerceIdProvider idProvider = Commerce.getCurrentConnection().getIdProvider();
    CatalogService catalogService = Commerce.getCurrentConnection().getCatalogService();

    String id = idProvider.formatProductId(externalId);
    //the ibm catalogservice allows to retrieve Products and/or ProductVariants by a single call of #findProductById
    Product result = catalogService.findProductById(id);
    if (result != null) {
      return result;
    } else {
      //for other catalog implementations an explicit request for ProductVariants
      id = idProvider.formatProductVariantId(externalId);
      result = catalogService.findProductVariantById(id);
    }
    return result;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setImageProperty(String imageProperty) {
    this.imageProperty = imageProperty;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

}
