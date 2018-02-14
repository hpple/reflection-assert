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

import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.CollectionDifference;
import org.unitils.reflectionassert.difference.Difference;
import static org.unitils.util.CollectionUtils.convertToCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Comparator for collections and arrays.
 * All elements are compared in the same order, i.e. element 1 of the left collection with element 1 of the
 * right collection and so on.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CollectionComparator implements Comparator {


    /**
     * Returns true when both objects are arrays or collections.
     *
     * @param left  The left object
     * @param right The right object
     * @return True in case of arrays/collections
     */
    public boolean canCompare(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        if ((left.getClass().isArray() || left instanceof Collection) && (right.getClass().isArray() || right instanceof Collection)) {
            return true;
        }
        return false;
    }


    /**
     * Compared the given collections/arrays.
     *
     * @param left                 The left collection/array, not null
     * @param right                The right collection/array, not null
     * @param onlyFirstDifference  True if only the first difference should be returned
     * @param reflectionComparator The root comparator for inner comparisons, not null
     * @return A CollectionDifference or null if both collections are equal
     */
    public Difference compare(Object left, Object right, boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {
        // Convert to list and compare as collection
        List<Object> leftList = new ArrayList<Object>(convertToCollection(left));
        List<Object> rightList = new ArrayList<Object>(convertToCollection(right));

        int elementIndex = -1;
        CollectionDifference difference = new CollectionDifference("Different elements", left, right, leftList, rightList);

        Iterator<?> leftIterator = leftList.iterator();
        Iterator<?> rightIterator = rightList.iterator();
        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            elementIndex++;

            Difference elementDifference = reflectionComparator.getDifference(leftIterator.next(), rightIterator.next(), onlyFirstDifference);
            if (elementDifference != null) {
                difference.addElementDifference(elementIndex, elementDifference);
                if (onlyFirstDifference) {
                    return difference;
                }
            }
        }

        // check for missing elements 
        int leftElementIndex = elementIndex;
        while (leftIterator.hasNext()) {
            leftIterator.next();
            difference.addLeftMissingIndex(++leftElementIndex);
        }
        int rightElementIndex = elementIndex;
        while (rightIterator.hasNext()) {
            rightIterator.next();
            difference.addRightMissingIndex(++rightElementIndex);
        }

        if (difference.getElementDifferences().isEmpty() && difference.getLeftMissingIndexes().isEmpty() && difference.getRightMissingIndexes().isEmpty()) {
            return null;
        }
        return difference;
    }

}
