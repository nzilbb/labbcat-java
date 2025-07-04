//
// Copyright 2020-2025 New Zealand Institute of Language, Brain and Behaviour, 
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import nzilbb.ag.Annotation;
import nzilbb.ag.Graph;
import nzilbb.ag.GraphNotFoundException;
import nzilbb.ag.GraphStore;
import nzilbb.ag.MediaFile;
import nzilbb.ag.PermissionException;
import nzilbb.ag.StoreException;
import nzilbb.ag.serialize.SerializationDescriptor;
import nzilbb.ag.serialize.SerializationException;
import nzilbb.ag.serialize.SerializerNotConfiguredException;
import nzilbb.ag.serialize.json.JSONSerialization;
import nzilbb.ag.serialize.util.NamedStream;
import nzilbb.ag.serialize.util.Utility;
import nzilbb.configure.Parameter;
import nzilbb.configure.ParameterSet;
import nzilbb.labbcat.http.*;
import nzilbb.labbcat.model.Upload;
import nzilbb.util.IO;

/**
 * Client-side implementation of 
 * <a href="https://nzilbb.github.io/ag/apidocs/nzilbb/ag/GraphStore.html">nzilbb.ag.GraphStore</a>.
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
 *     labbcat.{@link LabbcatView#search(JsonObject,String[],String[],boolean,Integer,Integer,Integer) search}(
 *        new {@link PatternBuilder}().addMatchLayer("orthography", "and").build(),
 *        participantIds, null, true, false, null, null), 1);
 *
 * // delete the transcript
 * store.{@link #deleteGraph(String) deleteGraph}(transcript.getName());
 * </pre>
 * @author Robert Fromont robert@fromont.net.nz
 */

public class LabbcatEdit extends LabbcatView implements GraphStore {
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
   * @throws MalformedURLException If the URL is invalid.
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
   * @throws MalformedURLException If the URL is invalid.
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
   * @param resource The relative name of the resource.
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
   * Saves the given transcript. The transcript can be partial e.g. include only some of the layers
   * that the stored version of the transcript contains.
   * @param transcript The transcript to save.
   * @return true if changes were saved, false if there were no changes to save.
   * @throws StoreException If an error prevents the transcript from being saved.
   * @throws PermissionException If saving the transcript is not permitted.
   * @throws GraphNotFoundException If the transcript doesn't exist.
   */
  public boolean saveTranscript(Graph transcript)
    throws StoreException, PermissionException, GraphNotFoundException {
    try {
      URL url = editUrl("saveTranscript");
      HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("saveTranscript -> " + request);
      
      JSONSerialization s = new JSONSerialization();
      s.configure(s.configure(new ParameterSet(), transcript.getSchema()), transcript.getSchema());
      final Vector<SerializationException> exceptions = new Vector<SerializationException>();
      final Vector<NamedStream> streams = new Vector<NamedStream>();
      s.serialize(Utility.OneGraphSpliterator(transcript), null,
                  (stream) -> streams.add(stream),
                  (warning) -> System.out.println(warning),
                  (exception) -> exceptions.add(exception));
      String json = IO.InputStreamToString​(streams.elementAt(0).getStream());
      response = new Response(request.post(json).getInputStream(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      JsonValue bool = (JsonValue)response.getModel();
      return bool.equals(JsonValue.TRUE);
      
    } catch(SerializerNotConfiguredException exception) { // shouldn't happen
        throw new StoreException("Could not serialize graph.", exception);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
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
   * Identifies a list of annotations that match a particular pattern, and tags them on
   * the given layer with the given label. If the specified layer ID does not allow peers,
   * all existing tags will be deleted. Otherwise, tagging does not affect any existing tags on
   * the matching annotations.
   * @param expression An expression that determines which annotations match.
   * <p> The expression language is loosely based on JavaScript; expressions such as the
   * following can be used: 
   * <ul>
   *  <li><code>layer.id == 'orthography' &amp;&amp; label == 'the'</code></li>
   *  <li><code>first('language').label == 'en' &amp;&amp; layer.id == 'orthography'
   *       &amp;&amp; label == 'word'</code></li> 
   * </ul>
   * <p><em>NB</em> all expressions must match by either id or layer.id.
   * @param layerId The layer ID of the resulting annotation.
   * @param label The label of the resulting annotation.
   * @param confidence The confidence rating.
   * @return The number of new annotations added.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public int tagMatchingAnnotations(
    String expression, String layerId, String label, Integer confidence)
    throws StoreException, PermissionException {
    
    try {
      URL url = editUrl("tagMatchingAnnotations");
      HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setParameter("expression", expression)
        .setParameter("layerId", layerId)
        .setParameter("label", label)
        .setParameter("confidence", confidence);
      if (verbose) System.out.println("tagMatchingAnnotations -> " + request);
      response = new Response(request.post().getInputStream(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return 0;
      return (Integer)response.getModel();
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
   * Saves a participant, and all its tags, to the database.  The participant is
   * represented by an Annotation that isn't assumed to be part of a transcript.
   * @param participant An annotation representing the participant, with
   * {@link Annotation#addAnnotation(Annotation)} called for each participant attribute
   * value to set. 
   * <p> The pass phrase for participant access can also be set by specifying a
   * an annotation on the "_password" pseudo layer, with the .
   * @return true if changes were saved, false if there were no changes to save.
   * @throws StoreException If an error prevents the participant from being saved.
   * @throws PermissionException If saving the participant is not permitted.
   */
  public boolean saveParticipant(Annotation participant)
    throws StoreException, PermissionException {
      
    try {
      URL url = editUrl("saveParticipant");
      HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setParameter("id", participant.getId())
        .setParameter("label", participant.getLabel());
      // add participant attributes to request
      for (String layerId : participant.getAnnotations().keySet()) {
        for (Annotation attribute : participant.getAnnotations().get(layerId)) {
          request.setParameter(layerId, attribute.getLabel());
        } // next child
      } // next child layer      
      if (verbose) System.out.println("saveParticipant -> " + request);
      response = new Response(request.post().getInputStream(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      JsonValue bool = (JsonValue)response.getModel();
      return bool.equals(JsonValue.TRUE);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * Saves the given media for the given transcript.
   * @param id The transcript ID.
   * @param mediaUrl A URL to the media content.
   * @param trackSuffix The track suffix of the media
   *  - see <a href="https://nzilbb.github.io/ag/apidocs/nzilbb/ag/MediaTrackDefinition.html#suffix">MediaTrackDefinition.suffix</a>}.
   * @throws StoreException If an error prevents the media from being saved.
   * @throws PermissionException If saving the media is not permitted.
   * @throws GraphNotFoundException If the transcript doesn't exist.
   */
  public MediaFile saveMedia(String id, String mediaUrl, String trackSuffix)
    throws StoreException, PermissionException, GraphNotFoundException {
    
    cancelling = false;
    URL url = editUrl("saveMedia");
    try {
      postRequest = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setParameter("id", id)
        .setParameter("trackSuffix", trackSuffix);
      File media = null;
      boolean deleteMediaAfterUpload = false;
      if (mediaUrl.startsWith("file:")) {
        try {
          media = new File(new URI(mediaUrl));
        } catch (URISyntaxException x) {
          throw new StoreException(x);
        }
      } else { // not file URL, but we need one, so download content to a temporary file
        URL downloadUrl = new URL(mediaUrl);
        media = File.createTempFile("saveMedia-", "."+IO.Extension(downloadUrl.getPath()));
        IO.SaveUrlToFile​(downloadUrl, media);
        media.delete();
        media.deleteOnExit();
        deleteMediaAfterUpload = false;
      }
      try {
        postRequest.setParameter("media", media);
        
        if (verbose) System.out.println("saveMedia -> " + postRequest);
        response = new Response(postRequest.post(), verbose);
        response.checkForErrors(); // throws a ResponseException on error

        return new MediaFile((JsonObject)response.getModel());
      } finally {
        if (deleteMediaAfterUpload) media.delete();
      }
    } catch (IOException x) {
      throw new StoreException(x);
    }
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
   * Saves the given document for the episode of the given transcript.
   * @param id The transcript ID
   * @param url A URL to the document.
   * @throws StoreException If an error prevents the media from being saved.
   * @throws PermissionException If saving the media is not permitted.
   * @throws GraphNotFoundException If the transcript doesn't exist.
   */
  public MediaFile saveEpisodeDocument(String id, String url)
    throws StoreException, PermissionException, GraphNotFoundException {
      
    cancelling = false;
    URL requestUrl = editUrl("saveEpisodeDocument");
    try {
      postRequest = new HttpRequestPostMultipart(requestUrl, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setParameter("id", id);
      File media = null;
      boolean deleteMediaAfterUpload = false;
      if (url.startsWith("file:")) {
        try {
          media = new File(new URI(url));
        } catch (URISyntaxException x) {
          throw new StoreException(x);
        }
      } else { // not file URL, but we need one, so download content to a temporary file
        URL downloadUrl = new URL(url);
        media = File.createTempFile(
          "saveEpisodeDocument-", "."+IO.Extension(downloadUrl.getPath()));
        IO.SaveUrlToFile​(downloadUrl, media);
        media.delete();
        media.deleteOnExit();
        deleteMediaAfterUpload = false;
      }
      try {
        postRequest.setParameter("document", media);
        
        if (verbose) System.out.println("saveEpisodeDocument -> " + postRequest);
        response = new Response(postRequest.post(), verbose);
        response.checkForErrors(); // throws a ResponseException on error
        
        return new MediaFile((JsonObject)response.getModel());
      } finally {
        if (deleteMediaAfterUpload) media.delete();
      }
    } catch (IOException x) {
      throw new StoreException(x);
    }
  }

  /**
   * Delete a given media or episode document file.
   * @param id The associated transcript ID.
   * @param fileName The media/document file name, e.g. {@link MediaFile#name}.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript doesn't exist.
   */
  public void deleteMedia(String id, String fileName)
    throws StoreException, PermissionException, GraphNotFoundException {
    
    try {
      URL url = editUrl("deleteMedia");
      HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setParameter("id", id)
        .setParameter("fileName", fileName);
      if (verbose) System.out.println("deleteTranscript -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
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
   * Upload a transcript file as the first stage in adding or
   * modifying a transcript to LaBB-CAT. The second stage is
   * {@link #transcriptUploadParameters(Upload)}
   * @param transcript The transcript to upload.
   * @param merge Whether the upload corresponds to updates to an existing transcript
   * (true) or a new transcript (false).
   * @return The ID and parameters required to complete the upload by calling
   * {@link #transcriptUploadParameters(Upload)}
   * <p> These may include both information
   * required by the format deserializer (e.g. mappings from tiers to LaBB-CAT layers) 
   * and also general information required by LaBB-CAT, such as:
   *  <dl>
   *   <dt> labbcat_corpus </dt>
   *       <dd> The corpus the new transcript(s) belong(s) to. </dd> 
   *   <dt> labbcat_episode </dt>
   *       <dd> The episode the new transcript(s) belong(s) to. </dd> 
   *   <dt> labbcat_transcript_type </dt>
   *       <dd> The transcript type for the new transcript(s). </dd> 
   *   <dt> labbcat_generate </dt>
   *       <dd> Whether to re-regenerate layers of automated annotations or not. </dd> 
   *  </dl> 
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public Upload transcriptUpload(File transcript, boolean merge)
    throws IOException, StoreException {
    return transcriptUpload(transcript, (TreeMap<String,File[]>)null, merge);
  }
  
  /**
   * Upload a transcript file and associated media files, as the first stage in adding or
   * modifying a transcript to LaBB-CAT. The second stage is
   * {@link #transcriptUploadParameters(Upload)}
   * @param transcript The transcript to upload.
   * @param media The media to upload, if any. These will be uploaded with the default
   * track suffix: "".
   * @param merge Whether the upload corresponds to updates to an existing transcript
   * (true) or a new transcript (false).
   * @return The ID and parameters required to complete the upload by calling
   * {@link #transcriptUploadParameters(Upload)}
   * <p> These may include both information
   * required by the format deserializer (e.g. mappings from tiers to LaBB-CAT layers) 
   * and also general information required by LaBB-CAT, such as:
   *  <dl>
   *   <dt> labbcat_corpus </dt>
   *       <dd> The corpus the new transcript(s) belong(s) to. </dd> 
   *   <dt> labbcat_episode </dt>
   *       <dd> The episode the new transcript(s) belong(s) to. </dd> 
   *   <dt> labbcat_transcript_type </dt>
   *       <dd> The transcript type for the new transcript(s). </dd> 
   *   <dt> labbcat_generate </dt>
   *       <dd> Whether to re-regenerate layers of automated annotations or not. </dd> 
   *  </dl> 
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public Upload transcriptUpload(File transcript, File[] media, boolean merge)
    throws IOException, StoreException {
    return transcriptUpload(transcript, new TreeMap<String,File[]>(){{ put("", media); }}, merge);
  }
  
  /**
   * Upload a transcript file and associated media files, as the first stage in adding or
   * modifying a transcript to LaBB-CAT. The second stage is
   * {@link #transcriptUploadParameters(Upload)}
   * @param transcript The transcript to upload.
   * @param media The media to upload, if any; a map of 
   * track suffixes ({@link nzilbb.labbcat.model.MediaTrack#suffix}) to media files to upload 
   * for that track.
   * @param merge Whether the upload corresponds to updates to an existing transcript
   * (true) or a new transcript (false).
   * @return The ID and {@link nzilbb.labbcat.model.Upload#parameters} required to 
   * complete the {@link #transcriptUploadParameters(Upload)}.
   * <p> These may include both information
   * required by the format deserializer (e.g. mappings from tiers to LaBB-CAT layers) 
   * and also general information required by LaBB-CAT, such as:
   *  <dl>
   *   <dt> labbcat_corpus </dt>
   *       <dd> The corpus the new transcript(s) belong(s) to. </dd> 
   *   <dt> labbcat_episode </dt>
   *       <dd> The episode the new transcript(s) belong(s) to. </dd> 
   *   <dt> labbcat_transcript_type </dt>
   *       <dd> The transcript type for the new transcript(s). </dd> 
   *   <dt> labbcat_generate </dt>
   *       <dd> Whether to re-regenerate layers of automated annotations or not. </dd> 
   *  </dl> 
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public Upload transcriptUpload(File transcript, Map<String,File[]> media, boolean merge)
    throws IOException, StoreException {
      
    cancelling = false;
    URL url = makeUrl("api/edit/transcript/upload");
    postRequest = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "application/json")
      .setParameter("transcript", transcript);
    if (merge) postRequest.setParameter("merge", true);
    if (media != null) {
      for (String trackSuffix : media.keySet()) {
        for (File file : media.get(trackSuffix)) {
          postRequest.setParameter("media"+trackSuffix, file);
        } // next media file
      } // next track suffix
    } // media is set
    if (verbose) System.out.println("transcriptUpload -> " + postRequest);
    response = new Response(postRequest.post(), verbose);
    response.checkForErrors(); // throws a ResponseException on error
    
    return new Upload((JsonObject)response.getModel());
  } // end of transcriptUpload()
  
  /**
   * The second part of a transcript upload process started by a call to
   * {@link #transcriptUpload(File,Map,boolean)}, which specifies values for the parameters
   * required to save the uploaded transcript to LaBB-CAT's database. 
   * <p> If the response includes more parameters, then this method should be called again
   * to supply their values.
   * @param upload Response from {@link #transcriptUpload(File,Map,boolean)} with
   * parameter values filled in as required.
   * @return The ID and a transcript-ID to thread-ID map ({@link Upload#transcripts}) for
   * transcripts that are being finalized (and any further parameters required to complete
   * the upload). 
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public Upload transcriptUploadParameters(Upload upload) throws IOException, StoreException {
    try {
      URL url = makeUrl("api/edit/transcript/upload/"+URLEncoder.encode(upload.getId(), "UTF-8"));
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setMethod("PUT");
      for (Parameter parameter : upload.getParameters().values()) {
        if (parameter.getValue() != null) {
          request.setParameter(parameter.getName(), parameter.getValue());
        }
      } // next parameter
      if (verbose) System.out.println("transcriptUploadParameters -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a ResponseException on error
      
      return new Upload((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of transcriptUploadParameters()

  /**
   * Delete an upload made by {@link #transcriptUpload(File,Map,boolean)}.
   * @param upload Response from {@link #transcriptUpload(File,Map,boolean)}.
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public void transcriptUploadDelete(Upload upload) throws IOException, StoreException {
    transcriptUploadDelete(upload.getId());
  } // end of transcriptUploadDelete()
  
  /**
   * Delete an upload made by {@link #transcriptUpload(File,Map,boolean)}.
   * @param id The ID of the upload as returned by {@link #transcriptUpload(File,Map,boolean)}.
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public void transcriptUploadDelete(String id) throws IOException, StoreException {
    try {
      URL url = makeUrl("api/edit/transcript/upload/"+URLEncoder.encode(id, "UTF-8"));
      HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setMethod("DELETE");
      if (verbose) System.out.println("transcriptUploadDelete -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a ResponseException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of transcriptUploadDelete()

  /**
   * Upload a new transcript.
   * @param transcript The transcript to upload.
   * @param media The media to upload, if any.
   * @param trackSuffix The track suffix for the media, which can be null.
   * @param transcriptType The transcript type.
   * @param corpus The corpus for the transcript.
   * @param episode The episode the transcript belongs to.
   * @return The taskId of the server task processing the upload. 
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public String newTranscript(File transcript, File[] media, String trackSuffix, String transcriptType, String corpus, String episode)
    throws IOException, StoreException {

    try { // from 20250324 onwards, use api/edit/transcript/upload/* endpoints
      // first upload file(s)
      File[] finalMedia = Optional.ofNullable(media).orElse(new File[0]);
      String finalTrackSuffix = Optional.ofNullable(trackSuffix).orElse("");
      Upload upload = transcriptUpload(
        transcript,
        new TreeMap<String,File[]>() {{ put(finalTrackSuffix, finalMedia); }},
        false); // merge=false when transcript doesn't already exist
      
      // set the upload parameters
      if (upload.getParameters().containsKey("labbcat_transcript_type")) {
        upload.getParameters().get("labbcat_transcript_type").setValue(transcriptType);
      }
      if (upload.getParameters().containsKey("labbcat_corpus")) {
        upload.getParameters().get("labbcat_corpus").setValue(corpus);
      }
      if (upload.getParameters().containsKey("labbcat_episode")) {
        upload.getParameters().get("labbcat_episode").setValue(episode);
      }

      // send the upload parameters (with whatever their default values were)
      upload = transcriptUploadParameters(upload);
      if (upload.getTranscripts() == null || upload.getTranscripts().size() == 0) {
        return null; // no thread ID to return
      } else if (upload.getTranscripts().containsKey(transcript.getName())) {
        // there is a thread named after the file
        return upload.getTranscripts().get(transcript.getName());
      } else { // no thread is named after the file, but there's at least one thread
        // return the first thread
        return upload.getTranscripts().values().iterator().next();
      }
      
    } catch (ResponseException x) { // prior versions return 404 for api/edit/transcript/upload/*
      if (x.getResponse().getHttpStatus() == 404) {
        // use older api/edit/transcript/new endpoint
        
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
          if (trackSuffix == null) trackSuffix = "";
          for (int f = 0; f < media.length; f++) {
            postRequest.setParameter("uploadmedia"+trackSuffix+"1", media[f]);
          } // next file
        }
        if (verbose) System.out.println("newTranscript -> " + postRequest);
        response = new Response(postRequest.post(), verbose);
        response.checkForErrors(); // throws a ResponseException on error
        
        // extract the threadId from model.result.id
        JsonObject model = (JsonObject)response.getModel();
        JsonObject result = model.getJsonObject("result");
        return result.getString(transcript.getName());
      } else { // not 404, some other problem, so just throw the exception
        throw x;
      }
    }
  } // end of newTranscript()

  /**
   * Uploads a new version of an existing transcript.
   * @param transcript The transcript file to upload.
   * @return The taskId of the server task processing the upload. 
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public String updateTranscript(File transcript)
    throws IOException, StoreException {
    return updateTranscript(transcript, true);
  }
  
  /**
   * Uploads a new version of an existing transcript.
   * @param transcript The transcript to upload.
   * @param generate Whether to regenerate automatic annotation layers or not.
   * @return The taskId of the server task processing the upload. 
   * @throws IOException If a communications error occurs.
   * @throws StoreException If the server returns an error.
   */
  public String updateTranscript(File transcript, boolean generate)
    throws IOException, StoreException {
      
    try { // from 20250324 onwards, use api/edit/transcript/upload/* endpoints
      // first upload file(s)
      Upload upload = transcriptUpload(
        transcript, true); // merge=true when transcript already exists
      
      // set the upload parameters
      if (upload.getParameters().containsKey("labbcat_generate")) {
        upload.getParameters().get("labbcat_generate").setValue(generate);
      }

      // send the upload parameters (with whatever their default values were)
      upload = transcriptUploadParameters(upload);
      if (upload.getTranscripts() == null || upload.getTranscripts().size() == 0) {
        return null; // no thread ID to return
      } else if (upload.getTranscripts().containsKey(transcript.getName())) {
        // there is a thread named after the file
        return upload.getTranscripts().get(transcript.getName());
      } else { // no thread is named after the file, but there's at least one thread
        // return the first thread
        return upload.getTranscripts().values().iterator().next();
      }
      
    } catch (ResponseException x) { // prior versions return 404 for api/edit/transcript/upload/*
      if (x.getResponse().getHttpStatus() == 404) {
        // use older api/edit/transcript/new endpoint

        cancelling = false;
        URL url = makeUrl("edit/transcript/new");
        postRequest = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
          .setUserAgent()
          .setHeader("Accept", "application/json")
          .setParameter("todo", "update")
          .setParameter("auto", true)
          .setParameter("uploadfile1_0", transcript);
        if (verbose) System.out.println("updateTranscript -> " + postRequest);
        response = new Response(postRequest.post(), verbose);
        response.checkForErrors(); // throws a ResponseException on error
        
        // extract the threadId from model.result.id
        JsonObject model = (JsonObject)response.getModel();
        JsonObject result = model.getJsonObject("result");
        return result.getString(transcript.getName());
      } else { // not 404, some other problem, so just throw the exception
        throw x;
      }
    }
  } // end of updateTranscript()
  
  /**
   * Deletes all annotations that match a particular pattern
   * @param expression An expression that determines which annotations match.
   * <p> The expression language is loosely based on JavaScript; expressions such as the
   * following can be used: 
   * <ul>
   *  <li><code>layer.id == 'pronunciation' 
   *       &amp;&amp; first('orthography').label == 'the'</code></li>
   *  <li><code>first('language').label == 'en' &amp;&amp; layer.id == 'pronunciation' 
   *       &amp;&amp; first('orthography').label == 'the'</code></li> 
   * </ul>
   * <p><em>NB</em> all expressions must match by either id or layer.id.
   * @return The number of new annotations deleted.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public int deleteMatchingAnnotations(String expression)
    throws StoreException, PermissionException {
    try {
      URL url = editUrl("deleteMatchingAnnotations");
      HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setParameter("expression", expression);
      if (verbose) System.out.println("deleteMatchingAnnotations -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return 0;
      return (Integer)response.getModel();
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
  
  /**
   * Adds an entry to a layer dictionary.
   * <p> This function adds a new entry to the dictionary that manages a given layer, and
   * updates all affected tokens in the corpus. Words can have multiple entries.
   * @param layerId The ID of the layer with a dictionary configured to manage it.
   * @param key The key (word) in the dictionary to add an entry for.
   * @param entry The value (definition) for the given key.
   * @throws StoreException If the server returns an error.
   */
  public void addLayerDictionaryEntry(String layerId, String key, String entry)
    throws StoreException {
    try {
      HttpRequestPost request = post("api/edit/dictionary/add")
        .setHeader("Accept", "application/json")
        .setParameter("layerId", layerId)
        .setParameter("key", key)
        .setParameter("entry", entry);
      if (verbose) System.out.println("addLayerDictionaryEntry -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of addLayerDictionaryEntry()  
  
  /**
   * Removes an entry from a layer dictionary.
   * <p> This function removes an existing entry from the dictionary that manages a given
   * layer, and updates all affected tokens in the corpus. Words can have multiple entries.
   * @param layerId The ID of the layer with a dictionary configured to manage it.
   * @param key The key (word) in the dictionary to remove an entry for.
   * @param entry The value (definition) to remove, or null to remove all the entries for
   * <var>key</var>. 
   * @throws StoreException If the server returns an error.
   */
  public void removeLayerDictionaryEntry(String layerId, String key, String entry)
    throws StoreException {
    try {
      HttpRequestPost request = post("api/edit/dictionary/remove")
        .setHeader("Accept", "application/json")
        .setParameter("layerId", layerId)
        .setParameter("key", key);
      if (entry != null) request.setParameter("entry", entry);
      if (verbose) System.out.println("addLayerDictionaryEntry -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of removeLayerDictionaryEntry()
  
  /**
   * Adds an entry to a dictionary.
   * <p> This function adds a new entry to the given dictionary. Words can have multiple entries.
   * @param managerId The layer manager ID of the dictionary, as returned by
   * {@link LabbcatView#getDictionaries()}
   * @param dictionaryId The ID of the dictionary, as returned by
   * {@link LabbcatView#getDictionaries()}
   * @param key The key (word) in the dictionary to add an entry for.
   * @param entry The value (definition) for the given key.
   * @throws StoreException If the server returns an error.
   */
  public void addDictionaryEntry(String managerId, String dictionaryId, String key, String entry)
    throws StoreException {
    try {
      HttpRequestPost request = post("api/edit/dictionary/add")
        .setHeader("Accept", "application/json")
        .setParameter("layerManagerId", managerId)
        .setParameter("dictionaryId", dictionaryId)
        .setParameter("key", key)
        .setParameter("entry", entry);
      if (verbose) System.out.println("addLayerDictionaryEntry -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of addDictionaryEntry()
  
  /**
   * Removes an entry from a dictionary.
   * <p> This function removes an existing entry from the given dictionary. Words can have
   * multiple entries.
   * @param managerId The layer manager ID of the dictionary, as returned by
   * {@link LabbcatView#getDictionaries()}
   * @param dictionaryId The ID of the dictionary, as returned by
   * {@link LabbcatView#getDictionaries()}
   * @param key The key (word) in the dictionary to remove an entry for.
   * @param entry The value (definition) to remove, or None to remove all the entries for key.
   * @throws StoreException If the server returns an error.
   */
  public void removeDictionaryEntry(
    String managerId, String dictionaryId, String key, String entry) throws StoreException {
    try {
      HttpRequestPost request = post("api/edit/dictionary/remove")
        .setHeader("Accept", "application/json")
        .setParameter("layerManagerId", managerId)
        .setParameter("dictionaryId", dictionaryId)
        .setParameter("key", key);
      if (entry != null) request.setParameter("entry", entry);
      if (verbose) System.out.println("addLayerDictionaryEntry -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of removeDictionaryEntry()
  
  /**
   * Retrieve an annotator's given "ext" resource.
   * <p> Retrieve a given resource from an annotator's "ext" web app. Annotators are modules
   * that perform different annotation tasks, and can optionally implement functionality for
   * providing extra data or extending functionality in an annotator-specific way. If the
   * annotator implements an "ext" web app, it can provide resources and implement a
   * mechanism for iterrogating the annotator. This function provides a mechanism for
   * accessing these resources via python.
   * <p> Details about the resources available for a given annotator are available by
   * calling {@link #getAnnotatorDescriptor(String)} and checking "hasExtWebapp" attribute
   * to ensure an 'ext' webapp is implemented, and checking details the "extApiInfo" attribute.
   * @param annotatorId ID of the annotator to interrogate.
   * @param resource The name of the file to retrieve or instance method (function) to
   * invoke. Possible values for this depend on the specific annotator being interrogated.
   * @return The resource requested.
   * @throws StoreException If the server returns an error.
   */
  public String annotatorExt(String annotatorId, String resource)
    throws StoreException {
    return annotatorExt(annotatorId, resource, null);
  }
  /**
   * Retrieve an annotator's given "ext" resource.
   * <p> Retrieve a given resource from an annotator's "ext" web app. Annotators are modules
   * that perform different annotation tasks, and can optionally implement functionality for
   * providing extra data or extending functionality in an annotator-specific way. If the
   * annotator implements an "ext" web app, it can provide resources and implement a
   * mechanism for iterrogating the annotator. This function provides a mechanism for
   * accessing these resources via python.
   * <p> Details about the resources available for a given annotator are available by
   * calling {@link #getAnnotatorDescriptor(String)} and checking "hasExtWebapp" attribute
   * to ensure an 'ext' webapp is implemented, and checking details the "extApiInfo" attribute.
   * @param annotatorId ID of the annotator to interrogate.
   * @param resource The name of the file to retrieve or instance method (function) to
   * invoke. Possible values for this depend on the specific annotator being interrogated.
   * @param parameters Optional list of ordered parameters for the instance method (function).
   * @return The resource requested.
   * @throws StoreException If the server returns an error.
   */
  public String annotatorExt(String annotatorId, String resource, String[] parameters)
    throws StoreException {
    try {
      String queryString = "";
      if (parameters != null && parameters.length > 0) {
        queryString = "?" + Arrays.stream(parameters)
          .map(p->{
              try {
                return URLEncoder.encode(p, "UTF-8");
              } catch(UnsupportedEncodingException exception) { return ""; }
            })
          .collect(Collectors.joining(","));
      }
      HttpRequestGet request = get(
        "edit/annotator/ext/"
        +URLEncoder.encode(annotatorId, "UTF-8")
        +"/"+URLEncoder.encode(resource, "UTF-8")
        +queryString)
        .setHeader("Accept", "text/plain");
      if (verbose) System.out.println("annotatorExt -> " + request);
      HttpURLConnection connection = request.get();
      int httpStatus = connection.getResponseCode();
      if (verbose) System.out.println("HTTP status: " + connection.getResponseCode());
      if (httpStatus != HttpURLConnection.HTTP_OK) {
        throw new StoreException(IO.InputStreamToString(connection.getErrorStream()));
      }
      return IO.InputStreamToString(connection.getInputStream());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of annotatorExt()

} // end of class LabbcatEdit
