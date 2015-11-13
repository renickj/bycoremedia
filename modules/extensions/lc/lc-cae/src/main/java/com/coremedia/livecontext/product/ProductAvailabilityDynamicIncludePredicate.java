package com.coremedia.livecontext.product;

import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.objectserver.view.RenderNode;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * Predicate to determine if a node to render is an instance of {@link AvailabilityInfo}.
 */
public class ProductAvailabilityDynamicIncludePredicate implements Predicate<RenderNode> {

  public static final String VIEW_NAME_AVAILABILITY_FRAGMENT = "availabilityFragment";

  @Override
  public boolean apply(@Nullable RenderNode input) {
    if(input ==null) {
      return false;
    }
    else if(input.getBean() instanceof ProductInSite && VIEW_NAME_AVAILABILITY_FRAGMENT.equals(input.getView())) {
      return true;
    }

    return false;
  }
}
