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
 * Tree.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.option.NestedConsumer;
import adams.core.option.NestedProducer;
import adams.flow.control.Breakpoint;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorPath;
import adams.flow.core.ActorUtils;
import adams.flow.core.FixedNameActorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.flow.execution.FlowExecutionListeningSupporter;
import adams.flow.processor.AbstractActorProcessor;
import adams.flow.processor.GraphicalOutputProducingProcessor;
import adams.flow.processor.ModifyingProcessor;
import adams.flow.processor.RemoveDisabledActors;
import adams.flow.template.AbstractActorTemplate;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTreeNode;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.DragAndDropTree;
import adams.gui.core.DragAndDropTreeNodeCollection;
import adams.gui.core.ErrorMessagePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;
import adams.gui.core.dotnotationtree.AbstractItemFilter;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.event.ActorChangeListener;
import adams.gui.event.NodeDroppedEvent;
import adams.gui.event.NodeDroppedEvent.NotificationTime;
import adams.gui.event.NodeDroppedListener;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.tree.menu.EditActor;
import adams.gui.flow.tree.menu.TreePopupAction;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.classtree.ActorClassTreeFilter;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * A custom tree for displaying the structure of a flow.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10218 $
 */
public class Tree
  extends DragAndDropTree {

  /** for serialization. */
  private static final long serialVersionUID = -6052602093735801950L;

  /**
   * Enumeration for how to insert a node.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 10218 $
   */
  public enum InsertPosition {
    /** beneath the current path. */
    BENEATH,
    /** here at this position. */
    HERE,
    /** after this position. */
    AFTER
  }
  
  /** the tree itself. */
  protected Tree m_Self;

  /** the owner. */
  protected FlowPanel m_Owner;

  /** the listeners for changes in the actors. */
  protected HashSet<ActorChangeListener> m_ActorChangeListeners;

  /** whether the setup was modified or not. */
  protected boolean m_Modified;

  /** the file this actor is based on (if at all). */
  protected File m_File;

  /** the node found in the last search. */
  protected Node m_LastSearchNode;

  /** the last search string used. */
  protected String m_LastSearchString;

  /** the HTML color string of the actor names (e.g., 'black' or '#000000'). */
  protected String m_ActorNameColor;

  /** the HTML font tag size of the actor names (e.g., '3' or '-1'). */
  protected String m_ActorNameSize;

  /** the HTML color string of the quick info (e.g., 'green' or '#008800'). */
  protected String m_QuickInfoColor;

  /** the HTML font tag size of the quick info (e.g., '3' or '-2'). */
  protected String m_QuickInfoSize;

  /** the HTML color string of the annotations (e.g., 'blue' or '#0000FF'). */
  protected String m_AnnotationsColor;

  /** the HTML font tag size of the annotations (e.g., '3' or '-2'). */
  protected String m_AnnotationsSize;

  /** the HTML color string of the input/output info (e.g., 'green' or '#008800'). */
  protected String m_InputOutputColor;

  /** the HTML font tag size of the input/output info (e.g., '3' or '-2'). */
  protected String m_InputOutputSize;

  /** the HTML color string of the placeholders (e.g., 'navy' or '#0000FF'). */
  protected String m_PlaceholdersColor;

  /** the HTML font tag size of the placeholders (e.g., '3' or '-2'). */
  protected String m_PlaceholdersSize;

  /** the background HTML color string of the variable highlights (e.g., 'red' or '#FFDD88'). */
  protected String m_VariableHighlightBackground;

  /** the background HTML color string of the bookmark highlights (e.g., 'orange' or '#FFDD00'). */
  protected String m_BookmarkHighlightBackground;

  /** whether to display the quick info or not. */
  protected boolean m_ShowQuickInfo;

  /** whether to show the annotations or not. */
  protected boolean m_ShowAnnotations;

  /** whether to display the input/output info or not. */
  protected boolean m_ShowInputOutput;

  /** the input/output class prefixes to remove. */
  protected String[] m_InputOutputPrefixes;

  /** the dialog for selecting a template for generating a flow fragment. */
  protected GenericObjectEditorDialog m_TemplateDialog;

  /** whether to store the flow as an object in the "state" or in nested format. */
  protected boolean m_StateUsesNested;

  /** the node that is currently being edited. */
  protected Node m_CurrentEditingNode;

  /** the parent of the currently edited node or node to be added. */
  protected Node m_CurrentEditingParent;

  /** the last template that was added via 'Add from template'. */
  protected AbstractActorTemplate m_LastTemplate;

  /** the position of the last template that was added via 'Add from template'. */
  protected InsertPosition m_LastTemplateInsertPosition;

  /** whether to ignore name changes of actors (suppressing application of post-processors). */
  protected boolean m_IgnoreNameChanges;

  /** the actions with shortcuts. */
  protected List<TreePopupAction> m_Shortcuts;

  /** the dialog for processing actors. */
  protected GenericObjectEditorDialog m_DialogProcessActors;

  /** whether to record the adding of actors to improve suggestions. */
  protected boolean m_RecordAdd;

  /**
   * Initializes the tree.
   *
   * @param owner	the owning panel
   */
  public Tree(FlowPanel owner) {
    this(owner, null);
  }

  /**
   * Initializes the tree.
   *
   * @param owner	the owning panel
   * @param root	the root actor, can be null
   */
  public Tree(FlowPanel owner, AbstractActor root) {
    super();
    m_Owner = owner;
    buildTree(root);
  }

  /**
   * Further initialization of the tree.
   */
  @Override
  protected void initialize() {
    String[]		classes;
    TreePopupAction	action;

    super.initialize();

    m_Self                        = this;
    m_Modified                    = false;
    m_ActorChangeListeners        = new HashSet<>();
    m_LastSearchString            = "";
    m_LastSearchNode              = null;
    m_ShowQuickInfo               = true;
    m_ShowAnnotations             = true;
    m_ShowInputOutput             = false;
    m_ActorNameColor              = "black";
    m_ActorNameSize               = "3";
    m_QuickInfoColor              = "#008800";
    m_QuickInfoSize               = "-2";
    m_AnnotationsColor            = "blue";
    m_AnnotationsSize             = "-2";
    m_PlaceholdersColor           = "navy";
    m_PlaceholdersSize            = "-2";
    m_InputOutputColor            = "grey";
    m_InputOutputSize             = "-2";
    m_VariableHighlightBackground = "#FFDD88";
    m_BookmarkHighlightBackground = "#FFDD00";
    m_StateUsesNested             = true;
    m_InputOutputPrefixes         = new String[0];
    m_CurrentEditingNode          = null;
    m_CurrentEditingParent        = null;
    m_File                        = null;
    m_LastTemplate                = null;
    m_IgnoreNameChanges           = false;
    m_RecordAdd                   = false;

    putClientProperty("JTree.lineStyle", "None");
    setLargeModel(true);
    setSelectionModel(new SelectionModel());
    setCellRenderer(new Renderer());
    setCellEditor(new CellEditor(this, (DefaultTreeCellRenderer) getCellRenderer()));
    setShowsRootHandles(true);
    setToggleClickCount(0);  // to avoid double clicks from toggling expanded/collapsed state

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (m_Self.isEnabled() && MouseUtils.isRightClick(e)) {
          e.consume();
          showNodePopupMenu(e);
        }
        else if (m_Self.isEnabled() && MouseUtils.isDoubleClick(e)) {
          e.consume();
          StateContainer state = getTreeState(e);
          if (state == null)
            return;
          EditActor action = new EditActor();
          action.update(getTreeState(e));
          action.actionPerformed(null);
        }
        else {
          super.mousePressed(e);
        }
      }
    });

    m_Shortcuts = new ArrayList<>();
    classes     = ClassLister.getSingleton().getClassnames(TreePopupAction.class);
    for (String cls: classes) {
      try {
	action = (TreePopupAction) Class.forName(cls).newInstance();
	if (action.hasAccelerator())
	  m_Shortcuts.add(action);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(OutputType.ERROR, "Failed to instantiate action '" + cls + "':\n" + Utils.throwableToString(e));
      }
    }
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	TreePath path = getSelectionPath();
	TreePath[] paths = getSelectionPaths();
	if (path != null) {
	  StateContainer state = getTreeState(paths, TreeHelper.pathToNode(path));
	  KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
	  for (TreePopupAction action: m_Shortcuts) {
	    action.update(state);
	    if (action.keyStrokeApplies(ks) && action.isEnabled()) {
	      action.actionPerformed(null);
	      e.consume();
	      break;
	    }
	  }
	}
      }
    });

    addNodeDroppedListener(new NodeDroppedListener() {
      @Override
      public void nodeDropped(NodeDroppedEvent e) {
	BaseTreeNode[] tnodes = e.getNodes();
	ArrayList<Node> nodes = new ArrayList<>();

	// update actor name, if necessary
	if (e.getNotificationTime() == NotificationTime.FINISHED) {
	  for (BaseTreeNode node: tnodes) {
	    if (node instanceof Node) {
	      if (updateActorName((Node) node))
		nodeStructureChanged((Node) node);
	    }
	  }
	}

	// undo + modified
	if (e.getNotificationTime() == NotificationTime.BEFORE) {
	  if (nodes.size() == 1)
	    addUndoPoint("Drag'n'Drop of actor '" + nodes.get(0).getActor().getName() + "'");
	  else
	    addUndoPoint("Drag'n'Drop of " + nodes.size() + " actors");
	}
	else {
	  setModified(true);
	  notifyActorChangeListeners(new ActorChangeEvent(m_Self, nodes.toArray(new Node[nodes.size()]), Type.MODIFY));
	}
      }
    });
  }

  /**
   * Sets the tree's selection model. When a <code>null</code> value is
   * specified an empty
   * <code>selectionModel</code> is used, which does not allow selections.
   *
   * @param selectionModel the <code>TreeSelectionModel</code> to use,
   *		or <code>null</code> to disable selections
   * @see TreeSelectionModel
   */
  @Override
  public void setSelectionModel(TreeSelectionModel selectionModel) {
    if (!(selectionModel instanceof SelectionModel))
      throw new IllegalArgumentException(
	  "Only " + SelectionModel.class.getName() + " models are allowed");

    super.setSelectionModel(selectionModel);
  }
  
  /**
   * Builds the tree with the given root.
   *
   * @param root	the root actor, can be null
   */
  public void buildTree(AbstractActor root) {
    DefaultTreeModel		model;
    TreeModel			modelOld;
    DefaultMutableTreeNode	rootNode;

    modelOld = null;
    if (getModel() instanceof TreeModel)
      modelOld = (TreeModel) getModel();

    if (root == null) {
      model = new DefaultTreeModel(null);
    }
    else {
      rootNode = buildTree(null, root, true);
      model    = new DefaultTreeModel(rootNode);
    }

    setModel(model);

    if (model.getRoot() != null)
      expandPath(new TreePath(model.getRoot()));

    // clean up old model
    if (modelOld != null)
      modelOld.destroy();
  }

  /**
   * Builds the tree recursively.
   *
   * @param parent	the parent to add the actor to
   * @param actor	the actor to add
   * @param append	whether to append the sub-tree to the parent or just
   * 			return it (recursive calls always append the sub-tree!)
   * @return		the generated node
   */
  public Node buildTree(Node parent, AbstractActor actor, boolean append) {
    return buildTree(parent, new AbstractActor[]{actor}, append)[0];
  }

  /**
   * Builds the tree recursively.
   *
   * @param parent	the parent to add the actor to
   * @param actors	the actors to add
   * @param append	whether to append the sub-tree to the parent or just
   * 			return it (recursive calls always append the sub-tree!)
   * @return		the generated nodes
   */
  protected Node[] buildTree(Node parent, AbstractActor[] actors, boolean append) {
    Node[]	result;
    int		n;
    int		i;

    result = new Node[actors.length];
    for (n = 0; n < actors.length; n++) {
      result[n] = new Node(this, actors[n]);
      if ((parent != null) && append)
	parent.add(result[n]);

      if (actors[n] instanceof ActorHandler) {
	for (i = 0; i < ((ActorHandler) actors[n]).size(); i++)
	  buildTree(result[n], ((ActorHandler) actors[n]).get(i), true);
      }
    }

    return result;
  }

  /**
   * Ensures that the name of the actor stored in the node is unique among
   * its siblings. For {@link FixedNameActorHandler} parents, the desired name
   * is determined.
   *
   * @param node	the actor to check
   * @return		true if the actor's name was modified
   */
  public boolean updateActorName(Node node) {
    boolean		result;
    Node 		parent;
    AbstractActor	actor;
    HashSet<String> 	names;
    String		name;
    int			i;

    result = false;

    parent = (Node) node.getParent();
    if (parent != null) {
      if (parent.getActor() instanceof FixedNameActorHandler) {
	i     = parent.getIndex(node);
	actor = node.getActor();
	name  = ((FixedNameActorHandler) parent.getActor()).getFixedName(i);
	if (!actor.getName().equals(name)) {
	  actor.setName(name);
	  node.setActor(actor);
	  result = true;
	}
      }
      else if (parent.getActor() instanceof ActorHandler) {
	actor = node.getActor();
	names = new HashSet<>();
	for (i = 0; i < parent.getChildCount(); i++) {
	  if (parent.getChildAt(i) == node)
	    continue;
	  names.add(((Node) parent.getChildAt(i)).getActor().getName());
	}
	result = ActorUtils.uniqueName(actor, names);
	if (result)
	  node.setActor(actor);
      }
    }

    return result;
  }

  /**
   * Returns the owning panel.
   *
   * @return		the panel
   */
  public FlowPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the owning editor.
   *
   * @return		the editor
   */
  public FlowEditorPanel getEditor() {
    return m_Owner.getEditor();
  }

  /**
   * Sets the HTML color string for the actor names.
   *
   * @param value	the HTML color string
   */
  public void setActorNameColor(String value) {
    m_ActorNameColor = value;
  }

  /**
   * Returns the HTML color string for the actor names.
   *
   * @return		the HTML color string
   */
  public String getActorNameColor() {
    return m_ActorNameColor;
  }

  /**
   * Sets the HTML font tag size string for the actor names.
   *
   * @param value	the HTML font tag size string
   */
  public void setActorNameSize(String value) {
    m_ActorNameSize = value;
  }

  /**
   * Returns the HTML font tag size string for the actor names.
   *
   * @return		the HTML font tag size string
   */
  public String getActorNameSize() {
    return m_ActorNameSize;
  }

  /**
   * Sets the HTML color string for the annotations.
   *
   * @param value	the HTML color string
   */
  public void setAnnotationsColor(String value) {
    m_AnnotationsColor = value;
  }

  /**
   * Returns the HTML color string for the annotations.
   *
   * @return		the HTML color string
   */
  public String getAnnotationsColor() {
    return m_AnnotationsColor;
  }

  /**
   * Sets the HTML font tag size string for the annotations.
   *
   * @param value	the HTML font tag size string
   */
  public void setAnnotationsSize(String value) {
    m_AnnotationsSize = value;
  }

  /**
   * Returns the HTML font tag size string for the annotations.
   *
   * @return		the HTML font tag size string
   */
  public String getAnnotationsSize() {
    return m_AnnotationsSize;
  }

  /**
   * Sets the HTML color string for the quick info.
   *
   * @param value	the HTML color string
   */
  public void setQuickInfoColor(String value) {
    m_QuickInfoColor = value;
  }

  /**
   * Returns the HTML color string for the quick info.
   *
   * @return		the HTML color string
   */
  public String getQuickInfoColor() {
    return m_QuickInfoColor;
  }

  /**
   * Sets the HTML font tag size string for the quick info.
   *
   * @param value	the HTML font tag size string
   */
  public void setQuickInfoSize(String value) {
    m_QuickInfoSize = value;
  }

  /**
   * Returns the HTML font tag size string for the quick info.
   *
   * @return		the HTML font tag size string
   */
  public String getQuickInfoSize() {
    return m_QuickInfoSize;
  }

  /**
   * Sets the HTML color string for the input/output information.
   *
   * @param value	the HTML color string
   */
  public void setInputOutputColor(String value) {
    m_InputOutputColor = value;
  }

  /**
   * Returns the HTML color string for the input/output information.
   *
   * @return		the HTML color string
   */
  public String getInputOutputColor() {
    return m_InputOutputColor;
  }

  /**
   * Sets the HTML font tag size string for the input/output information.
   *
   * @param value	the HTML font tag size string
   */
  public void setInputOutputSize(String value) {
    m_InputOutputSize = value;
  }

  /**
   * Returns the HTML font tag size string for the input/output information.
   *
   * @return		the HTML font tag size string
   */
  public String getInputOutputSize() {
    return m_InputOutputSize;
  }

  /**
   * Sets the HTML color string for the placeholders.
   *
   * @param value	the HTML color string
   */
  public void setPlaceholdersColor(String value) {
    m_PlaceholdersColor = value;
  }

  /**
   * Returns the HTML color string for the placeholders.
   *
   * @return		the HTML color string
   */
  public String getPlaceholdersColor() {
    return m_PlaceholdersColor;
  }

  /**
   * Sets the HTML font tag size string for the quick info.
   *
   * @param value	the HTML font tag size string
   */
  public void setPlaceholdersSize(String value) {
    m_PlaceholdersSize = value;
  }

  /**
   * Returns the HTML font tag size string for the placeholders.
   *
   * @return		the HTML font tag size string
   */
  public String getPlaceholdersSize() {
    return m_PlaceholdersSize;
  }

  /**
   * Sets the HTML background color string for the variable highlights.
   *
   * @param value	the HTML color string
   */
  public void setVariableHighlightBackground(String value) {
    m_VariableHighlightBackground = value;
  }

  /**
   * Returns the HTML background color string for the variable highlights.
   *
   * @return		the HTML color string
   */
  public String getVariableHighlightBackground() {
    return m_VariableHighlightBackground;
  }

  /**
   * Sets the HTML background color string for the bookmark highlights.
   *
   * @param value	the HTML color string
   */
  public void setBookmarkHighlightBackground(String value) {
    m_BookmarkHighlightBackground = value;
  }

  /**
   * Returns the HTML background color string for the bookmark highlights.
   *
   * @return		the HTML color string
   */
  public String getBookmarkHighlightBackground() {
    return m_BookmarkHighlightBackground;
  }

  /**
   * Sets whether to use nested format or objects when generating the "state".
   *
   * @param value	if true then the nested format is used
   * @see		#getState()
   */
  public void setStateUsesNested(boolean value) {
    m_StateUsesNested = value;
  }

  /**
   * Returns whether the nested format or the objects are used when generating
   * the "state" of the tree.
   *
   * @return		true if the nested format is used instead of objects
   */
  public boolean getStateUsesNested() {
    return m_StateUsesNested;
  }

  /**
   * Sets whether to ignore name changes of actors and don't prompt a dialog
   * with the user having the option to update the name throughout the glow.
   *
   * @param value	if true then name changes are ignored
   */
  public void setIgnoreNameChanges(boolean value) {
    m_IgnoreNameChanges = value;
  }

  /**
   * Returns whether name changes of actors are ignored and no dialog is
   * prompting the user whether to propagate the changes throughout the flow.
   *
   * @return		true if the name changes are ignored
   */
  public boolean getIgnoreNameChanges() {
    return m_IgnoreNameChanges;
  }

  /**
   * Sets whether to record the adding of actors in order to improve the
   * suggestions.
   *
   * @param value	true if to record
   */
  public void setRecordAdd(boolean value) {
    m_RecordAdd = value;
  }

  /**
   * Returns whether to record the adding of actors in order to improve the
   * suggestions.
   *
   * @return		true if to record
   */
  public boolean getRecordAdd() {
    return m_RecordAdd;
  }

  /**
   * Shortcut method for notifying model about node structure change.
   *
   * @param node	the node the triggered the structural change
   */
  public void nodeStructureChanged(Node node) {
    if (node != null)
      node.invalidateRendering();
    redraw(node);
  }

  /**
   * Returns the actor stored on the clipboard.
   *
   * @return		the actor or null if none available
   */
  public AbstractActor getActorFromClipboard() {
    AbstractActor	result;
    NestedConsumer	consumer;

    result = null;

    try {
      if (GUIHelper.canPasteStringFromClipboard()) {
	consumer = new NestedConsumer();
	consumer.setQuiet(true);
	result = (AbstractActor) consumer.fromString(GUIHelper.pasteSetupFromClipboard());
	consumer.cleanUp();
      }
    }
    catch (Exception ex) {
      result = null;
    }

    return result;
  }

  /**
   * Checks whether an actor can be pasted.
   *
   * @return		true if pasting is possible
   */
  protected boolean canPasteActor() {
    return (getActorFromClipboard() != null);
  }

  /**
   * Checks whether the specified actors can be removed, e.g., "cut" and placed
   * on the clipboard.
   *
   * @param paths	the paths to the actors
   * @return		true if the actors can be removed
   */
  protected boolean canRemoveActors(TreePath[] paths) {
    boolean	result;
    Node[]	nodes;
    Node	parent;

    result = (paths.length > 0);
    nodes  = TreeHelper.pathsToNodes(paths);
    for (Node node: nodes) {
      parent = (Node) node.getParent();
      result =    (parent != null)
               && (parent.getActor() instanceof MutableActorHandler);
      if (!result)
	break;
    }

    return result;
  }

  /**
   * Returns the state to be used for the popup menu.
   *
   * @param e		the event
   * @return		the state, null if no popup possible
   */
  protected StateContainer getTreeState(MouseEvent e) {
    // no path, no state
    if (getPathForLocation(e.getX(), e.getY()) == null)
      return null;

    return getTreeState(getSelectionPaths(), (Node) getPathForLocation(e.getX(), e.getY()).getLastPathComponent());
  }

  /**
   * Returns the state to be used for the popup menu or keyboard shortcuts.
   *
   * @param paths	the selected paths to work with
   * @param currNode	the currently active node
   * @return		the state
   */
  protected StateContainer getTreeState(TreePath[] paths, Node currNode) {
    StateContainer	result;

    result = new StateContainer();

    result.tree           = this;
    result.selPaths       = paths;
    result.numSel         = ((result.selPaths == null) ? 0 : result.selPaths.length);
    result.nodeAtMouseLoc = currNode;
    result.isSingleSel    = (result.numSel == 1);
    if (result.isSingleSel)
      result.selPath = result.selPaths[0];
    else
      result.selPath = null;
    if (result.numSel > 0) {
      result.selNode = TreeHelper.pathToNode(result.selPaths[0]);
      result.parent  = (Node) result.selNode.getParent();
    }
    else {
      result.selNode = null;
      result.parent  = null;
    }

    // is the node editable
    result.editable = isEditable() && result.nodeAtMouseLoc.isEditable();

    // can the node be deleted?
    result.canRemove = result.editable && (result.numSel > 0) && canRemoveActors(result.selPaths);

    // is clipboard content an actor?
    result.canPaste = result.editable && (result.numSel > 0) && canPasteActor();

    // mutable actor handlers?
    result.isMutable       = result.editable && (result.selNode != null) && (result.selNode.getActor() instanceof MutableActorHandler);
    result.isParentMutable = result.editable && (result.parent != null) && (result.parent.getActor() instanceof MutableActorHandler);

    // template related
    result.lastTemplate               = m_LastTemplate;
    result.lastTemplateInsertPosition = m_LastTemplateInsertPosition;
    
    // currently running flow
    result.runningFlow = null;
    if (getOwner().getRunningFlow() instanceof Flow)
      result.runningFlow = (FlowExecutionListeningSupporter) getOwner().getRunningFlow();

    return result;
  }

  /**
   * Shows a popup if possible for the given mouse event.
   *
   * @param e		the event
   */
  public void showNodePopupMenu(MouseEvent e) {
    JPopupMenu 	menu;

    menu = createNodePopupMenu(e);
    if (menu != null)
      menu.show(this, e.getX(), e.getY());
  }

  /**
   * Generates a popup if possible for the given mouse event.
   *
   * @param e		the event
   * @return		the popup menu, null if not possible
   */
  public JPopupMenu createNodePopupMenu(MouseEvent e) {
    JPopupMenu		menu;
    StateContainer	state;
    String[]		items;
    TreePopupAction	action;

    state = getTreeState(e);
    if (state == null)
      return null;

    menu  = new JPopupMenu();
    items = FlowEditorPanel.getPropertiesEditor().getProperty("Tree.PopupMenu", "").replace(" ", "").split(",");
    for (String item: items) {
      if (item.trim().length() == 0)
	continue;
      if (item.equals("-")) {
	menu.addSeparator();
      }
      else {
	try {
	  action = (TreePopupAction) Class.forName(item).newInstance();
	  action.update(state);
	  menu.add(action.getMenuItem());
	}
	catch (Exception ex) {
	  ConsolePanel.getSingleton().append(OutputType.ERROR, "Failed to instantiate tree popup menu item '" + item + "':\n" + Utils.throwableToString(ex));
	}
      }
    }

    return menu;
  }

  /**
   * Adds an undo point with the given comment.
   *
   * @param comment	the comment for the undo point
   */
  public void addUndoPoint(String comment) {
    if (getOwner() != null)
      getOwner().addUndoPoint("Saving undo data...", comment);
  }

  /**
   * Toggles the enabled state of actors.
   *
   * @param paths	the paths to the actors
   */
  public void toggleEnabledState(TreePath[] paths) {
    Node[]		nodes;
    AbstractActor	actor;
    int			i;

    nodes = TreeHelper.pathsToNodes(paths);
    if (nodes.length == 1)
      addUndoPoint("Toggling enabled state of " + nodes[0].getFullName());
    else
      addUndoPoint("Toggling enabled state of " + nodes.length + " actors");

    for (i = 0; i < nodes.length; i++) {
      actor = nodes[i].getActor();
      actor.setSkip(!actor.getSkip());
      nodes[i].setActor(actor);
      ((DefaultTreeModel) getModel()).nodeChanged(nodes[i]);
    }

    setModified(true);
    if (nodes.length == 1)
      notifyActorChangeListeners(new ActorChangeEvent(m_Self, nodes[0], Type.MODIFY));
    else
      notifyActorChangeListeners(new ActorChangeEvent(m_Self, nodes, Type.MODIFY_RANGE));
  }

  /**
   * Tries to figure what actors fit best in the tree at the given position.
   *
   * @param path	the path where to insert the actors
   * @param position	how the actors are to be inserted
   * @return		the actors
   */
  protected AbstractActor[] suggestActors(TreePath path, InsertPosition position) {
    AbstractActor[]	result;
    AbstractActor	parent;
    Node		parentNode;
    Node		node;
    int			pos;
    AbstractActor[]	actors;
    int			i;
    AbstractActor[]	suggestions;

    result = null;

    if (result == null) {
      if (position == InsertPosition.BENEATH) {
	parentNode = TreeHelper.pathToNode(path);
	pos        = parentNode.getChildCount();
      }
      else {
	node       = TreeHelper.pathToNode(path);
	parentNode = (Node) node.getParent();
	pos        = parentNode.getIndex(node);
	if (position == InsertPosition.AFTER)
	  pos++;
      }

      parent  = parentNode.getActor();
      actors  = new AbstractActor[parentNode.getChildCount()];
      for (i = 0; i < actors.length; i++)
	actors[i] = ((Node) parentNode.getChildAt(i)).getActor();

      suggestions = ActorSuggestion.getSingleton().suggest(parent, pos, actors);
      if (suggestions.length > 0)
	result = suggestions;
    }

    // default is "Filter"
    // TODO
    //if (result == null)
    //  result = ActorSuggestion.getSingleton().getDefaults();

    return result;
  }

  /**
   * Checks whether standalones can be placed beneath the parent actor.
   * If the actor isn't a standalone, this method returns true, of course.
   * In case the actor is a standalone and the parent doesn't allow standalones
   * to be placed, an error message pops up and informs the user.
   *
   * @param actor	the actor to place beneath the parent
   * @param parent	the parent to place the actor beneath
   * @return		true if actor can be placed, false if not
   */
  public boolean checkForStandalones(AbstractActor actor, Node parent) {
    return checkForStandalones(new AbstractActor[]{actor}, parent);
  }

  /**
   * Checks whether standalones can be placed beneath the parent actor.
   * If the actors contain no standalones, this method returns true, of course.
   * In case the actors contain a standalone and the parent doesn't allow standalones
   * to be placed, an error message pops up and informs the user.
   *
   * @param actors	the actors to place beneath the parent
   * @param parent	the parent to place the actor beneath
   * @return		true if actor can be placed, false if not
   */
  public boolean checkForStandalones(AbstractActor[] actors, Node parent) {
    for (AbstractActor actor: actors) {
      if (    ActorUtils.isStandalone(actor)
	  && (parent != null)
	  && (parent.getActor() instanceof ActorHandler)
	  && !((ActorHandler) parent.getActor()).getActorHandlerInfo().canContainStandalones()) {

	GUIHelper.showErrorMessage(
	    m_Self, "Actor '" + parent.getFullName() + "' cannot contain standalones!");
	return false;
      }
    }

    return true;
  }

  /**
   * Returns the nearest actor in the children of the provided parent node
   * that is not disabled.
   *
   * @param parent	the parent node to search
   * @param startIndex	the starting index in the children
   * @param forward	whether to search forward or backwards
   * @return		the nearest actor, null if none found
   */
  protected AbstractActor getNearestActor(Node parent, int startIndex, boolean forward) {
    AbstractActor	result;
    int			index;
    Node		child;
    AbstractActor	actor;

    result = null;

    if (parent.getChildCount() > 0) {
      index = startIndex;

      if (forward) {
	index++;
	while (index < parent.getChildCount()) {
	  child = (Node) parent.getChildAt(index);
	  actor = child.getActor();
	  if (actor.getSkip()) {
	    index++;
	    continue;
	  }
	  else {
	    result = actor;
	    break;
	  }
	}
      }
      else {
	index--;
	while (index >= 0) {
	  child = (Node) parent.getChildAt(index);
	  actor = child.getActor();
	  if (actor.getSkip()) {
	    index--;
	    continue;
	  }
	  else {
	    result = actor;
	    break;
	  }
	}
      }
    }

    return result;
  }

  /**
   * Configures a filter for the ClassTree.
   *
   * @param path	the path where to insert the actor
   * @param position	where to add the actor, if null "editing" an existing actor is assumed
   * @return		the configured filter
   */
  public AbstractItemFilter configureFilter(TreePath path, InsertPosition position) {
    ActorClassTreeFilter	result;
    AbstractActor		before;
    AbstractActor		after;
    AbstractActor		parent;
    Node			parentNode;
    Node			node;
    int				index;
    ActorHandlerInfo		handlerInfo;

    result      = new ActorClassTreeFilter();
    after       = null;
    before      = null;
    parentNode  = null;
    handlerInfo = null;

    // edit/update current actor
    if (position == null) {
      node       = TreeHelper.pathToNode(path);
      parentNode = (Node) node.getParent();
      if (parentNode != null) {
	parent = parentNode.getActor();
	if (parent instanceof MutableActorHandler) {
	  handlerInfo = ((ActorHandler) parent).getActorHandlerInfo();
	  if (handlerInfo.getActorExecution() == ActorExecution.SEQUENTIAL) {
	    index  = parentNode.getIndex(node);
	    before = getNearestActor(parentNode, index, false);
	    after  = getNearestActor(parentNode, index, true);
	  }
	}
      }
    }
    // add beneath
    else if (position == InsertPosition.BENEATH) {
      parentNode = TreeHelper.pathToNode(path);
      before     = getNearestActor(parentNode, parentNode.getChildCount(), false);
    }
    // add here
    else if (position == InsertPosition.HERE) {
      node       = TreeHelper.pathToNode(path);
      parentNode = (Node) node.getParent();
      index      = parentNode.getIndex(node);
      before     = getNearestActor(parentNode, index, false);
      after      = node.getActor();
      if (after.getSkip())
	after = getNearestActor(parentNode, index, true);
    }
    // add after
    else if (position == InsertPosition.AFTER) {
      node       = TreeHelper.pathToNode(path);
      parentNode = (Node) node.getParent();
      index      = parentNode.getIndex(node);
      after      = getNearestActor(parentNode, index, true);
      before     = node.getActor();
      if (before.getSkip())
	before = getNearestActor(parentNode, index, false);
    }

    if ((handlerInfo == null) && (parentNode != null)) {
      parent = parentNode.getActor();
      if (parent instanceof ActorHandler)
	handlerInfo = ((ActorHandler) parent).getActorHandlerInfo();
    }

    // check types
    if ((before != null) && !(before instanceof OutputProducer))
      before = null;
    if ((after != null) && !(after instanceof InputConsumer))
      after = null;

    // before/after only important for sequential execution
    if ((handlerInfo != null) && (handlerInfo.getActorExecution() != ActorExecution.SEQUENTIAL)) {
      before = null;
      after  = null;
    }

    // set constraints
    if (before != null)
      result.setAccepts(((OutputProducer) before).generates());
    else
      result.setAccepts(null);
    if (after != null)
      result.setGenerates(((InputConsumer) after).accepts());
    else
      result.setGenerates(null);

    // standalones?
    result.setStandalonesAllowed(false);
    if ((handlerInfo != null) && handlerInfo.canContainStandalones()) {
      // standalones can only be added at the start
      if ((before == null) || ((before != null) && ActorUtils.isStandalone(before)))
	result.setStandalonesAllowed(true);
    }

    // sources?
    result.setSourcesAllowed(false);
    if ((handlerInfo != null) && handlerInfo.canContainSource()) {
      // source can only be added at the start
      if ((before == null) || ((before != null) && ActorUtils.isStandalone(before)))
	result.setSourcesAllowed(true);
    }

    // restrictions?
    if ((handlerInfo != null) && handlerInfo.hasRestrictions())
      result.setRestrictions(handlerInfo.getRestrictions());

    return result;
  }
  
  /**
   * Brings up the GOE dialog for adding an actor if no actor supplied,
   * otherwise just adds the given actor at the position specified
   * by the path.
   *
   * @param path	the path to the actor to add the new actor sibling
   * @param actor	the actor to add, if null a GOE dialog is presented
   * @param position	where to insert the actor
   */
  public void addActor(TreePath path, AbstractActor actor, InsertPosition position) {
    addActor(path, actor, position, false);
  }

  /**
   * Brings up the GOE dialog for adding an actor if no actor supplied,
   * otherwise just adds the given actor at the position specified
   * by the path.
   *
   * @param path	the path to the actor to add the new actor sibling
   * @param actor	the actor to add, if null a GOE dialog is presented
   * @param position	where to insert the actor
   * @param record	whether to record the addition
   */
  public void addActor(TreePath path, AbstractActor actor, InsertPosition position, boolean record) {
    GenericObjectEditorDialog	dialog;
    Node			node;
    Node			parent;
    int				index;
    Node[]			children;
    AbstractActor[]		actors;
    String			txt;
    List<TreePath> 		exp;

    if (actor == null) {
      node = TreeHelper.pathToNode(path);
      if (position == InsertPosition.BENEATH)
	m_CurrentEditingParent = node;
      else
	m_CurrentEditingParent = (Node) node.getParent();
      dialog = GenericObjectEditorDialog.createDialog(this);
      if (position == InsertPosition.HERE)
	dialog.setTitle("Add here...");
      else if (position == InsertPosition.AFTER)
	dialog.setTitle("Add after...");
      else if (position == InsertPosition.BENEATH)
	dialog.setTitle("Add beneath...");
      actors = suggestActors(path, position);
      dialog.getGOEEditor().setCanChangeClassInDialog(true);
      dialog.getGOEEditor().setClassType(AbstractActor.class);
      dialog.getGOEEditor().setFilter(configureFilter(path, position));
      dialog.setProposedClasses(actors);
      if (actors != null)
	dialog.setCurrent(actors[0]);
      else
	dialog.setCurrent(null);
      dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
      dialog.setVisible(true);
      m_CurrentEditingParent = null;
      if (dialog.getResult() == GenericObjectEditorDialog.APPROVE_OPTION)
        addActor(path, (AbstractActor) dialog.getEditor().getValue(), position, record);
    }
    else {
      if (position == InsertPosition.BENEATH) {
	node = TreeHelper.pathToNode(path);

	// does actor handler allow standalones?
	if (actor instanceof ClipboardActorContainer)
	  actors = ((ClipboardActorContainer) actor).getActors();
	else
	  actors = new AbstractActor[]{actor};
	if (actors.length < 1)
	  return;
	if (!checkForStandalones(actors, node))
	  return;

	if (actors.length == 1)
	  txt = "'" + actors[0].getName() + "'";
	else
	  txt = actors.length + " actors";
	addUndoPoint("Adding " + txt + " to '" + node.getFullName() + "'");

	// add
	exp      = getExpandedNodes();
	children = buildTree(node, actors, true);
	for (Node child: children)
	  updateActorName(child);
	nodeStructureChanged(node);
	setExpandedNodes(exp);
	expand(node);
	
	// record
	if (m_RecordAdd && (actors.length == 1)) {
	  ActorSuggestion.getSingleton().record(
	      children[0], 
	      node, 
	      position);
	}
      }
      else {
	node   = TreeHelper.pathToNode(path);
	parent = (Node) node.getParent();
	index  = node.getParent().getIndex(node);
	if (position == InsertPosition.AFTER)
	  index++;

	// does actor handler allow standalones?
	if (actor instanceof ClipboardActorContainer)
	  actors = ((ClipboardActorContainer) actor).getActors();
	else
	  actors = new AbstractActor[]{actor};
	if (actors.length < 1)
	  return;
	if (!checkForStandalones(actors, parent))
	  return;

	if (actors.length == 1)
	  txt = "'" + actors[0].getName() + "'";
	else
	  txt = actors.length + " actors";
	if (position == InsertPosition.AFTER)
	  addUndoPoint("Adding " + txt + " after " + ((Node) parent.getChildAt(index - 1)).getFullName() + "'");
	else
	  addUndoPoint("Adding " + txt + " before " + ((Node) parent.getChildAt(index)).getFullName() + "'");

	// insert
	exp      = getExpandedNodes();
	children = buildTree(node, actors, false);
	for (Node child: children) {
	  parent.insert(child, index);
	  updateActorName(child);
	  index++;
	}
	nodeStructureChanged(parent);
	setExpandedNodes(exp);
	
	// record
	if (m_RecordAdd && (actors.length == 1)) {
	  ActorSuggestion.getSingleton().record(
	      children[0], 
	      parent, 
	      position);
	}
      }

      setModified(true);

      // notify listeners
      notifyActorChangeListeners(new ActorChangeEvent(m_Self, node, Type.MODIFY));
    }
  }

  /**
   * Adds the listener to the internal list of listeners.
   *
   * @param l		the listener to add
   */
  public void addActorChangeListener(ActorChangeListener l) {
    m_ActorChangeListeners.add(l);
  }

  /**
   * Removes the listener from the internal list of listeners.
   *
   * @param l		the listener to remove
   */
  public void removeActorChangeListener(ActorChangeListener l) {
    m_ActorChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners.
   *
   * @param e		the event to send
   */
  public void notifyActorChangeListeners(ActorChangeEvent e) {
    Iterator<ActorChangeListener>	iter;

    iter = m_ActorChangeListeners.iterator();
    while (iter.hasNext())
      iter.next().actorChanged(e);
  }

  /**
   * Sets the flow actor to display.
   *
   * @param value	the flow actor
   */
  public void setActor(AbstractActor value) {
    buildTree(value);
  }

  /**
   * Returns the underlying flow.
   * <p/>
   * WARNING: Recreates an actor hierarchy based on the tree. Method gets very
   * slow for large flows. If you only need the root actor, then use getRootActor()
   * instead.
   *
   * @return		the flow or null if none stored yet
   * @see		#getRootActor()
   * @see		Node#getFullActor()
   */
  public AbstractActor getActor() {
    return getActor(null);
  }

  /**
   * Returns the underlying flow.
   * <p/>
   * WARNING: Recreates an actor hierarchy based on the tree. Method gets very
   * slow for large flows. If you only need the root actor, then use getRootActor()
   * instead.
   *
   * @param errors	for storing any errors, use null to ignore
   * @return		the flow or null if none stored yet
   * @see		#getRootActor()
   * @see		Node#getFullActor()
   */
  public AbstractActor getActor(StringBuilder errors) {
    AbstractActor	result;

    result = null;

    if (getModel().getRoot() != null)
      result = ((Node) getModel().getRoot()).getFullActor(errors);

    return result;
  }

  /**
   * Returns the top-level actor (without any children).
   *
   * @return		the flow or null if none stored yet
   * @see		#getActor()
   */
  public AbstractActor getRootActor() {
    AbstractActor	result;

    result = null;

    if (getModel().getRoot() != null)
      result = ((Node) getModel().getRoot()).getActor();

    return result;
  }

  /**
   * Returns whether the actor is of type Flow or not.
   *
   * @return		true if actor is a Flow
   */
  public boolean isFlow() {
    if (getModel().getRoot() == null)
      return false;
    else
      return (((Node) getModel().getRoot()).getActor() instanceof Flow);
  }

  /**
   * Sets whether the tree is modified or not.
   *
   * @param value	true if tree is modified
   */
  public void setModified(boolean value) {
    m_Modified = value;
  }

  /**
   * Returns whether the tree is modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Sets the file this flow is associated with.
   *
   * @param value	the associated file, null if not associated with a file
   */
  public void setFile(File value) {
    m_File = value;
  }

  /**
   * Returns the file this flow is associated with.
   *
   * @return		the file, null if not associated with file
   */
  public File getFile() {
    return m_File;
  }

  /**
   * Returns the selected rows. Sorted from smallest to largest row.
   *
   * @return		the selected rows, 0-length array if none selected
   */
  @Override
  public int[] getSelectionRows() {
    int[]	result;

    result = super.getSelectionRows();
    if (result == null)
      result = new int[0];
    else
      Arrays.sort(result);

    return result;
  }

  /**
   * Returns the paths of all selected values. Sorted from smallest to largest
   * selection row.
   *
   * @return an array of <code>TreePath</code> objects indicating the selected
   *         nodes, or <code>empty array</code> if nothing is currently selected
   */
  @Override
  public TreePath[] getSelectionPaths() {
    TreePath[]	result;
    int[]	sel;
    int		i;

    sel    = getSelectionRows();
    result = new TreePath[sel.length];
    for (i = 0; i < sel.length; i++)
      result[i] = getPathForRow(sel[i]);

    return result;
  }

  /**
   * Returns the currently selected node.
   *
   * @return		the selected node, null if none selected
   */
  public Node getSelectedNode() {
    Node	result;

    result = null;

    if (getSelectionPath() != null)
      result = TreeHelper.pathToNode(getSelectionPath());

    return result;
  }

  /**
   * Returns the currently selected actor.
   *
   * @return		the selected actor, null if none selected
   */
  public AbstractActor getSelectedActor() {
    AbstractActor	result;
    Node		node;

    result = null;
    node   = getSelectedNode();

    if (node != null)
      result = node.getActor();

    return result;
  }

  /**
   * Returns the currently selected actors.
   *
   * @return		the selected actors, 0-length array if none selected
   */
  public AbstractActor[] getSelectedActors() {
    AbstractActor[]	result;

    if (getSelectionPaths() != null)
      result = TreeHelper.pathsToActors(getSelectionPaths());
    else
      result = new AbstractActor[0];

    return result;
  }

  /**
   * Returns the full name of the currently selected actor.
   *
   * @return		the full name of the selected actor, null if none selected
   */
  public String getSelectedFullName() {
    String	result;
    Node	node;

    result = null;
    node   = getSelectedNode();
    if (node != null)
      result = node.getFullName();

    return result;
  }

  /**
   * Tries to locate the node specified by the path parts.
   *
   * @param parent	the parent to start with
   * @param path	the path elements to traverse (below the parent)
   * @return		the located node or null if none found
   */
  protected Node locate(Node parent, ActorPath path) {
    Node		result;
    Node		child;
    int			index;
    int			i;

    result = null;

    index = -1;
    for (i = 0; i < parent.getChildCount(); i++) {
      child = (Node) parent.getChildAt(i);
      if (child.getActor().getName().equals(path.getFirstPathComponent())) {
	index = i;
	break;
      }
    }
    if (index != -1) {
      child = (Node) parent.getChildAt(index);
      if (path.getPathCount() == 1)
	result = child;
      else
	result = locate(child, path.getChildPath());
    }
    else {
      ConsolePanel.getSingleton().append(OutputType.ERROR, "Malformed path?");
    }

    return result;
  }

  /**
   * Locates the node in the tree based on the specified path.
   *
   * @param path	the path of the node to locate
   * @return		the located node or null if none found
   */
  public Node locate(String path) {
    Node	result;
    ActorPath	actorPath;
    Node	root;

    result = null;

    actorPath = new ActorPath(path);
    root      = (Node) getModel().getRoot();
    if (actorPath.getFirstPathComponent().equals(root.getActor().getName())) {
      if (actorPath.getPathCount() == 1)
	result = root;
      else
	result = locate(root, actorPath.getChildPath());
    }

    return result;
  }

  /**
   * Locates and selectes the node in the tree based on the specified path.
   *
   * @param path	the path of the node to locate
   */
  public void locateAndDisplay(String path) {
    Node	node;
    TreePath	tpath;

    node = locate(path);

    if (node != null) {
      tpath = getPath(node);
      setSelectionPath(tpath);
      scrollPathToVisible(tpath);
    }
  }

  /**
   * Searches for a node which is matches the "search" string. The search
   * string can be interpreted as regular expression as well.
   * A starting point (i.e., subtree) in the tree can be given as well.
   *
   * @param subtree	the starting point (= subtree) of the search, uses
   * 			the root node if null
   * @param last	the node returned from the last search, if not null
   * 			then a "find next" search is performed
   * @param search	the search string
   * @param isRegExp	whether the search string is a regular expression
   * @return		the path of the first matching node, otherwise null
   * 			if no match found
   */
  public Node find(Node subtree, Node last, String search, boolean isRegExp) {
    Node	result;
    Node	current;
    Enumeration	enm;

    result = null;

    if (!isRegExp)
      search = search.toLowerCase();

    // search whole tree?
    if (subtree == null)
      subtree = (Node) getModel().getRoot();

    enm = subtree.preorderEnumeration();

    // start from a specific node? -> skip nodes before this one
    if (last != null) {
      while (enm.hasMoreElements()) {
	current = (Node) enm.nextElement();
	if (current == last)
	  break;
      }
    }

    // perform search
    while (enm.hasMoreElements()) {
      current = (Node) enm.nextElement();
      if (isRegExp) {
	if (current.getActor().getName().toLowerCase().matches(search)) {
	  result = current;
	  break;
	}
      }
      else {
	if (current.getActor().getName().toLowerCase().indexOf(search) > -1) {
	  result = current;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Tries to find the node in the tree and returns the path to it.
   *
   * @param node	the node to look for
   * @return		the path if the node was found, null otherwise
   */
  public TreePath getPath(Node node) {
    return new TreePath(((DefaultTreeModel) getModel()).getPathToRoot(node));
  }

  /**
   * Searches for actor names in the tree.
   */
  public void find() {
    String	search;
    TreePath	path;
    Node	node;

    path = getSelectionPath();
    if (path != null)
      node = TreeHelper.pathToNode(path);
    else
      node = null;

    search = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(this),
	"Please enter the search string ("
	+ ((node == null) ? ("whole flow") : ("below '" + node.getActor().getName()) + "'") + "):",
	getLastSearchString());
    if (search == null)
      return;

    setLastSearchString(search);
    setLastSearchNode(find(node, null, getLastSearchString(), false));
    if (getLastSearchNode() == null) {
      GUIHelper.showErrorMessage(
	  m_Self, "Search string '" + getLastSearchString() + "' not found!");
    }
    else {
      path = getPath(getLastSearchNode());
      setSelectionPath(path);
      scrollPathToVisible(path);
    }
  }

  /**
   * Searches for the next actor in the tree.
   */
  public void findNext() {
    TreePath	path;

    setLastSearchNode(find(null, getLastSearchNode(), getLastSearchString(), false));
    if (getLastSearchNode() == null) {
      GUIHelper.showErrorMessage(
	  m_Self, "Search string '" + getLastSearchString() + "' not found!");
    }
    else {
      path = getPath(getLastSearchNode());
      setSelectionPath(path);
      scrollPathToVisible(path);
    }
  }

  /**
   * Sets the last search string in use.
   *
   * @param value	the search string
   */
  public void setLastSearchString(String value) {
    m_LastSearchString = value;
  }

  /**
   * Returns the last search string in use.
   *
   * @return		the search string
   */
  public String getLastSearchString() {
    return m_LastSearchString;
  }

  /**
   * Sets the node that was found in the last search.
   *
   * @param value	the node, can be null if no search performed yet or
   * 			last search unsuccessful
   */
  public void setLastSearchNode(Node value) {
    m_LastSearchNode = value;
  }

  /**
   * Returns the node that was found in the last search.
   *
   * @return		the node, can be null if no search performed yet or
   * 			last search unsuccessful
   */
  public Node getLastSearchNode() {
    return m_LastSearchNode;
  }

  /**
   * Sets whether to show the quick info or not.
   *
   * @param value	if true then the quick info will be displayed
   */
  public void setShowQuickInfo(boolean value) {
    m_ShowQuickInfo = value;
    nodeStructureChanged((Node) getModel().getRoot());
  }

  /**
   * Returns whether the quick info is shown or not.
   *
   * @return		true if the quick info is shown
   */
  public boolean getShowQuickInfo() {
    return m_ShowQuickInfo;
  }

  /**
   * Sets whether to show the annotations or not.
   *
   * @param value	if true then the annotations will be displayed
   */
  public void setShowAnnotations(boolean value) {
    m_ShowAnnotations = value;
    nodeStructureChanged((Node) getModel().getRoot());
  }

  /**
   * Returns whether the annotations are shown or not.
   *
   * @return		true if the annotations are shown
   */
  public boolean getShowAnnotations() {
    return m_ShowAnnotations;
  }

  /**
   * Sets whether to show the input/output information or not.
   *
   * @param value	if true then the input/output information will be displayed
   */
  public void setShowInputOutput(boolean value) {
    m_ShowInputOutput = value;
    nodeStructureChanged((Node) getModel().getRoot());
  }

  /**
   * Returns whether the input/output information is shown or not.
   *
   * @return		true if the input/output information is shown
   */
  public boolean getShowInputOutput() {
    return m_ShowInputOutput;
  }

  /**
   * Sets the class name prefixes to remove from the input/output info.
   *
   * @param value	the prefixes
   */
  public void setInputOutputPrefixes(String[] value) {
    m_InputOutputPrefixes = value.clone();
  }

  /**
   * Returns the class name prefixes to remove from the input/output info.
   *
   * @return		the prefixes
   */
  public String[] getInputOutputPrefixes() {
    return m_InputOutputPrefixes;
  }

  /**
   * Sets the scale factor for the icons.
   *
   * @param value	the scale factor (1.0 is actual size)
   */
  public void setIconScaleFactor(double value) {
    if (value != ((Renderer) getCellRenderer()).getScaleFactor())
      setCellRenderer(new Renderer(value));
  }

  /**
   * Returns the scale factor for the icons.
   *
   * @return		the scale factor (1.0 is actual size)
   */
  public double getIconScaleFactor() {
    return ((Renderer) getCellRenderer()).getScaleFactor();
  }

  /**
   * Sets the current state of the tree (tree, expansions, modified).
   *
   * @param value	the state to use
   */
  public void setState(Vector value) {
    AbstractActor	actor;
    boolean[]		expanded;
    Boolean		modified;
    NestedConsumer	consumer;
    File		file;

    if (m_StateUsesNested) {
      if (value.get(0) != null) {
	consumer = new NestedConsumer();
	consumer.setInput((List) value.get(0));
	actor = (AbstractActor) consumer.consume();
	consumer.cleanUp();
      }
      else {
	actor = null;
      }
    }
    else {
      actor = (AbstractActor) value.get(0);
    }
    expanded = (boolean[]) value.get(1);
    modified = (Boolean) value.get(2);
    file     = (File) value.get(3);

    setActor(actor);
    setExpandedState(expanded);
    setModified(modified);
    setFile(file);
  }

  /**
   * Returns the current state of the tree (tree, expansions, modified).
   * Is used for undo/redo.
   *
   * @return		the current state
   */
  public Vector getState() {
    Vector		result;
    AbstractActor	actor;
    NestedProducer	producer;

    result = new Vector();

    actor = getActor();
    if (m_StateUsesNested) {
      if (actor == null) {
	result.add(null);
      }
      else {
	producer = new NestedProducer();
	producer.produce(actor);
	result.add(producer.getOutput());
	actor.destroy();
	producer.cleanUp();
      }
    }
    else {
      result.add(actor);
    }
    result.add(getExpandedState());
    result.add(isModified());
    result.add(getFile());

    return result;
  }

  /**
   * Returns the string for the specified action.
   *
   * @param action	the action to get the string for
   * @return		the caption
   */
  @Override
  protected String getDropMenuActionCaption(DropMenu action) {
    if (action == DropMenu.ADD)
      return "Add actor";
    else if (action == DropMenu.MOVE)
      return "Move actor";
    else
      return super.getDropMenuActionCaption(action);
  }

  /**
   * Returns the icon for the drop action.
   *
   * @param action	the action to get the icon for
   * @return		the icon or null if none available
   */
  @Override
  protected ImageIcon getDropMenuActionIcon(DropMenu action) {
    if (action == DropMenu.ADD)
      return GUIHelper.getIcon("flow.gif");
    else if (action == DropMenu.CANCEL)
      return GUIHelper.getIcon("delete.gif");
    else
      return super.getDropMenuActionIcon(action);
  }

  /**
   * Returns whether dragging is enabled.
   *
   * @return		always true
   */
  @Override
  protected boolean isDragEnabled() {
    return true;
  }

  /**
   * Returns whether dropping is enabled.
   *
   * @return		always true
   */
  @Override
  protected boolean isDropEnabled() {
    return true;
  }

  /**
   * Checks whether the source node can be dragged at all.
   * <p/>
   * The parent of the source node must a MutableActorHandler
   *
   * @param source	the source node that is about to be dragged
   * @return		true if the source node can be dragged
   */
  protected boolean canStartDrag(BaseTreeNode source) {
    return    (source.getParent() != null)
           && (((Node) source.getParent()).getActor() instanceof MutableActorHandler);
  }

  /**
   * Checks whether the source can be dropped here.
   *
   * @param source	the source node
   * @param target	the target node
   * @param position	where to drop the data
   * @return		true if can be dropped
   */
  @Override
  protected boolean canDrop(Transferable source, TreeNode target, DropPosition position) {
    boolean	result;
    Node	parent;

    result = super.canDrop(source, target, position);

    if (result) {
      parent = (Node) target.getParent();
      if (position == DropPosition.BENEATH)
	result = (((Node) target).getActor() instanceof MutableActorHandler);
      else if (position == DropPosition.AFTER)
	result = (parent != null) && (parent.getActor() instanceof MutableActorHandler);
      else if (position == DropPosition.HERE)
	result = (parent != null) && (parent.getActor() instanceof MutableActorHandler);
    }

    return result;
  }

  /**
   * Highlights all the nodes that have a variable that match the regular
   * expression on the variable name.
   *
   * @param parent	the parent to start the search in
   * @param nameRegExp	the regular expression to match variable names against
   */
  protected void highlightVariables(Node parent, String nameRegExp) {
    Node	child;
    int		i;

    parent.findVariable(nameRegExp);

    for (i = 0; i < parent.getChildCount(); i++) {
      child = (Node) parent.getChildAt(i);
      child.findVariable(nameRegExp);
      highlightVariables(child, nameRegExp);
    }
  }

  /**
   * Highlights all the nodes that have a variable that match the regular
   * expression on the variable name.
   *
   * @param nameRegExp	the regular expression to match variable names against
   */
  public void highlightVariables(String nameRegExp) {
    highlightVariables((Node) getModel().getRoot(), nameRegExp);
    ((Node) getModel().getRoot()).invalidateRendering();
    treeDidChange();
  }

  /**
   * Enables/diables all breakpoint actors.
   *
   * @param parent	the parent node to start recursion from
   * @param enable	if true all breakpoint actors get enabled, otherwise disabled
   */
  protected void enableBreakpoints(Node parent, boolean enable) {
    int		i;

    if (parent.getActor() instanceof Breakpoint)
      parent.getActor().setSkip(!enable);

    for (i = 0; i < parent.getChildCount(); i++)
      enableBreakpoints((Node) parent.getChildAt(i), enable);
  }

  /**
   * Enables/diables all breakpoint actors.
   *
   * @param enable	if true all breakpoint actors get enabled, otherwise disabled
   */
  public void enableBreakpoints(boolean enable) {
    enableBreakpoints((Node) getModel().getRoot(), enable);
    treeDidChange();
  }
  
  /**
   * Restores the expanded state of the tree. Use this method instead of
   * {@link #setExpandedNodes(List)} if the tree has been rebuilt in the
   * meantime. This method uses the actor names to locate them in the tree.
   * This means, that this method is more expensive.
   * 
   * @param exp		the list of expanded nodes
   * @return		true if successfully restored
   * @see		#setExpandedNodes(List)
   */
  public boolean restoreExpandedNodes(List<TreePath> expanded) {
    List<TreePath>	exp;
    String		full;
    Node		node;
    
    if (expanded == null)
      return false;
    
    exp = new ArrayList<>();
    for (TreePath path: expanded) {
      full = TreeHelper.pathToNode(path).getFullName();
      node = locate(full);
      if (node != null)
	exp.add(new TreePath(node.getPath()));
    }
    
    setExpandedNodes(exp);
    
    return true;
  }

  /**
   * Processes the specified actor with a user-specified actor processor
   * (prompts user with GOE dialog).
   * NB: The options of the specified actor will get processed.
   *
   * @param path	the path of the actor, if null the root actor is used
   * @return		true if actors processed
   */
  public boolean processActor(TreePath path) {
    return processActor(path, null);
  }

  /**
   * Processes the specified actor with the specified actor processor.
   * NB: The options of the specified actor will get processed.
   *
   * @param path	the path of the actor, if null the root actor is used
   * @param processor	the processor to use, null if to prompt user
   * @return		true if actors processed
   */
  public boolean processActor(TreePath path, AbstractActorProcessor processor) {
    ModifyingProcessor			modifying;
    GraphicalOutputProducingProcessor	graphical;
    BaseDialog				dialog;
    final BaseDialog			fDialog;
    Node				node;
    AbstractActor			flow;
    AbstractActor			selected;
    Node				newNode;
    Node				parent;
    int					index;
    final Component			comp;
    ErrorMessagePanel			errorPanel;
    final ErrorMessagePanel		fErrorPanel;
    final BaseTabbedPane		tabbedPane;
    List<TreePath>			exp;

    // selected actor or full flow?
    flow = getActor();
    if ((path != null) && (path.getPathCount() == 1))
      path = null;
    if (path == null) {
      selected = flow;
      node     = (Node) getModel().getRoot();
    }
    else {
      selected = ActorUtils.locate(TreeHelper.treePathToActorPath(path).getChildPath(), flow);
      node     = TreeHelper.pathToNode(path);
    }

    // prompt for processor?
    if (processor == null) {
      if (m_DialogProcessActors == null) {
	if (getParentDialog() != null)
	  m_DialogProcessActors = new GenericObjectEditorDialog(getParentDialog());
	else
	  m_DialogProcessActors = new GenericObjectEditorDialog(getParentFrame());
	m_DialogProcessActors.setTitle("Process actors");
	m_DialogProcessActors.setModalityType(ModalityType.DOCUMENT_MODAL);
	m_DialogProcessActors.getGOEEditor().setCanChangeClassInDialog(true);
	m_DialogProcessActors.getGOEEditor().setClassType(AbstractActorProcessor.class);
	m_DialogProcessActors.setCurrent(new RemoveDisabledActors());
      }
      m_DialogProcessActors.setLocationRelativeTo(GUIHelper.getParentComponent(this));
      m_DialogProcessActors.setVisible(true);

      if (m_DialogProcessActors.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return false;

      processor = (AbstractActorProcessor) m_DialogProcessActors.getCurrent();
    }

    // process
    if (processor instanceof ModifyingProcessor)
      ((ModifyingProcessor) processor).setNoCopy(true);
    processor.process(selected);

    // modified?
    if (processor instanceof ModifyingProcessor) {
      modifying = (ModifyingProcessor) processor;
      if (modifying.isModified()) {
	addUndoPoint("Processing actors with " + processor.toString());
	exp = getExpandedNodes();
	if (path == null) {
	  buildTree(modifying.getModifiedActor());
	  newNode = node;
	}
	else {
	  newNode = buildTree((Node) node.getParent(), modifying.getModifiedActor(), false);
	  parent  = (Node) node.getParent();
	  index   = parent.getIndex(node);
	  parent.remove(index);
	  parent.insert(newNode, index);
	}
	setModified(true);
	nodeStructureChanged(newNode);
	restoreExpandedNodes(exp);
	notifyActorChangeListeners(new ActorChangeEvent(this, newNode, Type.MODIFY));
      }
    }

    // any errors?
    if (processor.hasErrors()) {
      errorPanel = new ErrorMessagePanel();
      errorPanel.setErrorMessage(Utils.flatten(processor.getErrors(), "\n"));
    }
    else {
      errorPanel = null;
    }

    // graphical output?
    if (processor instanceof GraphicalOutputProducingProcessor) {
      graphical = (GraphicalOutputProducingProcessor) processor;
      if (graphical.hasGraphicalOutput()) {
	if (getParentDialog() != null)
	  dialog = new BaseDialog(getParentDialog());
	else
	  dialog = new BaseDialog(getParentFrame());
	dialog.setTitle(processor.getClass().getSimpleName());
	dialog.getContentPane().setLayout(new BorderLayout());
	comp = graphical.getGraphicalOutput();
	if (errorPanel == null) {
	  dialog.getContentPane().add(comp, BorderLayout.CENTER);
	  if (comp instanceof MenuBarProvider)
	    dialog.setJMenuBar(((MenuBarProvider) comp).getMenuBar());
	}
	else {
	  fDialog     = dialog;
	  fErrorPanel = errorPanel;
	  tabbedPane  = new BaseTabbedPane();
	  tabbedPane.addChangeListener(new ChangeListener() {
	    @Override
	    public void stateChanged(ChangeEvent e) {
	      if (tabbedPane.getSelectedIndex() == 0) {
		if (comp instanceof MenuBarProvider)
		  fDialog.setJMenuBar(((MenuBarProvider) comp).getMenuBar());
		else
		  fDialog.setJMenuBar(null);
	      }
	      else {
		fDialog.setJMenuBar(fErrorPanel.getMenuBar());
	      }
	    }
	  });
	  dialog.getContentPane().add(tabbedPane, BorderLayout.CENTER);
	  tabbedPane.addTab("Output", comp);
	  tabbedPane.addTab("Errors", errorPanel);
	}
	if (comp instanceof MenuBarProvider)
	  dialog.setJMenuBar(((MenuBarProvider) comp).getMenuBar());
	dialog.pack();
	dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
	dialog.setVisible(true);
	errorPanel = null;
      }
    }

    // errors still to display?
    if (errorPanel != null) {
      if (getParentDialog() != null)
	dialog = new BaseDialog(getParentDialog());
      else
	dialog = new BaseDialog(getParentFrame());
      dialog.setTitle(processor.getClass().getSimpleName());
      dialog.getContentPane().setLayout(new BorderLayout());
      dialog.getContentPane().add(errorPanel, BorderLayout.CENTER);
      dialog.setJMenuBar(errorPanel.getMenuBar());
      dialog.pack();
      dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
      dialog.setVisible(true);
    }

    return true;
  }

  /**
   * Sets the tree editable or read-only. Also, forces the tree to redraw.
   *
   * @param value	if false the tree will be read-only
   */
  @Override
  public void setEditable(boolean value) {
    super.setEditable(value);
    if (getParent() != null) {
      getParent().invalidate();
      getParent().repaint();
    }
  }

  /**
   * Returns the node currently being edited.
   *
   * @return		the node, null if none being edited
   */
  public Node getCurrentEditingNode() {
    return m_CurrentEditingNode;
  }

  /**
   * Returns the parent of the node currently being edited or being added.
   *
   * @return		the node, null if none being edited/added
   */
  public Node getCurrentEditingParent() {
    return m_CurrentEditingParent;
  }

  /**
   * Updates the current editing position.
   * 
   * @param parent	the parent of the current node
   * @param node	the current node
   */
  public void updateCurrentEditing(Node parent, Node node) {
    m_CurrentEditingParent = parent;
    m_CurrentEditingNode   = node;
  }
  
  /**
   * Creates a new collection for transfer.
   *
   * @param nodes	the nodes to package
   * @return		the new collection
   */
  @Override
  protected DragAndDropTreeNodeCollection newNodeCollection(BaseTreeNode[] nodes) {
    Node[]	nnodes;
    int		i;

    nnodes = new Node[nodes.length];
    for (i = 0; i < nodes.length; i++)
      nnodes[i] = (Node) nodes[i];

    return new TreeNodeCollection(nnodes);
  }

  /**
   * Creates a new TreeNode for this tree.
   *
   * @param data	the data to use
   * @return		the new nodes
   */
  @Override
  protected BaseTreeNode[] newTreeNodes(Transferable data) {
    TreeNodeCollection	coll;

    coll = TreeNodeCollection.fromTransferable(this, data);

    if (coll == null)
      return new BaseTreeNode[0];
    else
      return coll.toArray(new Node[coll.size()]);
  }

  /**
   * Refreshes the tabs.
   */
  public void refreshTabs() {
    if (getEditor() != null)
      getEditor().refreshTabs();
  }
  
  /**
   * Updates the last template that was used.
   * 
   * @param template	the template
   * @param position	how the template was inserted
   */
  public void updateLastTemplate(AbstractActorTemplate template, InsertPosition position) {
    m_LastTemplate               = template;
    m_LastTemplateInsertPosition = position;
  }
}