package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.TransformerParameters;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;

import static org.junit.Assert.*;

public class CssTransformerTest {
  private static final Logger LOG = LoggerFactory.getLogger(CssTransformerTest.class);
  public static final String EXPECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE coremedia PUBLIC \"-//CoreMedia AG//DTD CoreMedia 3.2//EN\" \"classpath:xml/coremedia.dtd\"><coremedia><document type=\"CMCSS\" name=\"test.css\" path=\"/Sites/design/css/\" id=\"test.css\"><version number=\"1\"><text name=\"code\"><div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>/* without quotation marks */</p><p>a.pdf {</p><p>  background-image: url(<img xlink:href=\"coremedia:///cap/resources/word.gif/data\" alt=\"\" xlink:actuate=\"onLoad\" xlink:show=\"embed\" xlink:type=\"simple\"/>);</p><p>}</p><p>/* with quotation marks */</p><p>a.doc2 {</p><p>  background-image: url(<img xlink:href=\"coremedia:///cap/resources/word.gif/data\" alt=\"\" xlink:actuate=\"onLoad\" xlink:show=\"embed\" xlink:type=\"simple\"/>);</p><p>}</p><p>/* with double quotation marks */</p><p>a.doc {</p><p>  background-image: url(<img xlink:href=\"coremedia:///cap/resources/word.gif/data\" alt=\"\" xlink:actuate=\"onLoad\" xlink:show=\"embed\" xlink:type=\"simple\"/>);</p><p>}</p><p>/* with parameters */</p><p>a.doc3 {</p><p>  background-image: url(<img xlink:href=\"coremedia:///cap/resources/word.gif/data\" alt=\"\" xlink:actuate=\"onLoad\" xlink:show=\"embed\" xlink:type=\"simple\"/>#iefix);</p><p>}</p><p>/* with base64 encoded image */</p><p>a.doc4 {</p><p>  background-image: url(data:image/gif;base64,R0lGODlhEAAQAPcAAAEyeCg+bQgviwU2ggg8iAZCmwlLsiFMmjpamDJbtipitzhhrjppuE1qp0BmtERquVVtpF11q2d+s0JuxEl0zFJ3ylV7zl99w1h+0XeKnG6Ov3KQv3KRv3aTvXqVu3uVvH6XulWAyFmBxliCxV2ExF6ExGCBzWGIw2KJw2WKwmeLwWmMwWyOwGeK1XeR1XyX2P8A/4KavIWdvoOc2oCe5oigwIuiwoyiwouk3ZGnxpesyZCu1p2xzYml6ZOr5qO20am71K260K+836q+8a/A2LPD2rfI9MnS4tbc6tLi+tTj+tbk+tfl+9zi9Nnm+9vn+9zo+97p++Dq/OHs/OPt/OXu/Obv/Ojw/erx/evy/e3z/e/0/fDy+vD2/vL3/vT4/vX5/vf6/vn7/////wCpEQAAABLs7NS5srGlQNcVPRQCgBQCQBLtDNdNrxQCgGQCeNdN4xQCgBLtFAAAAJEFyCNr8BLt4JEFURQHqJEFbRLuOAAAABLtPAAAAJEFyFiHuBLuCJEFURQHSBLtWAAAAJEFyFiHuBLuJJEFURQHSJEFbRLuaAAABAAAAOaERAAAAgAABAAAMAAAACNr+NSLsf3QAAAAMAAABBQAABLrmJD7bAAAIAAAAFiHwBLuOAAAAAAAIADwqgAAIAAAAAAAAJDnvJDVhhLuCJD7bJD7cZDVhpDnvBQAABLt5JDnyBLujJDuGJD7eAH//wAABBLtaAAAABLujJDuGJEFcP///5EFbZEJvBQAAAAAAFiHwBLuSJEJkliHwAAAABLunN3tDt3tIGKmyAABxGKm1AAAAAAAAAAAAAAAAAAAABLuaBLu7BS3YBS3YBLuoOb8I8OlLsYaoBLu2MLCzQAABMLC4xS04BS3YAAAAxSwbsXS4BSwABLu1BLupP///xLvQMNclMEgcP///8LC40SV1RS3YGMboGMboEUEtRQAABS04IoASAAAAAAAAOqG1OqG1OqG1OqG1AAC8BLvJN1sdBLvLIoASIoASObgowAACeaCsAAABCH5BAEAADAALAAAAAAQABAAAAjhAGEILALkBw8dOWzIAAFCoEMYRMSEAfPFS5ctIMY0hOHDRw8aL1pgqDBBgZaMGjmOWclypYEsKDX2GDLDBBITTSDgMICFoU8aTWZcaPKgSYMMBq5YqUJlCggXY1w8EHIAB4IjBZY2lQKixRgJDyIMSBBgTIEqO3ZIieLBwhgICIwMGBBkDIGtUaB0oDBGwAIuAxysHDAlLZQnGxi0bAlg7WEnLBQYmFygMoEBAKKkdcJEhcMbWqc4fsJ5CQqHNZimXZ12iZISDmXgfczEdRIRDmN8+NCBg4YVKU6QGBEiREAAADs=);</p><p>}</p></div></text></version></document></coremedia>";
  public static final String EXPECTED_JS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE coremedia PUBLIC \"-//CoreMedia AG//DTD CoreMedia 3.2//EN\" \"classpath:xml/coremedia.dtd\"><coremedia><document type=\"CMJavaScript\" name=\"test.js\" path=\"/Sites/design/css/\" id=\"test.js\"><version number=\"1\"><text name=\"code\"><div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>var CAE_LOGIN = function() {</p><p>    return {</p><p>        login : function() {</p><p>            var token = document.getElementById(&quot;previewToken&quot;).value;</p><p>            $.ajax({</p><p>                url:&apos;/blueprint/servlet/externalpreview?token=&apos; + token + &apos;&amp;method=login&apos;,</p><p>                dataType:&apos;json&apos;,</p><p>                cache: false,</p><p>                data:[],</p><p>                success:function (json) {</p><p>                    var errorElement = document.getElementById(&quot;login-error&quot;);</p><p>                    errorElement.style.visibility=&quot;hidden&quot;;</p><p>                    if (json.status == &apos;ok&apos;) {</p><p>                        window.location = &quot;preview.html#&quot; + token;</p><p>                    }</p><p>                    else {</p><p>                        errorElement.style.visibility=&quot;visible&quot;;</p><p>                    }</p><p>                } ,</p><p>                error:function(result) {</p><p>                    alert(result.statusText);</p><p>                }</p><p>            });</p><p>        }</p><p>    };</p><p>}();</p></div></text></version></document></coremedia>";
  public static final String EXPECTED_SWF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE coremedia PUBLIC \"-//CoreMedia AG//DTD CoreMedia 3.2//EN\" \"classpath:xml/coremedia.dtd\"><coremedia><document type=\"CMInteractive\" name=\"expressInstall.swf\" path=\"/Sites/design/css/\" id=\"expressInstall.swf\"><version number=\"1\"><blob name=\"data\" mimetype=\"application/x-shockwave-flash\" href=\"%s\"/></version></document></coremedia>";

  private CssTransformer cssTransformer;
  private File workingDir;

  @Before
  public void setUp() throws IOException, ClassNotFoundException, URISyntaxException {
    final File original = new File(CssTransformerTest.class.getResource("test.css").toURI());
    workingDir = new File(FileUtils.getTempDirectoryPath() + "/CssTransformerTest_" + System.nanoTime());
    if (!workingDir.exists()) {
      final boolean created = workingDir.mkdir();
      LOG.debug("Created directory {} for test content: {}", workingDir.getAbsolutePath(), created);
    } else {
      FileUtils.cleanDirectory(workingDir);
    }
    FileUtils.copyDirectory(original.getParentFile(), workingDir);

    final CssTransformerFactory factory = new CssTransformerFactory();
    factory.setTargetpath("/Sites/design/css");
    try {
      cssTransformer = (CssTransformer) factory.getTransformer("CssTransformer");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    cssTransformer.setParameter(TransformerParameters.MULTI_SYSTEMID, workingDir.toURI().toString());
  }

  @After
  public void tearDown() throws IOException {
    FileUtils.deleteDirectory(workingDir);
  }

  @Test
  public void relUrlPatternTest() {
    String uri = "images/socialshareprivacy_on_off.png";
    String lineWithUrl = "background-image: url(\"" + uri + "\");";
    Matcher matcher = CssTransformer.URL_PATTERN.matcher(lineWithUrl);
    assertTrue("no match", matcher.find());
    assertEquals("wrong uri", uri, matcher.group(1));
    assertNull("unexpected protocol", matcher.group(3));
    assertEquals("wrong path", "images/socialshareprivacy_on_off.png", matcher.group(4));
    assertFalse("another match", matcher.find());
  }

  @Test
  public void dataUrlPatternTest() {
    String prefix = "image/png;base64,";
    String mumboJumbo = "iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAALdJREFUeNrs18ENgjAYhmFouDOCcQJGcARHgE10BDcgTOIosAGwQOuPwaQeuFRi2p/3Sb6EC5L3QCxZBgAAAOCorLW1zMn65TrlkH4NcV7QNcUQt7Gn7KIhxA+qNIR81spOGkL8oFJDyLJRdosqKDDkK+iX5+d7huzwM40xptMQMkjIOeRGo+VkEVvIPfTGIpKASfYIfT9iCHkHrBEzf4gcUQ56aEzuGK/mw0rHpy4AAACAf3kJMACBxjAQNRckhwAAAABJRU5ErkJggg==";
    String uri = "data:" + prefix + mumboJumbo;
    String lineWithUrl = "background-image: url('" + uri + "');";
    Matcher matcher = CssTransformer.URL_PATTERN.matcher(lineWithUrl);
    assertTrue("no match", matcher.find());
    assertEquals("wrong uri", uri, matcher.group(1));
    assertEquals("wrong protocol", "data", matcher.group(3));
    assertEquals("wrong path", prefix+mumboJumbo, matcher.group(4));
    assertFalse("another match", matcher.find());
  }

  @Test
  public void multiColonPatternTest() {
    String uri = "data::image/png;base64,foo:bar";
    String lineWithUrl = "background-image: url(" + uri + ");";
    Matcher matcher = CssTransformer.URL_PATTERN.matcher(lineWithUrl);
    assertTrue("no match", matcher.find());
    assertEquals("wrong uri", uri, matcher.group(1));
    assertEquals("wrong protocol", "data", matcher.group(3));
    assertEquals("wrong path", ":image/png;base64,foo:bar", matcher.group(4));
    assertFalse("another match", matcher.find());
  }

  @Test
  public void testTransformCss() throws IOException, TransformerException {
    final File cssFile = new File(workingDir + "/test.css");
    final Source source = new StreamSource(FileUtils.openInputStream(cssFile), cssFile.toURI().toASCIIString());
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final Result outputTarget = new StreamResult(out);
    cssTransformer.transform(source, outputTarget);
    final String string = new String(out.toByteArray());
    assertEquals(EXPECTED, string.trim());
  }

  @Test
  public void testTransformJavaScript() throws IOException, TransformerException {
    final File jsFile = new File(workingDir + "/test.js");
    final Source source = new StreamSource(FileUtils.openInputStream(jsFile), jsFile.toURI().toASCIIString());
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final Result outputTarget = new StreamResult(out);
    cssTransformer.transform(source, outputTarget);
    final String string = new String(out.toByteArray());
    assertEquals(EXPECTED_JS, string.trim());
  }

  @Test
  public void testTransformSwf() throws IOException, TransformerException {
    final File swfFile = new File(workingDir + "/expressInstall.swf");
    final Source source = new StreamSource(FileUtils.openInputStream(swfFile), swfFile.toURI().toASCIIString());
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final Result outputTarget = new StreamResult(out);
    cssTransformer.transform(source, outputTarget);
    final String string = new String(out.toByteArray());
    assertEquals(String.format(EXPECTED_SWF, swfFile.toURI().toASCIIString()), string.trim());
  }
}
