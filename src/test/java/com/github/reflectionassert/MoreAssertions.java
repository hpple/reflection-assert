package com.github.reflectionassert;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.function.Executable;
import org.opentest4j.AssertionFailedError;

public final class MoreAssertions {

  private MoreAssertions() {
  }

  public static void assertFailing(Executable executable) {
    assertThrows(AssertionFailedError.class, executable);
  }
}