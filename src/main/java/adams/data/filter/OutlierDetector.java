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
 * OutlierDetector.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.filter;

import java.util.List;

import adams.data.NotesHandler;
import adams.data.container.DataContainer;
import adams.data.outlier.AbstractOutlierDetector;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 <!-- globalinfo-start -->
 * A filter that runs an outlier detector over the data and attaches the generated detections as notes to the chromatogram.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-detector &lt;adams.data.outlier.AbstractOutlierDetector [options]&gt; (property: detector)
 * &nbsp;&nbsp;&nbsp;The outlier detector to use (the detections generated by the algorithm get
 * &nbsp;&nbsp;&nbsp;attached to the chromatogram).
 * &nbsp;&nbsp;&nbsp;default: adams.data.outlier.PassThrough
 * </pre>
 *
 * <pre>-only-warning (property: onlyWarning)
 * &nbsp;&nbsp;&nbsp;If enabled, the detections get added merely as warnings instead of as errors.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7774 $
 * @param <T> the type of data to pass through the filter
 */
public class OutlierDetector<T extends DataContainer>
  extends AbstractDatabaseConnectionFilter<T> {

  /** for serialization. */
  private static final long serialVersionUID = -7381879273745030342L;

  /** the detector algorithm. */
  protected AbstractOutlierDetector m_OutlierDetector;

  /** whether the detection is only added as warning instead of error. */
  protected boolean m_OnlyWarning;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A filter that runs an outlier detector over the data and "
      + "attaches the generated detections as notes to the chromatogram.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "detector", "detector",
	    new adams.data.outlier.PassThrough());

    m_OptionManager.add(
	    "only-warning", "onlyWarning",
	    false);
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  @Override
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Sets the outlier detector.
   *
   * @param value	the algorithm
   */
  public void setDetector(AbstractOutlierDetector value) {
    m_OutlierDetector = value;
    updateDatabaseConnection();
    reset();
  }

  /**
   * Returns the current outlier detector.
   *
   * @return 		the algorithm
   */
  public AbstractOutlierDetector getDetector() {
    return m_OutlierDetector;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for displaying in
   * 			the GUI or for listing the options.
   */
  public String detectorTipText() {
    return
        "The outlier detector to use (the detections generated by the "
      + "algorithm get attached to the chromatogram).";
  }

  /**
   * Sets whether the detections are added as error or warning.
   *
   * @param value	if true then the detections are added as warning
   * 			instead of as error
   */
  public void setOnlyWarning(boolean value) {
    m_OnlyWarning = value;
    reset();
  }

  /**
   * Returns whether the detections are added as error or warning.
   *
   * @return 		true if the detections get added as warning instead
   * 			of as error
   */
  public boolean getOnlyWarning() {
    return m_OnlyWarning;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for displaying in
   * 			the GUI or for listing the options.
   */
  public String onlyWarningTipText() {
    return "If enabled, the detections get added merely as warnings instead of as errors.";
  }

  /**
   * Updates the database connection in the outlier detectors.
   */
  @Override
  protected void updateDatabaseConnection() {
    if (m_OutlierDetector instanceof DatabaseConnectionHandler)
      ((DatabaseConnectionHandler) m_OutlierDetector).setDatabaseConnection(getDatabaseConnection());
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected T processData(T data) {
    T				result;
    AbstractOutlierDetector 	detector;
    List<String>		detection;
    int				i;
    NotesHandler		handler;

    detector = m_OutlierDetector.shallowCopy(true);
    detection = detector.detect(data);
    detector.destroy();

    getLogger().info("Data: " + data + ", detection size: " + detection.size());
    result = (T) data.getClone();
    if (result instanceof NotesHandler) {
      handler = (NotesHandler) result;
      for (i = 0; i < detection.size(); i++) {
	if (m_OnlyWarning)
	  handler.getNotes().addWarning(m_OutlierDetector.getClass(), detection.get(i));
	else
	  handler.getNotes().addError(m_OutlierDetector.getClass(), detection.get(i));
	getLogger().info((i+1) + ". " + detection.get(i));
      }
    }

    return result;
  }
}
