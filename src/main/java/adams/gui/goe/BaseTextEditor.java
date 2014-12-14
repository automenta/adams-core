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
 * BaseTextEditor.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import adams.core.Utils;
import adams.core.base.BaseObject;
import adams.core.base.BaseText;
import adams.core.option.AbstractOption;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.GUIHelper;

/**
 * A PropertyEditor for BaseText objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9025 $
 * @see adams.core.base.BaseText
 */
public class BaseTextEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, InlineEditorSupport {

  /** The text area with the value. */
  protected BaseTextArea m_TextValue;

  /**
   * Returns the BaseText as string.
   *
   * @param option	the current option
   * @param object	the BaseText object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((BaseText) object).stringValue();
  }

  /**
   * Returns a BaseText generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a BaseText
   * @return		the generated BaseText
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new BaseText(Utils.unbackQuoteChars(str));
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    String	result;

    result = "new " + getValue().getClass().getName() + "(\"" + toString(null, getValue()) + "\")";

    return result;
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  @Override
  public void paintValue(Graphics gfx, Rectangle box) {
    FontMetrics 	fm;
    int 		vpad;
    String 		val;

    fm   = gfx.getFontMetrics();
    vpad = (box.height - fm.getHeight()) / 2;
    if (getValue() == null)
      val = "null";
    else
      val = toString(null, getValue());
    gfx.drawString(val, 2, fm.getHeight() + vpad);
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		the editor
   */
  @Override
  protected JComponent createCustomEditor() {
    JPanel		panelAll;
    JPanel 		panelButtons;
    JButton 		buttonClose;
    JButton 		buttonOK;
    JButton		buttonClear;
    JPanel		panel;
    final JCheckBox	checkLineWrap;

    panelAll    = new JPanel(new BorderLayout());
    m_TextValue = new BaseTextArea(30, 60);
    panelAll.add(new BaseScrollPane(m_TextValue), BorderLayout.CENTER);
    panelAll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    panelButtons = new JPanel(new BorderLayout());
    panelAll.add(panelButtons, BorderLayout.SOUTH);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.WEST);
    buttonClear = new JButton("Clear");
    buttonClear.setMnemonic('l');
    buttonClear.setIcon(GUIHelper.getIcon("new.gif"));
    buttonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_TextValue.setText("");
      }
    });
    panel.add(buttonClear);

    checkLineWrap = new JCheckBox("Line wrap");
    checkLineWrap.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextValue.setLineWrap(checkLineWrap.isSelected());
      }
    });
    panel.add(checkLineWrap);
    
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.EAST);
    buttonOK = new JButton("OK");
    buttonOK.setMnemonic('O');
    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String s = m_TextValue.getText();
	if (((BaseText) getValue()).isValid(s) && !s.equals(((BaseObject) getValue()).getValue()))
	  setValue(new BaseText(s));
	closeDialog(APPROVE_OPTION);
      }
    });
    panel.add(buttonOK);

    buttonClose = new JButton("Cancel");
    buttonClose.setMnemonic('C');
    buttonClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	closeDialog(CANCEL_OPTION);
      }
    });
    panel.add(buttonClose);

    return panelAll;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    super.initForDisplay();
    if (!m_TextValue.getText().equals("" + getValue()))
      m_TextValue.setText("" + getValue());
    m_TextValue.setToolTipText(((BaseObject) getValue()).getTipText());
  }
  
  /**
   * Checks whether inline editing is available.
   * 
   * @return		always true
   */
  public boolean isInlineEditingAvailable() {
    return true;
  }

  /**
   * Sets the value to use.
   * 
   * @param value	the value to use
   */
  public void setInlineValue(String value) {
    setValue(new BaseText(Utils.unbackQuoteChars(value)));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return Utils.backQuoteChars(((BaseText) getValue()).getValue());
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		always true
   */
  public boolean isInlineValueValid(String value) {
    return true;
  }
}
