package com.coremedia.blueprint.analytics.elastic;


import com.coremedia.elastic.core.api.models.CollectionConfiguration;
import com.coremedia.elastic.core.api.models.configuration.ModelCollectionConfiguration;
import com.coremedia.elastic.core.api.models.configuration.ModelCollectionConfigurationBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

import static com.coremedia.blueprint.analytics.elastic.ReportModel.PROP_LAST_SAVED_DATE;

/**
 * Model collection configuration for analytics report models
 */
@Named
public class ALXCollectionConfigurations implements ModelCollectionConfiguration {

  private static final int DEFAULT_EXPIRE_AFTER_SECONDS = 30 * 24 * 60 * 60; //30 days

  private int expireAfterSeconds;

  @Inject
  private ModelCollectionConfigurationBuilder builder;

  @Override
  public Collection<CollectionConfiguration> getCollectionConfigurations() {
    return builder.
            configureTTL(TopNReportModelService.COLLECTION_NAME, PROP_LAST_SAVED_DATE, expireAfterSeconds).
            configureTTL(PageViewReportModelService.COLLECTION_NAME, PROP_LAST_SAVED_DATE, expireAfterSeconds).
            build();
  }

  @Value("${alxreports.expire.after.seconds:" + DEFAULT_EXPIRE_AFTER_SECONDS + "}")
  public void setExpireAfterSeconds(int expireAfterSeconds) {
    this.expireAfterSeconds = expireAfterSeconds;
  }
}
