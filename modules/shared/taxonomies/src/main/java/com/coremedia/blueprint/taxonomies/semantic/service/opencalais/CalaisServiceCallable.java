package com.coremedia.blueprint.taxonomies.semantic.service.opencalais;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Callable for OpenCalais
 */
public class CalaisServiceCallable implements Callable<Map<String, Object>> {
  private static final Log LOG = LogFactory.getLog(CalaisServiceCallable.class);

  private String data;
  private CalaisClient client;

  public CalaisServiceCallable(String data, CalaisClient client) {
    this.data = data;
    this.client = client;
  }

  @Override
  public Map<String, Object> call() {
    try {
      return client.analyze(data);
    } catch (Exception fe) {
      String msg = fe.getMessage();
      if(msg.contains("supported languages")) {
        //Open Calais only supports "English, French, and Spanish"
        LOG.info("Analyzing packet via Open Calais failed because the language is not supported by Open Calais. ('" + fe.getMessage() + "')");
      }
      else {
        LOG.error("Error analyzing packet via Open Calais: " + fe.getMessage(), fe);
      }

    }
    return null;
  }
}
