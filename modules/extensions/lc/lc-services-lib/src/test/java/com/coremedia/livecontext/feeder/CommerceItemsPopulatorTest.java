package com.coremedia.livecontext.feeder;

import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.cap.common.CapConnection;
import com.coremedia.cap.common.CapType;
import com.coremedia.cap.common.descriptors.StringPropertyDescriptor;
import com.coremedia.cap.common.descriptors.StructPropertyDescriptor;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.feeder.MutableFeedable;
import com.coremedia.cap.feeder.TextParameters;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructService;
import com.coremedia.blueprint.base.livecontext.util.ProductReferenceHelper;
import com.coremedia.xml.Markup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProductReferenceHelper.class})
public class CommerceItemsPopulatorTest {

  @Mock
  private MutableFeedable feedable;
  @Mock
  private Content content;
  @Mock
  private ContentType contentType;
  @Mock
  private ContentRepository repository;
  @Mock
  private CapConnection connection;
  @Mock
  private StructService structService;
  @Mock
  private Struct struct, commerceStruct;
  @Mock
  private CapType structType, commerceType;
  @Mock
  private StructPropertyDescriptor commerceDescriptor;
  @Mock
  private StringPropertyDescriptor productsDescriptor;
  @Mock
  private Markup markup;

  private CommerceItemsPopulator testling;

  @Before
  public void setUp() {
    testling = new CommerceItemsPopulator();

    //mock content type hierarchy
    when(content.getType()).thenReturn(contentType);
    when(contentType.getName()).thenReturn(CMLinkable.NAME);
    when(contentType.isSubtypeOf(CMLinkable.NAME)).thenReturn(true);

    mockStatic(ProductReferenceHelper.class);
  }

  @Test
  public void populateAndFeed() {
    //this is the positive case
    List<String> productsPartNumbers = Arrays.asList("PC_WINE_GLASS", "PC_EVENING_DRESS-RED-M");
    when(ProductReferenceHelper.getExternalIds(content)).thenReturn(productsPartNumbers);

    testling.populate(feedable, content);
    verify(feedable).setElement(SearchConstants.FIELDS.COMMERCE_ITEMS.toString(), productsPartNumbers, TextParameters.NONE.asMap());
  }

  @Test
  public void populateDontFeed() {
    //test that nothing is feeded when the products are empty.
    when(ProductReferenceHelper.getExternalIds(content)).thenReturn(Collections.EMPTY_LIST);

    testling.populate(feedable, content);
    verify(feedable, never()).setElement(anyString(), anyObject(), anyMap());
  }
}
