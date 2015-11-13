package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.elastic.social.cae.flows.PasswordPolicy;
import com.coremedia.blueprint.elastic.social.cae.flows.UserDetails;
import com.coremedia.blueprint.elastic.social.cae.flows.UserDetailsHelper;
import com.coremedia.elastic.core.api.users.User;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.livecontext.elastic.social.cae.services.LiveContextUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.webflow.execution.RequestContext;

import java.util.Locale;

public class LiveContextUserDetailsHelper extends UserDetailsHelper {
  private LiveContextUserService userService;

  /**
   * Overwritten save method to persist the user detail changes on the commerce site too.
   * @return True if both save calls were successful.
   */
  @Override
  public boolean doSave(UserDetails userDetails, RequestContext context, CommonsMultipartFile file) {
    userDetails.validate(context);
    if(!context.getMessageContext().hasErrorMessages()) {
      LiveContextUserDetails liveContextUserDetails = (LiveContextUserDetails) userDetails;
      boolean userDataStored = userService.saveUser(liveContextUserDetails, context);
      boolean passwordStored = true;
      if(userDataStored) {
        LiveContextPasswordReset reset = new LiveContextPasswordReset();
        reset.setCurrentPassword(liveContextUserDetails.getPassword());
        reset.setPassword(liveContextUserDetails.getNewPassword());
        reset.setConfirmPassword(liveContextUserDetails.getNewPasswordRepeat());

        if(!StringUtils.isBlank(reset.getCurrentPassword())
                && !StringUtils.isBlank(reset.getPassword())
                && !StringUtils.isBlank(reset.getConfirmPassword())) {
          passwordStored = userService.updatePassword(reset, context);
        }

        //store ES data too
        CommunityUser user = getLoggedInUser();
        user.setReceiveCommentReplyEmails(userDetails.isReceiveCommentReplyEmails());
        saveChanges(user, context);
      }

      //we do not save the community user to ES here, only to commerce. So the super.doSave() is skipped.
      return passwordStored && userDataStored;
    }

    return false;
  }

  /**
   * The overwritten getDetails(...) method ensure that the data is only read from the commerce system.
   * Also, not all default fields are read from the community user.
   * @return The LiveContextUserDetail instance that contains the combination of ES and commerce data.
   */
  @Override
  protected UserDetails getDetails(User user, boolean viewOwnProfile, PasswordPolicy passwordPolicy, Locale requestLocale, boolean preview) {
    return userService.getUserDetails(user);
  }

  // --------- Config --------------------------------------

  @Required
  public void setUserService(LiveContextUserService userService) {
    this.userService = userService;
  }
}
