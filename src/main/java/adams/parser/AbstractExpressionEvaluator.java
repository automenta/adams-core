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
 * AbstractExpressionEvaluator.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;

/**
 * Abstract ancestor for classes that evaluate expressions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9265 $
 * @param <T> the return type of the parser
 */
public abstract class AbstractExpressionEvaluator<T>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -2158795563625866483L;

  /** the environment class (dummy option, happens all in runJavadoc method). */
  protected String m_Environment;

  /** the expression to evaluate. */
  protected String m_Expression;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    // dummy option, is queried manually in runEvaluator method
    m_OptionManager.add(
	    "env", "environment",
	    Environment.class.getName());

    m_OptionManager.add(
	    "expression", "expression",
	    getDefaultExpression());
  }

  /**
   * sets the classname of the environment class to use.
   *
   * @param value	the environment class name
   */
  public void setEnvironment(String value) {
    m_Environment = value;
  }

  /**
   * returns the current classname of the environment class to use.
   *
   * @return	the current classname of the environment class
   */
  public String getEnvironment() {
    return m_Environment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String environmentTipText() {
    return "The class to use for determining the environment.";
  }

  /**
   * Returns the default expression to use.
   *
   * @return		the default expression
   */
  protected abstract String getDefaultExpression();

  /**
   * Sets the expression to evaluate.
   *
   * @param value	the expression
   */
  public void setExpression(String value) {
    m_Expression = value;
  }

  /**
   * Returns the expression to evaluate.
   *
   * @return		the expression
   */
  public String getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String expressionTipText();

  /**
   * Performs the evaluation.
   *
   * @return		the evaluation, or null in case of error
   * @throws Exception	if evaluation fails
   */
  public abstract T evaluate() throws Exception;

  /**
   * Instantiates the evaluator with the given options.
   *
   * @param classname	the classname of the evaluator to instantiate
   * @param options	the options for the evaluator
   * @return		the instantiated evaluator or null if an error occurred
   */
  public static AbstractExpressionEvaluator forName(String classname, String[] options) {
    AbstractExpressionEvaluator	result;

    try {
      result = (AbstractExpressionEvaluator) OptionUtils.forName(AbstractExpressionEvaluator.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the evaluator from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			evaluator to instantiate
   * @return		the instantiated evaluator or null if an error occurred
   */
  public static AbstractExpressionEvaluator forCommandLine(String cmdline) {
    return (AbstractExpressionEvaluator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }

  /**
   * Runs the evaluator from commandline.
   *
   * @param eval	the evaluator to execute
   * @param args	the commandline arguments, use -help to display all
   */
  public static void runEvaluator(Class eval, String[] args) {
    AbstractExpressionEvaluator	evalInst;
    String			env;

    // we have to set the environment before anything else happens
    env = OptionUtils.getOption(args, "-env");
    if ((env == null) || (env.length() == 0))
      env = Environment.class.getName();
    try {
      Environment.setEnvironmentClass(Class.forName(env));
    }
    catch (Exception e) {
      e.printStackTrace();
      Environment.setEnvironmentClass(Environment.class);
    }

    try {
      if (OptionUtils.helpRequested(args)) {
	System.out.println("Help requested...\n");
	evalInst = forName(eval.getName(), new String[0]);
	System.out.println("\n" + OptionUtils.list(evalInst));
      }
      else {
	evalInst = forName(eval.getName(), args);
	try {
	  Object obj = evalInst.evaluate();
	  if (obj == null)
	    System.err.println("Failed to generate a result!");
	  else
	    System.out.println("Result: " + obj);
	}
	catch (Exception e) {
	  System.err.println("Parsing of expression '" + evalInst.getExpression() + "' failed:");
	  e.printStackTrace();
	}
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
