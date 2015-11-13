package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.MultiSource;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.List;

public class MultiSourceMock implements MultiSource {
  private List<Source> items = new ArrayList<>();

  public MultiSourceMock(List<Source> items) {
    this.items = items;
  }

  @Override
  public MultiSource flattened() throws Exception {
    return this;
  }

  @Override
  public int size() throws Exception {
    return items.size();
  }

  @Override
  public Source getSource(int index, String format) throws Exception {
    return items.get(index);
  }

  @Override
  public void setSystemId(String systemId) {

  }

  @Override
  public String getSystemId() {
    return null;
  }
}
