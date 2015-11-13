package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.MultiResultGeneratorFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class InboxResultFactory extends MultiResultGeneratorFactoryImpl {
  private List<String> inboxes = new ArrayList<>();

  // invoked by reflection,
  // value s. css-import.properties#import.multiResultGeneratorFactory.property.inbox
  public void setInbox(String semicolonSeparatedPaths) {
    inboxes.clear();
    StringTokenizer st=new StringTokenizer(semicolonSeparatedPaths, ";");
    while (st.hasMoreElements()) {
      inboxes.add(st.nextToken());
    }
  }

  public List<String> getInboxes() {
    return inboxes;
  }
}
