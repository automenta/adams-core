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
 * BlobContainer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.blob;

import adams.core.Utils;
import adams.data.container.AbstractSimpleContainer;
import adams.data.id.MutableIDHandler;

/**
 * Simple container for blob (ie byte array) objects that also offers notes 
 * and a report for storing meta-data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6656 $
 */
public class BlobContainer
  extends AbstractSimpleContainer<byte[]>
  implements MutableIDHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4567572281060847914L;
  
  /** the ID. */
  protected String m_ID;
  
  /**
   * Initializes the container.
   */
  public BlobContainer() {
    super();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_ID = "";
  }
  
  /**
   * Returns a clone of the image.
   * 
   * @return		the clone
   */
  @Override
  protected byte[] cloneContent() {
    return m_Content.clone();
  }

  /**
   * Returns the ID.
   * 
   * @return		the ID
   */
  @Override
  public String getID() {
    return m_ID;
  }

  /**
   * Sets the ID.
   *
   * @param value	the ID
   */
  @Override
  public void setID(String value) {
    m_ID = value;
  }
  
  /**
   * Returns a string representation of the container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "id=" + m_ID + ", " + "content=" + Utils.arrayToString(m_Content) + ", report=" + m_Report + ", notes=" + m_Notes;
  }
}
