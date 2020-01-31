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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Vector;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import nzilbb.ag.Anchor;
import nzilbb.ag.Annotation;
import nzilbb.ag.Graph;
import nzilbb.ag.GraphNotFoundException;
import nzilbb.ag.IGraphStore;
import nzilbb.ag.Layer;
import nzilbb.ag.MediaFile;
import nzilbb.ag.MediaTrackDefinition;
import nzilbb.ag.PermissionException;
import nzilbb.ag.Schema;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.http.*;
import nzilbb.util.MonitorableSeries;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Client-side implementation of IGraphStore.
 * <p> e.g.
 * <pre> // create annotation store client
 * GraphStoreQuery store = new GraphStore("https://labbcat.canterbury.ac.nz", "demo", "demo");
 * // get some basic information
 * String id = store.getId();
 * String[] layers = store.getLayerIds();
 * String[] corpora = store.getCorpusIds();
 * String[] documents = store.getGraphIdsInCorpus(corpora[0]);
 * // delete a document
 * store.deleteGraph(documents[0]);
 * </pre>
 * @author Robert Fromont robert@fromont.net.nz
 */

public class GraphStore
   extends GraphStoreQuery
   implements IGraphStore
{
   // Attributes:
  
   // Methods:
   
   /**
    * Default constructor.
    */
   public GraphStore()
   {
   } // end of constructor
   
   /**
    * Constructor from string URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public GraphStore(String labbcatUrl)
      throws MalformedURLException
   {
      super(labbcatUrl);
   } // end of constructor
   
   /**
    * Constructor with String attributes.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    * @param username LaBB-CAT username.
    * @param password LaBB-CAT password.
    */
   public GraphStore(String labbcatUrl, String username, String password)
      throws MalformedURLException
   {
      super(labbcatUrl, username, password);
   } // end of constructor
   
   /**
    * Constructor from URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public GraphStore(URL labbcatUrl)
   {
      super(labbcatUrl);
   } // end of constructor
   
   /**
    * Constructor with attributes.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    * @param username LaBB-CAT username.
    * @param password LaBB-CAT password.
    */
   public GraphStore(URL labbcatUrl, String username, String password)
   {
      super(labbcatUrl, username, password);
   } // end of constructor

   /**
    * Constructs a URL for the given resource.
    * @param resource
    * @return A URL for the given resource.
    * @throws StoreException If the URL is malformed.
    */
   public URL editUrl(String resource)
      throws StoreException
   {
      try
      {
         return new URL(new URL(labbcatUrl, "edit/store/"), resource);
      }
      catch(Throwable t)
      {
         throw new StoreException("Could not construct request URL.", t);
      }
   } // end of editUrl()   

   // IGraphStore methods:
   
   /**
    * Saves the given graph. The graph can be partial e.g. include only some of the layers
    * that the stored version of the graph contains.
    * <p>The graph deltas are assumed to be set correctly, so if this is a new graph, then
    * {@link Graph#getChange()} should return Change.Operation.Create, if it's an update,
    * Change.Operation.Update, and to delete, Change.Operation.Delete.  Correspondingly,
    * all {@link Anchor}s and {@link Annotation}s should have their changes set also.  If
    * {@link Graph#getChanges()} returns no changes, no action will be taken, and this
    * method returns false.
    * <p>After this method has executed, {@link Graph#commit()} is <em>not</em> called -
    * this must be done by the caller, if they want changes to be committed.
    * @param graph The graph to save.
    * @return true if changes were saved, false if there were no changes to save.
    * @throws StoreException If an error prevents the graph from being saved.
    * @throws PermissionException If saving the graph is not permitted.
    * @throws GraphNotFoundException If the graph doesn't exist.
    */
   public boolean saveGraph(Graph graph)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Creates an annotation starting at <var>from</var> and ending at <var>to</var>.
    * @param id The ID of the graph.
    * @param fromId The start anchor's ID. TODO an expression identifying the start
    * anchor's ID. e.g. "'n_123'" or "start.id" or maybe something like
    * "first('segments').start.id)"
    * @param toId The end anchor's ID. TODO an expression identifying the end anchor's
    * ID. e.g. "'n_123'" or "end.id" or maybe something like "last('segments').end.id)"
    * @param layerId The layer ID of the resulting annotation.
    * @param label The label of the resulting annotation. TODO an expression identifying
    * the label. e.g. "'@gz#mpP'" or "my('orthography').label" or maybe something like
    * "SUM(list('segments').duration)"
    * @param confidence The confidence rating.
    * @param parentId The new annotation's parent's ID. TODO an expression identifying the
    * parent. e.g. "'em_0_123'" or "layer.id = 'orthography' AND label = 'example'"
    * @return The ID of the new annotation.
    */
   public String createAnnotation(String id, String fromId, String toId, String layerId, String label, Integer confidence, String parentId)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = editUrl("createAnnotation");
         HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json")
            .setParameter("id", id)
            .setParameter("fromId", fromId)
            .setParameter("toId", toId)
            .setParameter("layerId", layerId)
            .setParameter("label", label)
            .setParameter("confidence", confidence)
            .setParameter("parentId", parentId);
         if (verbose) System.out.println("createAnnotation -> " + request);
         Response response = new Response(request.post().getInputStream(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         return (String)response.getModel();
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }

   /**
    * Destroys the annotation with the given ID.
    * @param id The ID of the graph.
    * @param annotationId The annotation's ID.
    */
   public void destroyAnnotation(String id, String annotationId)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = editUrl("destroyAnnotation");
         HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json")
            .setParameter("id", id)
            .setParameter("annotationId", annotationId);
         if (verbose) System.out.println("destroyAnnotation -> " + request);
         Response response = new Response(request.post().getInputStream(), verbose);
         response.checkForErrors(); // throws a StoreException on error
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }
   
   /**
    * Saves a participant, and all its tags, to the database.  The participant is
    * represented by an Annotation that isn't assumed to be part of a graph.
    * @param participant
    * @return true if changes were saved, false if there were no changes to save.
    * @throws StoreException If an error prevents the participant from being saved.
    * @throws PermissionException If saving the participant is not permitted.
    */
   public boolean saveParticipant(Annotation participant)
      throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Saves the given media for the given graph
    * @param id The graph ID
    * @param trackSuffix The track suffix of the media - see {@link MediaTrackDefinition#suffix}.
    * @param mediaUrl A URL to the media content.
    * @throws StoreException If an error prevents the media from being saved.
    * @throws PermissionException If saving the media is not permitted.
    * @throws GraphNotFoundException If the graph doesn't exist.
    */
   public void saveMedia(String id, String trackSuffix, String mediaUrl)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Saves the given source file (transcript) for the given graph.
    * @param id The graph ID
    * @param url A URL to the transcript.
    * @throws StoreException If an error prevents the media from being saved.
    * @throws PermissionException If saving the media is not permitted.
    * @throws GraphNotFoundException If the graph doesn't exist.
    */
   public void saveSource(String id, String url)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Saves the given document for the episode of the given graph.
    * @param id The graph ID
    * @param url A URL to the document.
    * @throws StoreException If an error prevents the media from being saved.
    * @throws PermissionException If saving the media is not permitted.
    * @throws GraphNotFoundException If the graph doesn't exist.
    */
   public void saveEpisodeDocument(String id, String url)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Deletes the given graph, and all associated files.
    * @param id The ID graph to save.
    * @throws StoreException If an error prevents the graph from being saved.
    * @throws PermissionException If saving the graph is not permitted.
    * @throws GraphNotFoundException If the graph doesn't exist.
    */
   public void deleteGraph(String id)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = editUrl("deleteGraph");
         HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json")
            .setParameter("id", id);
         if (verbose) System.out.println("deleteGraph -> " + request);
         Response response = new Response(request.post(), verbose);
         response.checkForErrors(); // throws a StoreException on error
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }

} // end of class GraphStore
