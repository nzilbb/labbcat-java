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
package nzilbb.labbcat.model;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;

/**
 * Project record.
 * @author Robert Fromont robert@fromont.net.nz
 * @deprecated 
 * Projects are now layer categories - a type of {@link Category} with classId = "layer" 
 */
@Deprecated(since="1.2.0", forRemoval=true)
public class Project extends Category {
   
  // Attributes:
  
  /**
   * Deprecated getter for the database key value.
   * @return Database key value.
   */
  @Deprecated(since="1.2.0", forRemoval=true)
  public int getProjectId() { if (category != null) return category.hashCode(); else return -1; }
  /**
   * Deprecated setter for the database key value, which now has no effect.
   * @param newProjectId Database key value.
   * @return A reference to this object, so that setters can be chained.
   */
  @Deprecated(since="1.2.0", forRemoval=true)
  public Project setProjectId(int newProjectId) { /* Now does nothing */ return this; }

  /**
   * Getter for the name of the project.
   * @return The name of the project.
   */
  public String getProject() { return getCategory(); }
  /**
   * Setter for the name of the project.
   * @param newProject The name of the project.
   * @return A reference to this object, so that setters can be chained.
   */
  public Project setProject(String newProject) { setCategory(newProject); return this; }
  
  // Methods:
  
  /**
   * Default constructor.
   */
  public Project() {
    classId = "layer";
  } // end of constructor
  
  /**
   * Constructor from JSON.
    * @param json A JSON representation of the object to construct.
   */
  public Project(JsonObject json) {
    super(json);
    classId = "layer";
  } // end of constructor
  
  /**
   * Constructor from Category object.
    * @param category The cateory to convert into a Project.
   */
  public Project(Category category) {
    this.classId = "layer";
    this.category = category.getCategory();
    this.description = category.getDescription();
    this.displayOrder = category.getDisplayOrder();
  } // end of constructor
  
} // end of class Project
