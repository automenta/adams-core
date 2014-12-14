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
 * Pre.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xhtml;

import adams.doc.xml.AbstractSimpleTag;

/**
 * Verbatim PRE tag.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7060 $
 */
public class Pre
  extends AbstractSimpleTag {

  /** for serialization. */
  private static final long serialVersionUID = -6886830294571668230L;

  /**
   * Initializes the tag.
   */
  public Pre() {
    super("pre");
  }

  /**
   * Initializes the tag with the content.
   * 
   * @param content	the initial content
   */
  public Pre(String content) {
    super("pre", content);
  }
}
