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
 * GridView.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import adams.core.QuickInfoHelper;
import adams.flow.core.AbstractActor;
import adams.flow.sink.CallableSink;
import adams.flow.sink.ComponentSupplier;
import adams.gui.core.BasePanel;
import adams.gui.print.JComponentWriter;
import adams.gui.print.NullWriter;

/**
 <!-- globalinfo-start -->
 * Displays multiple graphical actors in a grid. The actors get added row-wise to the grid from top-left to bottom-right. The actors can be referenced in the flow using adams.flow.sink.CallableSink actors.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: GridView
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-actor &lt;adams.flow.core.AbstractActor&gt; [-actor ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The panel-generating actors to display in the grid.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-num-rows &lt;int&gt; (property: numRows)
 * &nbsp;&nbsp;&nbsp;The number of rows in the grid.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-cols &lt;int&gt; (property: numCols)
 * &nbsp;&nbsp;&nbsp;The number of columns in the grid.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8849 $
 */
public class GridView
  extends AbstractMultiView 
  implements ComponentSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -4454052058077687116L;
  
  /** the number of rows to display. */
  protected int m_NumRows;

  /** the number of columns to display. */
  protected int m_NumCols;

  /** the panels to display. */
  protected List<BasePanel> m_Panels;

  /** the writer to use. */
  protected JComponentWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Displays multiple graphical actors in a grid. The actors get added "
	+ "row-wise to the grid from top-left to bottom-right. The actors "
	+ "can be referenced in the flow using " 
	+ CallableSink.class.getName() + " actors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-rows", "numRows",
	    1, 1, null);

    m_OptionManager.add(
	    "num-cols", "numCols",
	    1, 1, null);

    m_OptionManager.add(
	    "writer", "writer",
	    new NullWriter());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Panels = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "numRows", m_NumRows, ", Rows: ");
    result += QuickInfoHelper.toString(this, "numCols", m_NumCols, ", Cols: ");
    
    return result;
  }

  /**
   * Sets the number of rows in the grid.
   *
   * @param value	the number of rows
   */
  public void setNumRows(int value) {
    m_NumRows = value;
    reset();
  }

  /**
   * Returns the number of rows in the grid.
   *
   * @return 		the number of rows
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String numRowsTipText() {
    return "The number of rows in the grid.";
  }

  /**
   * Sets the number of columns in the grid.
   *
   * @param value	the number of cols
   */
  public void setNumCols(int value) {
    m_NumCols = value;
    reset();
  }

  /**
   * Returns the number of columns in the grid.
   *
   * @return 		the number of cols
   */
  public int getNumCols() {
    return m_NumCols;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String numColsTipText() {
    return "The number of columns in the grid.";
  }

  /**
   * Sets the writer.
   *
   * @param value 	the writer
   */
  public void setWriter(JComponentWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer.
   *
   * @return 		the writer
   */
  public JComponentWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for generating the graphics output.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String actorsTipText() {
    return "The panel-generating actors to display in the grid.";
  }

  /**
   * Ensures that the wrapper is visible.
   * 
   * @param wrapper	the wrapper to make visible
   * @return		true if successful
   */
  @Override
  public boolean makeVisible(ViewWrapper wrapper) {
    // nothing to do
    return true;
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;
    JLabel	label;
    BasePanel	panel;
    int		i;
    
    result = new BasePanel(new GridLayout(m_NumRows, m_NumCols));

    // add dummy panels
    m_Panels = new ArrayList<>();
    for (i = 0; i < m_Actors.size(); i++) {
      panel = new BasePanel(new BorderLayout());
      label = new JLabel(m_Actors.get(i).getName(), JLabel.CENTER);
      panel.add(label, BorderLayout.CENTER);
      result.add(panel);
      m_Panels.add(panel);
    }
    
    return result;
  }
  
  /**
   * Replaces the current dummy panel with the actual panel.
   * 
   * @param actor	the actor this panel is for
   * @param panel	the panel to replace the dummy one
   */
  @Override
  public void addPanel(AbstractActor actor, BasePanel panel) {
    int		index;
    Runnable	run;
    
    index = indexOf(actor.getName());
    m_Panels.set(index, panel);
    
    run = new Runnable() {
      public void run() {
	m_Panel.removeAll();
	for (JComponent p: m_Panels)
	  m_Panel.add(p);
	m_Panel.validate();
	m_Panel.doLayout();
      };
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Returns the current component.
   *
   * @return		the current component, can be null
   */
  @Override
  public JComponent supplyComponent() {
    return m_Panel;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    Runnable	run;

    if (!(m_Writer instanceof NullWriter)) {
      run = new Runnable() {
	@Override
	public void run() {
	  try {
	    m_Writer.setComponent(supplyComponent());
	    m_Writer.toOutput();
	  }
	  catch (Exception e) {
	    handleException("Failed to write graphical output", e);
	  }
	}
      };
      SwingUtilities.invokeLater(run);
    }
    
    super.wrapUp();
  }
  
  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_Panels != null) {
      m_Panels.clear();
      m_Panels = null;
    }
    
    super.cleanUp();
  }
}
