package com.coremedia.livecontext.studio {
import com.coremedia.ecommerce.studio.*;
import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cap.content.impl.ContentStructRemoteBeanImpl;
import com.coremedia.cap.content.impl.ContentTypeImpl;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.*;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

public class AbstractCatalogStudioTest extends AbstractCatalogTest {

  override public function setUp():void {
    super.setUp();

    CatalogHelper.reset();

    BeanFactoryImpl(beanFactory).registerRemoteBeanClasses(ContentTypeImpl, ContentImpl, ContentStructRemoteBeanImpl,
            StoreImpl, CategoryImpl, MarketingImpl, CatalogImpl, ProductImpl, MarketingSpotImpl, ProductVariantImpl);

    new LivecontextCollectionViewActionsPlugin();
  }
}
}