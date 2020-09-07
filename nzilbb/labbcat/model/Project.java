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

/**
 * Project record.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class Project {
   
   // Attributes:
   
   /**
    * Database key value.
    * @see #getProjectId()
    * @see #setProjectId(int)
    */
   protected int projectId;
   /**
    * Getter for {@link #projectId}: Database key value.
    * @return Database key value.
    */
   public int getProjectId() { return projectId; }
   /**
    * Setter for {@link #projectId}: Database key value.
    * @param newProjectId Database key value.
    */
   public Project setProjectId(int newProjectId) { projectId = newProjectId; return this; }

   /**
    * The name of the project.
    * @see #getProject()
    * @see #setProject(String)
    */
   protected String project;
   /**
    * Getter for {@link #project}: The name of the project.
    * @return The name of the project.
    */
   public String getProject() { return project; }
   /**
    * Setter for {@link #project}: The name of the project.
    * @param newProject The name of the project.
    */
   public Project setProject(String newProject) { project = newProject; return this; }

   /**
    * Description of the project.
    * @see #getDescription()
    * @see #setDescription(String)
    */
   protected String description;
   /**
    * Getter for {@link #description}: Description of the project.
    * @return Description of the project.
    */
   public String getDescription() { return description; }
   /**
    * Setter for {@link #description}: Description of the project.
    * @param newDescription Description of the project.
    */
   public Project setDescription(String newDescription) { description = newDescription; return this; }
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public Project() {
   } // end of constructor
   
   /**
    * Constructor from JSON.
    */
   public Project(JsonObject json) {      
      if (json.containsKey("project_id")) {
         projectId = Integer.parseInt(json.get("project_id").toString());
      }
      project = json.getString("project");
      description = json.getString("description");
   } // end of constructor
   
   /**
    * Serializes the object to JSON.
    * @return A JSON serialization of the object.
    */
   public JsonObject toJson() {
      return Json.createObjectBuilder()
         .add("project_id", projectId)
         .add("project", project)
         .add("description", description)
         .build();
   } // end of toJSON()
   
} // end of class Project
