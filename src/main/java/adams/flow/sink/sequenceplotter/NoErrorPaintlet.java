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
 * NoErrorPaintlet.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Performs no painting at all.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7311 $
 */
public class NoErrorPaintlet
  extends AbstractErrorPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -713940308371660030L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Suppresses the painting of error bars.";
  }

  /**
   * Does nothing.
   *
   * @param g		the graphics context
   * @param data	the error data to draw
   * @param color	the color to draw in
   */
  @Override
  protected void drawData(Graphics g, SequencePlotSequence data, Color color) {
  }
}
