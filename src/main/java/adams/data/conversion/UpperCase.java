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
 * UpperCase.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

/**
 <!-- globalinfo-start -->
 * Turns a String into an uppercase String.
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
 * @version $Revision: 5356 $
 */
public class UpperCase
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = -4017583319699378889L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Turns a String into an uppercase String.";
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    return ((String) m_Input).toUpperCase();
  }
}
