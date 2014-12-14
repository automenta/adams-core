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
 * SizeOf.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import java.lang.reflect.Method;

/**
 * Helper class for measuring the size of objects using 
 * <a href="http://www.jroller.com/maxim/entry/again_about_determining_size_of" target="_blank">Maxim Zakharenkov's SizeOf agent</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4630 $
 */
public class SizeOf {

  /** the class to use. */
  public final static String CLASS_SIZEOF = "sizeof.agent.SizeOfAgent";

  /** the method to use. */
  public final static String METHOD_FULLSIZEOF = "fullSizeOf";
  
  /** whether the SizeOf agent is available. */
  protected static Boolean m_Available;

  /** the SizeOf agent class. */
  protected static Class m_Class;

  /** the SizeOf agent method to use. */
  protected static Method m_Method;
  
  /**
   * Returns whether the SizeOf agent is available or not.
   *
   * @return		true if agent available
   */
  public static synchronized boolean isSizeOfAgentAvailable() {
    Long	size;
    
    if (m_Available == null) {
      try {
	m_Class  = Class.forName(CLASS_SIZEOF);
	m_Method = m_Class.getMethod(METHOD_FULLSIZEOF, new Class[]{Object.class});
      }
      catch (Exception e) {
	m_Class     = null;
	m_Method    = null;
	m_Available = false;
	System.err.println("SizeOf agent not on classpath available!");
      }
      
      if (m_Class != null) {
	try {
	  size        = (Long) m_Method.invoke(null, new Object[]{1});
	  m_Available = (size > 0);
	}
	catch (Exception e) {
	  e.printStackTrace();
	  m_Available = false;
	  System.err.println("SizeOf agent not available! Use '-javaagent:sizeofag.jar' on commandline.");
	}
      }
    }

    return m_Available;
  }

  /**
   * Returns the size of an object.
   *
   * @param obj		the object to measure
   * @return		the size or -1 if agent not available
   */
  public static int sizeOf(Object obj) {
    Long	result;
    
    result = -1L;
    if (isSizeOfAgentAvailable()) {
      try {
	result = (Long) m_Method.invoke(null, new Object[]{obj});
      }
      catch (Exception e) {
	result = -1L;
      }
    }
    
    return result.intValue();
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    System.out.println(sizeOf(args));
    System.out.println(sizeOf(1));
    System.out.println(sizeOf(1.0));
    System.out.println(sizeOf(new DateFormat("yyyy-MM-dd")));
  }
}
