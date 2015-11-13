package com.coremedia.livecontext.studio.forms {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.DocumentTabPanel;
import com.coremedia.livecontext.studio.config.cmExternalChannelForm;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;



public class CMExternalChannelFormBase extends DocumentTabPanel{


public function CMExternalChannelFormBase(config:cmExternalChannelForm) {
    super(config);
  }

  public function getRootDocumentValueExpression(siteId:String):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():Content {
      return editorContext.getSitesService().getSiteRootDocument(siteId);
    });
  }
}
}