package com.coremedia.blueprint.webdav;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.webdav.filesystem.FileSystemListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This listener is used to populate required properties when content is changed via WebDAV.
 */
public class BlueprintWebdavFileSystemListener extends FileSystemListener {

  private static final Logger LOG = LoggerFactory.getLogger(BlueprintWebdavFileSystemListener.class);

  private static final String DOCTYPE_NAME_CM_MEDIA = "CMMedia";
  private static final String DOCTYPE_NAME_CM_DOWNLOAD = "CMDownload";
  private static final int MAX_FILENAME_LEN = 512;

  public static final String PROPERTY_NAME_LOCAL_SETTINGS = "localSettings";
  public static final String PROPERTY_NAME_TITLE = "title";

  /**
   * Default constructor.
   */
  public BlueprintWebdavFileSystemListener() {
    super();
  }

  /**
   * This method is called after a file has been created, changed or deleted by WebDAV. In case of a deletion no action
   * is performed.
   *
   * @param path     See {@link FileSystemListener#fileWritten(String, com.coremedia.cap.content.Content, String)}
   * @param content  See {@link FileSystemListener#fileWritten(String, com.coremedia.cap.content.Content, String)}
   * @param property See {@link FileSystemListener#fileWritten(String, com.coremedia.cap.content.Content, String)}
   */
  @Override
  public void fileWritten(String path, Content content, String property) {
    super.fileWritten(path, content, property);
    LOG.info("Processing content {} received via WebDAV.", content);
    // handle CMMedias and set default properties
    if (content.getType().isSubtypeOf(DOCTYPE_NAME_CM_MEDIA)) {
      handleCMMedia(path, content);
    }
    // handle CMDownload and set default properties
    if (content.getType().isSubtypeOf(DOCTYPE_NAME_CM_DOWNLOAD)) {
      handleCMDownload(path, content);
    }
  }

  /**
   * Handle content of type CMMedia.
   * @param path See {link fileWritten(String, com.coremedia.cap.content.Content, String)}
   * @param content See {link fileWritten(String, com.coremedia.cap.content.Content, String)}
   */
  private void handleCMMedia(String path, Content content) {
    if (StringUtils.isEmpty(content.getString(PROPERTY_NAME_TITLE))) {
      LOG.debug("{} property of content {} is empty. Setting default.", PROPERTY_NAME_TITLE, content);
      //set default title
      content.set(PROPERTY_NAME_TITLE, getDefaultTitle(path));
    }
    if (content.get(PROPERTY_NAME_LOCAL_SETTINGS) == null) {
      LOG.debug("{}  property of content {} is empty. Setting default.", PROPERTY_NAME_LOCAL_SETTINGS, content);
      // set empty struct 'localSettings' which must not be null
      content.set(PROPERTY_NAME_LOCAL_SETTINGS, content.getRepository().getConnection().getStructService().emptyStruct());
    }
  }

  /**
   * Handle content of type CMDownload.
   * @param path See {link fileWritten(String, com.coremedia.cap.content.Content, String)}
   * @param content See {link fileWritten(String, com.coremedia.cap.content.Content, String)}
   */
  private void handleCMDownload(String path, Content content) {
    if (StringUtils.isEmpty(content.getString(PROPERTY_NAME_TITLE))) {
      LOG.debug("{} property of content {} is empty. Setting default.", PROPERTY_NAME_TITLE, content);
      // set default title
      content.set(PROPERTY_NAME_TITLE, getDefaultTitle(path));
    }
  }

  /**
   * Determine a default title from the last part of the path and limit the length to 512 characters.
   * @param path The path of the created content which must not be <code>null</code>.
   *
   * @return The default title.
   */
  private String getDefaultTitle(String path) {
    // get file name as last part of the path
    String[] pathElements = path.split("/");
    String filename = pathElements[pathElements.length - 1];
    // limit length
    filename = StringUtils.left(filename, MAX_FILENAME_LEN);
    return filename;
  }

}
