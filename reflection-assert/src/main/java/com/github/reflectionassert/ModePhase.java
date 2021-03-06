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

import static java.util.Collections.emptySet;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_DATES;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import org.unitils.reflectionassert.ReflectionComparatorMode;

public final class ModePhase {

  private final Optional<String> message;
  private final Set<ReflectionComparatorMode> modes;

  ModePhase() {
    this(Optional.empty(), emptySet());
  }

  private ModePhase(Optional<String> message, Set<ReflectionComparatorMode> modes) {
    this.message = message;
    this.modes = modes;
  }

  /**
   * <p>Do not compare the order of collections and arrays.
   *
   * <p>Only check that all values of the expected side collection or array
   * are also contained in the actual side and vice versa.
   *
   * <p>Example: <code>["a", "b', "c"]</code> and <code>["b", "c", "a"]</code>
   * will be considered as equal.
   */
  public ModePhase withLenientOrder() {
    return with(LENIENT_ORDER);
  }

  private ModePhase with(ReflectionComparatorMode mode) {
    return new ModePhase(
        message,
        EnumSet.of(mode, modes.toArray(new ReflectionComparatorMode[]{}))
    );
  }

  /**
   * <p>Ignore fields that have a default value on the expected side.
   *
   * <p>
   * <p><b>WARNING!</b> This mode is supported mostly for compatibility purpose and
   * it's rarely required for modern code bases, especially for ones where immutable classes
   * and optional (or other null-safety mechanisms) are widely used, because it may lead to
   * obscure and error-prone test code when combined with aforementioned techniques.
   *
   * <p><p>Examples:
   * <p> Assertions like
   * <pre>assertReflective().withIgnoreDefaults()
   * .that(new Pair("a", "b"))
   * .isEqualTo(new Pair("a", null))
   * </pre>
   * <pre>assertReflective().withIgnoreDefaults()
   * .that(new IntPair(13, 42))
   * .isEqualTo(new IntPair(0, 42))
   * </pre>
   * will yield no failure.
   * <p>
   * <p> But, assertions like
   * <pre>assertReflective().withIgnoreDefaults()
   * .that(new Pair("a", null))
   * .isEqualTo(new Pair("a", "b"))
   * </pre>
   * <pre>assertReflective().withIgnoreDefaults()
   * .that(new IntPair(13, 42))
   * .isNotEqualTo(new IntPair(0, 0))
   * </pre>
   * will fail.
   */
  public ModePhase withIgnoreDefaults() {
    return with(IGNORE_DEFAULTS);
  }

  /**
   * <p>Do not compare the actual time/date value, just that both
   * expected side and actual side are null or not null.
   *
   * <p>This mode supports both old types (such as {@link java.util.Date},
   * {@link java.util.Calendar}) and modern ones from {@link java.time} package.
   */
  public ModePhase withLenientDates() {
    return with(LENIENT_DATES);
  }

  /**
   * A non-null message that will be used if the assertion fails.
   */
  public ModePhase withMessage(String message) {
    return new ModePhase(
        Optional.of(message),
        modes
    );
  }

  /**
   * Commits current configuration and binds actual parameter for assertion.
   */
  public OperationPhase that(Object actual) {
    return new OperationPhase(actual, message, modes);
  }
}
