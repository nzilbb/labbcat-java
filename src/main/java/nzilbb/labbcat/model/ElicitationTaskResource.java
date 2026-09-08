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
 * Localization resource for a participant-facing message in an elicitation task.
 * @author Robert Fromont robert@fromont.nz
 */
public class ElicitationTaskResource {
  
  // Attributes:

  /**
   * The identifier of the elicitation task the resource belongs to.
   * @see #getTaskId()
   * @see #setTaskId(int)
   */
  protected int taskId = -1;
  /**
   * Getter for {@link #taskId}: The identifier of the elicitation task the resource belongs to.
   * @return The identifier of the elicitation task the resource belongs to.
   */
  public int getTaskId() { return taskId; }
  /**
   * Setter for {@link #taskId}: The identifier of the elicitation task the resource belongs to.
   * @param newTaskId The identifier of the elicitation task the resource belongs to.
   */
  public ElicitationTaskResource setTaskId(int newTaskId) { taskId = newTaskId; return this; }

  /**
   * The name of the resource.
   * @see #getResourceId()
   * @see #setResourceId(String)
   */
  protected String resourceId;
  /**
   * Getter for {@link #resourceId}: The name of the resource.
   * @return The name of the resource.
   */
  public String getResourceId() { return resourceId; }
  /**
   * Setter for {@link #resourceId}: The name of the resource.
   * @param newResourceId The name of the resource.
   */
  public ElicitationTaskResource setResourceId(String newResourceId) { resourceId = newResourceId; return this; }

  /**
   * A description of the message to help administrators/translators understand the purpose of the resource.
   * @see #getHelp()
   * @see #setHelp(String)
   */
  protected String help;
  /**
   * Getter for {@link #help}: A description of the message to help administrators/translators understand the purpose of the resource.
   * @return A description of the message to help administrators/translators understand the purpose of the resource.
   */
  public String getHelp() { return help; }
  /**
   * Setter for {@link #help}: A description of the message to help administrators/translators understand the purpose of the resource.
   * @param newHelp A description of the message to help administrators/translators understand the purpose of the resource.
   */
  public ElicitationTaskResource setHelp(String newHelp) { help = newHelp; return this; }

  /**
   * The participant-facing message.
   * @see #getMessage()
   * @see #setMessage(String)
   */
  protected String message;
  /**
   * Getter for {@link #message}: The participant-facing message.
   * @return The participant-facing message.
   */
  public String getMessage() { return message; }
  /**
   * Setter for {@link #message}: The participant-facing message.
   * @param newMessage The participant-facing message.
   */
  public ElicitationTaskResource setMessage(String newMessage) { message = newMessage; return this; }
  
  // Methods:
  
  /**
   * Default constructor.
   */
  public ElicitationTaskResource() {
  } // end of constructor
  /**
   * Constructor from JSON.
   * @param json A JSON representation of the object to construct.
   */
  public ElicitationTaskResource(JsonObject json) {
    taskId = json.getInt("task_id");
    resourceId = json.getString("resource_id");
    help = json.getString("help");
    message = json.getString("message");
  } // end of constructor

  /**
   * Serializes the object to JSON.
   * @return A JSON serialization of the object.
   */
  public JsonObject toJson() {
    JsonObjectBuilder json = Json.createObjectBuilder();
    if (taskId >= 0) json = json.add("task_id", taskId);
    if (resourceId != null) json = json.add("resource_id", resourceId);
    if (help != null) json = json.add("help", help);
    if (message != null) json = json.add("message", message);
    return json.build();
  } // end of toJSON()
} // end of class ElicitationTaskResource
