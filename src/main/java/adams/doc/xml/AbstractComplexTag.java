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
 * AbstractComplexTag.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xml;

/**
 * Ancestor for tags that have nested tags.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7060 $
 */
public abstract class AbstractComplexTag
  extends AbstractTag {

  /** for serialization. */
  private static final long serialVersionUID = -43542338015882364L;

  /** whether to output empty tags. */
  protected boolean m_AllowEmpty;
  
  /**
   * Initializes the tag.
   *
   * @param tag		the name of the tag
   */
  public AbstractComplexTag(String tag) {
    super(tag);
    
    m_AllowEmpty = false;
  }

  /**
   * Checks whether there is any content to append.
   *
   * @return		true if content available
   */
  @Override
  public boolean hasContent() {
    return (getChildCount() > 0);
  }
  
  /**
   * Appends the content of the tag to the buffer.
   *
   * @param buffer	the buffer to append the content to
   */
  @Override
  protected void appendContent(StringBuilder buffer) {
    int				i;
    AbstractTag	child;

    for (i = 0; i < getChildCount(); i++) {
      child = (AbstractTag) getChildAt(i);
      child.toXML(buffer);
    }
  }

  /**
   * Turns the XML tree into its string representation.
   *
   * @param buffer	the buffer to append the tag to
   */
  @Override
  public void toXML(StringBuilder buffer) {
    if ((getChildCount() > 0) || m_AllowEmpty)
      super.toXML(buffer);
  }
}
