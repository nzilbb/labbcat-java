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

  /**
   * The definition of the item, if available.
   * @see #getDefinition()
   * @see #setDefinition(String)
   */
  protected String definition;
  /**
   * Getter for {@link #definition}: The definition of the item, if available.
   * @return The definition of the item, if available.
   */
  public String getDefinition() { return definition; }
  /**
   * Setter for {@link #definition}: The definition of the item, if available.
   * @param newDefinition The definition of the item, if available.
   */
  public DashboardItem setDefinition(String newDefinition) { definition = newDefinition; return this; }
  
  /**
   * Wether the item is displayed on the main dashboard.
   * @see #getDashboard()
   * @see #setDashboard(Boolean)
   */
  protected Boolean dashboard;
  /**
   * Getter for {@link #dashboard}: Wether the item is displayed on the main dashboard.
   * @return Wether the item is displayed on the main dashboard.
   */
  public Boolean getDashboard() { return dashboard; }
  /**
   * Setter for {@link #dashboard}: Wether the item is displayed on the main dashboard.
   * @param newDashboard Wether the item is displayed on the main dashboard.
   */
  public DashboardItem setDashboard(Boolean newDashboard) { dashboard = newDashboard; return this; }

  /**
   * Wether the item is displayed on the 'express' dashboard.
   * @see #getExpress()
   * @see #setExpress(Boolean)
   */
  protected Boolean express;
  /**
   * Getter for {@link #express}: Wether the item is displayed on the 'express' dashboard.
   * @return Wether the item is displayed on the 'express' dashboard.
   */
  public Boolean getExpress() { return express; }
  /**
   * Setter for {@link #express}: Wether the item is displayed on the 'express' dashboard.
   * @param newExpress Wether the item is displayed on the 'express' dashboard.
   */
  public DashboardItem setExpress(Boolean newExpress) { express = newExpress; return this; }

  /**
   * Wether the item is displayed on the 'statistics' dashboard.
   * @see #getStatistics()
   * @see #setStatistics(Boolean)
   */
  protected Boolean statistics;
  /**
   * Getter for {@link #statistics}: Wether the item is displayed on the 'statistics' dashboard.
   * @return Wether the item is displayed on the 'statistics' dashboard.
   */
  public Boolean getStatistics() { return statistics; }
  /**
   * Setter for {@link #statistics}: Wether the item is displayed on the 'statistics' dashboard.
   * @param newStatistics Wether the item is displayed on the 'statistics' dashboard.
   */
  public DashboardItem setStatistics(Boolean newStatistics) { statistics = newStatistics; return this; }

  /**
   * Where the item fits in the order amongst other items of the same type.
   * @see #getDisplayOrder()
   * @see #setDisplayOrder(int)
   */
  protected int displayOrder = -1;
  /**
   * Getter for {@link #displayOrder}: Where the item fits in the order amongst other items of the same type.
   * @return Where the item fits in the order amongst other items of the same type.
   */
  public int getDisplayOrder() { return displayOrder; }
  /**
   * Setter for {@link #displayOrder}: Where the item fits in the order amongst other items of the same type.
   * @param newDisplayOrder Where the item fits in the order amongst other items of the same type.
   */
  public DashboardItem setDisplayOrder(int newDisplayOrder) { displayOrder = newDisplayOrder; return this; }

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
    if (json.containsKey("definition")) definition = json.getString("definition");
    if (json.containsKey("display_order")) displayOrder = json.getInt("display_order");
    if (json.containsKey("dashboard")) dashboard = json.getInt("dashboard") == 1;
    if (json.containsKey("express")) express = json.getInt("express") == 1;
    if (json.containsKey("statistics")) statistics = json.getInt("statistics") == 1;
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
    if (definition != null) json = json.add("definition", definition);
    if (displayOrder >= 0) json = json.add("display_order", displayOrder);
    if (dashboard != null) json = json.add("dashboard", dashboard?1:0);
    if (express != null) json = json.add("express", express?1:0);
    if (statistics != null) json = json.add("statistics", statistics?1:0);
    return json.build();
  } // end of toJSON()
} // end of class DashboardItem
