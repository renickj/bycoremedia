package com.coremedia.blueprint.studio.externallibraryproviders;

import com.coremedia.blueprint.studio.rest.ExternalLibraryDataItemRepresentation;
import com.coremedia.blueprint.studio.rest.ExternalLibraryItemRepresentation;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Callable for requesting a RSS feed.
 */
public class RSSCallable implements Callable<List<ExternalLibraryItemRepresentation>> {

  public static final int UCS_CHARACTER_LINE_SEPARATOR = 8232;
  public static final int CHARACTER_SPACE = 32;
  private String rssUrl;
  private String filter;

  public RSSCallable(String url, String filter) {
    this.rssUrl = url;
    this.filter = filter;
  }

  /**
   * The callable implementation, reads the RSS stream for the given RSS URL
   * and tokenizes the returning XML representation into third party item objects.
   *
   * @return The list of third party items created for the RSS stream.
   * @throws Exception
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<ExternalLibraryItemRepresentation> call() throws IOException, FeedException {
    List<ExternalLibraryItemRepresentation> result = new ArrayList<>();
    URL feedSource = new URL(rssUrl);
    SyndFeedInput input = new SyndFeedInput();
    XmlReader reader =  new XmlReader(feedSource);
    reader.setDefaultEncoding("utf8");//NOSONAR
    SyndFeed feed = input.build(reader);
    List<SyndEntry> entries = feed.getEntries();
    for (SyndEntry syndEntry : entries) {
      ExternalLibraryItemRepresentation representation = buildThirdPartyRepresentation(syndEntry);
      if (representation.matches(filter)) {
        result.add(representation);
      }
    }
    return result;
  }

  /**
   * Conversion of the rome RSS result entry to the third party provider item.
   *
   * @param syndEntry The rome SyndEntry that contains RSS data.
   * @return The common third party data item that contains the RSS data.
   */
  private ExternalLibraryItemRepresentation buildThirdPartyRepresentation(SyndEntry syndEntry) {
    ExternalLibraryItemRepresentation item = new ExternalLibraryItemRepresentation();
    item.setDataUrl(rssUrl);
    item.setId(syndEntry.getUri());

    item.setPublicationDate(syndEntry.getPublishedDate());
    item.setCreatedAt(syndEntry.getPublishedDate());
    if (item.getCreatedAt() == null) {
      item.setCreatedAt(syndEntry.getUpdatedDate());
    }
    item.setDescription(syndEntry.getDescription().getValue());
    item.setDownloadUrl(syndEntry.getUri());
    item.setUserId(syndEntry.getAuthor());

    String title = syndEntry.getTitle();
    //a character with ASCII code 8232 was passed by bild.de
    while(title.indexOf(UCS_CHARACTER_LINE_SEPARATOR) != -1) {
      char a = UCS_CHARACTER_LINE_SEPARATOR;
      char sp = CHARACTER_SPACE;
      title = title.replace(a,sp);
    }
    item.setName(title);

    if (syndEntry.getContents() != null && !syndEntry.getContents().isEmpty()) {
      for (Object c : syndEntry.getContents()) {
        SyndContent content = (SyndContent) c;
        ExternalLibraryDataItemRepresentation dataItem = new ExternalLibraryDataItemRepresentation(ExternalLibraryDataItemRepresentation.DATA_TYPE_CONTENTS);
        dataItem.setMode(content.getMode());
        dataItem.setType(content.getType());
        dataItem.setValue(content.getValue());
        item.getRawDataList().add(dataItem);
      }
    }

    //ok, there are several more list, and some more link this pattern to fill up the REST representation.
    if (syndEntry.getEnclosures() != null && !syndEntry.getEnclosures().isEmpty()) {
      for (Object c : syndEntry.getEnclosures()) {
        SyndEnclosure content = (SyndEnclosure) c;
        ExternalLibraryDataItemRepresentation dataItem = new ExternalLibraryDataItemRepresentation(ExternalLibraryDataItemRepresentation.DATA_TYPE_ENCLOSURES);
        dataItem.setType(content.getType());
        dataItem.setValue(content.getUrl());
        dataItem.setLength(content.getLength());
        item.getRawDataList().add(dataItem);
      }
    }
    return item;
  }
}
