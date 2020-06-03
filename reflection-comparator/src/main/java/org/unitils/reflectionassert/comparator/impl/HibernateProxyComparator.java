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

import static org.unitils.reflectionassert.util.HibernateUtil.getEntitiyName;
import static org.unitils.reflectionassert.util.HibernateUtil.getIdentifier;
import static org.unitils.reflectionassert.util.HibernateUtil.getUnproxiedValue;
import static org.unitils.reflectionassert.util.HibernateUtil.isHibernateProxy;
import static org.unitils.reflectionassert.util.HibernateUtil.isUninitialized;

import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.difference.ObjectDifference;

/**
 * Comparator that can handle Hibernate proxies.
 * <p/>
 * Special thanks to Tim Peeters for helping us with the implementation.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Tim Peeters
 */
public class HibernateProxyComparator implements Comparator {

  /**
   * Returns true if one or both of the objects is a Hibernate proxy.
   *
   * @param left The left object
   * @param right The right object
   * @return True if one is a proxy
   */
  @Override
  public boolean canCompare(Object left, Object right) {
    return isHibernateProxy(left) || isHibernateProxy(right);
  }

  /**
   * Compares the given objects. If one of the objects is a proxy, the proxy is initialized and the
   * wrapped values are compared. If both objects are proxies and both objects are not yet loaded
   * (initialized) only the identifiers are compared. This avoids unitils performing unnecessary
   * loads from the database (potentially retrieving a huge amount of data). If the ids of the
   * proxies are identical, the objects are considered identical, if not the objects are considered
   * different.
   *
   * @param left The left object, not null
   * @param right The right object, not null
   * @param onlyFirstDifference True if only the first difference should be returned
   * @param reflectionComparator The root comparator for inner comparisons, not null
   * @return A ObjectDifference or null if both maps are equal
   */
  @Override
  public Difference compare(
      Object left,
      Object right,
      boolean onlyFirstDifference,
      ReflectionComparator reflectionComparator
  ) {
    if (isUninitialized(left) && isUninitialized(right)) {
      String leftType = getEntitiyName(left);
      String rightType = getEntitiyName(right);
      if (leftType == null || !leftType.equals(rightType)) {
        return new ObjectDifference(
            "Different hibernate proxy types. Left: " + leftType + ", right: " + rightType,
            left,
            right
        );
      }

      Object leftIdentifier = getIdentifier(left);
      Object rightIdentifier = getIdentifier(right);
      Difference identifierDifference = reflectionComparator
          .getDifference(leftIdentifier, rightIdentifier, onlyFirstDifference);
      if (identifierDifference != null) {
        ObjectDifference difference = new ObjectDifference(
            "Different hibernate proxy values",
            left,
            right
        );
        difference.addFieldDifference("<proxy id>", identifierDifference);
        return difference;
      }
      return null;
    }

    // get the actual value if the value is wrapped by a Hibernate proxy
    Object leftUnproxied = getUnproxiedValue(left);
    Object rightUnproxied = getUnproxiedValue(right);
    return reflectionComparator.getDifference(leftUnproxied, rightUnproxied, onlyFirstDifference);
  }

}