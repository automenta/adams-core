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
 * FlowContainer.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.inspectionhandler;

import java.util.Hashtable;
import java.util.Iterator;

import adams.core.ClassLocator;
import adams.flow.container.AbstractContainer;

/**
 * Provides further insight into flow containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7987 $
 */
public class FlowContainer
  extends AbstractInspectionHandler {

  /**
   * Checks whether the handler can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the handler can handle this type of object
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(AbstractContainer.class, cls);
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
    AbstractContainer		cont;
    Iterator<String>		names;
    String			name;

    result = new Hashtable<>();

    cont  = (AbstractContainer) obj;
    names = cont.stored();
    while (names.hasNext()) {
      name = names.next();
      result.put(cont.getClass().getSimpleName() + "." + name, cont.getValue(name));
    }

    return result;
  }
}
