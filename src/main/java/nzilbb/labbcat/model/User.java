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
    * @return A reference to this object, so that setters can be chained.
    */
   public User setUser(String newUser) { user = newUser; return this; }

   /**
    * Email address.
    * @see #getEmail()
    * @see #setEmail(String)
    */
   protected String email;
   /**
    * Getter for {@link #email}: Email address.
    * @return Email address.
    */
   public String getEmail() { return email; }
   /**
    * Setter for {@link #email}: Email address.
    * @param newEmail Email address.
    * @return A reference to this object, so that setters can be chained.
    */
   public User setEmail(String newEmail) { email = newEmail; return this; }

   /**
    * Whether the user must reset their password when they next log in.
    * @see #getResetPassword()
    * @see #setResetPassword(Boolean)
    */
   protected Boolean resetPassword;
   /**
    * Getter for {@link #resetPassword}: Whether the user must reset their password when
    * they next log in. 
    * @return Whether the user must reset their password when they next log in.
    */
   public Boolean getResetPassword() { return resetPassword; }
   /**
    * Setter for {@link #resetPassword}: Whether the user must reset their password when they 
    * next log in.
    * @param newResetPassword Whether the user must reset their password when they next log in.
    * @return A reference to this object, so that setters can be chained.
    */
   public User setResetPassword(Boolean newResetPassword) { resetPassword = newResetPassword; return this; }
   
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
    * @return A reference to this object, so that setters can be chained.
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
    * @param json A JSON representation of the object to construct.
    */
   public User(JsonObject json) {      
      user = json.getString("user");
      if (json.containsKey("email") && !json.isNull("email")) {
         email = json.getString("email");
      }
      if (json.containsKey("resetPassword") && !json.isNull("resetPassword")) {
         resetPassword = json.getInt("resetPassword", 0) != 0;
      }
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
      JsonObjectBuilder json = Json.createObjectBuilder();
      if (user != null) json = json.add("user", user);
      if (email != null) json = json.add("email", email);
      if (resetPassword != null) json = json.add("resetPassword", resetPassword?1:0);
      json = json.add("roles", roleArray);
      return json.build();
   } // end of toJSON()

} // end of class User
