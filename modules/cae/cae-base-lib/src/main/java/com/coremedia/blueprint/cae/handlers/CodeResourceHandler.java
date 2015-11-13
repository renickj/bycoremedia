package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.contentbeans.CodeResourcesCacheKey;
import com.coremedia.blueprint.common.contentbeans.CMAbstractCode;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.Version;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.coremedia.xml.Markup;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.activation.MimeTypeParseException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_SEGMENTS;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_RESOURCE;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ETAG;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_NAME;
import static com.coremedia.objectserver.web.HandlerHelper.createModel;
import static com.coremedia.objectserver.web.HandlerHelper.createModelWithView;
import static com.coremedia.objectserver.web.HandlerHelper.notFound;
import static com.coremedia.objectserver.web.HandlerHelper.redirectTo;

/**
 * Handler and LinkScheme for all CSS and JavaScript to the requested navigation object
 * supports usage of local resources (from file), minification and merging of resources
 */
@Link
@RequestMapping
public class CodeResourceHandler extends HandlerBase implements ApplicationContextAware, InitializingBean {

  // --- logging ---
  private static final Logger LOG = LoggerFactory.getLogger(CodeResourceHandler.class);
  // --- spring configured properties ---
  private CapConnection capConnection;
  private ApplicationContext applicationContext;
  private Cache cache;
  // --- various constants ---
  @VisibleForTesting static final String MARKUP_PROGRAMMED_VIEW_NAME = "script";
  private static final String DEFAULT_EXTENSION = "css";
  // --- path segments ---
  private static final String SEGMENT_PATH = "path";
  private static final String SEGMENT_HASH = "hash";
  // --- settings for minification, merging and local resources ---
  private boolean localResourcesEnabled = false;
  private boolean developerModeEnabled = false;

  private static final String CSS = "css";
  private static final String JS = "js";

  private static final String PREFIX_CSS = '/' + PREFIX_RESOURCE + '/' + CSS;
  private static final String PREFIX_JS = '/' + PREFIX_RESOURCE + '/' + JS;


  /**
   * Link to a merged resource. Usually merged for a navigation node.
   * <p/>
   * e.g. /resource/js/4/1035154981/media.js
   */
  private static final String URI_SUFFIX_BULK =
          "/{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}" +
          "/{" + SEGMENT_HASH + "}" +
          "/{" + SEGMENT_NAME + "}" +
          ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";

  public static final String CSS_PATTERN_BULK = PREFIX_CSS + URI_SUFFIX_BULK;
  public static final String JS_PATTERN_BULK = PREFIX_JS + URI_SUFFIX_BULK;

  /**
   * Link to a single resource inside the local workspace with
   * setting 'cae.use.local.resources' set to 'true'
   * <p/>
   * e.g. /resource/css/media/reset-123-0.css
   */
  public static final String URI_PATTERN_SINGLE =
          '/' + PREFIX_RESOURCE +
          "/{" + SEGMENT_PATH + ":" + PATTERN_SEGMENTS + "}" +
          "/{" + SEGMENT_NAME + "}" +
          "-{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}" +
          "-{" + SEGMENT_ETAG + ":" + PATTERN_NUMBER + "}" +
          ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";


  /**
   * If local resources are used, links in CSS files match this pattern.
   */
  public static final String URI_PATTERN_SINGLE_CSS_LINK =
          '/' + PREFIX_RESOURCE +
          "/{" + SEGMENT_PATH + ":" + PATTERN_SEGMENTS + "}" +
          "/{" + SEGMENT_NAME + "}" +
          ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";

  // --- spring config -------------------------------------------------------------------------------------------------

  @Required
  public void setCapConnection(CapConnection capConnection) {
    this.capConnection = capConnection;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * Links are generated to local resources instead of resources in the content repository if enabled.
   * <p/>
   * Default: false.
   */
  public void setLocalResourcesEnabled(boolean localResourcesEnabled) {
    this.localResourcesEnabled = localResourcesEnabled;
  }

  /**
   * CSS and JavaScript resources are generated as merged and minified if disabled.
   * <br/>
   * Must not be used with local resources.
   * <p/>
   * Default: false.
   */
  public void setDeveloperModeEnabled(boolean developerModeEnabled) {
    this.developerModeEnabled = developerModeEnabled;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Override
  public void afterPropertiesSet() {
    if(!developerModeEnabled && localResourcesEnabled) {
      throw new IllegalStateException("Illegal setting for resource delivery detected! " +
          "Either turn on CAE developer mode or turn of local resources!");
    }
  }

  // --- Handlers ------------------------------------------------------------------------------------------------------

  /**
   * Handles requests for merged and minified CSS/JS.
   *
   * @param cmContext The contentBean, should be of type {@link com.coremedia.blueprint.common.contentbeans.CMNavigation}
   * @param extension    The file-extension that was asked for, usually "css" or "js".
   * @param webRequest   The web request
   * @return             The ModelAndView or 404 (not found).
   */
  @RequestMapping(value = {JS_PATTERN_BULK,CSS_PATTERN_BULK})
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) CMContext cmContext,
                                    @PathVariable(SEGMENT_EXTENSION) String extension,
                                    @PathVariable(SEGMENT_HASH) String hash,
                                    WebRequest webRequest) {
    CodeResources codeResources = cache.get(new CodeResourcesCacheKey(cmContext, getCodePropertyName(extension), developerModeEnabled));
    //check scripthash
    if (!hash.equals(codeResources.getETag())) {
      //hash does not match
      return HandlerHelper.redirectTo(codeResources, extension);
    }
    if (webRequest.checkNotModified(codeResources.getETag())) {
      // shortcut exit - no further processing necessary
      return null;
    }
    //everything is in order, return correct MAV
    return HandlerHelper.createModelWithView(codeResources, extension);
  }

  private String getCodePropertyName(String extension) {
    return JS.equals(extension) ? CMNavigation.JAVA_SCRIPT : CMNavigation.CSS;
  }

  //------------

  /**
   * Handles requests to a single file linked in a CSS file
   *
   * @param name            The readable name of the code resource.
   * @param extension       The extension of the requested resource.
   * @return                The ModelAndView or 404 (not found).
   */
  @RequestMapping(value = URI_PATTERN_SINGLE_CSS_LINK)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_PATH) List<String> path,
                                    @PathVariable(SEGMENT_NAME) String name,
                                    @PathVariable(SEGMENT_EXTENSION) String extension,
                                    WebRequest webRequest) throws IOException, MimeTypeParseException {
    if (localResourcesEnabled) {
      return localResource(path, name, extension, webRequest);
    } else {
      return notFound();
    }
  }

  @RequestMapping(value = URI_PATTERN_SINGLE)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_PATH) List<String> path,
                                    @PathVariable(SEGMENT_ID) CMAbstractCode cmAbstractCode,
                                    @PathVariable(SEGMENT_ETAG) int version,
                                    @PathVariable(SEGMENT_NAME) String name,
                                    @PathVariable(SEGMENT_EXTENSION) String extension,
                                    WebRequest webRequest,
                                    HttpServletResponse response) throws IOException, MimeTypeParseException {
    if (localResourcesEnabled) {
      ModelAndView mav = localResource(path, name, extension, webRequest);
      // null represents "not modified" and is an appropriate return value
      if (mav==null || !HandlerHelper.isNotFound(mav)) {
        return mav;
      }
      // ... else fallbackthrough to contentResource ...
    }
    return contentResource(extension, cmAbstractCode, name, version, response, webRequest);
  }

  // creates a Markup/script model
  private ModelAndView contentResource(String extension, CMAbstractCode cmAbstractCode, String name,
                                       int version, HttpServletResponse response, WebRequest webRequest) {
    // URL validation: check that extension is OK and name matches expectation
    if (isExtensionValid(extension, cmAbstractCode) && isNameSegmentValid(name, cmAbstractCode)) {
      // URL validation: if the version is valid (positive and even) but old, redirect to the "correct" URL
      int latestVersion = getLatestVersion(cmAbstractCode.getContent());
      if (version == latestVersion) {
        if (webRequest.checkNotModified(cmAbstractCode.getContent().getModificationDate().getTimeInMillis())) {
          // shortcut exit - no further processing necessary
          return null;
        }
        Markup markup = cmAbstractCode.getCode();
        if (markup != null) {
          response.setContentType(cmAbstractCode.getContentType());
          return createModelWithView(markup, MARKUP_PROGRAMMED_VIEW_NAME);
        }
      } else if (version > 0 && version < latestVersion) {
        return redirectTo(cmAbstractCode);
      }
    }
    return notFound();
  }

  // creates a Blob/DEFAULT model
  private ModelAndView localResource(List<String> path, String name, String extension, WebRequest webRequest) throws IOException, MimeTypeParseException {
    String resourcePath = '/' + joinPath(path) + '/' + name + '.' + extension;
    Resource resource = applicationContext.getResource(resourcePath);
    if (resource != null && resource.isReadable()) {
      if (webRequest.checkNotModified(resource.lastModified())) {
        // shortcut exit - no further processing necessary
        return null;
      }
      String mimeType = getMimeTypeService().getMimeTypeForExtension(extension);
      Blob blob = capConnection.getBlobService().fromInputStream(resource.getInputStream(), mimeType);
      return createModel(blob);
    } else {
      LOG.warn("File {} not found in local resources, but was linked in the content!", resourcePath);
      return notFound();
    }
  }


  // === link schemes ==================================================================================================

  /**
   * Generated a link to a single resource file. Depending on resource settings,
   * the link will either point to a local file or a file inside the repository.
   *
   * @param cmAbstractCode  The contentBean, should be of type {@link com.coremedia.blueprint.common.contentbeans.CMAbstractCode}
   * @return                UriComponents of the generated link.
   */
  @Link(type = CMAbstractCode.class, uri = URI_PATTERN_SINGLE)
  public UriComponents buildLink(CMAbstractCode cmAbstractCode, UriComponentsBuilder uriBuilder) {
    String extension = getExtension(cmAbstractCode.getContentType(), DEFAULT_EXTENSION);
    String resourceName = formatResourceName(cmAbstractCode);
    String path = formatContentPath(cmAbstractCode);
    if (path == null) {
      path = extension;
    }
    String id = getId(cmAbstractCode);
    String latestVersion = String.valueOf(getLatestVersion(cmAbstractCode.getContent()));
    return uriBuilder.buildAndExpand(path, resourceName, id, latestVersion, extension);
  }

  /**
   * Generated a link to a merged version of all resources of a page.
   * Use {@link com.coremedia.blueprint.base.links.UriConstants.Segments#SEGMENT_EXTENSION} via cm:param in cm:link to specify
   * the resources to use: "css" or "js" are available
   * To support {@link HandlerHelper#redirectTo(Object, String)} the extension may also passed as view parameter.
   *
   * @param codeResources  The merged code resource used to build the link.
   * @param linkParameters  Parameters, that were passed inside the cm:link tag via cm:param.
   * @return                A Map containing the parts of the generated link.
   */
  @Link(type = CodeResources.class)
  public UriComponents buildLink(CodeResources codeResources, String view, Map<String, Object> linkParameters) {
    // 2. get passed parameters and resolve extension
    Object object = linkParameters.get(SEGMENT_EXTENSION);
    if (object == null){
      object = view;
    }
    String extension = (object instanceof String) ? ((String) object).toLowerCase() : DEFAULT_EXTENSION;

    CMNavigation cmNavigation = findFirstChannelWithCode(codeResources.getContext(), getCodePropertyName(extension));
    int navigationId = cmNavigation.getContentId();
    String scriptHash = codeResources.getETag();
    // 4. build link
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
    if(JS.equals(extension)){
      uriBuilder.path(JS_PATTERN_BULK);
    } else if (CSS.equals(extension)) {
      uriBuilder.path(CSS_PATTERN_BULK);
    }

    Map<String,Object> parameters = new ImmutableMap.Builder<String, Object>()
            .put(SEGMENT_ID, navigationId)
            .put(SEGMENT_HASH, scriptHash)
            .put(SEGMENT_NAME, getNavigationName(cmNavigation))
            .put(SEGMENT_EXTENSION, extension).build();

    return uriBuilder.buildAndExpand(parameters);
  }

  private CMNavigation findFirstChannelWithCode(Navigation navigation, String linkPropertyName) {
    CMContext context = navigation.getContext();
    if (!context.getContent().getLinks(linkPropertyName).isEmpty()) {
      return context;
    }
    Navigation parentNavigation = context.getParentNavigation();
    if (parentNavigation == null) {
      return context;
    }
    CMContext parentContext = parentNavigation.getContext();
    if (parentContext == null) {
      return context;
    }
    return findFirstChannelWithCode(parentContext, linkPropertyName);
  }

  // === internal ======================================================================================================

  /**
   * Helper Method to retrieve the latest checked in version of a resource content object.
   * @param content the content object
   * @return the current version.
   */
  private int getLatestVersion(Content content) {
    Version v = content.isCheckedIn() ? content.getCheckedInVersion() : content.getWorkingVersion();
    return IdHelper.parseVersionId(v.getId());
  }

  //------------

  /**
   * Helper Method to retrieve the readable name for a navigation object.
   * @param navigation The navigation object.
   * @return The readable name of the navigation.
   */
  private String getNavigationName(Navigation navigation) {
    String segment = navigation.getSegment();

    if (StringUtils.hasText(segment)) {
      segment = removeSpecialCharacters(segment);
    } else {
      segment = removeSpecialCharacters(navigation.getTitle());
    }
    return segment;
  }

  //------------

  private static String formatContentPath(CMAbstractCode cmAbstractCode) {
    // path will contain file name as last element
    String contentPath = cmAbstractCode.getContent().getPath();
    if (contentPath==null) {
      // content is deleted, get last path.
      contentPath = cmAbstractCode.getContent().getLastPath();
    }
    if (contentPath!=null) {
      // remove root slash and filename, ignore case
      contentPath = contentPath.substring(1, contentPath.lastIndexOf('/')).toLowerCase();
    }
    return contentPath;
  }

  private String formatResourceName(CMAbstractCode code) {
    String name = code.getContent().getName();
    String extension = getExtension(code.getContentType(), DEFAULT_EXTENSION);
    if (name.endsWith('.'+extension)) {
      name = name.substring(0, name.length() - 1 - extension.length());
    }
    return uriEncode(name);
  }

  private String uriEncode(String name) {
    try {
      return UriUtils.encodePathSegment(name, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new Error("UTF-8 MUST be supported, according to the Java language spec!");
    }
  }

  //------------

  /**
   * Helper Method to check the validity of the resource name.
   * @param name The reference name to check against.
   * @param code The code object.
   * @return Result of the validity check.
   */
  private boolean isNameSegmentValid(String name, CMAbstractCode code) {
    return name != null && name.equals(formatResourceName(code));
  }

  //------------

  /**
   * Helper Method to check the validity of the resource name.
   * @param extension The reference extension name to check against.
   * @param code The code object.
   * @return Result of the validity check.
   */
  private boolean isExtensionValid(String extension, CMAbstractCode code) {
    return extension != null && extension.equals(getExtension(code.getContentType(), DEFAULT_EXTENSION));
  }
}
