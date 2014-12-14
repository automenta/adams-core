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
 * AbstractHistoryPanel.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import gnu.trove.list.array.TIntArrayList;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;

/**
 * Abstract ancestor for panels that store a history of objects, e.g., results
 * of experiment runs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8488 $
 * @param <T> the type of objects that the history stores
 */
public abstract class AbstractNamedHistoryPanel<T>
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -6028207793460507677L;

  /**
   * Interface for classes that want to customize the popup menu for the
   * entries.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 8488 $
   */
  public static interface PopupCustomizer {

    /**
     * Gets called before the popup for the entries is displayed.
     *
     * @param entries	the selected entries
     * @param menu	the menu so far
     */
    public void customizePopup(String[] entries, JPopupMenu menu);
  }

  /**
   * Event object that gets sent whenever a history entry gets selected.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 8488 $
   */
  public static class HistoryEntrySelectionEvent
    extends EventObject {

    /** for serialization. */
    private static final long serialVersionUID = 6402304240357916438L;

    /** the names of the entries. */
    protected String[] m_Names;

    /**
     * Initializes the even.
     *
     * @param source	the panel that triggered the event
     * @param names	the names of the entries
     */
    public HistoryEntrySelectionEvent(AbstractNamedHistoryPanel source, String[] names) {
      super(source);

      m_Names = names.clone();
    }

    /**
     * Returns the panel that triggered the event.
     *
     * @return		the panel
     */
    public AbstractNamedHistoryPanel getPanel() {
      return (AbstractNamedHistoryPanel) getSource();
    }

    /**
     * Returns the names of the entries.
     *
     * @return		the names
     */
    public String[] getNames() {
      return m_Names;
    }
  }

  /**
   * Interface for listeners that want to get notified whenever a different
   * history entry gets selected.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 8488 $
   */
  public static interface HistoryEntrySelectionListener {

    /**
     * Gets called whenever a history entry gets selected.
     *
     * @param e		the event
     */
    public void historyEntrySelected(HistoryEntrySelectionEvent e);
  }

  /**
   * Interface for history panels that allow the content to be displayed
   * in separate frames.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 8488 $
   * @param <T> the type of content to display
   */
  public static interface FrameDisplaySupporter<T> {

    /**
     * Checks whether a frame is available fro this entry.
     *
     * @param name	the name of the entry to check
     * @return		true if a frame is already available
     */
    public boolean hasFrame(String name);

    /**
     * Creates a new frame for the entry.
     *
     * @param name	the name of the entry to create a frame for
     * @return		the frame
     */
    public AbstractHistoryEntryFrame<T> newFrame(String name);

    /**
     * Returns the frame associated with the entry.
     *
     * @param name	the name of the entry to retrieve
     * @return		the frame or null if not found
     */
    public AbstractHistoryEntryFrame<T> getFrame(String name);

    /**
     * Displays the entry in a new frame. If a frame is already avao;an;e for
     * the entry, then this one will be activated.
     *
     * @param name	the name of the entry to open or activate
     */
    public void showFrame(String name);

    /**
     * Removes the frame from the list. This method should be called when
     * the frame gets closed.
     *
     * @param name	the name of the entry this frame is associated with
     */
    public void removeFrame(String name);
  }

  /**
   * A specialized frame class for displaying a history entries.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 8488 $
   * @param <T> the type of
   */
  public static abstract class AbstractHistoryEntryFrame<T extends Object>
    extends BaseFrame {

    /** for serialization. */
    private static final long serialVersionUID = 2552148773749071235L;

    /** the history panel that opened this frame. */
    protected AbstractNamedHistoryPanel<T> m_EntryOwner;

    /** the history entry name . */
    protected String m_EntryName;

    /**
     * Initializes the frame.
     *
     * @param owner	the owning history panel
     * @param name	the name of the history entry
     */
    public AbstractHistoryEntryFrame(AbstractNamedHistoryPanel<T> owner, String name) {
      super("Result - " + name);

      if (!(owner instanceof FrameDisplaySupporter))
	throw new IllegalArgumentException(
	    "History panel must implement " + FrameDisplaySupporter.class.getName());

      addWindowListener(new WindowAdapter() {
	@Override
	public void windowClosing(WindowEvent e) {
	  ((FrameDisplaySupporter) getEntryOwner()).removeFrame(getEntryName());
	}
      });

      m_EntryOwner = owner;
      m_EntryName  = name;

      updateEntry();
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      setDefaultCloseOperation(BaseFrame.DISPOSE_ON_CLOSE);
      setSize(getDefaultSize());
    }

    /**
     * Returns the default size of the frame.
     *
     * @return		the default size
     */
    protected Dimension getDefaultSize() {
      return new Dimension(640, 480);
    }

    /**
     * Returns the owning history panel.
     *
     * @return		the panel
     */
    public AbstractNamedHistoryPanel<T> getEntryOwner() {
      return m_EntryOwner;
    }

    /**
     * Returns the name of the history entry.
     *
     * @return		the name
     */
    public String getEntryName() {
      return m_EntryName;
    }

    /**
     * Updates the entry, i.e., re-displays it.
     */
    public abstract void updateEntry();
  }

  /** the JList listing the history entries. */
  protected JList m_List;

  /** the underlying list model. */
  protected DefaultListModel m_ListModel;

  /** the filtered model. */
  protected DefaultListModel m_ListModelFiltered;
  
  /** stores the actual objects (name &lt;-&gt; object relation). */
  protected Hashtable<String,T> m_Entries;

  /** stores the optional payloads (name &lt;-&gt; object relation). */
  protected Hashtable<String,Object> m_Payloads;

  /** an optional customizer for the popup on the JList. */
  protected PopupCustomizer m_PopupCustomizer;

  /** the history entry selection listeners. */
  protected HashSet<HistoryEntrySelectionListener> m_HistoryEntrySelectionListeners;

  /** the panel for searching the entry names. */
  protected SearchPanel m_PanelSearch;
  
  /** the current search term. */
  protected String m_SearchString;
  
  /** whether the current search is using regular expressions. */
  protected boolean m_SearchRegexp;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Entries                        = new Hashtable<>();
    m_Payloads                       = new Hashtable<>();
    m_ListModel                      = new DefaultListModel();
    m_ListModelFiltered              = null;
    m_PopupCustomizer                = null;
    m_SearchString                   = null;
    m_SearchRegexp                   = false;
    m_HistoryEntrySelectionListeners = new HashSet<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_List = new JList(m_ListModel);
    m_List.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_List.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  showPopup(e);
	  e.consume();
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    m_List.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	// update entry
	String name = getSelectedEntry();
	if (name != null)
	  updateEntry(name);
	// notify listeners
	notifyHistoryEntrySelectionListeners(
	    new HistoryEntrySelectionEvent(
		AbstractNamedHistoryPanel.this, getSelectedEntries()));
      }
    });
    add(new BaseScrollPane(m_List), BorderLayout.CENTER);
    
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false, null, true, "");
    m_PanelSearch.setVisible(false);
    m_PanelSearch.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
	if (!getAllowSearch())
	  return;
	m_SearchString = e.getParameters().getSearchString();
	if (m_SearchString.isEmpty())
	  m_SearchString = null;
	m_SearchRegexp = e.getParameters().isRegExp();
	updateSearch();
      }
    });
    add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * Generates and pops up the right-click menu on the JList.
   *
   * @param e		the event that triggered the popup
   * @see		#createPopup(MouseEvent)
   */
  protected void showPopup(MouseEvent e) {
    JPopupMenu	menu;

    menu = createPopup(e);

    // customizer available?
    if (m_PopupCustomizer != null)
      m_PopupCustomizer.customizePopup(getSelectedEntries(), menu);

    menu.show(this, e.getX(), e.getY());
  }

  /**
   * Generates the right-click menu for the JList.
   * <p/>
   * Derived classes should override this method instead of making use
   * of the PopupCustomizer.
   *
   * @param e		the event that triggered the popup
   * @return		the generated menu
   * @see		#showPopup(MouseEvent)
   */
  protected JPopupMenu createPopup(MouseEvent e) {
    JPopupMenu	result;
    JMenuItem	menuitem;
    final int[]	indices;

    result  = new JPopupMenu();
    indices = getSelectedIndices();

    // show
    menuitem = new JMenuItem("Show");
    menuitem.setEnabled(indices.length == 1);
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	updateEntry(getEntryName(indices[0]));
      }
    });
    result.add(menuitem);

    // show in new frame
    if (this instanceof FrameDisplaySupporter) {
      menuitem = new JMenuItem("Show in separate frame");
      menuitem.setEnabled(indices.length == 1);
      menuitem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ((FrameDisplaySupporter) AbstractNamedHistoryPanel.this).showFrame(
              getEntryName(indices[0]));
        }
      });
      result.add(menuitem);
    }

    result.addSeparator();

    // remove
    menuitem = new JMenuItem();
    if (indices.length > 1)
      menuitem.setText("Remove " + indices.length + " entries");
    else
      menuitem.setText("Remove entry");
    menuitem.setEnabled(indices.length >= 1);
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	for (int i = indices.length - 1; i >= 0; i--)
	  removeEntry(getEntryName(indices[0]));
      }
    });
    result.add(menuitem);

    // remove all
    menuitem = new JMenuItem("Remove all");
    menuitem.setEnabled(m_Entries.size() > 0);
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	clear();
      }
    });
    result.add(menuitem);

    return result;
  }

  /**
   * Displays the specified entry.
   *
   * @param name	the name of the entry, can be null to empty display
   */
  protected abstract void updateEntry(String name);

  /**
   * Updates the search, filters the entries if necessary.
   */
  protected void updateSearch() {
    int		i;
    String	entry;
    boolean	match;
    String	search;
    
    if (m_SearchString == null) {
      m_ListModelFiltered = null;
      m_List.setModel(m_ListModel);
    }
    else {
      m_ListModelFiltered = new DefaultListModel();
      if (m_SearchRegexp)
	search = m_SearchString;
      else
	search = m_SearchString.toLowerCase();
      for (i = 0; i < m_ListModel.size(); i++) {
	entry = (String) m_ListModel.get(i);
	if (m_SearchRegexp)
	  match = entry.matches(search);
	else
	  match = (entry.toLowerCase().indexOf(search) > -1);
	if (match)
	  m_ListModelFiltered.addElement(entry);
      }
      m_List.setModel(m_ListModelFiltered);
    }
    updateEntry(null);
  }

  /**
   * Checks whether a search term has been entered by the user.
   * 
   * @return		true if search term was entered
   */
  public boolean hasSearch() {
    return (m_SearchString != null);
  }
  
  /**
   * Returns the list model.
   * 
   * @return		the list model
   */
  protected DefaultListModel getListModel() {
    if (m_ListModelFiltered != null)
      return m_ListModelFiltered;
    else
      return m_ListModel;
  }
  
  /**
   * Removes all entries and payloads.
   */
  public void clear() {
    m_Entries.clear();
    m_Payloads.clear();
    m_ListModel.clear();
    m_ListModelFiltered = null;
    updateEntry(null);
  }

  /**
   * Returns the number of entries in the list.
   *
   * @return		the number of entries
   */
  public int count() {
    return getListModel().size();
  }

  /**
   * Returns the all the currently stored entries.
   *
   * @return		the names of the entries
   */
  public Enumeration<String> entries() {
    return m_Entries.keys();
  }

  /**
   * Checks whether the given named entry exists.
   *
   * @param name	the name to look for
   * @return 		true if the entry exists
   */
  public boolean hasEntry(String name) {
    return m_Entries.containsKey(name);
  }

  /**
   * Checks whether there is a payload available for the name.
   *
   * @param name	the name to check for payload
   * @return		true if entry exists and a payload is available
   */
  public boolean hasPayload(String name) {
    return m_Payloads.containsKey(name);
  }

  /**
   * Ensures that the name is unique, by appending an index.
   * 
   * @param name	the name to make unique
   * @return		the unique name
   */
  public synchronized String newEntryName(String name) {
    String	result;
    int		count;
    
    result = name;
    count  = 1;
    while (hasEntry(result)) {
      count++;
      result = name + " (" + count + ")";
    }
    
    return result;
  }
  
  /**
   * Returns the index of the entry.
   *
   * @param name	the name to look for
   * @return		the index or -1 if not found
   */
  public int indexOfEntry(String name) {
    int		result;
    int		i;

    result = -1;

    if (hasEntry(name)) {
      for (i = 0; i < getListModel().size(); i++) {
	if (name.equals(getListModel().get(i))) {
	  result = i;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Returns the entry with the specified name.
   *
   * @param name	the name of the entry
   * @return		the object or null if not found
   */
  public T getEntry(String name) {
    return m_Entries.get(name);
  }

  /**
   * Returns the payload with the specified name.
   *
   * @param name	the name of the entry
   * @return		the object or null if not found or no payload available
   */
  public Object getPayload(String name) {
    return m_Payloads.get(name);
  }

  /**
   * Returns the entry at the specified index.
   *
   * @param index	the index of the entry
   * @return		the object or null if not found
   */
  public T getEntry(int index) {
    return m_Entries.get(getListModel().get(index));
  }

  /**
   * Returns the payload at the specified index.
   *
   * @param index	the index of the payload
   * @return		the object or null if not found
   */
  public Object getPayload(int index) {
    return m_Payloads.get(getListModel().get(index));
  }

  /**
   * Returns the name of the entry at the specified index.
   *
   * @param index	the index of the entry
   * @return		the name
   */
  public String getEntryName(int index) {
    return (String) getListModel().get(index);
  }

  /**
   * Adds the entry under the specified name.
   *
   * @param name	the name of the entry
   * @param entry	the object to store
   * @return		the entry previously stored under the same name or
   * 			null if it is a new entry
   */
  public T addEntry(String name, T entry) {
    return addEntry(name, entry, null);
  }

  /**
   * Adds the entry under the specified name.
   *
   * @param name	the name of the entry
   * @param entry	the object to store
   * @param payload	the payload to attach, can be null
   * @return		the entry previously stored under the same name or
   * 			null if it is a new entry
   */
  public T addEntry(String name, T entry, Object payload) {
    T		result;

    result = m_Entries.put(name, entry);
    if (result == null)
      m_ListModel.addElement(name);
    if (payload != null)
      setPayload(name, payload);

    if (hasSearch())
      updateSearch();
    
    return result;
  }

  /**
   * Set the payload for the specified entry.
   *
   * @param name	the name of the entry
   * @param payload	the payload object to store
   */
  public void setPayload(String name, Object payload) {
    if (hasEntry(name))
      m_Payloads.put(name, payload);
  }

  /**
   * Inserts the entry under the name at the specified location. Any previously
   * existing entry with the same name will be removed.
   *
   * @param name	the name of the entry
   * @param entry	the object to store
   * @param index	the index to store the entry at
   */
  public void insertEntryAt(String name, T entry, int index) {
    insertEntryAt(name, entry, null, index);
  }

  /**
   * Inserts the entry under the name at the specified location. Any previously
   * existing entry with the same name will be removed.
   *
   * @param name	the name of the entry
   * @param entry	the object to store
   * @param payload	the payload to attach, can be null
   * @param index	the index to store the entry at
   */
  public void insertEntryAt(String name, T entry, Object payload, int index) {
    int		oldIndex;

    oldIndex = indexOfEntry(name);
    m_Entries.put(name, entry);
    m_ListModel.insertElementAt(name, index);

    // remove old entry?
    if (oldIndex > -1) {
      if (oldIndex < index)
	m_ListModel.remove(oldIndex);
      else
	m_ListModel.remove(oldIndex - 1);
    }

    if (payload != null)
      setPayload(name, payload);

    if (hasSearch())
      updateSearch();
  }

  /**
   * Removes the specified entry.
   *
   * @param name	the name of the entry
   * @return		the entry that was stored under this name or null if
   * 			no entry was stored with this name
   */
  public T removeEntry(String name) {
    int		index;

    index = indexOfEntry(name);
    if (index > -1)
      m_ListModel.remove(index);
    m_Payloads.remove(name);
    updateEntry(null);

    if (hasSearch())
      updateSearch();
    
    return m_Entries.remove(name);
  }

  /**
   * Removes the specified payload.
   *
   * @param name	the name of the entry to remove the payload for
   * @return		the payload object that was stored under this name or
   * 			null if no payload was stored with this name
   */
  public Object removePayload(String name) {
    return m_Payloads.remove(name);
  }

  /**
   * Sets the index to be displayed as selected.
   *
   * @param value	the index to select
   */
  public void setSelectedIndex(int value) {
    m_List.setSelectedIndex(value);
  }

  /**
   * Attempts to rename the entry.
   * 
   * @param oldName	the current name of the entry
   * @param newName	the new name for the entry
   * @return		null if successful, otherwise error message
   */
  public synchronized String renameEntry(String oldName, String newName) {
    int		index;
    
    if (!hasEntry(oldName))
      return "'" + oldName + "' does not exist!";
    if (hasEntry(newName))
      return "'" + newName + "' already exists!";
    
    index = indexOfEntry(oldName);
    m_Entries.put(newName, m_Entries.remove(oldName));
    m_ListModel.set(index, newName);

    if (hasSearch())
      updateSearch();

    return null;
  }
  
  /**
   * Returns the currently selected index, the first if several selected.
   *
   * @return		the selected index, -1 if none selected
   */
  public int getSelectedIndex() {
    return m_List.getSelectedIndex();
  }

  /**
   * Sets the indices to be displayed as selected.
   *
   * @param value	the indices to select
   */
  public void setSelectedIndices(int[] value) {
    m_List.setSelectedIndices(value);
  }

  /**
   * Returns the currently selected indices.
   *
   * @return		the selected indices
   */
  public int[] getSelectedIndices() {
    return m_List.getSelectedIndices();
  }

  /**
   * Sets the entry to be displayed as selected.
   *
   * @param value	the entry name
   */
  public void setSelectedEntry(String value) {
    setSelectedEntries(new String[]{value});
  }

  /**
   * Returns the currently selected entry, the first if several selected.
   *
   * @return		the selected entry, null if none selected
   */
  public String getSelectedEntry() {
    String[]	entries;

    entries = getSelectedEntries();
    if (entries.length > 0)
      return entries[0];
    else
      return null;
  }

  /**
   * Sets the entries to be displayed as selected.
   *
   * @param value	the entry names
   */
  public void setSelectedEntries(String[] value) {
    TIntArrayList	list;
    int			index;
    int			i;

    list = new TIntArrayList();
    for (i = 0; i < value.length; i++) {
      index = indexOfEntry(value[i]);
      if (index > -1)
	list.add(index);
    }

    setSelectedIndices(list.toArray());
  }

  /**
   * Returns the currently selected entries.
   *
   * @return		the selected entries
   */
  public String[] getSelectedEntries() {
    String[]	result;
    int[]	indices;
    int		i;

    indices = getSelectedIndices();
    result  = new String[indices.length];
    for (i = 0; i < indices.length; i++)
      result[i] = (String) getListModel().get(indices[i]);

    return result;
  }

  /**
   * Sets the popup customizer to use.
   *
   * @param value	the customizer, use null to turn off
   */
  public void setPopupCustomizer(PopupCustomizer value) {
    m_PopupCustomizer = value;
  }

  /**
   * Returns the currently set popup customizer.
   *
   * @return		the customizer, can be null if none set
   */
  public PopupCustomizer getPopupCustomizer() {
    return m_PopupCustomizer;
  }

  /**
   * Adds a listener to the internal list of listeners that get notified when
   * a history entry gets selected.
   *
   * @param l		the listener to add
   */
  public void addHistoryEntrySelectionListener(HistoryEntrySelectionListener l) {
    m_HistoryEntrySelectionListeners.add(l);
  }

  /**
   * Removes a listener from the internal list of listeners that get notified when
   * a history entry gets selected.
   *
   * @param l		the listener to remove
   */
  public void removeHistoryEntrySelectionListener(HistoryEntrySelectionListener l) {
    m_HistoryEntrySelectionListeners.remove(l);
  }

  /**
   * Notifies all listeners with the specified event.
   *
   * @param e		the event to send to the listeners
   */
  protected void notifyHistoryEntrySelectionListeners(HistoryEntrySelectionEvent e) {
    for (HistoryEntrySelectionListener l: m_HistoryEntrySelectionListeners)
      l.historyEntrySelected(e);
  }
  
  /**
   * Sets whether the entry list is searchable.
   * 
   * @param value	true if to make the list searchable
   */
  public void setAllowSearch(boolean value) {
    m_PanelSearch.setVisible(value);
  }
  
  /**
   * Returns whether the entry list is searchable.
   * 
   * @return		true if list is searchable
   */
  public boolean getAllowSearch() {
    return m_PanelSearch.isVisible();
  }

  /**
   * Returns the preferred size for this component.
   * 
   * @return		the preferred size
   */
  @Override
  public Dimension getPreferredSize() {
    Dimension	result;
    Dimension	list;
    Dimension	search;
    
    result = super.getPreferredSize();
    
    list = m_List.getPreferredSize();
    if (list.width > result.width)
      result.width = list.width;
    
    if (getAllowSearch()) {
      search = m_PanelSearch.getPreferredSize();
      if (search.width > result.width)
	result.width = search.width;
    }
    
    return result;
  }
}
