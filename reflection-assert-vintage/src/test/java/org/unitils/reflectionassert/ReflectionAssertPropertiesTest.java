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
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ReflectionAssert} tests for with assertProperty methods.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
class ReflectionAssertPropertiesTest {

  /* Test object */
  private TestObject testObject;


  /**
   * Initializes the test fixture.
   */
  @BeforeEach
  void setUp() {
    testObject = new TestObject(1, "stringValue");
  }


  /**
   * Test for equal property value.
   */
  @Test
  void testAssertPropertyReflectionEquals_equals() {
    assertPropertyReflectionEquals("stringProperty", "stringValue", testObject);
  }


  /**
   * Test for equal property value (message version).
   */
  @Test
  void testAssertPropertyReflectionEquals_equalsMessage() {
    assertPropertyReflectionEquals("a message", "stringProperty", "stringValue", testObject);
  }


  /**
   * Test for equal property value.
   */
  @Test
  void testAssertPropertyLenientEquals_equals() {
    assertPropertyLenientEquals("stringProperty", "stringValue", testObject);
  }


  /**
   * Test for equal property value (message version).
   */
  @Test
  void testAssertPropertyLenientEquals_equalsMessage() {
    assertPropertyLenientEquals("a message", "stringProperty", "stringValue", testObject);
  }


  /**
   * Test for equal primitive property value.
   */
  @Test
  void testAssertPropertyReflectionEquals_equalsPrimitive() {
    assertPropertyReflectionEquals("primitiveProperty", 1L, testObject);
  }


  /**
   * Test for different property value.
   */
  @Test
  void testAssertPropertyReflectionEquals_notEqualsDifferentValues() {
    assertFailing(() ->
        assertPropertyReflectionEquals("stringProperty", "xxxxxx", testObject)
    );
  }

  /**
   * Test case for a null left-argument.
   */
  @Test
  void testAssertPropertyReflectionEquals_leftNull() {
    assertFailing(() ->
        assertPropertyReflectionEquals("stringProperty", null, testObject)
    );
  }


  /**
   * Test case for a null right-argument.
   */
  @Test
  void testAssertPropertyReflectionEquals_rightNull() {
    testObject.setStringProperty(null);
    assertFailing(() ->
        assertPropertyReflectionEquals("stringProperty", "stringValue", testObject)
    );
  }


  /**
   * Test case for null as actual object argument.
   */
  @Test
  void testAssertPropertyReflectionEquals_actualObjectNull() {
    assertFailing(() ->
        assertPropertyReflectionEquals("aProperty", "aValue", null)
    );
  }


  /**
   * Test case for both null arguments.
   */
  @Test
  void testAssertPropertyReflectionEquals_null() {
    testObject.setStringProperty(null);
    assertPropertyReflectionEquals("stringProperty", null, testObject);
  }


  /**
   * Test for ignored default left value.
   */
  @Test
  void testAssertPropertyReflectionEquals_equalsIgnoredDefault() {
    assertPropertyReflectionEquals(
        "a message",
        "stringProperty",
        null,
        testObject,
        ReflectionComparatorMode.IGNORE_DEFAULTS
    );
  }


  /**
   * Test for ignored default left value.
   */
  @Test
  void testAssertPropertyLenientEquals_equalsIgnoredDefault() {
    assertPropertyLenientEquals("stringProperty", null, testObject);
  }


  /**
   * Test class with failing equals containing test properties.
   */
  @SuppressWarnings({"InnerClassMayBeStatic", "unused"})
  public class TestObject {

    private long primitiveProperty;

    private String stringProperty;

    public TestObject(long primitiveProperty, String stringProperty) {
      this.primitiveProperty = primitiveProperty;
      this.stringProperty = stringProperty;
    }

    public long getPrimitiveProperty() {
      return primitiveProperty;
    }

    public void setPrimitiveProperty(long primitiveProperty) {
      this.primitiveProperty = primitiveProperty;
    }

    public String getStringProperty() {
      return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
      this.stringProperty = stringProperty;
    }
  }

}
