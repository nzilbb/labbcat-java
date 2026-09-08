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
 * A participant-facing option for the value of an attribute elicited during an elicitation task.
 * @author Robert Fromont robert@fromont.nz
 */
public class ElicitationTaskAttributeOption {
  
  // Attributes:

  /**
   * The identifier of the elicitation task the option belongs to.
   * @see #getTaskId()
   * @see #setTaskId(int)
   */
  protected int taskId = -1;
  /**
   * Getter for {@link #taskId}: The identifier of the elicitation task the option belongs to.
   * @return The identifier of the elicitation task the option belongs to.
   */
  public int getTaskId() { return taskId; }
  /**
   * Setter for {@link #taskId}: The identifier of the elicitation task the option belongs to.
   * @param newTaskId The identifier of the elicitation task the option belongs to.
   */
  public ElicitationTaskAttributeOption setTaskId(int newTaskId) { taskId = newTaskId; return this; }

  /**
   * The name of the attribute the option is for.
   * @see #getAttribute()
   * @see #setAttribute(String)
   */
  protected String attribute;
  /**
   * Getter for {@link #attribute}: The name of the attribute the option is for.
   * @return The name of the attribute the option is for.
   */
  public String getAttribute() { return attribute; }
  /**
   * Setter for {@link #attribute}: The name of the attribute the option is for.
   * @param newAttribute The name of the attribute the option is for.
   */
  public ElicitationTaskAttributeOption setAttribute(String newAttribute) { attribute = newAttribute; return this; }

  /**
   * The attribute value that the option selects.
   * @see #getValue()
   * @see #setValue(String)
   */
  protected String value;
  /**
   * Getter for {@link #value}: The attribute value that the option selects.
   * @return The attribute value that the option selects.
   */
  public String getValue() { return value; }
  /**
   * Setter for {@link #value}: The attribute value that the option selects.
   * @param newValue The attribute value that the option selects.
   */
  public ElicitationTaskAttributeOption setValue(String newValue) { value = newValue; return this; }

  /**
   * The participant-facing label of the option.
   * @see #getDescription()
   * @see #setDescription(String)
   */
  protected String description;
  /**
   * Getter for {@link #description}: The participant-facing label of the option.
   * @return The participant-facing label of the option.
   */
  public String getDescription() { return description; }
  /**
   * Setter for {@link #description}: The participant-facing label of the option.
   * @param newDescription The participant-facing label of the option.
   */
  public ElicitationTaskAttributeOption setDescription(String newDescription) { description = newDescription; return this; }
   
  // Methods:
   
  /**
   * Default constructor.
   */
  public ElicitationTaskAttributeOption() {
  } // end of constructor
  
  /**
   * Constructor from JSON.
   * @param json A JSON representation of the object to construct.
   */
  public ElicitationTaskAttributeOption(JsonObject json) {
    taskId = json.getInt("task_id");
    attribute = json.getString("attribute");
    value = json.getString("value");
    description = json.getString("description");
  } // end of constructor

  /**
   * Serializes the object to JSON.
   * @return A JSON serialization of the object.
   */
  public JsonObject toJson() {
    JsonObjectBuilder json = Json.createObjectBuilder();
    if (taskId >= 0) json = json.add("task_id", taskId);
    if (attribute != null) json = json.add("attribute", attribute);
    if (value != null) json = json.add("value", value);
    if (description != null) json = json.add("description", description);
    return json.build();
  } // end of toJSON()
} // end of class ElicitationTaskAttributeOption
