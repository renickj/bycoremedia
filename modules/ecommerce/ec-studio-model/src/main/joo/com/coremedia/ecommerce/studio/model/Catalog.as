package com.coremedia.ecommerce.studio.model {

public interface Catalog extends CatalogObject{
  /**
   * Return a list of top level categories
   */
  function getTopCategories():Array;

  /**
   * Return a mapping of the name of top level categories to the categories themselves
   *
   * @see CatalogObjectPropertyNames#CHILDREN_BY_NAME
   */
  function getChildrenByName():Object;

}
}