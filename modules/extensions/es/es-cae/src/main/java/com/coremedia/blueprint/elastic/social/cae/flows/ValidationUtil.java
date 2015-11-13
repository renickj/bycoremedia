package com.coremedia.blueprint.elastic.social.cae.flows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ValidationUtil {
  public static final String EMAIL_PATTERN = "^([\\w\\-]+)(\\.[\\w\\-]+)*@([\\w\\-]+\\.){1,5}([A-Za-z]){2,4}$";
  public static final int MINIMUM_USERNAME_LENGTH = 3;
  public static final int MINIMUM_PASSWORD_LENGTH = 4;

  private ValidationUtil() {
  }

  public static boolean validateUsernameLength(String username) {
    return username.length() >= MINIMUM_USERNAME_LENGTH;
  }

  public static boolean validateEmailAddressSyntax(String emailAddress) {
    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher = pattern.matcher(emailAddress);
    return matcher.matches();
  }

}
