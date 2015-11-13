package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.settings.SettingsFinder;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.navigation.PagePrefixContentKeywords.SETTINGS_SETTING_NAME;

/**
 * Request specific SettingsFinder
 * <p>
 * Effective for contents of type CMChannel and its subtypes.
 * <p>
 * Use with care: Calculations which invoke this SettingsFinder MUST NOT be cached!
 */
public class CompositeNameSettingsFinder implements SettingsFinder {

  private static final String CMChannel = "CMChannel";

  private SettingsFinder delegate;
  private TreeRelation<Content> hierarchy = null;

  /**
   * Maps the compositeName to a page key and fetches the setting with
   * the delegate finder.
   * The value of the setting is supposed to be a struct, or any other
   * object suitable as source bean for the SettingsService.
   */
  private Object getCompositeNameConfiguration(SettingsService settingsService, String compositeName, Object object) {
    String pagePrefix = CompositeNameHelper.getPagePrefix(compositeName);
    if (pagePrefix != null) {
      return delegate.setting(object, pagePrefix, settingsService);
    }
    return null;
  }

  /**
   * Fetches the "settings" setting from the compositeNameConfiguration.
   */
  @Nonnull
  private Map<String, Object> getCompositeNameSettings(Content content, SettingsService settingsService, String compositeName) {
    Object configuration = getCompositeNameConfiguration(settingsService, compositeName, content);
    return settingsService.mergedSettingAsMap(SETTINGS_SETTING_NAME, String.class, Object.class, configuration);
  }

  @Override
  public Object setting(Object bean, String name, SettingsService settingsService) {
    if (!(bean instanceof Content)) {
      return null;
    }
    Content content = (Content)bean;
    String compositeName = CompositeNameHelper.getCurrentCompositeName();

    // Responsible for Content objects of type CMChannel
    if (content.getType().isSubtypeOf(CMChannel) && CompositeNameHelper.isCompositeName(compositeName)) {
      return compositeSetting(name, settingsService, content, compositeName);
    } else {
      return delegate.setting(bean, name, settingsService);
    }
  }

  private Object compositeSetting(String name, SettingsService settingsService, Content content, String compositeName) {
    Object result = null;
    final Collection<Object> listOfBeans = new ArrayList<>();

    // settings to consider come from compositeNameSettings...
    Map<String, Object> compositeNameSettings = getCompositeNameSettings(content, settingsService, compositeName);
    listOfBeans.add(compositeNameSettings);

    // add Map with setting from root channel as fallback
    List<Content> path = hierarchy.pathToRoot(content);
    if (!path.isEmpty()) {
      Content root = Iterables.getLast(path);
      Object setting = delegate.setting(root, name, settingsService);
      if (setting != null) {
        listOfBeans.add(ImmutableMap.of(name, setting));
      }
    }

    if (!listOfBeans.isEmpty()) {
      Object[] beans = listOfBeans.toArray();
      result = settingsService.setting(name, Object.class, beans);
      if (result instanceof Map) {
        result = settingsService.mergedSettingAsMap(name, String.class, Object.class, beans);
      }
    }
    return result;
  }

  @Required
  public void setDelegate(SettingsFinder delegate) {
    this.delegate = delegate;
  }

  @Required
  public void setHierarchy(TreeRelation<Content> hierarchy) {
    this.hierarchy = hierarchy;
  }
}
