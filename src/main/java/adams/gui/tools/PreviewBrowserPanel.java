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
 * PreviewBrowserPanel.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.env.PreviewBrowserPanelDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.ChildWindow;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SearchableBaseList;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.tools.previewbrowser.AbstractArchiveHandler;
import adams.gui.tools.previewbrowser.AbstractContentHandler;
import adams.gui.tools.previewbrowser.CreatingPreviewPanel;
import adams.gui.tools.previewbrowser.MultipleFileContentHandler;
import adams.gui.tools.previewbrowser.NoPreviewAvailablePanel;
import adams.gui.tools.previewbrowser.PreviewPanel;

/**
 * Allows the browsing of files and previewing the content.
 * Also works for archives.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9868 $
 */
public class PreviewBrowserPanel
  extends BasePanel 
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 5284765971012530865L;

  /**
   * {@link Comparator} for {@link File} objects. Only uses the file name, not
   * the path, for comparison. Also, ignores the case.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 9868 $
   */
  public static class FileComparator 
    implements Comparator<File> {

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     * <p/>
     * Uses only the file name, not the path for comparison (lower case).
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     */
    @Override
    public int compare(File o1, File o2) {
      if ((o1 == null) && (o2 == null))
	return 0;
      if (o1 == null)
	return -1;
      if (o2 == null)
	return 1;
      return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    }
  }
  
  /** the name of the props file. */
  public final static String FILENAME = "PreviewBrowser.props";

  /** the file to store the recent directories. */
  public final static String SESSION_FILE = "PreviewBrowserSession.props";

  /** the prefix for the preferred content handler keys in the props file. */
  public final static String PREFIX_PREFERRED_CONTENT_HANDLER = "PreferredContentHandler-";

  /** the prefix for the preferred archive handler keys in the props file. */
  public final static String PREFIX_PREFERRED_ARCHIVE_HANDLER = "PreferredArchiveHandler-";

  /** the properties. */
  protected static Properties m_Properties;

  /** the split pane in use. */
  protected BaseSplitPane m_SplitPane;

  /** for selecting the directory to browse. */
  protected DirectoryChooserPanel m_PanelDir;

  /** the panel for browsing. */
  protected BasePanel m_PanelBrowsing;

  /** the panel with the local files. */
  protected BasePanel m_PanelLocalFiles;

  /** the list with the local files. */
  protected SearchableBaseList m_ListLocalFiles;

  /** the model for the local files. */
  protected DefaultListModel m_ModelLocalFiles;

  /** the search panel for the local files. */
  protected SearchPanel m_SearchLocalFiles;

  /** the panel with the archive files. */
  protected BasePanel m_PanelArchiveFiles;

  /** the list with the archive files. */
  protected SearchableBaseList m_ListArchiveFiles;

  /** the model for the archive files. */
  protected DefaultListModel m_ModelArchiveFiles;

  /** the search panel for the archive files. */
  protected SearchPanel m_SearchArchiveFiles;

  /** the panel for the content display. */
  protected BasePanel m_PanelContent;

  /** the panel for the view. */
  protected BasePanel m_PanelView;

  /** the panel with the content handlers. */
  protected BasePanel m_PanelContentHandlers;

  /** the combobox with the content handlers (if more than one available). */
  protected JComboBox m_ComboBoxContentHandlers;

  /** the model of the combobox. */
  protected DefaultComboBoxModel m_ModelContentHandlers;

  /** whether to ignore selections of the content handler combobox temporarily. */
  protected boolean m_IgnoreContentHandlerChanges;

  /** the panel with the archive handlers. */
  protected BasePanel m_PanelArchiveHandlers;

  /** the combobox with the archive handlers (if more than one available). */
  protected JComboBox m_ComboBoxArchiveHandlers;

  /** the model of the combobox. */
  protected DefaultComboBoxModel m_ModelArchiveHandlers;

  /** whether to ignore selections of the archive handler combobox temporarily. */
  protected boolean m_IgnoreArchiveHandlerChanges;

  /** the current archive handler. */
  protected AbstractArchiveHandler m_ArchiveHandler;

  /** the currently selected files. */
  protected File[] m_CurrentFiles;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuFileLoadRecent;
  
  /** the "show hidden files" menu item. */
  protected JMenuItem m_MenuItemShowHiddenFiles;
  
  /** the "show temp files" menu item. */
  protected JMenuItem m_MenuItemShowTempFiles;

  /** for generating the title of the dialog/frame. */
  protected TitleGenerator m_TitleGenerator;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ArchiveHandler              = null;
    m_IgnoreContentHandlerChanges = false;
    m_IgnoreArchiveHandlerChanges = false;
    m_CurrentFiles                = null;
    m_RecentFilesHandler          = null;
    m_TitleGenerator              = new TitleGenerator("Preview browser", false);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties		props;
    JPanel		panel;

    super.initGUI();

    props = getProperties();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setDividerLocation(props.getInteger("DividerLocation", 200));
    add(m_SplitPane, BorderLayout.CENTER);

    // browsing
    m_PanelBrowsing = new BasePanel(new GridLayout(2, 1));
    m_SplitPane.setLeftComponent(m_PanelBrowsing);

    m_PanelLocalFiles = new BasePanel(new BorderLayout(0, 5));
    m_PanelBrowsing.add(m_PanelLocalFiles);
    m_PanelLocalFiles.setBorder(BorderFactory.createTitledBorder("Files"));

    m_PanelDir = new DirectoryChooserPanel(props.getPath("InitialDir", "%h"));
    m_PanelDir.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	refreshLocalFiles();
	if (m_RecentFilesHandler != null)
	  m_RecentFilesHandler.addRecentItem(m_PanelDir.getCurrent());
      }
    });
    m_PanelLocalFiles.add(m_PanelDir, BorderLayout.NORTH);

    m_ModelLocalFiles = new DefaultListModel();
    m_ListLocalFiles  = new SearchableBaseList(m_ModelLocalFiles);
    m_ListLocalFiles.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (e.getValueIsAdjusting())
	  return;
	displayLocalFile();
      }
    });
    m_PanelLocalFiles.add(new BaseScrollPane(m_ListLocalFiles), BorderLayout.CENTER);

    m_SearchLocalFiles = new SearchPanel(LayoutType.VERTICAL, false);
    m_SearchLocalFiles.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	m_ListLocalFiles.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_SearchLocalFiles);
    m_PanelLocalFiles.add(panel, BorderLayout.SOUTH);

    m_PanelArchiveFiles = new BasePanel(new BorderLayout());
    m_PanelBrowsing.add(m_PanelArchiveFiles);
    m_PanelArchiveFiles.setVisible(false);
    m_PanelArchiveFiles.setBorder(BorderFactory.createTitledBorder("Archive"));

    m_ModelArchiveFiles = new DefaultListModel();
    m_ListArchiveFiles  = new SearchableBaseList(m_ModelArchiveFiles);
    m_ListArchiveFiles.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	if (e.getValueIsAdjusting())
	  return;
	displayArchiveContent();
      }
    });
    m_PanelArchiveFiles.add(new BaseScrollPane(m_ListArchiveFiles), BorderLayout.CENTER);

    m_SearchArchiveFiles = new SearchPanel(LayoutType.VERTICAL, false);
    m_SearchArchiveFiles.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	m_ListArchiveFiles.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_SearchArchiveFiles);
    m_PanelArchiveFiles.add(panel, BorderLayout.SOUTH);

    m_PanelArchiveHandlers = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelArchiveHandlers.setVisible(false);
    m_PanelArchiveFiles.add(m_PanelArchiveHandlers, BorderLayout.SOUTH);

    m_ModelArchiveHandlers    = new DefaultComboBoxModel();
    m_ComboBoxArchiveHandlers = new JComboBox(m_ModelArchiveHandlers);
    m_ComboBoxArchiveHandlers.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_IgnoreArchiveHandlerChanges)
	  return;
        updatePreferredArchiveHandler();
        displayArchiveContent();
      }
    });
    m_PanelArchiveHandlers.add(new JLabel("Preferred handler"));
    m_PanelArchiveHandlers.add(m_ComboBoxArchiveHandlers);

    // content
    m_PanelContent = new BasePanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PanelContent);

    m_PanelView = new BasePanel(new BorderLayout());
    m_PanelContent.add(m_PanelView, BorderLayout.CENTER);

    m_PanelContentHandlers = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelContentHandlers.setVisible(false);
    m_PanelContent.add(m_PanelContentHandlers, BorderLayout.SOUTH);

    m_ModelContentHandlers    = new DefaultComboBoxModel();
    m_ComboBoxContentHandlers = new JComboBox(m_ModelContentHandlers);
    m_ComboBoxContentHandlers.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	if (m_IgnoreContentHandlerChanges)
	  return;
        updatePreferredContentHandler();
        if (m_CurrentFiles != null)
          displayLocalContent(m_CurrentFiles, false);
      }
    });
    m_PanelContentHandlers.add(new JLabel("Preferred handler"));
    m_PanelContentHandlers.add(m_ComboBoxContentHandlers);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    refreshLocalFiles();
  }

  /**
   * Filters the files. Hidden/temp files may get removed.
   * 
   * @param files	the files to filter
   * @return		the filtered files
   */
  protected File[] filterFiles(File[] files) {
    ArrayList<File>	result;
    boolean		showHidden;
    boolean		showTemp;
    BaseRegExp		regExp;
    String[]		expList;
    String		exp;
    
    if (m_MenuItemShowHiddenFiles != null)
      showHidden = m_MenuItemShowHiddenFiles.isSelected();
    else
      showHidden = getProperties().getBoolean("ShowHiddenFiles", false);
    if (m_MenuItemShowTempFiles != null)
      showTemp = m_MenuItemShowTempFiles.isSelected();
    else
      showTemp = getProperties().getBoolean("ShowTempFiles", false);
    regExp = new BaseRegExp(BaseRegExp.MATCH_ALL);
    exp    = getProperties().getProperty("TempFiles", ".*\\.bak|.*\\.backup|.*~");
    if (exp.length() > 0) {
      expList = Utils.split(exp, '|');
      exp = "";
      for (String item: expList) {
	if (exp.length() > 0)
	  exp += "|";
	exp += item;
      }
      regExp = new BaseRegExp("^(" + exp + ")");
    }
    else {
      // no exclusion patterns
      showTemp = true;
    }

    if (showHidden && showTemp)
      return files;
    
    result = new ArrayList<>();
    for (File file: files) {
      if (!showTemp && regExp.isMatch(file.getName()))
	continue;
      if (!showHidden && file.isHidden())
	continue;
      result.add(file);
    }
    
    return result.toArray(new File[result.size()]);
  }
  
  /**
   * Refreshes the local file list.
   */
  protected synchronized void refreshLocalFiles() {
    File		dir;
    File[]		files;
    ArrayList<File>	fileList;

    dir   = m_PanelDir.getCurrent();
    files = filterFiles(dir.listFiles());
    fileList = new ArrayList<>();
    for (File file: files) {
      if (file.isDirectory())
	continue;
      fileList.add(file);
    }
    Collections.sort(fileList, new FileComparator());
    m_ListLocalFiles.clearSelection();
    m_ModelLocalFiles.clear();
    for (File file: fileList)
      m_ModelLocalFiles.addElement(file.getName());
    m_ListLocalFiles.search(m_ListLocalFiles.getSeachString(), m_ListLocalFiles.isRegExpSearch());

    m_ListArchiveFiles.clearSelection();
    m_ModelArchiveFiles.clear();
    m_PanelArchiveFiles.setVisible(false);
  }

  /**
   * Displays the given view in the content panel.
   *
   * @param panel	the view to display
   */
  protected void displayView(final JPanel panel) {
    if ((m_PanelView.getComponentCount() > 0) && (m_PanelView.getComponent(0) instanceof CleanUpHandler))
      ((CleanUpHandler) m_PanelView.getComponent(0)).cleanUp();
    m_PanelView.removeAll();
    m_PanelView.add(panel, BorderLayout.CENTER);
    m_SplitPane.validate();
  }

  /**
   * Displays "Creating view...".
   */
  protected void displayCreatingView() {
    displayView(new CreatingPreviewPanel());
  }

  /**
   * Displays a local file.
   */
  protected synchronized void displayLocalFile() {
    PlaceholderFile[]	localFiles;
    List<Class>		handlers;
    String[]		files;
    int			n;

    if (m_ListLocalFiles.getSelectedIndex() < 0)
      return;
    if (m_ListLocalFiles.getSelectedValues().length < 1)
      return;
    
    localFiles = new PlaceholderFile[m_ListLocalFiles.getSelectedValues().length];
    for (n = 0; n < localFiles.length; n++)
      localFiles[n] = new PlaceholderFile(m_PanelDir.getCurrent().getAbsolutePath() + File.separator + m_ListLocalFiles.getSelectedValues()[n]);

    if (AbstractArchiveHandler.hasHandler(localFiles[0])) {
      m_PanelArchiveFiles.setVisible(true);
      handlers = AbstractArchiveHandler.getHandlersForFile(localFiles[0]);
      // update combobox
      m_IgnoreArchiveHandlerChanges = true;
      m_ModelArchiveHandlers.removeAllElements();
      for (Class cls: handlers)
	m_ModelArchiveHandlers.addElement(cls.getName());
      m_PanelArchiveHandlers.setVisible(m_ModelArchiveHandlers.getSize() > 1);
      AbstractArchiveHandler preferred = getPreferredArchiveHandler(localFiles[0]);
      // set preferred one
      m_ComboBoxArchiveHandlers.setSelectedIndex(0);
      if (preferred != null) {
	for (int i = 0; i < handlers.size(); i++) {
	  if (preferred.getClass() == handlers.get(i)) {
	    m_ComboBoxArchiveHandlers.setSelectedIndex(i);
	    break;
	  }
	}
      }
      m_IgnoreArchiveHandlerChanges = false;
      try {
	Class cls = Class.forName((String) m_ComboBoxArchiveHandlers.getSelectedItem());
	m_ArchiveHandler = (AbstractArchiveHandler) cls.newInstance();
      }
      catch (Exception e) {
	m_ArchiveHandler = null;
	System.err.println("Failed to obtain archive handler for '" + localFiles[0] + "':");
	e.printStackTrace();
      }
    }
    else {
      m_ArchiveHandler = null;
    }

    m_ModelArchiveFiles.clear();
    m_PanelArchiveFiles.setVisible((m_ArchiveHandler != null));
    m_PanelView.removeAll();
    if (m_ArchiveHandler == null) {
      displayLocalContent(localFiles, false);
    }
    else {
      m_CurrentFiles = null;
      m_ArchiveHandler.setArchive(localFiles[0]);
      files = m_ArchiveHandler.getFiles();
      for (String file: files)
	m_ModelArchiveFiles.addElement(file);
    }
    m_ListArchiveFiles.search(m_ListArchiveFiles.getSeachString(), m_ListArchiveFiles.isRegExpSearch());
  }

  /**
   * Creates a preview for the files.
   * 
   * @param localFiles	the files to create the preview for
   * @return		the preview
   */
  protected JPanel createPreview(final File[] localFiles) {
    JPanel 			result;
    List<Class> 		handlers;
    AbstractContentHandler 	preferred;
    int				i;
    AbstractContentHandler 	contentHandler;
    Class 			cls;

    result = new NoPreviewAvailablePanel();
    if (AbstractContentHandler.hasHandler(localFiles[0])) {
      handlers = AbstractContentHandler.getHandlersForFile(localFiles[0]);
      // update combobox
      m_IgnoreContentHandlerChanges = true;
      m_ModelContentHandlers.removeAllElements();
      for (Class handler: handlers)
	m_ModelContentHandlers.addElement(handler.getName());
      m_PanelContentHandlers.setVisible(m_ModelContentHandlers.getSize() > 1);
      preferred = getPreferredContentHandler(localFiles[0]);
      // set preferred one
      m_ComboBoxContentHandlers.setSelectedIndex(0);
      if (preferred != null) {
	for (i = 0; i < handlers.size(); i++) {
	  if (preferred.getClass() == handlers.get(i)) {
	    m_ComboBoxContentHandlers.setSelectedIndex(i);
	    break;
	  }
	}
      }
      m_IgnoreContentHandlerChanges = false;
      // get preferred handler
      try {
	if (m_ComboBoxContentHandlers.getSelectedIndex() != -1)
	  cls = Class.forName((String) m_ComboBoxContentHandlers.getSelectedItem());
	else if (m_ComboBoxContentHandlers.getItemCount() > 0)
	  cls = Class.forName((String) m_ComboBoxContentHandlers.getItemAt(0));
	else
	  return null;
	contentHandler = (AbstractContentHandler) cls.newInstance();
	if (contentHandler instanceof MultipleFileContentHandler)
	  result = ((MultipleFileContentHandler) contentHandler).getPreview(localFiles);
	else
	  result = contentHandler.getPreview(localFiles[0]);
      }
      catch (Exception e) {
	contentHandler = null;
	System.err.println("Failed to obtain content handler for '" + Utils.arrayToString(localFiles) + "':");
	e.printStackTrace();
      }
    }
    
    return result;
  }
  
  /**
   * Displays the content from the specified files.
   *
   * @param localFiles	the files to display
   * @param wait	wait for worker thread to finish
   */
  protected void displayLocalContent(final File[] localFiles, boolean wait) {
    SwingWorker 	worker;

    if (localFiles.length < 1)
      return;
    
    m_CurrentFiles = localFiles;

    // notify user
    displayCreatingView();

    if (!wait) {
      worker = new SwingWorker() {
	JPanel contentPanel;
	@Override
	protected Object doInBackground() throws Exception {
	  contentPanel = createPreview(localFiles);
	  return null;
	}
	@Override
	protected void done() {
	  if (contentPanel != null)
	    displayView(contentPanel);
	  super.done();
	}
      };
      worker.execute();
    }
    else {
      displayView(createPreview(localFiles));
    }
  }

  /**
   * Displays an archive file.
   */
  protected synchronized void displayArchiveContent() {
    Object[]	selFiles;
    File[]	tmpFiles;
    int		i;

    if (m_ListArchiveFiles.getSelectedIndex() < 0)
      return;

    // notify user
    displayCreatingView();

    selFiles = (Object[]) m_ListArchiveFiles.getSelectedValues();
    tmpFiles = new File[selFiles.length];
    for (i = 0; i < selFiles.length; i++) {
      try {
	tmpFiles[i] = File.createTempFile("adams-pb-", "." + FileUtils.getExtension(selFiles[i].toString()), FileUtils.getTempDirectory());
	if (!m_ArchiveHandler.extract(selFiles[i].toString(), tmpFiles[i])) {
	  System.err.println("Failed to extract file '" + selFiles[i] + "'!");
	  return;
	}
      }
      catch (Exception e) {
	System.err.println("Failed to extract file '" + selFiles[i] + "':");
	e.printStackTrace();
      }
    }
    displayLocalContent(tmpFiles, false);
  }

  /**
   * Returns the preferrned content handler.
   *
   * @param file	the file to get the preferred handler for
   * @return		the preferred handler
   */
  public static AbstractContentHandler getPreferredContentHandler(File file) {
    AbstractContentHandler	result;
    Properties			props;
    String			ext;
    String			handler;

    result = null;

    ext = FileUtils.getExtension(file);
    if (ext == null)
      return result;
    ext = ext.toLowerCase();

    props = getProperties();
    if (props.hasKey(PREFIX_PREFERRED_CONTENT_HANDLER + ext)) {
      handler = props.getProperty(PREFIX_PREFERRED_CONTENT_HANDLER + ext);
      try {
	result = (AbstractContentHandler) Class.forName(handler).newInstance();
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate handler: " + handler);
	e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Updates the preferred handler.
   */
  protected void updatePreferredContentHandler() {
    String	ext;
    String	handler;
    Properties	props;
    String	filename;

    if (m_CurrentFiles == null)
      return;

    props = getProperties();
    for (File file: m_CurrentFiles) {
      ext = FileUtils.getExtension(file);
      if (ext == null)
	continue;
      ext = ext.toLowerCase();

      if (m_ComboBoxContentHandlers.getSelectedIndex() < 0)
	handler = (String) m_ComboBoxContentHandlers.getItemAt(0);
      else
	handler = (String) m_ComboBoxContentHandlers.getSelectedItem();

      // update props
      props.setProperty(PREFIX_PREFERRED_CONTENT_HANDLER + ext, handler);
    }

    // save props
    filename = Environment.getInstance().getHome() + File.separator + FILENAME;
    if (!props.save(filename))
      System.err.println("Failed to save properties to '" + filename + "'!");
  }

  /**
   * Returns the preferrned archive handler.
   *
   * @param file	the file to get the preferred handler for
   * @return		the preferred handler
   */
  public static AbstractArchiveHandler getPreferredArchiveHandler(File file) {
    AbstractArchiveHandler	result;
    Properties			props;
    String			ext;
    String			handler;

    result = null;

    ext = FileUtils.getExtension(file);
    if (ext == null)
      return result;
    ext = ext.toLowerCase();

    props = getProperties();
    if (props.hasKey(PREFIX_PREFERRED_ARCHIVE_HANDLER + ext)) {
      handler = props.getProperty(PREFIX_PREFERRED_ARCHIVE_HANDLER + ext);
      try {
	result = (AbstractArchiveHandler) Class.forName(handler).newInstance();
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate handler: " + handler);
	e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Updates the preferred handler.
   */
  protected void updatePreferredArchiveHandler() {
    String	ext;
    String	handler;
    Properties	props;
    String	filename;

    if (m_CurrentFiles == null)
      return;

    props = getProperties();
    for (File file: m_CurrentFiles) {
      ext = FileUtils.getExtension(file);
      if (ext == null)
	continue;
      ext = ext.toLowerCase();

      if (m_ComboBoxArchiveHandlers.getSelectedIndex() < 0)
	handler = (String) m_ComboBoxArchiveHandlers.getItemAt(0);
      else
	handler = (String) m_ComboBoxArchiveHandlers.getSelectedItem();

      // update props
      props.setProperty(PREFIX_PREFERRED_ARCHIVE_HANDLER + ext, handler);
    }

    // save props
    filename = Environment.getInstance().getCustomPropertiesFilename(PreviewBrowserPanelDefinition.KEY);
    if (!Environment.getInstance().write(PreviewBrowserPanelDefinition.KEY, props))
      System.err.println("Failed to save properties to '" + filename + "'!");
  }

  /**
   * Updates menu and title.
   */
  protected void update() {
    updateTitle();
    updateMenu();
  }
  
  /**
   * Returns the title generator in use.
   * 
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Updates the title.
   */
  protected void updateTitle() {
    if (!m_TitleGenerator.isEnabled())
      return;
    if (m_CurrentFiles.length == 0)
      setParentTitle(m_TitleGenerator.generate());
    else if (m_CurrentFiles.length == 1)
      setParentTitle(m_TitleGenerator.generate(m_CurrentFiles[0]));
    else
      setParentTitle(m_TitleGenerator.generate(m_CurrentFiles[0]) + " ...");
  }
  
  /**
   * Updates the menu.
   */
  protected void updateMenu() {
    // nothing at the moment
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Open...
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  m_PanelDir.choose();
	}
      });

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<>(
	  SESSION_FILE, getProperties().getInteger("MaxRecentDirs", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  open(new PlaceholderDirectory(e.getItem()));
	}
      });
      m_MenuFileLoadRecent = submenu;

      // File/Reload
      menuitem = new JMenuItem("Reload");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("F5"));
      menuitem.setIcon(GUIHelper.getIcon("refresh.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  reload();
	}
      });

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  closeParent();
	}
      });

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // View/Show hidden files
      menuitem = new JCheckBoxMenuItem("Show hidden files");
      menuitem.setSelected(getProperties().getBoolean("ShowHiddenFiles", false));
      menu.add(menuitem);
      menuitem.setMnemonic('h');
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  refreshLocalFiles();
	}
      });
      m_MenuItemShowHiddenFiles = menuitem;

      // View/Show temp files
      menuitem = new JCheckBoxMenuItem("Show temporary files");
      menuitem.setSelected(getProperties().getBoolean("ShowTempFiles", false));
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  refreshLocalFiles();
	}
      });
      m_MenuItemShowTempFiles = menuitem;

      // Window
      menu = new JMenu("Window");
      result.add(menu);
      menu.setMnemonic('W');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Window/New window
      menuitem = new JMenuItem("New window");
      menu.add(menuitem);
      menuitem.setMnemonic('N');
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  newWindow();
	}
      });

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Opens the specified directory.
   * 
   * @param dir		the directory to display
   */
  public void open(final PlaceholderDirectory dir) {
    Runnable	run;
    
    run = new Runnable() {
      @Override
      public void run() {
	m_PanelDir.setCurrent(dir);
	m_PanelDir.fireCurrentValueChanged();
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Displays the specified file.
   * 
   * @param file	the file to display
   */
  public void open(final PlaceholderFile file) {
    Runnable	run;
    
    run = new Runnable() {
      @Override
      public void run() {
	m_PanelDir.setCurrent(file.getParentFile());
	m_PanelDir.fireCurrentValueChanged();
      }
    };
    SwingUtilities.invokeLater(run);

    run = new Runnable() {
      @Override
      public void run() {
	m_ListLocalFiles.setSelectedValue(file.getName(), true);
      }
    };
    SwingUtilities.invokeLater(run);
    
    run = new Runnable() {
      @Override
      public void run() {
	displayLocalContent(new File[]{file}, true);
      }
    };
    SwingUtilities.invokeLater(run);
  }
  
  /**
   * Reloads the directory and content.
   */
  public void reload() {
    Object[]	selected;
    
    selected = m_ListLocalFiles.getSelectedValues();
    open(new PlaceholderDirectory(m_PanelDir.getCurrent()));
    if (selected.length > 0)
      m_ListLocalFiles.setSelectedValue(selected[0], true);
  }
  
  /**
   * Clears the preview.
   */
  public void clear() {
    m_CurrentFiles = new File[0];
    displayView(new NoPreviewAvailablePanel());
  }

  /**
   * Displays a new preview window/frame.
   *
   * @return		the new panel
   */
  public PreviewBrowserPanel newWindow() {
    PreviewBrowserPanel result;
    ChildFrame 		oldFrame;
    ChildFrame 		newFrame;
    ChildWindow 	oldWindow;
    ChildWindow 	newWindow;

    result    = null;
    oldFrame = (ChildFrame) GUIHelper.getParent(this, ChildFrame.class);
    if (oldFrame != null) {
      newFrame = oldFrame.getNewWindow();
      newFrame.setVisible(true);
      result  = (PreviewBrowserPanel) newFrame.getContentPane().getComponent(0);
    }
    else {
      oldWindow = (ChildWindow) GUIHelper.getParent(this, ChildWindow.class);
      if (oldWindow != null) {
	newWindow = oldWindow.getNewWindow();
	newWindow.setVisible(true);
	result  = (PreviewBrowserPanel) newWindow.getContentPane().getComponent(0);
      }
    }

    // use same directory
    if (result != null)
      result.open(new PlaceholderDirectory(m_PanelDir.getCurrent()));

    return result;
  }
  
  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return (m_CurrentFiles != null) && (m_CurrentFiles.length > 0);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object	result;
    File	file;

    result = null;
    file   = null;
    if ((m_CurrentFiles != null) && (m_CurrentFiles.length > 0))
      file = m_CurrentFiles[0];

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (file != null) {
	result = new PlaceholderFile(file);
      }
    }
    else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if ((m_PanelView.getComponentCount() > 0) && (m_PanelView.getComponent(0) instanceof PreviewPanel))
	result = ((PreviewPanel) m_PanelView.getComponent(0)).getContent();
      else
	result = m_PanelView;
    }

    return result;
  }
  
  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(PreviewBrowserPanelDefinition.KEY);

    return m_Properties;
  }
  
  /**
   * Sets whether the browsing panel is hidden or not.
   * 
   * @param value	true to enabled browsing, false to hide panel
   */
  public void setBrowsingEnabled(boolean value) {
    m_SplitPane.setLeftComponentHidden(!value);
  }
  
  /**
   * Returns whether the browsing panel is hidden or not.
   * 
   * @return		true if browsing enabled
   */
  public boolean isBrowsingEnabled() {
    return !m_SplitPane.isLeftComponentHidden();
  }
}
