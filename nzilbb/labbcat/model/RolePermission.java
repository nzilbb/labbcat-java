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
 * User role permission record.
 * <p> This model hides a wart in the API; it uses <q> attribute_name </q> instead of the
 * now-more-correct <q> layerId</q>.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class RolePermission {
   
   // Attributes:
   
   /**
    * The ID of the role this permission applies to.
    * @see #getRoleId()
    * @see #setRoleId(String)
    */
   protected String roleId;
   /**
    * Getter for {@link #roleId}: The ID of the role this permission applies to.
    * @return The ID of the role this permission applies to.
    */
   public String getRoleId() { return roleId; }
   /**
    * Setter for {@link #roleId}: The ID of the role this permission applies to.
    * @param newRoleId The ID of the role this permission applies to.
    */
   public RolePermission setRoleId(String newRoleId) { roleId = newRoleId; return this; }
   
   /**
    * The media entity this permission applies to - a string made up of "t" (transcript),
    * "a" (audio), "v" (video), or "i" (image). 
    * @see #getEntity()
    * @see #setEntity(String)
    */
   protected String entity;
   /**
    * Getter for {@link #entity}: The media entity this permission applies to - a string
    * made up of "t" (transcript), "a" (audio), "v" (video), or "i" (image). 
    * @return The media entity this permission applies to - a string made up of "t"
    * (transcript), "a" (audio), "v" (video), or "i" (image). 
    */
   public String getEntity() { return entity; }
   /**
    * Setter for {@link #entity}: The media entity this permission applies to - a string
    * made up of "t" (transcript), "a" (audio), "v" (video), or "i" (image). 
    * @param newEntity The media entity this permission applies to - a string made up of
    * "t" (transcript), "a" (audio), "v" (video), or "i" (image). 
    */
   public RolePermission setEntity(String newEntity) { entity = newEntity; return this; }

   /**
    * ID of the layer for which the label determines access. This is either a valid
    * transcript attribute layer ID, or "corpus". 
    * @see #getLayerId()
    * @see #setLayerId(String)
    */
   protected String layerId;
   /**
    * Getter for {@link #layerId}: ID of the layer for which the label determines
    * access. This is either a valid transcript attribute layer ID, or "corpus". 
    * @return ID of the layer for which the label determines access. This is either a
    * valid transcript attribute layer ID, or "corpus". 
    */
   public String getLayerId() { return layerId; }
   /**
    * Setter for {@link #layerId}: ID of the layer for which the label determines
    * access. This is either a valid transcript attribute layer ID, or "corpus". 
    * @param newLayerId ID of the layer for which the label determines access. This is
    * either a valid transcript attribute layer ID, or "corpus". 
    */
   public RolePermission setLayerId(String newLayerId) { layerId = newLayerId; return this; }

   /**
    * Regular expression for matching against the <var> layerId </var> label. If the
    * regular expression matches the label, access is allowed. 
    * @see #getValuePattern()
    * @see #setValuePattern(String)
    */
   protected String valuePattern;
   /**
    * Getter for {@link #valuePattern}: Regular expression for matching against the 
    * <var> layerId </var> label. If the regular expression matches the label, access is
    * allowed. 
    * @return Regular expression for matching against the <var> layerId </var> label. If
    * the regular expression matches the label, access is allowed. 
    */
   public String getValuePattern() { return valuePattern; }
   /**
    * Setter for {@link #valuePattern}: Regular expression for matching against the 
    * <var> layerId </var> label. If the regular expression matches the label, access is
    * allowed. 
    * @param newValuePattern Regular expression for matching against the <var> layerId </var>
    * label. If the regular expression matches the label, access is allowed. 
    */
   public RolePermission setValuePattern(String newValuePattern) { valuePattern = newValuePattern; return this; }

   // Methods:
   
   /**
    * Default constructor.
    */
   public RolePermission() {
   } // end of constructor
   
   /**
    * Constructor from JSON.
    */
   public RolePermission(JsonObject json) {      
      roleId = json.getString("role_id");
      entity = json.getString("entity");
      layerId = "transcript_"+json.getString("attribute_name");
      if (layerId.equals("transcript_corpus")) layerId = "corpus";
      else if (layerId.equals("transcript_null")) layerId = null;
      valuePattern = json.getString("value_pattern");
   } // end of constructor
   
   /**
    * Serializes the object to JSON.
    * @return A JSON serialization of the object.
    */
   public JsonObject toJson() {
      return Json.createObjectBuilder()
         .add("role_id", roleId)
         .add("entity", entity)
         .add("attribute_name", layerId == null?null:layerId.replaceAll("^transcript_",""))
         .add("value_pattern", valuePattern)
         .build();
   } // end of toJSON()
   
} // end of class Role
