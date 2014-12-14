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
 * MakePlotContainerTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;


import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.sink.SequencePlotter;
import adams.gui.visualization.sequence.LinePaintlet;
import adams.parser.MathematicalExpressionText;
import adams.data.conversion.IntToDouble;

/**
 * Tests the MakePlotContainer transformer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8896 $
 */
public class MakePlotContainerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MakePlotContainerTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    adams.flow.source.ForLoop fl = new adams.flow.source.ForLoop();
    fl.setLoopLower(1);
    fl.setLoopUpper(30);
    fl.setLoopStep(1);

    IntToDouble i2d = new IntToDouble();
    Convert con = new Convert();
    con.setConversion(i2d);

    MathExpression me = new MathExpression();
    me.setExpression(new MathematicalExpressionText("X^2 + X"));

    MakePlotContainer make = new MakePlotContainer();
    make.setPlotName("X^2 + X");

    SequencePlotter sp = new SequencePlotter();
    sp.setPaintlet(new LinePaintlet());

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{fl, con, me, make, sp});

    return flow;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MakePlotContainerTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}
