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
 * DataContainer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import java.util.Hashtable;

import adams.core.ClassLocator;

/**
 * Provides further insight into data containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9982 $
 */
public class DataContainer
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.hasInterface(adams.data.container.DataContainer.class, cls);
  }

  /**
   * Returns further inspection values.
   *
   * @param obj		the object to further inspect
   * @return		the named inspected values
   */
  @Override
  public Hashtable<String,Object> inspect(Object obj) {
    Hashtable<String,Object>	result;

    result = new Hashtable<>();

    result.put("data points", ((adams.data.container.DataContainer) obj).toArray());

    return result;
  }
}
