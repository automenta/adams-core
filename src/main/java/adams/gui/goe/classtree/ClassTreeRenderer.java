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
 * ClassTreeRenderer.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe.classtree;

import java.util.Hashtable;

import javax.swing.Icon;

import adams.gui.core.GUIHelper;
import adams.gui.core.dotnotationtree.DotNotationRenderer;

/**
 * A specialized renderer for the class tree elements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class ClassTreeRenderer
  extends DotNotationRenderer<ClassNode> {

  /** for serialization. */
  private static final long serialVersionUID = -3242391430220560720L;

  /** stores the classname/icon relationship. */
  protected Hashtable<String,Icon> m_Icons;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Icons = new Hashtable<>();
  }

  /**
   * Tries to obtain the icon for the given object.
   *
   * @param node	the node get the icon for
   * @return		the associated icon or null if not found
   */
  protected Icon getIcon(ClassNode node) {
    Icon	result;
    String	classname;

    result    = null;
    classname = node.getItem();

    if (classname != null) {
      if (m_Icons.containsKey(classname)) {
        result = m_Icons.get(classname);
      }
      else {
        try {
          result = GUIHelper.getIcon(Class.forName(classname));
          m_Icons.put(classname, result);
        }
        catch (Exception e) {
          result = null;
        }
      }
    }

    return result;
  }
}