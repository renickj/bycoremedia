package com.coremedia.blueprint.ecommerce.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.xml.Markup;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Product features of the ecommerce API are available by the {@link #getProduct}
 * delegate.  Specific features of the CMS catalog are available directly from
 * the content bean.
 */
public interface CMProduct extends CMTeasable {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMProduct'.
   */
  String NAME = "CMProduct";

  /**
   * The name of the downloads property.
   */
  String DOWNLOADS = "downloads";

  /**
   * The name of the short description property
   */
  String SHORT_DESCRIPTION = "shortDescription";

  /**
   * The name of the short description property
   */
  String LONG_DESCRIPTION = "longDescription";

  /**
   * The name of the productCode property.
   */
  String PRODUCT_CODE = "productCode";

  /**
   * The name of the productName property
   */
  String PRODUCT_NAME = "productName";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMProduct} object
   */
  @Override
  CMProduct getMaster();

  /**
   * Returns the variants of this {@link CMProduct} indexed by their {@link java.util.Locale}
   *
   * @return the variants of this {@link CMProduct} indexed by their {@link java.util.Locale}
   */
  @Override
  Map<Locale, ? extends CMProduct> getVariantsByLocale();

  /**
   * Returns the {@link java.util.Locale} specific variants of this {@link CMProduct}
   *
   * @return the {@link java.util.Locale} specific variants of this {@link CMProduct}
   */
  @Override
  Collection<? extends CMProduct> getLocalizations();

  /**
   * Returns a <code>Map</code> from aspectIDs to Aspects. AspectIDs consists of an aspect name with a
   * prefix which identifies the plugin provider.
   *
   * @return a <code>Map</code> from aspectIDs to <code>Aspect</code>s
   */
  @Override
  Map<String, ? extends Aspect<? extends CMProduct>> getAspectByName();

  /**
   * Returns a list of all  <code>Aspect</code>s from all availiable
   * PlugIns that are registered to this content bean.
   *
   * @return a list of {@link com.coremedia.cae.aspect.Aspect}
   */
  @Override
  List<? extends Aspect<? extends CMProduct>> getAspects();

  /**
   * Returns the underlying Product.
   *
   * @return the product bean representing the product in the commerce system
   */
  Product getProduct() throws CommerceException;

  /**
   * Returns the product pictures.
   */
  @Nonnull
  List<CatalogPicture> getProductPictures();

  /**
   * Returns a product picture.
   *
   * @return The first picture of {@link #getProductPictures()} or null if there is no picture.
   */
  CatalogPicture getProductPicture();

  /**
   * Returns the downloads attached to the product.
   *
   * @return A list of CMDownload beans
   */
  List<CMDownload> getDownloads();

  /**
   * Returns the short description of the product if set.
   * If not set the long description is used as fallback
   *
   * @return the short description of the product
   */
  Markup getShortDescription();

  /**
   * Returns the long description.
   *
   * @return the long description of the product
   */
  Markup getLongDescription();

  /**
   * Returns the product code.
   *
   * @return the product code
   */
  String getProductCode();

  /**
   * Returns the product name.
   *
   * @return the name of the product
   */
  String getProductName();
}
