package com.github.reflectionassert;

import static org.junit.jupiter.api.Assertions.fail;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;

import java.util.Optional;
import java.util.Set;
import org.opentest4j.AssertionFailedError;
import org.unitils.core.util.ObjectFormatter;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.ReflectionComparatorMode;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.report.DifferenceReport;
import org.unitils.reflectionassert.report.impl.DefaultDifferenceReport;

public final class OperationPhase {

  private final Object actual;
  private final Optional<String> message;
  private final Set<ReflectionComparatorMode> modes;

  OperationPhase(
      Object actual,
      Optional<String> message,
      Set<ReflectionComparatorMode> modes
  ) {
    this.actual = actual;
    this.message = message;
    this.modes = modes;
  }

  /**
   * Asserts that two objects are equal. Reflection is used to compare all fields of given values.
   * If they are not equal an {@link AssertionFailedError} is thrown.
   *
   * @throws AssertionFailedError when given objects are not equal
   */
  public void isEqualTo(Object expected) {
    ReflectionComparator reflectionComparator = createRefectionComparator(modes);
    Difference difference = reflectionComparator.getDifference(expected, actual);
    if (difference != null) {
      fail(buildFailureMessage(difference, new DefaultDifferenceReport()));
    }
  }

  private String buildFailureMessage(
      Difference difference,
      DifferenceReport report
  ) {
    //noinspection StringBufferReplaceableByString
    return new StringBuilder()
        .append(message.orElse(""))
        .append("\n")
        .append(report.createReport(difference))
        .toString();
  }

  /**
   * Asserts that two objects are not equal.
   * Reflection is used to compare all fields of given values.
   * If they are equal an {@link AssertionFailedError} is thrown.
   *
   * @throws AssertionFailedError when given objects are equal
   */
  public void isNotEqualTo(Object unexpected) {
    ReflectionComparator reflectionComparator = createRefectionComparator(modes);
    Difference difference = reflectionComparator.getDifference(unexpected, actual);
    if (difference == null) {
      fail(buildFailureMessage(
          null,
          ignore -> "expected: not equal, but was: <" + new ObjectFormatter().format(actual) + ">"
      ));
    }
  }

}
