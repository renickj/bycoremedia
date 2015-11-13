package com.coremedia.ecommerce.studio.model {
public interface ProductVariant extends Product{
  /**
   * @return the parent product of this given product variant
   * @see CatalogObjectPropertyNames#PARENT
   */
  function getParent():Product;

  [ArrayElementType("com.coremedia.livecontext.studio.model.ProductAttribute")]
  function getDefiningAttributes():Array;

}
}