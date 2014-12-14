
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
 * Platform.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.test;

/**
 * Identifies what platform the test is available on.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4748 $
 */
public enum Platform {
  /** all. */
  ALL,
  /** Mac only. */
  MAC,
  /** Windows only. */
  WINDOWS,
  /** Linux only. */
  LINUX
}
