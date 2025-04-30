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

import java.util.LinkedHashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import nzilbb.configure.ParameterSet;

/**
 * Information about a transcript upload in progress.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class Upload {
  
  /**
   * Unique upload identifier.
   * @see #getId()
   * @see #setId(String)
   */
  protected String id;
  /**
   * Getter for {@link #id}: Unique upload identifier.
   * @return Unique upload identifier.
   */
  public String getId() { return id; }
  /**
   * Setter for {@link #id}: Unique upload identifier.
   * @param newId Unique upload identifier.
   */
  public Upload setId(String newId) { id = newId; return this; }
  
  /**
   * Parameters that are required to complete the upload.
   * @see #getParameters()
   * @see #setParameters(ParameterSet)
   */
  protected ParameterSet parameters;
  /**
   * Getter for {@link #parameters}: Parameters that are required to complete the upload.
    * @return Parameters that are required to complete the upload.
    */
  public ParameterSet getParameters() { return parameters; }
  /**
   * Setter for {@link #parameters}: Parameters that are required to complete the upload.
   * @param newParameters Parameters that are required to complete the upload.
   */
  public Upload setParameters(ParameterSet newParameters) { parameters = newParameters; return this; }
  
  /**
   * A map of transcript IDs to thread IDs, if any transcripts are already being processed.
   * @see #getTranscripts()
   * @see #setTranscripts(Map)
   */
  protected Map<String,String> transcripts;
  /**
   * Getter for {@link #transcripts}: A map of transcript IDs to thread IDs, if any
   * transcripts are already being processed.
   * @return A map of transcript IDs to thread IDs, if any transcripts are already being processed.
   */
  public Map<String,String> getTranscripts() { return transcripts; }
  /**
   * Setter for {@link #transcripts}: A map of transcript IDs to thread IDs, if any
   * transcripts are already being processed. 
   * @param newTranscripts A map of transcript IDs to thread IDs, if any transcripts are
   * already being processed. 
   */
  public Upload setTranscripts(Map<String,String> newTranscripts) { transcripts = newTranscripts; return this; }

  /**
   * Default constructor.
   */
  public Upload() {
    parameters = new ParameterSet();
  } // end of constructor
  
  /**
   * Constructor from JSON.
   */
  public Upload(JsonObject json) {
    if (json.containsKey("id")) {
      id = json.getString("id");
    }
    if (json.containsKey("parameters")) {
      parameters = new ParameterSet().fromJson(json.getJsonArray("parameters"));
    }
    if (json.containsKey("transcripts")) {
      transcripts = new LinkedHashMap<String,String>();
      JsonObject transcriptsJson = json.getJsonObject("transcripts");
      for (String transcriptId : transcriptsJson.keySet()) {
        transcripts.put(transcriptId, transcriptsJson.getString(transcriptId));
      }
    }
  } // end of constructor
} // end of class Upload
