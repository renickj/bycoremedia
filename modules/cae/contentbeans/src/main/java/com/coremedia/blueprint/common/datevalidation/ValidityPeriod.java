package com.coremedia.blueprint.common.datevalidation;

import java.util.Calendar;

public interface ValidityPeriod {
  /**
   * Returns the valid from value.
   *
   * @return the valid from value
   */
  Calendar getValidFrom();

  /**
   * Returns the valid to value.
   *
   * @return the valid to value
   */
  Calendar getValidTo();

}
