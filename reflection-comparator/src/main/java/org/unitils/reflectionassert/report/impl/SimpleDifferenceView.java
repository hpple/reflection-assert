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
package org.unitils.reflectionassert.report.impl;

import static org.unitils.reflectionassert.report.impl.DefaultDifferenceReport.MAX_LINE_SIZE;

import org.unitils.core.util.ObjectFormatter;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.report.DifferenceView;

/**
 * @author Filip Neven
 */
public class SimpleDifferenceView implements DifferenceView {

  private static final int ASSERTION_ERROR_FQCN_LENGTH =
      "org.opentest4j.AssertionFailedError".length();

  private final ObjectFormatter objectFormatter = new ObjectFormatter();

  /**
   * Creates a string representation of the given difference tree.
   *
   * @param difference The root difference, not null
   * @return The string representation, not null
   */
  @Override
  public final String createView(Difference difference) {
    String expectedStr = objectFormatter.format(difference.getLeftValue());
    String actualStr = objectFormatter.format(difference.getRightValue());
    String formattedOnOneLine = formatOnOneLine(expectedStr, actualStr);
    if (ASSERTION_ERROR_FQCN_LENGTH + 2 + formattedOnOneLine.length() < MAX_LINE_SIZE) {
      return formattedOnOneLine;
    } else {
      return formatOnTwoLines(expectedStr, actualStr);
    }
  }

  protected String formatOnOneLine(String expectedStr, String actualStr) {
    return "Expected: " + expectedStr + ", actual: " + actualStr;
  }

  protected String formatOnTwoLines(String expectedStr, String actualStr) {
    return "\nExpected: " + expectedStr + "\n  Actual: " + actualStr;
  }
}
