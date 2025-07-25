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
 * A dashboard item.
 * @author Robert Fromont robert@fromont.nz
 */
public class DashboardItem {

  // Attributes:
  
  /**
   * The ID of the item.
   * @see #getItemId()
   * @see #setItemId(String)
   */
  protected int itemId = -1;
  /**
   * Getter for {@link #itemId}: The ID of the item.
   * @return The ID of the item.
   */
  public int getItemId() { return itemId; }
  /**
   * Setter for {@link #itemId}: The ID of the item.
   * @param newItemId The ID of the item.
   */
  public DashboardItem setItemId(int newItemId) { itemId = newItemId; return this; }

  /**
   * The type of the itme: "link", "sql", or "exec".
   * @see #getType()
   * @see #setType(String)
   */
  protected String type;
  /**
   * Getter for {@link #type}: The type of the itme: "link", "sql", or "exec".
   * @return The type of the itme: "link", "sql", or "exec".
   */
  public String getType() { return type; }
  /**
   * Setter for {@link #type}: The type of the itme: "link", "sql", or "exec".
   * @param newType The type of the itme: "link", "sql", or "exec".
   */
  public DashboardItem setType(String newType) { type = newType; return this; }

  /**
   * The item's text label.
   * @see #getLabel()
   * @see #setLabel(String)
   */
  protected String label;
  /**
   * Getter for {@link #label}: The item's text label.
   * @return The item's text label.
   */
  public String getLabel() { return label; }
  /**
   * Setter for {@link #label}: The item's text label.
   * @param newLabel The item's text label.
   */
  public DashboardItem setLabel(String newLabel) { label = newLabel; return this; }

  /**
   * The item's icon.
   * @see #getIcon()
   * @see #setIcon(String)
   */
  protected String icon;
  /**
   * Getter for {@link #icon}: The item's icon.
   * @return The item's icon.
   */
  public String getIcon() { return icon; }
  /**
   * Setter for {@link #icon}: The item's icon.
   * @param newIcon The item's icon.
   */
  public DashboardItem setIcon(String newIcon) { icon = newIcon; return this; }
  
  // Methods:
  
  /**
   * Default constructor.
   */
  public DashboardItem() {
  } // end of constructor

  /**
   * Constructor from JSON.
   * @param json A JSON representation of the object to construct.
   */
  public DashboardItem(JsonObject json) {
    itemId = json.getInt("item_id");
    type = json.getString("type");
    label = json.getString("label");
    icon = json.getString("icon");
  } // end of constructor

  /**
   * Serializes the object to JSON.
   * @return A JSON serialization of the object.
   */
  public JsonObject toJson() {
    JsonObjectBuilder json = Json.createObjectBuilder();
    if (itemId >= 0) json = json.add("item_id", itemId);
    if (type != null) json = json.add("type", type);
    if (label != null) json = json.add("label", label);
    if (icon != null) json = json.add("icon", icon);
    return json.build();
  } // end of toJSON()
} // end of class DashboardItem
