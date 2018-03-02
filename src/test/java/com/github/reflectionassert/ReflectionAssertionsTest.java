package com.github.reflectionassert;

import static com.github.reflectionassert.MoreAssertions.assertFailing;
import static com.github.reflectionassert.ReflectionAssertions.assertLenientThat;
import static com.github.reflectionassert.ReflectionAssertions.assertReflective;
import static com.github.reflectionassert.ReflectionAssertions.assertReflectiveThat;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.opentest4j.AssertionFailedError;

class ReflectionAssertionsTest {

  private static Stream<DynamicTest> cases(
      ThrowingConsumer<Sample> assertions,
      Sample... samples
  ) {
    return DynamicTest.stream(
        ImmutableList.copyOf(samples).iterator(),
        Sample::displayName,
        assertions
    );
  }

  private static Sample $(Object expected, Object actual) {
    return new Sample("", expected, actual);
  }

  private static Sample $(String displayName, Object expected, Object actual) {
    return new Sample(displayName, expected, actual);
  }

  @TestFactory
  @DisplayName("Strict Equals")
  Stream<? extends DynamicNode> strictEquals() {
    return Stream.<Generator>of(
        this::nulls,
        this::simple,
        this::equivalence,
        this::overriddenEquals,
        this::collections,
        this::optionals,
        this::differentClasses,
        this::oldDates,
        this::java8Dates
    ).map(generator -> generator.generate(
        sample -> assertReflectiveThat(sample.actual).isEqualTo(sample.expected),
        sample -> assertFailing(
            () -> assertReflectiveThat(sample.actual).isEqualTo(sample.expected)
        )
    ));
  }

  @TestFactory
  @DisplayName("Strict Not Equals")
  Stream<? extends DynamicNode> strictNotEquals() {
    return Stream.<Generator>of(
        this::nulls,
        this::simple,
        this::equivalence,
        this::overriddenEquals,
        this::collections,
        this::optionals,
        this::differentClasses,
        this::oldDates,
        this::java8Dates
    ).map(generator -> generator.generate(
        sample -> assertFailing(
            () -> assertReflectiveThat(sample.actual).isNotEqualTo(sample.expected)
        ),
        sample -> assertReflectiveThat(sample.actual).isNotEqualTo(sample.expected)
    ));
  }

  @TestFactory
  @DisplayName("Lenient Order Equals")
  Stream<? extends DynamicNode> lenientOrderEquals() {
    return Stream.<Generator>of(
        this::nulls,
        this::simple,
        this::equivalence,
        this::overriddenEquals,
        this::collections,
        this::optionals,
        this::differentClasses,
        this::lenientOrderCollections,
        this::oldDates,
        this::java8Dates
    ).map(generator -> generator.generate(
        sample -> assertLenientThat(sample.actual).isEqualTo(sample.expected),
        sample -> assertFailing(
            () -> assertLenientThat(sample.actual).isEqualTo(sample.expected)
        )
    ));
  }

  @TestFactory
  @DisplayName("Lenient Order Not Equals")
  Stream<? extends DynamicNode> lenientOrderNotEquals() {
    return Stream.<Generator>of(
        this::nulls,
        this::simple,
        this::equivalence,
        this::overriddenEquals,
        this::collections,
        this::optionals,
        this::differentClasses,
        this::lenientOrderCollections,
        this::oldDates,
        this::java8Dates
    ).map(generator -> generator.generate(
        sample -> assertFailing(
            () -> assertLenientThat(sample.actual).isNotEqualTo(sample.expected)
        ),
        sample -> assertLenientThat(sample.actual).isNotEqualTo(sample.expected)
    ));
  }

  @TestFactory
  @DisplayName("Lenient Dates Equals")
  Stream<? extends DynamicNode> lenientDatesEquals() {
    return Stream.<Generator>of(
        this::nulls,
        this::simple,
        this::equivalence,
        this::overriddenEquals,
        this::collections,
        this::optionals,
        this::differentClasses,
        this::lenientOldDates,
        this::lenientJava8Dates
    ).map(generator -> generator.generate(
        sample -> assertReflective().withLenientDates()
            .that(sample.actual)
            .isEqualTo(sample.expected),
        sample -> assertFailing(
            () -> assertReflective().withLenientDates()
                .that(sample.actual)
                .isEqualTo(sample.expected)
        )
    ));
  }

  @TestFactory
  @DisplayName("Lenient Dates Not Equals")
  Stream<? extends DynamicNode> lenientDatesNotEquals() {
    return Stream.<Generator>of(
        this::nulls,
        this::simple,
        this::equivalence,
        this::overriddenEquals,
        this::collections,
        this::optionals,
        this::differentClasses,
        this::lenientOldDates,
        this::lenientJava8Dates
    ).map(generator -> generator.generate(
        sample -> assertFailing(
            () -> assertReflective().withLenientDates()
                .that(sample.actual)
                .isNotEqualTo(sample.expected)
        ),
        sample -> assertReflective().withLenientDates()
            .that(sample.actual)
            .isNotEqualTo(sample.expected)
    ));
  }

  @TestFactory
  @DisplayName("Ignore Default Equals")
  Stream<? extends DynamicNode> ignoreDefaultsEquals() {
    return Stream.<Generator>of(
        this::simple,
        this::equivalence,
        this::overriddenEquals,
        this::collections,
        this::differentClasses,
        this::withDefaults
    ).map(generator -> generator.generate(
        sample -> assertReflective().withIgnoreDefaults()
            .that(sample.actual)
            .isEqualTo(sample.expected),
        sample -> assertFailing(
            () -> assertReflective().withIgnoreDefaults()
                .that(sample.actual)
                .isEqualTo(sample.expected)
        )
    ));
  }

  @TestFactory
  @DisplayName("Ignore Default Not Equals")
  Stream<? extends DynamicNode> ignoreDefaultsNotEquals() {
    return Stream.<Generator>of(
        this::simple,
        this::equivalence,
        this::overriddenEquals,
        this::collections,
        this::differentClasses,
        this::withDefaults
    ).map(generator -> generator.generate(
        sample -> assertFailing(
            () -> assertReflective().withIgnoreDefaults()
                .that(sample.actual)
                .isNotEqualTo(sample.expected)
        ),
        sample -> assertReflective().withIgnoreDefaults()
            .that(sample.actual)
            .isNotEqualTo(sample.expected)
    ));
  }

  @Disabled("not impl yet")
  @TestFactory
  Stream<? extends DynamicNode> complexModes() {
    throw new UnsupportedOperationException("not impl yet");
  }


  private DynamicContainer nulls(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Nulls",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("both null", null, null),
                    $("both have null field", new B(4, 2, null), new B(4, 2, null)),
                    $(
                        "both have deep null field",
                        new C(new A(null), new B(4, 2, null)),
                        new C(new A(null), new B(4, 2, null))
                    )
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("expected is null", null, "foo"),
                    $("actual is null", "foo", null),
                    $("expected has null field", new B(4, 2, "foo"), new B(4, 2, null)),
                    $("actual has null field", new B(4, 2, null), new B(4, 2, "foo")),
                    $(
                        "actual has deep null field",
                        new C(new A("foo"), new B(4, 2, null)),
                        new C(new A(null), new B(4, 2, null))
                    ),
                    $(
                        "expected has deep null field",
                        new C(new A(null), new B(4, 2, null)),
                        new C(new A("foo"), new B(4, 2, null))
                    )
                )
            )
        )
    );
  }

  private DynamicContainer simple(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Simple Cases",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("primitives", 1, 1),
                    $("strings", "foo", "foo"),
                    $("trivial object", new A("foo"), new A("foo"))
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("primitives", 1, -1),
                    $("strings", "foo", "bar"),
                    $("trivial object", new A("foo"), new A("bar"))
                )
            )
        )
    );
  }

  private DynamicContainer equivalence(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Equivalence Properties",
        ImmutableList.of(
            dynamicTest(
                "reflexive",
                () -> {
                  C c = new C(new A("ff"), new B(1, 2, "3"));
                  whenEq.accept($(c, c));
                }
            ),
            dynamicTest(
                "symmetric",
                () -> {
                  C foo = new C(new A("ff"), new B(1, 2, "3"));
                  C bar = new C(new A("ff"), new B(1, 2, "3"));
                  whenEq.accept($(foo, bar));
                  whenEq.accept($(bar, foo));
                }
            ),
            dynamicTest(
                "transitive",
                () -> {
                  C foo = new C(new A("ff"), new B(1, 2, "3"));
                  C bar = new C(new A("ff"), new B(1, 2, "3"));
                  C abc = new C(new A("ff"), new B(1, 2, "3"));
                  whenEq.accept($(foo, bar));
                  whenEq.accept($(bar, abc));
                  whenEq.accept($(abc, foo));
                }
            ),
            dynamicTest(
                "consistent",
                () -> {
                  C foo = new C(new A("ff"), new B(1, 2, "3"));
                  C bar = new C(new A("ff"), new B(1, 2, "3"));
                  whenEq.accept($(foo, bar));
                  whenEq.accept($(foo, bar));
                  whenEq.accept($(foo, bar));
                  whenEq.accept($(foo, bar));
                  whenEq.accept($(foo, bar));
                }
            ),
            dynamicTest(
                "nonNull",
                () -> whenNotEq.accept($(new C(new A("ff"), new B(1, 2, "3")), null))
            )
        )
    );
  }

  private DynamicContainer overriddenEquals(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Overridden 'equals()'",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("all fields are equal", new E(1, 11), new E(1, 11))
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("only field used in 'equals()' are equal", new E(1, -42), new E(1, +42))
                )
            )
        )
    );
  }

  private DynamicContainer collections(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Collections",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $(
                        "lists",
                        asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
                        asList(new B(1, 2, "fff"), new B(2, 3, "gg"))
                    ),
                    $(
                        "lists with different implementation",
                        asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
                        ImmutableList.of(new B(1, 2, "fff"), new B(2, 3, "gg"))
                    ),
                    $(
                        "list fields",
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg"))),
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg")))
                    ),
                    $(
                        "list fields different implementation",
                        new Collected<>(
                            'x',
                            ImmutableList.of(new B(1, 2, "fff"), new B(2, 3, "gg"))
                        ),
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg")))
                    )
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $(
                        "lists",
                        asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
                        asList(new B(1, 2, "fff"), new B(2, 4, "gg"))
                    ),
                    $(
                        "lists with different implementation",
                        asList(new B(1, 2, "fff"), new B(2, 4, "gg")),
                        ImmutableList.of(new B(1, 2, "fff"), new B(2, 3, "gg"))
                    ),
                    $(
                        "list fields",
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg"))),
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 4, "gg")))
                    ),
                    $(
                        "list fields different implementation",
                        new Collected<>(
                            'x',
                            ImmutableList.of(new B(1, 2, "fff"), new B(2, 4, "gg"))
                        ),
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg")))
                    )
                )
            )
        )
    );
  }

  private DynamicContainer optionals(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Optionals",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("empty", Optional.empty(), Optional.empty()),
                    $("some", Optional.of("foo"), Optional.of("foo"))
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("empty & some", Optional.empty(), Optional.of("some")),
                    $("some & empty", Optional.of("some"), Optional.empty()),
                    $("different present", Optional.of("some"), Optional.of("other"))
                )
            )
        )
    );
  }

  private DynamicContainer differentClasses(
      @SuppressWarnings("unused") ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    ZonedDateTime now = ZonedDateTime.now();
    Date date = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    return dynamicContainer(
        "Different Classes",
        ImmutableList.of(
            dynamicContainer(
                "when classes are different",
                cases(
                    whenNotEq,
                    $("custom", new B(1, 2, "fff"), new A("fff")),
                    $("date & custom", new Date(), new A("fff"))
                )
            ),
            dynamicContainer(
                "different date types",
                cases(
                    whenNotEq,
                    $("both 'java.time'", now, LocalDateTime.from(now)),
                    $("java.time & vintage", date, Instant.ofEpochMilli(date.getTime())),
                    $("both vintage", date, calendar)
                )
            )
        )
    );
  }

  private DynamicContainer lenientOrderCollections(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Collections With Lenient Order",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $(
                        "lists",
                        asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
                        asList(new B(2, 3, "gg"), new B(1, 2, "fff"))
                    ),
                    $(
                        "lists with different implementation",
                        ImmutableList.of(new B(1, 2, "fff"), new B(2, 3, "gg")),
                        asList(new B(2, 3, "gg"), new B(1, 2, "fff"))
                    ),
                    $(
                        "list fields",
                        new Collected<>('x', asList(new B(2, 3, "gg"), new B(1, 2, "fff"))),
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg")))
                    ),
                    $(
                        "list fields different implementation",
                        new Collected<>('x', asList(new B(2, 3, "gg"), new B(1, 2, "fff"))),
                        new Collected<>(
                            'x',
                            ImmutableList.of(new B(1, 2, "fff"), new B(2, 3, "gg"))
                        )
                    )
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $(
                        "lists",
                        asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
                        asList(new B(2, 3, "abc"), new B(1, 2, "ppp"))
                    ),
                    $(
                        "lists with different implementation",
                        asList(new B(1, 2, "fff"), new B(2, 4, "gg")),
                        ImmutableList.of(new B(2, 3, "abc"), new B(1, 2, "ppp"))
                    ),
                    $(
                        "list fields",
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg"))),
                        new Collected<>('x', asList(new B(2, 3, "abc"), new B(1, 2, "ppp")))
                    ),
                    $(
                        "list fields different implementation",
                        new Collected<>(
                            'x',
                            ImmutableList.of(new B(2, 3, "abc"), new B(1, 2, "ppp"))
                        ),
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg")))
                    )
                )
            ),
            dynamicContainer(
                "possible false positives",
                cases(
                    whenNotEq,
                    $(
                        "lists",
                        asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
                        asList(new B(2, 3, "fff"), new B(1, 2, "fff"))
                    ),
                    $(
                        "list fields",
                        new Collected<>('x', asList(new B(1, 2, "fff"), new B(2, 3, "gg"))),
                        new Collected<>('x', asList(new B(2, 3, "fff"), new B(1, 2, "fff")))
                    )
                )
            )
        )
    );
  }

  private DynamicContainer oldDates(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    Calendar calendar1 = Calendar.getInstance();
    calendar1.setTime(new Date(42));
    Calendar calendar2 = Calendar.getInstance();
    calendar2.setTime(new Date(42));

    return dynamicContainer(
        "Old 'java.util' Dates",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("dates", new Date(1337), new Date(1337)),
                    $("date fields", new D(42, new Date(1337)), new D(42, new Date(1337))),
                    $("calendars", calendar1, calendar2),
                    $("null date fields", new D(42, null), new D(42, null))
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("diff dates", new Date(), new Date(1337)),
                    $("diff date fields", new D(42, new Date()), new D(42, new Date(1337))),
                    $("diff calendars", calendar1, Calendar.getInstance()),
                    $("null & date", null, new Date(1337)),
                    $("date & null", new Date(), null),
                    $("null & calendar", null, Calendar.getInstance()),
                    $("calendar & null", Calendar.getInstance(), null),
                    $("null & date field", new D(42, null), new D(42, new Date(1337))),
                    $("date & null field", new D(42, new Date()), new D(42, null))
                )
            )
        )
    );
  }

  private DynamicContainer lenientOldDates(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Lenient Old 'java.util' Dates",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("diff dates", new Date(), new Date(1337)),
                    $("diff date fields", new D(42, new Date()), new D(42, new Date(1337))),
                    $("diff calendars", Calendar.getInstance(), Calendar.getInstance()),
                    $("null date fields", new D(42, null), new D(42, null))
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("null & date", null, new Date(1337)),
                    $("date & null", new Date(), null),
                    $("null & calendar", null, Calendar.getInstance()),
                    $("calendar & null", Calendar.getInstance(), null),
                    $("null & date field", new D(42, null), new D(42, new Date(1337))),
                    $("date & null field", new D(42, new Date()), new D(42, null))
                )
            ),
            dynamicContainer(
                "possible false positives",
                cases(
                    whenNotEq,
                    $("other field is diff", new D(42, new Date()), new D(1, new Date(1337))),
                    $(
                        "other field is diff, strictly equal dates",
                        new D(42, new Date(1337)),
                        new D(1, new Date(1337))
                    )
                )
            )
        )
    );
  }

  private DynamicContainer java8Dates(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Java 8 Dates",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("instant", Instant.ofEpochMilli(111), Instant.ofEpochMilli(111)),
                    $(
                        "datetime",
                        LocalDateTime.of(1917, Month.MARCH, 8, 0, 0),
                        LocalDateTime.of(1917, Month.MARCH, 8, 0, 0)
                    ),
                    $(
                        "instant fields",
                        new J8Time(42, Instant.ofEpochMilli(12312)),
                        new J8Time(42, Instant.ofEpochMilli(12312))
                    ),
                    $(
                        "null datetime fields",
                        new J8Time(42, LocalDateTime.of(1917, Month.MARCH, 8, 0, 0)),
                        new J8Time(42, LocalDateTime.of(1917, Month.MARCH, 8, 0, 0))
                    ),
                    $("null temporal fields", new J8Time(42, null), new J8Time(42, null))
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("diff instant", Instant.now(), Instant.ofEpochMilli(12312)),
                    $(
                        "diff datetime",
                        LocalDateTime.now(),
                        LocalDateTime.of(1917, Month.MARCH, 8, 0, 0)
                    ),
                    $(
                        "diff instant fields",
                        new J8Time(42, Instant.now()),
                        new J8Time(42, Instant.ofEpochMilli(12312))
                    ),
                    $(
                        "null datetime fields",
                        new J8Time(42, LocalDateTime.now()),
                        new J8Time(42, LocalDateTime.of(1917, Month.MARCH, 8, 0, 0))
                    ),
                    $("null & instant", null, Instant.now()),
                    $("instant & null", Instant.now(), null),
                    $("null & datetime", null, LocalDateTime.now()),
                    $("datetime & null", LocalDateTime.now(), null),
                    $("null & instant field", new J8Time(42, null), new J8Time(42, Instant.now())),
                    $("instant & null field", new J8Time(42, Instant.now()), new J8Time(42, null)),
                    $(
                        "null & datetime field",
                        new J8Time(42, null),
                        new J8Time(42, LocalDateTime.now())
                    ),
                    $(
                        "datetime & null field",
                        new J8Time(42, LocalDateTime.now()),
                        new J8Time(42, null)
                    )
                )
            )
        )
    );
  }

  private DynamicContainer lenientJava8Dates(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "Lenient Java 8 Dates",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("diff instant", Instant.now(), Instant.ofEpochMilli(12312)),
                    $(
                        "diff datetime",
                        LocalDateTime.now(),
                        LocalDateTime.of(1917, Month.MARCH, 8, 0, 0)
                    ),
                    $(
                        "diff instant fields",
                        new J8Time(42, Instant.now()),
                        new J8Time(42, Instant.ofEpochMilli(12312))
                    ),
                    $(
                        "diff datetime fields",
                        new J8Time(42, LocalDateTime.now()),
                        new J8Time(42, LocalDateTime.of(1917, Month.MARCH, 8, 0, 0))
                    ),
                    $("null temporal fields", new J8Time(42, null), new J8Time(42, null))
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("null & instant", null, Instant.now()),
                    $("instant & null", Instant.now(), null),
                    $("null & datetime", null, LocalDateTime.now()),
                    $("datetime & null", LocalDateTime.now(), null),
                    $("null & instant field", new J8Time(42, null), new J8Time(42, Instant.now())),
                    $("instant & null field", new J8Time(42, Instant.now()), new J8Time(42, null)),
                    $(
                        "null & datetime field",
                        new J8Time(42, null),
                        new J8Time(42, LocalDateTime.now())
                    ),
                    $(
                        "datetime & null field",
                        new J8Time(42, LocalDateTime.now()),
                        new J8Time(42, null)
                    )
                )
            ),
            dynamicContainer(
                "possible false positives",
                cases(
                    whenNotEq,
                    $(
                        "other field is diff, lenient instant",
                        new J8Time(42, Instant.now()),
                        new J8Time(1, Instant.ofEpochMilli(12312))
                    ),
                    $(
                        "other field is diff, strictly equal instant",
                        new J8Time(42, Instant.ofEpochMilli(1337)),
                        new J8Time(1, Instant.ofEpochMilli(1337))
                    ),
                    $(
                        "other field is diff, lenient datetime",
                        new J8Time(42, LocalDateTime.now()),
                        new J8Time(1, LocalDateTime.of(1917, Month.MARCH, 8, 0, 0))
                    ),
                    $(
                        "other field is diff, strictly equal datetime",
                        new J8Time(1, LocalDateTime.of(1917, Month.MARCH, 8, 0, 0)),
                        new J8Time(42, LocalDateTime.of(1917, Month.MARCH, 8, 0, 0))
                    )
                )
            )
        )
    );
  }

  private DynamicContainer withDefaults(
      ThrowingConsumer<Sample> whenEq,
      ThrowingConsumer<Sample> whenNotEq
  ) {
    return dynamicContainer(
        "With Defaults",
        ImmutableList.of(
            dynamicContainer(
                "when equal",
                cases(
                    whenEq,
                    $("both null", null, null),
                    $("both have null field", new Pair("a", null), new Pair("a", null)),
                    $(
                        "both have deep null field",
                        new C(new A(null), new B(4, 2, null)),
                        new C(new A(null), new B(4, 2, null))
                    ),
                    $("expected is null", null, "foo"),
                    $("expected is null, actual is date", null, new Date()),
                    $("expected field is null", new Pair("a", null), new Pair("a", "b")),
                    $("all expected fields are null", new Pair(null, null), new Pair("a", null)),
                    $("all fields are null", new Pair(null, null), new Pair(null, null)),
                    $("expected field with default zero", new IntPair(0, 42), new IntPair(13, 42)),
                    $(
                        "all expected fields with default zero",
                        new IntPair(0, 0),
                        new IntPair(13, 42)
                    ),
                    $("all fields with default zero", new IntPair(0, 0), new IntPair(0, 0))
                )
            ),
            dynamicContainer(
                "when not equal",
                cases(
                    whenNotEq,
                    $("actual is null", new A("foobar"), null),
                    $("actual is null, expected is date", new Date(), null),
                    $("actual field is null", new Pair("a", "b"), new Pair("a", null)),
                    $("actual field with default zero", new IntPair(13, 42), new IntPair(0, 42)),
                    $("all actual fields with default zero", new IntPair(13, 42), new IntPair(0, 0)),
                    $(
                        "actual has deep null field",
                        new C(new A("foo"), new B(4, 2, null)),
                        new C(new A(null), new B(4, 2, null))
                    )
                )
            ),
            dynamicContainer(
                "possible false positives",
                cases(
                    whenNotEq,
                    $(
                        "field is diff, expected field is null",
                        new Pair("a", null),
                        new Pair("c", "b")
                    ),
                    $(
                        "field is diff, other are nulls",
                        new Pair("a", null),
                        new Pair("c", null)
                    ),
                    $(
                        "field is diff, expected field with zero default",
                        new IntPair(0, -1),
                        new IntPair(10, 42)
                    ),
                    $(
                        "field is diff, other are both zero",
                        new IntPair(0, -1),
                        new IntPair(0, 42)
                    )
                )
            )

        )
    );
  }

  @FunctionalInterface
  private interface Generator {

    DynamicContainer generate(
        ThrowingConsumer<Sample> whenEq,
        ThrowingConsumer<Sample> whenNotEq
    );
  }

  private static class Sample {

    final String displayName;
    final Object expected;
    final Object actual;

    Sample(String displayName, Object expected, Object actual) {
      this.displayName = displayName;
      this.expected = expected;
      this.actual = actual;
    }

    String displayName() {
      return displayName;
    }
  }

  public static class A {

    private final String s;

    public A(String s) {
      this.s = s;
    }

    @SuppressWarnings("unused")
    public String getS() {
      return "foobar";
    }
  }

  public static class B {

    int a;
    int b;
    String c;

    public B(int a, int b, String c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }
  }

  public static class C {

    A a;
    B b;

    public C(A a, B b) {
      this.a = a;
      this.b = b;
    }
  }

  public static class D {

    int a;
    Date d;

    public D(int a, Date d) {
      this.a = a;
      this.d = d;
    }
  }

  public static class E {

    final int foo;
    int bar;

    public E(int foo, int bar) {
      this.foo = foo;
      this.bar = bar;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      E e = (E) o;
      return foo == e.foo;
    }

    @Override
    public int hashCode() {
      return Objects.hash(foo);
    }
  }

  public static class Collected<T> {

    char foo;
    List<T> values;

    public Collected(char foo, List<T> values) {
      this.foo = foo;
      this.values = values;
    }
  }

  public static class J8Time {

    int a;
    TemporalAccessor j8;

    public J8Time(int a, TemporalAccessor j8) {
      this.a = a;
      this.j8 = j8;
    }
  }

  public static class Pair {

    String a;
    String b;

    public Pair(String a, String b) {
      this.a = a;
      this.b = b;
    }
  }

  public static class IntPair {

    int a;
    int b;

    public IntPair(int a, int b) {
      this.a = a;
      this.b = b;
    }
  }

  @Nested
  class ErrorReport {

    @Test
    void assertionErrorOfIsEqualTo() {
      AssertionFailedError err = assertThrows(AssertionFailedError.class, () ->
          assertReflective().withMessage("it's obvious").that(true).isEqualTo(false)
      );

      assertTrue(err.getMessage().startsWith("it's obvious"));
      assertFalse(err.isActualDefined());
      assertFalse(err.isExpectedDefined());
    }

    @Test
    void assertionErrorOfIsNotEqualTo() {
      AssertionFailedError err = assertThrows(AssertionFailedError.class, () ->
          assertReflective().withMessage("it's obvious").that(false).isNotEqualTo(false)
      );

      assertTrue(err.getMessage().startsWith("it's obvious"));
      assertFalse(err.isActualDefined());
      assertFalse(err.isExpectedDefined());
    }
  }
}