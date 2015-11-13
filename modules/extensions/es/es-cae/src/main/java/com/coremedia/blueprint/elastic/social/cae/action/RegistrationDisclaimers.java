package com.coremedia.blueprint.elastic.social.cae.action;

import com.coremedia.blueprint.common.contentbeans.CMArticle;

/**
 * Serves some documents about legal issues concerning Elastic Social.
 */
public interface RegistrationDisclaimers {
  CMArticle getLinkPrivacyPolicy();
  CMArticle getLinkTermsOfUse();
  CMArticle getLinkTooYoungPolicy();
}
