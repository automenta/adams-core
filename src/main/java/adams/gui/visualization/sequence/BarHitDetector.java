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
 * BarHitDetector.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.data.sequence.XYSequenceUtils;
import adams.gui.visualization.container.VisibilityContainer;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 * Detects selections of sequence points in the sequence panel.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9458 $
 */
public class BarHitDetector
  extends AbstractXYSequencePointHitDetector {

  /** for serialization. */
  private static final long serialVersionUID = -6333044496555537078L;

  /** the width of the bar in pixel. */
  protected int m_Width;

  /**
   * Initializes the hit detector (constructor only for GOE) with no owner.
   */
  public BarHitDetector() {
    this(null);
  }

  /**
   * Initializes the hit detector.
   *
   * @param owner	the paintlet that uses this detector
   */
  public BarHitDetector(XYSequencePaintlet owner) {
    super(owner);
  }

  /**
   * Sets the width of the bar.
   *
   * @param value	width in pixel
   */
  public void setWidth(int value) {
    m_Width = value;
  }

  /**
   * Returns the width of the bar.
   *
   * @return		width in pixel
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Checks for a hit.
   *
   * @param e		the MouseEvent (for coordinates)
   * @return		the associated object with the hit, otherwise null
   */
  @Override
  protected Object isHit(MouseEvent e) {
    double			x;
    int				y;
    int				i;
    int				height;
    XYSequence			s;
    XYSequencePoint		sp;
    Vector<XYSequencePoint>	result;
    AxisPanel			axisBottom;
    AxisPanel			axisLeft;
    int				index;
    List<XYSequencePoint>	points;
    boolean			logging;
    Rectangle			rect;

    result     = new Vector<>();
    axisBottom = m_Owner.getPlot().getAxis(Axis.BOTTOM);
    axisLeft   = m_Owner.getPlot().getAxis(Axis.LEFT);
    x          = axisBottom.posToValue((int) e.getX());
    logging    = isLoggingEnabled();

    for (i = 0; i < m_Owner.getSequencePanel().getContainerManager().count(); i++) {
      if (!((VisibilityContainer) m_Owner.getSequencePanel().getContainerManager().get(i)).isVisible())
	continue;

      // check for hit
      s      = m_Owner.getSequencePanel().getContainerManager().get(i).getData();
      points = s.toList();
      
      if (logging)
	getLogger().info("\n" + s.getID() + ":");
      index = XYSequenceUtils.findClosestX(points, x);
      if (index == -1)
	continue;
      sp = points.get(index);

      height = Math.abs(axisLeft.valueToPos(0) - axisLeft.valueToPos(sp.getY()));
      if (sp.getY() > 0)
	y = axisLeft.valueToPos(sp.getY());
      else
	y = axisLeft.valueToPos(0);
      rect   = new Rectangle(
	  axisBottom.valueToPos(sp.getX()) - m_Width / 2 - m_MinimumPixelDifference,
	  y - m_MinimumPixelDifference,
	  m_Width + m_MinimumPixelDifference*2 + 1,
	  height + m_MinimumPixelDifference*2 + 1);
      
      if (logging)
	getLogger().info("rect: " + rect);
      
      if (rect.contains(e.getPoint())) {
	if (logging)
	  getLogger().info("hit!");
	result.add(sp);
      }
    }

    if (result.size() > 0)
      return result;
    else
      return null;
  }
}
