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
 * Model of a user record, including roles.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class User
{
   // Attributes:

   /**
    * User ID.
    * @see #getUser()
    * @see #setUser(String)
    */
   protected String user;
   /**
    * Getter for {@link #user}: User ID.
    * @return User ID.
    */
   public String getUser() { return user; }
   /**
    * Setter for {@link #user}: User ID.
    * @param newUser User ID.
    */
   public User setUser(String newUser) { user = newUser; return this; }

   /**
    * Roles or groups the user belongs to.
    * @see #getRoles()
    * @see #setRoles(String[])
    */
   protected String[] roles;
   /**
    * Getter for {@link #roles}: Roles or groups the user belongs to.
    * @return Roles or groups the user belongs to.
    */
   public String[] getRoles() { return roles; }
   /**
    * Setter for {@link #roles}: Roles or groups the user belongs to.
    * @param newRoles Roles or groups the user belongs to.
    */
   public User setRoles(String[] newRoles) { roles = newRoles; return this; }
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public User() {
   } // end of constructor

   /**
    * Constructor from JSON.
    */
   public User(JsonObject json) {      
      user = json.getString("user");
      if (json.containsKey("roles")) {
         JsonArray roleArray = json.getJsonArray("roles");
         roles = new String[roleArray.size()];
         for (int r = 0; r < roles.length; r++) {
            roles[r] = roleArray.getString(r);
         }
      }
   } // end of constructor
   
   /**
    * Serializes the object to JSON.
    * @return A JSON serialization of the object.
    */
   public JsonObject toJson() {
      JsonArrayBuilder roleArray = Json.createArrayBuilder();
      if (roles != null) {
         for (String role : roles) {
            roleArray = roleArray.add(role);
         } // next role
      }
      return Json.createObjectBuilder()
         .add("user", user)
         .add("roles", roleArray)
         .build();
   } // end of toJSON()

} // end of class User
