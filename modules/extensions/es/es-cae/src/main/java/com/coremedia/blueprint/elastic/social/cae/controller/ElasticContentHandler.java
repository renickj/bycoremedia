package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.core.cms.ContentWithSite;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.inject.Inject;

public class ElasticContentHandler<T extends ContributionResult> extends ElasticHandler<T> {

  private ContentBeanFactory contentBeanFactory;
  private ContentRepository contentRepository;
  private ContextHelper contextHelper;

  @Inject
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Inject
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Inject
  public void setContextHelper(ContextHelper contextHelper) {
    this.contextHelper = contextHelper;
  }

  public ContextHelper getContextHelper() {
    return contextHelper;
  }

  @Override
  protected UriComponentsBuilder getUriComponentsBuilder(Site site, T result, UriTemplate uriTemplate) {
    CMLinkable contentBean = getTargetAsContentBean(result);
    CMNavigation context = contextHelper.contextFor(contentBean);

    return getUriComponentsBuilder(uriTemplate, context, IdHelper.parseContentId(contentBean.getContent().getId()));
  }

  protected CMLinkable getTargetAsContentBean(T contributionResult) {
    ContentBean contentBean;
    Content content = null;

    if (contributionResult.getTarget() instanceof Content) {
      content = (Content) contributionResult.getTarget();
    } else if (contributionResult.getTarget() instanceof ContentWithSite) {
      content = ((ContentWithSite) contributionResult.getTarget()).getContent();
    }

    if (content != null) {
      contentBean = contentBeanFactory.createBeanFor(content);
    } else if (contributionResult.getTarget() instanceof ContentBean) {
      contentBean = (ContentBean) contributionResult.getTarget();
    } else {
      throw new IllegalArgumentException("Cannot handle comments target " + contributionResult.getTarget());
    }

    if (!(contentBean instanceof CMLinkable)) {
      throw new IllegalArgumentException("Cannot handle content beans that are not linkables: " + contentBean);
    }
    return (CMLinkable) contentBean;
  }


  protected Content getContent(String targetId) {
    if (StringUtils.isBlank(targetId)) {
      return null;
    }
    return contentRepository.getContent(IdHelper.formatContentId(targetId));
  }

  protected Object getContributionTarget(String targetId, Site site) {
    return getContentWithSite(targetId, site);
  }

  protected ContentWithSite getContentWithSite(String targetId, Site site) {
    Content content = getContent(targetId);
    return content == null ? null : new ContentWithSite(content, site);
  }

  /**
   * Provides a {@link com.coremedia.blueprint.common.contentbeans.CMNavigation} from a navigation context id.
   */
  protected Navigation getNavigation(String contextId) {
    final Content content = getContent(contextId);
    final ContentBean navigation = contentBeanFactory.createBeanFor(content);
    if (navigation instanceof Navigation) {
      return (Navigation) navigation;
    } else {
      throw new IllegalArgumentException("Content is not navigation " + content.getId());
    }
  }
}
