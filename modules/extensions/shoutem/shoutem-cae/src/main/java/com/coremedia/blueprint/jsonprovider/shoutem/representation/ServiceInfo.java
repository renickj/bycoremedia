package com.coremedia.blueprint.jsonprovider.shoutem.representation;

/**
 * Response of the service info.
 */
public class ServiceInfo {
  private static final int API_VERSION = 1;
  private static final String SERVER_TYPE = "coremedia";

  public String getServer_type() {// NOSONAR
    return SERVER_TYPE;
  }

  public int getApi_version() {// NOSONAR
    return API_VERSION;
  }
}
