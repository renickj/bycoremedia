package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.MultiResultGenerator;

public class CssImporterResultFactory extends InboxResultFactory {
  private static final String NO_TEMPLATES_FLAG = "--no-templates";

  private String filePattern;
  private boolean noTemplates = false;


  // --- configure --------------------------------------------------

  // invoked by reflection

  // value s. css-import.properties#import.multiResultGeneratorFactory.property.filePattern
  public void setFilePattern(String filePattern) {
    this.filePattern = filePattern;
  }
  // equivalent to --no-templates command line arg
  public void setNoTemplates(String noTemplates) {
    this.noTemplates = "true".equalsIgnoreCase(noTemplates);
  }


  // --- interface --------------------------------------------------

  @Override
  public MultiResultGenerator getMultiResultGenerator() {
    return new ThemeResultGenerator(getInboxes(), filePattern, noTemplates());
  }


  // --- internal ---------------------------------------------------

  private boolean noTemplates() {
    // precedence:
    // 1. command line args
    for (String arg : getArgs()) {
      if (NO_TEMPLATES_FLAG.equals(arg)) {
        return true;
      }
    }
    // 2. importer properties
    return noTemplates;
  }
}
