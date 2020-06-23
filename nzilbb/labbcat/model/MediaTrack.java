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

import org.json.JSONObject;

/**
 * MediaTrack record.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class MediaTrack {
   
   // Attributes:
   
   /**
    * The suffix associated with the media track.
    * @see #getSuffix()
    * @see #setSuffix(String)
    */
   protected String suffix;
   /**
    * Getter for {@link #suffix}: The suffix associated with the media track.
    * @return The suffix associated with the media track.
    */
   public String getSuffix() { return suffix; }
   /**
    * Setter for {@link #suffix}: The suffix associated with the media track.
    * @param newSuffix The suffix associated with the media track.
    */
   public MediaTrack setSuffix(String newSuffix) { suffix = newSuffix; return this; }

   /**
    * Description of the media track.
    * @see #getDescription()
    * @see #setDescription(String)
    */
   protected String description;
   /**
    * Getter for {@link #description}: Description of the media track.
    * @return Description of the media track.
    */
   public String getDescription() { return description; }
   /**
    * Setter for {@link #description}: Description of the media track.
    * @param newDescription Description of the media track.
    */
   public MediaTrack setDescription(String newDescription) { description = newDescription; return this; }
   
   /**
    * The position of the media track amongst other tracks.
    * @see #getDisplayOrder()
    * @see #setDisplayOrder(int)
    */
   protected int displayOrder;
   /**
    * Getter for {@link #displayOrder}: The position of the media track amongst other tracks.
    * @return The position of the media track amongst other tracks.
    */
   public int getDisplayOrder() { return displayOrder; }
   /**
    * Setter for {@link #displayOrder}: The position of the media track amongst other tracks.
    * @param newDisplayOrder The position of the media track amongst other tracks.
    */
   public MediaTrack setDisplayOrder(int newDisplayOrder) { displayOrder = newDisplayOrder; return this; }
   // Methods:
   
   /**
    * Default constructor.
    */
   public MediaTrack() {
   } // end of constructor
   
   /**
    * Constructor from JSON.
    */
   public MediaTrack(JSONObject json) {      
      suffix = json.optString("suffix");
      description = json.optString("description");
      if (json.has("display_order")) displayOrder = json.getInt("display_order");
   } // end of constructor
   
   /**
    * Serializes the object to JSON.
    * @return A JSON serialization of the object.
    */
   public JSONObject toJSON() {
      return new JSONObject()
         .put("suffix", suffix)
         .put("description", description)
         .put("display_order", displayOrder);
   } // end of toJSON()
   
} // end of class MediaTrack
