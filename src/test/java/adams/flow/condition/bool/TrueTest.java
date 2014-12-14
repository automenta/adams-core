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
// * TrueTest.java
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
// * Tests the 'True' boolean condition.
// * 
// * @author  fracpete (fracpete at waikato dot ac dot nz)
// * @version $Revision: 7333 $
// */
//public class TrueTest
//  extends AbstractBooleanConditionTestCase {
//
//  /**
//   * Constructs the test case. Called by subclasses.
//   *
//   * @param name 	the name of the test
//   */
//  public TrueTest(String name) {
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
//    return new AbstractActor[]{null};
//  }
//
//  /**
//   * Returns the input data to use in the regression test (one per regression setup).
//   *
//   * @return		the input data
//   */
//  @Override
//  protected Object[] getRegressionInputs() {
//    return new Object[]{"blah"};
//  }
//
//  /**
//   * Returns the setups to use in the regression test.
//   *
//   * @return		the setups
//   */
//  @Override
//  protected AbstractBooleanCondition[] getRegressionSetups() {
//    return new True[]{new True()};
//  }
//
//  /**
//   * Returns a test suite.
//   *
//   * @return		the test suite
//   */
//  public static Test suite() {
//    return new TestSuite(TrueTest.class);
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
