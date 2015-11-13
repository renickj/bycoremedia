package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.cae.richtext.filter.ScriptSerializer;
import com.coremedia.blueprint.cae.view.processing.Minifier;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.xml.Markup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.XMLFilter;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

/**
 * {@link com.coremedia.cache.CacheKey} to compute processed code segments
 * Although some more Objects are required for processing, the only real dependency
 * is to the CoreMedia Richtext Markup containing the code.
 */
public class CodeCacheKey extends CacheKey<String> {

  private static final Logger LOG = LoggerFactory.getLogger(CodeCacheKey.class);

  private String name;
  private Markup code;
  private List<XMLFilter> filters;
  private Minifier minifier;

  /**
   * Standard Constructor.
   * @param code the code (coremedia richtext)
   * @param filters a set of filters, required for general processing (like removing the markup). should not be null.
   * @param minifier the postprocessor. can be null if and only if postProcessing is disabled.
   */
  public CodeCacheKey(Markup code, List<XMLFilter> filters, String name, Minifier minifier) {
    this.code = code;
    this.filters = filters;
    this.name = name;
    this.minifier = minifier;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CodeCacheKey key = (CodeCacheKey) o;
    return code.equals(key.code);
  }

  @Override
  public int hashCode() {
    return 31 * code.hashCode();
  }

  @Override
  public String evaluate(Cache cache) {

    //strip <div> and <p> from markup
    StringWriter writer = new StringWriter();
    ScriptSerializer handler = new ScriptSerializer(writer);
    code.writeOn(filters, handler);

    //====
    StringWriter resultStringWriter = new StringWriter();
    String noMarkupScript = writer.getBuffer().toString();
    String evaluation = "";
    try {
      minifier.minify(resultStringWriter, new StringReader(noMarkupScript), name);
      evaluation = resultStringWriter.getBuffer().toString();
    }
    catch (Exception e) {
      LOG.error("Could not minify file {}. Will write unminified version.", name, e);
      evaluation = noMarkupScript;
    }
    return evaluation;
  }

  public Markup getCode() {
    return code;
  }

  public List<XMLFilter> getFilters() {
    return filters;
  }

  public Minifier getMinifier() {
    return minifier;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[name=" + name + "]";
  }
}
