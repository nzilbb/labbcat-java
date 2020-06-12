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
package nzilbb.labbcat.util;

import java.lang.reflect.Method;
import java.util.Vector;
import nzilbb.labbcat.LabbcatAdmin;
import nzilbb.labbcat.Response;
import nzilbb.labbcat.ResponseException;
import nzilbb.util.CommandLineProgram;
import nzilbb.util.ProgramDescription;
import nzilbb.util.Switch;
import nzilbb.util.Timers;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Command-line utility for making ad-hoc LaBB-CAT API calls.
 * <p> The utility generally prints the JSON response from the server.
 * e.g. 
 * <p><tt>java -jar nzilbb.labbcat.jar --labbcaturl=&hellip; --username=&hellip; --password=&hellip; getLayerIds</tt>
 * <p> &hellip; might print:
 * <p><tt>{"title":"LaBB-CAT","version":"20200212.1030","code":0,"errors":[],"messages":[],"model":"http://localhost:8080/labbcat/"}</tt>
 * <p>Human-readable JSON can be obtained using the <tt>--indent</tt> switch, 
 * e.g.
 * <p><tt>java -jar nzilbb.labbcat.jar <b>--indent</b> --labbcaturl=&hellip; getLayer orthography</tt>
 * <p> &hellip; might print:
 * <pre>{
 *     "title": "LaBB-CAT",
 *     "version": "20200212.1030",
 *     "code": 0,
 *     "errors": [],
 *     "messages": [],
 *     "model": {
 *         "id": "orthography",
 *         "parentId": "transcript",
 *         "description": "Standard Orthography",
 *         "alignment": 0,
 *         "peers": false,
 *         "peersOverlap": false,
 *         "parentIncludes": false,
 *         "saturated": false,
 *         "type": "string",
 *         "validLabels": {},
 *         "category": null
 *     }
 * }</pre>
 * 
 * @author Robert Fromont robert@fromont.net.nz
 */

@ProgramDescription(value="Command-line utility for making ad-hoc LaBB-CAT API calls.",arguments="function [arg ...]")
public class CommandLine extends CommandLineProgram {
   
   public static void main(String argv[]) {
      CommandLine application = new CommandLine();
      if (application.processArguments(argv)) {
         application.start();
      }
   }

   // Attributes:
   
   /**
    * Base URL for the LaBB-CAT server.
    * @see #getLabbcatUrl()
    * @see #setLabbcatUrl(String)
    */
   protected String labbcatUrl;
   /**
    * Getter for {@link #labbcatUrl}: Base URL for the LaBB-CAT server.
    * @return Base URL for the LaBB-CAT server.
    */
   public String getLabbcatUrl() { return labbcatUrl; }
   /**
    * Setter for {@link #labbcatUrl}: Base URL for the LaBB-CAT server.
    * @param newLabbcatUrl Base URL for the LaBB-CAT server.
    */
   @Switch(value="Base URL for the LaBB-CAT server.",compulsory=true)
   public CommandLine setLabbcatUrl(String newLabbcatUrl) { labbcatUrl = newLabbcatUrl; return this; }

   /**
    * Username for LaBB-CAT.
    * @see #getUsername()
    * @see #setUsername(String)
    */
   protected String username;
   /**
    * Getter for {@link #username}: Username for LaBB-CAT.
    * @return Username for LaBB-CAT.
    */
   public String getUsername() { return username; }
   /**
    * Setter for {@link #username}: Username for LaBB-CAT.
    * @param newUsername Username for LaBB-CAT.
    */
   @Switch("Username for LaBB-CAT.")
   public CommandLine setUsername(String newUsername) { username = newUsername; return this; }

   /**
    * Password for LaBB-CAT.
    * @see #getPassword()
    * @see #setPassword(String)
    */
   protected String password;
   /**
    * Getter for {@link #password}: Password for LaBB-CAT.
    * @return Password for LaBB-CAT.
    */
   public String getPassword() { return password; }
   /**
    * Setter for {@link #password}: Password for LaBB-CAT.
    * @param newPassword Password for LaBB-CAT.
    */
   @Switch("Password for LaBB-CAT.")
   public CommandLine setPassword(String newPassword) { password = newPassword; return this; }
      
   /**
    * Whether to produce verbose logging.
    * @see #getVerbose()
    * @see #setVerbose(Boolean)
    */
   protected Boolean verbose = Boolean.FALSE;
   /**
    * Getter for {@link #verbose}: Whether to produce verbose logging.
    * @return Whether to produce verbose logging.
    */
   public Boolean getVerbose() { return verbose; }
   /**
    * Setter for {@link #verbose}: Whether to produce verbose logging.
    * @param newVerbose Whether to produce verbose logging.
    */
   @Switch("Whether to produce verbose logging.")
   public CommandLine setVerbose(Boolean newVerbose) { verbose = newVerbose; return this; }

   /**
    * Whether to indent JSON for readability.
    * @see #getIndent()
    * @see #setIndent(Boolean)
    */
   protected Boolean indent = Boolean.FALSE;
   /**
    * Getter for {@link #indent}: Whether to indent JSON for readability.
    * @return Whether to indent JSON for readability.
    */
   public Boolean getIndent() { return indent; }
   /**
    * Setter for {@link #indent}: Whether to indent JSON for readability.
    * @param newIndent Whether to indent JSON for readability.
    */
   @Switch("Whether to indent JSON for readability.")
   public CommandLine setIndent(Boolean newIndent) { indent = newIndent; return this; }

   // Methods:
   
   /**
    * Default constructor.
    */
   public CommandLine() {
   } // end of constructor

   /** Start the utility */
   public void start() {
      
      try {
         LabbcatAdmin labbcat = new LabbcatAdmin(labbcatUrl, username, password);
         labbcat.setVerbose(verbose);

         if (arguments.size() == 0) {
            System.err.println("No function specified.");
            return;
         }

         String function = arguments.get(0);
         try { // ResponseException
            // directly parse the currently-known functions
            if (function.equalsIgnoreCase("getId")) { // GraphStoreQuery...
               labbcat.getId();
            } else if (function.equalsIgnoreCase("getLayerIds")) {
               labbcat.getLayerIds();
            } else if (function.equalsIgnoreCase("getLayers")) {
               labbcat.getLayers();
            } else if (function.equalsIgnoreCase("getLayer")) {
               labbcat.getLayer(arguments.get(1));
            } else if (function.equalsIgnoreCase("getCorpusIds")) {
               labbcat.getCorpusIds();
            } else if (function.equalsIgnoreCase("getParticipantIds")) {
               labbcat.getParticipantIds();
            } else if (function.equalsIgnoreCase("getParticipant")) {
               labbcat.getParticipant(arguments.get(1));
            } else if (function.equalsIgnoreCase("countMatchingParticipantIds")) {
               labbcat.countMatchingParticipantIds(arguments.get(1));
            } else if (function.equalsIgnoreCase("getTranscriptIds")) {
               labbcat.getTranscriptIds();
            } else if (function.equalsIgnoreCase("getTranscriptIdsInCorpus")) {
               labbcat.getTranscriptIdsInCorpus(arguments.get(1));
            } else if (function.equalsIgnoreCase("getTranscriptIdsWithParticipant")) {
               labbcat.getTranscriptIdsWithParticipant(arguments.get(1));
            } else if (function.equalsIgnoreCase("countMatchingTranscriptIds")) {
               labbcat.countMatchingTranscriptIds(arguments.get(1));
            } else if (function.equalsIgnoreCase("countMatchingAnnotations")) {
               labbcat.countMatchingAnnotations(arguments.get(1));
            } else if (function.equalsIgnoreCase("countAnnotations")) {
               labbcat.countAnnotations(arguments.get(1), arguments.get(2));
            } else if (function.equalsIgnoreCase("getMediaTracks")) {
               labbcat.getMediaTracks();
            } else if (function.equalsIgnoreCase("getAvailableMedia")) {
               labbcat.getAvailableMedia(arguments.get(1));
            } else if (function.equalsIgnoreCase("getMedia")) {
               labbcat.getMedia(arguments.get(1), arguments.get(2), arguments.get(3));
            } else if (function.equalsIgnoreCase("getEpisodeDocuments")) {
               labbcat.getEpisodeDocuments(arguments.get(1));
            } else if (function.equalsIgnoreCase("deleteTranscript")) { // GraphStore...
               labbcat.deleteTranscript(arguments.get(1));
            } else if (function.equalsIgnoreCase("taskStatus")) { // Labbcat...
               labbcat.taskStatus(arguments.get(1));
            } else if (function.equalsIgnoreCase("cancelTask")) {
               labbcat.cancelTask(arguments.get(1));
            } else if (function.equalsIgnoreCase("releaseTask")) {
               labbcat.releaseTask(arguments.get(1));
            } else if (function.equalsIgnoreCase("getTasks")) {
               labbcat.getTasks();
            } else if (function.equalsIgnoreCase("cancelTask")) {
               labbcat.cancelTask(arguments.get(1));
            } else if (function.equalsIgnoreCase("cancelTask")) {
               labbcat.cancelTask(arguments.get(1));
            } else if (function.equalsIgnoreCase("cancelTask")) {
               labbcat.cancelTask(arguments.get(1));
            } else if (function.equalsIgnoreCase("cancelTask")) {
               labbcat.cancelTask(arguments.get(1));
            } else if (function.equalsIgnoreCase("cancelTask")) {
               labbcat.cancelTask(arguments.get(1));
               // TODO explicitly map more functions - it should be faster
            } else { // not a predetermined function - use reflection to figure it out
               Method[] methods = labbcat.getClass().getMethods();
               for (Method method : methods) {
                  if (function.equals(method.getName())) {
                     @SuppressWarnings("rawtypes")
                        Class[] parameterTypes = method.getParameterTypes();
                     Vector<Object> parameters = new Vector<Object>();
                     int argNumber = 1;
                     
                     for (@SuppressWarnings("rawtypes") Class type : parameterTypes) {
                        if (arguments.size() <= argNumber) {
                           parameters.add(null);
                           argNumber++;
                        } else {
                           String arg = arguments.get(argNumber++);
                           
                           if (type.equals(String.class)) {
                              parameters.add(arg);
                           } else if (type.equals(Integer.class)) {
                              
                              parameters.add(Integer.valueOf(arg));
                           } else if (type.equals(Long.class)) {
                              parameters.add(Long.valueOf(arg));
                           } else if (type.equals(Double.class)) {
                              parameters.add(Double.valueOf(arg));
                           } else {
                              System.err.println(
                                 "Cannot call " + function + " parameter " + (argNumber-1)
                                 + " ("+arg+") is of an unsupported type: " + type.getName());
                              return;
                           }
                        }                    
                     } // next parameter
                     
                     switch (parameterTypes.length) {
                        case 0:
                           method.invoke(labbcat);
                           break;
                        case 1:
                           method.invoke(labbcat, parameters.get(0));
                           break;
                        case 2:
                           method.invoke(labbcat, parameters.get(0), parameters.get(1));
                           break;
                        case 3:
                           method.invoke(labbcat,
                                         parameters.get(0), parameters.get(1), parameters.get(2));
                           break;
                        case 4:
                           method.invoke(labbcat,
                                         parameters.get(0), parameters.get(1), parameters.get(2),
                                         parameters.get(3));
                           break;
                        case 5:
                           method.invoke(labbcat,
                                         parameters.get(0), parameters.get(1), parameters.get(2),
                                         parameters.get(3), parameters.get(4));
                           break;
                        case 6:
                           method.invoke(labbcat,
                                         parameters.get(0), parameters.get(1), parameters.get(2),
                                         parameters.get(3), parameters.get(4), parameters.get(5));
                           break;
                        default:
                           System.err.println(
                              "Cannot call " + function + " too many parameters required ("
                              +parameterTypes.length+").");
                           return;
                     }
                     break;
                  } // method name matches
               } // next method
            } // not a predetermined function
            
         } catch(ResponseException exception) {} // do nothing, report the response:
         Response response = labbcat.getResponse();
         if (response.getRaw() != null && response.getRaw().length() > 0) {
            if (indent) {
               try {
                  System.out.println(new JSONObject(response.getRaw()).toString(4));
               }
               catch(JSONException exception) {
                  // couldn't parse as JSON, so just print the raw response
                  System.out.println(response.getRaw());
               }
            } else { // not indent
               System.out.println(response.getRaw());
            }
         } else { // no response text
            System.out.println("HTTP: " + response.getHttpStatus());
         }
         
      } catch(ArrayIndexOutOfBoundsException exception) {
         System.err.println("ERROR Not enough parameters supplied.");
      } catch(Exception exception) {
         System.err.println("ERROR: " + exception.toString());
         exception.printStackTrace(System.err);
      }
   } // end of start()
   
} // end of class CommandLine

