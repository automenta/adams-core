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
 * LogT.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import adams.core.base.BaseDateTime;
import adams.db.indices.Index;
import adams.db.indices.IndexColumn;
import adams.db.indices.Indices;
import adams.db.types.Auto_increment_type;
import adams.db.types.SQL_type;

/**
 * Table for storing log messages.
 *
 * @author Fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8118 $
 */
public class LogT
  extends AbstractIndexedTable {

  /** for serialization. */
  private static final long serialVersionUID = -5955333734406125700L;

  /** the table name. */
  public final static String TABLE_NAME = "logging";

  /** the table manager. */
  protected static TableManager<LogT> m_TableManager;

  /**
   * The constructor.

   * @param dbcon	the database context this table is used in
   */
  private LogT(AbstractDatabaseConnection dbcon) {
    super(dbcon, TABLE_NAME);
  }

  /**
   * Returns column names and data types.
   *
   * @return	Column Mapping
   */
  @Override
  protected ColumnMapping getColumnMapping() {
    ColumnMapping 	result;

    result = new ColumnMapping();
    result.addMapping("AUTO_ID",    new Auto_increment_type());         // automatic generated id
    result.addMapping("HOST",       new SQL_type(Types.VARCHAR,255));   // the host name
    result.addMapping("IP",         new SQL_type(Types.VARCHAR,50));    // the IP address
    result.addMapping("TYPE",       new SQL_type(Types.VARCHAR,255));   // type of the log message
    result.addMapping("MESSAGE",    new SQL_type(Types.LONGVARCHAR));   // the message itself (props format)
    result.addMapping("GENERATION", new SQL_type(Types.TIMESTAMP));     // the timestamp of the message
    result.addMapping("SOURCE",     new SQL_type(Types.LONGVARCHAR));   // the origin of the message
    result.addMapping("STATUS",     new SQL_type(Types.VARCHAR,255));   // the status of the message

    return result;
  }

  /**
   * Returns required indices for this table.
   *
   * @return	Indices
   */
  @Override
  protected Indices getIndices() {
    Indices 	result;
    Index 	index;

    result = new Indices();

    index = new Index();
    index.add(new IndexColumn("AUTO_ID"));
    result.add(index);

    index = new Index();
    index.add(new IndexColumn("HOST"));
    result.add(index);

    index = new Index();
    index.add(new IndexColumn("IP"));
    result.add(index);

    index = new Index();
    index.add(new IndexColumn("TYPE"));
    result.add(index);

    index = new Index();
    index.add(new IndexColumn("GENERATION"));
    result.add(index);

    index = new Index();
    index.add(new IndexColumn("SOURCE", true, 255));
    result.add(index);

    index = new Index();
    index.add(new IndexColumn("STATUS"));
    result.add(index);

    return result;
  }

  /**
   * Loads a log entry from db, using the database ID.
   *
   * @param auto_id	the auto_id to retrieve
   * 			and field from
   * @return		the log entry, null if not found
   */
  public LogEntry load(int auto_id) {
    LogEntry	result;
    ResultSet 	rs;

    result = null;

    rs = null;
    try {
      rs     = select("*", "AUTO_ID = " + auto_id);
      result = resultsetToObject(rs);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load auto_id=" + auto_id, e);
    }
    finally {
      closeAll(rs);
    }

    return result;
  }

  /**
   * Loads log entries from the database that match the conditions.
   *
   * @param cond	the conditions for the entries to match
   * @return		the log entries
   */
  public List<LogEntry> load(LogEntryConditions cond) {
    List<LogEntry>	result;
    LogEntry		log;
    ResultSet		rs;
    StringBuilder	sqlWhere;
    List<String>	where;
    int			i;

    result = new ArrayList<>();

    cond.update();

    // translate conditions
    where = new ArrayList<>();
    if (!cond.getHost().isEmpty() && !cond.getHost().isMatchAll())
      where.add("HOST RLIKE " + backquote(cond.getHost()));
    if (!cond.getIP().isEmpty() && !cond.getIP().isMatchAll())
      where.add("IP RLIKE " + backquote(cond.getIP()));
    if (!cond.getType().isEmpty() && !cond.getType().isMatchAll())
      where.add("TYPE RLIKE " + backquote(cond.getType()));
    if (!cond.getStatus().isEmpty() && !cond.getStatus().isMatchAll())
      where.add("STATUS RLIKE " + backquote(cond.getStatus()));
    if (!cond.getSource().isEmpty() && !cond.getSource().isMatchAll())
      where.add("SOURCE RLIKE " + backquote(cond.getSource()));
    if (!cond.getGenerationStartDate().equals(BaseDateTime.infinityPast()))
      where.add("GENERATION >= '" + cond.getGenerationStartDate().stringValue() + "'");
    if (!cond.getGenerationEndDate().equals(BaseDateTime.infinityFuture()))
      where.add("GENERATION <= '" + cond.getGenerationEndDate().stringValue() + "'");

    // generate sql
    sqlWhere = new StringBuilder();
    for (i = 0; i < where.size(); i++) {
      if (i > 0)
	sqlWhere.append(" AND ");
      sqlWhere.append(where.get(i));
    }
    if (cond.getLatest())
      sqlWhere.append(" ORDER BY GENERATION DESC");
    else
      sqlWhere.append(" ORDER BY GENERATION ASC");
    if (cond.getLimit() > -1)
      sqlWhere.append(" LIMIT " + cond.getLimit());

    // retrieve data
    rs = null;
    try {
      rs = select("*", sqlWhere.toString());
      while ((log = resultsetToObject(rs)) != null)
	result.add(log);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load: " + sqlWhere, e);
    }
    finally {
      closeAll(rs);
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Turns the values at the next position into a substance object
   * if possible.
   *
   * @param rs		the result set
   * @return		the generate object or null in case of an error
   */
  protected LogEntry resultsetToObject(ResultSet rs) {
    LogEntry	result;

    result = null;

    try {
      if (rs.next()) {
	result = new LogEntry();
	result.setDatabaseID(rs.getInt("AUTO_ID"));
	result.setHost(rs.getString("HOST"));
	result.setIP(rs.getString("IP"));
	result.setType(rs.getString("TYPE"));
	result.setMessage(rs.getString("MESSAGE"));
	result.setGeneration(rs.getTimestamp("GENERATION"));
	result.setSource(rs.getString("SOURCE"));
	result.setStatus(rs.getString("STATUS"));
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to processed resultset", e);
    }

    return result;
  }

  /**
   * Looks for a log entry in the DB, looking for the auto_id.
   *
   * @param log		the log entry
   * @return		true if a log entry already exists
   */
  public boolean exists(LogEntry log) {
    return (load(log.getDatabaseID()) != null);
  }

  /**
   * Inserts or updates a substance.
   *
   * @param log		the log entry to store/update
   * @param update	if true then an UPDATE will be performed, otherwise
   * 			an INSERT
   * @return  		true if insert successful or already present
   */
  protected boolean store(LogEntry log, boolean update) {
    boolean		result;
    String		sql;
    PreparedStatement	stmt;

    result = false;
    stmt   = null;

    if (update) {
      sql  = "UPDATE " + getTableName() + " ";
      sql += "SET HOST = ?, IP = ?, TYPE = ?, MESSAGE = ?, GENERATION = ?, SOURCE = ?, STATUS = ? ";
      sql += "WHERE AUTO_ID = ?";
      try {
	stmt = prepareStatement(sql);
	stmt.setString(1, log.getHost());
	stmt.setString(2, log.getIP());
	stmt.setString(3, log.getType());
	stmt.setString(4, log.getMessageAsProperties().toString());
	stmt.setTimestamp(5, new Timestamp(log.getGeneration().getTime()));
	stmt.setString(6, log.getSource());
	stmt.setString(7, log.getStatus());
	stmt.setInt(8, log.getDatabaseID());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to prepare update statement: " + sql, e);
	stmt = null;
      }
    }
    else {
      sql  = "INSERT INTO " + getTableName() + "(HOST, IP, TYPE, MESSAGE, GENERATION, SOURCE, STATUS) ";
      sql += "VALUES(?, ?, ?, ?, ?, ?, ?)";
      try {
	stmt = prepareStatement(sql);
	stmt.setString(1, log.getHost());
	stmt.setString(2, log.getIP());
	stmt.setString(3, log.getType());
	stmt.setString(4, log.getMessageAsProperties().toString());
	stmt.setTimestamp(5, new Timestamp(log.getGeneration().getTime()));
	stmt.setString(6, log.getSource());
	stmt.setString(7, log.getStatus());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to prepare insert statement: " + sql, e);
	stmt = null;
      }
    }

    if (stmt != null) {
      try {
	getLogger().info("Executing prepared statement: " + sql);
	result = stmt.execute();
	getLogger().info("  --> result: " + result);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Storing failed using prepared statement: " + sql, e);
	result = false;
      }
    }

    return result;
  }

  /**
   * Adds a log entry.
   *
   * @param log		the log entry to add
   * @return  		true if insert successful or already present
   */
  public boolean add(LogEntry log) {
    if (exists(log))
      return true;
    else
      return store(log, false);
  }

  /**
   * Updates a log entry.
   *
   * @param log		the log entry to update
   * @return  		true if update successful or false if not present
   */
  public boolean update(LogEntry log) {
    if (exists(log))
      return store(log, true);
    else
      return false;
  }

  /**
   * Removes a log entry from the DB.
   *
   * @param log		the log entry
   * @return		true if successful
   */
  public boolean remove(LogEntry log) {
    boolean	result;
    String	sql;

    sql = "DELETE FROM " + getTableName() + " "
       	+ "WHERE AUTO_ID = " + log.getDatabaseID() + "";

    getLogger().info("Removing: " + sql);
    try {
      execute(sql);
      result = true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error removing: " + sql, e);
      result = false;
    }

    return result;
  }

  /**
   * Initializes the table. Used by the "InitializeTables" tool.
   *
   * @param dbcon	the database context
   */
  public static synchronized void initTable(AbstractDatabaseConnection dbcon) {
    getSingleton(dbcon).init();
  }

  /**
   * Returns the singleton of the table.
   *
   * @param dbcon	the database connection to get the singleton for
   * @return		the singleton
   */
  public static synchronized LogT getSingleton(AbstractDatabaseConnection dbcon) {
    if (m_TableManager == null)
      m_TableManager = new TableManager<>(TABLE_NAME, dbcon.getOwner());
    if (!m_TableManager.has(dbcon))
      m_TableManager.add(dbcon, new LogT(dbcon));

    return m_TableManager.get(dbcon);
  }
}
