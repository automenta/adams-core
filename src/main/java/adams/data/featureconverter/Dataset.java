///*
// *   This program is free software: you can redistribute it and/or modify
// *   it under the terms of the GNU General Public License as published by
// *   the Free Software Foundation, either version 3 of the License, or
// *   (at your option) any later version.
// *
// *   This program is distributed in the hope that it will be useful,
// *   but WITHOUT ANY WARRANTY; without even the implied warranty of
// *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *   GNU General Public License for more details.
// *
// *   You should have received a copy of the GNU General Public License
// *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
///**
// * Dataset.java
// * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
// */
//package adams.data.featureconverter;
//
//import adams.data.spreadsheet.SpreadSheet;
//
//
///**
// * Turns the features into ADAMS dataset format.
// * 
// * @author  fracpete (fracpete at waikato dot ac dot nz)
// * @version $Revision$
// */
//public class Dataset
//  extends SpreadSheet {
//
//  /** for serialization. */
//  private static final long serialVersionUID = 2655075828639195810L;
//
//  /**
//   * Returns a string describing the object.
//   *
//   * @return 			a description suitable for displaying in the gui
//   */
//  @Override
//  public String globalInfo() {
//    return "Turns the features into ADAMS dataset format.";
//  }
//
//  /**
//   * Returns the default spreadsheet to use.
//   * 
//   * @return		the spreadsheet
//   */
//  @Override
//  protected adams.data.spreadsheet.SpreadSheet getDefaultSpreadSheetType() {
//    return new adams.ml.data.Dataset();
//  }
//}
