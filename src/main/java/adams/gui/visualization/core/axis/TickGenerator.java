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
 * TickGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core.axis;

import java.util.List;

import adams.core.ShallowCopySupporter;
import adams.core.option.OptionHandler;

/**
 * Interface for tick generators.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7800 $
 */
public interface TickGenerator
  extends OptionHandler, ShallowCopySupporter<TickGenerator> {
  
  /**
   * Sets the owning axis model.
   *
   * @param value	the model
   */
  public void setParent(AbstractAxisModel value);

  /**
   * Returns the owning axis model.
   *
   * @return		the model
   */
  public AbstractAxisModel getParent();

  /**
   * Returns the ticks of this axis.
   *
   * @return		the current ticks to display
   */
  public List<Tick> getTicks();
}
