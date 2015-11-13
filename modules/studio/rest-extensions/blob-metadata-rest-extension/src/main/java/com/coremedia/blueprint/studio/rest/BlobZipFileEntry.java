package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.common.CapBlobRef;

/**
 * Pojo representing a zip file entry of a blob ref
 */
public class BlobZipFileEntry {

  private final CapBlobRef capBlobRef;
  private final String path;

  public BlobZipFileEntry(CapBlobRef capBlobRef, String path) {
    this.capBlobRef = capBlobRef;
    this.path = path;
  }

  public CapBlobRef getCapBlobRef() {
    return capBlobRef;
  }

  public String getPath() {
    return path;
  }
}
