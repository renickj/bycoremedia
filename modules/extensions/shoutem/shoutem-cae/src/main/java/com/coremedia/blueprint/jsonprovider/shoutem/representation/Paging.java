package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import com.coremedia.blueprint.jsonprovider.shoutem.ShoutemApi;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Contains the previous and next link for paging through results.
 */
public class Paging {
  private String next;
  private String prev;
  
  public Paging(List<Object> data, String sessionId, String method, Integer offset, Integer limit) {
    this(data, sessionId, method, null, null, offset, limit);
  }

  public Paging(List<Object> data, String sessionId, String method, String paramName, String paramValue,
                Integer offset, Integer limit) {
    Integer pagingOffset = offset;
    if(pagingOffset == null || pagingOffset < 0) {
      pagingOffset = 0;
    }
    if(limit == null || limit <= 0) {
      limit = ShoutemApi.DEFAULT_LIMIT; //NOSONAR
    }

    String link = "method=" + method;
    if(sessionId != null) {
      link+="&session_id=" + sessionId;
    }

    if(paramName != null) {
      link+="&" + paramName + "=" + paramValue;
    }

    link+="&limit=" + limit;

    if(pagingOffset > 0) {
      int prevOffset = pagingOffset-limit;
      if(prevOffset < 0 ) {
        prevOffset = 0;
      }
      prev = link + "&offset=" + prevOffset;
    }

    if(data.size() > (pagingOffset+limit)) {
      int nextOffset = (pagingOffset+limit);
      next = link + "&offset=" + nextOffset;
    }
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getPrev() {
    return prev;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getNext() {
    return next;
  }
}
