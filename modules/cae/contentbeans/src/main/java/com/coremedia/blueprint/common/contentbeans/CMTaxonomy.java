package com.coremedia.blueprint.common.contentbeans;

import java.util.List;

/**
 * CMTaxonomy beans allow for a categorization of contents.
 *
 * <p>Represents document type {@link #NAME CMTaxonomy}.</p>
 */
public interface CMTaxonomy extends CMTeasable {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMTaxonomy'.
   */
  String NAME = "CMTaxonomy";

  /**
   * Name of the document property 'value'.
   */
  String VALUE = "value";

  /**
   * Returns the value of the document property {@link #VALUE}.
   *
   * @return the value of the document property {@link #VALUE}
   */
  String getValue();

  /**
   * Returns the parent taxonomy or <code>null</code> if this taxonomy has no parent taxonomy.
   *
   * @return parent taxonomy or <code>null</code> for the root taxonomy node.
   */
  CMTaxonomy getParent();

  /**
   * Name of the document property 'children'.
   */
  String CHILDREN = "children";

  /**
   * Returns the value of the document property {@link #CHILDREN}.
   *
   * @return the value of the document property {@link #CHILDREN}
   */
  List<? extends CMTaxonomy> getChildren();

  /**
   * Name of the document property 'externalReference'.
   */
  String EXTERNAL_REFERENCE = "externalReference";

  /**
   * Returns the value of the document property {@link #EXTERNAL_REFERENCE}.
   *
   * @return the value of the document property {@link #EXTERNAL_REFERENCE}
   */
  String getExternalReference();

  /**
   * Returns the list of {@link CMTaxonomy} items from the root taxonomy item to this item including this item.
   *
   * @return a list of {@link CMTaxonomy} items
   */
  List<? extends CMTaxonomy> getTaxonomyPathList();
}
