package com.coremedia.blueprint.studio.rest;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A POJO for building the archive file tree. This will automatically be serialized to JSON by Jersey and Jackson.
 */
final class ZipFileEntry {

  private final String name;
  private final String path;
  private final Date time;
  private final long size;
  private final boolean isDirectory;
  private final BlobZipFileEntry blobZipFileEntry;
  private final List<ZipFileEntry> children;
  private final Object url;

  // just for tree building, not serialized to JSON due to annotation @JsonIgnore:
  private Map<String, ZipFileEntry> childrenByName = new HashMap<>();

  ZipFileEntry(String name, String path, Date time, long size, boolean directory, BlobZipFileEntry blobZipFileEntry, Object url) {
    this.name = name;
    this.path = path;
    this.time = time;
    this.size = size;
    isDirectory = directory;
    this.blobZipFileEntry = blobZipFileEntry;
    this.children = directory ? new ArrayList<ZipFileEntry>() : Collections.<ZipFileEntry>emptyList();
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public Date getTime() {
    return time;
  }

  public long getSize() {
    return size;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  @JsonIgnore
  public BlobZipFileEntry getBlobZipFileEntry() {
    return blobZipFileEntry;
  }

  public List<ZipFileEntry> getChildren() {
    return children;
  }

  @JsonIgnore
  public Map<String, ZipFileEntry> getChildrenByName() {
    return childrenByName;
  }

  public Object getUrl() {
    return url;
  }
}
