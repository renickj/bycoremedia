package com.coremedia.livecontext.contentbeans;


import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cae.aspect.Aspect;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface CMExternalChannel extends CMChannel {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMExternalChannel'.
   */
  String NAME = "CMExternalChannel";

  /**
   * Name of the document property 'externalId'.
   *
   * <p>Useful for queries and content level code.
   */
  String EXTERNAL_ID = "externalId";

  /**
   * Name of the localSettings struct property 'externalUriPath'.
   *
   * <p>Useful to build link to the external system
   */
  String EXTERNAL_URI_PATH = "externalUriPath";

  /**
   * Name of the localSettings struct property 'catalog'.
   *
   * <p>Defines the page type (catalog page or not catalog page).
   * The type "is catalog page" references an external category in the
   * external id field. Otherwise it is a "non catalog" or "other" page
   * that references the external page in another way (e.g. by id).
   * </p>
   */
  String IS_CATALOG_PAGE = "catalog";

  @Override
  CMExternalChannel getMaster();

  @Override
  Map<Locale, ? extends CMExternalChannel> getVariantsByLocale();

  @Override
  Collection<? extends CMExternalChannel> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMExternalChannel>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMExternalChannel>> getAspects();

  String getExternalId();

  String getExternalUriPath();

  boolean isCatalogPage();
}
