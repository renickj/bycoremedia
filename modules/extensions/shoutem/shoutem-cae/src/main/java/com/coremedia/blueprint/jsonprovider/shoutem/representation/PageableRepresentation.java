package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import com.coremedia.blueprint.jsonprovider.shoutem.ShoutemApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation that supports paging.
 */
public class PageableRepresentation {
  private List<Object> data = new ArrayList<>();

  private Paging paging;

  public void addItem(Object item) {
    data.add(item);
  }

  public List<Object> getData() {
    return data;
  }

  public Paging getPaging() {
    return paging;
  }

  /**
   * Applies the limit for the given method and offset.
   */
  public void filter(String sessionId, String methodName, Integer offset, Integer limit) {
    filter(sessionId, methodName, null, null, offset, limit);
  }

  /**
   * Applies the limit for the given method and offset.
   */
  public void filter(String sessionId, String methodName, String paramName,
                     String paramValue, Integer offset, Integer limit) {
    paging = new Paging(data, sessionId, methodName, paramName, paramValue, offset, limit);
    filterOffset(offset, limit);
  }

  /**
   * Creates the subset that is displayed for the current page.
   * @param offset The offset of the result.
   * @param limit The limit to display.
   */
  private void filterOffset(Integer offset, Integer limit) {
    if(offset == null || offset < 0) {
      offset = 0;//NOSONAR
    }
    if(limit == null || limit <= 0) {
      limit = ShoutemApi.DEFAULT_LIMIT;//NOSONAR
    }

    if(offset >= 0 && data.size() > offset) {
      data = data.subList(offset, data.size());
      if(data.size() > limit) {
        data = data.subList(0, limit);
      }
    }
  }
}
