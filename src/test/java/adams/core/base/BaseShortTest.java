/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * BaseShortTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.core.base.BaseShort class. Run from commandline with: <p/>
 * java adams.core.base.BaseShortTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class BaseShortTest
  extends AbstractBaseNumberTestCase<BaseShort> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BaseShortTest(String name) {
    super(name);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  protected BaseShort getDefault() {
    return new BaseShort();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  protected BaseShort getCustom(String s) {
    return new BaseShort(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  protected String getTypicalValue() {
    return "12";
  }

  /**
   * Returns the string representing a value below the parsing range that must
   * fail parsing.
   *
   * @return		the value, if null the test gets ignored
   */
  protected String getBelowValue() {
    return "-32769";  // -2^15 - 1
  }

  /**
   * Returns the string representing a value above the parsing range that must
   * fail parsing.
   *
   * @return		the value, if null the test gets ignored
   */
  protected String getAboveValue() {
    return "32768";  // 2^15
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BaseShortTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
