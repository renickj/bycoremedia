package com.coremedia.blueprint.studio.rest.validators;

import com.coremedia.cap.common.CapType;
import com.coremedia.cap.common.descriptors.StringPropertyDescriptor;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.common.util.Function;
import com.coremedia.common.util.Predicate;
import com.coremedia.common.util.Predicates;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import com.coremedia.rest.validation.Severity;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Validates that a string property of some {@link Content} contains a unique value.
 *
 * <p>This validator does not compare the property value with other contents itself but uses a configured lookup
 * {@link Function} which returns all contents with some value. This validator creates a validation error if the
 * function returns other contents for the same value. The set of contents across which the
 * value must be unique can be constrained either by the function or by overwriting {@link #isRelevantFor(Content)}.
 *
 * <p>The configured lookup function should not perform expensive computations as it may be called quite often.
 */
public class UniqueStringValidator extends ContentTypeValidatorBase {

  private static final Logger LOG = LoggerFactory.getLogger(UniqueStringValidator.class);

  private final Function<String, Set<Content>> lookupFunction;
  private final String property;

  private String code = getClass().getSimpleName();

  public UniqueStringValidator(@Nonnull ContentRepository contentRepository,
                               @Nonnull String contentType,
                               @Nonnull String property,
                               @Nonnull Function<String, Set<Content>> lookupFunction) {
    setConnection(contentRepository.getConnection());
    setContentType(contentType);
    this.lookupFunction = requireNonNull(lookupFunction);
    this.property = requireNonNull(property);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    CapType type = getType();
    if (!(type.getDescriptor(property) instanceof StringPropertyDescriptor)) {
      throw new IllegalStateException("configured property '" + property
              + "' is not a string property of configured type '" + type.getName() + '\'');
    }
  }

  /**
   * Sets the error code used for created issues.
   *
   * <p>Defaults to the {@link Class#getSimpleName() simple name} of this class.
   *
   * @param code error code
   * @see Issues#addIssue(Severity, String, String, Object...)
   */
  public void setCode(@Nonnull String code) {
    this.code = requireNonNull(code);
  }

  @Override
  public void validate(Content content, Issues issues) {
    String myValue = content.getString(property);
    if (Strings.isNullOrEmpty(myValue)) {
      // configure a NotEmptyValidator to create an issue for empty property
      return;
    }

    Set<Content> contents = lookupFunction.apply(myValue);
    LOG.debug("Found content with property value '{}': {}", myValue, contents);

    Set<Content> conflicts = FluentIterable.from(contents)
            .filter(com.google.common.base.Predicates.not(com.google.common.base.Predicates.equalTo(content)))
            .filter(com.coremedia.common.util.undoc.Predicates.asGuava(isRelevantFor(content)))
            .toSet();
    for (Content conflict : conflicts) {
      issues.addIssue(Severity.ERROR, property, code, conflict);
    }
  }

  /**
   * Returns a predicate on {@link Content} to decide whether unique values are required for the content
   * passed to this method and the content passed to the predicate.
   *
   * <p>The default implementation returns {@link Predicates#alwaysTrue()}, which means that the validator
   * enforces unique values across all contents returned by the lookup function. Subclasses may overwrite this
   * method and return other predicates.
   *
   * @param content the content
   * @return predicate
   */
  @Nonnull
  protected Predicate<Content> isRelevantFor(@Nonnull Content content) {
    return Predicates.alwaysTrue();
  }

}
