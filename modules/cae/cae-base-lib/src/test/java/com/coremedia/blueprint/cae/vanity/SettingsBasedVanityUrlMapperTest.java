package com.coremedia.blueprint.cae.vanity;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.links.SettingsBasedVanityUrlMapper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.coremedia.blueprint.base.links.SettingsBasedVanityUrlMapper.VANITY_URLS_KEY;
import static com.coremedia.blueprint.base.links.SettingsBasedVanityUrlMapper.VANITY_URL_ID;
import static com.coremedia.blueprint.base.links.SettingsBasedVanityUrlMapper.VANITY_URL_TARGET;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SettingsBasedVanityUrlMapperTest {
  @Test
  public void testVanityUrlMapper() {
    SettingsBasedVanityUrlMapper mapper = new SettingsBasedVanityUrlMapper(source, settingsService);
    assertTrue(mapper.patternFor(TARGET_A).equals(ID_A));
    assertTrue(mapper.forPattern(ID_A).equals(TARGET_A));
    assertTrue(mapper.forPattern(ID_B) == TARGET_EMPTY);
    assertTrue(mapper.forPattern(ID_EMPTY) == TARGET_EMPTY);
  }

  @SuppressWarnings("ConstantConditions")
  @Before
  public void defaultSetup() {
    Struct vanityUrlDefinitionA = mock(Struct.class);
    when(vanityUrlDefinitionA.get(VANITY_URL_ID)).thenReturn(ID_A);
    when(vanityUrlDefinitionA.get(VANITY_URL_TARGET)).thenReturn(TARGET_A);

    Struct emptyObject = mock(Struct.class);
    when(emptyObject.get(VANITY_URL_ID)).thenReturn(ID_B);
    when(emptyObject.get(VANITY_URL_TARGET)).thenReturn(TARGET_EMPTY);

    Struct emptyPath = mock(Struct.class);
    when(emptyPath.get(VANITY_URL_ID)).thenReturn(ID_BLANK);
    when(emptyPath.get(VANITY_URL_TARGET)).thenReturn(TARGET_B);

    Struct notAStringPath = mock(Struct.class);
    when(notAStringPath.get(VANITY_URL_ID)).thenReturn(ID_NOT_A_STRING);
    when(notAStringPath.get(VANITY_URL_TARGET)).thenReturn(TARGET_STRING);

    List<Object> vanityUrlDefinitions = ImmutableList.<Object>of(vanityUrlDefinitionA, emptyObject, emptyPath, notAStringPath);
    when(settingsService.settingAsList(VANITY_URLS_KEY, Object.class, source)).thenReturn(vanityUrlDefinitions);
  }

  @Mock
  private Content source;

  @Mock
  private SettingsService settingsService;

  private static final String ID_A = "a";
  private static final String ID_B = "b";
  private static final String ID_EMPTY = "";
  private static final String ID_BLANK = null;
  private static final Object ID_NOT_A_STRING = new ArrayList();
  private static final Object TARGET_A = new Object();
  private static final Object TARGET_B = new Object();
  private static final Object TARGET_EMPTY = null;
  private static final Object TARGET_STRING = "b";
}
