package com.coremedia.blueprint.studio.rest.taxonomies;

import com.coremedia.blueprint.taxonomies.Taxonomy;
import com.coremedia.blueprint.taxonomies.TaxonomyNode;
import com.coremedia.blueprint.taxonomies.TaxonomyNodeList;
import com.coremedia.blueprint.taxonomies.TaxonomyResolver;
import com.coremedia.blueprint.taxonomies.TaxonomyUtil;
import com.coremedia.blueprint.taxonomies.semantic.SemanticStrategy;
import com.coremedia.blueprint.taxonomies.semantic.Suggestion;
import com.coremedia.blueprint.taxonomies.semantic.Suggestions;
import com.coremedia.blueprint.studio.rest.ConfigurationService;
import com.coremedia.blueprint.studio.rest.StructConfiguration;
import com.coremedia.rest.linking.AbstractLinkingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Path("taxonomies")
public class TaxonomyResource extends AbstractLinkingResource implements InitializingBean {
  private static final Logger LOG = LoggerFactory.getLogger(TaxonomyResource.class);
  private static final String ID = "id";
  private static final String MAX = "max";
  private static final String SITE = "site";
  private static final String RELOAD = "reload";
  private static final String TEXT = "text";
  private static final String OFFSET = "offset";
  private static final String LENGTH = "length";
  public static final String TAXONOMY_ID = "taxonomyId";
  public static final String NODE_REF = "nodeRef";
  public static final String TARGET_NODE_REF = "targetNodeRef";
  public static final String DEFAULT_NAME = "defaultName";

  private TaxonomyResolver strategyResolver;

  private static final String TAXONOMY_SETTINGS = "TaxonomySettings";
  private ConfigurationService configurationService;

  private List<SemanticStrategy> semanticStrategies = new ArrayList<>();
  private Map<String, SemanticStrategy> semanticStrategyById = new HashMap<>();

  @GET
  @Path("settings")
  public TaxonomySettingsRepresentation getSettings(@QueryParam(SITE) String siteId) {
    TaxonomySettingsRepresentation representation = new TaxonomySettingsRepresentation();
    StructConfiguration config = configurationService.getStructMaps(siteId, TAXONOMY_SETTINGS, "settings");
    representation.getAdminGroups().addAll(getGroupsFromStruct(config.getGlobalStructs()));
    representation.getAdminGroups().addAll(getGroupsFromStruct(config.getLocalStructs()));
    return representation;
  }

  @GET
  @Path("find")
  public TaxonomyNodeList find(@QueryParam(SITE) String siteId,
                               @QueryParam(TAXONOMY_ID) String taxonomyId,
                               @QueryParam(TEXT) String text) {
    LOG.debug("find called for text '{}'", text);
    TaxonomyNodeList list = new TaxonomyNodeList(new ArrayList<TaxonomyNode>());
    try {
      if (taxonomyId == null || taxonomyId.length() == 0) {
        for (Taxonomy strategy : getTaxonomiesForAdministration(siteId)) {
          TaxonomyNodeList strategyHits = strategy.find(text);
          if (strategyHits.getNodes() != null) {
            list.getNodes().addAll(strategyHits.getNodes());
          }
        }
      } else {
        Taxonomy taxonomy = getTaxonomy(siteId, taxonomyId);
        TaxonomyNodeList strategyHits = taxonomy.find(text);
        if (strategyHits.getNodes() != null) {
          list.getNodes().addAll(strategyHits.getNodes());
        }
      }
    } catch (Exception e) {
      LOG.error("Search failed for text " + text, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
    return list;
  }

  @GET
  @Path("roots")
  public TaxonomyNodeList getRoots(@QueryParam(SITE) String siteId, @QueryParam(RELOAD) boolean reload) {
    LOG.debug("roots  called for site='{}'", siteId);
    try {
      List<TaxonomyNode> roots = new ArrayList<>();
      if (reload) {
        strategyResolver.reload();
      }
      for (Taxonomy strategy : getTaxonomiesForAdministration(siteId)) {
        roots.add(strategy.getRoot());
      }
      TaxonomyNodeList list = new TaxonomyNodeList(roots);
      list.sortByName();
      return list;
    } catch (Exception e) {
      LOG.error("roots failed.", e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @GET
  @Path("root")
  public TaxonomyNode getRoot(@QueryParam(SITE) String siteId, @QueryParam(TAXONOMY_ID) String taxonomyId) {
    LOG.debug("root called for " + TAXONOMY_ID + "='" + taxonomyId + "'"); //NOSONAR
    try {
      Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
      if (strategy != null) {
        return strategy.getRoot();
      }
    } catch (Exception e) {
      LOG.error("root failed.", e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
    return new TaxonomyNode();
  }

  @GET
  @Path("parent")
  public TaxonomyNode getParent(@QueryParam(SITE) String siteId,
                                @QueryParam(TAXONOMY_ID) String taxonomyId,
                                @QueryParam(NODE_REF) String ref) {
    LOG.debug("parent node called for " + TAXONOMY_ID + "='" + taxonomyId + "', " + NODE_REF + "='" + ref + "'"); //NOSONAR
    try {
      Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
      return strategy.getParent(ref);
    } catch (Exception e) {
      LOG.error("parent failed for " + ref, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @GET
  @Path("node")
  public TaxonomyNode getNode(@QueryParam(SITE) String siteId,
                              @QueryParam(TAXONOMY_ID) String taxonomyId,
                              @QueryParam(NODE_REF) String ref) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("node  called for " + TAXONOMY_ID + "='" + taxonomyId + "', " + NODE_REF + "='" + ref + "'"); //NOSONAR
    }
    try {
      Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
      return strategy.getNodeByRef(ref);
    } catch (Exception e) {
      LOG.error("getNode failed for " + ref, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @GET
  @Path("move")
  public TaxonomyNode moveNode(@QueryParam(SITE) String siteId,
                               @QueryParam(TAXONOMY_ID) String taxonomyId,
                               @QueryParam(NODE_REF) String ref,
                               @QueryParam(TARGET_NODE_REF) String targetRef) {
    LOG.debug("move node called for " + TAXONOMY_ID + "='" + taxonomyId + "', " + NODE_REF + "='" + ref + "'"); //NOSONAR
    try {
      Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
      TaxonomyNode node = strategy.getNodeByRef(ref);
      TaxonomyNode targetNode = strategy.getNodeByRef(targetRef);
      return strategy.moveNode(node, targetNode);
    } catch (Exception e) {
      LOG.error("move node failed for " + ref, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @GET
  @Path("path")
  public TaxonomyNode getPath(@QueryParam(SITE) String siteId,
                              @QueryParam(TAXONOMY_ID) String taxonomyId,
                              @QueryParam(NODE_REF) String ref) {
    LOG.debug("path called for " + TAXONOMY_ID + "='" + taxonomyId + "', " + NODE_REF + "='" + ref + "'"); //NOSONAR
    try {
      if (taxonomyId == null) {
        LOG.warn("path called without taxonomyId!");
        return null;
      }
      Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
      if (strategy != null) {
        TaxonomyNode node = strategy.getNodeByRef(ref);
        return strategy.getPath(node);
      }
    } catch (Exception e) {
      LOG.error("getPath failed for " + ref, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
    //return empty node if the node is not readable
    return new TaxonomyNode();
  }

  @GET
  @Path("children")
  public TaxonomyNodeList getChildren(@QueryParam(SITE) String siteId,
                                      @QueryParam(TAXONOMY_ID) String taxonomyId,
                                      @QueryParam(NODE_REF) String ref,
                                      @QueryParam(OFFSET) Integer offset,
                                      @QueryParam(LENGTH) Integer length) {
    LOG.debug("children called for " + TAXONOMY_ID + "='" + taxonomyId + "', " + NODE_REF + "='" + ref + "'");  //NOSONAR
    try {
      if (taxonomyId == null) {
        LOG.warn("children called without taxonomyId!");
        return null;
      }
      Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
      //can happen when the taxonomy root node has been deleted but the manager is still open
      if (strategy != null) {
        TaxonomyNode node = (ref == null)
                ? strategy.getRoot()
                : strategy.getNodeByRef(ref);
        TaxonomyNodeList children = strategy.getChildren(node, (offset == null) ? 0 : offset, (length == null) ? -1 : length);
        children.sortByName();
        return children;
      }
      return new TaxonomyNodeList();
    } catch (Exception e) {
      LOG.error("getChildren failed for " + ref, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @GET
  @Path("createChild")
  public TaxonomyNode createChild(@QueryParam(SITE) String siteId,
                                  @QueryParam(TAXONOMY_ID) String taxonomyId,
                                  @QueryParam(NODE_REF) String ref,
                                  @QueryParam(DEFAULT_NAME) String defaultName) {
    LOG.debug("createChild called for " + TAXONOMY_ID + "='" + taxonomyId + "', " + NODE_REF + "='" + ref + "'"); //NOSONAR
    Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
    TaxonomyNode node = (ref == null)
            ? strategy.getRoot()
            : strategy.getNodeByRef(ref);

    TaxonomyNode newChild = strategy.createChild(node, defaultName);
    if (node.isRoot()) {
      waitUntilSearchable(newChild);
    }
    return newChild;
  }

  @GET
  @Path("delete")
  public TaxonomyNode delete(@QueryParam(SITE) String siteId,
                             @QueryParam(TAXONOMY_ID) String taxonomyId,
                             @QueryParam(NODE_REF) String ref) {
    LOG.debug("Delete called for " + TAXONOMY_ID + "='" + taxonomyId + "', " + NODE_REF + "='" + ref + "'");  //NOSONAR
    try {
      Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
      TaxonomyNode node = (ref == null)
              ? strategy.getRoot()
              : strategy.getNodeByRef(ref);

      return strategy.delete(node);
    } catch (Exception e) {
      LOG.error("delete failed for " + ref, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  @GET
  @Path("commit")
  public TaxonomyNode commit(@QueryParam(SITE) String siteId,
                             @QueryParam(TAXONOMY_ID) String taxonomyId,
                             @QueryParam(NODE_REF) String ref) {
    LOG.debug("commit called for " + TAXONOMY_ID + "='" + taxonomyId + "', " + NODE_REF + "='" + ref + "'");   //NOSONAR
    try {
      Taxonomy strategy = getTaxonomy(siteId, taxonomyId);
      TaxonomyNode node = (ref == null)
              ? strategy.getRoot()
              : strategy.getNodeByRef(ref);

      return strategy.commit(node);
    } catch (Exception e) {
      LOG.error("commit failed for {}", ref, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }


  @GET
  @Path("suggestions")
  public TaxonomyNodeList suggestions(@QueryParam(SITE) String siteId,
                                      @QueryParam(TAXONOMY_ID) String taxonomyId,
                                      @QueryParam("semanticStrategyId") String semanticStrategyId,
                                      @QueryParam(ID) String id,
                                      @QueryParam(MAX) int max) {
    LOG.debug("suggestions called for id '{}'", id);
    TaxonomyNodeList list = new TaxonomyNodeList(new ArrayList<TaxonomyNode>());
    try {
      Taxonomy taxonomyStrategy = getTaxonomy(siteId, taxonomyId);
      if (taxonomyStrategy != null && semanticStrategyId != null) {
        SemanticStrategy semanticStrategy = semanticStrategyById.get(semanticStrategyId.toLowerCase()); //NOSONAR
        //the strategy may have been disabled
        if(semanticStrategy != null) {
          Suggestions suggestions = semanticStrategy.suggestions(taxonomyStrategy, id);
          List<Suggestion> result = suggestions.asList(max);
          for (Suggestion match : result) {
            String restId = TaxonomyUtil.getRestIdFromCapId(match.getId());
            TaxonomyNode hit = taxonomyStrategy.getNodeByRef(restId);
            TaxonomyNodeList nodeList = taxonomyStrategy.getPath(hit).getPath();
            hit.setPath(nodeList);
            hit.setWeight(match.getWeight());
            list.getNodes().add(hit);
          }
        }
        else {
          LOG.warn("Semantic strategy '" + semanticStrategyId + "' not found, returning empty suggestion list.");
        }
      }

    } catch (Exception e) {
      LOG.error("suggestions failed for " + semanticStrategyId + "/" + id, e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    }
    return list;
  }

  // === Helper ===

  /**
   * Finds the taxonomy strategy for the given taxonomy id and site.
   */
  private Taxonomy getTaxonomy(String siteId, String taxonomyId) {
    Taxonomy taxonomyStrategy = strategyResolver.getTaxonomy(siteId, taxonomyId);
    if (taxonomyStrategy == null) {
      LOG.debug("No taxonomy strategy found for site id '" + siteId + "' and taxonomy id '" + taxonomyId + "', " +
              "or taxonomy is not readable.");
    }
    return taxonomyStrategy;
  }

  /**
   * Returns only those strategies that are searchable during the admin view.
   *
   * @param siteId Then id of the site to filter the taxonomies or null.
   * @return The ITaxonomy instance that will be shown in the administration console.
   */
  private Collection<Taxonomy> getTaxonomiesForAdministration(String siteId) {
    List<Taxonomy> result = new ArrayList<>();
    for (Taxonomy taxonomy : strategyResolver.getTaxonomies()) {
      if ((siteId == null || taxonomy.getSiteId() == null || taxonomy.getSiteId().equals(siteId)) && taxonomy.isValid()) {
        result.add(taxonomy);
      }
    }
    return result;
  }

  /**
   * Waits until the given node is searchable.
   *
   * @param node The node to wait for.
   */
  private void waitUntilSearchable(TaxonomyNode node) {
    TaxonomyNode root = getRoot(node.getSiteId(), node.getTaxonomyId());
    int attempts = 0;
    TaxonomyNodeList list = getChildren(node.getSiteId(), node.getTaxonomyId(), root.getRef(), null, null);
    while (!list.contains(node)) {
      list = getChildren(node.getSiteId(), node.getTaxonomyId(), root.getRef(), null, null);
      try {
        // These numbers are not "magic"
        Thread.sleep(500);  //NOSONAR
        attempts++;
        if (attempts == 20) {  //NOSONAR
          break;
        }
      } catch (InterruptedException e) {
        LOG.error("waiting for child node to be searchable failed: " + e.getMessage());
      }
    }
  }

  // === Dependency Injection ===

  @Required
  public void setStrategyResolver(TaxonomyResolver strategyResolver) {
    this.strategyResolver = strategyResolver;
  }

  @Required
  public void setSemanticStrategies(List<SemanticStrategy> semanticStrategies) {
    this.semanticStrategies = semanticStrategies;
  }

  @Override
  public void afterPropertiesSet() {
    for (SemanticStrategy strategy : semanticStrategies) {
      semanticStrategyById.put(strategy.getServiceId().toLowerCase(), strategy); //NOSONAR
    }
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }


  //  === Helper ===
  private List<String> getGroupsFromStruct(Map<String, Object> localStructs) {
    if (localStructs != null) {
      List<String> groups = (List) localStructs.get("administrationGroups"); //NOSONAR
      if (groups != null) {
        return groups;
      }
    }
    return Collections.emptyList();
  }
}
