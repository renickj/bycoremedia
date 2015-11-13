package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.List;
import java.util.Map;

/**
 * CMViewtype symbols are used to represent viewtypes of documents.
 *
 * <p>Represents the document type {@link #NAME CMViewtype}.</p>
 */
public interface CMViewtype extends CMSymbol {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMViewtype'
   */
  String NAME = "CMViewtype";

  @Override
  Map<String, ? extends Aspect<? extends CMViewtype>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMViewtype>> getAspects();

  /**
   * Name of the document property 'layout'.
   */
  String LAYOUT = "layout";

  /**
   * Returns the value of the document property {@link #LAYOUT}.
   *
   * @return the value of the document property {@link #LAYOUT}
   */
  String getLayout();
}
  