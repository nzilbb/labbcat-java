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

import java.util.Map;
import java.util.LinkedHashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * system_attribute record.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class SystemAttribute {
   
   // Attributes:

   /** ID of the attribute.
    * @see #getAttribute()
    * @see #setAttribute(String) */
   protected String attribute;
   /** Getter for {@link #attribute}: ID of the attribute.
    * @return ID of the attribute. */
   public String getAttribute() { return attribute; }
   /** Setter for {@link #attribute}: ID of the attribute.
    * @param newAttribute ID of the attribute. */
   public SystemAttribute setAttribute(String newAttribute) { attribute = newAttribute; return this; }
   
   /** The type of the attribute - "string", "integer", "boolean", "select", etc.
    * @see #getType()
    * @see #setType(String) */
   protected String type;
   /** Getter for {@link #type}: The type of the attribute - "string", "integer",
    * "boolean", "select", etc.  
    * @return The type of the attribute - "string", "integer", "boolean", "select", etc. */
   public String getType() { return type; }
   /** Setter for {@link #type}: The type of the attribute - "string", "integer",
    * "boolean", "select", etc. 
    * @param newType The type of the attribute - "string", "integer", "boolean", "select", etc. */
   public SystemAttribute setType(String newType) { type = newType; return this; }

   /** Style definition which depends on {@link #type} - e.g. whether the "boolean" is shown as a
    * checkbox or radio buttons, etc. 
    * @see #getStyle()
    * @see #setStyle(String) */
   protected String style;
   /** Getter for {@link #style}: Style definition which depends on {@link #type} -
    * e.g. whether the "boolean" is shown as a checkbox or radio buttons, etc. 
    * @return Style definition which depends on {@link #type} - e.g. whether the "boolean"
    * is shown as a checkbox or radio buttons, etc. */
   public String getStyle() { return style; }
   /** Setter for {@link #style}: Style definition which depends on {@link #type} - e.g. whether
    * the "boolean" is shown as a checkbox or radio buttons, etc. 
    * @param newStyle Style definition which depends on {@link #type} - e.g. whether the
    * "boolean" is shown as a checkbox or radio buttons, etc. */
   public SystemAttribute setStyle(String newStyle) { style = newStyle; return this; }

   /** User-facing label for the attribute.
    * @see #getLabel()
    * @see #setLabel(String) */
   protected String label;
   /** Getter for {@link #label}: User-facing label for the attribute.
    * @return User-facing label for the attribute. */
   public String getLabel() { return label; }
   /** Setter for {@link #label}: User-facing label for the attribute.
    * @param newLabel User-facing label for the attribute. */
   public SystemAttribute setLabel(String newLabel) { label = newLabel; return this; }

   /** User-facing (long) description of the attribute.
    * @see #getDescription()
    * @see #setDescription(String) */
   protected String description;
   /** Getter for {@link #description}: User-facing (long) description of the attribute.
    * @return User-facing (long) description of the attribute. */
   public String getDescription() { return description; }
   /** Setter for {@link #description}: User-facing (long) description of the attribute.
    * @param newDescription User-facing (long) description of the attribute. */
   public SystemAttribute setDescription(String newDescription) { description = newDescription; return this; }

   /** If {@link #type} is "select", these are the valid options for the attribute, where the map
    * key is the attribute value and the map value is the user-facing label for the
    * option. 
    * @see #getOptions()
    * @see #setOptions(Map) */
   protected Map<String,String> options;
   /** Getter for {@link #options}: If {@link #type} is "select", these are the valid
    * options for the attribute, where the map key is the attribute value and the map
    * value is the user-facing label for the option. 
    * @return If {@link #type} is "select", these are the valid options for the attribute,
    * where the map key is the attribute value and the map value is the user-facing label
    * for the option. */
   public Map<String,String> getOptions() { return options; }
   /** Setter for {@link #options}: If {@link #type} is "select", these are the valid
    * options for the attribute, where the map key is the attribute value and the map
    * value is the user-facing label for the option. 
    * @param newOptions If {@link #type} is "select", these are the valid options for the
    * attribute, where the map key is the attribute value and the map value is the
    * user-facing label for the option. */
   public SystemAttribute setOptions(Map<String,String> newOptions) { options = newOptions; return this; }

   /** The value of the attribute.
    * @see #getValue()
    * @see #setValue(String) */
   protected String value;
   /** Getter for {@link #value}: The value of the attribute.
    * @return The value of the attribute. */
   public String getValue() { return value; }
   /** Setter for {@link #value}: The value of the attribute.
    * @param newValue The value of the attribute. */
   public SystemAttribute setValue(String newValue) { value = newValue; return this; }
   
   // Methods:
   
   /** Default constructor. */
   public SystemAttribute() {      
   } // end of constructor
   
   /** Constructor from JSON. */
   public SystemAttribute(JsonObject json) {      
      attribute = json.getString("attribute");
      if (json.containsKey("type")) type = json.getString("type");
      if (json.containsKey("style")) style = json.getString("style");
      if (json.containsKey("label")) label = json.getString("label");
      if (json.containsKey("description")) description = json.getString("description");
      if (json.containsKey("value")) value = json.getString("value");
      if (json.containsKey("options")) {
         options = new LinkedHashMap<String,String>();
         for (String value : json.getJsonObject("options").keySet()) {
            options.put(value, json.getJsonObject("options").getString(value));
         } // next option
      }
   } // end of constructor
   
   /** Serializes the object to JSON.
    * @return A JSON serialization of the object. */
   public JsonObject toJson() {
      JsonObjectBuilder o = Json.createObjectBuilder()
         .add("attribute", attribute);
      if (type != null) o = o.add("type", type);
      if (style != null) o = o.add("style", style);
      if (label != null) o = o.add("label", label);
      if (description != null) o = o.add("description", description);
      if (options != null) {
         JsonObjectBuilder optionsObject = Json.createObjectBuilder();
         for (String value : options.keySet()) {
            optionsObject = optionsObject.add(value, options.get(value));
         } // next option
         o = o.add("options", optionsObject);
      }
      if (value != null) o = o.add("value", value);
      return o.build();
   } // end of toJSON()

} // end of class SystemAttribute
