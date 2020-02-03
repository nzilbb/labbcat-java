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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a single match from search results.
 * @author Robert Fromont robert@fromont.net.nz
 */

public class Match
{
   // Attributes:
   
   /**
    * The match ID, which encodes which token in which utterance by which participant of
    * which transcript matched. 
    * @see #getMatchId()
    * @see #setMatchId(String)
    */
   protected String MatchId;
   /**
    * Getter for {@link #MatchId}: The match ID, which encodes which token in which
    * utterance by which participant of which transcript matched. 
    * @return The match ID, which encodes which token in which utterance by which
    * participant of which transcript matched. 
    */
   public String getMatchId() { return MatchId; }
   /**
    * Setter for {@link #MatchId}: The match ID, which encodes which token in which
    * utterance by which participant of which transcript matched. 
    * @param newMatchId The match ID, which encodes which token in which utterance by
    * which participant of which transcript matched. 
    */
   public Match setMatchId(String newMatchId) { MatchId = newMatchId; return this; }

   /**
    * The name of the transcript document that the match appearsis from.
    * @see #getTranscript()
    * @see #setTranscript(String)
    */
   protected String Transcript;
   /**
    * Getter for {@link #Transcript}: The name of the transcript document that the match
    * appearsis from. 
    * @return The name of the transcript document that the match appearsis from.
    */
   public String getTranscript() { return Transcript; }
   /**
    * Setter for {@link #Transcript}: The name of the transcript document that the match
    * appearsis from. 
    * @param newTranscript The name of the transcript document that the match appearsis from.
    */
   public Match setTranscript(String newTranscript) { Transcript = newTranscript; return this; }

   /**
    * The name of the participant who uttered the match.
    * @see #getParticipant()
    * @see #setParticipant(String)
    */
   protected String Participant;
   /**
    * Getter for {@link #Participant}: The name of the participant who uttered the match.
    * @return The name of the participant who uttered the match.
    */
   public String getParticipant() { return Participant; }
   /**
    * Setter for {@link #Participant}: The name of the participant who uttered the match.
    * @param newParticipant The name of the participant who uttered the match.
    */
   public Match setParticipant(String newParticipant) { Participant = newParticipant; return this; }

   /**
    * The corpus the match comes from
    * @see #getCorpus()
    * @see #setCorpus(String)
    */
   protected String Corpus;
   /**
    * Getter for {@link #Corpus}: The corpus the match comes from
    * @return The corpus the match comes from
    */
   public String getCorpus() { return Corpus; }
   /**
    * Setter for {@link #Corpus}: The corpus the match comes from
    * @param newCorpus The corpus the match comes from
    */
   public Match setCorpus(String newCorpus) { Corpus = newCorpus; return this; }

   /**
    * The start time of the utterance.
    * @see #getLine()
    * @see #setLine(Double)
    */
   protected Double Line;
   /**
    * Getter for {@link #Line}: The start time of the utterance.
    * @return The start time of the utterance.
    */
   public Double getLine() { return Line; }
   /**
    * Setter for {@link #Line}: The start time of the utterance.
    * @param newLine The start time of the utterance.
    */
   public Match setLine(Double newLine) { Line = newLine; return this; }

   /**
    * The end time of the utterance.
    * @see #getLineEnd()
    * @see #setLineEnd(Double)
    */
   protected Double LineEnd;
   /**
    * Getter for {@link #LineEnd}: The end time of the utterance.
    * @return The end time of the utterance.
    */
   public Double getLineEnd() { return LineEnd; }
   /**
    * Setter for {@link #LineEnd}: The end time of the utterance.
    * @param newLineEnd The end time of the utterance.
    */
   public Match setLineEnd(Double newLineEnd) { LineEnd = newLineEnd; return this; }

   /**
    * The context before the match.
    * @see #getBeforeMatch()
    * @see #setBeforeMatch(String)
    */
   protected String BeforeMatch;
   /**
    * Getter for {@link #BeforeMatch}: The context before the match.
    * @return The context before the match.
    */
   public String getBeforeMatch() { return BeforeMatch; }
   /**
    * Setter for {@link #BeforeMatch}: The context before the match.
    * @param newBeforeMatch The context before the match.
    */
   public Match setBeforeMatch(String newBeforeMatch) { BeforeMatch = newBeforeMatch; return this; }

   /**
    * The match text.
    * @see #getText()
    * @see #setText(String)
    */
   protected String Text;
   /**
    * Getter for {@link #Text}: The match text.
    * @return The match text.
    */
   public String getText() { return Text; }
   /**
    * Setter for {@link #Text}: The match text.
    * @param newText The match text.
    */
   public Match setText(String newText) { Text = newText; return this; }

   /**
    * The context after the match.
    * @see #getAfterMatch()
    * @see #setAfterMatch(String)
    */
   protected String AfterMatch;
   /**
    * Getter for {@link #AfterMatch}: The context after the match.
    * @return The context after the match.
    */
   public String getAfterMatch() { return AfterMatch; }
   /**
    * Setter for {@link #AfterMatch}: The context after the match.
    * @param newAfterMatch The context after the match.
    */
   public Match setAfterMatch(String newAfterMatch) { AfterMatch = newAfterMatch; return this; }
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public Match()
   {
   } // end of constructor
   
   /**
    * Constructor from JSON.
    */
   public Match(JSONObject json)
   {
      MatchId = json.optString("MatchId");
      Transcript = json.optString("Transcript");
      Participant = json.optString("Participant");
      Corpus = json.optString("Corpus");
      Line = json.optDouble("Line");
      LineEnd = json.optDouble("LineEnd");
      BeforeMatch = json.optString("BeforeMatch");
      Text = json.optString("Text");
      AfterMatch = json.optString("AfterMatch");
   } // end of constructor

   
   /**
    * String represtation for logging.
    * @return A string representation of the object.
    */
   public String toString()
   {
      return MatchId + ": [" + BeforeMatch + "] " + Text + " [" + AfterMatch + "]";
   } // end of toString()

} // end of class Match
