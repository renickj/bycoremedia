package com.coremedia.blueprint.common.importfilter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SystemIdUtilTest {
  private static final String HTML = "html://www.coremedia.com/index.html";
  private static final String HTML_NO_EXTENSION = "html://www.coremedia.com/";
  private static final String FILE = "file:///foo/bar.css";
  private static final String FILE_NO_EXTENSION = "file:///foo/bar";
  private static final String FILE_UPCASE = "file:///foo/bar.JPG";

  @Test
  public void testExtension() {
    assertEquals("html", "html", SystemIdUtil.extension(HTML));
    assertEquals("html no extension", "", SystemIdUtil.extension(HTML_NO_EXTENSION));
    assertEquals("file", "css", SystemIdUtil.extension(FILE));
    assertEquals("file no extension", "", SystemIdUtil.extension(FILE_NO_EXTENSION));
    assertEquals("file upcase", "JPG", SystemIdUtil.extension(FILE_UPCASE));
  }

  public void testType() {
    assertEquals("file", "css", SystemIdUtil.type(FILE));
    assertEquals("file no extension", "", SystemIdUtil.type(FILE_NO_EXTENSION));
    assertEquals("file upcase", "jpg", SystemIdUtil.type(FILE_UPCASE));
  }
}
