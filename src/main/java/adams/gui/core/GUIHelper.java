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
 * GUIHelper.java
 * Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import adams.core.ClassLocator;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.management.OS;
import adams.core.net.HtmlUtils;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.GUIHelperDefinition;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.Child;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextDialog;
import adams.gui.dialog.TextPanel;

/**
 * A little helper class for GUI related stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10222 $
 */
public class GUIHelper {

  /** the name of the props file. */
  public final static String FILENAME = "GUIHelper.props";

  /** the empty icon name. */
  public final static String EMPTY_ICON = "empty.gif";

  /** the mnemonic character indicator. */
  public final static char MNEMONIC_INDICATOR = '_';

  /** the approve option. */
  public final static int APPROVE_OPTION = ApprovalDialog.APPROVE_OPTION;

  /** the discard option. */
  public final static int DISCARD_OPTION = ApprovalDialog.DISCARD_OPTION;

  /** the cancel option. */
  public final static int CANCEL_OPTION = ApprovalDialog.CANCEL_OPTION;

  /** the properties. */
  protected static Properties m_Properties;

  /** the screen boundaries. */
  protected static Rectangle m_ScreenBoundaries;

  /**
   * Initializes the properties if necessary.
   */
  protected static synchronized void initializeProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(GUIHelperDefinition.KEY);
  }

  /**
   * Checks whether the image is available.
   *
   * @param name	the name of the image (filename without path but with
   * 			extension)
   * @return		true if image exists
   */
  public static boolean hasImageFile(String name) {
    return (getImageFilename(name) != null);
  }

  /**
   * Adds the path of the images directory to the name of the image.
   *
   * @param name	the name of the image to add the path to
   * @return		the full path of the image
   */
  public static String getImageFilename(String name) {
    String	result;
    String[]	dirs;
    int		i;
    URL		url;

    result = null;

    dirs = getString("ImagesDirectory", "adams/gui/images/").split(",");
    for (i = 0; i < dirs.length; i++) {
      if (!dirs[i].endsWith("/"))
	dirs[i] += "/";
      try {
	url = ClassLoader.getSystemClassLoader().getResource(dirs[i] + name);
	if (url != null) {
	  result = dirs[i] + name;
	  break;
	}
      }
      catch (Exception e) {
	// ignored
      }
    }

    return result;
  }

  /**
   * Returns an ImageIcon for the given class.
   *
   * @param cls		the class to get the icon for (gif, png or jpg)
   * @return		the ImageIcon or null if none found
   */
  public static ImageIcon getIcon(Class cls) {
    if (hasImageFile(cls.getName() + ".gif"))
      return getIcon(cls.getName() + ".gif");
    else if (hasImageFile(cls.getName() + ".png"))
      return getIcon(cls.getName() + ".png");
    else if (hasImageFile(cls.getName() + ".jpg"))
      return getIcon(cls.getName() + ".jpg");
    else
      return null;
  }

  /**
   * Returns an ImageIcon from the given name.
   *
   * @param name	the filename without path
   * @return		the ImageIcon or null if not available
   */
  public static ImageIcon getIcon(String name) {
    String	filename;

    filename = getImageFilename(name);
    if (filename != null)
      return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(filename));
    else
      return null;
  }

  /**
   * Returns an ImageIcon from the given name.
   *
   * @param filename	the filename
   * @return		the ImageIcon or null if not available
   */
  public static ImageIcon getExternalIcon(String filename) {
    ImageIcon	result;

    try {
      result = new ImageIcon(ClassLoader.getSystemClassLoader().getResource(filename));
    }
    catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Returns the ImageIcon for the empty icon.
   *
   * @return		the ImageIcon
   */
  public static ImageIcon getEmptyIcon() {
    return getIcon(EMPTY_ICON);
  }

  /**
   * Returns an ImageIcon of the logo (large image).
   *
   * @return		the logo or null if none available
   */
  public static ImageIcon getLogoImage() {
    ImageIcon	result;
    String	filename;

    result = null;

    filename = getString("LogoImage", "");
    if (filename.length() != 0)
      result = getIcon(filename);

    return result;
  }

  /**
   * Returns an ImageIcon of the logo (icon sized image).
   *
   * @return		the logo or null if none available
   */
  public static ImageIcon getLogoIcon() {
    ImageIcon	result;
    String	filename;

    result = null;

    filename = getString("LogoIcon", "");
    if (filename.length() != 0)
      result = getIcon(filename);

    return result;
  }

  /**
   * Sets size stored in the props file.
   *
   * @param window	the frame to work on
   * @param c		the component to use for lookup in the props file;
   * 			null can be used to bypass the lookup in the props file
   */
  public static void setSize(Window window, Component c) {
    int		width;
    int		height;

    initializeProperties();

    // custom size?
    if (c != null) {
      if (m_Properties.getBoolean(c.getClass().getName() + ".pack", false))
	window.pack();

      width = m_Properties.getInteger(c.getClass().getName() + ".width", window.getWidth());
      if (width == -1)
	width = window.getGraphicsConfiguration().getBounds().width;

      height = m_Properties.getInteger(c.getClass().getName() + ".height", window.getHeight());
      if (height == -1)
	height = window.getGraphicsConfiguration().getBounds().height;

      window.setSize(width, height);
      window.validate();
    }
  }

  /**
   * Sets size and location stored in the props file.
   *
   * @param window	the frame to work on
   * @see		#setSizeAndLocation(Window, Component)
   */
  public static void setSizeAndLocation(Window window) {
    setSizeAndLocation(window, null);
  }

  /**
   * Determines the left location for a window.
   * 
   * @param window	the window to work on
   * @param left	the left position, -1: centered, -2: right-justified
   */
  public static int calcLeftPosition(Window window, int left) {
    int		result;
    
    if (left == -1)
      result = (window.getGraphicsConfiguration().getBounds().width - window.getWidth()) / 2;
    else if (left == -2)
      result = window.getGraphicsConfiguration().getBounds().width;
    else
      result = left;
    if (result < 0)
      result = 0;
    
    return result;
  }

  /**
   * Determines the top location for a window.
   * 
   * @param window	the window to work on
   * @param top		the top position, -1: centered, -2: bottom-justified
   */
  public static int calcTopPosition(Window window, int top) {
    int		result;
    
    if (top == -1)
      result = (window.getGraphicsConfiguration().getBounds().height - window.getHeight()) / 2;
    else if (top == -2)
      result = window.getGraphicsConfiguration().getBounds().height;
    else
      result = top;
    if (result < 0)
      result = 0;
    
    return result;
  }
  
  /**
   * Sets size and location stored in the props file.
   *
   * @param window	the frame to work on
   * @param c		the component to use for lookup in the props file;
   * 			null can be used to bypass the lookup in the props file
   * 			(in that case, "top" and "left" are initialized with 0)
   * @see		#setSizeAndLocation(Window, int, int, Component)
   */
  public static void setSizeAndLocation(Window window, Component c) {
    int		left;
    int		top;

    initializeProperties();

    // determine size first
    setSize(window, c);

    // custom location
    if (c != null) {
      left = m_Properties.getInteger(c.getClass().getName() + ".left", window.getX());
      left = calcLeftPosition(window, left);
    }
    else {
      left = 0;
    }

    if (c != null) {
      top = m_Properties.getInteger(c.getClass().getName() + ".top", window.getY());
      top = calcTopPosition(window, top);
    }
    else {
      top = 0;
    }

    setSizeAndLocation(window, top, left, c);
  }

  /**
   * Sets size and location stored in the props file.
   * <p/>
   * Takes the following parameters from the props file into account as well:
   * MaxPercentHeight, ScreenBorder.Bottom
   *
   * @param window	the frame to work on
   * @param top		the y position
   * @param left	the x position
   * @see		#setSizeAndLocation(Window, int, int, Component)
   */
  public static void setSizeAndLocation(Window window, int top, int left) {
    setSizeAndLocation(window, top, left, null);
  }

  /**
   * Sets size and location stored in the props file.
   * <p/>
   * Takes the following parameters from the props file into account as well:
   * MaxPercentHeight, ScreenBorder.Bottom
   *
   * @param window	the frame to work on
   * @param top		the y position
   * @param left	the x position
   * @param c		the component to use for lookup in the props file;
   * 			null can be used to bypass the lookup in the props file
   */
  public static void setSizeAndLocation(Window window, int top, int left, Component c) {
    int		width;
    int		height;
    Rectangle	screen;

    initializeProperties();

    // custom size?
    setSize(window, c);
    adjustSize(window);

    // position
    height = window.getHeight();
    width  = window.getWidth();
    screen = getScreenBounds(window);
    if (left + width > screen.width)
      left = screen.width - width;
    if (top + height > screen.height)
      top = screen.height - height;

    window.setLocation(left, top);
  }

  /**
   * Returns the actual screen real estate bounds according to
   * ScreenBorder.Top/Left/Bottom/Right in the props file.
   *
   * <pre>
   * +----------------------------+  physical screen
   * |                            |
   * |   +--------------------+   |
   * |   |                    |   |
   * |   |  available screen  |   |
   * |   |                    |   |
   * |   |                    |   |
   * |   +--------------------+   |
   * |                            |
   * |                            |
   * +----------------------------+
   * </pre>
   *
   * @param window	the window to get the graphics config from
   * @return		the "inner" rectangle where we can display stuff
   */
  public static synchronized Rectangle getScreenBounds(Window window) {
    int		height;
    int 	width;
    int		top;
    int		left;
    int		bottom;
    int		right;

    if (m_ScreenBoundaries == null) {
      initializeProperties();

      top     = m_Properties.getInteger("ScreenBorder.Top", 0);
      left    = m_Properties.getInteger("ScreenBorder.Left", 0);
      bottom  = m_Properties.getInteger("ScreenBorder.Bottom", 0);
      right   = m_Properties.getInteger("ScreenBorder.Right", 0);
      height  = window.getGraphicsConfiguration().getBounds().height - top - bottom;
      width   = window.getGraphicsConfiguration().getBounds().width - left - right;

      m_ScreenBoundaries = new Rectangle(left, top, width, height);
    }

    return m_ScreenBoundaries;
  }

  /**
   * Adjusts the size of the window, that it fits onto the screen.
   *
   * @param window	the window to adjust
   * @see		#getScreenBounds(Window)
   */
  public static void adjustSize(Window window) {
    Rectangle	screen;
    double	percHeight;
    double	percWidth;
    int		height;
    int		width;

    screen = getScreenBounds(window);
    height = window.getHeight();
    width  = window.getWidth();

    percHeight = m_Properties.getDouble("MaxWindowHeight", 0.95);
    if ((percHeight <= 0) || (percHeight > 1))
      percHeight = 0.95;
    percWidth = m_Properties.getDouble("MaxWindowWidth", 0.95);
    if ((percWidth <= 0) || (percWidth > 1))
      percWidth = 0.95;

    if (height > screen.height * percHeight)
      height = (int) (screen.height * percHeight);
    if (width > screen.width * percWidth)
      width = (int) (screen.width * percWidth);

    window.setSize(width, height);
    window.validate();
  }

  /**
   * Returns the startup script, if any, for the given component.
   *
   * @param c		the component to look for a startup script for
   * @return		the script file or null if none listed or none-existing
   */
  public static File getStartupScript(Component c) {
    File	result;
    String	key;
    String	script;

    initializeProperties();

    result = null;

    key = c.getClass().getName() + ".script";
    if (m_Properties.hasKey(key)) {
      script = m_Properties.getPath(key);
      result = new File(script);
      if (!result.exists()) {
	System.err.println(
	    "Startup script '" + script
	    + "' listed for component '" + c.getClass().getName()
	    + "' does not exist - ignored!");
	result = null;
      }
    }

    return result;
  }

  /**
   * Returns the string value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static String getString(String key, String defValue) {
    initializeProperties();

    return m_Properties.getProperty(key, defValue);
  }

  /**
   * Returns the string value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static String getString(Class cls, String key, String defValue) {
    return getString(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the integer value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Integer getInteger(String key, Integer defValue) {
    initializeProperties();

    return m_Properties.getInteger(key, defValue);
  }

  /**
   * Returns the integer value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Integer getInteger(Class cls, String key, Integer defValue) {
    return getInteger(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the color value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Color getColor(String key, Color defValue) {
    initializeProperties();

    return m_Properties.getColor(key, defValue);
  }

  /**
   * Returns the color value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Color getColor(Class cls, String key, Color defValue) {
    return getColor(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the double value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Double getDouble(String key, Double defValue) {
    initializeProperties();

    return m_Properties.getDouble(key, defValue);
  }

  /**
   * Returns the double value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Double getDouble(Class cls, String key, Double defValue) {
    return getDouble(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the boolean value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Boolean getBoolean(String key, Boolean defValue) {
    initializeProperties();

    return m_Properties.getBoolean(key, defValue);
  }

  /**
   * Returns the boolean value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Boolean getBoolean(Class cls, String key, Boolean defValue) {
    return getBoolean(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the Font value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Font getFont(String key, Font defValue) {
    initializeProperties();

    return m_Properties.getFont(key, defValue);
  }

  /**
   * Returns the Font value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static Font getFont(Class cls, String key, Font defValue) {
    return getFont(cls.getName() + "." + key, defValue);
  }

  /**
   * Returns the OptionHandler value listed in the props file, or the default value
   * if not found.
   *
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static OptionHandler getOptionHandler(String key, OptionHandler defValue) {
    initializeProperties();

    try {
      return OptionUtils.forCommandLine(OptionHandler.class, m_Properties.getProperty(key, OptionUtils.getCommandLine(defValue)));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return null;
  }

  /**
   * Returns the OptionHandler value listed in the props file, or the default value
   * if not found. The key used is this: classname + "." + key.
   *
   * @param cls		the class to retrieve the key for
   * @param key		the key of the property
   * @param defValue	the default value to return if property is not stored
   * 			in props file
   * @return		the value
   */
  public static OptionHandler getOptionHandler(Class cls, String key, OptionHandler defValue) {
    return getOptionHandler(cls.getName() + "." + key, defValue);
  }

  /**
   * Tries to determine the parent this panel is part of.
   *
   * @param cont	the container to get the parent for
   * @param parentClass	the class of the parent to obtain
   * @return		the parent if one exists or null if not
   */
  public static Object getParent(Container cont, Class parentClass) {
    Container	result;
    Container	parent;

    result = null;

    parent = cont;
    while (parent != null) {
      if (parentClass.isInstance(parent)) {
	result = parent;
	break;
      }
      else {
	parent = parent.getParent();
      }
    }

    return result;
  }

  /**
   * Tries to determine the frame the container is part of.
   *
   * @param cont	the container to get the frame for
   * @return		the parent frame if one exists or null if not
   */
  public static Frame getParentFrame(Container cont) {
    return (Frame) getParent(cont, Frame.class);
  }

  /**
   * Tries to determine the frame the component is part of.
   *
   * @param comp	the component to get the frame for
   * @return		the parent frame if one exists or null if not
   */
  public static Frame getParentFrame(Component comp) {
    if (comp instanceof Container)
      return (Frame) getParent((Container) comp, Frame.class);
    else
      return null;
  }

  /**
   * Tries to determine the dialog this container is part of.
   *
   * @param cont	the container to get the dialog for
   * @return		the parent dialog if one exists or null if not
   */
  public static Dialog getParentDialog(Container cont) {
    return (Dialog) getParent(cont, Dialog.class);
  }

  /**
   * Tries to determine the dialog this component is part of.
   *
   * @param comp	the component to get the dialog for
   * @return		the parent dialog if one exists or null if not
   */
  public static Dialog getParentDialog(Component comp) {
    if (comp instanceof Container)
      return (Dialog) getParent((Container) comp, Dialog.class);
    else
      return null;
  }

  /**
   * Tries to determine the internal frame this container is part of.
   *
   * @param cont	the container to start with
   * @return		the parent internal frame if one exists or null if not
   */
  public static JInternalFrame getParentInternalFrame(Container cont) {
    return (JInternalFrame) getParent(cont, JInternalFrame.class);
  }

  /**
   * Tries to determine the internal frame this component is part of.
   *
   * @param comp	the component to start with
   * @return		the parent internal frame if one exists or null if not
   */
  public static JInternalFrame getParentInternalFrame(Component comp) {
    if (comp instanceof Container)
      return (JInternalFrame) getParent((Container) comp, JInternalFrame.class);
    else
      return null;
  }

  /**
   * Tries to determine the child window/frame this container is part of.
   *
   * @param cont	the container to get the child window/frame for
   * @return		the parent child window/frame if one exists or null if not
   */
  public static Child getParentChild(Container cont) {
    return (Child) getParent(cont, Child.class);
  }

  /**
   * Tries to determine the child window/frame this component is part of.
   *
   * @param comp	the component to get the child window/frame for
   * @return		the parent child window/frame if one exists or null if not
   */
  public static Child getParentChild(Component comp) {
    if (comp instanceof Container)
      return (Child) getParent((Container) comp, Child.class);
    else
      return null;
  }

  /**
   * Tries to determine the component this panel is part of in this order:
   * 1. dialog, 2. child, 3. frame.
   *
   * @param comp	the component to get the parent component for, must
   * 			be a container actually
   * @return		the parent component if one exists or null if not
   * @see		#getParentDialog(Container)
   * @see		#getParentChild(Container)
   * @see		#getParentFrame(Container)
   */
  public static Component getParentComponent(Component comp) {
    Component	result;
    Container	cont;

    if (comp == null)
      return null;
    
    if (comp instanceof Container)
      cont = (Container) comp;
    else
      return null;

    result = getParentDialog(cont);
    if (result == null)
      result = (Component) getParentChild(cont);
    if (result == null)
      result = getParentFrame(cont);

    return result;
  }

  /**
   * Closes the parent dialog/frame of this container.
   * 
   * @param cont	the container to close the parent for
   */
  public static void closeParent(Container cont) {
    Dialog		dialog;
    Frame		frame;
    JFrame		jframe;
    JInternalFrame	jintframe;
    int		i;
    WindowListener[] 	listeners;
    WindowEvent	event;

    if (getParentDialog(cont) != null) {
      dialog = getParentDialog(cont);
      dialog.setVisible(false);
    }
    else if (getParentFrame(cont) != null) {
      jintframe = getParentInternalFrame(cont);
      if (jintframe != null) {
	jintframe.doDefaultCloseAction();
      }
      else {
	frame = getParentFrame(cont);
	if (frame instanceof JFrame) {
	  jframe = (JFrame) frame;
	  if (jframe.getDefaultCloseOperation() == JFrame.HIDE_ON_CLOSE)
	    jframe.setVisible(false);
	  else if (jframe.getDefaultCloseOperation() == JFrame.DISPOSE_ON_CLOSE)
	    jframe.dispose();
	  else if (jframe.getDefaultCloseOperation() == JFrame.EXIT_ON_CLOSE)
	    System.exit(0);

	  // notify listeners
	  listeners = jframe.getWindowListeners();
	  event     = new WindowEvent(jframe, WindowEvent.WINDOW_CLOSED);
	  for (i = 0; i < listeners.length; i++)
	    listeners[i].windowClosed(event);
	}
	else {
	  frame.dispose();
	}
      }
    }
  }

  /**
   * Attempts to bring the enclosing window to the front. Note: does not
   * work on all platforms.
   * 
   * @param cont	the container whose parent window to bring to front
   */
  public static void toFront(final Container cont) {
    Runnable	run;
    
    run = new Runnable() {
      @Override
      public void run() {
	if (getParentDialog(cont) != null)
	  getParentDialog(cont).toFront();
	else if (getParentFrame(cont) != null)
	  getParentFrame(cont).toFront();
	else if (getParentInternalFrame(cont) != null)
	  getParentInternalFrame(cont).toFront();
      }
    };
    
    SwingUtilities.invokeLater(run);
  }
  
  /**
   * Copies the given transferable to the system's clipboard.
   *
   * @param t		the transferable to copy
   */
  public static void copyToClipboard(Transferable t) {
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);
  }

  /**
   * Copies the given string to the system's clipboard.
   *
   * @param s		the string to copy
   */
  public static void copyToClipboard(String s) {
    copyToClipboard(new TransferableString(s));
  }

  /**
   * Copies the given image to the system's clipboard.
   *
   * @param img		the image to copy
   */
  public static void copyToClipboard(BufferedImage img) {
    copyToClipboard(new TransferableImage(img));
  }

  /**
   * Checks whether the specified "flavor" can be obtained from the clipboard.
   *
   * @param flavor	the type of data to look for
   * @return		true if the data can be obtained, false if not available
   */
  public static boolean canPasteFromClipboard(DataFlavor flavor) {
    Clipboard 		clipboard;
    boolean		result;

    try {
      clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      result    = clipboard.isDataFlavorAvailable(flavor);
    }
    catch (Exception e) {
      result = false;
    }

    return result;
  }

  /**
   * Checks whether a string can be obtained from the clipboard.
   *
   * @return		true if string can be obtained, false if not available
   */
  public static boolean canPasteStringFromClipboard() {
    return canPasteFromClipboard(DataFlavor.stringFlavor);
  }

  /**
   * Obtains an object from the clipboard.
   *
   * @param flavor	the type of object to obtain
   * @return		the obtained object, null if not available
   */
  public static Object pasteFromClipboard(DataFlavor flavor) {
    Clipboard 		clipboard;
    Object		result;
    Transferable	content;

    result = null;

    try {
      clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      content   = clipboard.getContents(null);
      if ((content != null) && (content.isDataFlavorSupported(flavor)))
	result = content.getTransferData(flavor);
    }
    catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Obtains a string from the clipboard.
   *
   * @return		the obtained string, null if not available
   */
  public static String pasteStringFromClipboard() {
    Clipboard 		clipboard;
    String		result;
    Transferable	content;

    result = null;

    try {
      clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      content   = clipboard.getContents(null);
      if ((content != null) && (content.isDataFlavorSupported(DataFlavor.stringFlavor)))
	result = (String) content.getTransferData(DataFlavor.stringFlavor);
    }
    catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Obtains a setup string from the clipboard. This command can be spread
   * across several lines, each line (apart from last one) ending with a "\".
   * If that is the case, then the lines are collapsed into a single line.
   *
   * @return		the obtained string, null if not available
   */
  public static String pasteSetupFromClipboard() {
    StringBuilder	result;
    String[]		parts;
    List<String>	lines;
    String		line;
    int			i;
    boolean		formatOK;

    result = new StringBuilder(pasteStringFromClipboard());

    if (result != null) {
      parts = result.toString().replaceAll("\r", "").split("\n");
      if (parts.length > 1) {
	// preprocess string
	lines = new ArrayList<>();
	for (i = 0; i < parts.length; i++) {
	  line = parts[i].trim();
	  if (line.length() == 0)
	    continue;
	  lines.add(line);
	}

	// check format:
	// all lines apart from last one must have a trailing backslash
	formatOK = true;
	for (i = 0; i < lines.size(); i++) {
	  if (i < lines.size() - 1)
	    formatOK = lines.get(i).endsWith("\\");
	  else
	    formatOK = !lines.get(i).endsWith("\\");
	  if (!formatOK)
	    break;
	}

	// collapse the command
	if (formatOK) {
	  result = new StringBuilder();
	  for (i = 0; i < lines.size(); i++) {
	    if (i > 0)
	      result.append(" ");
	    line = lines.get(i);
	    if (i < lines.size() - 1)
	      line = line.substring(0, lines.get(i).length() - 1).trim();
	    result.append(line);
	  }
	}
      }
    }

    return result.toString();
  }

  /**
   * Returns the look'n'feel classname from the props file.
   *
   * @return		the classname
   */
  public static String getLookAndFeel() {
    return getString("LookAndFeel", UIManager.getCrossPlatformLookAndFeelClassName());
  }

  /**
   * Installs the specified look'n'feel.
   *
   * @param classname	the classname of the look'n'feel to install
   * @return		true if successfully installed
   */
  public static boolean setLookAndFeel(String classname) {
    boolean	result;

    try {
      UIManager.setLookAndFeel(classname);
      result = true;
    }
    catch (Exception e) {
      result = false;
      System.err.println("Can't set look & feel:" + e);
    }

    return result;
  }

  /**
   * Suggests mnemonics for the given labels.
   *
   * @param labels	the labels to set the mnemonics for
   * @return		the mnemonics
   */
  public static char[] getMnemonics(String[] labels) {
    char[]					result;
    String      				allowed;
    String[]					strLabels;
    Hashtable<Character,HashSet<Integer>>	charIndices;
    int						i;
    int						n;
    char					ch;
    Enumeration<Character>			enm;
    HashSet<Integer>				processedIndices;
    int						numLabels;

    result = new char[labels.length];

    // a-z and 0-9 minus (O)K, (C)ancel and (M)ore
    allowed = "abdefghijklnpqrstuvwxyz0123456789";

    // determine letters and numbers per label
    strLabels = new String[labels.length];
    numLabels = 0;
    for (i = 0; i < labels.length; i++) {
      strLabels[i] = labels[i].toLowerCase().replaceAll("[^a-z0-9]", "");
      if (strLabels[i].length() == 0)
	strLabels[i] = null;
      else
	numLabels++;
    }

    // determine counts of characters across the labels
    charIndices = new Hashtable<>();
    for (i = 0; i < allowed.length(); i++) {
      ch = allowed.charAt(i);
      for (n = 0; n < strLabels.length; n++) {
	if (strLabels[n] == null)
	  continue;
	if (strLabels[n].indexOf(ch) > -1) {
	  if (!charIndices.containsKey(ch))
	    charIndices.put(ch, new HashSet<>());
	  charIndices.get(ch).add(n);
	}
      }
    }

    // set the mnemonics for the labels which are the most unique
    i                = 0;
    processedIndices = new HashSet<>();
    do {
      i++;
      enm = charIndices.keys();
      while (enm.hasMoreElements()) {
	ch = enm.nextElement();
	if (charIndices.get(ch).size() == i) {
	  for (Integer index: charIndices.get(ch)) {
	    if (index < 0)
	      continue;
	    if (strLabels[index] == null)
	      continue;
	    result[index]    = ch;
	    strLabels[index] = null;
	  }

	  // flag indices as 'processed'
	  processedIndices.addAll(charIndices.get(ch));
	  processedIndices.remove(-1);
	}
      }
    }
    while (processedIndices.size() != numLabels);

    return result;
  }

  /**
   * Checks the caption whether an underscore "_" is present to indicate
   * that the following character is to act as mnemonic.
   *
   * @param caption	the caption to analyze
   * @return		true if an underscore is present
   * @see		#MNEMONIC_INDICATOR
   */
  public static boolean hasMnemonic(String caption) {
    return (caption.indexOf(MNEMONIC_INDICATOR) > -1);
  }

  /**
   * Returns the mnemonic for this caption, preceded by an underscore "_".
   *
   * @param caption	the caption to extract
   * @return		the extracted mnemonic, \0 if none available
   * @see		#MNEMONIC_INDICATOR
   */
  public static char getMnemonic(String caption) {
    int		pos;

    pos = caption.indexOf(MNEMONIC_INDICATOR);
    if ((pos > -1) && (pos < caption.length() - 1))
      return caption.charAt(pos + 1);
    else
      return '\0';
  }

  /**
   * Removes the mnemonic indicator in this caption.
   *
   * @param caption	the caption to process
   * @return		the processed caption
   * @see		#MNEMONIC_INDICATOR
   */
  public static String stripMnemonic(String caption) {
    return caption.replace("" + MNEMONIC_INDICATOR, "");
  }

  /**
   * Displays an error message with the default title "Error".
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   */
  public static void showErrorMessage(Component parent, String msg) {
    showErrorMessage(parent, msg, "Error");
  }

  /**
   * Displays an error message.
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   * @param title	the title of the error message
   */
  public static void showErrorMessage(Component parent, final String msg, String title) {
    final ApprovalDialog	dlg;
    String[]			lines;
    int				height;
    ErrorMessagePanel		errorPanel;
    
    parent = GUIHelper.getParentComponent(parent);

    lines  = msg.split("\n");
    height = Math.min(350, (lines.length + 1) * 20);
    if (parent instanceof Frame)
      dlg = ApprovalDialog.getDialog((Frame) parent, true);
    else if (parent instanceof Dialog)
      dlg = ApprovalDialog.getDialog((Dialog) parent, ModalityType.APPLICATION_MODAL);
    else
      dlg = ApprovalDialog.getDialog(null, ModalityType.APPLICATION_MODAL);
    dlg.setTitle(title);
    dlg.setApproveCaption("Close");
    dlg.setApproveMnemonic('l');
    dlg.setCancelVisible(false);
    dlg.setDiscardVisible(false);
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setIconImage(GUIHelper.getIcon("stop.gif").getImage());
    
    errorPanel = new ErrorMessagePanel();
    errorPanel.setTitle(title);
    errorPanel.setErrorMessage(msg);
    errorPanel.setLineWrap(false);
    dlg.getButtonsPanel(true).add(errorPanel.getConsoleCheckBox());
    dlg.getContentPane().add(errorPanel, BorderLayout.CENTER);
    dlg.setJMenuBar(errorPanel.getMenuBar());
    
    dlg.pack();
    dlg.setSize(600, Math.min(dlg.getHeight() + height, (int) (getScreenBounds(dlg).height * 0.5)));
    errorPanel.setErrorMessage(msg);
    dlg.setLocationRelativeTo(parent);
    dlg.getApproveButton().requestFocusInWindow();
    dlg.setVisible(true);
  }

  /**
   * Displays an information message with the default title "Information".
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the information message to display
   */
  public static void showInformationMessage(Component parent, String msg) {
    showInformationMessage(parent, msg, "Information");
  }

  /**
   * Displays an information message.
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the information message to display
   * @param title	the title of the information message
   */
  public static void showInformationMessage(Component parent, String msg, String title) {
    final ApprovalDialog	dlg;
    String[]				lines;
    int					height;
    TextPanel				editor;
    
    parent = GUIHelper.getParentComponent(parent);

    lines  = msg.split("\n");
    height = Math.min(350, (lines.length + 1) * 20);
    if (parent instanceof Frame)
      dlg = ApprovalDialog.getDialog((Frame) parent, true);
    else if (parent instanceof Dialog)
      dlg = ApprovalDialog.getDialog((Dialog) parent, ModalityType.DOCUMENT_MODAL);
    else
      dlg = ApprovalDialog.getDialog(null, ModalityType.DOCUMENT_MODAL);
    dlg.setTitle(title);
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setIconImage(GUIHelper.getIcon("information.png").getImage());
    editor = new TextPanel();
    editor.setTitle(title);
    editor.setEditable(false);
    editor.setLineWrap(true);
    dlg.getContentPane().add(editor, BorderLayout.CENTER);
    dlg.pack();
    dlg.setSize(600, Math.min(dlg.getHeight() + height, (int) (getScreenBounds(dlg).height * 0.5)));
    dlg.setLocationRelativeTo(parent);
    editor.setContent(msg);
    dlg.getApproveButton().requestFocusInWindow();
    dlg.setVisible(true);
  }

  /**
   * Displays a confirmation dialog (yes/no/cancel) with the default title "Confirm".
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   * @return		the selected option
   * @see		ApprovalDialog#APPROVE_OPTION
   * @see		ApprovalDialog#DISCARD_OPTION
   * @see		ApprovalDialog#CANCEL_OPTION
   */
  public static int showConfirmMessage(Component parent, String msg) {
    return showConfirmMessage(parent, msg, "Confirm");
  }

  /**
   * Displays a confirmation dialog (yes/no/cancel).
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param msg		the error message to display
   * @param title	the title of the error message
   * @return		the selected option
   * @see		ApprovalDialog#APPROVE_OPTION
   * @see		ApprovalDialog#DISCARD_OPTION
   * @see		ApprovalDialog#CANCEL_OPTION
   */
  public static int showConfirmMessage(Component parent, String msg, String title) {
    return showConfirmMessage(parent, null, msg, title);
  }

  /**
   * Displays a confirmation dialog (yes/no/cancel).
   *
   * @param parent	the parent, to make the dialog modal; can be null
   * @param header	the text explaining the message, null to ignore
   * @param msg		the error message to display
   * @param title	the title of the error message
   * @return		the selected option
   * @see		ApprovalDialog#APPROVE_OPTION
   * @see		ApprovalDialog#DISCARD_OPTION
   * @see		ApprovalDialog#CANCEL_OPTION
   */
  public static int showConfirmMessage(Component parent, String header, String msg, String title) {
    final ApprovalDialog	dlg;
    String[]			lines;
    int				height;
    TextPanel			editor;
    
    parent = GUIHelper.getParentComponent(parent);
    
    lines  = msg.split("\n");
    height = Math.min(350, (lines.length + 1) * 20);
    if (parent instanceof Frame)
      dlg = ApprovalDialog.getConfirmationDialog((Frame) parent, true);
    else if (parent instanceof Dialog)
      dlg = ApprovalDialog.getConfirmationDialog((Dialog) parent, ModalityType.APPLICATION_MODAL);
    else
      dlg = ApprovalDialog.getConfirmationDialog(null, ModalityType.APPLICATION_MODAL);
    dlg.setTitle(title);
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setIconImage(GUIHelper.getIcon("question.png").getImage());
    editor = new TextPanel();
    editor.setTitle(title);
    editor.setEditable(false);
    editor.setLineWrap(true);
    if (header != null)
      editor.setInfoText(header);
    dlg.getContentPane().add(editor, BorderLayout.CENTER);
    dlg.pack();
    dlg.setSize(600, Math.min(dlg.getHeight() + height, (int) (getScreenBounds(dlg).height * 0.5)));
    dlg.setLocationRelativeTo(parent);
    editor.setContent(msg);
    dlg.getCancelButton().requestFocusInWindow();
    dlg.setVisible(true);
    
    return dlg.getOption();
  }

  /**
   * A simple dialog for entering a string.
   * 
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Enter value" in that case)
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg) {
    return showInputDialog(parent, null, msg);
  }

  /**
   * A simple dialog for entering a string.
   * 
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Enter value" in that case)
   * @param initial	the initial selection, can be null
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial) {
    return showInputDialog(parent, msg, initial, (String[])null);
  }

  /**
   * A simple dialog for entering a string.
   * 
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Enter value" in that case)
   * @param initial	the initial selection, can be null
   * @param title	the title of the input dialog, can be null (uses "Enter value" in that case)
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String title) {
    JPanel			panelAll;
    JPanel			panel;
    JLabel			label;
    BaseTextArea		textValue;
    final ApprovalDialog	dialog;
    Component			pparent;

    if (initial == null)
      initial = "";
    if ((title == null) || (title.isEmpty()))
      title = "Enter value";
    if ((msg == null) || (msg.isEmpty()))
      msg = "Enter value";
    
    pparent = GUIHelper.getParentComponent(parent);
    if (pparent instanceof Dialog)
      dialog = ApprovalDialog.getDialog((Dialog) pparent, ModalityType.DOCUMENT_MODAL);
    else
      dialog = ApprovalDialog.getDialog((Frame) pparent, true);
    dialog.setTitle(title);
    
    textValue = new BaseTextArea(1, 20);
    textValue.setText(initial);
    if (!initial.isEmpty()) {
      textValue.setSelectionStart(0);
      textValue.setSelectionEnd(initial.length());
    }
    textValue.setLineWrap(true);
    textValue.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  e.consume();
	  dialog.getApproveButton().doClick();
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  dialog.getCancelButton().doClick();
	}
	else {
	  super.keyPressed(e);
	}
      }
    });

    panelAll = new JPanel(new BorderLayout(5, 5));
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    label = new JLabel(msg);
    panelAll.add(label, BorderLayout.NORTH);
    
    panel = new JPanel(new BorderLayout());
    panel.add(new BaseScrollPane(textValue), BorderLayout.CENTER);
    panelAll.add(panel, BorderLayout.CENTER);

    dialog.getContentPane().add(panelAll, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
      return textValue.getText();
    else
      return null;
  }

  /**
   * A simple dialog for selecting a string.
   * 
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Select value" in that case)
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String[] options) {
    return showInputDialog(parent, msg, initial, options, null);
  }

  /**
   * A simple dialog for selecting a string.
   * 
   * @param parent	the parent for this dialog
   * @param msg		the message to display, can be null (uses "Select value" in that case)
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @param title	the title of the input dialog, can be null (uses "Select value" in that case)
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String[] options, String title) {
    return showInputDialog(parent, msg, initial, options, true, title);
  }

  /**
   * A simple dialog for selecting a string.
   * 
   * @param parent	the parent for this dialog
   * @param title	the title of the input dialog, can be null (uses "Select value" in that case)
   * @param msg		the message to display, can be null (uses "Select value" in that case)
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @return		the value entered, null if cancelled
   */
  protected static String showInputDialogComboBox(Component parent, String msg, String initial, String[] options, String title) {
    JPanel			panelAll;
    JPanel			panelCombo;
    JPanel			panel;
    JLabel			label;
    JComboBox			combobox;
    final ApprovalDialog	dialog;
    Component			pparent;
    
    if (initial == null)
      initial = "";
    if ((title == null) || (title.isEmpty()))
      title = "Select value";
    if ((msg == null) || (msg.isEmpty()))
      msg = "Select value";
    
    pparent = GUIHelper.getParentComponent(parent);
    if (pparent instanceof Dialog)
      dialog = ApprovalDialog.getDialog((Dialog) pparent, ModalityType.DOCUMENT_MODAL);
    else
      dialog = ApprovalDialog.getDialog((Frame) pparent, true);
    dialog.setTitle(title);
    
    combobox = new JComboBox(options);
    if (!initial.isEmpty())
      combobox.setSelectedItem(initial);
    else
      combobox.setSelectedIndex(0);
    combobox.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  e.consume();
	  dialog.getApproveButton().doClick();
	}
	else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
	  e.consume();
	  dialog.getCancelButton().doClick();
	}
	else {
	  super.keyPressed(e);
	}
      }
    });
    panelCombo = new JPanel(new BorderLayout());
    panelCombo.add(combobox, BorderLayout.NORTH);

    panelAll = new JPanel(new BorderLayout(5, 0));
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    label = new JLabel(msg);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panelAll.add(panel, BorderLayout.NORTH);
    
    panel = new JPanel(new BorderLayout());
    panel.add(panelCombo, BorderLayout.CENTER);
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panelAll.add(panel, BorderLayout.CENTER);

    dialog.getContentPane().add(panelAll, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (dialog.getOption() == ApprovalDialog.APPROVE_OPTION)
      return (String) combobox.getSelectedItem();
    else
      return null;
  }

  /**
   * A simple dialog for selecting a string by clicking on a button.
   * 
   * @param parent	the parent for this dialog
   * @param title	the title of the input dialog, can be null (uses "Select value" in that case)
   * @param msg		the message to display, can be null (uses "Select value" in that case)
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @return		the value entered, null if cancelled
   */
  protected static String showInputDialogButtons(Component parent, String msg, String initial, String[] options, String title) {
    Component		pparent;
    final BaseDialog	dialog;
    JPanel		panelButtons;
    JPanel		panel;
    JPanel		panelAll;
    JLabel		label;
    final StringBuilder	result;
    
    if (initial == null)
      initial = "";
    if ((title == null) || (title.isEmpty()))
      title = "Select value";
    if ((msg == null) || (msg.isEmpty()))
      msg = "Select alue";
    
    pparent = GUIHelper.getParentComponent(parent);
    if (pparent instanceof Dialog)
      dialog = new BaseDialog((Dialog) pparent, ModalityType.DOCUMENT_MODAL);
    else
      dialog = new BaseDialog((Frame) pparent, true);
    dialog.setTitle(title);

    panelAll = new JPanel(new BorderLayout(5, 5));
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    label = new JLabel(msg);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panelAll.add(panel, BorderLayout.NORTH);
    
    result = new StringBuilder();

    panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    for (String option: options) {
      final JButton button = new JButton(option);
      button.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          result.append(button.getText());
          dialog.setVisible(false);
        }
      });
      panelButtons.add(button);
    }
    panelAll.add(panelButtons, BorderLayout.CENTER);
    
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panelAll, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    if (result.length() == 0)
      return null;
    else
      return result.toString();
  }

  /**
   * A simple dialog for entering a string.
   * 
   * @param parent	the parent for this dialog
   * @param title	the title of the input dialog, can be null
   * @param msg		the message to display
   * @param initial	the initial selection, can be null
   * @param options	the available options
   * @param useComboBox	whether to use a combobox or buttons
   * @return		the value entered, null if cancelled
   */
  public static String showInputDialog(Component parent, String msg, String initial, String[] options, boolean useComboBox, String title) {
    if (useComboBox)
      return showInputDialogComboBox(parent, msg, initial, options, title);
    else
      return showInputDialogButtons(parent, msg, initial, options, title);
  }
  
  /**
   * Parses the tool tip and breaks it up into multiple lines if longer
   * than width characters.
   *
   * @param text	the tiptext to parse
   * @param width	the maximum width
   * @return		the processed tiptext
   */
  public static String processTipText(String text, int width) {
    String	result;
    String[]	lines;

    result = HtmlUtils.toHTML(text);
    if (result.length() > width) {
      lines  = Utils.breakUp(result, width);
      result = Utils.flatten(lines, "<br>");
    }
    result = "<html>" + result + "</html>";

    return result;
  }

  /**
   * Processes the keystrokes. For Macs, "ctrl/control" is replaced by "meta".
   * Also, if no "ctrl/control/alt" is present, a "meta" is prefix.
   * All other platforms simply return the string.
   *
   * @param keystroke	the keystroke string to process
   * @return		the (potentially) processed keystroke definition
   */
  public static String processKeyStroke(String keystroke) {
    String	result;

    result = keystroke;

    if (OS.isMac()) {
      // "meta + Q" usually closes application
      if (keystroke.toLowerCase().equals("ctrl pressed q") || keystroke.toLowerCase().equals("control pressed q"))
	keystroke = "ctrl shift pressed W";
      result = result.replace("ctrl", "meta").replace("control", "meta");
      if ((result.indexOf("meta") == -1) && (result.indexOf("alt") == -1))
	result = "meta " + result;
    }

    return result;
  }

  /**
   * Creates a keystroke from the string.
   *
   * @param keystroke	the keystroke string to turn into a
   * @return		the generated keystroke
   * @see		#processKeyStroke(String)
   */
  public static KeyStroke getKeyStroke(String keystroke) {
    return KeyStroke.getKeyStroke(processKeyStroke(keystroke));
  }
  
  /**
   * Parses a font string ('name-face-size') and returns the Font object.
   * 
   * @param s		the string to parse
   * @return		the font
   * @see		Font#decode(String)
   */
  public static Font decodeFont(String s) {
    return Font.decode(s);
  }
  
  /**
   * Turns a font into a string representation that can get parsed with
   * {@link #decodeFont(String)} again.
   * 
   * @param f		the font to turn into a string
   * @return		the font
   */
  public static String encodeFont(Font f) {
    String	result;
    String	face;

    if (f.isBold() && f.isItalic())
      face = "BOLDITALIC";
    else if (f.isBold())
      face = "BOLD";
    else if (f.isItalic())
      face = "ITALIC";
    else
      face = "PLAIN";
    
    result = f.getName() + "-" + face + "-" + f.getSize();
    
    return result;
  }
  
  /**
   * Returns the system wide Monospaced font.
   * 
   * @return		the font
   */
  public static Font getMonospacedFont() {
    return getFont("Font.Monospaced", new Font("monospaced", Font.PLAIN, 12));
  }
  
  /**
   * Retrieves all components of the specified type starting with
   * the given parent container.
   * 
   * @param parent	the container to start the search in
   * @param recursive	whether to recurse into other containers
   * @param exact	whether the class must be exactly this type of merely derived
   * @param onlyFirst	whether to stop search once the first component was found
   * @return		the list of located components
   */
  protected static void findComponents(Container parent, Class type, boolean recursive, boolean exact, List<Component> list, boolean onlyFirst) {
    int		i;
    Component	comp;
    
    for (i = 0; i < parent.getComponentCount(); i++) {
      comp = parent.getComponent(i);
      
      // match?
      if (exact) {
	if (comp.getClass().equals(type))
	  list.add(comp);
      }
      else {
	if (ClassLocator.isSubclass(type, comp.getClass()))
	  list.add(comp);
	else if (ClassLocator.hasInterface(type, comp.getClass()))
	  list.add(comp);
      }
      
      // stop at first hit?
      if (onlyFirst && (list.size() > 0))
	return;
      
      // search deeper?
      if (recursive && (comp instanceof Container))
	findComponents((Container) comp, type, recursive, exact, list, onlyFirst);
    }
  }
  
  /**
   * Retrieves all components of the specified type starting with
   * the given parent container.
   * 
   * @param parent	the container to start the search in
   * @param recursive	whether to recurse into other containers
   * @param exact	whether the class must be exactly this type of merely derived
   * @return		the list of located components
   */
  public static List<Component> findAllComponents(Container parent, Class type, boolean recursive, boolean exact) {
    ArrayList<Component>	result;
    
    result = new ArrayList<>();
    findComponents(parent, type, recursive, exact, result, false);
    
    return result;
  }
  
  /**
   * Retrieves the first component of the specified type starting with
   * the given parent container.
   * 
   * @param parent	the container to start the search in
   * @param recursive	whether to recurse into other containers
   * @param exact	whether the class must be exactly this type of merely derived
   * @return		the located component or null if none found
   */
  public static Component findFirstComponent(Container parent, Class type, boolean recursive, boolean exact) {
    ArrayList<Component>	result;
    
    result = new ArrayList<>();
    findComponents(parent, type, recursive, exact, result, true);
    
    if (result.isEmpty())
      return null;
    else
      return result.get(0);
  }
  
  /**
   * Enables/disables anti-aliasing in the Graphics context.
   * 
   * @param g		the graphics context to enable/disable anti-aliasing for
   * @param enable	if true anti-aliasing gets enabled, otherwise disabled
   */
  public static void configureAntiAliasing(Graphics g, boolean enable) {
    if (enable)
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    else
      ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
  }
  
  /**
   * Determines the top-level menu (the one sitting inside the JMenuBar) that 
   * is associated with a menu item.
   * 
   * @param menuitem	the menu item to get the menu bar for
   * @return		the menu, null if not found
   */
  public static JMenu getTopLevelMenu(JMenuItem menuitem) {
    JMenu	result;
    Container	cont;
    Container	prev;
    
    result = null;
    
    cont = menuitem;
    prev = null;
    while (cont != null) {
      if (cont instanceof JPopupMenu) {
	result = (JMenu) prev;
	break;
      }
      prev = cont;
      cont = cont.getParent();
    }
    
    return result;
  }
  
  /**
   * Attempts to launch the specified menuitem. Requires to find the overall
   * application frame in order to launch the menu item.
   * 
   * @param source	where the menuitem launch originated from
   * @param menuitem	the menuitem class to launch
   * @return		true if successfully launched
   */
  public static boolean launchMenuItem(Container source, Class menuitem) {
    Child			child;
    AbstractMenuItemDefinition	mitem;
    
    child = getParentChild(source);
    if (child == null)
      return false;
    if (child.getParentFrame() == null)
      return false;
    
    try {
      mitem = (AbstractMenuItemDefinition) menuitem.newInstance();
      mitem.setOwner(child.getParentFrame());
      mitem.launch();
    }
    catch (Exception e) {
      // ignored
      return false;
    }
    
    return true;
  }
}
