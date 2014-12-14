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
 * SystemInfo.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.tools.SystemInfoPanel;

/**
 * Displays system parameters (Java, etc.).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class SystemInfo
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 8793960724995111227L;

  /**
   * Initializes the menu item with no owner.
   */
  public SystemInfo() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public SystemInfo(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  public String getIconName() {
    return "properties.gif";
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    createChildFrame(new SystemInfoPanel(), 800, 600);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "System info";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  public boolean isSingleton() {
    return true;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_HELP;
  }
}