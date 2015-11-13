package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.AbstractTransformer;
import com.coremedia.publisher.importer.MultiResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.ENGLISH;

/**
 * @deprecated Not needed any longer.
 */
@Deprecated
public class FindRequiredFilesTransformer extends AbstractTransformer {
  private static final Log LOG = LogFactory.getLog(FindRequiredFilesTransformer.class);
  private static final Pattern URL_PATTERN = Pattern.compile("url\\([\"\']?([^)\"\']*)[\"\']?\\)");

  @Override
  public void transform(Source source, Result outputTarget) throws TransformerException {
    if (!(source instanceof StreamSource)) {
      throw new TransformerException("Source " + source + " is not a StreamSource");
    }

    StreamSource streamSource = (StreamSource) source;
    MultiResult result = (MultiResult) outputTarget; //NOSONAR
    InputStream is = streamSource.getInputStream();

    try {
      result.addNewResult(streamSource.getSystemId());
      URI systemIdUri = createUri(source.getSystemId());
      String filetype = getExtension(systemIdUri);

      // This transformer applies only on CSS Files
      if ("css".equals(filetype)) {
        String css = IOUtils.toString(is);
        addOutgoingLinks(css, systemIdUri, result);
        LOG.debug("Finished with " + result.size() + " files in the result list");
      }
    } catch (Exception e) { //NOSONAR
      LOG.error("Error finding additional Files", e);
    } finally {
      IOUtils.closeQuietly(is);
    }
  }

  private void addOutgoingLinks(String css, URI systemIdUri, MultiResult result) {
    for (StringTokenizer st = new StringTokenizer(css, "\n"); st.hasMoreTokens(); ) {
      String line = st.nextToken();
      for (StringTokenizer stmt = new StringTokenizer(line, ";"); stmt.hasMoreTokens(); ) {
        String statement = stmt.nextToken();
        if (statement.trim().length() > 0) {
          addOutgoingLinksOfStatement(statement, systemIdUri, result);
        }
      }
    }
  }

  private void addOutgoingLinksOfStatement(String statement, URI systemIdUri, MultiResult result) {
    try {
      for (StringTokenizer block = new StringTokenizer(statement, ","); block.hasMoreTokens(); ) {
        String singleBlock = block.nextToken();
        if (singleBlock.trim().length() > 0) {
          StreamSource fileToAdd = urlsToXlinks(singleBlock, systemIdUri);
          if (fileToAdd != null) {
            LOG.debug("Adding file " + fileToAdd.getSystemId());
            result.addNewResult(fileToAdd.getSystemId());
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Cannot add outgoing links", e);
    }
  }

  private StreamSource urlsToXlinks(String line, URI systemId) throws URISyntaxException {
    Matcher matcher = URL_PATTERN.matcher(line);
    while (matcher.find()) {
      URI linkUri = URI.create(matcher.group(1));
      URI linkFileUri = systemId.resolve(linkUri);
      String linkError = checkLink(linkFileUri);
      if (linkError == null) {
        URI uri = new URI(linkFileUri.getScheme(), null, null, -1, linkFileUri.getPath(), null, null);
        return new StreamSource(uri.toASCIIString());
      }
    }
    return null;
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
    boolean isOfCorrectType = false;
    List<String> extensions = (List<String>) getParameter("extensions");
    for (String extension : extensions) {
      if (linkFile.getPath().toLowerCase(Locale.getDefault()).endsWith(extension.trim())) {
        isOfCorrectType = true;
      }
    }
    if (!isOfCorrectType) {
      return "File " + linkFile.getAbsolutePath() + "  is not of correct Type";
    }
    return null;
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

  protected static String getName(URI uri) {
    String systemIdPath = uri.getPath();
    int slash = systemIdPath.lastIndexOf('/');
    return slash >= 0 ? systemIdPath.substring(slash + 1) : systemIdPath;
  }

  protected static String getExtension(URI uri) {
    String name = getName(uri);
    int dot = name.lastIndexOf('.');
    return dot >= 0 ? name.substring(dot + 1).toLowerCase(ENGLISH) : "";
  }


}
