package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.rest.cap.blob.metadata.BlobMetadataResolver;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Blob resolver reading the ID3 data of mp3 files.
 */
public class Mp3MetadataResolver implements BlobMetadataResolver {
  private static final Logger LOG = LoggerFactory.getLogger(Mp3MetadataResolver.class);

  private static final String PROPERTY = "property";
  private static final String SECTION = "section";
  private static final String VALUE = "value";

  private static final MimeType AUDIO_MP3 = new MimeType("audio", "mpeg3");
  private static final MimeType AUDIO_XMP3 = new MimeType("audio", "x-mpeg-3");
  private static final MimeType AUDIO_MPEG = new MimeType("audio", "mpeg");

  private String name;

  @Nonnull
  @Override
  public String getName() {
    return name;
  }

  @Required
  public void setName(@Nonnull String name) {
    Preconditions.checkNotNull(name, "name must not be null");
    this.name = name;
  }

  @Override
  public boolean canHandle(@Nonnull Blob blob) {
    final MimeType mimeType = MimeTypeUtils.parseMimeType(blob.getContentType().toString());
    return AUDIO_MP3.isCompatibleWith(mimeType) || AUDIO_XMP3.isCompatibleWith(mimeType) || AUDIO_MPEG.isCompatibleWith(mimeType);
  }

  /**
   * Reads the ID3 data of an audio file if available.
   *
   * @param blob The audio content to read the id3 data from.
   * @return The representation with the id3 properties.
   */
  @Override
  public Object resolveMetadata(@Nonnull CapBlobRef blob) {
    final List<Map<String, String>> result = new LinkedList<>();
    try {
      org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
      try (InputStream input = new BufferedInputStream(blob.getInputStream())) {
        ContentHandler handler = new DefaultHandler();
        Parser parser = new Mp3Parser();
        ParseContext parseCtx = new ParseContext();
        parser.parse(input, handler, metadata, parseCtx);
      }

      // List all metadata
      String[] metadataNames = metadata.names();

      for (String name : metadataNames) {
        Map<String, String> info = new HashMap<>();
        info.put(SECTION, "ID3");
        info.put(PROPERTY, formatKey(name));
        info.put(VALUE, formatValue(metadata.get(name)));
        result.add(info);
      }

    } catch (Exception e) {
      LOG.warn("Error processing audio meta data of {}", blob, e);
    }
    return result;
  }


  /**
   * Ensures that there is a value, non empty or null so that the template formatting is working.
   * @param value the value to format
   */
  private String formatValue(String value) {
    if(StringUtils.isEmpty(value)) {
      return "-";
    }
    return value;
  }

  /**
   * Pretty format for meta data keys.
   *
   * @param key The key to format.
   * @return the formatted key
   */
  static String formatKey(String key) {
    String result = key;
    if (result.contains(":")) {
      result = result.substring(result.lastIndexOf(':') + 1, result.length());
    }
    result = WordUtils.capitalize(result);
    result = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(result), ' ');
    result = result.replaceAll(" - ", "-");
    result = result.replaceAll(" / ", "/");
    return result;
  }
}
