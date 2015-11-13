package com.coremedia.blueprint.common.importfilter;


import com.coremedia.publisher.importer.MultiResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class InboxResultGeneratorTest {
  private static final String FILE_PATTERN = "(.*\\.css|.*\\.js|.*\\.swf)";

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void simpleInbox() throws Exception {
    // setup
    File inbox = createSimpleInbox();
    List<String> paths = paths(inbox);

    // test
    InboxResultGenerator testling = new InboxResultGenerator(paths, FILE_PATTERN);
    checkOneInbox(testling, 1);
  }

  @Test
  public void testEmptyInbox() throws Exception {
    // setup
    File inbox = createEmptyInbox();
    List<String> paths = paths(inbox);

    // test
    InboxResultGenerator testling = new InboxResultGenerator(paths, FILE_PATTERN);
    MultiResult multiResult = testling.next();
    assertNull("Result for empty inbox", multiResult);
  }

  @Test
  public void testMessyInbox() throws Exception {
    // setup
    File inbox = createMessyInbox();
    List<String> paths = paths(inbox);

    // test
    InboxResultGenerator testling = new InboxResultGenerator(paths, FILE_PATTERN);
    checkOneInbox(testling, 3);
  }

  @Test
  public void testOnlyIrrelevantFiles() throws Exception {
    // setup
    File inbox = createIrrelevantInbox();
    List<String> paths = paths(inbox);

    // test
    InboxResultGenerator testling = new InboxResultGenerator(paths, FILE_PATTERN);
    MultiResult multiResult = testling.next();
    assertNull("Result for irrelevant inbox", multiResult);
  }

  @Test
  public void testNestedInbox() throws Exception {
    // setup
    File inbox = createNestedInbox();
    List<String> paths = paths(inbox);

    // test
    InboxResultGenerator testling = new InboxResultGenerator(paths, FILE_PATTERN);
    checkOneInbox(testling, 4);
  }

  @Test
  public void testMultipleInboxes() throws IOException {
    // setup
    File emptyInbox = createEmptyInbox();  // ignored
    File simpleInbox = createSimpleInbox();  // multiresult
    File irrelevantInbox = createIrrelevantInbox();  // ignored
    File messyInbox = createMessyInbox();  // multiresult
    File nestedInbox = createNestedInbox();  // multiresult
    List<String> paths = paths(emptyInbox, simpleInbox, irrelevantInbox, messyInbox, nestedInbox);

    // test
    InboxResultGenerator testling = new InboxResultGenerator(paths, FILE_PATTERN);
    assertNotNull("No first", testling.next());
    assertNotNull("No second ", testling.next());
    assertNotNull("No third", testling.next());
    assertNull("Unexpected fourth", testling.next());
  }


  // --- utilities --------------------------------------------------

  private List<String> paths(File... inboxes) {
    List<String> paths = new ArrayList<>();
    for (File inbox : inboxes) {
      paths.add(inbox.getAbsolutePath());
    }
    return paths;
  }

  private File createEmptyInbox() throws IOException {
    return folder.newFolder("empty");
  }

  private File createSimpleInbox() throws IOException {
    File inbox = folder.newFolder("simple");
    createFiles(inbox, "foo.css");
    return inbox;
  }

  private File createMessyInbox() throws IOException {
    File inbox = folder.newFolder("messy");
    createFiles(inbox, "foo.css", "foo.js", "foo.swf", "foo", "foo.exe");
    return inbox;
  }

  private File createIrrelevantInbox() throws IOException {
    File inbox = folder.newFolder("irrelevant");
    createFiles(inbox, "foo", "foo.exe");
    return inbox;
  }

  private File createNestedInbox() throws IOException {
    File inbox = folder.newFolder("nested");
    createFiles(inbox, "foo.css", "foo.swf", "foo");
    File nested = new File(inbox, "sub");
    if (!nested.mkdir()) {
      throw new IOException("Test setup failed, cannot create folder " + nested.getAbsolutePath());
    }
    createFiles(nested, "foo.css", "foo.js", "foo.exe");
    return inbox;
  }

  private static void createFiles(File inbox, String... filenames) throws IOException {
    for (String filename : filenames) {
      File file = new File(inbox, filename);
      if (!file.createNewFile()) {
        throw new IOException("Test setup failed, cannot create file " + file.getAbsolutePath());
      }
    }
  }

  private void checkOneInbox(InboxResultGenerator testling, int expectedSize) throws Exception {
    MultiResult multiResult = testling.next();
    assertNotNull("No MultiResult", multiResult);
    assertEquals("Wrong size", expectedSize, multiResult.size());
    multiResult = testling.next();
    assertNull("Another result", multiResult);
  }

}
