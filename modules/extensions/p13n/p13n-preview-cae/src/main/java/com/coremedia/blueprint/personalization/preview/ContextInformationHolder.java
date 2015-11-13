package com.coremedia.blueprint.personalization.preview;

final class ContextInformationHolder {

  private String contextName;
  private String propertyName;
  private Object value;

  ContextInformationHolder(String contextName, String propertyName, Object value) {
    this.contextName = contextName;
    this.propertyName = propertyName;
    this.value = value;
  }

  public String getContextName() {
    return contextName;
  }

  public String getPropertyName() {
    return propertyName;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }
}
