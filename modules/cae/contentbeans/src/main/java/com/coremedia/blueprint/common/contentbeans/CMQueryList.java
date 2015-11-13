package com.coremedia.blueprint.common.contentbeans;

/**
 * CMQueryList documents utilize a struct in the localSettings property to store
 * a query.
 *
 * <p>Represents the document type {@link #NAME CMQueryList}.</p>
 */
public interface CMQueryList extends CMDynamicList<CMLinkable> {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMQueryList'
   */
  String NAME = "CMQueryList";

  /**
   * Returns the value of the document property {@link #MASTER}
   *
   * @return the value of the document property {@link #MASTER}
   */
  @Override
  CMQueryList getMaster();
}
