package com.coremedia.livecontext.elastic.social.rest;

import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.rest.api.JsonCustomizer;
import com.coremedia.elastic.social.rest.api.JsonProperties;
import org.springframework.core.annotation.Order;

import javax.inject.Named;
import java.util.Map;

/**
 * In LC we do not display user details in the Studio moderation.
 * Consequently, we set the preview URL in the CommunityUser
 * serialization to null (it is initially set by the CommunityUserJsonSerializer).
 * This customizer receives order 2. This way, other customizers
 * may explicitly be executed earlier if desired.
 *
 */
@Named
@Order(2)
public class LcCommunityUserJsonCustomizer implements JsonCustomizer<CommunityUser> {
  @Override
  public void customize(CommunityUser communityUser, Map<String, Object> serializedObject) {  // NOSONAR unused parameters
    serializedObject.put(JsonProperties.PREVIEW_URL, null);
  }
}