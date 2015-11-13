package com.coremedia.blueprint.studio.rest;

import com.coremedia.cache.Cache;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.coremedia.rest.cap.intercept.InterceptService;
import com.coremedia.rest.cap.intercept.RestBlobService;
import com.coremedia.rest.linking.LinkResolver;
import com.coremedia.rest.linking.LocationHeaderResourceFilter;
import com.coremedia.xml.MarkupFactory;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.spi.container.ResourceFilters;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A REST service for resolving mime types and handling
 * uploads.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("upload")
public class UploadResource {

  private static final String SITE = "site";

  private static final String MARKUP_TEMPLATE = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>%s</p></div>";

  private CapConnection capConnection;
  private Cache cache;
  private ConfigurationService configurationService;

  private InterceptService interceptService;

  private MimeTypeService mimeTypeService;

  private LinkResolver linkResolver;

  private RestBlobService restBlobService;

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  @Required
  public void setInterceptService(InterceptService interceptService) {
    this.interceptService = interceptService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @Required
  public void setCapConnection(CapConnection capConnection) {
    this.capConnection = capConnection;
  }

  @Required
  public void setLinkResolver(LinkResolver linkResolver) {
    this.linkResolver = linkResolver;
  }

  @Required
  public void setRestBlobService(RestBlobService restBlobService) {
    this.restBlobService = restBlobService;
  }

  /**
   * Uses the mime type service to guess the mime type from the given filename.
   * If null is returned, the user has to select the mime type manually.
   *
   * @param filename The filename to retrieve the mime type for.
   * @return The mime type for the given file name or null.
   */
  @GET
  @Path("mimetype")
  public String getMimeType(@QueryParam("filename") String filename) {
    String extension = filename;
    if (filename.contains(".")) {
      extension = filename.substring(0, filename.lastIndexOf("."));
    }
    return mimeTypeService.getMimeTypeForExtension(extension);
  }

  /**
   * Creates a new document for the the browser file.
   */
  @POST
  @Path("create")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ResourceFilters(value = {LocationHeaderResourceFilter.class})
  public Content handleBlobUpload(@HeaderParam("site") String siteId,
                                  @HeaderParam("folderUri") String folderUri,
                                  @FormDataParam("contentName") String contentName,
                                  @FormDataParam("file") InputStream inputStream,
                                  @FormDataParam("file") FormDataContentDisposition fileDetail,
                                  @FormDataParam("file") FormDataBodyPart fileBodyPart)
          throws MimeTypeParseException, IOException {

    // Load upload configuration
    UploadConfigurationRepresentation config = loadConfiguration(siteId);

    String fileName = fileDetail.getFileName();
    String mimeTypeString = null;

    String ext = FilenameUtils.getExtension(fileName);
    if (ext != null) {
      mimeTypeString = mimeTypeService.getMimeTypeForExtension(ext);
    }

    if (mimeTypeString == null) {
      mimeTypeString = fileBodyPart.getMediaType().toString();
    }

    MimeType mimeType = new MimeType(mimeTypeString);

    Blob blob = restBlobService.fromInputStream(inputStream, mimeTypeString, fileName);

    Object propertyValue = blob;
    String propertyName = config.getMimeTypeToMarkupPropertyMapping(mimeType);
    if (propertyName != null) {
      // The property is configured as markup - convert blob to markup
      String source = IOUtils.toString(blob.getInputStream());
      source = StringEscapeUtils.escapeXml(source);
      source = convertLineBreaks(source);
      source = String.format(MARKUP_TEMPLATE, source);
      propertyValue = MarkupFactory.fromString(source);
    } else {
      // Get property name for the blob
      propertyName = config.getMimeTypeToBlobPropertyMapping(mimeType);
    }

    // Put blob into properties
    Map<String,Object> properties = new HashMap<>();
    properties.put(propertyName, propertyValue);

    // Get all other information for content creation:
    // 1. folder under which to create the content
    // 2. Unique name for the content (derived from file name)
    // 3. Content Type (derived from mime type)
    Content folder = (Content) linkResolver.resolveLink(folderUri);
    String name = (contentName == null) ? fileName : contentName;
    String uniqueFileName = resolveUniqueFilename(folder, name);
    String contentTypeName = config.getMimeTypeMapping(mimeType);
    ContentType contentType = capConnection.getContentRepository().getContentType(contentTypeName);

    // Create content (taking possible interceptors into consideration)
    ContentWriteRequest writeRequest = interceptService.interceptCreate(folder, uniqueFileName, contentType, properties);
    interceptService.handleErrorIssues(writeRequest);
    Content content = contentType.createByTemplate(folder, uniqueFileName, "{3} ({1})", writeRequest.getProperties());
    interceptService.postProcess(content, null);
    return content;
  }

  /**
   * Ensures that the file with the given name not exists yet.
   *
   * @param folder The folder to create the unique name for.
   * @param filename The file name to create.
   * @return The unique file name for the given folder.
   */
  private String resolveUniqueFilename(Content folder, String filename) {
    String uniqueFilename = filename;
    int index = 1;
    while(folder.getChild(uniqueFilename) != null) {
      uniqueFilename = filename + " (" + index +")";
      index++;
    }
    return uniqueFilename;
  }

  /**
   * Returns the upload configuration, including all mime types that are displayed in the combo box
   * of each upload item.
   *
   * @param siteId The id of the site to retrieve additional settings for.
   * @return The upload settings representation.
   */
  @GET
  @Path("config")
  public UploadConfigurationRepresentation getUploadConfiguration(@QueryParam(SITE) String siteId) {
    return loadConfiguration(siteId);
  }

  /**
   * Loads the upload settings, using a cache key
   *
   * @param siteId The site to retrieve site specific settings for.
   * @return The upload settings.
   */
  private UploadConfigurationRepresentation loadConfiguration(String siteId) {
    UploadConfigurationCacheKey cacheKey = new UploadConfigurationCacheKey(configurationService, siteId);
    return cache.get(cacheKey);
  }

  private String convertLineBreaks(String richtextContent) {
    String result = richtextContent;
    result = result.replaceAll("\r\n", "<br/>");
    result = result.replaceAll("\r", "<br/>");
    result = result.replaceAll("\n", "<br/>");
    return result;
  }

}
