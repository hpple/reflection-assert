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
package org.unitils.reflectionassert.comparator.impl;

import static java.util.Arrays.asList;

import java.util.Collection;

@SuppressWarnings("MethodCanBeVariableArityMethod")
final class CollectionConverter {

  private static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
  private static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
  private static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
  private static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
  private static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
  private static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
  private static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
  private static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

  private CollectionConverter() {
  }

  /**
   * Converts the given array or collection object (possibly primitive array) to type Collection
   *
   * @param object The array or collection
   * @return The object collection
   */
  static Collection<?> convertToCollection(Object object) {
    if (object instanceof Collection<?>) {
      return (Collection<?>) object;
    }

    // If needed convert primitive array to object array
    Object[] objectArray = convertToObjectArray(object);

    // Convert array to collection
    return asList(objectArray);
  }

  /**
   * Converts the given array object (possibly primitive array) to type Object[]
   *
   * @param object The array
   * @return The object array
   */
  private static Object[] convertToObjectArray(Object object) {
    if (object instanceof byte[]) {
      return toObject((byte[]) object);
    }

    if (object instanceof short[]) {
      return toObject((short[]) object);
    }

    if (object instanceof int[]) {
      return toObject((int[]) object);
    }

    if (object instanceof long[]) {
      return toObject((long[]) object);
    }

    if (object instanceof char[]) {
      return toObject((char[]) object);
    }

    if (object instanceof float[]) {
      return toObject((float[]) object);
    }

    if (object instanceof double[]) {
      return toObject((double[]) object);
    }

    if (object instanceof boolean[]) {
      return toObject((boolean[]) object);
    }

    return (Object[]) object;
  }

  private static Boolean[] toObject(boolean[] array) {
    if (array == null) {
      return null;
    }
    if (array.length == 0) {
      return EMPTY_BOOLEAN_OBJECT_ARRAY;
    }
    final Boolean[] result = new Boolean[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  private static Byte[] toObject(byte[] array) {
    if (array == null) {
      return null;
    }
    if (array.length == 0) {
      return EMPTY_BYTE_OBJECT_ARRAY;
    }
    final Byte[] result = new Byte[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  private static Character[] toObject(char[] array) {
    if (array == null) {
      return null;
    }
    if (array.length == 0) {
      return EMPTY_CHARACTER_OBJECT_ARRAY;
    }
    final Character[] result = new Character[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  private static Double[] toObject(double[] array) {
    if (array == null) {
      return null;
    }
    if (array.length == 0) {
      return EMPTY_DOUBLE_OBJECT_ARRAY;
    }
    final Double[] result = new Double[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  private static Float[] toObject(float[] array) {
    if (array == null) {
      return null;
    }
    if (array.length == 0) {
      return EMPTY_FLOAT_OBJECT_ARRAY;
    }
    final Float[] result = new Float[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  private static Integer[] toObject(int[] array) {
    if (array == null) {
      return null;
    }
    if (array.length == 0) {
      return EMPTY_INTEGER_OBJECT_ARRAY;
    }
    final Integer[] result = new Integer[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  private static Long[] toObject(long[] array) {
    if (array == null) {
      return null;
    }
    if (array.length == 0) {
      return EMPTY_LONG_OBJECT_ARRAY;
    }
    final Long[] result = new Long[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }

  private static Short[] toObject(short[] array) {
    if (array == null) {
      return null;
    }
    if (array.length == 0) {
      return EMPTY_SHORT_OBJECT_ARRAY;
    }
    final Short[] result = new Short[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = array[i];
    }
    return result;
  }
}
