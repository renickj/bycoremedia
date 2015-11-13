package com.coremedia.blueprint.personalization.preview;

import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.ServerControl;
import com.coremedia.cap.common.infos.CapLicenseInfo;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.collector.ContextCollector;
import com.coremedia.personalization.context.collector.LicenseHelper;
import com.coremedia.personalization.context.collector.SegmentSource;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class TestContextCollectorBean implements BeanPostProcessor {

  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if(beanName.contains("LicenseHelper")) {
      return createLicenseHelper();
    } else if(beanName.equals("segmentSource")) {
      ((SegmentSource)bean).setPathToSegments("/");
    }
    return bean;
  }

  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  private LicenseHelper createLicenseHelper() {
    final LicenseHelper licenseHelper = new LicenseHelper();
    final CapConnection mockConnection = Mockito.mock(CapConnection.class);
    final ServerControl serverControl = Mockito.mock(ServerControl.class);
    final CapLicenseInfo capLicenseInfo = Mockito.mock(CapLicenseInfo.class);
    final ContentRepository contentRepository = Mockito.mock(ContentRepository.class);
    Mockito.when(mockConnection.getServerControl()).thenReturn(serverControl);
    Mockito.when(serverControl.getLicenseInformation()).thenReturn(capLicenseInfo);
    Mockito.when(capLicenseInfo.isEnabled("personalization")).thenReturn(true);
    Mockito.when(mockConnection.getContentRepository()).thenReturn(contentRepository);
    Mockito.when(contentRepository.isContentManagementServer()).thenReturn(true);
    licenseHelper.setCapConnection(mockConnection);
    return licenseHelper;
  }

}
