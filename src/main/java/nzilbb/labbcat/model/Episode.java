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
 * Episode record, for the purposes of administration.
 * @author Robert Fromont robert@fromont.net.nz
 */
public class Episode {
   
  // Attributes:
  
  /**
   * Episode name/ID.
   * @see #getEpisodeId()
   * @see #setEpisodeId(String)
   */
  protected int episodeId = -1;
  /**
   * Getter for {@link #episodeId}: Episode name/ID.
   * @return Episode name/ID.
   */
  public int getEpisodeId() { return episodeId; }
  /**
   * Setter for {@link #episodeId}: Episode name/ID.
   * @param newEpisodeId Episode name/ID.
   * @return A reference to this object, so that setters can be chained.
   */
  public Episode setEpisodeId(int newEpisodeId) { episodeId = newEpisodeId; return this; }
  
  /**
   * Name of the episode.
   * @see #getName()
   * @see #setName(String)
   */
  protected String name;
  /**
   * Getter for {@link #name}: Name of the episode.
   * @return Name of the episode.
   */
  public String getName() { return name; }
  /**
   * Setter for {@link #name}: Name of the episode.
   * @param newName Name of the episode.
   * @return A reference to this object, so that setters can be chained.
   */
  public Episode setName(String newName) { name = newName; return this; }
  
  /**
   * The corpus the episode is in.
   * @see #getCorpus()
   * @see #setCorpus(String)
   */
  protected String corpus;
  /**
   * Getter for {@link #corpus}: The corpus the episode is in.
   * @return The corpus the episode is in.
   */
  public String getCorpus() { return corpus; }
  /**
   * Setter for {@link #corpus}: The corpus the episode is in.
   * @param newCorpus The corpus the episode is in.
   */
  public Episode setCorpus(String newCorpus) { corpus = newCorpus; return this; }
  
  /**
   * Number of transcripts in the episode, if known.
   * @see #getTranscriptCount()
   * @see #setTranscriptCount(Integer)
   */
  protected Integer transcriptCount;
  /**
   * Getter for {@link #transcriptCount}: Number of transcripts in the episode, if known.
   * @return Number of transcripts in the episode, if known.
   */
  public Integer getTranscriptCount() { return transcriptCount; }
  /**
   * Setter for {@link #transcriptCount}: Number of transcripts in the episode, if known.
   * @param newTranscriptCount Number of transcripts in the episode, if known.
   */
  public Episode setTranscriptCount(Integer newTranscriptCount) { transcriptCount = newTranscriptCount; return this; }
  
  // Methods:
  
  /**
   * Default constructor.
   */
  public Episode() {
  } // end of constructor
  
  /**
   * Constructor from JSON.
   * @param json A JSON representation of the object to construct.
   */
  public Episode(JsonObject json) {      
    episodeId = json.getInt("episode_id");
    name = json.getString("name");
    corpus = json.getString("corpus");
    if (json.containsKey("transcript_count")) {
      transcriptCount = json.getInt("transcript_count");
    }
  } // end of constructor
  
  /**
   * Serializes the object to JSON.
   * @return A JSON serialization of the object.
   */
  public JsonObject toJson() {
    JsonObjectBuilder json = Json.createObjectBuilder();
    if (episodeId >= 0) json = json.add("episode_id", episodeId);
    if (name != null) json = json.add("name", name);
    if (corpus != null) json = json.add("corpus", corpus);
    if (transcriptCount != null) json = json.add("transcript_count", transcriptCount);
    return json.build();
  } // end of toJSON()
   
} // end of class Episode
