package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.common.contentbeans.CMLocTaxonomy;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.persistentcache.EvaluationException;
import com.coremedia.cap.persistentcache.PersistentCache2;
import com.coremedia.cap.persistentcache.PersistentCacheKey;
import com.coremedia.cap.persistentcache.StoreException;
import org.junit.Before;

public abstract class AbstractTaxonomyPropertyConverterTest {
  private static final TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withContentBeanFactory()
          .withContentRepository("classpath:/com/coremedia/testing/contenttest.xml")
          .withDataViewFactory()
          .withIdProvider()
          .withCache()
          .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
          .build();
  protected CMLocTaxonomy sanFrancisco;
  protected CMLocTaxonomy michigan;
  protected CMTaxonomy formula1;
  protected TaxonomyPropertyConverter taxonomyPropertyConverter;

  @Before
  public void setUp() throws Exception {
    sanFrancisco = ContentTestCaseHelper.getContentBean(infrastructure, 72);
    michigan = ContentTestCaseHelper.getContentBean(infrastructure, 70);
    formula1 = ContentTestCaseHelper.getContentBean(infrastructure, 80);

    // spring in java.
    TreePathKeyFactory taxonomyIdPathKeyFactory = createTreePathKeyFactory();
    taxonomyIdPathKeyFactory.setContentRepository(infrastructure.getContentRepository());
    taxonomyIdPathKeyFactory.setTreeRelation(infrastructure.getBean("taxonomyTreeRelation", TreeRelation.class));
    DummyPersistentCache dummyPersistentCache = new DummyPersistentCache();
    taxonomyIdPathKeyFactory.setPersistentCache(dummyPersistentCache);
    taxonomyPropertyConverter = new TaxonomyPropertyConverter();
    taxonomyPropertyConverter.setTaxonomyPathKeyFactory(taxonomyIdPathKeyFactory);
  }

  protected abstract TreePathKeyFactory createTreePathKeyFactory();

  private static class DummyPersistentCache implements PersistentCache2 {
    @Override
    public Object getCached(PersistentCacheKey key) throws StoreException, EvaluationException {
      return get(key);
    }

    @Override
    public Object get(PersistentCacheKey persistentCacheKey) throws StoreException, EvaluationException {
      try {
        return persistentCacheKey.evaluate();
      } catch (Exception e) {
        throw new EvaluationException(e);
      }
    }

    @Override
    public void remove(PersistentCacheKey persistentCacheKey) throws StoreException {
      throw new UnsupportedOperationException("Unimplemented: #remove");
    }
  }
}
