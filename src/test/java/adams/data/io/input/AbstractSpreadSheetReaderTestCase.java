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

/**
 * AbstractSpreadSheetReaderTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.io.FileUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Ancestor for spreadsheet reader test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7486 $
 */
public abstract class AbstractSpreadSheetReaderTestCase
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractSpreadSheetReaderTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/io/input/data");
  }

  /**
   * Reads the data using the reader.
   *
   * @param filename	the file to read (no path)
   * @param scheme	the scheme to process the data with
   * @return		the generated content
   */
  protected SpreadSheet load(String filename, SpreadSheetReader scheme) {
    SpreadSheet		result;

    m_TestHelper.copyResourceToTmp(filename);
    result = scheme.read(new TmpFile(filename));
    m_TestHelper.deleteFileFromTmp(filename);

    return result;
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected abstract String[] getRegressionInputFiles();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract SpreadSheetReader[] getRegressionSetups();

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the line indices
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Saves the generated content as file.
   *
   * @param data	the generated output data
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  protected boolean save(SpreadSheet data, String filename) {
    return FileUtils.writeToFile(new TmpFile(filename).getAbsolutePath(), data, false);
  }

  /**
   * Creates an output filename based on the input filename.
   *
   * @param input	the input filename (no path)
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(String input, int no) {
    String	result;
    int		index;
    String	ext;

    ext = "-out" + no;

    index = input.lastIndexOf('.');
    if (index == -1) {
      result = input + ext;
    }
    else {
      result  = input.substring(0, index);
      result += ext;
      result += input.substring(index);
    }

    return result;
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    SpreadSheet		data;
    boolean		ok;
    String		regression;
    int			i;
    String[]		input;
    SpreadSheetReader[]	setups;
    String[]		output;
    TmpFile[]		outputFiles;

    if (m_NoRegressionTest)
      return;

    input   = getRegressionInputFiles();
    output  = new String[input.length];
    setups  = getRegressionSetups();
    assertEquals("Number of files and setups differ!", input.length, setups.length);

    // process data
    for (i = 0; i < input.length; i++) {
      data = load(input[i], setups[i]);
      assertNotNull("Failed to load data?", data);

      output[i] = createOutputFilename(input[i], i);
      ok        = save(data, output[i]);
      assertTrue("Failed to save regression data?", ok);
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles, getRegressionIgnoredLineIndices());
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      if (setups[i] instanceof Destroyable)
	setups[i].destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
    cleanUpAfterRegression();
  }

  /**
   * For further cleaning up after the regression tests.
   * <p/>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
