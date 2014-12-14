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
 * AboutBoxPanel.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import adams.env.Modules;
import adams.env.Modules.Module;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;

/**
 * Represents an "About" box displayed from the main menu.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8466 $
 */
public class AboutBoxPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -5180917605195603000L;

  /** the panel for the image. */
  protected JPanel m_PanelImage;

  /** the label displaying the image. */
  protected JLabel m_LabelImage;

  /** the panel for the title, copyright, etc. */
  protected JPanel m_PanelTitle;

  /** the panel with the modules. */
  protected JPanel m_PanelModules;

  /** the scroll pane for the modules. */
  protected BaseScrollPane m_ScrollPane;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;
    JPanel		panelModule;
    List<Module>	modules;
    String		name;
    JLabel		label;
    String		tiptext;

    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelImage = new JPanel(new BorderLayout());
    m_LabelImage = new JLabel(GUIHelper.getLogoImage());
    m_PanelImage.add(m_LabelImage, BorderLayout.CENTER);
    add(m_PanelImage, BorderLayout.CENTER);

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.SOUTH);

    m_PanelTitle = new JPanel();
    m_PanelTitle.setLayout(new GridLayout(0, 1));
    m_PanelTitle.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    panel.add(m_PanelTitle, BorderLayout.CENTER);

    modules = Modules.getSingleton().getModules();
    m_PanelModules = new JPanel(new FlowLayout(FlowLayout.LEFT));
    for (Module module: modules) {
      panelModule = new JPanel(new BorderLayout());
      panelModule.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
      tiptext = "";
      if (!module.getDescription().isEmpty()) {
	if (tiptext.length() > 0)
	  tiptext += "<br>\n";
	tiptext += module.getDescription();
      }
      if (!module.getAuthor().isEmpty()) {
	if (tiptext.length() > 0)
	  tiptext += "<br>\n";
	tiptext += module.getAuthor();
      }
      if (!module.getOrganization().isEmpty()) {
	if (tiptext.length() > 0)
	  tiptext += "<br>\n";
	tiptext += module.getOrganization();
      }
      label = new JLabel(module.getLogo());
      if (tiptext.length() > 0)
	label.setToolTipText("<html>" + tiptext + "</html>");
      panelModule.add(label, BorderLayout.CENTER);
      name = "<html><center>";
      name += module.getName() + "<br>" + (module.getVersion().isEmpty() ? "?.?.?" : module.getVersion());
      name += "</center></html>";
      panelModule.add(new JLabel(name), BorderLayout.SOUTH);
      m_PanelModules.add(panelModule);
    }
    m_ScrollPane = new BaseScrollPane(m_PanelModules);
    m_ScrollPane.setPreferredSize(new Dimension(75, 115));
    panel.add(m_ScrollPane, BorderLayout.SOUTH);
  }

  /**
   * Sets the image to display.
   *
   * @param name	the name of the image
   */
  public void setImage(String name) {
    m_LabelImage.setIcon(GUIHelper.getIcon(name));
  }

  /**
   * Adds a label with the info.
   *
   * @param info	the information to display
   */
  public void addInfo(String info) {
    m_PanelTitle.add(new JLabel(info, SwingConstants.CENTER));
  }
}
