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
 * VariableRenamed.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.postprocessor;

import java.util.List;

import adams.core.VariableName;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.flow.core.AbstractActor;
import adams.flow.processor.UpdateVariableName;
import adams.gui.event.ActorChangeEvent;
import adams.gui.event.ActorChangeEvent.Type;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.Tree;

/**
 <!-- globalinfo-start -->
 * Updates all references of the variable that was renamed.
 * <p/>
 <!-- globalinfo-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7229 $
 */
public class VariableRenamed
  extends AbstractEditPostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -8661419635908219055L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Updates all references of the variable that was renamed.";
  }

  /**
   * Extracts the variable name (the option) from the actor.
   * 
   * @param actor	the actor to get the variable name from
   * @return		the variable name, null if none found
   */
  protected VariableName getVariableName(AbstractActor actor) {
    VariableName		result;
    int				i;
    List<AbstractOption>	options;
    AbstractArgumentOption	option;
    
    result = null;

    options = actor.getOptionManager().getOptionsList();
    for (i = 0; i < options.size(); i++) {
      if (options.get(i) instanceof AbstractArgumentOption) {
	option = (AbstractArgumentOption) options.get(i);
	if (!option.isMultiple() && option.getBaseClass().equals(VariableName.class)) {
	  result = (VariableName) option.getCurrentValue();
	  break;
	}
      }
    }
    
    return result;
  }
  
  /**
   * Checks whether this post processor scheme applies to the current situation.
   * 
   * @param oldActor	the old actor
   * @param newActor	the new, updated actor
   * @return		true if this post processor applies to the situation
   */
  @Override
  public boolean applies(AbstractActor parent, AbstractActor oldActor, AbstractActor newActor) {
    boolean		result;
    VariableName	oldName;
    VariableName	newName;
    
    result = false;
    
    oldName = getVariableName(oldActor);
    newName = getVariableName(newActor);
    
    if (oldName != null)
      result = !oldName.equals(newName);
    
    return result;
  }
  
  /**
   * Post-processes the tree.
   * 
   * @param tree	the tree to post-process
   * @param parent	the parent actor
   * @param oldActor	the old actor
   * @param newActor	the new, updated actor
   * @return		true if tree got modified
   */
  @Override
  public boolean postProcess(Tree tree, AbstractActor parent, AbstractActor oldActor, AbstractActor newActor) {
    boolean			result;
    UpdateVariableName	updater;
    
    result = false;
    
    updater = new UpdateVariableName();
    updater.setOldName(getVariableName(oldActor).getValue());
    updater.setNewName(getVariableName(newActor).getValue());
    updater.process(tree.getActor());
    if (updater.isModified()) {
      result = true;
      tree.setModified(true);
      tree.setActor(updater.getModifiedActor());
      tree.notifyActorChangeListeners(new ActorChangeEvent(tree, new Node[0], Type.MODIFY_BULK));
      tree.refreshTabs();
    }

    return result;
  }
}
