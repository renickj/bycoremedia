package com.coremedia.blueprint.studio.externallibraryproviders;

import com.coremedia.blueprint.studio.rest.ExternalLibraryDataItemRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryItemListRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryItemRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryPostProcessingRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryProvider;
import com.coremedia.cap.common.BlobService;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.xml.MarkupFactory;
import com.kaltura.client.KalturaApiException;
import com.kaltura.client.KalturaClient;
import com.kaltura.client.KalturaConfiguration;
import com.kaltura.client.enums.KalturaSessionType;
import com.kaltura.client.services.KalturaSessionService;
import com.kaltura.client.types.KalturaMediaEntry;
import com.kaltura.client.types.KalturaMediaEntryFilter;
import com.kaltura.client.types.KalturaMediaListResponse;
import com.kaltura.client.types.KalturaThumbAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The external library provider implementation for the Kaltura external library platform.
 */
public class KalturaVideoProvider implements ExternalLibraryProvider { // NOSONAR  cyclomatic complexity
  private static final Logger LOG = LoggerFactory.getLogger(KalturaVideoProvider.class);
  private static final long MILLIS_TO_SECONDS = 1000L;
  private static final int EXPIRY_MILLISECONDS = 86400;

  private String dataUrl;
  private String kalturaAdminName;
  private String kalturaAdminSecret;
  private String kalturaSecret;
  private Integer kalturaPartnerId;
  private String dataUrlTemplate;

  private MimeTypeService mimeTypeService;
  private String preferredSite;

  @Required
  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  @Override
  public void init(String preferredSite, Map<String, Object> parameters) {
    this.preferredSite = preferredSite;
    this.dataUrl = (String) parameters.get("dataUrl");
    this.kalturaAdminName = (String) parameters.get("kaltura.admin.name.external.account");
    this.kalturaAdminSecret = (String) parameters.get("kaltura.admin.secret.external.account");
    this.kalturaSecret = (String) parameters.get("kaltura.secret.external.account");
    this.kalturaPartnerId = (Integer) parameters.get("kaltura.partner.id.external.account");
    this.dataUrlTemplate = (String) parameters.get("dataUrl.template");
  }

  private KalturaClient createKaltureClient() {
    KalturaClient client = null;
    try {
      KalturaConfiguration kalturaConfig = new KalturaConfiguration();
      kalturaConfig.setPartnerId(kalturaPartnerId);
      kalturaConfig.setSecret(kalturaSecret);
      kalturaConfig.setAdminSecret(kalturaAdminSecret);
      kalturaConfig.setEndpoint(dataUrl);
      client = new KalturaClient(kalturaConfig);

      KalturaSessionService sessionService = client.getSessionService();
      String sessionId = sessionService.start(kalturaConfig.getAdminSecret(), kalturaAdminName,
              KalturaSessionType.ADMIN, kalturaConfig.getPartnerId(), EXPIRY_MILLISECONDS, "");
      client.setSessionId(sessionId);
    } catch (KalturaApiException ke) {
      LOG.error("Error initializing Kaltura connection: " + ke.getMessage(), ke);
    } catch (Exception ke) {
      LOG.error("Error initializing Kaltura Video Provider: " + ke.getMessage(), ke);
    }
    return client;
  }

  @Override
  public ExternalLibraryItemListRepresentation getItems(String filter) {
    ExternalLibraryItemListRepresentation result = new ExternalLibraryItemListRepresentation();
    KalturaClient client;
    try {
      client = createKaltureClient();
    } catch (NoClassDefFoundError e) {
      LOG.warn("Problems contacting Kaltura Sevice. Maybe the credentials are not set correctly.");
      result.setErrorMessage("Problems contacting Kaltura Sevice. Maybe the credentials are not set correctly.");
      return result;
    }

    try {
      KalturaMediaListResponse response;
      //filter is set?
      if (filter == null || filter.length() == 0) {
        response = client.getMediaService().list();
      } else {
        //yes, so apply the search text matching with OR
        KalturaMediaEntryFilter kFilter = new KalturaMediaEntryFilter();
        kFilter.searchTextMatchOr = filter;
        response = client.getMediaService().list(kFilter);
      }

      //convert the result list into common external content entries.
      for (KalturaMediaEntry entry : response.objects) {
        ExternalLibraryItemRepresentation video = buildVideoRepresentation(null, entry);
        if (video != null && video.matches(filter)) {
          result.add(video);
        }
      }
    } catch (KalturaApiException ke) {
      LOG.error("Error reading Kaltura base entry list: " + ke.getMessage(), ke);
      result.setErrorMessage(ke.getMessage());
    } catch (Exception e) {
      LOG.error("Error in Kaltura Video Provider: " + e.getMessage());
      result.setErrorMessage(e.getMessage());
    }
    return result;
  }


  @Override
  public ExternalLibraryItemRepresentation getItem(String id) {
    KalturaClient client = createKaltureClient();
    try {
      KalturaMediaEntry entry = client.getMediaService().get(id);
      if (entry != null) {
        return buildVideoRepresentation(client, entry);
      }
    } catch (KalturaApiException ke) {
      LOG.info("Could not find Kaltura external library for id {}: {}", id, ke.getMessage());
    } catch (Exception e) {
      LOG.error("Error in Kaltura Video Provider: " + e.getMessage());
    }
    return null;
  }


  /**
   * Maps the Kaltura media entry pojo to the third party item representation.
   *
   * @param client The kaltura client
   * @param entry  The kaltura data entry.
   * @return The third party item filled with Kaltura data.
   */
  private ExternalLibraryItemRepresentation buildVideoRepresentation(KalturaClient client, KalturaMediaEntry entry) throws Exception {// NOSONAR  cyclomatic complexity
    ExternalLibraryItemRepresentation video = new ExternalLibraryItemRepresentation();
    video.setDataUrl(dataUrl);
    video.setAdminTags(entry.adminTags);
    video.setCategories(entry.categories);
    if (entry.createdAt > 0) {
      video.setCreatedAt(new Date(entry.createdAt * MILLIS_TO_SECONDS));
    }
    video.setDescription(entry.description);

    if (dataUrlTemplate == null) {
      String msg = "Could not create a Kaltura Video item representation, the mandatory config-parameter 'dataUrl.template'" +
              " is not set for site '" + preferredSite + "'";
      throw new Exception(msg); // NOSONAR
    }

    String playerUrl = dataUrlTemplate.replaceAll("\\{ENTRY_ID\\}", entry.id);
    playerUrl = playerUrl.replaceAll("\\{PARTNER_ID\\}", String.valueOf(kalturaPartnerId));
    video.setDownloadUrl(playerUrl);
    if (entry.endDate > 0) {
      video.setEndDate(new Date(entry.endDate * MILLIS_TO_SECONDS));
    }
    video.setGroupId(String.valueOf(entry.groupId));
    video.setId(entry.id);
    if (entry.licenseType != null) {
      video.setLicense(entry.licenseType.name());
    }
    video.setModerationCount(entry.moderationCount);
    if (entry.moderationStatus != null) {
      video.setModerationStatus(entry.moderationStatus.name());
    }
    video.setName(entry.name);
    video.setReferenceId(entry.referenceId);
    video.setSearchText(entry.searchText);
    if (entry.startDate > 0) {
      video.setStartDate(new Date(entry.startDate * MILLIS_TO_SECONDS));
    }
    if (entry.status != null) {
      video.setStatus(entry.status.name());
    }
    video.setTags(entry.tags);
    if (entry.type != null) {
      video.setTags(entry.type.name());
    }
    video.setThumbnailUri(entry.thumbnailUrl);
    if (entry.type != null) {
      video.setType(entry.type.name());
    }
    if (entry.updatedAt > 0) {
      video.setUpdatedAt(new Date(entry.updatedAt * MILLIS_TO_SECONDS));
    }
    video.setUserId(entry.userId);
    video.setVotes(entry.votes);
    if (entry.version > 0) {
      video.setVersion(String.valueOf(entry.version));
    }
    video.setDuration(entry.duration);
    video.setWidth(entry.width);
    video.setHeight(entry.height);

    if (client != null) {
      try {
        List<KalturaThumbAsset> assets = client.getThumbAssetService().getByEntryId(entry.id);
        for (KalturaThumbAsset asset : assets) {
          ExternalLibraryDataItemRepresentation item = new ExternalLibraryDataItemRepresentation(asset.description);
          item.setWidth(asset.width);
          item.setHeight(asset.height);
          item.setType(asset.fileExt);
          String url = client.getThumbAssetService().getUrl(asset.id);
          item.setValue(url);
          video.getRawDataList().add(item);
        }
      } catch (Exception e) {
        LOG.error("Error accessing Kaltura media assets: " + e.getMessage(), e);
      }
    }

    return video;
  }

  @Override
  public void postProcessNewContent(ExternalLibraryItemRepresentation item, ExternalLibraryPostProcessingRepresentation representation) {
    Content content = representation.getCreatedContent();
    content.set("teaserTitle", item.getName());
    content.set("title", item.getName());
    content.set("dataUrl", item.getDownloadUrl());
    addAssets(item, representation);
    if (item.getHeight() > 0) {
      content.set("height", item.getHeight());
    }
    if (item.getWidth() > 0) {
      content.set("width", item.getWidth());
    }

    if (item.getDescription() != null) {
      String description = item.getDescription();
      String teaserText = "<?xml version=\"1.0\" ?><div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p>" + description + "</p></div>";
      content.set("teaserText", MarkupFactory.fromString(teaserText));
    }
  }

  private void addAssets(ExternalLibraryItemRepresentation videoItem, ExternalLibraryPostProcessingRepresentation rep) {
    try {
      Content content = rep.getCreatedContent();
      List<Content> imageList = new ArrayList<>();
      int index = 0;
      for (ExternalLibraryDataItemRepresentation item : videoItem.getRawDataList()) {
        index++;
        String imageUrl = item.getValue();
        URL url = new URL(imageUrl);
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String folder = content.getParent().getPath();
        String pictureName = videoItem.getName() + " (Image " + index + ")";

        CapConnection connection = content.getRepository().getConnection();
        BlobService blobService = connection.getBlobService();
        ContentType contentType = connection.getContentRepository().getContentType("CMPicture");
        Content newImageContent = contentType.createByTemplate(connection.getContentRepository().getChild(folder), pictureName, "{3} ({1})", new HashMap<String, Object>());
        String mimeTypeString = mimeTypeService.getMimeTypeForExtension(item.getType());
        if (mimeTypeString == null) {
          mimeTypeString = "image/jpeg";
        }
        MimeType mimeType = new MimeType(mimeTypeString);
        newImageContent.set("data", blobService.fromInputStream(in, mimeType));
        in.close();

        newImageContent.checkIn();
        imageList.add(newImageContent);
        rep.addCreatedContent(newImageContent);
      }
      content.set("pictures", imageList);
    } catch (MalformedURLException e) {
      LOG.error("Error adding thumbnail image to video content: " + e.getMessage(), e);
    } catch (MimeTypeParseException e) {
      LOG.error("Error adding thumbnail image to video content: " + e.getMessage(), e);
    } catch (IOException e) {
      LOG.error("Error adding thumbnail image to video content: " + e.getMessage(), e);
    }
  }
}
