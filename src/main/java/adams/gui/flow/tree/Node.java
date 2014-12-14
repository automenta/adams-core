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
 * Node.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree;

import java.awt.datatransfer.Transferable;
import java.util.HashSet;
import java.util.List;

import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.base.BaseAnnotation.Tag;
import adams.core.net.HtmlUtils;
import adams.core.option.NestedConsumer;
import adams.core.option.NestedProducer;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.flow.core.ExternalActorHandler;
import adams.flow.core.InputConsumer;
import adams.flow.core.MutableActorHandler;
import adams.flow.core.OutputProducer;
import adams.gui.core.GUIHelper;
import adams.gui.core.LazyExpansionTreeNode;
import adams.gui.core.TransferableString;

/**
 * A custom tree node for actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9037 $
 */
public class Node
  extends LazyExpansionTreeNode
  implements Destroyable {

  /** for serialization. */
  private static final long serialVersionUID = 8994468228049539761L;

  /** the owning tree. */
  protected Tree m_Owner;

  /** the variables. */
  protected HashSet<String> m_Variables;

  /** the regular expression of the variable to highlight. */
  protected String m_VariableSearch;

  /** whether the searched for variable has been found in this actor. */
  protected Boolean m_VariableFound;

  /** the render string. */
  protected String m_RenderString;
  
  /** whether the node is editable. */
  protected boolean m_Editable;

  /** whether the node is currently bookmarked. */
  protected boolean m_Bookmarked;

  /**
   * Initializes the node.
   *
   * @param owner	the owning tree
   * @param actor	the underlying actor
   */
  public Node(Tree owner, AbstractActor actor) {
    super(strip(actor));

    m_Owner             = owner;
    m_VariableSearch    = null;
    m_RenderString      = null;
    m_Editable          = true;
    m_Bookmarked        = false;
  }

  /**
   * Sets the user object for this node to <code>userObject</code>.
   *
   * @param userObject	the Object that constitutes this node's
   * 			user-specified data
   */
  @Override
  public void setUserObject(Object userObject) {
    m_Variables     = null;
    m_VariableFound = null;
    m_RenderString  = null;
    super.setUserObject(userObject);
  }

  /**
   * Returns the tree the node belongs to.
   * 
   * @return		the tree
   */
  public Tree getOwner() {
    return m_Owner;
  }
  
  /**
   * Sets the actor. Strips it down before using it.
   *
   * @param value	the actor
   */
  public void setActor(AbstractActor value) {
    AbstractActor	oldActor;
    AbstractActor	parent;
    AbstractActor	stripped;

    stripped = strip(value);
    oldActor = getActor();
    if (oldActor != null) {
      parent = oldActor.getParent();
      stripped.setParent(parent);
    }
    else {
      System.err.println("No old actor stored?");
    }

    setUserObject(stripped);

    if (oldActor != null)
      oldActor.destroy();
  }

  /**
   * Returns the stored (stripped down) actor.
   *
   * @return		the actor
   */
  public AbstractActor getActor() {
    return (AbstractActor) getUserObject();
  }

  /**
   * Returns the full name of the node (up to the root).
   *
   * @return		the full name
   */
  public String getFullName() {
    StringBuilder	result;
    Node		parent;
    Node		child;

    result = new StringBuilder(getActor().getName().replace(".", "\\."));
    child  = this;
    parent = (Node) child.getParent();
    do {
      if (parent != null) {
	result.insert(0, parent.getActor().getName().replace(".", "\\.") + ".");
	child  = parent;
	parent = (Node) child.getParent();
      }
    }
    while (parent != null);

    return result.toString();
  }

  /**
   * Returns the full actor, i.e., including all possible sub-actors.
   *
   * @return		the generated actor
   */
  public AbstractActor getFullActor() {
    return getFullActor(null);
  }

  /**
   * Returns the full actor, i.e., including all possible sub-actors.
   *
   * @param errors	for appending errors encountered while assembling, use null to ignore
   * @return		the generated actor
   */
  public AbstractActor getFullActor(StringBuilder errors) {
    AbstractActor	result;
    Node		child;
    int			i;
    boolean		mutable;
    ActorHandler	handler;
    String		msg;

    result  = getActor().shallowCopy();
    mutable = (result instanceof MutableActorHandler);

    // remove all children
    if (mutable) {
      while (((MutableActorHandler) result).size() > 0)
	((MutableActorHandler) result).remove(0);
    }

    // set/add children
    if (result instanceof ActorHandler) {
      handler = (ActorHandler) result;
      for (i = 0; i < getChildCount(); i++) {
	child = (Node) getChildAt(i);
	try {
	  if (mutable)
	    ((MutableActorHandler) handler).add(child.getFullActor(errors));
	  else
	    handler.set(i, child.getFullActor(errors));
	}
	catch (Exception e) {
	  msg = "Failed to add '" + child.getActor().getName() + "' to '" + handler.getName() + "':";
	  if (errors != null)
	    errors.append(msg + "\n" + Utils.throwableToString(e) + "\n");
	  System.err.println(msg);
	  e.printStackTrace();
	}
      }
    }

    return result;
  }

  /**
   * Returns whether this node can be removed or not. E.g., the elements of
   * non-mutable ActorHandlers.
   *
   * @return		true if removeable
   */
  public boolean isRemovable() {
    boolean		result;

    result = false;

    if (getActor().getParent() != null)
      result = (getActor().getParent() instanceof MutableActorHandler);

    return result;
  }

  /**
   * Replaces an object in the tree (ie, the actors), recursion is optional.
   *
   * @param find	the object to find
   * @param replace	the replacement
   * @param recursive	whether to recurse deeper in the tree
   * @return		true if something got replaced
   */
  public int replace(Comparable find, Comparable replace, boolean recursive) {
    int			result;
    int			i;
    Node		child;
    HashSet<Class>	excluded;

    excluded = new HashSet<>();
    excluded.add(AbstractActor.class);

    result = ActorUtils.replace(getActor(), find, replace, recursive, excluded);

    if (recursive) {
      for (i = 0; i < getChildCount(); i++) {
	child  = (Node) getChildAt(i);
	result += child.replace(find, replace, true);
      }
    }

    return result;
  }

  /**
   * Sets the search string for variables.
   *
   * @param nameRegExp	the regular expression for the name
   */
  public void findVariable(String nameRegExp) {
    m_VariableSearch = nameRegExp;
    m_VariableFound  = null;
  }

  /**
   * Checks whether the actor has any variables that match the regular
   * expression on the name.
   *
   * @see		#m_VariableSearch
   * @see		#m_VariableFound
   */
  protected void updateVariableSearch() {
    // already searched?
    if (m_VariableFound != null)
      return;

    // no search string?
    if (m_VariableSearch == null) {
      m_VariableFound = false;
      return;
    }

    if (m_Variables == null)
      m_Variables = getActor().findVariables();

    m_VariableFound = false;

    for (String var: m_Variables) {
      if (var.matches(m_VariableSearch)) {
	m_VariableFound = true;
	break;
      }
    }
  }

  /**
   * Turns the node/userObject into a transferable string.
   *
   * @return		the generated string
   */
  @Override
  public Transferable toTransferable() {
    return new TransferableString(getFullActor().toCommandLine());
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  public void destroy() {
    m_Owner = null;
    getActor().destroy();
  }

  /**
   * Turns a class array (from input/output arrays) into a string.
   *
   * @param classes	the classes to turn into a list
   * @return		the generated list string
   */
  protected String classArrayToString(Class[] classes) {
    StringBuilder	result;
    int			i;
    int			n;
    Class		cls;
    String		name;
    String		prefix;

    result = new StringBuilder();

    for (i = 0; i < classes.length; i++) {
      if (i > 0)
	result.append(", ");
      cls = classes[i];
      if (classes[i].isArray())
	cls = cls.getComponentType();
      name = cls.getName();

      // remove common prefixes
      for (n = 0; n < m_Owner.getInputOutputPrefixes().length; n++) {
	prefix = m_Owner.getInputOutputPrefixes()[n];
	if (name.startsWith(prefix))
	  name = name.replace(prefix, "");
      }

      result.append(name);
      if (classes[i].isArray())
	result.append("[]");
    }

    if (result.length() == 0)
      result.append("-none-");

    return result.toString();
  }

  /**
   * Scales the HTML font size using the icon scaling factor of the tree.
   *
   * @param size	the HTML font size string
   * @return		the new font size
   */
  protected String scaleFontSize(String size) {
    int		newSize;

    try {
      size = size.trim();
      if (size.startsWith("+"))
	newSize = 3 + Integer.parseInt(size.substring(1));
      else if (size.startsWith("-"))
	newSize = 3 - Integer.parseInt(size.substring(1));
      else
	newSize = Integer.parseInt(size);
      newSize = (int) (newSize * m_Owner.getIconScaleFactor());
      if (newSize < 1)
	newSize = 1;
      if (newSize > 7)
	newSize = 7;
      if (newSize > 3)
	return "+" + (newSize - 3);
      else if (newSize < 3)
	return "-" + (3 - newSize);
      else
	return "" + newSize;
    }
    catch (Exception e) {
      return size;
    }
  }

  /**
   * Inserts line breaks.
   * 
   * @param s		the string to process
   * @return		the updated string
   */
  protected String insertLineBreaks(String s) {
    StringBuilder	result;
    String[]		lines;
    int			i;
    int			n;
    String		line;
    boolean		trailingLF;

    result     = new StringBuilder();
    trailingLF = s.endsWith("\n");
    lines  = s.split("\n");
    result = new StringBuilder();
    for (i = 0; i < lines.length; i++) {
      if (lines[i].startsWith(" ")) {
	line = lines[i].trim();
	for (n = 0; n < lines[i].length() - line.length(); n++)
	  line = "&nbsp;" + HtmlUtils.toHTML(line);
      }
      else {
	line = HtmlUtils.toHTML(lines[i]);
      }
      if (i > 0)
	result.append("<br>");
      result.append(line);
    }
    
    if (trailingLF)
      result.append("<br>");
    
    return result.toString();
  }
  
  /**
   * Assembles the annotation HTML string.
   * 
   * @param actor	the actor to obtain the annotation from
   * @return		the generated string
   */
  protected String assembleAnnotation(AbstractActor actor) {
    StringBuilder	result;
    String		colorDef;
    String		sizeDef;
    String		color;
    String		size;
    boolean		font;
    List		parts;
    Tag			tag;
    
    result   = new StringBuilder();
    colorDef = m_Owner.getAnnotationsColor();
    sizeDef  = m_Owner.getAnnotationsSize();
    
    if (actor.getAnnotations().hasTag()) {
      font  = false;
      parts = actor.getAnnotations().getParts();
      for (Object part: parts) {
	if (part instanceof Tag) {
	  tag   = (Tag) part;
	  color = colorDef;
	  size  = sizeDef;
	  if (tag.getOptions().containsKey("color"))
	    color = tag.getOptions().get("color");
	  if (tag.getOptions().containsKey("size"))
	    size = tag.getOptions().get("size");
	  if (font)
	    result.append("</font>");
	  result.append("<font size='" + scaleFontSize(size) + "' color='" + color + "'>");
	  result.append(tag.getName());
	  font = true;
	}
	else {
	  if (!font)
	    result.append("<font size='" + scaleFontSize(sizeDef) + "' color='" + colorDef + "'>");
	  result.append(insertLineBreaks(part.toString()));
	  font = true;
	}
      }
      result.append("</font>");
    }
    else {
      result.append("<font size='" + scaleFontSize(sizeDef) + "' color='" + colorDef + "'>");
      result.append(insertLineBreaks(actor.getAnnotations().getValue()));
      result.append("</font>");
    }
    
    return result.toString();
  }
  
  /**
   * Returns the actor in HTML.
   *
   * @return		the HTML description
   */
  @Override
  public String toString() {
    StringBuilder	html;
    String		quickInfo;
    AbstractActor	actor;

    if (m_RenderString == null) {
      html = new StringBuilder();

      actor = getActor();
      if (actor == null) {
	html.append("<b>[none]</b>");
      }
      else {
	html.append(
	    "<font size='" + scaleFontSize(m_Owner.getActorNameSize()) + "' color='" + m_Owner.getActorNameColor() + "'>"
	    + HtmlUtils.toHTML(actor.getName()) + "</font>");

	// skip this actor?
	if (actor.getSkip()) {
	  html.insert(0, "<s>");
	  html.append("</s>");
	}
	else if (actor.getOptionManager().getVariableForProperty("skip") != null) {
	  html.insert(0, "<u>");
	  html.append("</u>");
	}

	// show input?
	if (m_Owner.getShowInputOutput() && (actor instanceof InputConsumer)) {
	  html.insert(
	      0,
	      "<font size='" + scaleFontSize(m_Owner.getInputOutputSize()) + "' color='" + m_Owner.getInputOutputColor() + "'>"
	      + HtmlUtils.toHTML(classArrayToString(((InputConsumer) actor).accepts())) + "</font>"
	      + "<br>");
	}

	// quick info available?
	if (m_Owner.getShowQuickInfo()) {
	  quickInfo = actor.getQuickInfo();
	  if ((quickInfo != null) && (quickInfo.trim().length() > 0)) {
	    html.append("&nbsp;&nbsp;"
		+ "<font size='" + scaleFontSize(m_Owner.getQuickInfoSize()) + "' color='" + m_Owner.getQuickInfoColor() + "'>"
		+ HtmlUtils.toHTML(quickInfo)
		+ "</font>");
	  }
	}

	// annotations?
	if (m_Owner.getShowAnnotations()) {
	  if (actor.getAnnotations().getValue().length() > 0)
	    html.append("<br>" + assembleAnnotation(actor));
	}

	// show output?
	if (m_Owner.getShowInputOutput() && (actor instanceof OutputProducer)) {
	  html.append("<br>");
	  html.append(
	      "<font size='" + scaleFontSize(m_Owner.getInputOutputSize()) + "' color='" + m_Owner.getInputOutputColor() + "'>"
	      + HtmlUtils.toHTML(classArrayToString(((OutputProducer) actor).generates())) + "</font>");
	}

	// variable/bookmark highlighting
	updateVariableSearch();
	if (m_VariableFound) {
	  html.insert(0, "<font style='background-color: " + m_Owner.getVariableHighlightBackground() + "'>");
	  html.append("</font>");
	}
	else if (m_Bookmarked) {
	  html.insert(0, "<font style='background-color: " + m_Owner.getBookmarkHighlightBackground() + "'>");
	  html.append("</font>");
	}
      }

      // finish
      html.insert(0, "<html>");
      html.append("</html>");

      m_RenderString = html.toString();
    }

    return m_RenderString;
  }
  
  /**
   * Returns the actor in plain text.
   *
   * @return		the actor
   */
  @Override
  public String toPlainText() {
    StringBuilder	plain;
    String[]		lines;
    String		line;
    int			i;
    int			n;
    String		quickInfo;
    AbstractActor	actor;

    if (m_RenderString == null) {
      plain = new StringBuilder();

      actor = getActor();
      if (actor == null) {
	plain.append("[none]");
      }
      else {
	plain.append(actor.getName());

	// skip this actor?
	if (actor.getSkip()) {
	  plain.insert(0, "---");
	  plain.append("---");
	}

	// show input?
	if (m_Owner.getShowInputOutput() && (actor instanceof InputConsumer)) {
	  plain.insert(
	      0,
	      classArrayToString(((InputConsumer) actor).accepts())
	      + "\n");
	}

	// quick info available?
	if (m_Owner.getShowQuickInfo()) {
	  quickInfo = actor.getQuickInfo();
	  if (quickInfo != null) {
	    plain.append("  " + quickInfo);
	  }
	}

	// annotations?
	if (m_Owner.getShowAnnotations()) {
	  if (actor.getAnnotations().getValue().length() > 0) {
	    lines = actor.getAnnotations().getValue().split("\n");
	    for (i = 0; i < lines.length; i++) {
	      if (lines[i].startsWith(" ")) {
		line = lines[i].trim();
		for (n = 0; n < lines[i].length() - line.length(); n++)
		  line = " " + line;
	      }
	      else {
		line = lines[i];
	      }
	      plain.append("\n" + line);
	    }
	  }
	}

	// show output?
	if (m_Owner.getShowInputOutput() && (actor instanceof OutputProducer)) {
	  plain.append("\n");
	  plain.append(classArrayToString(((OutputProducer) actor).generates()));
	}

	// variable highlighting
	updateVariableSearch();
      }

      m_RenderString = plain.toString();
    }

    return m_RenderString;
  }

  /**
   * Returns a stripped down version of the actor, i.e., for ActorHandlers,
   * a copy of the actor without any sub-actors gets returned.
   *
   * @param actor	the actor to strip down
   * @return		the stripped down actor
   * @see		ActorHandler
   */
  public static AbstractActor strip(AbstractActor actor) {
    AbstractActor	result;
    NestedProducer	producer;
    NestedConsumer	consumer;

    // create actor with only default sub-actors
    if (actor instanceof ActorHandler) {
      result   = (AbstractActor) Utils.newInstance(actor);
      producer = new NestedProducer();
      producer.setBlacklisted(new Class[]{AbstractActor[].class, AbstractActor.class, Actor[].class, Actor.class});
      producer.produce(actor);
      consumer = new NestedConsumer();
      consumer.setInput(producer.getOutput());
      result = (AbstractActor) consumer.consume();
      producer.cleanUp();
      consumer.cleanUp();
    }
    // create a shallow copy of actor
    else {
      result = actor.shallowCopy();
    }

    result.setParent(actor.getParent());

    return result;
  }

  /**
   * Invalidates the rendering string recursively, forces the tree to redraw
   * itself.
   */
  public void invalidateRendering() {
    int		i;

    m_RenderString = null;
    for (i = 0; i < getChildCount(); i++)
      ((Node) getChildAt(i)).invalidateRendering();
  }

  /**
   * Returns the index of the child with the specified actor name.
   *
   * @param name	the name to look for
   * @return		the index, -1 if not found
   */
  public int indexOf(String name) {
    int		result;
    int		i;
    Node	child;

    result = -1;

    for (i = 0; i < getChildCount(); i++) {
      child = (Node) getChildAt(i);
      if (child.getActor().getName().equals(name)) {
	result = i;
	break;
      }
    }

    return result;
  }
  
  /**
   * Returns whether the node can be expanded at all.
   * 
   * @return		true if it can be expanded
   */
  @Override
  public boolean canExpand() {
    return (getActor() instanceof ExternalActorHandler);
  }
  
  /**
   * Returns whether the expansion has already occurred.
   * 
   * @return		true if the expansion has already occurred
   */
  @Override
  public boolean getExpansionOccurred() {
    if (getActor() instanceof ExternalActorHandler)
      return m_ExpansionOccurred;
    else
      return true;
  }
  
  /**
   * Expands this node.
   * 
   * @return		true if structure below this node was changed
   */
  @Override
  protected boolean doExpand() {
    boolean			result;
    ExternalActorHandler	actor;
    String			msg;
    Node			node;
    
    result = false;
    
    if (getActor() instanceof ExternalActorHandler) {
      actor = (ExternalActorHandler) getActor();
      msg   = actor.setUpExternalActor();
      if (msg == null) {
	node = m_Owner.buildTree(this, actor.getExternalActor(), true);
	node.setEditable(false, true);
	result = (getChildCount() > 0);
      }
      else {
	GUIHelper.showErrorMessage(null, "Failed to expand node '" + getFullName() + "': " + msg);
      }
    }
    
    return result;
  }

  /**
   * Resets the node.
   */
  @Override
  protected void doReset() {
    if (getActor() instanceof ExternalActorHandler) {
      ((ExternalActorHandler) getActor()).cleanUpExternalActor();
    }
  }
  
  /**
   * Sets whether the node is editable.
   * 
   * @param value	if true then the node will be editable
   */
  public void setEditable(boolean value) {
    setEditable(value, false);
  }
  
  /**
   * Sets whether the node is editable.
   * 
   * @param value	if true then the node will be editable
   * @param recurse	whether to set the state recursively
   */
  public void setEditable(boolean value, boolean recurse) {
    int		i;
    
    m_Editable = value;
    
    if (recurse) {
      for (i = 0; i < getChildCount(); i++)
	((Node) getChildAt(i)).setEditable(value, true);
    }
  }
 
  /**
   * Sets whether the node is being bookmarked.
   * 
   * @param value	true if bookmarked
   */
  public void setBookmarked(boolean value) {
    m_Bookmarked = value;
  }
  
  /**
   * Returns whether the node is currently bookmarked.
   * 
   * @return		true if bookmarked
   */
  public boolean isBookmarked() {
    return m_Bookmarked;
  }
  
  /**
   * Returns whether the node is editable.
   * 
   * @return		true if the node is editable
   */
  public boolean isEditable() {
    return m_Editable;
  }
  
  /**
   * Expands the node, if not yet occurred.
   * 
   * @return		true if structure below node was changed
   */
  @Override
  public synchronized boolean expand() {
    boolean	result;
    
    result = super.expand();

    if (result)
      getOwner().nodeStructureChanged(this);
    
    return result;
  }
  
  /**
   * Collapses the node and removes all children, resetting the node.
   * 
   * @return		true if sub-tree was changed
   */
  @Override
  public synchronized boolean collapse() {
    boolean	result;
    
    result = super.collapse();
    
    if (result)
      getOwner().nodeStructureChanged(this);
    
    return result;
  }
}