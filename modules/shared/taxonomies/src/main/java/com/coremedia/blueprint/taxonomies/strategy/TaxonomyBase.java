package com.coremedia.blueprint.taxonomies.strategy;

import com.coremedia.blueprint.taxonomies.Taxonomy;
import com.coremedia.blueprint.taxonomies.TaxonomyNode;

/**
 * The abstract base class of taxonomy strategies, implementing some
 * default behaviours of the taxonomy strategy interface.
 */
public abstract class TaxonomyBase implements Taxonomy {

  private String taxonomyId;
  private String siteId;

  public TaxonomyBase(String taxonomyId, String siteId) {
    this.taxonomyId = taxonomyId;
    this.siteId = siteId;
  }

  public final void setTaxonomyId(String taxonomyId) {
    this.taxonomyId = taxonomyId;
  }

  @Override
  public final String getTaxonomyId() {
    return taxonomyId;
  }

  /**
   * Returns the site name configured for this taxonomy.
   *
   * @return the site name configured for this taxonomy.
   */
  @Override
  public String getSiteId() {
    return this.siteId;
  }

  /**
   * creates a new TaxonomyNode and sets the correct taxonomyId
   * <p/>
   * Use this method in order to create taxonomy node objects.
   * Override this method in order to fill default properties for every node.
   *
   * @return a new node
   */
  protected TaxonomyNode createEmptyNode() {
    TaxonomyNode node = new TaxonomyNode();
    node.setTaxonomyId(taxonomyId);
    node.setSelectable(true);
    return node;
  }
}
