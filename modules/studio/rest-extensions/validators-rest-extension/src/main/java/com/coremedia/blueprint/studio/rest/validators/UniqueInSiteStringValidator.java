package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.common.util.Function;
import com.coremedia.common.util.Predicate;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Validates that a string property of some {@link Content} contains a unique value across all contents of the
 * same {@link Site}.
 *
 * <p>Contents that do not belong to a site must have a unique value across all contents without a site.
 *
 * <p>This validator does not compare the property value with other contents itself but uses a configured lookup
 * {@link Function} which returns all contents with some value. This validator creates a validation error if the
 * function returns other contents for the same value.
 *
 * <p>The configured lookup function should not perform expensive computations as it may be called quite often.
 */
public class UniqueInSiteStringValidator extends UniqueStringValidator {

  private final SitesService sitesService;

  public UniqueInSiteStringValidator(@Nonnull ContentRepository contentRepository,
                                     @Nonnull String contentType,
                                     @Nonnull String property,
                                     @Nonnull Function<String, Set<Content>> lookupFunction,
                                     @Nonnull SitesService sitesService) {
    super(contentRepository, contentType, property, lookupFunction);
    this.sitesService = requireNonNull(sitesService);
  }

  /**
   * Returns a predicate on {@link Content} to decide whether unique values are required for the content
   * passed to this method and the content passed to the predicate.
   *
   * <p>The returned predicate returns {@code true} for contents of the same site as the content passed to this method.
   *
   * @param content the content
   * @return predicate
   */
  @Nonnull
  @Override
  protected Predicate<Content> isRelevantFor(@Nonnull Content content) {
    final Site site = getSite(content);
    return new Predicate<Content>() {
      @Override
      public boolean include(Content input) {
        return input != null && Objects.equals(getSite(input), site);
      }
    };
  }

  private Site getSite(@Nonnull Content content) {
    return sitesService.getContentSiteAspect(content).getSite();
  }

}
