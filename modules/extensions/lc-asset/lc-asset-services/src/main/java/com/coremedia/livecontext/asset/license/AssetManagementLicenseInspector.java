package com.coremedia.livecontext.asset.license;

import com.coremedia.cap.common.CapConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Named("lcAssetManagementLicenseInspector")
public class AssetManagementLicenseInspector {
  public static final String LICENSE_KEY_LC_ASSET_MANAGEMENT = "livecontext-asset-management";
  private static final Logger LOG = LoggerFactory.getLogger(AssetManagementLicenseInspector.class);

  @Inject
  @Named("connection")
  private CapConnection capConnection;
  private boolean licensed = false;
  private boolean preview = false;

  public AssetManagementLicenseInspector() {
  }

  @PostConstruct
  public void checkLicense() {
    if (this.capConnection != null) {
      this.licensed = this.capConnection.getServerControl()
              .getLicenseInformation().isEnabled(LICENSE_KEY_LC_ASSET_MANAGEMENT);

      this.preview =  capConnection.getContentRepository().isContentManagementServer();
    }

    if (!this.isLicensed()) {
      LOG.warn("CoreMedia LiveContext Asset Management is not licensed!");
    }

  }

  public boolean isLicensed() {
    return this.licensed;
  }

  public boolean isConnectedToPreview() {
    return this.preview;
  }

  public boolean isFeatureActive() {
    return isLicensed() || isConnectedToPreview() ;
  }
}