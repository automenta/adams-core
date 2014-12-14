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
 * SideBySideDiffTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for SideBySideDiff actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision: 6839 $
 */
public class SideBySideDiffTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SideBySideDiffTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("diff1.txt");
    m_TestHelper.copyResourceToTmp("diff2.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("diff1.txt");
    m_TestHelper.deleteFileFromTmp("diff2.txt");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SideBySideDiffTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      tmp2.setOutputArray(true);

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] tmp3 = new adams.core.io.PlaceholderFile[2];
      tmp3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/diff1.txt");
      tmp3[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/diff2.txt");
      tmp2.setFiles(tmp3);

      tmp1[0] = tmp2;
      adams.flow.transformer.Diff tmp4 = new adams.flow.transformer.Diff();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("type");
      tmp4.setType((adams.flow.transformer.Diff.DiffType) argOption.valueOf("SIDE_BY_SIDE"));

      tmp1[1] = tmp4;
      adams.flow.sink.SideBySideDiff tmp6 = new adams.flow.sink.SideBySideDiff();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("x");
      tmp6.setX((Integer) argOption.valueOf("-3"));

      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("y");
      tmp6.setY((Integer) argOption.valueOf("-3"));

      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter tmp10 = new adams.gui.print.NullWriter();
      tmp6.setWriter(tmp10);

      tmp1[2] = tmp6;
      flow.setActors(tmp1);

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

