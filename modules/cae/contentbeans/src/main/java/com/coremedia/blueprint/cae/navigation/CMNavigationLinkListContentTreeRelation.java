package com.coremedia.blueprint.cae.navigation;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.base.tree.NavigationLinkListContentTreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Just a delegation implementation to make the existing NavigationLinkListContentTreeRelation class
 * applicable for Linkable/Navigation instances.
 */
public class CMNavigationLinkListContentTreeRelation implements InitializingBean, TreeRelation<Linkable> {

  private String siteContentTypeName;
  private String siteRootPropertyName;
  private String contentTypeName;
  private String childrenPropertyName;

  private NavigationLinkListContentTreeRelation treeRelation;
  private ContentBeanFactory contentBeanFactory;


  // --- construct and configure ------------------------------------

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  /**
   * The content type name this relationship is for
   *
   * @param contentTypeName this relationship is for
   */
  @Required
  public void setContentTypeName(String contentTypeName) {
    this.contentTypeName = contentTypeName;
  }

  /**
   * The property denoting how to retrieve children for an item
   *
   * @param childrenPropertyName the property denoting how to retrieve children for an item
   */
  @Required
  public void setChildrenPropertyName(String childrenPropertyName) {
    this.childrenPropertyName = childrenPropertyName;
  }

  /**
   * The content type to be used in {@link NavigationLinkListContentTreeRelation#isRoot(com.coremedia.cap.content.Content)}
   *
   * @param siteContentTypeName to be used in {@link NavigationLinkListContentTreeRelation#isRoot(com.coremedia.cap.content.Content)}
   */
  @Required
  public void setSiteContentTypeName(String siteContentTypeName) {
    this.siteContentTypeName = siteContentTypeName;
  }

  /**
   * The property to be used in {@link NavigationLinkListContentTreeRelation#isRoot(com.coremedia.cap.content.Content)}
   *
   * @param siteRootPropertyName to be used in {@link NavigationLinkListContentTreeRelation#isRoot(com.coremedia.cap.content.Content)}
   */
  @Required
  public void setSiteRootPropertyName(String siteRootPropertyName) {
    this.siteRootPropertyName = siteRootPropertyName;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    treeRelation = new NavigationLinkListContentTreeRelation();
    treeRelation.setSiteContentTypeName(siteContentTypeName);
    treeRelation.setSiteRootPropertyName(siteRootPropertyName);
    treeRelation.setChildrenPropertyName(childrenPropertyName);
    treeRelation.setContentTypeName(contentTypeName);
  }

  @Override
  public Collection<Linkable> getChildrenOf(Linkable parent) {
    Content content = ((CMNavigation) parent).getContent();
    Collection<Content> childrenOf = treeRelation.getChildrenOf(content);
    return contentBeanFactory.createBeansFor(new ArrayList<>(childrenOf), Linkable.class);
  }

  @Override
  public Linkable getParentOf(Linkable child) {
    Content parentOf = treeRelation.getParentOf(((CMLinkable) child).getContent());
    return contentBeanFactory.createBeanFor(parentOf, Linkable.class);
  }

  @Override
  public Linkable getParentUnchecked(Linkable child) {
    Content parentUnchecked = treeRelation.getParentUnchecked(((CMLinkable) child).getContent());
    return contentBeanFactory.createBeanFor(parentUnchecked, Linkable.class);
  }

  @Override
  public List<Linkable> pathToRoot(Linkable child) {
    List<Content> contents = treeRelation.pathToRoot(((CMLinkable) child).getContent());
    return contentBeanFactory.createBeansFor(new ArrayList<>(contents), Linkable.class);
  }

  @Override
  public boolean isRoot(Linkable item) {
    return treeRelation.isRoot(((CMLinkable) item).getContent());
  }

  @Override
  public boolean isApplicable(Linkable linkable) {
    return linkable instanceof CMNavigation;
  }
}
