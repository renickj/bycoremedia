package com.coremedia.blueprint.studio.plugins {
import com.coremedia.blueprint.studio.config.components.siteAwareVisibilityPlugin;
import com.coremedia.blueprint.studio.util.StudioConfigurationUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.plugins.BindVisibilityPlugin;

/**
 * Baseclass for the SiteAwareStudioFeatureVisibilityPlugin.
 */
public class SiteAwareVisibilityPluginBase extends BindVisibilityPlugin {

  private static const STRINGLIST_PROPERTY_NAME:String = 'features';
  private static const STUDIO_FEATURES_BUNDLE_NAME:String = "Studio Features";

  internal var contentValueExpression:ValueExpression;
  internal var feature:String;
  internal var ifUndefined:Boolean;

  public function SiteAwareVisibilityPluginBase(config:siteAwareVisibilityPlugin) {
    super(config);
    feature = config.feature;
    contentValueExpression = config.contentValueExpression;
    ifUndefined = config.ifUndefined;
  }

  /**
   * provides the ValueExpression which calculates the visibility of the component.
   * @return A ValueExpression which evaluates to a Boolean value.
   */
  internal function getCalculateVisibilityValueExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(calculateIsVisible);
  }

  /**
   * Calculates the visibility of the component the plugin is bind to, based on the features configuration in the the
   * site, where the content in contentValueExpression belongs to.
   * @return true, if the component should be shown, else false
   */
  internal function calculateIsVisible():Boolean {
    var listOfFeatures:Array;
    if (contentValueExpression) {
      listOfFeatures = StudioConfigurationUtil.getConfiguration(STUDIO_FEATURES_BUNDLE_NAME, STRINGLIST_PROPERTY_NAME,
              contentValueExpression.getValue());
    } else {
      listOfFeatures = StudioConfigurationUtil.getConfiguration(STUDIO_FEATURES_BUNDLE_NAME, STRINGLIST_PROPERTY_NAME);
    }

    if (listOfFeatures === undefined) {
      return ifUndefined || false;
    }

    var isVisible:Boolean = listOfFeatures !== null && listOfFeatures.indexOf(feature) !== -1;
    return isVisible;
  }

  /**
   * prefetch the configuration to avoid rendering issues, if configuration state is unknown.
   * Is called in BlueprintStudioPluginBase
   */
  public static function preLoadConfiguration():void {
    var valueExpression:ValueExpression = ValueExpressionFactory.createFromFunction(function ():Boolean {
      var sites:Array = editorContext.getSitesService().getSites();

      if (sites === undefined) {
        return undefined;
      }
      for each(var site:Site in sites) {
        var siteRootDocument:Content = site.getSiteRootDocument();
        if (siteRootDocument === undefined) {
          return undefined;
        }
        var configuration:Boolean = StudioConfigurationUtil.getConfiguration(STUDIO_FEATURES_BUNDLE_NAME, STRINGLIST_PROPERTY_NAME, siteRootDocument);
        if (configuration === undefined) {
          return undefined;
        }
      }

      var globalConfiguration:Boolean = StudioConfigurationUtil.getConfiguration(STUDIO_FEATURES_BUNDLE_NAME, STRINGLIST_PROPERTY_NAME);
      if (globalConfiguration === undefined) {
        return undefined;
      }
      return true;
    });
    valueExpression.loadValue(function(value:Boolean):void {
      valueExpression = null;
    });
  }
}
}
