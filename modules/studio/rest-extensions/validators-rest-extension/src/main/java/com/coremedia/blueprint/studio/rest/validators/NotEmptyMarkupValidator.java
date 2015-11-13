package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.xml.MarkupUtil;
import com.coremedia.rest.validation.PropertyValidatorBase;
import com.coremedia.xml.Markup;

/**
 * <p>Validate richtext to be not empty.
 * Null values are legal, but not valid.</p>
 * <p>Empty means that the markup has at least one element containing characters not being whitespaces
 * or line breaks</p>
 *
 * <ul>
 *   <li>{@code <div><p></p></div>} is invalid</li>
 *   <li>{@code <div><p> </p></div>} is invalid</li>
 *   <li>{@code <div><p><br/></p></div>} is invalid</li>
 *   <li>{@code <div><p>Some text.</p></div>} is valid</li>
 *   <li>{@code <div><p></p><p>Some text.</p></div>} is valid</li>
 * </ul>
 */
public class NotEmptyMarkupValidator extends PropertyValidatorBase<Markup> {

  public NotEmptyMarkupValidator() {
    super(Markup.class);
  }

  @Override
  protected boolean isValid(Markup value) {
    return !MarkupUtil.isEmptyRichtext(value, true);
  }

}
