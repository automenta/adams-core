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
 * AbstractErrorPaintlet.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import java.awt.Color;
import java.awt.Graphics;

import adams.data.sequence.XYSequence;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractStrokePaintlet;

/**
 * Ancestor for error paintlets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7311 $
 */
public abstract class AbstractErrorPaintlet
  extends AbstractStrokePaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -4384114526759056961L;

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PAINT;
  }

  /**
   * Returns the plotter panel.
   *
   * @return		the plotter panel
   */
  public SequencePlotterPanel getPlotterPanel() {
    if (getPanel() instanceof SequencePlotterPanel)
      return (SequencePlotterPanel) getPanel();
    else
      return null;
  }

  /**
   * Returns the color for the data with the given index.
   *
   * @param index	the index of the spectrum
   * @return		the color for the spectrum
   */
  public Color getColor(int index) {
    return getPlotterPanel().getContainerManager().get(index).getColor();
  }

  /**
   * Draws the error data with the given color.
   *
   * @param g		the graphics context
   * @param data	the error data to draw
   * @param color	the color to draw in
   */
  protected abstract void drawData(Graphics g, SequencePlotSequence data, Color color);

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    int		i;
    XYSequence 	data;

    // paint all markers
    synchronized(getPlotterPanel().getContainerManager()) {
      for (i = 0; i < getPlotterPanel().getContainerManager().count(); i++) {
	if (!getPlotterPanel().getContainerManager().isVisible(i))
	  continue;
	data = getPlotterPanel().getContainerManager().get(i).getData();
	if (data instanceof SequencePlotSequence) {
	  synchronized(data) {
	    drawData(g, (SequencePlotSequence) data, getColor(i));
	  }
	}
      }
    }
  }
}
