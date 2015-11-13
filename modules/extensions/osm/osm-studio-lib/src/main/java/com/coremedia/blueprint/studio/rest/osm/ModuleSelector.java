package com.coremedia.blueprint.studio.rest.osm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * The jangaroo module selector
 */
public class ModuleSelector {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModuleSelector.class);

  private String jooApplicationPath;
  private java.util.List<String> bufferedLines = new ArrayList<>();
  private String disabledModule;

  public ModuleSelector(String path) throws Exception {
    jooApplicationPath = path;
    loadModules();
  }

  /**
   * Loads the modules names from the file.
   *
   * @return
   */
  private void loadModules() throws Exception {
    String path = jooApplicationPath;
    RandomAccessFile raf = new RandomAccessFile(path, "rw");
    String line = null;
    while ((line = raf.readLine()) != null) {
      bufferedLines.add(line);
    }
    raf.close();
  }

  private boolean isModuleLine(String line) {
    return line.contains("// FROM")
            && !line.contains("net.jangaroo")
            && !line.contains("studio-webapp")
            && !line.contains("com.coremedia.ui");
  }

  /**
   * Rewrites the joo application file.
   */
  private void rewrite() {
    try {
      if(new File(jooApplicationPath).delete()) {
        RandomAccessFile raf = new RandomAccessFile(jooApplicationPath, "rw");
        for (int i = 0; i < bufferedLines.size(); i++) {
          String line = bufferedLines.get(i);
          if (isModuleLine(line) && !isModuleEnabled(line)) {
            int newLineIndex = findNextModuleLine(i + 1);

            if(LOGGER.isInfoEnabled()) {
              LOGGER.info("Removing module '" + line + "' from line " + i + " to " + newLineIndex);
            }

            i = newLineIndex;
            continue;
          }
          raf.writeBytes(line);
          raf.writeBytes(System.getProperty("line.separator"));
        }
        raf.close();
      }
      else {
        LOGGER.error("Failed to delete jangaroo-application.js");
      }

    } catch (FileNotFoundException e) {
      LOGGER.error("Error rewriting jangaroo-application.js: " + e.getMessage(), e);
    } catch (IOException e) {
      LOGGER.error("Error rewriting jangaroo-application.js: " + e.getMessage(), e);
    }
  }

  public void disable(String module) {
    disabledModule = module;
    rewrite();
  }

  private boolean isModuleEnabled(String line) {
    return !line.contains(disabledModule);
  }

  private int findNextModuleLine(int index) {
    for (int i = index; i < bufferedLines.size(); i++) {
      String line = bufferedLines.get(i);
      if (isModuleLine(line)) {
        return i - 1;
      }
    }
    return bufferedLines.size();
  }
}

