package com.coremedia.livecontext.view;

import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.objectserver.view.RenderNode;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 * Dynamically include {@link Cart} beans if they are displayed as Header items.
 */
public class CartDynamicIncludePredicate implements Predicate<RenderNode> {

  @Override
  public boolean apply(@Nullable RenderNode input) {
    return input != null && input.getBean() instanceof Cart;
  }

}
