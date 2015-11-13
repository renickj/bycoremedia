package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.AbstractMultiResultGenerator;
import com.coremedia.publisher.importer.MultiResult;
import com.coremedia.publisher.importer.ResultFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Locale.ENGLISH;

public class InboxResultGenerator extends AbstractMultiResultGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(InboxResultGenerator.class);

  private Iterator<String> inboxIterator;
  private IOFileFilter fileFilter;

  public InboxResultGenerator(List<String> inboxes, String filePattern) {
    inboxIterator = inboxes.iterator();
    fileFilter = filePattern==null ? TrueFileFilter.INSTANCE : new ExtensionOrFolderFilter(filePattern);
  }


  // --- MultiResultGenerator ---------------------------------------

  /**
   * Returns one big chunk of all files.
   *
   * @return A MultiResult with all the files
   */
  @Override
  public MultiResult next() {
    try {
      File inbox = nextInbox();
      if (inbox==null) {
        LOG.info("Done!");
        return null;
      }
      MultiResult result = ResultFactory.getInstance().getMultiResult();
      Collection<File> files = FileUtils.listFiles(inbox, getFileFilter(), TrueFileFilter.INSTANCE);
      for (File file : files) {
        result.addNewResult(file.toURI().toString());
      }
      if (result.size()>0) {  // NOSONAR false positive, not a Collection
        String systemId = inbox.toURI().toString();
        result.setSystemId(systemId);
        LOG.info("Import inbox {}", systemId);
        return result;
      } else {
        return next();
      }
    } catch (Exception e) {
      throw new IllegalStateException("Cannot create MultiResult", e);
    }
  }

  protected IOFileFilter getFileFilter() {
    return fileFilter;
  }


  // --- internal ---------------------------------------------------

  private File nextInbox() {
    if (!inboxIterator.hasNext()) {
      return null;
    }
    String nextInboxPath = inboxIterator.next();
    File inboxDir = new File(nextInboxPath);
    if (!inboxDir.exists() || !inboxDir.isDirectory() || !inboxDir.canRead()) {
      LOG.error("Cannot process inbox {}, omit and proceed", nextInboxPath);
      suggestExitCode(1);
      return nextInbox();
    }
    return inboxDir;
  }


  // --- inner classes ----------------------------------------------

  private static class ExtensionOrFolderFilter implements IOFileFilter {
    private Pattern filePattern;

    public ExtensionOrFolderFilter(String filePattern) {
      this.filePattern = Pattern.compile(filePattern);
    }

    @Override
    public boolean accept(File file) {
      return filePattern.matcher(file.getName().toLowerCase(ENGLISH)).matches();
    }

    @Override
    public boolean accept(File file, String s) {
      return accept(new File(file, s));
    }
  }
}
