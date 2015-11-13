package com.coremedia.livecontext.fragment.links.transformers.resolvers.seo;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class})
public class ExternalSeoSegmentBuilderTest {

  @Mock
  private CMObject object;

  @Mock
  private CMLinkable linkable;

  @Mock
  private CMNavigation navigation;

  @Before
  public void beforeEachTest() {
    when(navigation.getContentId()).thenReturn(1234);
    when(navigation.getTitle()).thenReturn("Some Channel Title");
    when(object.getContentId()).thenReturn(3456);
    when(linkable.getContentId()).thenReturn(5678);
    when(linkable.getTitle()).thenReturn("A Perfect Dinner @ Home!");
  }

  @Test
  public void testPartialValuesReturnNull() throws Exception {
    ExternalSeoSegmentBuilder testling = new ExternalSeoSegmentBuilder();

    assertNull(testling.asSeoSegment(null, object));
    assertNull(testling.asSeoSegment(navigation, null));
  }

  @Test
  public void testAsSeoSegmentForCMObjects() throws Exception {
    ExternalSeoSegmentBuilder testling = new ExternalSeoSegmentBuilder();

    // second param is an object
    assertEquals("--1234-3456", testling.asSeoSegment(navigation, object));
  }

  @Test
  public void testAsSeoSegmentForCMLinkables() throws Exception {
    ExternalSeoSegmentBuilder testling = new ExternalSeoSegmentBuilder();

    // second param is a linkable with a title
    assertEquals("a-perfect-dinner-home--1234-5678", testling.asSeoSegment(navigation, linkable));
  }

  @Test
  public void testAsSeoSegmentForChannels() throws Exception {
    ExternalSeoSegmentBuilder testling = new ExternalSeoSegmentBuilder();

    // linkable == navigation here
    assertEquals("some-channel-title--1234", testling.asSeoSegment(navigation, navigation));
  }


  @Test
  public void testAsSeoTitleEscaping() throws Exception {
    ExternalSeoSegmentBuilder testling = new ExternalSeoSegmentBuilder();

    assertEquals("a-perfect-dinner", testling.asSeoTitle("A Perfect Dinner"));
    assertEquals("-p-rfect-dinner", testling.asSeoTitle("Ä Pörfect Dinner"));
  }
}