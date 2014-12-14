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
 * FilterDialog.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.container;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.data.container.DataContainer;
import adams.data.filter.AbstractFilter;
import adams.data.filter.PassThrough;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.BaseDialog;
import adams.gui.event.FilterEvent;
import adams.gui.event.FilterListener;
import adams.gui.goe.GenericObjectEditorPanel;

/**
 * A dialog that lets the user select a filter and also choose whether to
 * overlay the original data or not.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 * @param <T> the type of data the filters handle
 */
public class FilterDialog<T extends DataContainer>
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = 3690400655773745448L;

  /** the current filter. */
  protected AbstractFilter<T> m_CurrentFilter;

  /** the panel with the filter. */
  protected GenericObjectEditorPanel m_PanelFilter;

  /** the OK button. */
  protected JButton m_ButtonOK;

  /** the Cancel button. */
  protected JButton m_ButtonCancel;

  /** the checkbox for whether the filtered data is to be overlayed over the
   * original data. */
  protected JCheckBox m_CheckboxOverlay;

  /** the listener. */
  protected FilterListener m_FilterListener;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public FilterDialog(Dialog owner) {
    super(owner, "Filter");
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public FilterDialog(Frame owner) {
    super(owner, "Filter");
  }

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    m_CurrentFilter  = new PassThrough<>();
    m_FilterListener = null;
  }

  /**
   * For initializing the GUI.
   */
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    getContentPane().setLayout(new BorderLayout());

    m_PanelFilter = new GenericObjectEditorPanel(AbstractFilter.class, m_CurrentFilter, true);
    m_PanelFilter.setPrefix("Filter");
    m_PanelFilter.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	m_CurrentFilter = (AbstractFilter) ((AbstractChooserPanel) e.getSource()).getCurrent();
      }
    });

    m_CheckboxOverlay = new JCheckBox("Overlay original data");
    m_CheckboxOverlay.setMnemonic('d');

    m_ButtonOK = new JButton("OK");
    m_ButtonOK.setMnemonic('O');
    m_ButtonOK.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  setVisible(false);
	  if (m_FilterListener != null)
	    m_FilterListener.filter(
		new FilterEvent<>(
		    FilterDialog.this,
		    m_CurrentFilter,
		    m_CheckboxOverlay.isSelected()));
	}
    });

    m_ButtonCancel = new JButton("Cancel");
    m_ButtonCancel.setMnemonic('C');
    m_ButtonCancel.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  setVisible(false);
	}
    });

    // filter
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panel2.add(m_PanelFilter, BorderLayout.CENTER);
    getContentPane().add(panel2, BorderLayout.NORTH);

    // overlay
    panel2 = new JPanel(new BorderLayout());
    getContentPane().add(panel2, BorderLayout.CENTER);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel2.add(panel, BorderLayout.NORTH);
    panel.add(m_CheckboxOverlay);

    // buttons
    panel2 = new JPanel(new BorderLayout());
    getContentPane().add(panel2, BorderLayout.SOUTH);
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel2.add(panel, BorderLayout.NORTH);
    panel.add(m_ButtonOK);
    panel.add(m_ButtonCancel);

    pack();
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(AbstractFilter<T> value) {
    m_CurrentFilter = value;
    m_PanelFilter.setCurrent(value);
  }

  /**
   * Returns the current filter.
   *
   * @return		the filter
   */
  public AbstractFilter<T> getFilter() {
    return m_CurrentFilter;
  }

  /**
   * Sets whether to overlay the filtered data with the original data.
   *
   * @param value	if true then the data will be overlayed
   */
  public void setOverlayOriginalData(boolean value) {
    m_CheckboxOverlay.setSelected(value);
  }

  /**
   * Returns whether the filtered data is to be overlayed with the original
   * data.
   *
   * @return		true if data should be overlayed
   */
  public boolean getOverlayOriginalData() {
    return m_CheckboxOverlay.isSelected();
  }

  /**
   * Sets the listener to use.
   *
   * @param value	the listener
   */
  public void setFilterListener(FilterListener value) {
    m_FilterListener = value;
  }

  /**
   * Returns the listener to use.
   *
   * @return		the listener
   */
  public FilterListener getFilterListener() {
    return m_FilterListener;
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
    super.beforeShow();

    m_PanelFilter.grabFocus();
  }
}
