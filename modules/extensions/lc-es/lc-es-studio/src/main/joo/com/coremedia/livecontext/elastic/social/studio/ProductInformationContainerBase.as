package com.coremedia.livecontext.elastic.social.studio {

import com.coremedia.elastic.social.studio.config.contentInformationContainer;
import com.coremedia.elastic.social.studio.model.Contribution;
import com.coremedia.elastic.social.studio.model.ContributionAdministrationPropertyNames;
import com.coremedia.elastic.social.studio.model.impl.AbstractContributionAdministration;
import com.coremedia.ui.components.IconLabel;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Button;
import ext.Container;

public class ProductInformationContainerBase extends Container {
  protected static const TARGET_BUTTON_ID:String = "cm-elastic-social-target-button";
  protected static const TARGET_BUTTON_ICON_ITEM_ID:String = "cm-elastic-social-target-icon";

  private var moderationContributionAdministrationImpl:AbstractContributionAdministration;
  private var targetIconLabel:IconLabel;
  private var targetButton:Button;
  private var displayedContributionValueExpression:ValueExpression;

  public function ProductInformationContainerBase(config:contentInformationContainer) {
    moderationContributionAdministrationImpl = config.contributionAdministration as AbstractContributionAdministration;

    displayedContributionValueExpression = ValueExpressionFactory.create(
            ContributionAdministrationPropertyNames.DISPLAYED, moderationContributionAdministrationImpl);

    super(config);
  }

  override protected function afterRender():void {
    super.afterRender();
    displayedContributionValueExpression.addChangeListener(toggleTargetLink);
    displayedContributionValueExpression.addChangeListener(setContentTypeIconCssClass);
    setContentTypeIconCssClass();
    toggleTargetLink();
  }

  private function toggleTargetLink():void {
    var contribution:Contribution = moderationContributionAdministrationImpl.getDisplayed();
    if (contribution && contribution.getTarget()) {
      getTargetIcon().show();
      getTargetButton().show();
    } else {
      getTargetIcon().hide();
      getTargetButton().hide();
    }
    this.doLayout();
  }

  private function setContentTypeIconCssClass():void {
    getTargetIcon().setIconClass("");

    if (moderationContributionAdministrationImpl) {
      var displayed:Contribution = moderationContributionAdministrationImpl.getDisplayed();
      if (displayed && displayed.getTarget) {
        displayed.getTarget(function (target:*):void {
          getTargetIcon().setIconClass("content-type-xs product-icon");
        });
      }
    }
  }

  private function getTargetIcon():IconLabel {
    if (!targetIconLabel) {
      targetIconLabel = get(TARGET_BUTTON_ICON_ITEM_ID) as IconLabel;
    }

    return targetIconLabel;
  }

  private function getTargetButton():Button {
    if (!targetButton) {
      targetButton = get(TARGET_BUTTON_ID) as Button;
    }

    return targetButton;
  }

  override protected function beforeDestroy():void {
    displayedContributionValueExpression && displayedContributionValueExpression.removeChangeListener(toggleTargetLink);
    displayedContributionValueExpression && displayedContributionValueExpression.removeChangeListener(setContentTypeIconCssClass);
    super.beforeDestroy();
  }
}
}
