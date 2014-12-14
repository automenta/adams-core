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
 * ActorType.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.provenance;

/**
 * Enumeration of actor types as used in provenance.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5990 $
 */
public enum ActorType {

  /** data generator. */
  DATAGENERATOR,
  /** preprocessor. */
  PREPROCESSOR,
  /** model generator.*/
  MODEL_GENERATOR,
  /** evaluator.*/
  EVALUATOR,
  /** other. */
  OTHER
}