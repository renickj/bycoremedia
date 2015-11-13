package com.coremedia.livecontext.ecommerce.ibm.common;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import java.util.Map;

public class ConnectorHelper {
  /**
   * Build an URIComponent by the given request template uri and it's parameter key/value pair.
   * All variables in the template uri will be replaced if a given key/value pair exists in the given map.
   *
   * @param requestTemplateUri uri with some variables which should be replaced. For example:
   *                           http://any.host.com/Login?storeid={storeid}
   * @param parameters map full with key value pairs, where keys must match the variable in template uri. For example:
   *                   In the example template uri above it must exist an entry in the map with the key "storeid".
   * @return An UriComponents object which can be used to retrieve an uri. This UriComponents is not encoded yet.
   */
  @Nonnull
  public UriComponents buildRequestUrl(@Nonnull String requestTemplateUri, @Nonnull Map<String, String> parameters) {
    return UriComponentsBuilder.fromUriString(requestTemplateUri).buildAndExpand(parameters);
  }
}
