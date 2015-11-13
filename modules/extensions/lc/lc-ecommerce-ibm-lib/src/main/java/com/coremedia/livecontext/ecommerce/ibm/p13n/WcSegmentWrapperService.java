package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.*;
import static java.util.Arrays.asList;

/**
 * A service that uses the rest connector to get segment information from IBM WebSphere Commerce.
 */
public class WcSegmentWrapperService extends AbstractWcWrapperService {

  private static final Logger LOG = LoggerFactory.getLogger(WcSegmentWrapperService.class);

  private static final WcRestConnector.WcRestServiceMethod<HashMap, Void>
    FIND_ALL_SEGMENTS = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/segment", true, true, HashMap.class);

  private static final WcRestConnector.WcRestServiceMethod<HashMap, Void>
    FIND_SEGMENT_BY_ID = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/segment/{id}", true, true, HashMap.class);

  private static final WcRestConnector.WcRestServiceMethod<HashMap, Void>
    FIND_SEGMENTS_BY_USER = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/segment?q=byUserId&qUserId={id}", true, true, HashMap.class);

  /**
   * Gets a list of all customer segments.
   *
   * @param storeContext the current store context
   * @param userContext the current user context
   * @return A map which contains the JSON response data retrieved from the commerce server
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> findAllSegments(final StoreContext storeContext, final UserContext userContext) {
    try {
      if (StoreContextHelper.getWcsVersion(storeContext) < StoreContextHelper.WCS_VERSION_7_7) {
        return Collections.emptyMap();
      }
      return getRestConnector().callService(
        FIND_ALL_SEGMENTS, asList(getStoreId(storeContext)),
        createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext)),
        null, storeContext, userContext);

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a segment map by a given external id.
   *
   * @param externalId   the external id
   * @param storeContext the store context
   * @param userContext the current user context
   * @return the segment map which contains the JSON response data retrieved from the commerce server or null if no spot was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> findSegmentByTechId(final String externalId, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      if (StoreContextHelper.getWcsVersion(storeContext) < StoreContextHelper.WCS_VERSION_7_7) {
        return null;
      }
      Map<String, Object> data = getRestConnector().callService(
        FIND_SEGMENT_BY_ID, asList(getStoreId(storeContext), externalId),
        createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext)),
        null, storeContext, userContext);
      if (data != null) {
        List<Map<String, Object>> memberGroups = DataMapHelper.getValueForPath(data, "MemberGroup", List.class);
        if (memberGroups != null && memberGroups.size() > 0) {
          Map<String, Object> firstSegment = memberGroups.get(0);
          String segmentId = DataMapHelper.getValueForPath(firstSegment, "id", String.class);
          if (segmentId != null && !segmentId.isEmpty()) {
            return firstSegment;
          }
        }
      }
      return null;

    } catch (CommerceException e) {
      //ibm returns 403 or 500 instead of 404 for a unknown segment id
      LOG.warn("CommerceException", e);
      return null;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a segment map by a given id (in a coremedia internal format like "ibm:///catalog/segment/081523924379243").
   *
   * @param id           the coremedia internal id
   * @param storeContext the store context
   * @param userContext the current user context
   * @return the segment map which contains the JSON response data retrieved from the commerce server or null if no segment was found
   * @throws com.coremedia.livecontext.ecommerce.common.CommerceException if something is wrong with the catalog connection
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidIdException if the id is in a wrong format
   */
  @SuppressWarnings("unused")
  public Map<String, Object> findSegmentById(final String id, final StoreContext storeContext, final UserContext userContext) throws CommerceException {
    try {
      String externalId = CommerceIdHelper.parseExternalIdFromId(id);
      return findSegmentByTechId(externalId, storeContext, userContext);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  /**
   * Gets a map of customer segments that is associated to the current context user.
   *
   * @param storeContext the current store context
   * @param userContext the current user context
   * @return A map which contains the JSON response data retrieved from the commerce server.
   */
  @SuppressWarnings("unchecked")
  public Map<String, Object> findSegmentsByUser(StoreContext storeContext, UserContext userContext) {
    try {
      if (StoreContextHelper.getWcsVersion(storeContext) < StoreContextHelper.WCS_VERSION_7_7) {
        return Collections.emptyMap();
      }
      Integer forUserId = UserContextHelper.getForUserId(userContext);
      if (forUserId == null) {
        return Collections.emptyMap();
      }
      Map<String, Object> data = getRestConnector().callService(
        FIND_SEGMENTS_BY_USER, asList(getStoreId(storeContext), forUserId+""),
        createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext)),
        null, storeContext, userContext);
      if (data != null) {
        return data;
      }
      return null;

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

}
