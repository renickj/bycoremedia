package com.coremedia.blueprint.cae.search;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * A value in a {@link Condition}. Allows to wrap multiple values connected with AND or OR in one object.
 */
public class Value {

  public enum Operators {
    AND, OR
  }

  private Collection<String> values;
  private Operators op;

  protected Value(Collection<String> values, Operators op) {
    this.values = ImmutableList.copyOf(values);
    this.op = op;
  }

  public Collection<String> getValue() {
    return values;
  }

  public void setValue(Collection<String> values) {
    this.values = values;
  }

  public Operators getOp() {
    return op;
  }

  public void setOp(Operators op) {
    this.op = op;
  }

  public static Value exactly(String s) {
    return new Value(ImmutableList.of(s), Operators.AND);
  }

  public static Value anyOf(Collection<String> c) {
    return new Value(c, Operators.OR);
  }

  public static Value allOf(Collection<String> c) {
    return new Value(c, Operators.AND);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Value)) {
      return false;
    }

    Value value = (Value) o;

    if (op != value.op) {
      return false;
    }
    if (!values.equals(value.values)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = values.hashCode();
    result = 31 * result + op.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Value{" +
            "values=" + values +
            ", op=" + op +
            '}';
  }
}
