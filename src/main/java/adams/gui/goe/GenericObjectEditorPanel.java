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
 * GenericObjectEditorPanel.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.goe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.GUIHelper;
import adams.gui.event.HistorySelectionEvent;
import adams.gui.event.HistorySelectionListener;
import adams.gui.goe.Favorites.FavoriteSelectionEvent;
import adams.gui.goe.Favorites.FavoriteSelectionListener;
import adams.gui.goe.GenericObjectEditor.GOEPanel;

/**
 * A panel that contains text field with the current setup of the object
 * and a button for bringing up the GenericObjectEditor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8097 $
 */
public class GenericObjectEditorPanel
  extends AbstractChooserPanel {

  /** for serialization. */
  private static final long serialVersionUID = -8351558686664299781L;

  /** the generic object editor. */
  protected transient GenericObjectEditor m_Editor;

  /** the dialog for displaying the editor. */
  protected GenericObjectEditorDialog m_Dialog;

  /** the history of used setups. */
  protected ObjectHistory m_History;

  /** the current object. */
  protected transient Object m_Current;

  /**
   * Initializes the panel with the given class and default value. Cannot
   * change the class.
   *
   * @param cls				the class to handler
   * @param defValue			the default value
   */
  public GenericObjectEditorPanel(Class cls, Object defValue) {
    this(cls, defValue, false);
  }

  /**
   * Initializes the panel with the given class and default value. Cannot
   * change the class.
   *
   * @param cls				the class to handler
   * @param defValue			the default value
   * @param canChangeClassInDialog	whether the user can change the class
   */
  public GenericObjectEditorPanel(Class cls, Object defValue, boolean canChangeClassInDialog) {
    super();

    m_Editor = new GenericObjectEditor(canChangeClassInDialog);
    m_Editor.setClassType(cls);
    ((GOEPanel) m_Editor.getCustomEditor()).addOkListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (isEditable()) {
	  setCurrent(m_Editor.getValue());
	  m_History.add(m_Editor.getValue());
	  notifyChangeListeners(new ChangeEvent(m_Self));
	}
      }
    });
    ((GOEPanel) m_Editor.getCustomEditor()).addCancelListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Editor.setValue(getCurrent());
      }
    });

    setCurrent(defValue);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Editor  = null;
    m_History = new ObjectHistory();
    m_Current = null;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected Object doChoose() {
    if (m_Current != null)
      m_Editor.setValue(m_Current);
    if (m_Dialog == null)
      m_Dialog = GenericObjectEditorDialog.createDialog(this, m_Editor);
    m_Dialog.setLocationRelativeTo(GenericObjectEditorPanel.this);
    m_Dialog.setVisible(true);
    if (m_Dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
      return m_Editor.getValue();
    else
      return null;
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  @Override
  protected Object fromString(String value) {
    try {
      return OptionUtils.forAnyCommandLine(Object.class, value);
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  @Override
  public Object getCurrent() {
    return Utils.deepCopy(m_Current);
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(Object value) {
    return OptionUtils.getCommandLine(value);
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null
   * @return		true if successfully set
   */
  @Override
  public boolean setCurrent(Object value) {
    boolean	result;

    result = super.setCurrent(value);

    if (result) {
      m_Current = value;
      if (m_Current != null)
        m_Editor.setValue(m_Current);
    }

    return result;
  }
  
  /**
   * Generates the right-click popup menu.
   *
   * @return		the generated menu
   */
  @Override
  protected JPopupMenu getPopupMenu() {
    GenericObjectEditorPopupMenu 	menu;
    JMenuItem				item;

    menu = new GenericObjectEditorPopupMenu(m_Editor, m_Self);
    menu.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	setCurrent(m_Editor.getValue());
	notifyChangeListeners(new ChangeEvent(m_Self));
      }
    });

    if (isEditable())
      item = new JMenuItem("Edit...", GUIHelper.getIcon("properties.gif"));
    else
      item = new JMenuItem("Show...", GUIHelper.getIcon("properties.gif"));
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	choose();
      }
    });
    menu.insert(new JPopupMenu.Separator(), 0);
    menu.insert(item, 0);

    if (isEditable()) {
      menu.addSeparator();
      Favorites.getSingleton().customizePopupMenu(
	  menu,
	  m_Editor.getClassType(),
	  getCurrent(),
	  new FavoriteSelectionListener() {
	    public void favoriteSelected(FavoriteSelectionEvent e) {
	      setCurrent(e.getFavorite().getObject());
	      notifyChangeListeners(new ChangeEvent(m_Self));
	    }
	  });

      m_History.customizePopupMenu(
	  menu,
	  getCurrent(),
	  new HistorySelectionListener() {
	    public void historySelected(HistorySelectionEvent e) {
	      setCurrent(e.getHistoryItem());
	      notifyChangeListeners(new ChangeEvent(m_Self));
	    }
	  });
    }

    // customized menu?
    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(this, menu);

    return menu;
  }
}
