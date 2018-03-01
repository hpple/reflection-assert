package com.github.reflectionassert;

import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_DATES;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import org.unitils.reflectionassert.ReflectionComparatorMode;

public final class ModePhase {

  private final Optional<String> message;
  private final ImmutableSet<ReflectionComparatorMode> modes;

  ModePhase() {
    this(Optional.empty(), ImmutableSet.of());
  }

  private ModePhase(Optional<String> message, ImmutableSet<ReflectionComparatorMode> modes) {
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
        ImmutableSet.<ReflectionComparatorMode>builder()
            .addAll(modes)
            .add(mode)
            .build()
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
