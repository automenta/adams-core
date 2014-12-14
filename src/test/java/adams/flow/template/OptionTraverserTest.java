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
 * OptionTraverserTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.template;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the OptionTraverser template.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5046 $
 */
public class OptionTraverserTest
  extends AbstractActorTemplateTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public OptionTraverserTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractActorTemplate[] getRegressionSetups() {
    OptionTraverser[]	result;

    result = new OptionTraverser[2];
    result[0] = new OptionTraverser();
    result[1] = new OptionTraverser();
    result[1].setOnce(true);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(OptionTraverserTest.class);
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
