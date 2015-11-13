package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cae.aspect.provider.AspectsProvider;
import com.coremedia.objectserver.beans.ContentBean;

import java.util.List;
import java.util.Map;

/**
 * Root interface for all content beans.
 * <p/>
 * <p>Represents the document type {@link #NAME CMObject}.</p>
 */
public interface CMObject extends ContentBean {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMObject'.
   */
  String NAME = "CMObject";

  /**
   * returns the CoreMedia internal id of the underlying {@link com.coremedia.cap.content.Content}
   *
   * @return the id as int (without any prefix)
   */
  int getContentId();

  /**
   * Returns a list of all {@link com.coremedia.cae.aspect.Aspect} from all availiable
   * PlugIns that are registered to this contentbean.
   *
   * @return a list of Aspects
   */
  List<? extends Aspect<? extends CMObject>> getAspects();

  /**
   * Returns a Map from aspectIDs to Aspects. AspectIDs consists of an aspectname with a prefix which identifies
   * the plugin provider.
   *
   * @return a Map from aspectIDs to Aspects
   */
  Map<String, ? extends Aspect<? extends CMObject>> getAspectByName();

  /**
   * Provides access to the {@link com.coremedia.cae.aspect.provider.AspectsProvider} which then offers access several {@link Aspect}s for a bean
   *
   * @return the {@link com.coremedia.cae.aspect.provider.AspectsProvider}
   */
  AspectsProvider getAspectsProvider();
}
