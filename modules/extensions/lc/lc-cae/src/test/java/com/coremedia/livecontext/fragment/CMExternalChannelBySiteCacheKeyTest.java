package com.coremedia.livecontext.fragment;

import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CMExternalChannelBySiteCacheKeyTest {

  @Mock
  Site site;
  @Mock
  Content rootFolder;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  ContentRepository contentRepository;
  @Mock
  ContentType externalChannelContentType;

  private Cache cache;

  @Before
  public void setup() {
    cache = new Cache("cache");
    cache.setCapacity(Object.class.getName(), 42);

    when(site.getSiteRootFolder()).thenReturn(rootFolder);
    when(rootFolder.getRepository()).thenReturn(contentRepository);
    when(contentRepository.getContentType("CMExternalChannel")).thenReturn(externalChannelContentType);
    Collection<Content> externalChannels = emptyList();
    channelsFulfilling(externalChannels);
  }

  void channelsFulfilling(Collection<Content> externalChannels) {
    when(contentRepository.getQueryService().getContentsFulfilling(anyCollectionOf(Content.class), anyString(), anyVararg()))
            .thenReturn(externalChannels);
  }

  @Test
  public void contentTypeMissing() throws Exception {
    when(contentRepository.getContentType("CMExternalChannel")).thenReturn(null);
    assertEquals(emptyMap(), cache.get(new CMExternalChannelBySiteCacheKey(site)));
    assertEquals(emptyMap(), cache.get(new CMExternalChannelBySiteCacheKey(site)));
  }

  @Test
  public void evaluateEmpty() throws Exception {
    assertEquals(emptyMap(), cache.get(new CMExternalChannelBySiteCacheKey(site)));
    assertEquals(emptyMap(), cache.get(new CMExternalChannelBySiteCacheKey(site)));
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

    Map<String, Object> expected = ImmutableMap.<String, Object>of("hi", content1, "ho", content2);
    assertEquals(expected, cache.get(new CMExternalChannelBySiteCacheKey(site)));
    assertEquals(expected, cache.get(new CMExternalChannelBySiteCacheKey(site)));
  }

}