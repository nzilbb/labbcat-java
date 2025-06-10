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
 * User role record.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class Role {
   
   // Attributes:
   
   /**
    * Role name/ID.
    * @see #getRoleId()
    * @see #setRoleId(String)
    */
   protected String roleId;
   /**
    * Getter for {@link #roleId}: Role name/ID.
    * @return Role name/ID.
    */
   public String getRoleId() { return roleId; }
   /**
    * Setter for {@link #roleId}: Role name/ID.
    * @param newRoleId Role name/ID.
    * @return A reference to this object, so that setters can be chained.
    */
   public Role setRoleId(String newRoleId) { roleId = newRoleId; return this; }

   /**
    * Description of the role.
    * @see #getDescription()
    * @see #setDescription(String)
    */
   protected String description;
   /**
    * Getter for {@link #description}: Description of the role.
    * @return Description of the role.
    */
   public String getDescription() { return description; }
   /**
    * Setter for {@link #description}: Description of the role.
    * @param newDescription Description of the role.
    * @return A reference to this object, so that setters can be chained.
    */
   public Role setDescription(String newDescription) { description = newDescription; return this; }
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public Role() {
   } // end of constructor
   
   /**
    * Constructor from JSON.
    * @param json A JSON representation of the object to construct.
    */
   public Role(JsonObject json) {      
      roleId = json.getString("role_id");
      description = json.getString("description");
   } // end of constructor
   
   /**
    * Serializes the object to JSON.
    * @return A JSON serialization of the object.
    */
   public JsonObject toJson() {
      JsonObjectBuilder json = Json.createObjectBuilder();
      if (roleId != null) json = json.add("role_id", roleId);
      if (description != null) json = json.add("description", description);
      return json.build();
   } // end of toJSON()
   
} // end of class Role
