package com.coremedia.blueprint.common.importfilter;

import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.publisher.importer.AbstractTransformer;
import com.coremedia.publisher.importer.TransformerParameters;
import com.coremedia.xml.XmlUtil5;
import org.apache.commons.io.IOUtils;
import com.coremedia.dtd.CoremediaDtd;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.ENGLISH;

@SuppressWarnings({"JavaDoc"})
public class CssTransformer extends AbstractTransformer {
  private static final Log LOG = LogFactory.getLog(CssTransformer.class);

  private static final String UTF_8 = "UTF-8";
  private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"" + UTF_8 + "\"?>";

  private static final String DOCTYPE_CMCSS = "CMCSS";
  private static final String DOCTYPE_JAVASCRIPT = "CMJavaScript";
  private static final String DOCTYPE_INTERACTIVE = "CMInteractive";
  private static final String DOCTYPE_TEMPLATESET = "CMTemplateSet";
  private static final String DOCTYPE_IMAGE = "CMImage";

  static final String PARAM_MIME_TYPE_SERVICE = "mimeTypeService";

  // The pattern means                                url  ("        protocol   :  the/path    "        )
  static final Pattern URL_PATTERN = Pattern.compile("url\\([\"\']?((([^)\"']*?):)?([^)\"\']*))[\"\']?\\)");
  // Capturing groups                                              123         3 2 4         41

  private OutputStream os = null;
  private InputStream is = null;
  private URI sourceUri = null;


  // --- Configure --------------------------------------------------

  /**
   * Returns the CSSs which are to be included in the CSS denoted by systemId.
   * <p/>
   * The result paths must be relative to {@link #sourceUri}.
   * <p/>
   * This default implementation returns an empty array.  Override it for your
   * particular set of stylesheets.
   *
   * @return an array of css file paths
   */
  protected String[] getLinks(URI systemId) {
    return new String[0];
  }


  // --- Transformer ------------------------------------------------

  @Override
  public void transform(Source source, Result outputTarget) throws TransformerException {
    if (!(source instanceof StreamSource)) {
      throw new TransformerException("Source " + source + " is not a StreamSource");
    }

    try {
      is = ((StreamSource) source).getInputStream();
      os = ((StreamResult) outputTarget).getOutputStream();
      sourceUri = new URI((String) getParameter(TransformerParameters.MULTI_SYSTEMID));

      URI systemIdUri = createUri(source.getSystemId());
      URI relativeTarget = sourceUri.relativize(systemIdUri);
      URI targetUri = (URI) getParameter("targeturi");
      targetUri = targetUri.resolve(relativeTarget);
      URI targetPath = targetUri.resolve(createUri("."));
      String name = getName(systemIdUri);
      String filetype = getExtension(systemIdUri);
      transformByFiletype(systemIdUri, targetPath, name, filetype);
    } catch (Exception e) {
      throw new TransformerException("Cannot transform Resource file " + source.getSystemId(), e);
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(os);
      os = null;
    }
  }


  // --- internal ---------------------------------------------------

  private void transformByFiletype(URI systemIdUri, URI targetPath, String name, String filetype) throws IOException, URISyntaxException {
    write(XML_HEADER);
    write(CoremediaDtd.DOCTYPE);

    if ("css".equals(filetype)) {
      writeResourceDocument(name, targetPath, systemIdUri, is, DOCTYPE_CMCSS);
    } else if ("js".equals(filetype)) {
      writeResourceDocument(name, targetPath, systemIdUri, is, DOCTYPE_JAVASCRIPT);
    } else if ("swf".equals(filetype)) {
      writeResouceDocumentBinary(name, targetPath, systemIdUri, "application/x-shockwave-flash", DOCTYPE_INTERACTIVE, "data");
    } else if ("jar".equals(filetype)) {
      writeResouceDocumentBinary(name, targetPath, systemIdUri, "application/java-archive", DOCTYPE_TEMPLATESET, "archive");
    } else  {
      writeImageDocument(name, targetPath, systemIdUri);
    }
  }


  // --- image ------------------------------------------------------

  private void writeImageDocument(String name, URI path, URI systemId) throws IOException {
    writeOpening(name, path, systemId, DOCTYPE_IMAGE);
    writePictureDataProperty(systemId);
    writeClosing();
  }

  private void writePictureDataProperty(URI systemId) throws IOException {
    String imgtype = getExtension(systemId);
    String mimetype = ((MimeTypeService)getParameter(PARAM_MIME_TYPE_SERVICE)).getMimeTypeForExtension(imgtype);
    if (mimetype == null) {
      mimetype = "image/" + imgtype.toLowerCase(ENGLISH);
    }
    write("<blob name=\"data\"");
    writeAttr("mimetype", mimetype);
    writeAttr("href", systemId);
    write("/>");
  }

  // --- css and js -------------------------------------------------

  private void writeResourceDocument(String name, URI path, URI systemId, InputStream is, String docType) throws IOException, URISyntaxException {
    writeOpening(name, path, systemId, docType);
    writeResourceDataProperty(is, systemId, docType);
    writeLinks(systemId);
    writeClosing();
  }

  // --- flash ------------------------------------------------------

  private void writeResouceDocumentBinary(String name, URI path, URI systemId, String mimetype, String docType, String blobProperty) throws IOException, URISyntaxException {
    writeOpening(name, path, systemId, docType);
    writeResourceDataPropertyBinary(systemId, mimetype, blobProperty);
    writeLinks(systemId);
    writeClosing();
  }

  private void writeLinks(URI systemId) throws IOException {
    String[] files = getLinks(systemId);
    if (files != null && files.length > 0) {
      write("<linklist name=\"include\">");
      for (String link : files) {
        File file = new File(sourceUri.getPath(), link);
        if (file.exists()) {
          write("<link idref=\"");
          write(systemIdToNmToken(sourceUri.relativize(file.toURI())));
          write("\"/>");
        } else {
          LOG.warn("No such file: " + file.getAbsolutePath() + ", cannot create link in " + systemId.getPath());
          suggestExitCode(1);
        }
      }
      write("</linklist>");
    }
  }

  private void writeResourceDataProperty(InputStream is, URI systemId, String docType) throws IOException, URISyntaxException {
    write("<text name=\"code\"><div");
    writeAttr("xmlns", "http://www.coremedia.com/2003/richtext-1.0");
    writeAttr("xmlns:xlink", "http://www.w3.org/1999/xlink");
    write(">");
    if (DOCTYPE_CMCSS.equals(docType)) {
      writeCss(is, systemId);
    } else if (DOCTYPE_JAVASCRIPT.equals(docType)) {
      writeJavascript(is);
    }
    write("</div></text>");
  }

  private void writeResourceDataPropertyBinary(URI systemId, String mimetype, String blobProperty) throws IOException, URISyntaxException {
    write("<blob name=\"");
    write(blobProperty);
    write("\"");
    writeAttr("mimetype", mimetype);
    writeAttr("href", systemId);
    write("/>");
  }

  private void writeCss(InputStream is, URI systemId) throws IOException, URISyntaxException {
    StringBuilder css = new StringBuilder();
    byte[] bytes = new byte[is.available()];
    for (int count = is.read(bytes); count > 0; count = is.read(bytes)) {
      css.append(new String(bytes, 0, count, UTF_8));
    }
    for (StringTokenizer st = new StringTokenizer(css.toString(), "\n"); st.hasMoreTokens(); ) {
      String line = st.nextToken();
      if (line.trim().length() > 0) {
        line = urlsToXlinks(line, systemId);
        write("<p>");
        write(line);
        write("</p>");
      }
    }
  }

  private void writeJavascript(InputStream is) throws IOException, URISyntaxException {
    StringBuilder css = new StringBuilder();
    byte[] bytes = new byte[is.available()];
    for (int count = is.read(bytes); count > 0; count = is.read(bytes)) {
      css.append(new String(bytes, 0, count, UTF_8));
    }
    for (StringTokenizer st = new StringTokenizer(css.toString(), "\n"); st.hasMoreTokens(); ) {
      String line = st.nextToken();
      if (line.trim().length() > 0) {
        write("<p>");
        write(XmlUtil5.escape(line));
        write("</p>");
      }
    }
  }

  private String urlsToXlinks(String line, URI systemId) throws URISyntaxException {
    Matcher matcher = URL_PATTERN.matcher(line);
    StringBuffer appender = new StringBuffer();  // NOSONAR Matcher#appendReplacement needs a StringBuffer
    StringBuilder result = new StringBuilder();
    int startNoMatch = 0;
    int endNoMatch;

    while (matcher.find()) {
      // Matcher does not support access to the non-matching part during an
      // appendReplacement loop. So we have to escape the part before the match...
      endNoMatch = matcher.start();
      result.append(XmlUtil5.escape(line.substring(startNoMatch, endNoMatch)));

      String uri = matcher.group(1);
      String protocol = matcher.group(3);  // NOSONAR magic number
      String path = matcher.group(4);  // NOSONAR magic number

      // replace url -> RichText (incl non-matching prefix)
      String replacement;
      if (protocol==null) {
        replacement = toRichtextInternalLink(systemId, path);
      } else if ("data".equals(protocol)) {
        replacement = toRichtextPlain(protocol, path);
      } else {
        replacement = toRichtextHref(createUri(uri), uri);
      }
      matcher.appendReplacement(appender, replacement);

      // extract the actual url replacement from the appendReplacement result
      result.append(appender.substring(endNoMatch - startNoMatch));

      // loop for next match
      startNoMatch = matcher.end();
      appender.setLength(0);
    }

    // ...and finally not use appendTail but do it manually
    result.append(XmlUtil5.escape(line.substring(startNoMatch)));

    return result.toString();
  }

  private String toRichtextInternalLink(URI systemId, String uriMatch) throws URISyntaxException {
    URI linkUri = createUri(uriMatch);
    URI linkFileUri = systemId.resolve(linkUri);
    String linkError = checkLink(linkFileUri);
    if (linkError == null) {
      URI linkImportId = new URI("coremedia", "", "/cap/resources/" + systemIdToNmToken(sourceUri.relativize(linkFileUri)), null);
      String filetype = getExtension(linkUri);
      if ("css".equals(filetype)) {
        return toRichtextHref(linkImportId, uriMatch);
      } else {
        return toRichtextImg(linkImportId, linkFileUri);
      }
    } else {
      // Could not resolve link. Preserve target as ordinary text.
      LOG.warn("Cannot resolve " + uriMatch + " in " + systemId + ": " + linkError);
      suggestExitCode(1);
      return toRichtextPlain(null, uriMatch);
    }
  }

  private String toRichtextPlain(String protocol, String uriMatch) {
    String protocolPrefix = protocol==null ? "" : protocol+":";
    return "url(" + protocolPrefix + XmlUtil5.escape(uriMatch) + ")";
  }

  private String toRichtextImg(URI href, URI linkFileUri) {
    StringBuilder builder = new StringBuilder("url(<img xlink:href=\"");
    builder.append(href);
    builder.append("/data\" alt=\"\" xlink:actuate=\"onLoad\" xlink:show=\"embed\" xlink:type=\"simple\"/>");
    if (linkFileUri.getFragment() != null) {
      builder.append("#").append(linkFileUri.getFragment());
    }
    builder.append(")");
    return builder.toString();
  }

  private String toRichtextHref(URI href, String linktext) {
    return "url(<a xlink:href=\"" + href + "\">" + XmlUtil5.escape(linktext) + "</a>)";
  }

  private String checkLink(URI linkFileUri) {  // NOSONAR  cyclomatic complexity
    // Cut off any existing uri fragment
    String linkFileStr;
    try {
      linkFileStr = linkFileUri.toURL().getPath();
    } catch (MalformedURLException e) {  // NOSONAR handling is appropriate here
      return "URI " + linkFileUri + " is not pointing to an existing file. " + e.getMessage();
    }

    File linkFile = new File(linkFileStr);
    if (!linkFile.exists()) {
      return "File " + linkFile.getAbsolutePath() + " not found";  // NOSONAR duplicate String
    }
    if (!linkFile.isFile()) {
      return "File " + linkFile.getAbsolutePath() + " is not a file";  // NOSONAR duplicate String
    }
    if (!linkFile.canRead()) {
      return "Cannot read file " + linkFile.getAbsolutePath();
    }
    if (!linkFileUri.getPath().startsWith(sourceUri.getPath())) {
      return "File " + linkFile.getAbsolutePath() + " is outside import scope";  // NOSONAR duplicate String
    }
    return null;
  }


  // --- low level write --------------------------------------------

  private void writeOpening(String name, URI path, URI systemId, String type) throws IOException {
    String id = systemIdToNmToken(sourceUri.relativize(systemId));
    write("<coremedia><document");
    writeAttr("type", type);
    writeAttr("name", name);
    writeAttr("path", path.getPath());
    writeAttr("id", id);
    write("><version number=\"1\">");
  }

  private void writeClosing() throws IOException {
    write("</version></document></coremedia>");
  }

  protected void writeAttr(String name, Object value) throws IOException {
    if (value != null) {
      write(" ");
      write(name);
      write("=\"");
      write(value.toString());
      write("\"");
    }
  }

  protected void write(String s) throws IOException {
    if (os == null) {
      throw new IllegalStateException("No output stream");
    }
    byte[] bytes = s.getBytes(UTF_8);
    // Strip obsolete leading and trailing CR chars next to
    // LF chars which have already been removed by a StringTokenizer.
    int numBytes = bytes.length;
    int from = numBytes > 0 && bytes[0] == '\r' ? 1 : 0;
    int len = (numBytes > 0 && bytes[numBytes - 1] == '\r' ? numBytes - 1 : numBytes) - from;
    os.write(bytes, from, len);
  }


  // --- URI helpers ------------------------------------------------

  protected static String getName(URI uri) {
    String systemIdPath = uri.getPath();
    int slash = systemIdPath.lastIndexOf('/');
    return slash >= 0 ? systemIdPath.substring(slash + 1) : systemIdPath;
  }

  protected static String getExtension(URI uri) {
    return SystemIdUtil.type(uri.getPath());
  }

  /**
   * Creates an URI from a string.
   * <p/>
   * Delegates to {@link URI#create(String)}. Hook method for extending classes to
   * handle malformed URLs.
   */
  protected URI createUri(String str) {
    return URI.create(str);
  }

  /**
   * Create an NmToken from systemId, use _ as escaping char.
   */
  protected static String systemIdToNmToken(URI systemId) {
    String uriStr = systemId.getPath();
    StringBuilder result = new StringBuilder(uriStr.length());
    for (int i = 0; i < uriStr.length(); ++i) {
      char c = uriStr.charAt(i);
      if (c == '_') {
        result.append("__");
      } else if (Character.isDigit(c) || Character.isLetter(c) || ".:-#".indexOf(c) >= 0) {
        result.append(c);
      } else {
        result.append("_").append(Integer.toHexString(c));
      }
    }
    return result.toString();
  }
}
