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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import nzilbb.ag.Annotation;
import nzilbb.ag.Graph;
import nzilbb.ag.GraphNotFoundException;
import nzilbb.ag.GraphStore;
import nzilbb.ag.PermissionException;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.http.*;

/**
 * Client-side implementation of 
 * <a href="https://nzilbb.github.io/ag/javadoc/nzilbb/ag/GraphStore.html">nzilbb.ag.GraphStore</a>.
 * <p>This class inherits the <em>read-only</em> operations of {@link LabbcatView}
 * and adds some <em>write</em> operations for updating data, i.e. those that can be
 * performed by users with <q>edit</q> permission.
 * <p> e.g.
 * <pre> // create annotation store client
 * LabbcatEdit store = new {@link #LabbcatEdit(String,String,String) LabbcatEdit}("https://labbcat.canterbury.ac.nz", "demo", "demo");
 *
 * // get a corpus ID
 * String[] corpora = labbcat.{@link LabbcatView#getCorpusIds() getCorpusIds()};
 * String corpus = ids[0];
 *
 * // get a transcript type
 * Layer typeLayer = labbcat.{@link LabbcatView#getLayer(String) getLayer("transcript_type")};
 * String transcriptType = typeLayer.getValidLabels().keySet().iterator().next();
 *
 * // upload a transcript
 * File transcript = new File("/some/transcript.txt");
 * String taskId = labbcat.{@link #newTranscript(File,File[],String,String,String,String) newTranscript(transcript, null, null, transcriptType, corpus, "test")};
 *
 * // wait until all automatic annotations have been generated
 * TaskStatus layerGenerationTask = labbcat.{@link #waitForTask(String,int) waitForTask(taskId, 30)};
 *
 * // get all the POS annotations
 * Annotation[] pos = labbcat.{@link LabbcatView#getAnnotations(String,String,Integer,Integer) getAnnotations(transcript.getName(), "pos")};
 *
 * // search for tokens of "and"
 * Matches[] matches = labbcat.{@link LabbcatView#getMatches(String,int) getMatches}(
 *     labbcat.{@link LabbcatView#search(JsonObject,String[],String[],boolean,boolean,Integer) search}(
 *        new {@link PatternBuilder}().addMatchLayer("orthography", "and").build(),
 *        participantIds, null, true, false, null), 1);
 *
 * // delete the transcript
 * store.{@link #deleteGraph(String) deleteGraph}(transcript.getName());
 * </pre>
 * @author Robert Fromont robert@fromont.net.nz
 */

public class LabbcatEdit extends LabbcatView implements GraphStore
{
   // Attributes:
  
   // Methods:
   
   /**
    * Default constructor.
    */
   public LabbcatEdit() {
   } // end of constructor
   
   /**
    * Constructor from string URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public LabbcatEdit(String labbcatUrl) throws MalformedURLException {
      super(labbcatUrl);
   } // end of constructor
   
   /**
    * Constructor with String attributes.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    * @param username LaBB-CAT username.
    * @param password LaBB-CAT password.
    */
   public LabbcatEdit(String labbcatUrl, String username, String password)
      throws MalformedURLException {
      super(labbcatUrl, username, password);
   } // end of constructor
   
   /**
    * Constructor from URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public LabbcatEdit(URL labbcatUrl) {
      super(labbcatUrl);
   } // end of constructor
   
   /**
    * Constructor with attributes.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    * @param username LaBB-CAT username.
    * @param password LaBB-CAT password.
    */
   public LabbcatEdit(URL labbcatUrl, String username, String password) {
      super(labbcatUrl, username, password);
   } // end of constructor

   /**
    * Constructs a URL for the given resource.
    * @param resource
    * @return A URL for the given resource.
    * @throws StoreException If the URL is malformed.
    */
   public URL editUrl(String resource) throws StoreException {
      
      try {
         return new URL(new URL(labbcatUrl, "api/edit/store/"), resource);
      } catch(Throwable t) {
         throw new StoreException("Could not construct request URL.", t);
      }
   } // end of editUrl()   

   // GraphStore methods:
   
   /**
    * <em>NOT YET IMPLEMENTED</em> - Saves the given transcript. The transcript can be partial e.g. include only some of the layers
    * that the stored version of the transcript contains.
    * @param transcript The transcript to save.
    * @return true if changes were saved, false if there were no changes to save.
    * @throws StoreException If an error prevents the transcript from being saved.
    * @throws PermissionException If saving the transcript is not permitted.
    * @throws GraphNotFoundException If the transcript doesn't exist.
    */
   public boolean saveTranscript(Graph transcript)
      throws StoreException, PermissionException, GraphNotFoundException {
      throw new StoreException("Not implemented");
   }

   /**
    * Creates an annotation starting at <var>from</var> and ending at <var>to</var>.
    * @param id The ID of the transcript.
    * @param fromId The start anchor's ID. TODO: an expression identifying the start
    * anchor's ID. e.g. "'n_123'" or "start.id" or maybe something like
    * "first('segments').start.id)"
    * @param toId The end anchor's ID. TODO: an expression identifying the end anchor's
    * ID. e.g. "'n_123'" or "end.id" or maybe something like "last('segments').end.id)"
    * @param layerId The layer ID of the resulting annotation.
    * @param label The label of the resulting annotation. TODO an expression identifying
    * the label. e.g. "'@gz#mpP'" or "my('orthography').label" or maybe something like
    * "SUM(list('segments').duration)"
    * @param confidence The confidence rating.
    * @param parentId The new annotation's parent's ID. TODO: an expression identifying the
    * parent. e.g. "'em_0_123'" or "layer.id = 'orthography' AND label = 'example'"
    * @return The ID of the new annotation.
    */
   public String createAnnotation(String id, String fromId, String toId, String layerId, String label, Integer confidence, String parentId)
      throws StoreException, PermissionException, GraphNotFoundException {
      
      try {
         URL url = editUrl("createAnnotation");
         HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
            .setUserAgent()
            .setHeader("Accept", "application/json")
            .setParameter("id", id)
            .setParameter("fromId", fromId)
            .setParameter("toId", toId)
            .setParameter("layerId", layerId)
            .setParameter("label", label)
            .setParameter("confidence", confidence)
            .setParameter("parentId", parentId);
         if (verbose) System.out.println("createAnnotation -> " + request);
         response = new Response(request.post().getInputStream(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         return (String)response.getModel();
      } catch(IOException x) {
         throw new StoreException("Could not get response.", x);
      }
   }

   /**
    * Destroys the annotation with the given ID.
    * @param id The ID of the transcript.
    * @param annotationId The annotation's ID.
    */
   public void destroyAnnotation(String id, String annotationId)
      throws StoreException, PermissionException, GraphNotFoundException {
      
      try {
         URL url = editUrl("destroyAnnotation");
         HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
            .setUserAgent()
            .setHeader("Accept", "application/json")
            .setParameter("id", id)
            .setParameter("annotationId", annotationId);
         if (verbose) System.out.println("destroyAnnotation -> " + request);
         response = new Response(request.post().getInputStream(), verbose);
         response.checkForErrors(); // throws a StoreException on error
      } catch(IOException x) {
         throw new StoreException("Could not get response.", x);
      }
   }
   
   /**
    * <em>NOT YET IMPLEMENTED</em> - Saves a participant, and all its tags, to the database.  The participant is
    * represented by an Annotation that isn't assumed to be part of a transcript.
    * @param participant
    * @return true if changes were saved, false if there were no changes to save.
    * @throws StoreException If an error prevents the participant from being saved.
    * @throws PermissionException If saving the participant is not permitted.
    */
   public boolean saveParticipant(Annotation participant)
      throws StoreException, PermissionException {
      
      throw new StoreException("Not implemented");
   }

   /**
    * <em>NOT YET IMPLEMENTED</em> - Saves the given media for the given transcript
    * @param id The transcript ID
    * @param trackSuffix The track suffix of the media
    *  - see <a href="https://nzilbb.github.io/ag/javadoc/nzilbb/ag/MediaTrackDefinition.html#suffix">MediaTrackDefinition.suffix</a>}.
    * @param mediaUrl A URL to the media content.
    * @throws StoreException If an error prevents the media from being saved.
    * @throws PermissionException If saving the media is not permitted.
    * @throws GraphNotFoundException If the transcript doesn't exist.
    */
   public void saveMedia(String id, String trackSuffix, String mediaUrl)
      throws StoreException, PermissionException, GraphNotFoundException {
      
      throw new StoreException("Not implemented");
   }

   /**
    * <em>NOT YET IMPLEMENTED</em> - Saves the given source file for the given transcript.
    * @param id The transcript ID
    * @param url A URL to the transcript.
    * @throws StoreException If an error prevents the media from being saved.
    * @throws PermissionException If saving the media is not permitted.
    * @throws GraphNotFoundException If the transcript doesn't exist.
    */
   public void saveSource(String id, String url)
      throws StoreException, PermissionException, GraphNotFoundException {
      
      throw new StoreException("Not implemented");
   }

   /**
    * <em>NOT YET IMPLEMENTED</em> - Saves the given document for the episode of the given transcript.
    * @param id The transcript ID
    * @param url A URL to the document.
    * @throws StoreException If an error prevents the media from being saved.
    * @throws PermissionException If saving the media is not permitted.
    * @throws GraphNotFoundException If the transcript doesn't exist.
    */
   public void saveEpisodeDocument(String id, String url)
      throws StoreException, PermissionException, GraphNotFoundException {
      
      throw new StoreException("Not implemented");
   }

   /**
    * Deletes the given transcript, and all associated files.
    * @param id The ID transcript to save.
    * @throws StoreException If an error prevents the transcript from being deleted.
    * @throws PermissionException If deleting the transcript is not permitted.
    * @throws GraphNotFoundException If the transcript doesn't exist.
    */
   public void deleteTranscript(String id)
      throws StoreException, PermissionException, GraphNotFoundException {
      
      try {
         URL url = editUrl("deleteTranscript");
         HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
            .setUserAgent()
            .setHeader("Accept", "application/json")
            .setParameter("id", id);
         if (verbose) System.out.println("deleteTranscript -> " + request);
         response = new Response(request.post(), verbose);
         response.checkForErrors(); // throws a StoreException on error
      } catch(IOException x) {
         throw new StoreException("Could not get response.", x);
      }
   }

   /**
    * Deletes the given participant, and all associated meta-data.
    * @param id The ID participant to delete.
    * @throws StoreException If an error prevents the transcript from being saved.
    * @throws PermissionException If saving the transcript is not permitted.
    * @throws GraphNotFoundException If the transcript doesn't exist.
    */
   public void deleteParticipant(String id)
      throws StoreException, PermissionException, GraphNotFoundException {
      try {
         URL url = editUrl("deleteParticipant");
         HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
            .setUserAgent()
            .setHeader("Accept", "application/json")
            .setParameter("id", id);
         if (verbose) System.out.println("deleteParticipant -> " + request);
         response = new Response(request.post(), verbose);
         response.checkForErrors(); // throws a StoreException on error
      } catch(IOException x) {
         throw new StoreException("Could not get response.", x);
      }
   }

   // Other methods:
   
   /**
    * Upload a new transcript.
    * @param transcript The transcript to upload.
    * @param media The media to upload, if any.
    * @param mediaSuffix The media suffix for the media.
    * @param transcriptType The transcript type.
    * @param corpus The corpus for the transcript.
    * @param episode The episode the transcript belongs to.
    * @return The taskId of the server task processing the upload. 
    * @throws IOException
    * @throws ResponseException
    */
   public String newTranscript(File transcript, File[] media, String mediaSuffix, String transcriptType, String corpus, String episode)
      throws IOException, StoreException {
      
      cancelling = false;
      URL url = makeUrl("edit/transcript/new");
      postRequest = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
         .setUserAgent()
         .setHeader("Accept", "application/json")
         .setParameter("todo", "new")
         .setParameter("auto", true)
         .setParameter("transcriptType", transcriptType)
         .setParameter("corpus", corpus)
         .setParameter("episode", episode)
         .setParameter("uploadfile1_0", transcript);
      if (media != null && media.length > 0) {
         if (mediaSuffix == null) mediaSuffix = "";
         for (int f = 0; f < media.length; f++) {
            postRequest.setParameter("uploadmedia"+mediaSuffix+"1", media[f]);
         } // next file
      }
      if (verbose) System.out.println("taskStatus -> " + postRequest);
      response = new Response(postRequest.post(), verbose);
      response.checkForErrors(); // throws a ResponseException on error

      // extract the threadId from model.result.id
      JsonObject model = (JsonObject)response.getModel();
      JsonObject result = model.getJsonObject("result");
      return result.getString(transcript.getName());
   } // end of newTranscript()

   /**
    * Uploads a new version of an existing transcript.
    * @param transcript
    * @return The taskId of the server task processing the upload. 
    * @throws IOException
    * @throws ResponseException
    */
   public String updateTranscript(File transcript)
      throws IOException, StoreException {
      
      cancelling = false;
      URL url = makeUrl("edit/transcript/new");
      postRequest = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
         .setUserAgent()
         .setHeader("Accept", "application/json")
         .setParameter("todo", "update")
         .setParameter("auto", true)
         .setParameter("uploadfile1_0", transcript);
      if (verbose) System.out.println("taskStatus -> " + postRequest);
      response = new Response(postRequest.post(), verbose);
      response.checkForErrors(); // throws a ResponseException on error
      
      // extract the threadId from model.result.id
      JsonObject model = (JsonObject)response.getModel();
      JsonObject result = model.getJsonObject("result");
      return result.getString(transcript.getName());
   } // end of updateTranscript()
   
} // end of class LabbcatEdit
