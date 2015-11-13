package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.validation.Issues;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.List;

import static com.coremedia.rest.validation.Severity.ERROR;
import static com.coremedia.rest.validation.Severity.WARN;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ExternalChannelExternalIdValidator extends CatalogLinkValidator {
  private static final String CODE_ISSUE_CATEGORY_EMPTY = "externalChannelEmptyCategory";
  private static final String CODE_ISSUE_ID_EMPTY = "externalChannelEmptyExternalId";
  private static final String CODE_ISSUE_ID_INVALID = "externalChannelInvalidId";
  static final String CODE_ISSUE_ID_VALID_ONLY_IN_A_WORKSPACE = "externalChannelValidInAWorkspace";
  private static final String CODE_ISSUE_CONTEXT_INVALID = "externalChannelInvalidStoreContext";
  private static final String CODE_ISSUE_DUPLICATE_EXTERNAL_ID = "externalChannelInvalidDuplicate";
  private static final String CODE_ISSUE_CONTEXT_NOT_FOUND = CODE_ISSUE_CONTEXT_INVALID;
  private static final String LOCAL_SETTINGS_STRUCT_NAME = "localSettings";
  private static final String CATALOG_STRUCT_PROPERTY = "catalog";

  private boolean catalog = true;

  protected static final String CACHE_CLASS = ExternalChannelExternalIdValidator.class.getName();
  public static final String EXTERNAL_CHANNEL_DOCTYPE = "CMExternalChannel";

  private Cache cache;
  private SitesService sitesService;
  private boolean isHomepage = false;

  @Override
  public void validate(Content content, Issues issues) {
    if (content.getStruct(LOCAL_SETTINGS_STRUCT_NAME) != null && Boolean.FALSE.equals(content.getStruct(LOCAL_SETTINGS_STRUCT_NAME).get(CATALOG_STRUCT_PROPERTY))) {
      catalog = false;
    } else {
      catalog = true;
    }
    Site site = sitesService.getContentSiteAspect(content).getSite();
    if (site != null) {
      isHomepage = site.getSiteRootDocument().equals(content);
    }
    super.validate(content, issues);
    validateUniqueness(content, issues, site);
  }

  private void validateUniqueness(Content content, Issues issues, Site site) {
    if (site == null) {
      throw new IllegalStateException("Could not find the site of content " + content.getPath());
    }

    Content rootChannel = site.getSiteRootDocument();
    validateUniqueness(content, rootChannel, issues);
  }

  private void validateUniqueness(Content content, Content rootChannel, Issues issues) {
    String externalId = content.getString(getPropertyName());
    if (isNotBlank(externalId)) {
      Content duplicate = cache.get(new DuplicateExternalIdCacheKey(content, rootChannel));
      if (duplicate != null) {
        issues.addIssue(ERROR, getPropertyName(), CODE_ISSUE_DUPLICATE_EXTERNAL_ID, externalId, duplicate.getPath());
      }
    }
  }

  @Override
  protected void emptyPropertyValue(Issues issues) {
    if (catalog) {
      issues.addIssue(ERROR, getPropertyName(), CODE_ISSUE_CATEGORY_EMPTY);
    } else if (!isHomepage) {
      issues.addIssue(ERROR, getPropertyName(), CODE_ISSUE_ID_EMPTY);
    }
  }

  @Override
  protected void invalidStoreContext(Issues issues, Object... arguments) {
    issues.addIssue(WARN, getPropertyName(), CODE_ISSUE_CONTEXT_INVALID, arguments);
  }

  @Override
  protected void invalidExternalId(Issues issues, Object... arguments) {
    if (catalog) {
      issues.addIssue(WARN, getPropertyName(), CODE_ISSUE_ID_INVALID, arguments);
    }
  }

  @Override
  protected void validOnlyInWorkspace(Issues issues, Object... arguments) {
    issues.addIssue(WARN, getPropertyName(), CODE_ISSUE_ID_VALID_ONLY_IN_A_WORKSPACE, arguments);
  }

  @Override
  protected void storeContextNotFound(Issues issues, Object... arguments) {
    issues.addIssue(WARN, getPropertyName(), CODE_ISSUE_CONTEXT_NOT_FOUND, arguments);
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  protected final class DuplicateExternalIdCacheKey extends CacheKey<Content> {
    private static final String PROPERTY_CHILDREN = "children";

    private Content testling;
    private Content rootChannel;

    public DuplicateExternalIdCacheKey(Content testling, Content rootChannel) {
      if (!testling.getType().isSubtypeOf(EXTERNAL_CHANNEL_DOCTYPE)) {
        throw new IllegalArgumentException("The testling of this cache key needs to be a " + EXTERNAL_CHANNEL_DOCTYPE);
      }

      this.testling = testling;
      this.rootChannel = rootChannel;
    }

    @Override
    public Content evaluate(Cache cache) throws Exception {
      String externalId = testling.getString(getPropertyName());
      return findDuplicate(rootChannel, externalId);
    }

    @Override
    public String cacheClass(Cache cache, Content value) {
      return CACHE_CLASS;
    }

    private Content findDuplicate(Content parent, String externalId) {
      if (isDuplicate(parent, externalId)) {
        return parent;
      }

      if (hasProperty(PROPERTY_CHILDREN, parent)) {
        List<Content> children = parent.getLinks(PROPERTY_CHILDREN);
        if (isNotEmpty(children)) {
          for (Content child : children) {
            Content duplicate = findDuplicate(child, externalId);
            if (duplicate != null) {
              return duplicate;
            }
          }
        }
      }

      return null;
    }

    private boolean hasProperty(@Nonnull String propertyName, @Nonnull Content content) {
      return content.getProperties().containsKey(propertyName);
    }

    private boolean isDuplicate(Content candidate, String externalId) {
      boolean catalogCandidate = true;
      if (candidate.getStruct(LOCAL_SETTINGS_STRUCT_NAME) != null && Boolean.FALSE.equals(candidate.getStruct(LOCAL_SETTINGS_STRUCT_NAME).get(CATALOG_STRUCT_PROPERTY))) {
        catalogCandidate = false;
      }
      return catalog == catalogCandidate && !testling.getId().equals(candidate.getId())
              && candidate.getType().isSubtypeOf(EXTERNAL_CHANNEL_DOCTYPE)
              && externalId.equals(candidate.getString(getPropertyName()));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      DuplicateExternalIdCacheKey that = (DuplicateExternalIdCacheKey) o;

      if (!rootChannel.equals(that.rootChannel)) {
        return false;
      }
      //noinspection RedundantIfStatement
      if (!testling.equals(that.testling)) {
        return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      int result = testling.hashCode();
      result = 31 * result + rootChannel.hashCode();
      return result;
    }
  }
}
