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
 * Paragraph.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.doc.xhtml;


/**
 * Represents the P tag.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7060 $
 */
public class Paragraph
  extends AbstractMixedTag {

  /** for serialization. */
  private static final long serialVersionUID = -7501143393940807106L;

  /**
   * Initializes the paragraph tag.
   */
  public Paragraph() {
    super("p");
  }

  /**
   * Initializes the paragraph tag.
   *
   * @param content	the content of the paragraph.
   */
  public Paragraph(String content) {
    super("p", content);
  }
}
