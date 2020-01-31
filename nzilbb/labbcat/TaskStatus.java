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
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public TaskStatus()
   {
   } // end of constructor
} // end of class TaskStatus
