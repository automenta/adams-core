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
 * AbstractGraphicalFlowExecutionListener.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import java.awt.Dimension;

import adams.gui.core.BasePanel;

/**
 * Ancestor for graphical listeners.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7385 $
 */
public abstract class AbstractGraphicalFlowExecutionListener
  extends AbstractFlowExecutionListener
  implements GraphicalFlowExecutionListener {
  
  /** for serialization. */
  private static final long serialVersionUID = -1461579886264001305L;

  /**
   * Returns the panel to use.
   * 
   * @return		the panel, null if none available
   */
  public abstract BasePanel newListenerPanel();
  
  /**
   * Returns the default size for the frame.
   * 
   * @return		the frame size
   */
  public Dimension getDefaultFrameSize() {
    return new Dimension(800, 600);
  }

  /**
   * Updates the GUI.
   */
  protected abstract void updateGUI();

  /**
   * Gets called when the flow execution ends.
   * <p/>
   * Also updates the GUI for a final time.
   */
  @Override
  public void finishListening() {
    super.finishListening();
    
    updateGUI();
  }
}
