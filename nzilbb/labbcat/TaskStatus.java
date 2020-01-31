//
// Copyright 2020 New Zealand Institute of Language, Brain and Behaviour, 
// University of Canterbury
// Written by Robert Fromont - robert.fromont@canterbury.ac.nz
//
//    This file is part of LaBB-CAT.
//
//    LaBB-CAT is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    LaBB-CAT is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with LaBB-CAT; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
package nzilbb.labbcat;

import org.json.JSONObject;

/**
 * The status of a server-side task.
 * @author Robert Fromont robert@fromont.net.nz
 */

public class TaskStatus
{
   // Attributes:

   /**
    * ID of the task.
    * @see #getThreadId()
    * @see #setThreadId(String)
    */
   protected String threadId;
   /**
    * Getter for {@link #threadId}: ID of the task.
    * @return ID of the task.
    */
   public String getThreadId() { return threadId; }
   /**
    * Setter for {@link #threadId}: ID of the task.
    * @param newThreadId ID of the task.
    */
   public TaskStatus setThreadId(String newThreadId) { threadId = newThreadId; return this; }

   /**
    * Name of the task.
    * @see #getThreadName()
    * @see #setThreadName(String)
    */
   protected String threadName;
   /**
    * Getter for {@link #threadName}: Name of the task.
    * @return Name of the task.
    */
   public String getThreadName() { return threadName; }
   /**
    * Setter for {@link #threadName}: Name of the task.
    * @param newThreadName Name of the task.
    */
   public TaskStatus setThreadName(String newThreadName) { threadName = newThreadName; return this; }

   /**
    * Whether the task is currently running (true) or complete (false).
    * @see #getRunning()
    * @see #setRunning(boolean)
    */
   protected boolean running;
   /**
    * Getter for {@link #running}: Whether the task is currently running (true) or complete (false).
    * @return Whether the task is currently running (true) or complete (false).
    */
   public boolean getRunning() { return running; }
   /**
    * Setter for {@link #running}: Whether the task is currently running (true) or complete (false).
    * @param newRunning Whether the task is currently running (true) or complete (false).
    */
   public TaskStatus setRunning(boolean newRunning) { running = newRunning; return this; }

   /**
    * Duration in seconds.
    * @see #getDuration()
    * @see #setDuration(int)
    */
   protected int duration;
   /**
    * Getter for {@link #duration}: Duration in seconds.
    * @return Duration in seconds.
    */
   public int getDuration() { return duration; }
   /**
    * Setter for {@link #duration}: Duration in seconds.
    * @param newDuration Duration in seconds.
    */
   public TaskStatus setDuration(int newDuration) { duration = newDuration; return this; }

   /**
    * Percent complete.
    * @see #getPercentComplete()
    * @see #setPercentComplete(int)
    */
   protected int percentComplete;
   /**
    * Getter for {@link #percentComplete}: Percent complete.
    * @return Percent complete.
    */
   public int getPercentComplete() { return percentComplete; }
   /**
    * Setter for {@link #percentComplete}: Percent complete.
    * @param newPercentComplete Percent complete.
    */
   public TaskStatus setPercentComplete(int newPercentComplete) { percentComplete = newPercentComplete; return this; }

   /**
    * Last status message.
    * @see #getStatus()
    * @see #setStatus(String)
    */
   protected String status;
   /**
    * Getter for {@link #status}: Last status message.
    * @return Last status message.
    */
   public String getStatus() { return status; }
   /**
    * Setter for {@link #status}: Last status message.
    * @param newStatus Last status message.
    */
   public TaskStatus setStatus(String newStatus) { status = newStatus; return this; }

   /**
    * Suggested status refresh interval.
    * @see #getRefreshSeconds()
    * @see #setRefreshSeconds(int)
    */
   protected int refreshSeconds;
   /**
    * Getter for {@link #refreshSeconds}: Suggested status refresh interval.
    * @return Suggested status refresh interval.
    */
   public int getRefreshSeconds() { return refreshSeconds; }
   /**
    * Setter for {@link #refreshSeconds}: Suggested status refresh interval.
    * @param newRefreshSeconds Suggested status refresh interval.
    */
   public TaskStatus setRefreshSeconds(int newRefreshSeconds) { refreshSeconds = newRefreshSeconds; return this; }
   
   /**
    * URL for task results, if any.
    * @see #getResultUrl()
    * @see #setResultUrl(String)
    */
   protected String resultUrl;
   /**
    * Getter for {@link #resultUrl}: URL for task results, if any.
    * @return URL for task results, if any.
    */
   public String getResultUrl() { return resultUrl; }
   /**
    * Setter for {@link #resultUrl}: URL for task results, if any.
    * @param newResultUrl URL for task results, if any.
    */
   public TaskStatus setResultUrl(String newResultUrl) { resultUrl = newResultUrl; return this; }

   /**
    * The label for the results.
    * @see #getResultText()
    * @see #setResultText(String)
    */
   protected String resultText;
   /**
    * Getter for {@link #resultText}: The label for the results.
    * @return The label for the results.
    */
   public String getResultText() { return resultText; }
   /**
    * Setter for {@link #resultText}: The label for the results.
    * @param newResultText The label for the results.
    */
   public TaskStatus setResultText(String newResultText) { resultText = newResultText; return this; }

   // Methods:
   
   /**
    * Default constructor.
    */
   public TaskStatus()
   {
   } // end of constructor
   
   /**
    * Constructor from JSON.
    */
   public TaskStatus(JSONObject json)
   {
      threadId = json.optString("threadId");
      threadName = json.optString("threadName");
      running = json.optBoolean("running");
      duration = json.optInt("duration");
      percentComplete = json.optInt("percentComplete");
      refreshSeconds = json.optInt("refreshSeconds");
      resultUrl = json.optString("resultUrl");
      resultText = json.optString("resultText");
      status = json.optString("status");
   } // end of constructor
   
   /**
    * Represents the object as a String, for logging.
    * @return A string representation of the object.
    */
   public String toString()
   {
      return "threadId: " + threadId + " ("+threadName+") status: " + status
         + " (" + percentComplete + "% "
         + (running?"running...)":"finished.)")
         + (resultUrl==null?"":" "+resultUrl);
   } // end of toString()

} // end of class TaskStatus
