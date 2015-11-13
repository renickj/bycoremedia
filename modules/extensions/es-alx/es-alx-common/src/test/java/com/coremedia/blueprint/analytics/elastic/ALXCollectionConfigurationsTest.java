package com.coremedia.blueprint.analytics.elastic;


import com.coremedia.elastic.core.api.models.CollectionConfiguration;
import com.coremedia.elastic.core.api.models.CollectionTTLConfiguration;
import com.coremedia.elastic.core.api.models.configuration.ModelCollectionConfigurationBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import static com.coremedia.blueprint.analytics.elastic.ReportModel.PROP_LAST_SAVED_DATE;
import static com.coremedia.elastic.core.api.models.ModelIndex.ModelIndexOptions.SPARSE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ALXCollectionConfigurationsTest {

  @InjectMocks
  private ALXCollectionConfigurations alxCollectionConfigurations = new ALXCollectionConfigurations();

  @Mock
  private ModelCollectionConfigurationBuilder builder;

  @Mock
  private CollectionTTLConfiguration collectionTTLConfiguration;

  @Test
  public void getCollectionConfigurations() {
    int timeToLive = 10;
    //noinspection unchecked
    when(builder.configureTTL(anyString(), anyString(), anyInt())).thenReturn(builder);
    when(builder.build()).thenReturn(Arrays.<CollectionConfiguration>asList(collectionTTLConfiguration));
    alxCollectionConfigurations.setExpireAfterSeconds(timeToLive);
    Collection<CollectionConfiguration> collectionTTLConfigurations = alxCollectionConfigurations.getCollectionConfigurations();
    assertEquals(1, collectionTTLConfigurations.size());
    assertEquals(collectionTTLConfiguration, collectionTTLConfigurations.iterator().next());
    verify(builder).configureTTL(TopNReportModelService.COLLECTION_NAME, PROP_LAST_SAVED_DATE, timeToLive);
    verify(builder).configureTTL(PageViewReportModelService.COLLECTION_NAME, PROP_LAST_SAVED_DATE, timeToLive);
    verify(builder).build();
  }
}