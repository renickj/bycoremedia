package com.coremedia.blueprint.common.services.validation;

import com.google.common.base.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

public abstract class AbstractValidator<T> implements Validator<T> {
  
  private static final Logger LOG = LoggerFactory.getLogger(AbstractValidator.class);


  // --- Validator --------------------------------------------------

  /**
   * @param source The objects to be filtered
   * @return the filtered objects
   */
  @Override
  public List<? extends T> filterList(List<? extends T> source) {
    List<? extends T> linkables = internallyFilterList(source);
    return new ArrayList<>(linkables);
  }

  /**
   * @param source A single object to be tested
   * @return true if valid, false otherwise
   */
  @Override
  public boolean validate(T source) {
    List<? extends T> results = filterList(Collections.singletonList(source));
    return !results.isEmpty();
  }


  // --- abstract ---------------------------------------------------

  protected abstract Predicate createPredicate();

  protected void addCustomDependencies(List<? extends T> result) {
    LOG.debug("The default implementation is not adding any dependencies");
  }


  // --- internal ---------------------------------------------------

  /**
   * internal method which will be called by any implementing validation service
   *
   * @param allItems the items to be filtered
   * @return the filtered objects or null
   */
  private List<T> internallyFilterList(List<? extends T> allItems) {
    //fist collect all valid items
    LOG.debug("Before selecting the list contained {} items ({})", allItems.size(), allItems);
    
    List<T> validItems = new ArrayList<>();
    Predicate predicate = createPredicate();
    for (T item : allItems) {
      if (supports(item.getClass())) {
        try {
          if (predicate.apply(item)) {
            validItems.add(item);
          }
        } catch (Exception e) {
          LOG.info("caught exception while validating item '{}'", item, e);
        }
      } else {
        validItems.add(item);
      }
    }
    LOG.debug("Afterwards {} items ({})", validItems.size(), validItems);
    addCustomDependencies(allItems);
    return validItems;
  }
}
