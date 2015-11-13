package com.coremedia.livecontext.asset.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Named("lcAssetManagementLicenseInterceptor")
public class AssetManagementLicenseInterceptor extends HandlerInterceptorAdapter {
  private static final Logger LOG = LoggerFactory.getLogger(AssetManagementLicenseInterceptor.class);

  @Inject
  @Named("lcAssetManagementLicenseInspector")
  private AssetManagementLicenseInspector licenseInspector;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (!licenseInspector.isLicensed()) {
      LOG.warn("CoreMedia LiveContext Asset Management is not licensed!");
    }

    return true;
  }
}