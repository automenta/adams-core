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
 * MetaFileReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

/**
 * Interface for file readers that actually use another reader internally
 * with its own supported file format extensions. This interface is used
 * to expose these.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7762 $
 */
public interface MetaFileReader {

  /**
   * Returns the underlying format extensions.
   * 
   * @return		the format extensions (excluding dot)
   */
  public String[] getActualFormatExtensions();
}
