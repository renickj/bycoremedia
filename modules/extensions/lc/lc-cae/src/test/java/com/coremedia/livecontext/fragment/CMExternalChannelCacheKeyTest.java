package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CMExternalChannelCacheKeyTest {

  @Mock
  Site site;
  @Mock
  Content rootFolder;
  @Mock
  Content rootDocument;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  ContentRepository contentRepository;
  @Mock
  ContentType externalChannelContentType;
  @Mock
  private TreeRelation<Content> treeRelation;

  private Cache cache;

  @Before
  public void setup() {
    cache = new Cache("cache");
    cache.setCapacity(Object.class.getName(), 42);

    when(site.getSiteRootFolder()).thenReturn(rootFolder);
    when(site.getSiteRootDocument()).thenReturn(rootDocument);
    when(rootFolder.getRepository()).thenReturn(contentRepository);
    when(contentRepository.getContentType("CMExternalChannel")).thenReturn(externalChannelContentType);
    Collection<Content> externalChannels = emptyList();
    channelsFulfilling(externalChannels);
  }

  void channelsFulfilling(Collection<Content> externalChannels) {
    channelsFulfilling(externalChannels, this.contentRepository);
  }

  public static void channelsFulfilling(Collection<Content> externalChannels, ContentRepository contentRepository) {
    when(contentRepository.getQueryService().getContentsFulfilling(anyCollectionOf(Content.class), anyString(), anyVararg()))
            .thenReturn(externalChannels);
  }

  @Test
  public void evaluateEmpty() throws Exception {
    assertNull(cache.get(new CMExternalChannelCacheKey("gibtsgar nicht", site, treeRelation)));
  }

  @Test
  public void evaluate() throws Exception {
    // now let's add some instances
    Content content1 = mock(Content.class);
    Content content2 = mock(Content.class);
    when(content1.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn("hi");
    when(content2.getString(CMExternalChannel.EXTERNAL_ID)).thenReturn("ho");
    when(externalChannelContentType.getInstances()).thenReturn(ImmutableSet.of(content1, content2));
    channelsFulfilling(asList(content1, content2));

    when(treeRelation.pathToRoot(any(Content.class))).thenReturn(singletonList(mock(Content.class)));
    when(treeRelation.pathToRoot(eq(content1))).thenReturn(asList(rootDocument, content1));

    assertEquals(content1, cache.get(new CMExternalChannelCacheKey("hi", site, treeRelation)));
    assertEquals(content2, cache.get(new CMExternalChannelCacheKey("ho", site, treeRelation)));

    assertEquals(content1, cache.get(new CMExternalChannelCacheKey("hi", site, treeRelation)));
    assertEquals(content2, cache.get(new CMExternalChannelCacheKey("ho", site, treeRelation)));
  }

}