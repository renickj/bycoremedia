package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import com.coremedia.livecontext.fragment.links.context.accessors.LiveContextContextAccessor;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;

/**
 * Suitable for URLs whose second segment denotes the store, e.g. /fragment/10001/...
 */
public class FragmentCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  private LiveContextContextAccessor fragmentContextAccessor;
  private LiveContextSiteResolver liveContextSiteResolver;
  private boolean contractsProcessingEnabled = true;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    setFragmentContext(request);
    return super.preHandle(request, response, handler);
  }

  @Override
  protected void initStoreContext(Site site, HttpServletRequest request) {
    super.initStoreContext(site, request);
    if (isCommerceContextAvailable()) {
      StoreContext storeContext = Commerce.getCurrentConnection().getStoreContext();
      Context fragmentContext = LiveContextContextHelper.fetchContext(request);
      if (fragmentContext != null) {
        if (isPreview()) {
          String memberGroups = (String) fragmentContext.get("wc.preview.memberGroups");
          if (memberGroups != null) {
            storeContext.setUserSegments(memberGroups);
          }
          boolean previewMode = Boolean.valueOf(fragmentContext.get("wc.p13n_test") + "");
          boolean timeIsElapsing = Boolean.valueOf(fragmentContext.get("wc.preview.timeiselapsing") + "");
          // are we in a studio preview call?
          request.setAttribute(PreviewHandler.REQUEST_ATTR_IS_STUDIO_PREVIEW, timeIsElapsing || previewMode);
          if (!timeIsElapsing) {
            String timestamp = (String) fragmentContext.get("wc.preview.timestamp");
            if (timestamp != null) {
              Timestamp ts = Timestamp.valueOf(timestamp);
              long millis = roundToMinute(ts.getTime());

              Calendar cal = Calendar.getInstance();
              cal.setTimeInMillis(millis);

              String timezone = (String) fragmentContext.get("wc.preview.timezone");
              if (timezone != null) {
                TimeZone tz = TimeZone.getTimeZone(timezone);
                cal.setTimeZone(tz);
              }

              String previewDate = convertToPreviewDateRequestParameterFormat(cal);
              storeContext.setPreviewDate(previewDate);
              request.setAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE, cal);
            }
          }
          String workspaceId = (String) fragmentContext.get("wc.preview.workspaceId");
          if (workspaceId != null) {
            storeContext.setWorkspaceId(workspaceId);
          }
        }
      }
    }
  }

  @Override
  protected void initUserContext(HttpServletRequest request) {
    if (!isUserContextInitialized(request)) {
      super.initUserContext(request);
      if (Commerce.getCurrentConnection().getUserContext() != null) {
        Context fragmentContext = LiveContextContextHelper.fetchContext(request);
        if (fragmentContext != null) {
          UserContext userContext = Commerce.getCurrentConnection().getUserContext();
          if (userContext != null) {
            String userId = (String) fragmentContext.get("wc.user.id");
            if (userId != null) {
              userContext.setUserId(userId);
            }
            String userName = (String) fragmentContext.get("wc.user.loginid");
            if (userName != null) {
              userContext.setUserName(userName);
            }
            if (contractsProcessingEnabled) {
              String contractIdsStr = (String) fragmentContext.get("wc.preview.contractIds");
              if (contractIdsStr != null && !contractIdsStr.isEmpty()) {
                String[] contractIdsFromContext = contractIdsStr.split(" ");
                if (contractIdsFromContext.length > 0) {
                  //check if user is allowed to execute a call for the passed contracts
                  ContractService contractService = Commerce.getCurrentConnection().getContractService();
                  StoreContext storeContext = Commerce.getCurrentConnection().getStoreContext();
                  if (contractService != null && storeContext != null) {
                    Collection<Contract> contractsForUser = contractService.findContractIdsForUser(
                            Commerce.getCurrentConnection().getUserContext(), storeContext);
                    if (contractsForUser != null) {
                      Collection<String> contractIdsForUser = new ArrayList<>();
                      for (Contract contract : contractsForUser) {
                        contractIdsForUser.add(contract.getExternalTechId());
                      }
                      Collection<String> intersection = CollectionUtils.intersection(contractIdsForUser,
                              new ArrayList<>(Arrays.asList(contractIdsFromContext)));
                      if (intersection.size() > 0) {
                        storeContext.setContractIds(Arrays.copyOf(intersection.toArray(), intersection.size(), String[].class));
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  protected void setFragmentContext(HttpServletRequest request) {
    //apply the absolute URL flag for fragment requests
    request.setAttribute(ABSOLUTE_URI_KEY, true);

    fragmentContextAccessor.openAccessToContext(request);
  }

  @Override
  @Nullable
  protected Site getSite(HttpServletRequest request, String normalizedPath) {
    FragmentParameters parameters = FragmentContextProvider.getFragmentContext(request).getParameters();
    return liveContextSiteResolver.findSiteFor(parameters);
  }

  @Required
  public void setFragmentContextAccessor(LiveContextContextAccessor fragmentContextAccessor) {
    this.fragmentContextAccessor = fragmentContextAccessor;
  }

  @Required
  public void setLiveContextSiteResolver(LiveContextSiteResolver liveContextSiteResolver) {
    this.liveContextSiteResolver = liveContextSiteResolver;
  }

  @Override
  public SiteResolver getSiteResolver() {
    return liveContextSiteResolver;
  }

  public void setContractsProcessingEnabled(boolean contractsProcessingEnabled) {
    this.contractsProcessingEnabled = contractsProcessingEnabled;
  }

  static String convertToPreviewDateRequestParameterFormat(Calendar calendar) {
    String result = null;
    if (calendar != null) {
      Long ms = calendar.getTimeInMillis();
      SimpleDateFormat sdb = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//      sdb.setTimeZone(calendar.getTimeZone());
      result = sdb.format(ms) + " " + calendar.getTimeZone().getID();
    }
    return result;
  }

  private static long roundToMinute(long currentTimeMillis) {
    long now = currentTimeMillis;
    long MILLISECONDS_PER_MINUTE = 60 * 1000L;
    long msRem = now % MILLISECONDS_PER_MINUTE;
    if (msRem < MILLISECONDS_PER_MINUTE/2) now = now - msRem; else now = now - msRem + MILLISECONDS_PER_MINUTE;
    return now;
  }

}
