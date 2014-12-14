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
 * SetStorageValueTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.random.JavaRandomInt;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.sink.DumpFile;
import adams.flow.source.RandomNumberGenerator;
import adams.flow.source.StorageValue;
import adams.test.TmpFile;

/**
 * Tests the SetStorageValue actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4748 $
 */
public class SetStorageValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SetStorageValueTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    RandomNumberGenerator rng = new RandomNumberGenerator();
    rng.setGenerator(new JavaRandomInt());
    rng.setMaxNum(100);

    SetStorageValue ssv = new SetStorageValue();
    ssv.setStorageName(new StorageName("rand"));

    StorageValue sv = new StorageValue();
    sv.setStorageName(new StorageName("rand"));

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Trigger tr = new Trigger();
    tr.setActors(new AbstractActor[]{
	sv, df
    });

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{rng, ssv, tr});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SetStorageValueTest.class);
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
