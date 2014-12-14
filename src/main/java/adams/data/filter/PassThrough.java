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
 * PassThrough.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import adams.data.container.DataContainer;

/**
 <!-- globalinfo-start -->
 * A dummy filter that just passes the data through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 * @param <T> the type of data to pass through the filter
 */
public class PassThrough<T extends DataContainer>
  extends AbstractFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -3576292594181295517L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "A dummy filter that just passes the data through.";
  }

  /**
   * Performs no filtering at all, just uses a copy of the input as filtered data.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  protected T processData(T data) {
    return (T) data.getClone();
  }
}
