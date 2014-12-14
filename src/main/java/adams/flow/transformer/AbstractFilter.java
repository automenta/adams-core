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
 * AbstractFilter.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.container.DataContainer;
import adams.db.DatabaseConnectionHandler;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 * Ancestor for domain-specific filter transformers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6830 $
 */
public abstract class AbstractFilter
  extends AbstractDataContainerTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4527040722924866539L;

  /** the filter to apply. */
  protected adams.data.filter.AbstractFilter m_Filter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Filters data using the specified filter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new adams.data.filter.PassThrough());
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(adams.data.filter.AbstractFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter in use.
   *
   * @return		the filter
   */
  public adams.data.filter.AbstractFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to use for filtering the data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "filter", m_Filter);
  }

  /**
   * Returns the data container class in use.
   *
   * @return		the container class
   */
  @Override
  protected abstract Class getDataContainerClass();

  /**
   * Determines the database connection in the flow.
   *
   * @return		the database connection to use
   */
  protected abstract adams.db.AbstractDatabaseConnection getDatabaseConnection();

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_Filter instanceof DatabaseConnectionHandler)
	((DatabaseConnectionHandler) m_Filter).setDatabaseConnection(getDatabaseConnection());
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    DataContainer	chr;

    result = null;

    chr = m_Filter.filter((DataContainer) m_InputToken.getPayload());
    if (chr == null)
      result = "No data obtained from filter: " + m_InputToken;
    else
      m_OutputToken = new Token(chr);

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);

    m_Filter.cleanUp();

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.PREPROCESSOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
