package com.coremedia.blueprint.studio.forms {

import com.coremedia.blueprint.studio.config.cmSiteForm;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.DocumentTabPanel;
import com.coremedia.cms.editor.sdk.sites.SitesService;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;


public class CMSiteFormBase extends DocumentTabPanel {
  private var siteNameReadOnlyValueExpression:ValueExpression;

  public function CMSiteFormBase(config:cmSiteForm = null) {
    super(config);
  }

  internal native function get bindTo():ValueExpression;

  internal function getSiteNameReadOnlyValueExpression():ValueExpression {
    return siteNameReadOnlyValueExpression ||
            (siteNameReadOnlyValueExpression = ValueExpressionFactory.createFromFunction(calculateReadOnly));
  }

  private function calculateReadOnly():Boolean {
    // the admin user always may change the name of the site
    if (isAdministrator()) {
      return false;
    }

    var sitesService:SitesService = editorContext.getSitesService();
    var content:Content;

    if (!bindTo) {
      return true;
    }

    content = bindTo.getValue() as Content;

    if (!content) {
      return true;
    }

    return !!sitesService.getMaster(content);
  }

  protected static function isAdministrator():Boolean {
    return session.getUser().isAdministrative();
  }
}
}
