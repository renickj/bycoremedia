package com.coremedia.blueprint.cae.web.links;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cap.struct.Struct;
import com.coremedia.id.IdProvider;
import com.coremedia.objectserver.web.links.TokenResolver;
import org.springframework.core.annotation.Order;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * A {@link TokenResolver} that can resolve link tokens from settings.
 * The settings key can be used directly as token. This resolver supports lookup of settings
 * within a substruct by giving a token that identifies the struct followed by a "." char,
 * followed by the settings key. Example: with a token "a.b.c" at first a lookup for the whole key
 * "a.b.c" is done. If there is no setting we try subsequent calls for "a", "a.b" and "a.b" to
 * find a struct. A found struct will then be used to look for the remaining key(part) (like "b.c").
 */
@Named
@Order(10)
public class SettingsTokenResolver implements TokenResolver {

  @Inject
  private SettingsService settingsService;

  @Override
  public String resolveToken(String token, Object bean, HttpServletRequest request) {
    Object setting = null;

    if (bean instanceof IdProvider.UnknownId) {
      Object self = request.getAttribute("self");
      bean = self != null ? self : bean;
    }

    Page page = RequestAttributeConstants.getPage(request);
    if (page != null) {
      setting = settingsService.setting(token, Object.class, bean, page);
      int fromIndex = 1;
      while (setting == null && fromIndex > 0 && fromIndex < token.length()) {
        int index = token.indexOf(".", fromIndex);
        if (index > 0 && index < token.length() - 1) {
          Object value = settingsService.setting(token.substring(0, index), Object.class, bean, page);
          // it's weird, in some cases (if the settings comes from the page) it is from type "Map" and
          // in other cases (when it comes from the localSettings of the bean) it is from type "Struct".
          if (value instanceof Struct) {
            setting = ((Struct)value).get(token.substring(index + 1));
            break;
          } else if (value instanceof Map) {
            setting = ((Map)value).get(token.substring(index + 1));
            break;
          }
        }
        fromIndex = index + 1;
      }
    }
    return setting != null ? setting.toString() : null;
  }

}
