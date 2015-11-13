package com.coremedia.livecontext.incubator.handler;

import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.cache.Cache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * TODO: Description
 * To change this template use File | Settings | File Templates.
 */
@RequestMapping
public class CommerceCacheInvalidationHandler extends HandlerBase {
  private static final String URI_PATTERN = "/cache";
  private Cache cache;

  @RequestMapping(value = URI_PATTERN)
  public ResponseEntity<String> invalidateSingleCacheEntry(@RequestParam(value = "key", required = false) String key) {
    invalidateKey(key);

    return new ResponseEntity<String>("success", HttpStatus.OK);
  }

  @RequestMapping(value = URI_PATTERN, method = RequestMethod.POST, headers = "Content-Type=text/plain")
  public ResponseEntity<String> invalidateMultipleCacheEntries(@RequestBody String keysJson) {
    String[] keys = null;
    if (keysJson != null) {
      keys =StringUtils.split(keysJson, ",");
    }

    if (keys != null) {
      for (String key : keys) {
        invalidateKey(key);
      }
    }

    return new ResponseEntity<String>("success", HttpStatus.OK);
  }

  private void invalidateKey(String key) {
    if (key != null && !key.trim().isEmpty()) {
      cache.invalidate(key.trim());
    }
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }
}
