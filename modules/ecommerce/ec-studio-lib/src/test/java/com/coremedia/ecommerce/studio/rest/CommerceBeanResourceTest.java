package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.rest.cap.content.ContentRepositoryResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceBeanResourceTest {

  @Test
  public void computePreviewUrl() {
    //noinspection unchecked
    CommerceBeanResource commerceBeanResource = mock(CommerceBeanResource.class);
    ContentRepositoryResource contentRepositoryResource = mock(ContentRepositoryResource.class);
    when(contentRepositoryResource.getPreviewControllerUrlPattern()).thenReturn("a={0}&b={1}");
    when(commerceBeanResource.getContentRepositoryResource()).thenReturn(contentRepositoryResource);
    CommerceBean commerceBean = mock(CommerceBean.class);
    when(commerceBeanResource.getEntity()).thenReturn(commerceBean);
    when(commerceBean.getId()).thenReturn("_object_ID_");
    when(commerceBeanResource.getSiteId()).thenReturn("_site_ID_");

    when(commerceBeanResource.computePreviewUrl()).thenCallRealMethod();
    Assert.assertEquals("a=_object_ID_&site=_site_ID_&b={1}", commerceBeanResource.computePreviewUrl());
  }

}