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

import org.unitils.reflectionassert.ReflectionComparatorMode;

/**
 * <p> This is the entry point of reflection-based assertions.
 *
 * <p> Two objects/collections can be tested by comparing fields (<b>not properties!</b>)
 * of these objects/collections using reflection.
 * Some of classes (eg collections) may have special treatment.
 *
 * <p> The combination of comparator modes specifies how strict the comparison must be:
 * <ul>
 * <li>ignore defaults: compare only arguments (and inner values) that have a non default value
 * (eg null for reference type or 0 for primitives) as expected value</li>
 * <li>lenient dates: do not compare actual date values, just that they both
 * have a value or not, this mode supports both old types (such as {@link java.util.Date},
 * {@link java.util.Calendar}) and modern ones from {@link java.time} package
 * </li>
 * <li>lenient order: order is not important when comparing collections or arrays</li>
 * </ul>
 *
 * <p>Using no modes means strict comparision.
 *
 * <p>
 * <p>It's strongly recommended to import these assertion methods statically in order to achieve
 * better test readability.
 *
 * <p>Please, note that although compatibility on method signature level is guaranteed,
 * intermediate phases are not supposed to be used directly in the code
 * and their class names may be a subject of future changes.
 *
 * <p>Just use it in a fluent interface way, like:
 * <pre>assertReflective().that(actual).isEqualTo(expected)</pre>
 * <pre>assertReflective().withMessage("message").that(actual).isNotEqualTo(unexpected)</pre>
 * <pre>assertReflective().withLenientOrder().that(xs).isEqualTo(ys)</pre>
 *
 * <p>
 * <p>Also, there are some convenient shortcuts for simple or frequent use cases, e.g.:
 * <pre>assertReflectiveThat(actual).isEqualTo(expected)</pre>
 * <pre>assertLenientThat(actual).isEqualTo(expected)</pre>
 *
 * <p> <b>WARNING! Unlike original Unitils, our lenient equals shortcut does not add
 * {@link ReflectionComparatorMode#IGNORE_DEFAULTS} mode.</b>
 */
public final class ReflectionAssertions {

  /**
   * This is the entry point of reflective assertion.
   */
  public static ModePhase assertReflective() {
    return new ModePhase();
  }

  /**
   * Just a shortcut for <pre>assertReflective().that(foo).isEqualTo(bar)</pre>
   */
  public static OperationPhase assertReflectiveThat(Object actual) {
    return new ModePhase().that(actual);
  }

  /**
   * <p>Basically, this is just a shortcut for
   * <pre>assertReflective().withLenientOrder().that(foo).isEqualTo(bar)</pre>
   *
   * <p><b>Pay attention to the fact that unlike original Unitils' lenient equals shortcut,
   * this one does not add {@link ReflectionComparatorMode#IGNORE_DEFAULTS} mode.</b>
   */
  public static OperationPhase assertLenientThat(Object actual) {
    return new ModePhase().withLenientOrder().that(actual);
  }
}
