package com.boots.cms.by.caefeeder;

import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.cap.feeder.bean.PropertyConverter;
import com.coremedia.xml.PlaintextSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link PropertyConverter} for the property {@link com.coremedia.blueprint.common.contentbeans.CMLinkable#getContexts()}
 * which takes the related documents and writes their teaserTitle and teaserText to a result collection.
 */
public class RelatedPropertyConverter implements PropertyConverter {

  private static final Log LOG = LogFactory.getLog(RelatedPropertyConverter.class);


  @Override
  public List<String> convertValue(Object value) {


    if (value == null) {
      return Collections.emptyList();
    }


    List<CMTeasable> related = (List<CMTeasable>)value;

    List<String> result = new ArrayList<>(related.size()*2);



    for (CMTeasable cmTeasable : related) {
      result.add(cmTeasable.getTeaserTitle());
      StringWriter w = new StringWriter();
      PlaintextSerializer pst = new PlaintextSerializer();
      pst.setTarget(w);
      cmTeasable.getTeaserText().writeOn(pst);
      result.add(w.toString());
    }
    return result;
  }



  @Override
  public Class<List> convertType(Class type) {
    return List.class;
  }



}
