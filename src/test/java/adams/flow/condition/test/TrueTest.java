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
 * TrueTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the True condition.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7399 $
 */
public class TrueTest
  extends AbstractTestConditionTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TrueTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractTestCondition[] getRegressionSetups() {
    return new True[]{
	new True()
    };
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(TrueTest.class);
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