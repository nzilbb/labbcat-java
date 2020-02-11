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

import java.util.HashMap;
import java.util.Map;

/**
 * Interpreter for match ID strings.
 * <p>The schema is:</p>
 * <ul>
 * 	<li>
 * 		when there's a defining annotation UID:<br>
 * 		g_<i>ag_id</i>;<em>uid</em><br>
 * 		e.g. <tt>g_243;em_12_20035</tt></li>
 * 	<li>
 * 		when there's anchor IDs:<br>
 * 		g_<i>ag_id</i>;<em>startuid</em>-<em>enduid</em><br>
 * 		e.g. <tt>g_243;n_72700-n_72709</tt></li>
 * 	<li>
 * 		when there's anchor offsets:<br>
 * 		g_<i>ag_id</i>;<em>startoffset</em>-<em>endoffset</em><br>
 * 		e.g. <tt>g_243;39.400-46.279</tt></li>
 * 	<li>
 * 		when there's a participant/speaker number, it will be appended:<br>
 * 		<em>...</em>;p_<em>speakernumber</em><br>
 * 		e.g.&nbsp;<tt>g_243;n_72700-n_72709;p_76</tt></li>
 * 	<li>
 * 		a target annotation by appending a uid prefixed by <samp>#=</samp>:<br>
 * 		...;#=<em>uid</em><br>
 * 		e.g. <samp>g_243;n_72700-n_72709;#=ew_0_123</samp></li>
 * 	<li>
 * 		other items (search name or prefix) could then come after all that, and key=value pairs:<br>
 * 		...;<em>key</em>=<em>value</em><br>
 * 		e.g.&nbsp;<tt>g_243;n_72700-n_72709;ew_0_123-ew_0_234;prefix=024-;name=the_aeiou</tt></li>
 * <p>These can be something like:
 * <ul>
 * <li><q>g_3;em_11_23;n_19985-n_20003;p_4;#=ew_0_12611;prefix=001-;[0]=ew_0_12611</q></li>
 * <li><q>AgnesShacklock-01.trs;60.897-67.922;prefix=001-</q></li>
 * <li><q>AgnesShacklock-01.trs;60.897-67.922;m_-1_23-</q></li>
 * </ul>
 * @author Robert Fromont robert@fromont.net.nz
 */

public class MatchId {
   
   // Attributes:
   
   /**
    * The graph identifier.
    * @see #getGraphId()
    */
   protected String graphId;
   /**
    * Getter for {@link #graphId}: The graph identifier.
    * @return The graph identifier.
    */
   public String getGraphId() { return graphId; }
   
   /**
    * ID of the start anchor.
    * @see #getStartAnchorId()
    */
   protected String startAnchorId;
   /**
    * Getter for {@link #startAnchorId}: ID of the start anchor.
    * @return ID of the start anchor.
    */
   public String getStartAnchorId() { return startAnchorId; }

   /**
    * ID of the end anchor.
    * @see #getEndAnchorId()
    */
   protected String endAnchorId;
   /**
    * Getter for {@link #endAnchorId}: ID of the end anchor.
    * @return ID of the end anchor.
    */
   public String getEndAnchorId() { return endAnchorId; }

   /**
    * Offset of the start anchor.
    * @see #getStartOffset()
    */
   protected Double startOffset;
   /**
    * Getter for {@link #startOffset}: Offset of the start anchor.
    * @return Offset of the start anchor.
    */
   public Double getStartOffset() { return startOffset; }

   /**
    * Offset of the end anchor.
    * @see #getEndOffset()
    */
   protected Double endOffset;
   /**
    * Getter for {@link #endOffset}: Offset of the end anchor.
    * @return Offset of the end anchor.
    */
   public Double getEndOffset() { return endOffset; }

   /**
    * ID of the match utterance.
    * @see #getUtteranceId()
    */
   protected String utteranceId;
   /**
    * Getter for {@link #utteranceId}: ID of the match utterance.
    * @return ID of the match utterance.
    */
   public String getUtteranceId() { return utteranceId; }

   /**
    * ID of the match target annotation.
    * @see #getTargetId()
    */
   protected String targetId;
   /**
    * Getter for {@link #targetId}: ID of the match target annotation.
    * @return ID of the match target annotation.
    */
   public String getTargetId() { return targetId; }

   /**
    * Match prefix for fragments.
    * @see #getPrefix()
    */
   protected String prefix;
   /**
    * Getter for {@link #prefix}: Match prefix for fragments.
    * @return Match prefix for fragments.
    */
   public String getPrefix() { return prefix; }

   /**
    * Other attributes in the MatchId.
    * @see #getAttributes()
    */
   protected Map<String,String> attributes = new HashMap<String,String>();
   /**
    * Getter for {@link #attributes}: Other attributes in the MatchId.
    * @return Other attributes in the MatchId.
    */
   public Map<String,String> getAttributes() { return attributes; }
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public MatchId() {
   } // end of constructor
   
   /**
    * String constructor.
    */
   public MatchId(String matchId) {
      parseId(matchId);
   } // end of constructor

   /**
    * String constructor.
    */
   public MatchId(Match match) {
      parseId(match.getMatchId());
   } // end of constructor

   /**
    * Parses the given match ID string.
    * @param matchId
    * @return A reference to this object.
    */
   public MatchId parseId(String matchId) {
      
      String[] parts = matchId.split(";");
      graphId = parts[0];
      String intervalPart = null;
      for (int p = 1; p < parts.length; p++) {
         if (parts[p].indexOf("-") > 0) {
            intervalPart = parts[p];
            break;
         }
      }
      String[] interval = intervalPart.split("-");
      if (interval[0].startsWith("n_")) { // anchor IDs
         startAnchorId = interval[0];
         endAnchorId = interval[1];
      } else { // offsets
         startOffset = Double.parseDouble(interval[0]);
         endOffset = Double.parseDouble(interval[1]);
      }
      for (int p = 1; p < parts.length; p++) {
         if (parts[p].startsWith("prefix=")) {
            prefix = parts[p].substring("prefix=".length());
         } else if (parts[p].startsWith("em_") || parts[p].startsWith("m_")) {
            utteranceId = parts[p];
         } else if (parts[p].startsWith("#=")) {
            targetId = parts[p].substring("#=".length());
         }
      } // next part
      return this;
   } // end of parseId()

} // end of class MatchId
