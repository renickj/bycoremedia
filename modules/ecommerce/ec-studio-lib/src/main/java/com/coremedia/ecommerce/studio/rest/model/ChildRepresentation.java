package com.coremedia.ecommerce.studio.rest.model;

/**
 *
 */
public class ChildRepresentation {
    private String displayName;
    private Object child;

    public ChildRepresentation(){
    }


    public String getDisplayName() {
      return displayName;
    }

    public Object getChild() {
      return child;
    }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setChild(Object child) {
    this.child = child;
  }
}
