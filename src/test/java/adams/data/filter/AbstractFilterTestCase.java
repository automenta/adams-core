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
 * AbstractFilterTestCase.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.data.AbstractDataProcessorTestCase;
import adams.data.container.DataContainer;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;

/**
 * Ancestor for filter test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5759 $
 * @param <A> the type of algorithm to use
 * @param <D> the type of data to filter
 */
public abstract class AbstractFilterTestCase<A extends AbstractFilter, D extends DataContainer>
  extends AbstractDataProcessorTestCase<A, D, D> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractFilterTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/data/filter/data");
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Processes the input data and returns the processed data.
   *
   * @param data	the data to work on
   * @param scheme	the scheme to process the data with
   * @return		the processed data
   */
  @Override
  protected D process(D data, A scheme) {
    return (D) scheme.filter(data);
  }
}
