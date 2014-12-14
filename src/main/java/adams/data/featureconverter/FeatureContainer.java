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
 * FeatureContainer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.List;

import adams.flow.container.FeatureConverterContainer;

/**
 <!-- globalinfo-start -->
 * Simply returns the header definition&#47;row in a adams.flow.container.FeatureConverterContainer container.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-dataset &lt;java.lang.String&gt; (property: dataset)
 * &nbsp;&nbsp;&nbsp;The dataset name to use.
 * &nbsp;&nbsp;&nbsp;default: Dataset
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FeatureContainer
  extends AbstractFeatureConverter {

  /** for serialization. */
  private static final long serialVersionUID = 2245576408802564218L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Simply returns the header definition/row "
	+ "in a " + FeatureConverterContainer.class.getName() + " container.";
  }

  /**
   * Returns the class of the dataset that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getDatasetFormat() {
    return FeatureConverterContainer.class;
  }

  /**
   * Returns the class of the row that the converter generates.
   * 
   * @return		the format
   */
  @Override
  public Class getRowFormat() {
    return FeatureConverterContainer.class;
  }

  /**
   * Performs the actual generation of the header data structure using the 
   * supplied header definition.
   * 
   * @param header	the header definition
   * @return		the dataset structure
   */
  @Override
  protected FeatureConverterContainer doGenerateHeader(HeaderDefinition header) {
    HeaderDefinition	headerNew;
    
    headerNew = header.getClone();
    headerNew.setDataset(m_Dataset);
    
    return new FeatureConverterContainer(headerNew, null);
  }
  
  /**
   * Generates the header data structure using the supplied header definition.
   * 
   * @param header	the header definition
   * @return		the dataset structure
   */
  @Override
  public FeatureConverterContainer generateHeader(HeaderDefinition header) {
    FeatureConverterContainer	result;

    result             = (FeatureConverterContainer) super.generateHeader(header);
    m_HeaderDefinition = (HeaderDefinition) result.getValue(FeatureConverterContainer.VALUE_HEADER);
    
    return result;
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  @Override
  protected FeatureConverterContainer doGenerateRow(List data) {
    return new FeatureConverterContainer(m_HeaderDefinition, data);
  }
}
