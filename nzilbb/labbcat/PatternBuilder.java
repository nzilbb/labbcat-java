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

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Helper class for building layered search patterns for
 * {@link LabbcatView#search(JSONObject,String[],String[],boolean,boolean,Integer)}.
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
   
   // Attributes:
   
   /**
    * The pattern object.
    */
   protected JSONArray columns = new JSONArray();
   
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
      
      JSONObject lastColumn = null;
      if (columns.length() > 0) {
         lastColumn = columns.getJSONObject(columns.length() - 1);
      }
      if (lastColumn != null) {
         if (lastColumn.getJSONObject("layers").length() == 0) {
            // they haven't added any layers to the last column yet
            // so don't add any more columns
            return this;
         }
         lastColumn.put("adj", adj);
      }
      columns.put(new JSONObject()
                  .put("layers", new JSONObject()));
      return this;
   } // end of addColumn()
   
   /**
    * Adds a layer for matching a regular expression.
    * @param layerId The ID of the layer.
    * @param regularExpression The regular expression to match.
    * @return A reference to this object.
    */
   public PatternBuilder addMatchLayer(String layerId, String regularExpression) {
      
      lastLayers().put(layerId, new JSONObject()
                     .put("pattern", regularExpression));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Adds a layer for <em>not</em> matching a regular expression.
    * @param layerId The ID of the layer.
    * @param regularExpression The regular expression to match.
    * @return A reference to this object.
    */
   public PatternBuilder addNotMatchLayer(String layerId, String regularExpression) {
      
      lastLayers().put(layerId, new JSONObject()
                     .put("not", Boolean.TRUE)
                     .put("pattern", regularExpression));
      return this;
   } // end of addMatchLayer()

   /**
    * Adds a layer for matching a minimum value.
    * @param layerId The ID of the layer.
    * @param min The minimum value.
    * @return A reference to this object.
    */
   public PatternBuilder addMinLayer(String layerId, double min) {
      
      lastLayers().put(layerId, new JSONObject()
                     .put("min", ""+min));
      return this;
   } // end of addMinLayer()
   
   /**
    * Adds a layer for matching a minimum value.
    * @param layerId The ID of the layer.
    * @param min The minimum value.
    * @return A reference to this object.
    */
   public PatternBuilder addMinLayer(String layerId, int min) {
      
      lastLayers().put(layerId, new JSONObject()
                     .put("min", ""+min));
      return this;
   } // end of addMinLayer()
   
   /**
    * Adds a layer for matching a maximum value.
    * @param layerId The ID of the layer.
    * @param max The maximum value.
    * @return A reference to this object.
    */
   public PatternBuilder addMaxLayer(String layerId, double max) {
      
      lastLayers().put(layerId, new JSONObject()
                     .put("max", ""+max));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Adds a layer for matching a maximum value.
    * @param layerId The ID of the layer.
    * @param max The maximum value.
    * @return A reference to this object.
    */
   public PatternBuilder addMaxLayer(String layerId, int max) {
      
      lastLayers().put(layerId, new JSONObject()
                     .put("max", ""+max));
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
      
      lastLayers().put(layerId, new JSONObject()
                     .put("min", ""+min)
                     .put("max", ""+max));
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
      
      lastLayers().put(layerId, new JSONObject()
                     .put("min", ""+min)
                     .put("max", ""+max));
      return this;
   } // end of addMatchLayer()
   
   /**
    * Construct a valid pattern object for passing to
    * {@link LabbcatView#search(JSONObject,String[],String[],boolean,boolean,Integer)}.
    * @return A valid pattern object.
    */
   public JSONObject build() {      
      return new JSONObject().put("columns", columns);
   } // end of build()
   
   /**
    * Returns the last column, adding one if there aren't any.
    * @return The last column.
    */
   protected JSONObject lastColumn() {
      
      if (columns.length() == 0) addColumn();
      return columns.getJSONObject(columns.length() - 1);
   } // end of lastColumn()

   /**
    * Returns the "layers" collection of the last column, adding a column if there aren't any.
    * @return The "layers" collection of the last column.
    */
   protected JSONObject lastLayers() {
      return lastColumn().getJSONObject("layers");
   } // end of lastLayers()
   
   /**
    * A String representation of the JSON pattern object.
    * @return A String representation of the JSON pattern object.
    */
   public String toString() {
      return build().toString();
   } // end of toString()

} // end of class PatternBuilder
