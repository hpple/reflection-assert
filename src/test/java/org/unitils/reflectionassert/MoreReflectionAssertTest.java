package org.unitils.reflectionassert;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;

class MoreReflectionAssertTest {

  static void assertFailing(Executable executable) {
    assertThrows(AssertionFailedError.class, executable);
  }

  public static class A {

    private final String s;

    public A(String s) {
      this.s = s;
    }

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

  @Nested
  class ReflectionEquals {

    @Test
    void differentObjectsFailing() {
      assertFailing(() ->
          assertReflectionEquals(
              asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
              asList(new B(1, 2, "fff"), new B(2, 4, "gg"))
          )
      );
    }

    @Test
    void equalObjectsNotFailing() {
      assertReflectionEquals(
          asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
          asList(new B(1, 2, "fff"), new B(2, 3, "gg"))
      );
    }

    @Test
    void optional() {
      assertReflectionEquals(Optional.empty(), Optional.empty());
      assertReflectionEquals(Optional.of("foo"), Optional.of("foo"));

      assertFailing(() ->
          assertReflectionEquals(Optional.empty(), Optional.of("foo"))
      );

      assertFailing(() ->
          assertReflectionEquals(Optional.of("bar"), Optional.of("foo"))
      );
    }
  }

  @Nested
  class LenientEquals {

    @Test
    void lenientOrder() {
      assertLenientEquals(
          asList(new B(1, 2, "fff"), new B(2, 3, "gg")),
          asList(new B(2, 3, "gg"), new B(1, 2, "fff"))
      );
    }
  }

}