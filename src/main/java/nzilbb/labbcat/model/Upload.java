//
// Copyright 2025-2026 New Zealand Institute of Language, Brain and Behaviour, 
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
 * Information about a transcript or fragment upload in progress.
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
   * @return A reference to this object, so that setters can be chained.
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
   * @return A reference to this object, so that setters can be chained.
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
   * @return A reference to this object, so that setters can be chained.
   */
  public Upload setTranscripts(Map<String,String> newTranscripts) { transcripts = newTranscripts; return this; }
  
  /**
   * The name of the full transcript (if the upload is for a fragment).
   * @see #getTranscript()
   * @see #setTranscript(String)
   */
  protected String transcript;
  /**
   * Getter for {@link #transcript}: The name of the full transcript
   * (if the upload is for a fragment). 
   * @return The name of the full transcript (if the upload is for a fragment).
   */
  public String getTranscript() { return transcript; }
  /**
   * Setter for {@link #transcript}: The name of the full transcript
   * (if the upload is for a fragment). 
   * @param newTranscript The name of the full transcript (if the upload is for a fragment).
   */
  public Upload setTranscript(String newTranscript) { transcript = newTranscript; return this; }

  /**
   * The start offset of the fragment (if the upload is for a fragment).
   * @see #getStart()
   * @see #setStart(Double)
   */
  protected Double start;
  /**
   * Getter for {@link #start}: The start offset of the fragment (if
   * the upload is for a fragment). 
   * @return The start offset of the fragment (if the upload is for a fragment).
   */
  public Double getStart() { return start; }
  /**
   * Setter for {@link #start}: The start offset of the fragment (if
   * the upload is for a fragment). 
   * @param newStart The start offset of the fragment (if the upload is for a fragment).
   */
  public Upload setStart(Double newStart) { start = newStart; return this; }

  /**
   * The end offset of the fragment (if the upload is for a fragment).
   * @see #getEnd()
   * @see #setEnd(Double)
   */
  protected Double end;
  /**
   * Getter for {@link #end}: The end offset of the fragment (if
   * the upload is for a fragment). 
   * @return The end offset of the fragment (if the upload is for a fragment).
   */
  public Double getEnd() { return end; }
  /**
   * Setter for {@link #end}: The end offset of the fragment (if
   * the upload is for a fragment). 
   * @param newEnd The end offset of the fragment (if the upload is for a fragment).
   */
  public Upload setEnd(Double newEnd) { end = newEnd; return this; }
  
  /**
   * The URL of the fragment in the context of the whole transcript
   * (if the upload is for a fragment). 
   * @see #getUrl()
   * @see #setUrl(String)
   */
  protected String url;
  /**
   * Getter for {@link #url}: The URL of the fragment in the context
   * of the whole transcript (if the upload is for a fragment). 
   * @return The URL of the fragment in the context of the whole transcript (if the upload is for a fragment).
   */
  public String getUrl() { return url; }
  /**
   * Setter for {@link #url}: The URL of the fragment in the context
   * of the whole transcript (if the upload is for a fragment). 
   * @param newUrl The URL of the fragment in the context of the whole
   * transcript (if the upload is for a fragment). 
   */
  public Upload setUrl(String newUrl) { url = newUrl; return this; }

  /**
   * Default constructor.
   */
  public Upload() {
    parameters = new ParameterSet();
  } // end of constructor
  
  /**
   * Constructor from JSON.
   * @param json A JSON representation of the object to construct.
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
    if (json.containsKey("transcript")) {
      transcript = json.getString("transcript");
    }
    if (json.containsKey("start")) {
      start = json.getJsonNumber("start").doubleValue();
    }
    if (json.containsKey("end")) {
      end = json.getJsonNumber("end").doubleValue();
    }
    if (json.containsKey("url")) {
      url = json.getString("url");
    }
  } // end of constructor
} // end of class Upload
