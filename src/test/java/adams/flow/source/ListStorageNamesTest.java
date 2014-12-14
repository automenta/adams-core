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
 * ListStorageNamesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for ListStorageNames actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision: 6662 $
 */
public class ListStorageNamesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ListStorageNamesTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ListStorageNamesTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor1 = new adams.flow.core.AbstractActor[3];

      // Flow.StringConstants
      adams.flow.source.StringConstants stringconstants2 = new adams.flow.source.StringConstants();
      stringconstants2.setOutputArray(true);

      argOption = (AbstractArgumentOption) stringconstants2.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] basestring3 = new adams.core.base.BaseString[3];
      basestring3[0] = (adams.core.base.BaseString) argOption.valueOf("1");
      basestring3[1] = (adams.core.base.BaseString) argOption.valueOf("2");
      basestring3[2] = (adams.core.base.BaseString) argOption.valueOf("3");
      stringconstants2.setStrings(basestring3);

      abstractactor1[0] = stringconstants2;

      // Flow.SetStorageValue
      adams.flow.transformer.SetStorageValue setstoragevalue4 = new adams.flow.transformer.SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue4.getOptionManager().findByProperty("storageName");
      setstoragevalue4.setStorageName((adams.flow.control.StorageName) argOption.valueOf("list"));

      abstractactor1[1] = setstoragevalue4;

      // Flow.Trigger
      adams.flow.control.Trigger trigger6 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] abstractactor7 = new adams.flow.core.AbstractActor[2];

      // Flow.Trigger.ListStorageNames
      adams.flow.source.ListStorageNames liststoragenames8 = new adams.flow.source.ListStorageNames();
      abstractactor7[0] = liststoragenames8;

      // Flow.Trigger.DumpFile
      adams.flow.sink.DumpFile dumpfile9 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile9.getOptionManager().findByProperty("outputFile");
      dumpfile9.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      dumpfile9.setAppend(true);

      abstractactor7[1] = dumpfile9;
      trigger6.setActors(abstractactor7);

      abstractactor1[2] = trigger6;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener12 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener12);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

