package com.coremedia.blueprint.caefeeder;

import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.cap.feeder.bean.PropertyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TaxonomyPropertyConverter implements PropertyConverter {
  private static final Logger LOGGER = LoggerFactory.getLogger(TaxonomyPropertyConverter.class);

  private boolean ignoreParents;
  private TreePathKeyFactory taxonomyPathKeyFactory;

  @Override
  public Object convertValue(Object object) {
    if (object instanceof List) {
      @SuppressWarnings("unchecked")
      List<CMTaxonomy> taxonomies = (List<CMTaxonomy>) object;
      Set<String> tags = new TreeSet<>();
      if(ignoreParents) {
        for(CMTaxonomy taxonomy: taxonomies) {
          // obtain the path segment (id or value)
          String tag = taxonomyPathKeyFactory.getPathSegment(taxonomy.getContent());
          tags.add(tag);
        }
      } else {
        for(CMTaxonomy taxonomy: taxonomies) {
          // do the (recursive) fragment cache key lookup
          String path = taxonomyPathKeyFactory.getPath(taxonomy.getContent());
          // path starts with a slash and is separated by slashes. Ignore the empty token in the beginning.
          String[] strings = StringUtils.tokenizeToStringArray(path, "/", false, true);
          tags.addAll(Arrays.asList(strings));
        }
      }
      if(tags.isEmpty()){
        return null;
      }
      // convert the Set to a string like "2,10,6,4,8". order is irrelevant for solr.
      String convertedValue = StringUtils.collectionToDelimitedString(tags, ",", "", "");
      LOGGER.debug("Converted into: {}", convertedValue);
      return convertedValue;
    }
    return null;
  }

  @Override
  public Class<?> convertType(Class<?> type) {
    return String.class;
  }

  /**
   * In some use cases the parents of a taxonomy are not required. This can be achieved by setting the parameter to true
   * @param ignoreParents whether parents can be ignored, default is false
   */
  public void setIgnoreParents(boolean ignoreParents) {
    this.ignoreParents = ignoreParents;
  }

  public void setTaxonomyPathKeyFactory(TreePathKeyFactory taxonomyPathKeyFactory) {
    this.taxonomyPathKeyFactory = taxonomyPathKeyFactory;
  }
}
