package com.coremedia.blueprint.studio.components {

import com.coremedia.blueprint.studio.config.components.pathDoctypeContentSelectorPropertyField;
import com.coremedia.cms.editor.sdk.util.PathFormatter;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cms.editor.sdk.premular.fields.ContentListChooserPropertyField;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.logging.Logger;

public class PathDoctypeContentSelectorPropertyFieldBase extends ContentListChooserPropertyField {

  private var paths:Array;
  private var allowedDocTypes:Array;
  private var bindTo:ValueExpression;
  private var comparator:Function;

  public function PathDoctypeContentSelectorPropertyFieldBase(config:pathDoctypeContentSelectorPropertyField = null) {
    paths = config.paths;
    bindTo = config.bindTo;
    allowedDocTypes = config.allowedDocTypes;
    comparator = config.sortComparator;
    super(config);
  }

  public function getContentList(noSelectionBean:Bean):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Array {
      var contentListResult:Array = noSelectionBean ? [noSelectionBean] : [];
      var contentRepo:ContentRepository = session.getConnection().getContentRepository();

      // Method returns undefined until this flag is still true at the end of the function.
      var ready:Boolean = true;
      paths.forEach(function (path:String):void {
        path = PathFormatter.formatSitePath(path, bindTo.getValue());
        if(!path) {
          return;
        }
        var currentNode:Content = contentRepo.getRoot();
        var pathSegments:Array = path.split("/");
        pathSegments = pathSegments.splice(1,pathSegments.length);

        var validPath:Boolean = true;
        pathSegments.forEach(function (segment:String):void {
          if (currentNode && currentNode.getChildrenByName()) { // currentNode is loaded
            if (!currentNode.getChildrenByName()[segment]) {
              validPath = false;
            } else {
              currentNode = currentNode.getChildrenByName()[segment]
            }
          } else { // currentNode is not loaded
            currentNode = undefined;
          }
        });
        if (validPath) {
          if (currentNode && currentNode.getChildDocuments()) {
            currentNode.getChildDocuments().forEach(function (c:Content):void {
              if (c.getType()) {
                if (allowedDocTypes.indexOf(c.getType().getName()) !== -1) {
                  if (contentListResult.indexOf(c) === -1) {
                    contentListResult.push(c);
                  }
                }
              } else {
                ready = false;
              }
            });
          } else {
            ready = false;
          }
        } else {
          Logger.info(ViewtypeContentSelectorPropertyField+": No valid documents are found in configured path '" + path + "'.");
        }
      });
      if (ready) {
        // All contents should be loaded by now, adding a comparator does not cause the FVE to fire again.
        return contentListResult.sort(comparator);
      } else {
        // Make sure that the contentList remains undefined until it is complete.
        return undefined;
      }
    });
  }

}
}
