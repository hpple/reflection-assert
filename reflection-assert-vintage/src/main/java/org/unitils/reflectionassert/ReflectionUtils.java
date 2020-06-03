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
package org.unitils.reflectionassert;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.unitils.core.UnitilsException;

/**
 * Utility methods that use reflection for instance creation or class inspection.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
class ReflectionUtils {

  private ReflectionUtils() {
  }

  /**
   * Returns the value of the given field (may be private) in the given object
   *
   * @param object The object containing the field, null for static fields
   * @param field The field, not null
   * @return The value of the given field in the given object
   * @throws UnitilsException if the field could not be accessed
   */
  @SuppressWarnings("unchecked")
  static <T> T getFieldValue(Object object, Field field) {
    try {
      field.setAccessible(true);
      return (T) field.get(object);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new UnitilsException("Error while trying to access field " + field, e);
    }
  }

  /**
   * From the given class, returns the getter for the given property name. If isStatic == true, a
   * static getter is searched. If no such getter exists in the given class, null is returned.
   * <p>
   * When the given field is a boolean the getGetter will also try the isXxxxx.
   *
   * @param clazz The class to get the setter from, not null
   * @param propertyName The name of the property, not null
   * @param isStatic True if a static getter is to be returned, false for non-static
   * @return The getter method that matches the given parameters, or null if no such method exists
   */
  static Method getGetter(Class<?> clazz, String propertyName, boolean isStatic) {
    String getterName = "get" + capitalize(propertyName);
    Method result = getMethod(clazz, getterName, isStatic);

    try {
      if (result == null && (Boolean.TYPE.equals(clazz.getDeclaredField(propertyName).getType())
          || Boolean.class.equals(clazz.getDeclaredField(propertyName).getType()))) {
        String isName = "is" + capitalize(propertyName);
        result = getMethod(clazz, isName, isStatic);
      }
    } catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Gets the method with the given name from the given class or one of its super-classes.
   *
   * @param clazz The class containing the method
   * @param methodName The name of the method, not null
   * @param isStatic True for a static method, false for non-static
   * @param parameterTypes The parameter types
   * @return The method, null if no matching method was found
   */
  private static Method getMethod(
      Class<?> clazz,
      String methodName,
      boolean isStatic,
      Class<?>... parameterTypes
  ) {
    if (clazz == null || clazz.equals(Object.class)) {
      return null;
    }

    Method result;
    try {
      result = clazz.getDeclaredMethod(methodName, parameterTypes);
    } catch (NoSuchMethodException e) {
      result = null;
    }
    if (result != null && isStatic(result.getModifiers()) == isStatic) {
      return result;
    }
    return getMethod(clazz.getSuperclass(), methodName, isStatic, parameterTypes);
  }

  /**
   * Gets all fields of the given class and all its super-classes.
   *
   * @param clazz The class
   * @return The fields, not null
   */
  static Set<Field> getAllFields(Class<?> clazz) {
    Set<Field> result = new HashSet<Field>();
    if (clazz == null || clazz.equals(Object.class)) {
      return result;
    }

    // add all fields of this class
    Field[] declaredFields = clazz.getDeclaredFields();
    result.addAll(asList(declaredFields));
    // add all fields of the super-classes
    result.addAll(getAllFields(clazz.getSuperclass()));
    return result;
  }

  private static String capitalize(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return Character.toTitleCase(str.charAt(0)) + str.substring(1);
  }
}