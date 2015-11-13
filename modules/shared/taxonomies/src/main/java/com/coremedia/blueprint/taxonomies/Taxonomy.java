package com.coremedia.blueprint.taxonomies;

import java.util.List;

/**
 * To support a new taxonomy type, classes must implement this interface
 * and a bean definition has to be added to the web application's spring configuration.
 */
public interface Taxonomy {

  /**
   * Returns the taxonomy id of the strategy, must be unique.
   *
   * @return
   */
  String getTaxonomyId();

  /**
   * For each taxonomy request the active siteId parameter is passed so that
   * the taxonomy tree can be build site depending.
   *
   * @return The name ofthe site.
   */
  String getSiteId();

  /**
   * Returns the root node of the taxonomy tree. Usually, this node is not displayed
   * like the folder of documents that contains all keyword items.
   *
   * @return The root node of the taxonomy strategy.
   */
  TaxonomyNode getRoot();

  /**
   * The reference is the unique id of a node inside the tree. The method
   * looks up the node for the given ref.
   *
   * @param ref The reference of the node to find.
   * @return The node to find or null.
   */
  TaxonomyNode getNodeByRef(String ref);


  /**
   * Returns the parent of the node with the given id
   *
   * @param ref The id of the node
   * @return The parent of the node.
   */
  TaxonomyNode getParent(String ref);

  /**
   * Returns the children of the given node.
   *
   * @param node   The node to retrieve the children from.
   * @param offset The offset if paging is used.
   * @param count  The page count if paging is used.
   * @return The node list wrapper that contains child nodes.
   */
  TaxonomyNodeList getChildren(TaxonomyNode node, int offset, int count);

  /**
   * The TaxonomyNodeList contains the full path of the node, including itself
   * and the taxonomy root node (origin) that isn't displayed usually.
   *
   * @param node The node to retrieve the path from.
   * @return The node that contains all nodes of the path.
   */
  TaxonomyNode getPath(TaxonomyNode node);

  /**
   * Looks up nodes for the given search text. This method is
   * used for the search text field of link lists and the taxonomy admin console.
   *
   * @param text The search text.
   * @return A list of hits that match the search text.
   */
  TaxonomyNodeList find(String text);

  /**
   * Deletes the given node from the taxonomy tree.
   *
   * @param toDelete The node to be deleted
   * @return The parent of the deleted node or null if the deletion was not possible (e.g. referrers)
   */
  TaxonomyNode delete(TaxonomyNode toDelete);

  /**
   * Creates a new child for the given parent.
   *
   * @param parent The parent to create the child for.
   * @param defaultName The default name to use for the new child.
   * @return The newly created node.
   */
  TaxonomyNode createChild(TaxonomyNode parent, String defaultName);

  /**
   * Commits the changes that have been done on the node.
   *
   * @param node The node to store the new values for.
   * @return The updated node instance.
   */
  TaxonomyNode commit(TaxonomyNode node);

  /**
   * Moves the given taxonomy node to the children of the target node.
   *
   * @param node   The node to move.
   * @param target The target node that will be the new parent.
   * @return The move node with updated references.
   */
  TaxonomyNode moveNode(TaxonomyNode node, TaxonomyNode target);

  /**
   * Returns a list of all available keywords for this taxonomy.
   *
   * @return
   */
  List<TaxonomyNode> getAllChildren();

  /**
   * Returns the document type used for keywords.
   *
   * @return
   */
  String getKeywordType();

  /**
   * Returns true if the taxonomy strategy is still valid (and not deleted).
   *
   * @return
   */
  boolean isValid();
}
