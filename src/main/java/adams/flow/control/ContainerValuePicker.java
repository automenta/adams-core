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
 * ContainerValuePicker.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.flow.container.AbstractContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Picks a named value from any container object and tees it off.<br/>
 * With the 'switch-outputs' option it is possible to forward the named value and teeing off the container instead.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ContainerValuePicker
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-tee &lt;adams.flow.core.AbstractActor&gt; [-tee ...] (property: actors)
 * &nbsp;&nbsp;&nbsp;The actors to siphon-off the tokens to.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.Null
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: valueName)
 * &nbsp;&nbsp;&nbsp;The name of the value to tee off.
 * &nbsp;&nbsp;&nbsp;default: Classification
 * </pre>
 *
 * <pre>-switch-outputs (property: switchOutputs)
 * &nbsp;&nbsp;&nbsp;Whether to switch the output for the tee actors with the one normally being
 * &nbsp;&nbsp;&nbsp;forwarded.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6830 $
 */
public class ContainerValuePicker
  extends Tee {

  /** for serialization. */
  private static final long serialVersionUID = 8352837834646017416L;

  /** the value to pick. */
  protected String m_ValueName;

  /** whether to switch outputs. */
  protected boolean m_SwitchOutputs;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Picks a named value from any container object and tees it off.\n"
      + "With the 'switch-outputs' option it is possible to forward the named "
      + "value and teeing off the container instead.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "value", "valueName",
	    "Classification");

    m_OptionManager.add(
	    "switch-outputs", "switchOutputs",
	    false);
  }

  /**
   *
   */
  @Override
  protected void reset() {
    super.reset();

    if (m_SwitchOutputs)
      m_MinimumActiveActors = 0;
    else
      m_MinimumActiveActors = 1;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "valueName", m_ValueName);
    result += QuickInfoHelper.toString(this, "switchOutputs", m_SwitchOutputs, "[outputs switched]", " ");
    
    if (super.getQuickInfo() != null)
      result += ", " + super.getQuickInfo();

    return result;
  }

  /**
   * Sets the name of the value to tee off.
   *
   * @param value	the name
   */
  public void setValueName(String value) {
    m_ValueName = value;
    reset();
  }

  /**
   * Returns the name of the value to tee off.
   *
   * @return		the name
   */
  public String getValueName() {
    return m_ValueName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueNameTipText() {
    return "The name of the value to tee off.";
  }

  /**
   * Sets whether to switch regular and tee output.
   *
   * @param value	if true then outputs are switched
   */
  public void setSwitchOutputs(boolean value) {
    m_SwitchOutputs = value;
    reset();
  }

  /**
   * Returns whether the regular and tee output are switched.
   *
   * @return		true if the outputs are switched
   */
  public boolean getSwitchOutputs() {
    return m_SwitchOutputs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String switchOutputsTipText() {
    return "Whether to switch the output for the tee actors with the one normally being forwarded.";
  }

  /**
   *
   */
  protected Token extract(Token token) {
    Token		result;
    AbstractContainer	cont;
    Object		value;

    result = null;

    if (token.getPayload() instanceof AbstractContainer) {
      cont   = (AbstractContainer) token.getPayload();
      value  = cont.getValue(m_ValueName);
      if (value != null)
	result = new Token(value);
    }

    return result;
  }

  /**
   * Creates the token to tee-off.
   *
   * @param token	the input token
   * @return		the token to tee-off or null if nothing available
   */
  @Override
  protected Token createTeeToken(Token token) {
    Token		result;

    result = null;

    if (m_SwitchOutputs)
      result = token;
    else
      result = extract(token);

    return result;
  }

  /**
   * Checks whether we can process the token.
   *
   * @param token	the token to check
   * @return		true if token can be processed
   */
  protected boolean canProcessToken(Token token) {
    return (createTeeToken(token) != null);
  }

  /**
   * Returns whether the token can be processed in the tee actor.
   *
   * @param token	the token to process
   * @return		true if token can be processed
   */
  @Override
  protected boolean canProcessInput(Token token) {
    return (super.canProcessInput(token) && canProcessToken(token));
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = super.doExecute();

    if (m_SwitchOutputs && (result == null)) {
      m_OutputToken = extract(m_OutputToken);
    }

    return result;
  }
}
