package com.coremedia.livecontext.fragment.pagegrid;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.layout.PageGridPlacement;
import com.coremedia.livecontext.navigation.CompositeNameHelper;
import com.coremedia.livecontext.navigation.PagePrefixContentKeywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * The resolver uses the CompositeNameHelper to extract page prefixes and a placement name.
 *
 * The resolver will attempt to find a map setting named after the prefix delivered
 * from {@see CompositeNameHelper.getPagePrefixes(compositeName)} and will lookup its page grid (a CMChannel object) using its (nested)
 * {@see ProductDetailPageContentKeywords.PAGEGRID_SETTING_NAME}  setting.
 * Given that CMChannel exists, it will attempt to resolve the placement name.
 */
public class CompositeNamePageGridPlacementResolver implements PageGridPlacementResolver {
  private static final Logger LOG = LoggerFactory.getLogger(CompositeNamePageGridPlacementResolver.class);

  private SettingsService settingsService;

  @Nullable
  @Override
  public PageGridPlacement resolvePageGridPlacement(@Nonnull CMChannel context, @Nonnull String compositeName) {
    //noinspection ConstantConditions
    if (!CompositeNameHelper.isCompositeName(compositeName) || context == null) {
      return null;
    }

    return resolveCompositePageGridPlacement(context, compositeName);
  }

  @Nullable
  protected PageGridPlacement resolveCompositePageGridPlacement(@Nonnull CMChannel context, @Nonnull String compositeName) {
    String pagePrefix = CompositeNameHelper.getPagePrefix(compositeName);
    String placementName = CompositeNameHelper.getPlacementName(compositeName);

    PageGridPlacement result = resolvePageGridPlacement(context, pagePrefix, placementName);

    if (result == null) {
      LOG.info("Composite name {} could not be resolved", compositeName);
    }
    return result;
  }

  @Nullable
  protected PageGridPlacement resolvePageGridPlacement(@Nonnull CMChannel context, @Nonnull String pagePrefix, @Nonnull String placementName) {
    // Lookup page prefix settings object
    Map<String, Object> pagePrefixSettings;
    try {
      pagePrefixSettings = settingsService.settingAsMap(pagePrefix, String.class, Object.class, context);
    }
    catch (Exception e) {
      LOG.error("Cannot resolve settings map for page prefix '" + pagePrefix + "'", e);
      return null;
    }

    // Lookup page grid
    //noinspection ConstantConditions
    if (pagePrefixSettings != null) {
      Object pageGrid = pagePrefixSettings.get(PagePrefixContentKeywords.PAGEGRID_SETTING_NAME);
      if (pageGrid instanceof CMChannel) {
        return resolvePlacement((CMChannel) pageGrid, placementName);
      }
    }
    return null;
  }

  @Nullable
  protected PageGridPlacement resolvePlacement(CMChannel pageGrid, String placementName) {
    try {
      return pageGrid.getPageGrid().getPlacementForName(placementName);
    }
    catch (Exception e) {
      LOG.error("Error when resolving placement '" + placementName + "' of page grid", e);
      return null;
    }
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }
}
