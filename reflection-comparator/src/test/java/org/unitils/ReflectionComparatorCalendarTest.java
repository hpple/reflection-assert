/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.unitils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createReflectionComparator;

import java.util.Calendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.difference.Difference;


/**
 * Test class for {@link ReflectionComparator}. Contains tests with date types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
class ReflectionComparatorCalendarTest {

  /* Test object */
  private Calendar calendarA;

  /* Same as A but different instance */
  private Calendar calendarB;

  /* Calendar with a different value */
  private Calendar differentCalendar;


  /* Class under test */
  private ReflectionComparator reflectionComparator;


  /**
   * Initializes the test fixture.
   */
  @BeforeEach
  void setUp() {
    calendarA = Calendar.getInstance();
    calendarA.set(2000, 11, 5);
    calendarB = Calendar.getInstance();
    calendarB.setTime(calendarA.getTime());
    differentCalendar = Calendar.getInstance();

    reflectionComparator = createReflectionComparator();
  }


  /**
   * Test for two equal dates.
   */
  @Test
  void testGetDifference_equals() {
    Difference result = reflectionComparator.getDifference(calendarA, calendarB);
    assertNull(result);
  }

  /**
   * Test for two different dates.
   */
  @Test
  void testGetDifference_notEqualsDifferentValues() {
    Difference result = reflectionComparator.getDifference(calendarA, differentCalendar);

    assertEquals(calendarA, result.getLeftValue());
    assertEquals(differentCalendar, result.getRightValue());
  }

}