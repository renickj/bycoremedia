package com.coremedia.livecontext.asset;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.objectserver.view.RenderNode;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * Predicate to determine if a node to render is dynamic include of product assets.
 */
public class ProductAssetsDynamicIncludePredicate implements Predicate<RenderNode> {

  public static String VIEW_NAME = "asDynaAssets";

  @Override
  public boolean apply(@Nullable RenderNode input) {
    if (input == null) {
      return false;
    } else if (input.getBean() instanceof Product && VIEW_NAME.equals(input.getView())) {
      return true;
    }
    return false;
  }
}
