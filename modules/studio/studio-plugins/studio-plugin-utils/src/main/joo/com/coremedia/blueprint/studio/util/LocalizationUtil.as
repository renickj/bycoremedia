package com.coremedia.blueprint.studio.util {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.LocaleUtil;

/**
 * Util class for generell localization helper methods
 */
public class LocalizationUtil {

  /**
   * private constructor as it is a Util class which only provides static helper methods
   */
  function LocalizationUtil() {
  }

  /**
   * This method provides a localized version of a content gathered by the following strategy:
   * It takes the actual local for the logged in user. Then first it tries to fetch a content in the same
   * folder as the origin content with _[locale] Prefix. If no content is found, it looks for a folder named with
   * locale, and then looks there, if a content with the same name as the origin exists.
   *
   * This is the synchronous variant for usage in function value expressions
   * @param c the origin content to search a translation for
   * @return the translated content, the origin content, if no translation is found, or undefined, if
   *         the async loading of contents is not finished yet.
   */
  public static function getLocalizedContentSync(c:Content): Content {
    var folder:Content = c.getParent();
    if (folder && folder.getChildrenByName()) {
      var name:String = c.getName();
      var locale:String = LocaleUtil.getLocale();
      var locContent:Content = folder.getChildrenByName()[name+"_"+locale];
      if (!locContent) { // If no content exists, then try the subfolder strategy
        var locFolder:Content = folder.getChildrenByName()[locale];
        if (!locFolder) {
          return c;
        } else if (locFolder && locFolder.getProperties()) {
          if (locFolder.isFolder()) {
            var locContentInFolder:Content = locFolder.getChildrenByName()[name];
            if (!locContentInFolder) {
              return c;
            } else if (locContentInFolder && locContentInFolder.getProperties()){
              return locContentInFolder;
            } else {
              return undefined;
            }
          }
        } else {
          return undefined;
        }
      } else if (locContent && locContent.getProperties()) {
        return locContent;
      } else {
        return undefined;
      }
    } else {
      return undefined;
    }
    return c;
  }
}
}