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
 * ScriptingLog.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;

/**
 * Shows the global scripting log.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6312 $
 */
public class ScriptingLog
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -2260458201891948961L;

  /**
   * Initializes the menu item with no owner.
   */
  public ScriptingLog() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public ScriptingLog(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "script.gif";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    createChildFrame(getOwner().getScriptingLogPanel(), 800, 600);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Scripting log";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return true;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.EXPERT;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_PROGRAM;
  }
}