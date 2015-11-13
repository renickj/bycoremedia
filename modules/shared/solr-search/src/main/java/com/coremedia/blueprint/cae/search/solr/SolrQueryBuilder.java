package com.coremedia.blueprint.cae.search.solr;

import com.coremedia.blueprint.cae.search.SearchQueryBean;
import org.apache.solr.client.solrj.SolrQuery;

/**
 * A SolrQueryBuilder is responsible for creating Solrj specific {@link SolrQuery} objects from generic
 * {@link com.coremedia.blueprint.cae.search.SearchQueryBean}s. Different implementations may want to interpret the {@link SearchQueryBean}
 * in different ways.
 */
public interface SolrQueryBuilder {

  String SORT_ORDER_ASC = "ASC";
  String SORT_ORDER_DESC = "DESC";

  String ANY_VALUE = "*";
  String OPENING_BRACKET = "[";
  String CLOSING_BRACKET = "]";
  String ANY_FIELD_ANY_VALUE = ANY_VALUE + ":" + ANY_VALUE;
  String ANY_VALUE_TO = ANY_VALUE + " TO ";
  String TO_ANY_VALUE = " TO " + ANY_VALUE;

  // Solr operators for con- and disjunction
  String AND = " AND ";
  String OR = " OR ";

  SolrQuery buildQuery(SearchQueryBean input);

  boolean isPreview();

}
