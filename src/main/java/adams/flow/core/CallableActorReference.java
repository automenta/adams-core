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
 * CallableActorReference.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.base.AbstractBaseString;

/**
 * Wrapper around the name of a callable classifier (= string).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8665 $
 */
public class CallableActorReference
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = 5082359039831645068L;

  /**
   * Initializes the string with length 0.
   */
  public CallableActorReference() {
    this("");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public CallableActorReference(String s) {
    super(s);
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "The name of the callable actor.";
  }
}
