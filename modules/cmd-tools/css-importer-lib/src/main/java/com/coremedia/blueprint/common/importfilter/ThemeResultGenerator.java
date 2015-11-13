package com.coremedia.blueprint.common.importfilter;


import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import java.util.List;

public class ThemeResultGenerator extends InboxResultGenerator {
  private static final String JAR = ".jar";

  private IOFileFilter fileFilter;

  public ThemeResultGenerator(List<String> inboxes, String filePattern, boolean noTemplates) {
    super(inboxes, filePattern);
    initFileFilter(noTemplates);
  }

  @Override
  protected IOFileFilter getFileFilter() {
    return fileFilter;
  }


  // --- internal ---------------------------------------------------

  private void initFileFilter(boolean noTemplates) {
    fileFilter = super.getFileFilter();
    if (noTemplates) {
      IOFileFilter noTemplatesFilter = new NotFileFilter(new SuffixFileFilter(JAR, IOCase.INSENSITIVE));
      fileFilter = new AndFileFilter(fileFilter, noTemplatesFilter);
    }
  }

}
