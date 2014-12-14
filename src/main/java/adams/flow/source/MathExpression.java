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
 * MathExpression.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import java.util.HashMap;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import adams.parser.GrammarSupplier;
import adams.parser.MathematicalExpression;
import adams.parser.MathematicalExpressionText;

/**
 <!-- globalinfo-start -->
 * Evaluates a mathematical expression.<br/>
 * Variables are supported as well, e.g.: pow(X,&#64;{exp}) with '&#64;{exp}' being a variable available at execution time.<br/>
 * <br/>
 * The following grammar is used for the expressions:<br/>
 * <br/>
 * expr_list ::= '=' expr_list expr_part | expr_part ;<br/>
 * expr_part ::=  expr ;<br/>
 * <br/>
 * expr      ::=   ( expr )<br/>
 * <br/>
 * # data types<br/>
 *               | number<br/>
 *               | string<br/>
 *               | boolean<br/>
 *               | date<br/>
 * <br/>
 * # constants<br/>
 *               | true<br/>
 *               | false<br/>
 *               | pi<br/>
 *               | e<br/>
 *               | now()<br/>
 *               | today()<br/>
 * <br/>
 * # negating numeric value<br/>
 *               | -expr<br/>
 * <br/>
 * # comparisons<br/>
 *               | expr &lt; expr<br/>
 *               | expr &lt;= expr<br/>
 *               | expr &gt; expr<br/>
 *               | expr &gt;= expr<br/>
 *               | expr = expr<br/>
 *               | expr != expr (or: expr &lt;&gt; expr)<br/>
 * <br/>
 * # boolean operations<br/>
 *               | ! expr (or: not expr)<br/>
 *               | expr &amp; expr (or: expr and expr)<br/>
 *               | expr | expr (or: expr or expr)<br/>
 *               | if[else] ( expr , expr (if true) , expr (if false) )<br/>
 *               | ifmissing ( variable , expr (default value if variable is missing) )<br/>
 * <br/>
 * # arithmetics<br/>
 *               | expr + expr<br/>
 *               | expr - expr<br/>
 *               | expr * expr<br/>
 *               | expr &#47; expr<br/>
 *               | expr ^ expr (power of)<br/>
 *               | expr % expr (modulo)<br/>
 *               ;<br/>
 * <br/>
 * # numeric functions<br/>
 *               | abs ( expr )<br/>
 *               | sqrt ( expr )<br/>
 *               | log ( expr )<br/>
 *               | exp ( expr )<br/>
 *               | sin ( expr )<br/>
 *               | cos ( expr )<br/>
 *               | tan ( expr )<br/>
 *               | rint ( expr )<br/>
 *               | floor ( expr )<br/>
 *               | pow[er] ( expr , expr )<br/>
 *               | ceil ( expr )<br/>
 *               | year ( expr )<br/>
 *               | month ( expr )<br/>
 *               | day ( expr )<br/>
 *               | hour ( expr )<br/>
 *               | minute ( expr )<br/>
 *               | second ( expr )<br/>
 *               | weekday ( expr )<br/>
 *               | weeknum ( expr )<br/>
 * <br/>
 * # string functions<br/>
 *               | substr ( expr , start [, end] )<br/>
 *               | left ( expr , len )<br/>
 *               | mid ( expr , start , len )<br/>
 *               | right ( expr , len )<br/>
 *               | rept ( expr , count )<br/>
 *               | concatenate ( expr1 , expr2 [, expr3-5] )<br/>
 *               | lower[case] ( expr )<br/>
 *               | upper[case] ( expr )<br/>
 *               | trim ( expr )<br/>
 *               | matches ( expr , regexp )<br/>
 *               | trim ( expr )<br/>
 *               | len[gth] ( str )<br/>
 *               | find ( search , expr [, pos] )<br/>
 *               | replace ( str , pos , len , newstr )<br/>
 *               | substitute ( str , find , replace [, occurrences] )<br/>
 *               ;<br/>
 * <br/>
 * Notes:<br/>
 * - Variables are either all upper case letters (e.g., "ABC") or any character   apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]").<br/>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br/>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br/>
 * - Line comments start with '#'.<br/>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br/>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br/>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br/>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br/>
 * - the characters in square brackets in function names are optional:<br/>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br/>
 * <br/>
 * A lot of the functions have been modeled after LibreOffice:<br/>
 *   https:&#47;&#47;help.libreoffice.org&#47;Calc&#47;Functions_by_Category<br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: MathExpression
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-expression &lt;adams.parser.MathematicalExpressionText&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The mathematical expression to evaluate.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7829 $
 * @see adams.parser.MathematicalExpression
 */
public class MathExpression
  extends AbstractSimpleSource
  implements GrammarSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -8477454145267616359L;

  /** the mathematical expression to evaluate. */
  protected MathematicalExpressionText m_Expression;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates a mathematical expression.\n"
      + "Variables are supported as well, e.g.: pow(X,@{exp}) with '@{exp}' "
      + "being a variable available at execution time.\n\n"
      + "The following grammar is used for the expressions:\n\n"
      + getGrammar();
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return new MathematicalExpression().getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "expression", "expression",
	    new MathematicalExpressionText("1.0"));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "expression", m_Expression);
  }

  /**
   * Sets the mathematical expression to evaluate.
   *
   * @param value	the expression
   */
  public void setExpression(MathematicalExpressionText value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the mathematical expression to evaluate.
   *
   * @return		the expression
   */
  public MathematicalExpressionText getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return "The mathematical expression to evaluate.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Double.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Double.class};
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if ((m_Expression == null) || (m_Expression.getValue().length() == 0))
	result = "No expression provided!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Double	res;
    String	exp;

    result = null;

    exp = m_Expression.getValue();
    try {
      // replace variables with their actual values
      if (isLoggingEnabled())
	getLogger().info("Expression: " + exp);
      exp = getVariables().expand(exp);
      if (isLoggingEnabled())
	getLogger().info("--> expanded: " + exp);

      // evaluate the expression
      res = null;
      res = MathematicalExpression.evaluate(exp, new HashMap());

      if (res != null) {
	m_OutputToken = new Token(new Double(res));
	if (isLoggingEnabled())
	  getLogger().info("--> res: " + res);
      }
      else {
	result = "Failed to generate output?";
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Error evaluating: " + exp, e);
    }

    return result;
  }
}
