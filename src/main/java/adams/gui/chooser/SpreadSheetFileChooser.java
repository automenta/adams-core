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
 * SpreadSheetFileChooser.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.io.FileFormatHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adams.data.io.input.AbstractSpreadSheetReader;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.io.output.AbstractSpreadSheetWriter;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7572 $
 */
public class SpreadSheetFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<SpreadSheetReader,SpreadSheetWriter>
  implements FileTypeDeterminingFileChooser<SpreadSheetReader,SpreadSheetWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -6341967475735162796L;

  /** the file filters for the readers. */
  protected static List<ExtensionFileFilterWithClass> m_ReaderFileFilters;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public SpreadSheetFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public SpreadSheetFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public SpreadSheetFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Returns whether the filters have already been initialized.
   *
   * @return		true if the filters have been initialized
   */
  @Override
  protected boolean getFiltersInitialized() {
    return (m_ReaderFileFilters != null);
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(true, AbstractSpreadSheetReader.getReaders());
    initFilters(false, AbstractSpreadSheetWriter.getWriters());
  }

  /**
   * initializes the SpreadSheetFileExtensionFilters.
   *
   * @param reader	if true then the reader filters are initialized
   * @param classnames	the classnames of the converters
   */
  protected static void initFilters(boolean reader, String[] classnames) {
    int					i;
    String 				classname;
    Class 				cls;
    String[] 				ext;
    String 				desc;
    Object		 		converter;
    ExtensionFileFilterWithClass 	filter;

    if (reader)
      m_ReaderFileFilters = new ArrayList<>();
    else
      m_WriterFileFilters  = new ArrayList<>();

    for (i = 0; i < classnames.length; i++) {
      classname = classnames[i];

      // get data from converter
      try {
	cls       = Class.forName(classname);
	converter = cls.newInstance();
	if (reader) {
	  desc = ((FileFormatHandler) converter).getFormatDescription();
	  ext  = ((FileFormatHandler) converter).getFormatExtensions();
	}
	else {
	  desc = ((SpreadSheetWriter) converter).getFormatDescription();
	  ext  = ((SpreadSheetWriter) converter).getFormatExtensions();
	}
      }
      catch (Exception e) {
	System.err.println("Failed to set up '" + classname + "':");
	e.printStackTrace();
	cls       = null;
	converter = null;
	ext       = new String[0];
	desc      = "";
      }

      if (converter == null)
	continue;

      // reader?
      if (reader) {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_ReaderFileFilters.add(filter);
      }
      else {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_WriterFileFilters.add(filter);
      }
    }

    if (reader)
      Collections.sort(m_ReaderFileFilters);
    else
      Collections.sort(m_WriterFileFilters);
  }

  /**
   * Returns the file filters for opening files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getOpenFileFilters() {
    return m_ReaderFileFilters;
  }

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getSaveFileFilters() {
    return m_WriterFileFilters;
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return SpreadSheetReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return SpreadSheetWriter.class;
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected SpreadSheetReader getDefaultReader() {
    return new CsvSpreadSheetReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected SpreadSheetWriter getDefaultWriter() {
    return new CsvSpreadSheetWriter();
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public SpreadSheetReader getReaderForFile(File file) {
    SpreadSheetReader	result;

    result = null;

    for (ExtensionFileFilterWithClass filter: m_ReaderFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (SpreadSheetReader) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate reader '" + filter.getClassname() + "':");
	  e.printStackTrace();
	}
      }
    }

    return result;
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public SpreadSheetWriter getWriterForFile(File file) {
    SpreadSheetWriter	result;

    result = null;

    for (ExtensionFileFilterWithClass filter: m_WriterFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (SpreadSheetWriter) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate writer '" + filter.getClassname() + "':");
	  e.printStackTrace();
	}
      }
    }

    return result;
  }
}
