package com.coremedia.blueprint.studio.rest;

import com.coremedia.blueprint.base.testing.XmlRepoConfiguration;
import com.coremedia.blueprint.base.testing.XmlUapiConfig;
import com.coremedia.blueprint.studio.rest.taxonomies.TaxonomyResource;
import com.coremedia.blueprint.taxonomies.Taxonomy;
import com.coremedia.blueprint.taxonomies.TaxonomyNode;
import com.coremedia.blueprint.taxonomies.TaxonomyNodeList;
import com.coremedia.blueprint.taxonomies.TaxonomyResolver;
import com.coremedia.blueprint.taxonomies.semantic.SemanticStrategy;
import com.coremedia.blueprint.taxonomies.semantic.service.SemanticServiceStrategy;
import com.coremedia.blueprint.taxonomies.strategy.CMTaxonomyResolver;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XmlRepoConfiguration.class, TaxonomyResourceTest.LocalConfig.class})
public class TaxonomyResourceTest {
  @Inject
  private TaxonomyResource taxonomyResource;
  @Inject
  private ContentRepository contentRepository;
  @Inject
  private SitesService sitesService;
  @Inject
  private TaxonomyResolver taxonomyResolver;

  @Test
  public void testTaxonomyResource() throws Exception {
    TaxonomyNodeList roots = taxonomyResource.getRoots(null,false);
    Assert.assertFalse(roots.getNodes().isEmpty());
    roots.sortByName();
    for (TaxonomyNode node : roots.getNodes()) {
      Assert.assertTrue(node.getName() != null);
      Assert.assertTrue(node.getRef() != null);
      Assert.assertTrue(node.getType() != null);
      Assert.assertTrue(node.isRoot());
      Assert.assertTrue(node.getPath() == null);
      Assert.assertTrue(node.getTaxonomyId() != null);

      Assert.assertTrue(taxonomyResource.getRoot(null, node.getTaxonomyId()) != null);
      Assert.assertTrue(taxonomyResource.getNode(null, node.getTaxonomyId(), node.getRef()) != null);

      TaxonomyNodeList children = taxonomyResource.getChildren(null, node.getTaxonomyId(), node.getRef(), 0, 50);
      Assert.assertTrue(children != null);
      for (TaxonomyNode child : children.getNodes()) {
        Assert.assertTrue(taxonomyResource.getPath(null, child.getTaxonomyId(), child.getRef()) != null);
      }
    }
  }

  @Test
  public void testSemanticMatching() throws Exception {
    SemanticServiceStrategy strategy = new SemanticServiceStrategy();
    strategy.setContentRepository(contentRepository);
    strategy.setServiceId("nameMatching");

    Collection<Taxonomy> taxonomies = taxonomyResolver.getTaxonomies();
    Taxonomy taxonomy = taxonomies.iterator().next();
    //mpf, not a real test since the SOLR instance is null
    Assert.assertTrue(strategy.suggestions(taxonomy, "coremedia:///cap/content/206").asList(50).size() == 0);
  }

  @Configuration
  @ImportResource(value = {"classpath:com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml",
  },
          reader = com.coremedia.springframework.component.ResourceAwareXmlBeanDefinitionReader.class)
  static class LocalConfig {
    @Bean
    XmlUapiConfig xmlUapiConfig() {
      return new XmlUapiConfig("classpath:/com/coremedia/testing/contenttest.xml");
    }

    @Bean
    TaxonomyResource taxonomyResource(TaxonomyResolver taxonomyResolver) {
      final TaxonomyResource taxonomyResource = new TaxonomyResource();
      taxonomyResource.setSemanticStrategies(new ArrayList<SemanticStrategy>());
      taxonomyResource.setStrategyResolver(taxonomyResolver);
      taxonomyResource.afterPropertiesSet();
      return taxonomyResource;
    }

    /**
     * <bean id="strategyResolver" class="com.coremedia.blueprint.taxonomies.strategy.CMTaxonomyResolver">
     * <property name="contentRepository" ref="contentRepository"/>
     * <property name="solrSearchService" ref="searchService"/>
     * <property name="sites" value="/Sites/"/>
     * <property name="taxonomyFolders" value="/Settings/Taxonomies/,Settings/Options/Taxonomies/"/>
     * <property name="aliasMapping">
     * <map>
     * <entry key="Query" value="Subject"/>
     * <entry key="QueryLocation" value="Location"/>
     * </map>
     * </property>
     * </bean>
     */
    @Bean
    @Inject
    TaxonomyResolver getTaxonomyResolver(ContentRepository contentRepository, SitesService sitesService) throws Exception {
      CMTaxonomyResolver resolver = new CMTaxonomyResolver();
      resolver.setContentRepository(contentRepository);
      resolver.setTaxonomyFolders("/taxonomies,Settings/Options/Taxonomies/");
      resolver.setSitesService(sitesService);
      Map<String, String> aliasMapping = new HashMap<>();
      aliasMapping.put("Query", "Subject");
      aliasMapping.put("QueryLocation", "Location");
      resolver.setAliasMapping(aliasMapping);
      resolver.setContentType("CMTaxonomy");
      resolver.afterPropertiesSet();
      return resolver;
    }
  }}
