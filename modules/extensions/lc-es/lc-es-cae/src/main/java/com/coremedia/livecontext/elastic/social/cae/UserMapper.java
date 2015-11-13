package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.ratings.LikeService;
import com.coremedia.elastic.social.api.ratings.RatingService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.livecontext.ecommerce.user.User;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;

/**
 * Helper to convert models between ES and Commerce.
 */
public class UserMapper {
  private CommentService commentService;
  private RatingService ratingService;
  private LikeService likeService;
  private CommunityUserService communityUserService;

  /**
   * Applies the ES model changes to the commerce user model.
   * @param user The target model.
   * @param userDetails The source model.
   */
  public void applyUserToPerson(User user, LiveContextUserDetails userDetails) {
    user.setEmail1(userDetails.getEmailAddress());
    user.setLastName(userDetails.getGivenname());
    user.setFirstName(userDetails.getSurname());
  }

  /**
   * Applies the commerce person data to the ES user details model.
   * @param user The commerce person model.
   * @param details The user details used in ES.
   * @param communityUser The community user details that contains the social values like comments, etc.
   */
  public void applyPersonToUserDetails(@Nullable User user, LiveContextUserDetails details, @Nonnull CommunityUser communityUser) {
    if(user != null) {
      //apply the given person details
      details.setEmailAddress(user.getEmail1());
      details.setGivenname(user.getLastName());
      details.setSurname(user.getFirstName());
      details.setUsername(user.getLogonId());
    } else {
      details.setEmailAddress(communityUser.getEmail());
      details.setGivenname(communityUser.getGivenName());
      details.setSurname(communityUser.getSurName());
      details.setUsername(communityUser.getName());
    }

    //apply fix community user details that are stored on the ES site
    details.setId(communityUser.getId());
    details.setRegistrationDate(communityUser.getRegistrationDate());
    details.setNumberOfLogins(communityUserService.getNumberOfLogins(communityUser));
    details.setNumberOfComments(commentService.getNumberOfApprovedComments(communityUser));
    details.setNumberOfRatings(ratingService.getNumberOfRatingsFromUser(communityUser));
    details.setNumberOfLikes(likeService.getNumberOfLikesFromUser(communityUser));
    details.setReceiveCommentReplyEmails(communityUser.isReceiveCommentReplyEmails());

    details.setLastLoginDate(new Date());
    details.setViewOwnProfile(true);
    details.setPreview(false);
    details.setPreModerationChanged(false);

    @SuppressWarnings({"unchecked"}) Collection<String> providerIds = communityUser.getProperty("providerIds", Collection.class);
    details.setConnectedWithTwitter(isConnectedWithProvider("twitter", providerIds));
    details.setConnectedWithFacebook(isConnectedWithProvider("facebook", providerIds));
  }


  /**
   * The method is called during the successful registration of the user.
   * Since the ES registration is called before, we can assume here that it was already successful.
   * We are copying here some ES registration values and copy them to the WC person instance.
   * @param user The person instance that is stored on the commerce site.
   * @param registration The registration instance created by the registration formular.
   */
  public void applyRegistrationToPerson(User user, LiveContextRegistration registration) {
    user.setFirstName(registration.getGivenname());
    user.setLastName(registration.getSurname());
    user.setChallengeAnswer(registration.getChallengeAnswer());
  }


  // ----------- Helper ----------------------

  private boolean isConnectedWithProvider(String providerId, Collection<String> providerIds) {
    if (providerIds != null) {
      for (String id : providerIds) {
        if (id.startsWith(providerId + ":")) {
          return true;
        }
      }
    }
    return false;
  }

  //---------- Config -------------------------

  @Required
  public void setCommunityUserService(CommunityUserService communityUserService) {
    this.communityUserService = communityUserService;
  }

  @Required
  public void setCommentService(CommentService commentService) {
    this.commentService = commentService;
  }

  @Required
  public void setRatingService(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @Required
  public void setLikeService(LikeService likeService) {
    this.likeService = likeService;
  }
}
