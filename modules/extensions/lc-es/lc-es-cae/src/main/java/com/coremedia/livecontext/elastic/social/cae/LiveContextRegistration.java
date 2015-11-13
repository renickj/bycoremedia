package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.elastic.social.cae.flows.Registration;
import com.coremedia.blueprint.elastic.social.cae.flows.WebflowMessageKeys;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper.addErrorMessageWithSource;

public class LiveContextRegistration extends Registration {
  private Date birthdate;
  private String challengeAnswer;

  private boolean acceptTooYoungPolicy = false;

  public void validateEnterUserDetails(ValidationContext context) {
    super.validateRegistration(context);
    validateBirthdate(context);
  }

  public void validateAcceptTooYoungPolicy(ValidationContext context) {
    if (!acceptTooYoungPolicy) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_ACCEPT_TOO_YOUNG_POLICY_ERROR, "acceptTooYoungPolicy");
    }
  }

  private void validateBirthdate(ValidationContext context) {
    if (birthdate == null || birthdate.after(new Date())) {
      addErrorMessageWithSource(context, WebflowMessageKeys.REGISTRATION_BIRTHDATE_ERROR, "birthdate");
    }
  }

  public boolean isYoungerThan(int youngest) {
    LocalDate theBirthdate = new LocalDate(getBirthdate());
    LocalDate now = new LocalDate();

    Years age = Years.yearsBetween(theBirthdate, now);
    return age.getYears() < youngest;
  }

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  public Date getBirthdate() {
    return birthdate;
  }

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  public void setBirthdate(Date birthdate) {
    this.birthdate = birthdate;
  }

  public boolean isAcceptTooYoungPolicy() {
    return acceptTooYoungPolicy;
  }

  public void setAcceptTooYoungPolicy(boolean acceptTooYoungPolicy) {
    this.acceptTooYoungPolicy = acceptTooYoungPolicy;
  }

  public String getChallengeAnswer() {
    return challengeAnswer;
  }

  public void setChallengeAnswer(String challengeAnswer) {
    this.challengeAnswer = challengeAnswer;
  }
}
