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
 * PercentileTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.statistics;

import java.util.Random;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.data.statistics.Percentile class. Run from commandline with: <p/>
 * java adams.data.statistics.PercentileTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class PercentileTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PercentileTest(String name) {
    super(name);
  }

  /**
   * Tests the quartiles for Integer objects.
   */
  public void testInteger() {
    Vector<Integer>	values;
    Random		rand;
    int			i;
    int			index;
    Percentile<Integer>	q;
    int		q1;
    int		q3;

    values = new Vector<>();
    for (i = 1; i <= 100; i++)
      values.add(i);

    q    = new Percentile<>();
    rand = new Random(1);
    while (values.size() > 0) {
      index = rand.nextInt(values.size());
      q.add(values.get(index));
      values.remove(index);
    }

    q1 = q.getPercentile(0.25);
    q3 = q.getPercentile(0.75);

    assertEquals(25, q1);
    assertEquals(75, q3);
  }

  /**
   * Tests the quartiles for Double objects.
   */
  public void testDouble() {
    Vector<Double>	values;
    Random		rand;
    int			i;
    int			index;
    Percentile<Double>	q;
    Double		q1;
    Double		q3;

    values = new Vector<>();
    for (i = 1; i <= 100; i++)
      values.add(((double) i) / 10);

    q    = new Percentile<>();
    rand = new Random(1);
    while (values.size() > 0) {
      index = rand.nextInt(values.size());
      q.add(values.get(index));
      values.remove(index);
    }

    q1 = q.getPercentile(0.25);
    q3 = q.getPercentile(0.75);

    assertEquals(2.5, q1);
    assertEquals(7.5, q3);
  }

  /**
   * Tests the quartiles for String objects.
   */
  public void testString() {
    Vector<String>	values;
    Random		rand;
    int			i;
    int			index;
    Percentile<String>	q;
    String		q1;
    String		q3;

    values = new Vector<>();
    for (i = 1; i <= 100; i++)
      values.add(Integer.toString(1000 + i));

    q    = new Percentile<>();
    rand = new Random(1);
    while (values.size() > 0) {
      index = rand.nextInt(values.size());
      q.add(values.get(index));
      values.remove(index);
    }

    q1 = q.getPercentile(0.25);
    q3 = q.getPercentile(0.75);

    assertEquals(Integer.toString(1000 + 25), q1);
    assertEquals(Integer.toString(1000 + 75), q3);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PercentileTest.class);
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
