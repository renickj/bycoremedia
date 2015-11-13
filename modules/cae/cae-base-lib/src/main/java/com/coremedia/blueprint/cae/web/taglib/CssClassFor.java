package com.coremedia.blueprint.cae.web.taglib;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.jstl.core.LoopTagStatus;
import java.util.List;

/**
 * Helper class to generate css classes used in JSP Taglibs.
 * For Freemarker use {@link BlueprintFreemarkerFacade} instead.
 */
public final class CssClassFor {

  // static class
  private CssClassFor() {
  }

  /**
   * generates cssClass based on the {@link javax.servlet.jsp.jstl.core.LoopTagStatus}
   *
   * @param status    the {@link javax.servlet.jsp.jstl.core.LoopTagStatus} to use
   * @param firstLast true if cssClass should be firstLast sensitive, false otherwise
   * @param oddEven   true if cssClass should be oddEven sensitive, false otherwise
   * @return cssClass based on the {@link javax.servlet.jsp.jstl.core.LoopTagStatus}
   */
  private static String cssClassFor(LoopTagStatus status, boolean firstLast, boolean oddEven, boolean createCssClassAttribute) {
    StringBuilder result = new StringBuilder();
    if (firstLast) {
      if (status.isFirst()) {
        result.append("first ");
      }
      if (status.isLast()) {
        result.append("last ");
      }
    }
    if (oddEven) {
      if (status.getIndex() % 2 == 0) {
        result.append("even ");
      } else {
        result.append("odd ");
      }
    }
    String toReturn = result.toString().trim();
    if (createCssClassAttribute) {
      toReturn = " class=\"" + toReturn + "\"";
    }
    return toReturn;
  }

  /**
   * generates cssClass based on the {@link javax.servlet.jsp.jstl.core.LoopTagStatus}
   *
   * @param status the {@link javax.servlet.jsp.jstl.core.LoopTagStatus} to use
   * @return a cssClass containing first, last or both and odd or even based on the {@link javax.servlet.jsp.jstl.core.LoopTagStatus}
   */
  public static String cssClassFor(LoopTagStatus status, boolean createCssClassAttribute) {
    return cssClassFor(status, true, true, createCssClassAttribute);
  }

  /**
   * generates cssClass based on the {@link javax.servlet.jsp.jstl.core.LoopTagStatus}
   *
   * @param status the {@link javax.servlet.jsp.jstl.core.LoopTagStatus} to use
   * @return a cssClass containing first, last or both based on the {@link javax.servlet.jsp.jstl.core.LoopTagStatus}
   */
  public static String cssClassForFirstLast(LoopTagStatus status, boolean createCssClassAttribute) {
    return cssClassFor(status, true, false, createCssClassAttribute);
  }

  /**
   * generates cssClass based on the {@link javax.servlet.jsp.jstl.core.LoopTagStatus}
   *
   * @param status the {@link javax.servlet.jsp.jstl.core.LoopTagStatus} to use
   * @return a cssClass containing odd or even based on the {@link javax.servlet.jsp.jstl.core.LoopTagStatus}
   */
  public static String cssClassForOddEven(LoopTagStatus status, boolean createCssClassAttribute) {
    return cssClassFor(status, false, true, createCssClassAttribute);
  }

  /**
   * Appends @{code appendix} to provided CSS class, if navigation is part of current navigation path list. If
   * provided CSS class is not blank, appendix will automatically prefixed with a blank space.
   *
   * @param currentCssClass    Current CSS class, might be @{code null} or blank.
   * @param appendix           Appendix.
   * @param navigation         Navigation.
   * @param navigationPathList Navigation path list.
   * @return CSS class appended with "active", if navigation is part of provided navigation path list.
   */
  public static String cssClassAppendNavigationActive(String currentCssClass, String appendix, CMNavigation navigation, List<CMNavigation> navigationPathList) {
    if (navigationPathList.contains(navigation)) {
      return StringUtils.isBlank(currentCssClass) ? appendix : currentCssClass + " " + appendix;
    } else {
      return currentCssClass;
    }
  }
}
