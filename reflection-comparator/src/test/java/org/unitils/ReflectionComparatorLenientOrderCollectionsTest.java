/*
 * Copyright 2008,  Unitils.org
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
package org.unitils;

import static java.util.Arrays.binarySearch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createReflectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.difference.UnorderedCollectionDifference;


/**
 * Test class for {@link org.unitils.reflectionassert.ReflectionComparator}. Contains tests for
 * ignore defaults and lenient dates.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
class ReflectionComparatorLenientOrderCollectionsTest {

  /* Class under test */
  private ReflectionComparator reflectionComparator;

  @BeforeEach
  void setUp() {
    reflectionComparator = createReflectionComparator(LENIENT_ORDER);
  }

  /**
   * Test for UNI-156: ReflectionAssert.assertReflectionEquals is leading to an endless loop
   */
  @Test
  void lenientOrderPerformance() {
    String[] expected = {"1", "2", "3", "4", "17", "18", "19", "20", "22", "23", "50"};
    String[] actual = {"1", "3", "4", "2", "17", "18", "19", "20", "21", "22", "23"};

    UnorderedCollectionDifference difference = (UnorderedCollectionDifference) reflectionComparator
        .getDifference(expected, actual);
    assertEquals(1, difference.getBestMatchingIndexes().size());
    assertBestMatch(expected, "50", actual, "21", difference);
  }

  @Test
  void firstBestMatchIsPicked() {
    String[] expected = {"1", "2", "3"};
    String[] actual = {"4", "5", "6"};

    UnorderedCollectionDifference difference = (UnorderedCollectionDifference) reflectionComparator
        .getDifference(expected, actual);
    assertEquals(3, difference.getBestMatchingIndexes().size());
    assertBestMatch(expected, "1", actual, "4", difference);
    assertBestMatch(expected, "2", actual, "4", difference);
    assertBestMatch(expected, "3", actual, "4", difference);
  }

  private void assertBestMatch(
      String[] expected,
      String expectedValue,
      String[] actual,
      String actualValue,
      UnorderedCollectionDifference difference
  ) {
    int expectedIndex = binarySearch(expected, expectedValue);
    int actualIndex = binarySearch(actual, actualValue);
    Integer bestMatchingIndex = difference.getBestMatchingIndexes().get(expectedIndex);
    assertNotNull(
        bestMatchingIndex,
        "Expected (" + expectedValue + "," + actualValue
            + ") as best match, but found no difference"
    );
    assertEquals(
        actualIndex,
        (int) bestMatchingIndex,
        "Expected (" + expectedValue + "," + actualValue + ") as best match, but found ("
            + expected[bestMatchingIndex] + "," + actualValue + ")."
    );
  }

}