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
 * ArrayLength.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.statistics;

import java.io.Serializable;

/**
 <!-- globalinfo-start -->
 * Determines the length of an array.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6555 $
 * @param <T> the data to process
 */
public class ArrayLength<T extends Serializable>
  extends AbstractArrayStatistic<T> {

  /** for serialization. */
  private static final long serialVersionUID = -1540382151498537227L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Determines the length of an array.";
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  @Override
  public int getMin() {
    return 1;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  @Override
  public int getMax() {
    return -1;
  }

  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  @Override
  protected StatisticContainer doCalculate() {
    StatisticContainer<Number>	result;
    int				i;
    String 			prefix;

    result = new StatisticContainer<>(1, size());

    prefix = "length";
    if (size() > 1)
      prefix += "-";

    for (i = 0; i < size(); i++) {
      if (size() > 1)
	result.setHeader(i, prefix + (i+1));
      else
	result.setHeader(i, prefix);

      result.setCell(0, i, get(i).length);
    }

    return result;
  }
}
