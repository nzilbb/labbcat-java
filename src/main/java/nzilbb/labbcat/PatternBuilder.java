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
package nzilbb.labbcat;

import java.util.LinkedHashMap;
import java.util.Vector;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Helper class for building layered search patterns for
 * {@link LabbcatView#search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}.
 * <p>e.g.
 * <pre>// words starting with 'ps...'
 *  JSONObject pattern = new PatternBuilder().addMatchLayer("orthography", "ps.*").build();
 * 
 * // the word 'the' followed immediately or with one intervening word by
 * // a hapax legomenon (word with a frequency of 1) that doesn't start with a vowel
 * JSONObject pattern2 = new PatternBuilder()
 *    .addColumn()
 *    .addMatchLayer("orthography", "the")
 *    .addColumn()
 *    .addNotMatchLayer("phonemes", "[cCEFHiIPqQuUV0123456789~#\\$@].*")
 *    .addMaxLayer("frequency", 2)
 *    .build();
 * @author Robert Fromont robert@fromont.net.nz
 */

public class PatternBuilder {

   class Column {
      LinkedHashMap<String,JsonObjectBuilder> layers = new LinkedHashMap<String,JsonObjectBuilder>();
      int adj = 1;
   }
   // Attributes:
   
   /**
    * The pattern object.
    */
   protected Vector<Column> columns = new Vector<Column>();
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public PatternBuilder() {
   } // end of constructor
   
   /**
    * Adds a column to the search matrix.
    * @return A reference to this builder.
    */
   public PatternBuilder addColumn() {
      return addColumn(1);
   } // end of addColumn()
   
   /**
    * Adds a column to the search matrix.
    * @param adj Maximum distance, in tokens, from previous column.
    * @return A reference to this builder.
    */
   public PatternBuilder addColumn(int adj) {
      
      Column lastColumn = null;
      if (columns.size() > 0) {
         lastColumn = columns.lastElement();
      }
      if (lastColumn != null) {
         if (lastColumn.layers.size() == 0) {
            // they haven't added any layers to the last column yet
            // so don't add any more columns
            lastColumn.adj = adj;
            return this;
         }
      }
      lastColumn = new Column();
      lastColumn.adj = adj;
      columns.add(lastColumn);
      return this;
   } // end of addColumn()
   
   /**
    * Adds a layer for matching a regular expression.
    * @param layerId The ID of the layer.
    * @param regularExpression The regular expression to match.
    * @return A reference to this object.
    */
   public PatternBuilder addMatchLayer(String layerId, String regularExpression) {
      
      lastLayers().put(layerId, Json.createObjectBuilder()
                       .add("pattern", regularExpression));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Adds a layer for <em>not</em> matching a regular expression.
    * @param layerId The ID of the layer.
    * @param regularExpression The regular expression to match.
    * @return A reference to this object.
    */
   public PatternBuilder addNotMatchLayer(String layerId, String regularExpression) {
      
      lastLayers().put(layerId, Json.createObjectBuilder()
                       .add("not", Boolean.TRUE)
                       .add("pattern", regularExpression));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Adds a layer for matching a minimum value.
    * @param layerId The ID of the layer.
    * @param min The minimum value.
    * @return A reference to this object.
    */
   public PatternBuilder addMinLayer(String layerId, double min) {
      
      lastLayers().put(layerId, Json.createObjectBuilder()
                       .add("min", ""+min));
      return this;
   } // end of addMinLayer()
   
   /**
    * Adds a layer for matching a minimum value.
    * @param layerId The ID of the layer.
    * @param min The minimum value.
    * @return A reference to this object.
    */
   public PatternBuilder addMinLayer(String layerId, int min) {
      
      lastLayers().put(layerId, Json.createObjectBuilder()
                       .add("min", ""+min));
      return this;
   } // end of addMinLayer()
   
   /**
    * Adds a layer for matching a maximum value.
    * @param layerId The ID of the layer.
    * @param max The maximum value.
    * @return A reference to this object.
    */
   public PatternBuilder addMaxLayer(String layerId, double max) {
      
      lastLayers().put(layerId, Json.createObjectBuilder()
                       .add("max", ""+max));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Adds a layer for matching a maximum value.
    * @param layerId The ID of the layer.
    * @param max The maximum value.
    * @return A reference to this object.
    */
   public PatternBuilder addMaxLayer(String layerId, int max) {
      
      lastLayers().put(layerId, Json.createObjectBuilder()
                       .add("max", ""+max));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Adds a layer for matching a minimum to maximum range.
    * @param layerId The ID of the layer.
    * @param min The minimum value.
    * @param max The maximum value.
    * @return A reference to this object.
    */
   public PatternBuilder addRangeLayer(String layerId, double min, double max) {
      
      lastLayers().put(layerId, Json.createObjectBuilder()
                       .add("min", ""+min)
                       .add("max", ""+max));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Adds a layer for matching a minimum to maximum range.
    * @param layerId The ID of the layer.
    * @param min The minimum value.
    * @param max The maximum value.
    * @return A reference to this object.
    */
   public PatternBuilder addRangeLayer(String layerId, int min, int max) {
      
      lastLayers().put(layerId, Json.createObjectBuilder()
                       .add("min", ""+min)
                       .add("max", ""+max));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Construct a valid pattern object for passing to
    * {@link LabbcatView#search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}.
    * @return A valid pattern object.
    */
   public JsonObject build() {
      JsonArrayBuilder jsonColumns = Json.createArrayBuilder();
      for (Column column : columns) {
         JsonObjectBuilder jsonLayers = Json.createObjectBuilder();
         for (String id : column.layers.keySet()) {
            jsonLayers = jsonLayers.add(id, column.layers.get(id));
         } // next layer
         
         JsonObjectBuilder jsonColumn = Json.createObjectBuilder()
            .add("layers", jsonLayers);
         if (column != columns.lastElement()) {
            jsonColumn = jsonColumn.add("adj", column.adj);
         }
         jsonColumns = jsonColumns.add(jsonColumn);
      } // next column
      
      return Json.createObjectBuilder().add("columns", jsonColumns).build();
   } // end of build()
   
   /**
    * Returns the last column, adding one if there aren't any.
    * @return The last column.
    */
   protected Column lastColumn() {
      
      if (columns.size() == 0) addColumn();
      return columns.lastElement();
   } // end of lastColumn()

   /**
    * Returns the "layers" collection of the last column, adding a column if there aren't any.
    * @return The "layers" collection of the last column.
    */
   protected LinkedHashMap<String,JsonObjectBuilder> lastLayers() {
      return lastColumn().layers;
   } // end of lastLayers()
   
   /**
    * A String representation of the JSON pattern object.
    * @return A String representation of the JSON pattern object.
    */
   public String toString() {
      return build().toString();
   } // end of toString()

} // end of class PatternBuilder
