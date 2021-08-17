/*
 * Copyright 2018,  Stanislav Kashirin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.reflectionassert;

import static org.unitils.reflectionassert.ReflectionComparatorFactory.createReflectionComparator;

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
    ReflectionComparator reflectionComparator = createReflectionComparator(modes);
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
    ReflectionComparator reflectionComparator = createReflectionComparator(modes);
    Difference difference = reflectionComparator.getDifference(unexpected, actual);
    if (difference == null) {
      fail(buildFailureMessage(
          null,
          ignore -> "expected: not equal, but was: <" + new ObjectFormatter().format(actual) + ">"
      ));
    }
  }


  private void fail(String message) {
    throw new AssertionFailedError(message);
  }
}
