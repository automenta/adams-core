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
 * ArrayPercentileTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import java.util.Random;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Tests the adams.data.statistics.ArrayPercentile class. Run from commandline with: <p/>
 * java adams.data.statistics.ArrayPercentileTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class ArrayPercentileTest
  extends AbstractArrayStatisticTestCase<ArrayPercentile, Number> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayPercentileTest(String name) {
    super(name);
  }

  /**
   * Returns the data used in the regression test.
   *
   * @return		the data
   */
  protected Number[][][] getRegressionInputData() {
    Vector<Double>	values;
    Random		rand;
    int			i;
    int			index;
    Percentile<Double>	q;
    Number[][][]	result;
    Double[]		vals;

    values = new Vector<>();
    for (i = 1; i <= 100; i++)
      values.add(((double) i) / 10);

    vals = new Double[100];
    rand = new Random(1);
    i    = 0;
    while (values.size() > 0) {
      index   = rand.nextInt(values.size());
      vals[i] = values.get(index);
      values.remove(index);
      i++;
    }

    result = new Number[2][1][];
    result[0][0] = vals;
    result[1][0] = vals;

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected ArrayPercentile[] getRegressionSetups() {
    ArrayPercentile[]	result;

    result = new ArrayPercentile[2];

    result[0] = new ArrayPercentile();
    result[1] = new ArrayPercentile();
    result[1].setPercentile(0.25);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayPercentileTest.class);
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
