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
// * TableExistsTest.java
// * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
// */
//package adams.flow.condition.bool;
//
//import junit.framework.Test;
//import junit.framework.TestSuite;
//import adams.env.Environment;
//import adams.flow.core.AbstractActor;
//
///**
// * Tests the 'TableExists' boolean condition.
// * <p/>
// * NB: Dummy test.
// * 
// * @author  fracpete (fracpete at waikato dot ac dot nz)
// * @version $Revision: 7340 $
// */
//public class TableExistsTest
//  extends AbstractBooleanConditionTestCase {
//
//  /**
//   * Constructs the test case. Called by subclasses.
//   *
//   * @param name 	the name of the test
//   */
//  public TableExistsTest(String name) {
//    super(name);
//  }
//  
//  /**
//   * Returns the owning actors to use in the regression test (one per regression setup).
//   *
//   * @return		the owners (not all conditions might need owners)
//   */
//  @Override
//  protected AbstractActor[] getRegressionOwners() {
//    return new AbstractActor[0];
//  }
//
//  /**
//   * Returns the input data to use in the regression test (one per regression setup).
//   *
//   * @return		the input data
//   */
//  @Override
//  protected Object[] getRegressionInputs() {
//    return new Object[0];
//  }
//
//  /**
//   * Returns the setups to use in the regression test.
//   *
//   * @return		the setups
//   */
//  @Override
//  protected AbstractBooleanCondition[] getRegressionSetups() {
//    return new TableExists[0];
//  }
//
//  /**
//   * Returns a test suite.
//   *
//   * @return		the test suite
//   */
//  public static Test suite() {
//    return new TestSuite(TableExistsTest.class);
//  }
//
//  /**
//   * Runs the test from commandline.
//   *
//   * @param args	ignored
//   */
//  public static void main(String[] args) {
//    Environment.setEnvironmentClass(Environment.class);
//    runTest(suite());
//  }
//}