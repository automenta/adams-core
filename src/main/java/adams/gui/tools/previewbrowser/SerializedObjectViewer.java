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
 * SerializedObjectViewer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import javax.swing.JPanel;

/**
 * Interface for classes that display serialized content.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5664 $
 */
public interface SerializedObjectViewer 
  extends Comparable<SerializedObjectViewer> {

  /**
   * Returns whether viewer handles this object.
   * 
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  public boolean handles(Object obj);
  
  /**
   * Creates a {@link JPanel} for the given object.
   * 
   * @param obj		the object to create a preview for
   * @return		the preview, null if failed to generate
   */
  public JPanel getPreview(Object obj);
}
