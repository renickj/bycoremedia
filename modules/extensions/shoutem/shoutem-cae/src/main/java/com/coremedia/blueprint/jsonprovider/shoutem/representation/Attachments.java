package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import com.coremedia.blueprint.common.contentbeans.CMMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Attachment types.
 */
public class Attachments {
  private static final String TYPE_IMAGE = "image";
  private static final String TYPE_VIDEO = "video";
  private static final String TYPE_AUDIO = "audio";

  private List<Attachment> audio = new ArrayList<>();
  private List<Attachment> videos = new ArrayList<>();
  private List<Attachment> images = new ArrayList<>();

  public void addAudio(CMMedia media, String url, String thumnailUrl) {
    addAttachment(audio, new Attachment(TYPE_AUDIO, media, url, thumnailUrl));

  }

  public void addVideo(CMMedia media, String url, String thumnailUrl, int width, int height) {
    addAttachment(videos, new Attachment(TYPE_VIDEO, media, url, thumnailUrl, width, height));
  }

  public void addImage(CMMedia media, String url, String thumnailUrl, int width, int height) {
    addAttachment(images, new Attachment(TYPE_IMAGE, media, url, thumnailUrl, width, height));
  }

  private void addAttachment(List<Attachment> items, Attachment attachment) {
    if (!items.contains(attachment)) {
      items.add(attachment);
    }
  }

  public List<Attachment> getAudio() {
    return audio;
  }

  public List<Attachment> getVideos() {
    return videos;
  }

  public List<Attachment> getImages() {
    return images;
  }
}
