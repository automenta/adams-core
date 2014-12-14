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
 * FromFileTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.template;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the FromFile template.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class FromFileTest
  extends AbstractActorTemplateTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FromFileTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("subprocess.flow");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("subprocess.flow");

    super.tearDown();
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractActorTemplate[] getRegressionSetups() {
    FromFile[]	result;

    result = new FromFile[2];

    result[0] = new FromFile();
    result[0].setTemplateFile(new TmpFile("subprocess.flow"));

    result[1] = new FromFile();
    result[1].setTemplateFile(new TmpFile("subprocess.flow"));
    result[1].setName("blahblah");

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FromFileTest.class);
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
