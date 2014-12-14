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
 * NamedSetupsDefinition.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.env;

import adams.core.NamedSetups;

/**
 * Definition for the NamedSetups props file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class NamedSetupsDefinition
  extends AbstractPropertiesDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 761924597513800238L;

  /** the key as constant. */
  public final static String KEY = "named setups";

  /**
   * Returns the key this definition is for.
   *
   * @return		the key
   */
  public String getKey() {
    return KEY;
  }

  /**
   * Returns the properties file name (no path) this definition is for.
   *
   * @return		the key
   */
  public String getFile() {
    return NamedSetups.FILENAME;
  }

  /**
   * Updates the environment object with its definition for the props file
   * (whether to add/replace/etc the values).
   *
   * @param env		the environment object to update
   */
  public void update(AbstractEnvironment env) {
    add(env, Project.NAME + "/core", new String[0]);
  }
}
