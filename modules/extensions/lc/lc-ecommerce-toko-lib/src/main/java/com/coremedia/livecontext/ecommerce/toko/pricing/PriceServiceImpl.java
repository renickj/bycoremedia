package com.coremedia.livecontext.ecommerce.toko.pricing;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.pricing.PriceService;

import javax.annotation.Nonnull;
import java.math.BigDecimal;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class PriceServiceImpl implements PriceService {

  @Override
  public BigDecimal findListPriceForProduct(String productId) {
    return BigDecimal.TEN;
  }

  @Override
  public BigDecimal findOfferPriceForProduct(String productId) {
    return BigDecimal.ONE;
  }

  @Nonnull
  @Override
  public PriceService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, PriceService.class);
  }
}
