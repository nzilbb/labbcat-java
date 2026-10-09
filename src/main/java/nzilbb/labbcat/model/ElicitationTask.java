//
// Copyright 2025 New Zealand Institute of Language, Brain and Behaviour, 
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
package nzilbb.labbcat.model;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * An elicitation task.
 * @author Robert Fromont robert@fromont.nz
 */
public class ElicitationTask {
  
  // Attributes:
  
  /**
   * ID of the task.
   * @see #getTaskId()
   * @see #setTaskId(String)
   */
  protected int taskId = -1;
  /**
   * Getter for {@link #taskId}: ID of the task.
   * @return ID of the task.
   */
  public int getTaskId() { return taskId; }
  /**
   * Setter for {@link #taskId}: ID of the task.
   * @param newTaskId ID of the task.
   */
  public ElicitationTask setTaskId(int newTaskId) { taskId = newTaskId; return this; }

  /**
   * Name of the task
   * @see #getTaskName()
   * @see #setTaskName(String)
   */
  protected String taskName;
  /**
   * Getter for {@link #taskName}: Name of the task
   * @return Name of the task
   */
  public String getTaskName() { return taskName; }
  /**
   * Setter for {@link #taskName}: Name of the task
   * @param newTaskName Name of the task
   */
  public ElicitationTask setTaskName(String newTaskName) { taskName = newTaskName; return this; }

  /**
   * Description of the task.
   * @see #getDescription()
   * @see #setDescription(String)
   */
  protected String description;
  /**
   * Getter for {@link #description}: Description of the task.
   * @return Description of the task.
   */
  public String getDescription() { return description; }
  /**
   * Setter for {@link #description}: Description of the task.
   * @param newDescription Description of the task.
   */
  public ElicitationTask setDescription(String newDescription) { description = newDescription; return this; }
  
  /**
   * The corpus into which task transcripts/recordings will be uploaded.
   * @see #getCorpusName()
   * @see #setCorpusName(String)
    */
  protected String corpusName;
  /**
   * Getter for {@link #corpusName}: The corpus into which task transcripts/recordings will be uploaded.
   * @return The corpus into which task transcripts/recordings will be uploaded.
   */
  public String getCorpusName() { return corpusName; }
  /**
   * Setter for {@link #corpusName}: The corpus into which task transcripts/recordings will be uploaded.
   * @param newCorpusName The corpus into which task transcripts/recordings will be uploaded.
   */
  public ElicitationTask setCorpusName(String newCorpusName) { corpusName = newCorpusName; return this; }
  
  /**
   * The transcript type for elicited transcripts.
   * @see #getTranscriptType()
   * @see #setTranscriptType(String)
   */
  protected String transcriptType;
  /**
   * Getter for {@link #transcriptType}: The transcript type for elicited transcripts.
   * @return The transcript type for elicited transcripts.
   */
  public String getTranscriptType() { return transcriptType; }
  /**
   * Setter for {@link #transcriptType}: The transcript type for elicited transcripts.
   * @param newTranscriptType The transcript type for elicited transcripts.
   */
  public ElicitationTask setTranscriptType(String newTranscriptType) { transcriptType = newTranscriptType; return this; }

  /**
   * The HTML-encoded text displayed to the participant before starting the task.
   * @see #getPreamble()
   * @see #setPreamble(String)
   */
  protected String preamble;
  /**
   * Getter for {@link #preamble}: The HTML-encoded text displayed to the participant before starting the task.
   * @return The HTML-encoded text displayed to the participant before starting the task.
   */
  public String getPreamble() { return preamble; }
  /**
   * Setter for {@link #preamble}: The HTML-encoded text displayed to the participant before starting the task.
   * @param newPreamble The HTML-encoded text displayed to the participant before starting the task.
   */
  public ElicitationTask setPreamble(String newPreamble) { preamble = newPreamble; return this; }

  /**
   * Optionally, the HTML-encoded consent form the participant 'signs' before starting the task.
   * @see #getConsent()
   * @see #setConsent(String)
   */
  protected String consent;
  /**
   * Getter for {@link #consent}: Optionally, the HTML-encoded consent form the participant 'signs' before starting the task.
   * @return Optionally, the HTML-encoded consent form the participant 'signs' before starting the task.
   */
  public String getConsent() { return consent; }
  /**
   * Setter for {@link #consent}: Optionally, the HTML-encoded consent form the participant 'signs' before starting the task.
   * @param newConsent Optionally, the HTML-encoded consent form the participant 'signs' before starting the task.
   */
  public ElicitationTask setConsent(String newConsent) { consent = newConsent; return this; }

  /**
   * Optionally, the URL for redirecting the participant to after finishing the task.
   * @see #getEndUrl()
   * @see #setEndUrl(String)
   */
  protected String endUrl;
  /**
   * Getter for {@link #endUrl}: Optionally, the URL for redirecting the participant to after finishing the task.
   * @return Optionally, the URL for redirecting the participant to after finishing the task.
   */
  public String getEndUrl() { return endUrl; }
  /**
   * Setter for {@link #endUrl}: Optionally, the URL for redirecting the participant to after finishing the task.
   * @param newEndUrl Optionally, the URL for redirecting the participant to after finishing the task.
   */
  public ElicitationTask setEndUrl(String newEndUrl) { endUrl = newEndUrl; return this; }

  
  // Methods:
  
  /**
   * Default constructor.
   */
  public ElicitationTask() {
  } // end of constructor
  
  /**
   * Constructor from JSON.
   * @param json A JSON representation of the object to construct.
   */
  public ElicitationTask(JsonObject json) {
    taskId = json.getInt("task_id");
    taskName = json.getString("task_name");
    description = json.getString("description");
    corpusName = json.getString("corpus_name");
    transcriptType = json.getString("transcript_type");
    if (json.containsKey("preamble") && !json.isNull("preamble"))
      preamble = json.getString("preamble");
    if (json.containsKey("consent") && !json.isNull("consent"))
      consent = json.getString("consent");
    if (json.containsKey("endUrl") && !json.isNull("endUrl"))
      endUrl = json.getString("endUrl");
  } // end of constructor

  /**
   * Serializes the object to JSON.
   * @return A JSON serialization of the object.
   */
  public JsonObject toJson() {
    JsonObjectBuilder json = Json.createObjectBuilder();
    if (taskId >= 0) json = json.add("task_id", taskId);
    if (taskName != null) json = json.add("task_name", taskName);
    if (description != null) json = json.add("description", description);
    if (corpusName != null) json = json.add("corpus_name", corpusName);
    if (transcriptType != null) json = json.add("transcript_type", transcriptType);
    if (preamble != null) json = json.add("preamble", preamble);
    if (consent != null) json = json.add("consent", consent);
    if (endUrl != null) json = json.add("endUrl", endUrl);
    return json.build();
  } // end of toJSON()
} // end of class ElicitationTask
