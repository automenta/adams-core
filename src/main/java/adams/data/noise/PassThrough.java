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
 * PassThrough.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.noise;

import adams.data.container.DataContainer;

/**
 <!-- globalinfo-start -->
 * A dummy denoiser, which removes no noise at all.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-regions (property: recordRegions)
 *         If set to true, the noisy regions will be recorded as well.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class PassThrough
  extends AbstractDenoiser {

  /** for serialization. */
  private static final long serialVersionUID = 6019995704005696521L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "A dummy denoiser, which removes no noise at all.";
  }

  /**
   * Performs the actual denoising - just copies the input data to the output.
   *
   * @param data	the data to process
   * @return		the denoised data
   */
  protected DataContainer processData(DataContainer data) {
    return (DataContainer) data.getClone();
  }
}
