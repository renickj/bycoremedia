package com.coremedia.livecontext.asset.license;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.ServerControl;
import com.coremedia.cap.common.infos.CapLicenseInfo;
import com.coremedia.cap.content.ContentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetManagementLicenseInspectorTest {
  @InjectMocks
  private AssetManagementLicenseInspector licenseInspector = new AssetManagementLicenseInspector();

  @Mock
  private CapConnection capConnection;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ServerControl serverControl;

  @Mock
  private CapLicenseInfo capLicenseInfo;

  @Before
  public void setUp() {
    when(capConnection.getContentRepository()).thenReturn(contentRepository);
    when(contentRepository.isContentManagementServer()).thenReturn(true);
    when(capConnection.getServerControl()).thenReturn(serverControl);
    when(serverControl.getLicenseInformation()).thenReturn(capLicenseInfo);
  }

  @Test
  public void licenseEnabledTest() {
    when(capLicenseInfo.isEnabled(AssetManagementLicenseInspector.LICENSE_KEY_LC_ASSET_MANAGEMENT)).thenReturn(true);
    licenseInspector.checkLicense();
    assertTrue(licenseInspector.isLicensed());
  }

  @Test
  public void licenseDisabledTest() {
    when(capLicenseInfo.isEnabled(AssetManagementLicenseInspector.LICENSE_KEY_LC_ASSET_MANAGEMENT)).thenReturn(false);
    licenseInspector.checkLicense();
    assertFalse(licenseInspector.isLicensed());
  }

  @Test
  public void isConnectedToPreviewTest() {
    when(contentRepository.isContentManagementServer()).thenReturn(false);
    licenseInspector.checkLicense();
    assertFalse(licenseInspector.isConnectedToPreview());
  }

  @Test
  public void isActiveTestTrue() {
    when(contentRepository.isContentManagementServer()).thenReturn(false);
    when(capLicenseInfo.isEnabled(AssetManagementLicenseInspector.LICENSE_KEY_LC_ASSET_MANAGEMENT)).thenReturn(true);
    licenseInspector.checkLicense();
    assertTrue(licenseInspector.isFeatureActive());
  }

  @Test
  public void isActiveTestFalse() {
    when(contentRepository.isContentManagementServer()).thenReturn(false);
    when(capLicenseInfo.isEnabled(AssetManagementLicenseInspector.LICENSE_KEY_LC_ASSET_MANAGEMENT)).thenReturn(false);
    licenseInspector.checkLicense();
    assertFalse(licenseInspector.isFeatureActive());
  }
}