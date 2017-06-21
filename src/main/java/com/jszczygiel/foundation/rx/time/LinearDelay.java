package com.jszczygiel.foundation.rx.time;

import java.util.concurrent.TimeUnit;

public class LinearDelay extends Delay {

  private final double growBy;
  private final long lower;
  private final long upper;

  LinearDelay(TimeUnit unit, long upper, long lower, double growBy) {
    super(unit);

    this.growBy = growBy;
    this.lower = lower;
    this.upper = upper;
  }

  @Override
  public long calculate(long attempt) {
    long calc = Math.round(attempt * growBy);
    if (calc < lower) {
      return lower;
    }
    if (calc > upper) {
      return upper;
    }
    return calc;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("LinearDelay{");
    sb.append("growBy ").append(growBy);
    sb.append(" " + unit());
    sb.append("; lower=").append(lower);
    sb.append(", upper=").append(upper);
    sb.append('}');
    return sb.toString();
  }
}
