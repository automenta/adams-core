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
 * SimpleConsoleHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.logging;

import java.util.logging.LogRecord;

/**
 * Specialized logger for outputting the logging in the console.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7676 $
 */
public class SimpleConsoleHandler
  extends AbstractLogHandler {

  /**
   * Publish a <tt>LogRecord</tt>.
   * <p>
   * The logging request was made initially to a <tt>Logger</tt> object,
   * which initialized the <tt>LogRecord</tt> and forwarded it here.
   * <p>
   * The <tt>Handler</tt>  is responsible for formatting the message, when and
   * if necessary.  The formatting should include localization.
   *
   * @param  record  description of the log event. A null record is
   *                 silently ignored and is not published
   */
  @Override
  protected void doPublish(LogRecord record) {
    System.out.println(LoggingHelper.assembleMessage(record).toString());
  }
}
