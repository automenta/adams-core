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
 * Job.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.multiprocess;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Vector;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingObject;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;

/**
 * A job is a unit of execution.
 *
 * @author dale
 * @version $Revision: 7171 $
 */
public abstract class Job
  extends LoggingObject
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4365906331615932775L;

  /** identifying name of job. */
  protected String m_jobInfo;

  /** parameters necessary to process job. */
  protected Hashtable<String,Serializable> m_jobParams;

  /** Jobs this Job requires to have finished first. */
  protected Vector<Job> m_depends;

  /** Has this job completed processing? */
  protected boolean m_complete;

  /** Object to call once job has been completed. */
  protected JobCompleteListener m_completed;

  /** whether an error occurred in the execution. */
  protected String m_ExecutionError;

  /**
   * Job constructor. Create a new Job with no identifier.
   */
  public Job() {
    this("");
  }

  /**
   * Job constructor. Create a new Job with given identifier.
   *
   * @param info  	Job function
   */
  public Job(String info) {
    super();

    m_jobParams      = new Hashtable<>();
    m_depends        = new Vector<>();
    m_jobInfo        = info;
    m_complete       = false;
    m_completed      = null;
    m_ExecutionError = null;
  }

  /**
   * Returns the job info/identifier.
   *
   * @return		the info
   */
  public String getJobInfo() {
    return m_jobInfo;
  }

  /**
   * Get this Job as a Vector.
   *
   * @return		Job Vector
   */
  public Vector<Job> getAsVector() {
    Vector<Job> vj = new Vector<>();
    vj.add(this);
    return(vj);
  }

  /**
   * Called once a job has completed execution.
   *
   * @param j		Job
   * @param jr		Result of Job
   */
  public void jobCompleted(Job j, JobResult jr) {
    if (m_completed != null)
      m_completed.jobCompleted(new JobCompleteEvent(this, j,jr));
  }


  /**
   * Add a dependency to this Job.
   *
   * @param j		Job that needs to be completed before this one can run
   */
  public void addDependency(Job j) {
    m_depends.add(j);
  }

  /**
   * Returns all jobs this job depends on.
   *
   * @return		the dependencies
   */
  public Vector<Job> getDependencies() {
    return new Vector<>(m_depends);
  }

  /**
   * Add a parameter to this Job.
   *
   * @param name 	paramater name
   * @param o 		parameter to be used by JobProcessor
   */
  public void addParam(String name, Serializable o) {
    m_jobParams.put(name,o);
  }

  /**
   * Get Parameter with given name.
   *
   * @param  name   	parameter name
   * @return     	paramater object
   */
  public Serializable getParam(String name) {
    return(m_jobParams.get(name));
  }

  /**
   * Sets the listener that gets notified when the job got finished.
   *
   * @param l		the listener
   */
  public void setJobCompleteListener(JobCompleteListener l) {
    m_completed = l;
  }

  /**
   * Returns the listener that gets notified when the job got finished.
   *
   * @return		the listener, can be null
   */
  public JobCompleteListener getJobCompleteListener() {
    return m_completed;
  }

  /**
   * Whether the job has been finished.
   *
   * @return		true if the job has finished, false otherwise
   */
  public boolean isComplete() {
    return m_complete;
  }

  /**
   * Checks whether all pre-conditions have been met.
   *
   * @return		null if everything is OK, otherwise an error message
   */
  protected abstract String preProcessCheck();

  /**
   * Does the actual execution of the job.
   * 
   * @throws Exception if fails to execute job
   */
  protected abstract void process() throws Exception;

  /**
   * Checks whether all post-conditions have been met.
   *
   * @return		null if everything is OK, otherwise an error message
   */
  protected abstract String postProcessCheck();

  /**
   * Returns additional information to be added to the error message.
   * Default returns an empty string.
   *
   * @return		the additional information
   */
  protected String getAdditionalErrorInformation() {
    return "";
  }

  /**
   * Override to do computation.
   *
   * @return		JobResult
   */
  public JobResult execute() {
    JobResult		result;
    boolean		success;
    String		addInfo;
    StringWriter	writer;

    // pre-check
    m_ExecutionError = preProcessCheck();
    success          = (m_ExecutionError == null);
    if (!success)
      m_ExecutionError = "'pre-check' failed: " + m_ExecutionError;

    // process data
    if (success) {
      try {
	process();
      }
      catch (Exception e) {
	writer  = new StringWriter();
	e.printStackTrace(new PrintWriter(writer));
	m_ExecutionError = "'process' failed with exception: " + writer.toString();
	success          = false;
      }
    }

    // post-check
    if (success) {
      m_ExecutionError = postProcessCheck();
      success          = (m_ExecutionError == null);
      if (!success)
	m_ExecutionError = "'post-check' failed: " + m_ExecutionError;
    }

    if (!success) {
      addInfo = getAdditionalErrorInformation();
      if (addInfo.length() > 0)
	m_ExecutionError += "\n" + addInfo;
    }

    // assemble result
    m_complete = true;
    result     = new JobResult(success ? toString() : m_ExecutionError, success);

    return result;
  }

  /**
   * Checks whether there was a problem with the job execution.
   *
   * @return		true if an error occurred
   */
  public boolean hasExecutionError() {
    return (m_ExecutionError != null);
  }

  /**
   * Returns the execution error, if any.
   *
   * @return		the error, null if none occurred
   */
  public String getExecutionError() {
    return m_ExecutionError;
  }

  /**
   * Cleans up data structures, frees up memory.
   * Removes dependencies and job parameters.
   */
  public void cleanUp() {
    m_depends.clear();
    m_jobParams.clear();
  }

  /**
   * Returns a string representation of this job.
   *
   * @return		the job as string
   */
  @Override
  public abstract String toString();
}
