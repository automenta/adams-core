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
 * AbstractContainerDisplayStringGenerator.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import java.io.Serializable;

/**
 * Abstract class for generating display IDs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8077 $
 * @param <T> the type of container to use
 */
public abstract class AbstractContainerDisplayStringGenerator<T extends AbstractContainer>
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 3359135975733339153L;

  /**
   * Returns the display String for the container.
   *
   * @param c		the container to get the display string for
   * @return		the string
   */
  public abstract String getDisplay(T c);
}