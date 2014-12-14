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
 * PlotPanelTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.visualization.core.PlotPanel;

/**
 * Only for testing the PlotPanel component.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class PlotPanelTest
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 1947370537357191065L;

  /**
   * Initializes the menu item with no owner.
   */
  public PlotPanelTest() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public PlotPanelTest(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Launches the functionality of the menu item.
   */
  public void launch() {
    createChildFrame(new PlotPanel(true), 800, 600);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  public String getTitle() {
    return "PlotPanel test";
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
    return UserMode.DEBUGGER;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  public String getCategory() {
    return CATEGORY_VISUALIZATION;
  }
}