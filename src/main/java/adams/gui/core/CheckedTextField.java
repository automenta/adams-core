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
 * CheckedTextField.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.io.Serializable;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * A specialized JTextField that takes a check model as input.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class CheckedTextField
  extends JTextField {

  /** for serialization. */
  private static final long serialVersionUID = -1011286612484850433L;

  /**
   * Abstract model for checking the text from a text field.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 4584 $
   */
  public static abstract class AbstractCheckModel
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = -8150917807970079198L;

    /** the default text. */
    protected String m_DefaultValue;

    /**
     * Initializes the model.
     */
    public AbstractCheckModel() {
      this("");
    }

    /**
     * Initializes the model with the specified default value.
     *
     * @param defValue	the default value to use
     */
    public AbstractCheckModel(String defValue) {
      super();

      m_DefaultValue = defValue;
    }

    /**
     * Checks whether the content is valid.
     *
     * @param text	the string to check
     * @return		true if valid
     */
    public abstract boolean isValid(String text);

    /**
     * Sets the default value to use.
     *
     * @param value	the default
     */
    public void setDefaultValue(String value) {
      m_DefaultValue = value;
    }

    /**
     * Returns the default value in case the current string is not valid.
     *
     * @return		the default string to use
     */
    public String getDefaultValue() {
      return m_DefaultValue;
    }

    /**
     * Returns a short string representation.
     *
     * @return		the string representation
     */
    public String toString() {
      return "defValue=" + m_DefaultValue;
    }
  }

  /**
   * A (dummy) check model that allows any string.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 4584 $
   */
  public static class StringCheckModel
    extends AbstractCheckModel {

    /** for serialization. */
    private static final long serialVersionUID = 451762705522167701L;

    /**
     * Checks whether the content is valid.
     *
     * @param text	the string to check
     * @return		always true
     */
    public boolean isValid(String text) {
      return true;
    }
  }

  /** the checkmodel in use. */
  protected AbstractCheckModel m_CheckModel;

  /**
   * Constructs a new <code>TextField</code>.  A default model is created,
   * the initial string is <code>null</code>,
   * and the number of columns is set to 0.
   * A StringCheckModel is used.
   */
  public CheckedTextField() {
    super();
    initialize();
  }

  /**
   * Constructs a new <code>TextField</code>.  A default model is created,
   * the initial string is <code>null</code>,
   * and the number of columns is set to 0.
   * The provided model is used.
   *
   * @param model the check model to use
   */
  public CheckedTextField(AbstractCheckModel model) {
    this();
    setCheckModel(model);
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text. A default model is created and the number of
   * columns is 0.
   * A StringCheckModel is used.
   *
   * @param text the text to be displayed, or <code>null</code>
   */
  public CheckedTextField(String text) {
    super(text);
    initialize();
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text. A default model is created and the number of
   * columns is 0.
   * The provided model is used.
   *
   * @param text the text to be displayed, or <code>null</code>
   * @param model the check model to use
   */
  public CheckedTextField(String text, AbstractCheckModel model) {
    this(text);
    setCheckModel(model);
  }

  /**
   * Constructs a new empty <code>TextField</code> with the specified
   * number of columns.
   * A default model is created and the initial string is set to
   * <code>null</code>.
   * A StringCheckModel is used.
   *
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public CheckedTextField(int columns) {
    super(columns);
    initialize();
  }

  /**
   * Constructs a new empty <code>TextField</code> with the specified
   * number of columns.
   * A default model is created and the initial string is set to
   * <code>null</code>.
   * The provided model is used.
   *
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   * @param model the check model to use
   */
  public CheckedTextField(int columns, AbstractCheckModel model) {
    this(columns);
    setCheckModel(model);
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text and columns.  A default model is created.
   * A StringCheckModel is used.
   *
   * @param text the text to be displayed, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   */
  public CheckedTextField(String text, int columns) {
    super(text, columns);
    initialize();
  }

  /**
   * Constructs a new <code>TextField</code> initialized with the
   * specified text and columns.  A default model is created.
   * The provided model is used.
   *
   * @param text the text to be displayed, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width; if columns is set to zero, the
   *   preferred width will be whatever naturally results from
   *   the component implementation
   * @param model the check model to use
   */
  public CheckedTextField(String text, int columns, AbstractCheckModel model) {
    this(text, columns);
    setCheckModel(model);
  }

  /**
   * Constructs a new <code>JTextField</code> that uses the given text
   * storage model and the given number of columns.
   * This is the constructor through which the other constructors feed.
   * If the document is <code>null</code>, a default model is created.
   * A StringCheckModel is used.
   *
   * @param doc  the text storage to use; if this is <code>null</code>,
   *		a default will be provided by calling the
   *		<code>createDefaultModel</code> method
   * @param text  the initial string to display, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width >= 0; if <code>columns</code>
   *   is set to zero, the preferred width will be whatever
   *   naturally results from the component implementation
   */
  public CheckedTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    initialize();
  }

  /**
   * Constructs a new <code>JTextField</code> that uses the given text
   * storage model and the given number of columns.
   * This is the constructor through which the other constructors feed.
   * If the document is <code>null</code>, a default model is created.
   * The provided model is used.
   *
   * @param doc  the text storage to use; if this is <code>null</code>,
   *		a default will be provided by calling the
   *		<code>createDefaultModel</code> method
   * @param text  the initial string to display, or <code>null</code>
   * @param columns  the number of columns to use to calculate
   *   the preferred width >= 0; if <code>columns</code>
   *   is set to zero, the preferred width will be whatever
   *   naturally results from the component implementation
   * @param model the check model to use
   */
  public CheckedTextField(Document doc, String text, int columns, AbstractCheckModel model) {
    this(doc, text, columns);
    setCheckModel(model);
  }

  /**
   * Returns the default model to use.
   *
   * @return		the default model
   */
  protected AbstractCheckModel getDefaultCheckModel() {
    return new StringCheckModel();
  }

  /**
   * Initializes the text field.
   */
  protected void initialize() {
    m_CheckModel = getDefaultCheckModel();
  }

  /**
   * Sets the underlying check model.
   *
   * @param value	the model for checking the input
   */
  public void setCheckModel(AbstractCheckModel value) {
    m_CheckModel = value;
    if (!m_CheckModel.isValid(getText()))
      setText(m_CheckModel.getDefaultValue());
  }

  /**
   * Returns the underlying check model.
   *
   * @return		the model for checking the input
   */
  public AbstractCheckModel getCheckModel() {
    return m_CheckModel;
  }

  /**
   * Sets the text, but only if it is valid according to the current model.
   *
   * @param t		the text to set
   */
  public void setText(String t) {
    if (m_CheckModel != null) {
      if (m_CheckModel.isValid(t))
	super.setText(t);
      else
	super.setText(m_CheckModel.getDefaultValue());
    }
    else {
      super.setText(t);
    }
  }

  /**
   * Bypasses the checks when setting the text.
   *
   * @param value	the text to set
   */
  protected void setTextUnchecked(String value) {
    super.setText(value);
  }

  /**
   * Returns the raw text currently being displayed.
   *
   * @return		the raw text or null if not
   */
  protected String getTextUnchecked() {
    if (getDocument() != null)
      return super.getText();
    else
      return null;
  }

  /**
   * Returns the text. If the current input is not valid, then the default
   * value will be returned.
   *
   * @return		the current text
   */
  public String getText() {
    String	result;
    String	current;

    current = getTextUnchecked();

    if ((m_CheckModel != null) && (current != null)) {
      if (m_CheckModel.isValid(current)) {
	result = current;
      }
      else {
	result = m_CheckModel.getDefaultValue();
	// fix input as well
	setTextUnchecked(result);
      }
    }
    else {
      result = current;
    }

    return result;
  }

  /**
   * Returns whether the current input string is valid.
   *
   * @return		true if the input string is valid
   */
  public boolean isValid() {
    boolean	result;
    String	current;

    result  = true;
    current = getTextUnchecked();

    if ((m_CheckModel != null) && (current != null))
      result = m_CheckModel.isValid(current);

    return result;
  }

  /**
   * Sets the default value in the edit field.
   *
   * @see		AbstractCheckModel#getDefaultValue()
   */
  public void setDefaultValue() {
    if (m_CheckModel != null)
      setTextUnchecked(m_CheckModel.getDefaultValue());
  }

  /**
   * Returns a short string representation.
   *
   * @return		the representation
   */
  public String toString() {
    String	result;

    result = super.toString();
    result = result.substring(0, result.length() - 1) + ", checkModel=" + m_CheckModel + "]";

    return result;
  }
}
