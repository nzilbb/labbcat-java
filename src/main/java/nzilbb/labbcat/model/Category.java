//
// Copyright 2023 New Zealand Institute of Language, Brain and Behaviour, 
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
import javax.json.JsonString;

/**
 * Attribute category record.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class Category {
  
  // Attributes:

  /**
   * What kind of attributes are categorised - "transcript", "speaker", or "layer".
   * @see #getClassId()
   * @see #setClassId(String)
   */
  protected String classId;
  /**
   * Getter for {@link #classId}: What kind of attributes are categorised - "transcript",
   * "speaker", or "layer". 
   * @return What kind of attributes are categorised - "transcript", "speaker", or "layer".
   */
  public String getClassId() { return classId; }
  /**
   * Setter for {@link #classId}: What kind of attributes are categorised - "transcript",
   * "speaker", or "layer". 
   * @param newClassId What kind of attributes are categorised - "transcript", "speaker", 
   * or "layer".
   */
  public Category setClassId(String newClassId) { classId = newClassId; return this; }
  
  /**
   * The name/id of the category.
   * @see #getCategory()
   * @see #setCategory(String)
   */
  protected String category;
  /**
   * Getter for {@link #category}: The name/id of the category.
   * @return The name/id of the category.
   */
  public String getCategory() { return category; }
  /**
   * Setter for {@link #category}: The name/id of the category.
   * @param newCategory The name/id of the category.
   */
  public Category setCategory(String newCategory) { category = newCategory; return this; }
  
  /**
   * The description of the category.
   * @see #getDescription()
   * @see #setDescription(String)
   */
  protected String description;
  /**
   * Getter for {@link #description}: The description of the category.
   * @return The description of the category.
   */
  public String getDescription() { return description; }
  /**
   * Setter for {@link #description}: The description of the category.
   * @param newDescription The description of the category.
   */
  public Category setDescription(String newDescription) { description = newDescription; return this; }
  
  /**
   * Where the category appears among other categories.
   * @see #getDisplayOrder()
   * @see #setDisplayOrder(int)
   */
  protected int displayOrder;
  /**
   * Getter for {@link #displayOrder}: Where the category appears among other categories.
   * @return Where the category appears among other categories.
   */
  public int getDisplayOrder() { return displayOrder; }
  /**
   * Setter for {@link #displayOrder}: Where the category appears among other categories.
   * @param newDisplayOrder Where the category appears among other categories.
   */
  public Category setDisplayOrder(int newDisplayOrder) { displayOrder = newDisplayOrder; return this; }
  
  // Methods:
  
  /**
   * Default constructor.
   */
  public Category() {
  } // end of constructor
  
  /**
   * Constructor from JSON.
   */
  public Category(JsonObject json) {
    classId = json.getString("class_id");
    category = json.getString("category");
    if (json.containsKey("description") && !json.isNull("description")) {
      description = json.getString("description");
    }
    if (json.containsKey("display_order") && !json.isNull("display_order")) {
      displayOrder = json.getInt("display_order", -1);
    }
  } // end of constructor
  
  /**
   * Serializes the object to JSON.
   * @return A JSON serialization of the object.
   */
  public JsonObject toJson() {
    JsonObjectBuilder json = Json.createObjectBuilder();
    if (classId != null) json = json.add("class_id", classId);
    if (category != null) json = json.add("category", category);
    if (description != null) json = json.add("description", description);
    if (displayOrder >=0 ) json = json.add("display_order", displayOrder);
    return json.build();
  } // end of toJSON()
  
} // end of class Category
