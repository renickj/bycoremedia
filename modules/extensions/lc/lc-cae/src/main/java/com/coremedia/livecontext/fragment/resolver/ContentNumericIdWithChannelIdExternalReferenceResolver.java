package com.coremedia.livecontext.fragment.resolver;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.FragmentParameters;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * External Content resolver for 'externalRef' values that contain numeric content ids for linkable and navigation.
 */
public class ContentNumericIdWithChannelIdExternalReferenceResolver extends ExternalReferenceResolverBase {

  public ContentNumericIdWithChannelIdExternalReferenceResolver() {
    super(CONTENT_ID_FRAGMENT_PREFIX);
  }

  // --- interface --------------------------------------------------

  @Override
  protected boolean include(@Nonnull FragmentParameters fragmentParameters, @Nonnull String referenceInfo) {
    try {
      if (referenceInfo.contains("-")) {
        String[] numbers = referenceInfo.split("-");
        if (numbers.length == 2) {
          //noinspection ResultOfMethodCallIgnored
          Integer.parseInt(numbers[0]);
          //noinspection ResultOfMethodCallIgnored
          Integer.parseInt(numbers[1]);
          return true;
        }
      }
    } catch (NumberFormatException e) {
      //ignore
    }
    return false;
  }

  @Nullable
  @Override
  protected LinkableAndNavigation resolveExternalRef(@Nonnull FragmentParameters fragmentParameters,
                                                     @Nonnull String referenceInfo,
                                                     @Nonnull Site site) {
    String[] numbers = referenceInfo.split("-");
    if (numbers.length == 2) {
      Content navigation = getContentById(fragmentParameters, numbers[0]);
      Content linkable = getContentById(fragmentParameters, numbers[1]);
      return new LinkableAndNavigation(linkable, navigation);
    }
    return null;
  }

  // --- internal ---------------------------------------------------

  private Content getContentById(FragmentParameters params, String number) {
    String capId = IdHelper.formatContentId(number);
    return contentRepository.getContent(capId);
  }

}
