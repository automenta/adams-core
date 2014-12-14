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
 * StorageName.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper around the name of a storage item (= string).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10214 $
 */
public class StorageName
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = 3909330668809621538L;

  /** the default value. */
  public final static String DEFAULT = "storage";
  
  /**
   * Initializes the name with a default value.
   */
  public StorageName() {
    this(DEFAULT);
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public StorageName(String s) {
    super(s);
  }

  /**
   * Initializes the internal object.
   */
  @Override
  protected void initialize() {
    m_Internal = DEFAULT;
  }

  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if a valid name
   * @see		Storage#isValidName(String)
   */
  @Override
  public boolean isValid(String value) {
    if (value == null)
      return false;
    return Storage.isValidName(value);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Name for storage item, valid characters: " + Storage.CHARS;
  }
}
