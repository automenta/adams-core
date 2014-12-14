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
 * BooleanExpression.java
 * Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import java_cup.runtime.DefaultSymbolFactory;
import java_cup.runtime.SymbolFactory;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.parser.booleanexpression.Parser;
import adams.parser.booleanexpression.Scanner;

/**
 <!-- globalinfo-start -->
 * Evaluates boolean expressions.<br/>
 * <br/>
 * It uses the following grammar:<br/>
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
 *               | isNaN ( expr )<br/>
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
 * <br/>
 * # array functions<br/>
 *               | len[gth] ( array )<br/>
 *               | get ( array , index )<br/>
 *               ;<br/>
 * <br/>
 * Notes:<br/>
 * - Variables are either all upper case letters (e.g., "ABC") or any character   apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]").<br/>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br/>
 * - 'index' for function 'get' starts at 1.<br/>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br/>
 * - Line comments start with '#'<br/>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br/>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br/>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br/>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br/>
 * - the characters in square brackets in function names are optional:<br/>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br/>
 * <br/>
 * A lot of the functions have been modeled after LibreOffice:<br/>
 *   https:&#47;&#47;help.libreoffice.org&#47;Calc&#47;Functions_by_Category<br/>
 * <br/>
 * Additional functions:<br/>
 * - env(String): String<br/>
 * &nbsp;&nbsp;&nbsp;First argument is the name of the environment variable to retrieve.<br/>
 * &nbsp;&nbsp;&nbsp;The result is the value of the environment variable.<br/>
 * <br/>
 * Additional procedures:<br/>
 * - println(...)<br/>
 * &nbsp;&nbsp;&nbsp;One or more arguments are printed as comma-separated list to stdout.<br/>
 * &nbsp;&nbsp;&nbsp;If no argument is provided, a simple line feed is output.<br/>
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-env &lt;java.lang.String&gt; (property: environment)
 * &nbsp;&nbsp;&nbsp;The class to use for determining the environment.
 * &nbsp;&nbsp;&nbsp;default: adams.env.Environment
 * </pre>
 * 
 * <pre>-expression &lt;java.lang.String&gt; (property: expression)
 * &nbsp;&nbsp;&nbsp;The boolean expression to evaluate (must evaluate to a boolean).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-symbol &lt;adams.core.base.BaseString&gt; [-symbol ...] (property: symbols)
 * &nbsp;&nbsp;&nbsp;The symbols to initialize the parser with, key-value pairs: name=value.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10230 $
 */
public class BooleanExpression
  extends AbstractSymbolEvaluator<Boolean> {

  /** for serialization. */
  private static final long serialVersionUID = -5923987640355752595L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates boolean expressions.\n\n"
      + "It uses the following grammar:\n\n"
      + getGrammar();
  }

  /**
   * Returns a string representation of the grammar.
   *
   * @return		the grammar, null if not available
   */
  public String getGrammar() {
    return
        "expr_list ::= '=' expr_list expr_part | expr_part ;\n"
      + "expr_part ::=  expr ;\n"
      + "\n"
      + "expr      ::=   ( expr )\n"
      + "\n"
      + "# data types\n"
      + "              | number\n"
      + "              | string\n"
      + "              | boolean\n"
      + "              | date\n"
      + "\n"
      + "# constants\n"
      + "              | true\n"
      + "              | false\n"
      + "              | pi\n"
      + "              | e\n"
      + "              | now()\n"
      + "              | today()\n"
      + "\n"
      + "# negating numeric value\n"
      + "              | -expr\n"
      + "\n"
      + "# comparisons\n"
      + "              | expr < expr\n"
      + "              | expr <= expr\n"
      + "              | expr > expr\n"
      + "              | expr >= expr\n"
      + "              | expr = expr\n"
      + "              | expr != expr (or: expr <> expr)\n"
      + "\n"
      + "# boolean operations\n"
      + "              | ! expr (or: not expr)\n"
      + "              | expr & expr (or: expr and expr)\n"
      + "              | expr | expr (or: expr or expr)\n"
      + "              | if[else] ( expr , expr (if true) , expr (if false) )\n"
      + "              | ifmissing ( variable , expr (default value if variable is missing) )\n"
      + "              | isNaN ( expr )\n"
      + "\n"
      + "# arithmetics\n"
      + "              | expr + expr\n"
      + "              | expr - expr\n"
      + "              | expr * expr\n"
      + "              | expr / expr\n"
      + "              | expr ^ expr (power of)\n"
      + "              | expr % expr (modulo)\n"
      + "              ;\n"
      + "\n"
      + "# numeric functions\n"
      + "              | abs ( expr )\n"
      + "              | sqrt ( expr )\n"
      + "              | log ( expr )\n"
      + "              | exp ( expr )\n"
      + "              | sin ( expr )\n"
      + "              | cos ( expr )\n"
      + "              | tan ( expr )\n"
      + "              | rint ( expr )\n"
      + "              | floor ( expr )\n"
      + "              | pow[er] ( expr , expr )\n"
      + "              | ceil ( expr )\n"
      + "              | year ( expr )\n"
      + "              | month ( expr )\n"
      + "              | day ( expr )\n"
      + "              | hour ( expr )\n"
      + "              | minute ( expr )\n"
      + "              | second ( expr )\n"
      + "              | weekday ( expr )\n"
      + "              | weeknum ( expr )\n"
      + "\n"
      + "# string functions\n"
      + "              | substr ( expr , start [, end] )\n"
      + "              | left ( expr , len )\n"
      + "              | mid ( expr , start , len )\n"
      + "              | right ( expr , len )\n"
      + "              | rept ( expr , count )\n"
      + "              | concatenate ( expr1 , expr2 [, expr3-5] )\n"
      + "              | lower[case] ( expr )\n"
      + "              | upper[case] ( expr )\n"
      + "              | trim ( expr )\n"
      + "              | matches ( expr , regexp )\n"
      + "              | trim ( expr )\n"
      + "              | len[gth] ( str )\n"
      + "              | find ( search , expr [, pos] )\n"
      + "              | replace ( str , pos , len , newstr )\n"
      + "              | substitute ( str , find , replace [, occurrences] )\n"
      + "\n"
      + "# array functions\n"
      + "              | len[gth] ( array )\n"
      + "              | get ( array , index )\n"
      + "              ;\n"
      + "\n"
      + "Notes:\n"
      + "- Variables are either all upper case letters (e.g., \"ABC\") or any character "
      + "  apart from \"]\" enclosed by \"[\" and \"]\" (e.g., \"[Hello World]\").\n"
      + "- 'start' and 'end' for function 'substr' are indices that start at 1.\n"
      + "- 'index' for function 'get' starts at 1.\n"
      + "- Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)\n"
      + "- Line comments start with '#'\n"
      + "- Semi-colons (';') or commas (',') can be used as separator in the formulas,\n"
      + "  e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'\n"
      + "- dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'\n"
      + "- times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'\n"
      + "- the characters in square brackets in function names are optional:\n"
      + "  e.g. 'len(\"abc\")' is the same as 'length(\"abc\")'\n"
      + "\n"
      + "A lot of the functions have been modeled after LibreOffice:\n"
      + "  https://help.libreoffice.org/Calc/Functions_by_Category\n"
      + "\n"
      + "Additional functions:\n"
      + ParserHelper.getFunctionOverview() + "\n"
      + "\n"
      + "Additional procedures:\n"
      + ParserHelper.getProcedureOverview() + "\n"
      ;
  }

  /**
   * Returns the default expression to use.
   *
   * @return		the default expression
   */
  @Override
  protected String getDefaultExpression() {
    return "true";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String expressionTipText() {
    return "The boolean expression to evaluate (must evaluate to a boolean).";
  }

  /**
   * Initializes the symbol.
   *
   * @param name	the name of the symbol
   * @param value	the string representation of the symbol
   * @return		the object representation of the symbol
   */
  @Override
  protected Object initializeSymbol(String name, String value) {
    Double	result;

    try {
      result = new Double(value);
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to parse the value of symbol '" + name + "': " + value, e);
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param symbols	the symbols to use
   * @return		the evaluation, or null in case of error
   * @throws Exception	if evaluation fails
   */
  @Override
  protected Boolean doEvaluate(HashMap symbols) throws Exception {
    return evaluate(m_Expression, symbols);
  }

  /**
   * Parses and evaluates the given expression.
   * Returns the result of the boolean expression, based on the given
   * values of the symbols.
   *
   * @param expr	the expression to evaluate
   * @param symbols	the symbol/value mapping
   * @return		the evaluated result
   * @throws Exception	if something goes wrong
   */
  public static boolean evaluate(String expr, HashMap symbols) throws Exception {
    SymbolFactory 		sf;
    ByteArrayInputStream 	parserInput;
    Parser 			parser;

      switch (expr) {
          case "true":
              return true;
          case "false":
              return false;
      }

    sf          = new DefaultSymbolFactory();
    parserInput = new ByteArrayInputStream(expr.getBytes());
    parser      = new Parser(new Scanner(parserInput, sf), sf);
    parser.setSymbols(symbols);
    parser.parse();

    return parser.getResult();
  }

  /**
   * Turns the report into symbols for the expression evaluation.
   * 
   * @param reports	the report to turn into symbols
   * @return		the generated symbols
   */
  public static HashMap reportToSymbols(Report report) {
    HashMap		result;
    List<AbstractField>	fields;
    
    result = new HashMap();
    fields = report.getFields();
    for (AbstractField field: fields) {
      switch (field.getDataType()) {
	case NUMERIC:
	  result.put(field.toString(), report.getDoubleValue(field));
	  break;
	case BOOLEAN:
	  result.put(field.toString(), report.getBooleanValue(field));
	  break;
	default:
	  result.put(field.toString(), "" + report.getValue(field));
      }
    }
    
    return result;
  }
  
  /**
   * Parses and evaluates the given expression.
   * Returns the result of the mathematical expression, based on the given
   * values of the symbols.
   *
   * @param expr	the expression to evaluate
   * @param report	the report
   * @return		the evaluated result
   * @throws Exception	if something goes wrong
   */
  public static boolean evaluate(String expr, Report report) throws Exception {
    return evaluate(expr, reportToSymbols(report));
  }

  /**
   * Runs the evaluator from command-line.
   *
   * @param args	the command-line options, use "-help" to list them
   */
  public static void main(String[] args) {
    runEvaluator(BooleanExpression.class, args);
  }
}
