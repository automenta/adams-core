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
 * AbstractFeatureConverter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.List;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for generic feature converter schemes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <D> the type of dataset that is generated
 * @param <R> the type of row that is generated
 */
public abstract class AbstractFeatureConverter<D,R>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4745159188031576718L;

  /** the header. */
  protected D m_Header;

  /** the data types. */
  protected HeaderDefinition m_HeaderDefinition;
  
  /** the name of the dataset. */
  protected String m_Dataset;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dataset", "dataset",
	    HeaderDefinition.DEFAULT_DATASET_NAME);
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();
    
    m_Header           = null;
    m_HeaderDefinition = null;
  }

  /**
   * Sets the dataset name to use.
   *
   * @param value	the dataset name
   */
  public void setDataset(String value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the dataset name in use.
   *
   * @return		the dataset name
   */
  public String getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The dataset name to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns whether the header has been initialized.
   * 
   * @return		true if initialized
   */
  public boolean isInitialized() {
    return (m_Header != null);
  }
  
  /**
   * Returns the current header, if any.
   * 
   * @return		the header, null if none generated yet
   */
  public D getHeader() {
    return m_Header;
  }
  
  /**
   * Returns the current header definition, if any.
   * 
   * @return		the header definition, null if none supplied yet
   */
  public HeaderDefinition getHeaderDefinition() {
    return m_HeaderDefinition;
  }
  
  /**
   * Returns the class of the dataset that the converter generates.
   * 
   * @return		the format
   */
  public abstract Class getDatasetFormat();
  
  /**
   * Returns the class of the row that the converter generates.
   * 
   * @return		the format
   */
  public abstract Class getRowFormat();
  
  /**
   * Performs the actual generation of the header data structure using the 
   * supplied header definition.
   * 
   * @param header	the header definition
   * @return		the dataset structure
   */
  protected abstract D doGenerateHeader(HeaderDefinition header);
  
  /**
   * Generates the header data structure using the supplied header definition.
   * 
   * @param header	the header definition
   * @return		the dataset structure
   */
  public D generateHeader(HeaderDefinition header) {
    m_Header           = doGenerateHeader(header);
    m_HeaderDefinition = header;
    
    return m_Header;
  }

  /**
   * Performs the actual generation of a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   */
  protected abstract R doGenerateRow(List<Object> data);

  /**
   * Generates a row from the raw data.
   * 
   * @param data	the data of the row, elements can be null (= missing)
   * @return		the dataset structure
   * @see		#generateHeader(List, List)
   */
  public R generateRow(List<Object> data) {
    if (m_Header == null)
      throw new IllegalStateException("No header available! generatedHeader called?");
    if (m_HeaderDefinition.size() != data.size())
      throw new IllegalStateException("Header and data differ in size: " + m_HeaderDefinition.size() + " != " + data.size());
    
    return doGenerateRow(data);
  }
}
