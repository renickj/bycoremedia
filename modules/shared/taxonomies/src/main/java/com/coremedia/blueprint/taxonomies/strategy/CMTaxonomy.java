package com.coremedia.blueprint.taxonomies.strategy;

import com.coremedia.blueprint.taxonomies.TaxonomyNode;
import com.coremedia.blueprint.taxonomies.TaxonomyNodeList;
import com.coremedia.blueprint.taxonomies.TaxonomyUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.publication.PublicationService;
import com.coremedia.rest.cap.content.search.solr.SolrSearchService;
import com.coremedia.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * A strategy which represents the folder/document structure of the content repository
 * as a taxonomy...
 * <p/>
 * this class is maybe not very useful but it demonstrates how to implement taxonomy strategies.
 */
public class CMTaxonomy extends TaxonomyBase { // NOSONAR  cyclomatic complexity

  public static final String ROOT_TYPE = "root";

  private static final Logger LOG = LoggerFactory.getLogger(CMTaxonomy.class);

  private static final int LIMIT = 100;
  private static final String VALUE = "value";
  private static final String CHILDREN = "children";

  private static final String NEW_KEYWORD = "new keyword";

  private ContentRepository contentRepository;
  private ContentType taxonomyContentType = null;
  private TaxonomyNode root;
  private Content rootFolder;

  private SolrSearchService solrSearchService;

  public CMTaxonomy(Content rootFolder, String siteId, ContentType type, ContentRepository contentRepository, SolrSearchService solrSearchService) {
    super(rootFolder.getName(), siteId);

    this.rootFolder = rootFolder;
    this.contentRepository = contentRepository;
    this.solrSearchService = solrSearchService;
    this.taxonomyContentType = type;

    // Constructor Calls Overridable Method
    root = createEmptyNode();
    root.setName(getTaxonomyId());
    root.setSiteId(siteId);
    root.setSelectable(false);
    root.setRoot(true);
    root.setRef(TaxonomyUtil.getRestIdFromCapId(rootFolder.getId()));
    root.setType(ROOT_TYPE);
    root.setLevel(0);
  }

  @Override
  public boolean isValid() {
    return rootFolder.isReadable() && rootFolder.isInProduction() && !rootFolder.getChildDocuments().isEmpty();
  }

  @Override
  public TaxonomyNode getNodeByRef(String ref) {
    if (root.getRef().equals(ref)) {
      return root;
    } else {
      Content c = getContent(ref);
      return asNode(c);
    }
  }

  @Override
  public TaxonomyNode getRoot() {
    return root;
  }

  @Override
  public TaxonomyNode getParent(String ref) {
    Content nodeContent = getContent(ref);
    Content parent = getParent(nodeContent);
    return asNode(parent);
  }

  @Override
  public TaxonomyNodeList getChildren(TaxonomyNode node, int offset, int count) {
    if (node.isRoot()) {
      return getTopLevel();
    }

    Content content = asContent(node);
    return asNodeList(getValidChildren(content), offset, count, false);
  }

  @Override
  public TaxonomyNode getPath(TaxonomyNode node) {
    Content content = asContent(node);
    List<Content> path = new ArrayList<>();
    buildPathRecursively(content, path);

    TaxonomyNodeList list = asNodeList(path, -1, -1, true);
    node.setPath(list);
    return node;
  }

  @Override
  public TaxonomyNodeList find(String text) {
    TaxonomyNodeList list = new TaxonomyNodeList();
    List<TaxonomyNode> hits = new ArrayList<>();
    if (StringUtils.isBlank(text)) {
      return list;
    }

    String query = TaxonomyUtil.formatSolrSearch(text);//NOSONAR
    List<Content> matches = TaxonomyUtil.solrSearch(solrSearchService, rootFolder, taxonomyContentType, query, LIMIT);
    for (Content match : matches) {
      if (match.isDeleted()) {
        continue;
      }
      if (TaxonomyUtil.isCyclic(match, taxonomyContentType)) {
        continue;
      }

      if (StringUtils.containsIgnoreCase(match.getName(), text) ||
              StringUtils.containsIgnoreCase(match.getString(VALUE), text)) {
        TaxonomyNode hit = asNode(match);
        hit.setPath(getPath(hit).getPath());
        hits.add(hit);
      }
    }
    list.setNodes(hits);
    return list;
  }

  @Override
  public TaxonomyNode moveNode(TaxonomyNode node, TaxonomyNode target) {  // NOSONAR  cyclomatic complexity
    //retrieve the contents we need for this operation
    Content nodeContent = asContent(node);
    Content parent = getParent(nodeContent);
    Content targetContent = asContent(target);

    if (parent != null && targetContent.getId().equals(parent.getId())) {
      LOG.warn("Can not move '" + node.getName() + "' to '" + targetContent.getName() + "', it's already there.");
      return asNode(nodeContent);
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Moving '" + node.getName() + "' to '" + targetContent.getName() + "'");
    }

    //checkout the content objects first.
    if (!nodeContent.isCheckedOut()) {
      nodeContent.checkOut();
    }
    if (!targetContent.isFolder() && !targetContent.isCheckedOut()) {
      targetContent.checkOut();
    }
    if (parent != null && !parent.isCheckedOut()) {
      parent.checkOut();
    }

    //remove child relation in the parent
    if (parent != null) {
      List<Content> children = new ArrayList<>(getValidChildren(parent));
      children.remove(nodeContent);
      parent.set(CHILDREN, children);
    }

    //now move the content to the new target by appending as child (we do not set the parent relation anymore!)
    if (targetContent.isDocument()) { // NOSONAR //target can be the root folder too!!!
      List<Content> targetChildren = new ArrayList<>(getValidChildren(targetContent));
      targetChildren.add(nodeContent);
      targetContent.set(CHILDREN, targetChildren);
    }


    //finally update the lifecycle status
    if (nodeContent.isCheckedOut()) {
      nodeContent.checkIn();
    }
    approveAndPublish(nodeContent);
    if (parent != null) {
      if (parent.isCheckedOut()) {
        parent.checkIn();
      }
      approveAndPublish(parent);
    }

    if (!targetContent.isFolder() && targetContent.isCheckedOut()) {
      targetContent.checkIn();
    }
    approveAndPublish(targetContent);

    //return updated ref
    return getNodeByRef(node.getRef());
  }

  @Override
  public TaxonomyNode delete(TaxonomyNode toDelete) {
    Content deleteMe = asContent(toDelete);
    Content parent = getParent(deleteMe);

    LOG.info("Deleting taxonomy node {}", toDelete);

    if (parent != null) {
      LOG.info("Removing node {} from {}", toDelete, parent);
      if (!parent.isCheckedOut()) {
        parent.checkOut();
      }
      List<Content> children = new ArrayList<>(getValidChildren(parent));
      children.remove(deleteMe);
      parent.set(CHILDREN, children);
      parent.checkIn();
      if (contentRepository.getPublicationService().isPublished(parent)) {
        approveAndPublish(parent);
      }
    }

    if (deleteMe.isCheckedOut()) {
      deleteMe.checkIn();
    }

    // check for referrers...
    if (contentRepository.getPublicationService().isPublished(deleteMe)) {
      Set<Content> referrers = deleteMe.getReferrers();
      contentRepository.getPublicationService().toBeDeleted(deleteMe);
      if (referrers.isEmpty()) {
        approveAndPublish(deleteMe);
      } else {
        contentRepository.getPublicationService().approve(deleteMe.getCheckedInVersion());
      }
    } else {
      deleteMe.delete();
    }

    return (parent == null) ? root : asNode(parent);
  }

  @Override
  public TaxonomyNode createChild(final TaxonomyNode parentNode, final String defaultName) {
    Content parent = (parentNode.isRoot()) ? null : asContent(parentNode);
    ContentType type = (parentNode.isRoot()) ? taxonomyContentType : parent.getType();
    Content folder = rootFolder;

    //check if the corresponding parent folder is used
    if (parent != null && parent.isDocument()) {
      folder = parent.getParent();
    }

    Content content = type.createByTemplate(folder, NEW_KEYWORD, "{3} ({1})", Collections.EMPTY_MAP);
    content.set(VALUE, StringUtil.isEmpty(defaultName) ? NEW_KEYWORD : defaultName);
    content.checkIn();

    if (parent != null) {
      if (!parent.isCheckedOut()) {
        parent.checkOut();
      }
      List<Content> children = new ArrayList<>(getValidChildren(parent));
      children.add(content);
      parent.set(CHILDREN, children);
      parent.checkIn();
    }

    return asNode(content);
  }

  @Override
  public TaxonomyNode commit(TaxonomyNode node) {
    Content content = asContent(node);
    Content parent = getParent(content);
    try {
      if (!content.isDeleted()) {
        //test if renaming is required
        if (!node.getName().equals(content.getName())) {
          String newNodeName = getTaxonomyDocumentName(content);
          //check out document and...
          if (!content.isCheckedOut()) {
            content.checkOut();
          }

          //...check if we have checked out it with our session
          if (content.isCheckedOutByCurrentSession()) {
            // rename content
            String name = content.getString(VALUE);
            if (!StringUtils.isEmpty(name)) {
              content.rename(newNodeName);
            }
          }
        }

        publish(content);

        // publish parent if necessary...(publishing of parent must be done before publishing the child node, otherwise "An internal link of this document could not be published.")
        if (parent != null && parent.getCheckedInVersion() != null && !contentRepository.getPublicationService().isPublished(parent.getCheckedInVersion())) {
          LOG.info("Publishing parent {} of {}", parent, content);
          if(parent.isCheckedOut()){
            parent.checkIn();
          }
          approveAndPublish(parent);
        }

        return asNode(content);
      }
    } catch (Exception e) { //NOSONAR
      LOG.error("Error committing " + node + ": " + e.getMessage(), e);
    }
    return asNode(content);
  }


  /**
   * Used for the name matching strategy to resolve all matching taxonomies for text
   * by simple name matching.
   *
   * @return
   */
  @Override
  public List<TaxonomyNode> getAllChildren() {
    List<TaxonomyNode> allChildren = new ArrayList<>();
    List<Content> matches = new ArrayList<>();
    findAll(rootFolder, matches);
    for (Content child : matches) {
      if (TaxonomyUtil.isTaxonomy(child, taxonomyContentType)) {
        allChildren.add(asNode(child));
      }
    }
    return allChildren;
  }

  @Override
  public String getKeywordType() {
    return taxonomyContentType.getName();
  }

  // === HELPER ===

  /**
   * Content should be publish after each change.
   *
   * @param content
   */
  private void publish(Content content) {
    try {
      if (content.isCheckedOutByCurrentSession()) {
        content.checkIn();
        approveAndPublish(content);
      }
    } catch (Exception e) {
      LOG.error("Error publishing " + content + ": " + e.getMessage(), e);
    }
  }

  /**
   * Returns the name that is used after a taxonomy has been renamed.
   * The new value of the "value" field will be used as document name too.
   *
   * @param content The content to rename.
   * @return The new document name or the original one if the "value" field is empty.
   */
  private String getTaxonomyDocumentName(Content content) {
    // rename content
    String name = content.getString(VALUE);
    if (!StringUtils.isEmpty(name)) {
      name = name.replace('/', '_');
      String formattingName = name;
      int renamingIndex = 0;
      while (content.getParent().getChildDocumentsByName().containsKey(formattingName)) {
        renamingIndex++;
        formattingName = name + "(" + renamingIndex + ")";
      }
      return formattingName;
    }
    return content.getName();
  }

  /**
   * Returns the content for the given ref.
   *
   * @param ref
   * @return
   */
  private Content getContent(String ref) {
    return contentRepository.getContent(TaxonomyUtil.asContentId(ref));
  }

  /**
   * Approves and publishes the given content, used
   * when a taxonomy content has been changed.
   *
   * @param content The content to approve and publish.
   */
  private void approveAndPublish(Content content) {
    try {
      LOG.info("Publishing taxonomy node {}", content);
      PublicationService publisher = contentRepository.getPublicationService();
      publisher.approve(content.getCheckedInVersion());
      publisher.approvePlace(content);
      //publish the folder containing the content
      Content parentFolder = content.getParent();
      if(!publisher.isPublished(parentFolder)){
        publisher.approvePlace(parentFolder);
        publisher.publish(parentFolder);
      }
      publisher.publish(content);
    } catch (Exception e) {
      LOG.error("Publication of taxonomy node '" + content + "' failed.", e);
    }
  }

  private void buildPathRecursively(Content content, List<Content> path) {
    path.add(0, content);
    Content parent = getParent(content);
    if (parent != null && !path.contains(parent)) {
      buildPathRecursively(parent, path);
    }
  }

  /**
   * Returns the first referrer of the given content to determine the path
   * of a taxonomy node.
   *
   * @param content The taxonomy content to search the referrer for.
   * @return
   */
  private Content getParent(Content content) {
    return content.getReferrerWithDescriptorFulfilling(taxonomyContentType.getName(), CHILDREN, "isInProduction");
  }


  /**
   * Converts the given list of nodes to a taxonomy node list representation.
   *
   * @param contents The contents to create the list for.
   * @param offset   The offset value if used or -1.
   * @param count    The count of the items if used or -1.
   * @param addRoot  If true, the root is added to the node list, used when a path is build as list.
   * @return The taxonomy node list representation.
   */
  protected TaxonomyNodeList asNodeList(List<Content> contents, int offset, int count, boolean addRoot) {
    List<TaxonomyNode> nodes = new ArrayList<>();
    //used for path info
    if (addRoot) {
      nodes.add(getRoot());
    }

    int totalSize = contents.size();
    List<Content> contentList = new ArrayList<>(contents);
    if (offset > -1 && count > -1) {
      int lastIndex = offset + count;
      if (lastIndex > totalSize) {
        lastIndex = totalSize;
      }
      contentList = contents.subList(offset, lastIndex);
    }

    for (Content c : contentList) {
      TaxonomyNode n = asNode(c);
      nodes.add(n);
    }
    return new TaxonomyNodeList(nodes);
  }

  /**
   * Converts a content object to a taxonomy node instance.
   *
   * @param content The content object to convert.
   * @return The taxonomy node representation.
   */
  protected TaxonomyNode asNode(Content content) {
    TaxonomyNode node = createEmptyNode();
    node.setName(content.getString(VALUE));
    if (StringUtil.isEmpty(node.getName())) {
      node.setName(content.getName());
    }
    node.setRef(TaxonomyUtil.asNodeRef(content.getId()));
    node.setExtendable(true);
    node.setSiteId(getSiteId());
    node.setType(taxonomyContentType.getName());
    node.setLeaf(getValidChildren(content).isEmpty());
    List<Content> path = new ArrayList<>();
    buildPathRecursively(content, path);
    node.setLevel(path.size());
    return node;
  }

  protected Content asContent(TaxonomyNode node) {
    return contentRepository.getContent(TaxonomyUtil.asContentId(node.getRef()));
  }

  /**
   * Filters deleted or destroyed children of the taxonomy.
   *
   * @param content
   * @return
   */
  private List<Content> getValidChildren(Content content) {
    List<Content> validChildren = new ArrayList<>();
    for (Content child : content.getLinks(CHILDREN)) {
      if (!child.isDestroyed() && child.isInProduction()) {
        validChildren.add(child);
      }
    }
    return validChildren;
  }

  /**
   * Creates a list of top level nodes.
   *
   * @return
   */
  private TaxonomyNodeList getTopLevel() {
    List<TaxonomyNode> list = new ArrayList<>();
    List<Content> topLevelContent = new ArrayList<>();
    findRootNodes(rootFolder, topLevelContent);
    for (Content c : topLevelContent) {
      if(TaxonomyUtil.isTaxonomy(c, taxonomyContentType)) {
        list.add(asNode(c));
      }
    }
    return new TaxonomyNodeList(list);
  }

  /**
   * Recursively collects the nodes from the taxonomy that have no parent
   *
   * @param folder  The folder to lookup keywords in.
   * @param matches
   */
  private void findRootNodes(Content folder, List<Content> matches) {
    Collection<Content> nodes = folder.getChildren();
    for (Content child : nodes) {
      if (child.isDocument()
              && !child.isDeleted()
              && !child.isDestroyed()
              && getParent(child) == null
              && !contentRepository.getPublicationService().isToBeDeleted(child)) { //NOSONAR
        matches.add(child);
      }
    }
  }

  /**
   * Recursively collects the nodes from the taxonomy that have no parent
   *
   * @param folder  The folder to lookup keywords in.
   * @param matches
   */
  private void findAll(Content folder, List<Content> matches) {
    Collection<Content> nodes = folder.getChildren();
    for (Content child : nodes) {
      if (child.isFolder()) {
        findAll(child, matches);
      } else if (!child.isDeleted()
              && !child.isDestroyed()
              && !TaxonomyUtil.isCyclic(child, taxonomyContentType)
              && !contentRepository.getPublicationService().isToBeDeleted(child)) {
        matches.add(child);
      }
    }
  }
}
