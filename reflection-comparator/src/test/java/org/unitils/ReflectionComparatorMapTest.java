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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import static org.unitils.reflectionassert.util.InnerDifferenceFinder.getInnerDifference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.difference.MapDifference;


/**
 * Test class for {@link ReflectionComparator}. Contains tests with map types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
class ReflectionComparatorMapTest {

  /* Test map */
  private Map<String, Element> mapA;

  /* Same as A but different instance */
  private Map<String, Element> mapB;

  /* Same as A and B but different string value for element 2 */
  private Map<String, Element> mapDifferentValue;

  /* Same as A and B but different key value for element 2 */
  private Map<String, Element> mapDifferentKey;

  /* Test map with inner map for element 2 */
  private Map<String, Element> mapInnerA;

  /* Same as innerA but different instance  */
  private Map<String, Element> mapInnerB;

  /* Same as innerA and innerB but different string value for inner element 2 */
  private Map<String, Element> mapInnerDifferentValue;

  /* Test map having a key type that returns false for equals() */
  private Map<Element, Element> mapReflectionCompareKeyA;

  /* Same as A but different instance */
  private Map<Element, Element> mapReflectionCompareKeyB;

  /* Same as A and B but with a different key value */
  private Map<Element, Element> mapReflectionCompareDifferentKey;

  /* Class under test */
  private ReflectionComparator reflectionComparator;


  /**
   * Initializes the test fixture.
   */
  @BeforeEach
  void setUp() {
    mapA = createMap("key 2", "test 2", null);
    mapB = createMap("key 2", "test 2", null);
    mapDifferentValue = createMap("key 2", "XXXXXX", null);
    mapDifferentKey = createMap("XXXXX", "test 2", null);

    mapInnerA = createMap("key 2", null, mapA);
    mapInnerB = createMap("key 2", null, mapB);
    mapInnerDifferentValue = createMap("key 2", null, mapDifferentValue);

    mapReflectionCompareKeyA = createNotEqualsKeyMap("key 2");
    mapReflectionCompareKeyB = createNotEqualsKeyMap("key 2");
    mapReflectionCompareDifferentKey = createNotEqualsKeyMap("XXXXXX");

    reflectionComparator = createRefectionComparator();
  }


  /**
   * Test for two equal maps.
   */
  @Test
  void testGetDifference_equals() {
    Difference result = reflectionComparator.getDifference(mapA, mapB);
    assertNull(result);
  }


  /**
   * Test for two equal maps as an inner field of an object.
   */
  @Test
  void testGetDifference_equalsInner() {
    Difference result = reflectionComparator.getDifference(mapInnerA, mapInnerB);
    assertNull(result);
  }


  /**
   * Test for two maps that contain different values.
   */
  @Test
  void testGetDifference_notEqualsDifferentValues() {
    Difference result = reflectionComparator.getDifference(mapA, mapDifferentValue);

    Difference difference = getInnerDifference("string", getInnerDifference("\"key 2\"", result));
    assertEquals("test 2", difference.getLeftValue());
    assertEquals("XXXXXX", difference.getRightValue());
  }


  /**
   * Test for two maps that have a different size. The first element was removed from the left map
   */
  @Test
  void testGetDifference_notEqualsLeftElementRemoved() {
    mapA.remove("key 1");
    Difference result = reflectionComparator.getDifference(mapA, mapB);

    assertEquals("key 1", ((MapDifference) result).getRightMissingKeys().get(0));
  }


  /**
   * Test for two maps that have a different size. The first element was removed from the right map
   */
  @Test
  void testGetDifference_notEqualsRightElementRemoved() {
    mapB.remove("key 1");
    Difference result = reflectionComparator.getDifference(mapA, mapB);

    assertEquals("key 1", ((MapDifference) result).getLeftMissingKeys().get(0));
  }


  /**
   * Test for objects with inner maps that contain different values.
   */
  @Test
  void testGetDifference_notEqualsInnerDifferentValues() {
    Difference result = reflectionComparator.getDifference(mapInnerA, mapInnerDifferentValue);

    Difference difference = getInnerDifference("inner", getInnerDifference("\"key 2\"", result));
    Difference innerDifference = getInnerDifference(
        "string",
        getInnerDifference("\"key 2\"", difference)
    );
    assertEquals("test 2", innerDifference.getLeftValue());
    assertEquals("XXXXXX", innerDifference.getRightValue());
  }


  /**
   * Test for maps that contain different keys.
   */
  @Test
  void testGetDifference_notEqualsDifferentKeys() {
    Difference result = reflectionComparator.getDifference(mapA, mapDifferentKey);

    assertSame(mapA, result.getLeftValue());
    assertSame(mapDifferentKey, result.getRightValue());
  }


  /**
   * Tests for objects with inner maps that have a different size.
   */
  @Test
  void testGetDifference_notEqualsInnerDifferentSize() {
    Iterator<?> iterator = mapB.entrySet().iterator();
    iterator.next();
    iterator.remove();

    Difference result = reflectionComparator.getDifference(mapInnerA, mapInnerB);

    Difference difference = getInnerDifference("inner", getInnerDifference("\"key 2\"", result));
    assertSame(mapA, difference.getLeftValue());
    assertSame(mapB, difference.getRightValue());
  }


  /**
   * Tests for maps but right value is not a map.
   */
  @Test
  void testGetDifference_notEqualsRightNotMap() {
    Difference result = reflectionComparator.getDifference(mapA, "Test string");

    assertSame(mapA, result.getLeftValue());
    assertEquals("Test string", result.getRightValue());
  }


  /**
   * Tests for equal maps for which the keys are not equals() but are equal using reflection. The
   * reflection comparator uses strict reflection compare on keys.
   */
  @Test
  void testGetDifference_equalsMapComparingKeysUsingReflection() {
    Difference result = reflectionComparator
        .getDifference(mapReflectionCompareKeyA, mapReflectionCompareKeyB);
    assertNull(result);
  }


  /**
   * Tests for using reflection on key values with a different value for one of the keys. The
   * reflection comparator uses strict reflection compare on keys.
   */
  @Test
  void testGetDifference_notEqualsMapComparingKeysUsingReflection() {
    Difference result = reflectionComparator
        .getDifference(mapReflectionCompareKeyA, mapReflectionCompareDifferentKey);

    assertSame(mapReflectionCompareKeyA, result.getLeftValue());
    assertSame(mapReflectionCompareDifferentKey, result.getRightValue());
  }


  /**
   * Creates a map.
   *
   * @param keyElement2 the key for the 2nd element in the map
   * @param stringValueElement2 the value for the 2nd element in the map
   * @param innerElement2 the value for the inner array of the 2nd element in the map
   * @return the test map
   */
  private Map<String, Element> createMap(
      String keyElement2,
      String stringValueElement2,
      Map<?, ?> innerElement2
  ) {
    Map<String, Element> map = new HashMap<>();
    map.put("key 1", new Element("test 1", null));
    map.put(keyElement2, new Element(stringValueElement2, innerElement2));
    map.put("key 3", new Element("test 3", null));
    return map;
  }


  /**
   * Creates a map.
   *
   * @param keyElement2 the key for the 2nd element in the map
   * @return the test map
   */
  private Map<Element, Element> createNotEqualsKeyMap(String keyElement2) {
    Map<Element, Element> map = new HashMap<>();
    map.put(new Element("key 1", null), new Element("test 1", null));
    map.put(new Element(keyElement2, null), new Element("test 2", null));
    return map;
  }


  /**
   * Test class with failing equals.
   */
  private class Element {

    /* A string value */
    private String string;

    /* An inner map */
    private Map<?, ?> inner;


    /**
     * Creates and initializes the element.
     *
     * @param string the string value
     * @param inner the inner map
     */
    Element(String string, Map<?, ?> inner) {
      this.string = string;
      this.inner = inner;
    }

    /**
     * Gets the string value
     *
     * @return the value
     */
    public String getString() {
      return string;
    }

    /**
     * Gets the inner map
     *
     * @return the map
     */
    public Map<?, ?> getInner() {
      return inner;
    }

    /**
     * Always returns false
     *
     * @param o the object to compare to
     */
    @Override
    public boolean equals(Object o) {
      return false;
    }
  }


}