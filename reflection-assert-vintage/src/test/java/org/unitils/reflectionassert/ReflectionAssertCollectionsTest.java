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
package org.unitils.reflectionassert;

import static org.unitils.reflectionassert.MoreAssertions.assertFailing;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ReflectionAssert} tests for with assertProperty methods with collection
 * arguments.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
class ReflectionAssertCollectionsTest {

  /* A test collection */
  private List<String> listA;

  /* Same as listA but different instance */
  private List<String> listB;

  /* Same as listA but with a different order */
  private List<String> listDifferentOrder;

  /* A list having same size as listA but containing different values */
  private List<String> listDifferentValues;

  /* A list containing 1 extra element as listA, a double of another element */
  private List<String> listDuplicateElement;

  /* A list with one element less than listA */
  private List<String> listOneElementLess;

  /* A list with one element more than listA */
  private List<String> listOneElementMore;


  /**
   * Initializes the test fixture.
   */
  @BeforeEach
  void setUp() {
    listA = Arrays.asList("el1", "el2");
    listB = Arrays.asList("el1", "el2");
    listDifferentOrder = Arrays.asList("el2", "el1");
    listDifferentValues = Arrays.asList("el2", "el3");
    listDuplicateElement = Arrays.asList("el2", "el2", "el1");
    listOneElementLess = Arrays.asList("el1");
    listOneElementMore = Arrays.asList("el1", "el2", "el3");
  }


  /**
   * Test for two equal collections.
   */
  @Test
  void testAssertReflectionEquals() {
    assertReflectionEquals(listA, listB);
  }


  /**
   * Test for two equal collections but with different order.
   */
  @Test
  void testAssertReflectionEquals_notEqualsDifferentOrder() {
    assertFailing(() ->
        assertReflectionEquals(listA, listDifferentOrder)
    );
  }


  /**
   * Test for two equal collections but with different order.
   */
  @Test
  void testAssertReflectionEquals_equalsDifferentOrder() {
    assertReflectionEquals(listA, listDifferentOrder, ReflectionComparatorMode.LENIENT_ORDER);
  }


  /**
   * Test for two equal collections but with different order.
   */
  @Test
  void testAssertLenientEquals_equalsDifferentOrder() {
    assertLenientEquals(listA, listDifferentOrder);
  }


  /**
   * Test for two collections with different elements.
   */
  @Test
  void testAssertEquals_differentListSameSize() {
    assertFailing(() ->
        assertReflectionEquals(listA, listDifferentValues)
    );
  }


  /**
   * Test for a collection with a duplicate element.
   */
  @Test
  void testAssertEquals_duplicateElement() {
    assertFailing(() ->
        assertReflectionEquals(listA, listDuplicateElement)
    );
  }


  /**
   * Test for with a collection that has one element less.
   */
  @Test
  void testAssertEquals_oneElementLess() {
    assertFailing(() ->
        assertReflectionEquals(listA, listOneElementLess)
    );
  }


  /**
   * Test for with a collection that has one element more.
   */
  @Test
  void testAssertEquals_oneElementMore() {
    assertFailing(() ->
        assertReflectionEquals(listA, listOneElementMore)
    );
  }

}
