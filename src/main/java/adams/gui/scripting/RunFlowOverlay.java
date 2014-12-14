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
 * AbstractRunFlowOverlay.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.flow.control.SubProcess;

/**
 * Abstract ancestor for scriptlets that run flows and overlay the data
 * containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class RunFlowOverlay
  extends AbstractFlowScriptlet {

  /** for serialization. */
  private static final long serialVersionUID = 6501404255520088400L;

  /** the action to execute. */
  public final static String ACTION = "run-flow-overlay";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		the action string
   */
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  public String getDescription() {
    return
        "Executes the flow stored in the given file.\n"
      + "The base actor has to be '" + SubProcess.class.getName() + "'.\n"
      + "The processed " + getOwner().getRequiredFlowClass().getName().replaceAll(".*\\.", "") + "s "
      + "overlay the currently loaded ones.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public String process(String options) throws Exception {
    return process(options, true);
  }
}
