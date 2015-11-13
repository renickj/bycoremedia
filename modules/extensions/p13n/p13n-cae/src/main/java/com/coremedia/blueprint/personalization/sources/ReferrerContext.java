package com.coremedia.blueprint.personalization.sources;

import com.coremedia.personalization.context.CoDecException;
import com.coremedia.personalization.context.ContextCoDec;
import com.coremedia.personalization.context.DirtyFlagMaintainer;
import com.coremedia.personalization.context.PropertyProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferrerContext implements PropertyProvider, DirtyFlagMaintainer {
  private static final Logger LOG = LoggerFactory.getLogger(ReferrerContext.class);

  private static final String URL_PROP = "url";
  private static final String SEARCHENGINE_PROP = "searchengine";
  private static final String QUERY_PROP = "query";

  private static final Pattern GOOGLE_REGEXP = Pattern.compile("^http://(\\w+\\.)+google.*(\\?|&)q=([^&]+).*");
  private static final Pattern BING_REGEXP = Pattern.compile("^http://(\\w+\\.)+bing.*(\\?|&)q=([^&]+).*");
  private static final Pattern YAHOO_REGEXP = Pattern.compile("^http://(\\w+\\.)+yahoo.*(\\?|&)p=([^&]+).*");
  private static final int REGEXP_URL_POSITION = 3;
  
  private boolean isDirty = true;
  private final Map<String, String> referrers = new HashMap<>();

  /**
   * Encodes/decodes ScoringContexts from and to Strings.
   */
  public static final class CoDec implements ContextCoDec {
    private final ObjectMapper objectMapper;

    /**
     * Initialize new Objectmapper on instantiation
     */
    public CoDec() {
      this.objectMapper = new ObjectMapper();
    }

    @Override
    public Object contextFromString(final String str) {
      if (str == null) {
        throw new IllegalArgumentException("supplied str must not be null");
      }

      try {
        final Map props = objectMapper.readValue(str, Map.class);
        final ReferrerContext context = (ReferrerContext) createNewContext();
        context.referrers.putAll(props);
        context.isDirty = false;
        return context;
      } catch (final IOException ex) {
        throw new CoDecException("unable to decode context", ex);
      }
    }


    @Override
    public String stringFromContext(final Object context) {
      if (!(context instanceof ReferrerContext)) {
        throw new IllegalArgumentException("supplied context is not of required type ScoringContext: " + context);
      }
      try {
        final ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        final HttpServletRequest request = attr.getRequest();

        ReferrerContext ctx = (ReferrerContext)context;
        final String referer = request.getHeader("referer");
        if (referer != null) {
          ctx.referrers.put(URL_PROP, referer);
          if (!addGoogleProperties(ctx.referrers, referer) && !addBingProperties(ctx.referrers, referer)) {
            addYahooProperties(ctx.referrers, referer);
          }
        }
        return objectMapper.writeValueAsString(((ReferrerContext) context).referrers);
      } catch (final Exception ex) { // NOSONAR
        throw new CoDecException("unable to encode context", ex);
      }
    }

    private boolean addGoogleProperties(final Map<String,String> context, final String referer) {
      final Matcher match = GOOGLE_REGEXP.matcher(referer);
      if (match.matches()) {
        context.put(SEARCHENGINE_PROP, "google");
        context.put(QUERY_PROP, urlDecode(match.group(REGEXP_URL_POSITION)));
        return true;
      } else {
        return false;
      }
    }

    private boolean addBingProperties(final Map<String,String> context, final String referer) {
      final Matcher match = BING_REGEXP.matcher(referer);
      if (match.matches()) {
        context.put(SEARCHENGINE_PROP, "bing");
        context.put(QUERY_PROP, urlDecode(match.group(REGEXP_URL_POSITION)));
        return true;
      } else {
        return false;
      }
    }

    private boolean addYahooProperties(final Map<String,String> context, final String referer) {
      final Matcher match = YAHOO_REGEXP.matcher(referer);
      if (match.matches()) {
        context.put(SEARCHENGINE_PROP, "yahoo");
        context.put(QUERY_PROP, urlDecode(match.group(REGEXP_URL_POSITION)));
        return true;
      } else {
        return false;
      }
    }

    private String urlDecode(final String str) {
      assert (str != null);
      try {
        return URLDecoder.decode(str, "UTF-8");
      } catch (final UnsupportedEncodingException ex) {
        LOG.error("UTF-8 encoding not supported! Are you kidding me?!?", ex);
        return null;
      }
    }

    /**
     * Returns a human-readable representation of the state of this object. The format may change without notice.
     *
     * @return human-readable representation of this object
     */
    @Override
    public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append('[').append(getClass().getName()).
              append(']');
      return builder.toString();
    }

    @Override
    public Object createNewContext() {
      return new ReferrerContext();
    }
  }

  public boolean isEmpty() {
    return referrers.isEmpty();
  }

  @Override
  public boolean isDirty() {
    return isDirty;
  }

  @Override
  public void setDirty(boolean value) {
    this.isDirty = value;
  }

  @Override
  public Object getProperty(String key) {
    return referrers.get(key);
  }

  @Override
  public <T> T getProperty(String key, T defaultValue) {
    final T score = (T) referrers.get(key);
    return score != null ? score : defaultValue;
  }

  @Override
  public Collection<String> getPropertyNames() {
    return referrers.keySet();  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Returns a human-readable representation of the state of this object. The format may change without notice.
   *
   * @return human-readable representation of this object
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append('[').append(getClass().getName()).
            append(']');
    return builder.toString();
  }
}
