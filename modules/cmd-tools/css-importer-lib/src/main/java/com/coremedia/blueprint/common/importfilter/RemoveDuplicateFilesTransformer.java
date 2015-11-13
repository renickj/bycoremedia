package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.AbstractTransformer;
import com.coremedia.publisher.importer.MultiResult;
import com.coremedia.publisher.importer.MultiSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Not needed any longer.
 */
@Deprecated
public class RemoveDuplicateFilesTransformer extends AbstractTransformer {
  private static final Log LOG = LogFactory.getLog(RemoveDuplicateFilesTransformer.class);


  @Override
  public void transform(Source source, Result outputTarget) throws TransformerException {
    if (!(source instanceof MultiSource)) {
      throw new TransformerException("Source " + source + " is not a MultiSource");
    }
    MultiSource multiSource = (MultiSource) source;
    MultiResult result = (MultiResult) outputTarget; //NOSONAR
    List<String> systemIds = new ArrayList<>();


    try {
      multiSource = multiSource.flattened();
      LOG.debug("Started with " + multiSource.size() + " files in the result list");
      for (int i = 0; i < multiSource.size(); i++) {
        String systemId = multiSource.getSource(i, StreamSource.FEATURE).getSystemId();
        if (systemIds.contains(systemId)) {
          LOG.debug("Already added file " + systemId);
        } else {
          result.addNewResult(systemId);
          systemIds.add(systemId);
        }
        LOG.debug("Ended with " + result.size() + " files in the result list");
      }
    } catch (Exception e) {  //NOSONAR
      LOG.error("Unable to remove duplicates", e);
    }
  }
}
