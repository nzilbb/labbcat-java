//
// Copyright 2020-2022 New Zealand Institute of Language, Brain and Behaviour, 
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import nzilbb.ag.GraphStoreAdministration;
import nzilbb.ag.Layer;
import nzilbb.ag.PermissionException;
import nzilbb.ag.StoreException;
import nzilbb.ag.serialize.GraphDeserializer;
import nzilbb.ag.serialize.GraphSerializer;
import nzilbb.ag.serialize.SerializationDescriptor;
import nzilbb.labbcat.http.*;
import nzilbb.labbcat.model.Corpus;
import nzilbb.labbcat.model.MediaTrack;
import nzilbb.labbcat.model.Project;
import nzilbb.labbcat.model.Role;
import nzilbb.labbcat.model.RolePermission;
import nzilbb.labbcat.model.SystemAttribute;
import nzilbb.labbcat.model.User;
import nzilbb.util.IO;

/**
 * Client-side implementation of 
 * <a href="https://nzilbb.github.io/ag/apidocs/nzilbb/ag/GraphStoreAdministration.html">nzilbb.ag.GraphStoreAdminitration</a>.
 * <p>This class inherits the <em>read-write</em> operations of {@link LabbcatEdit}
 * and adds some administration operations, including definition of layers,
 * registration of converters, etc., i.e. those that can be performed by users with
 * <q>admin</q> permission. 
 * <p> e.g.
 * <pre> // create annotation store client
 * LabbcatAdmin store = new LabbcatAdmin("https://labbcat.canterbury.ac.nz", "demo", "demo");
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

public class LabbcatAdmin extends LabbcatEdit implements GraphStoreAdministration {
   
  // Attributes:
  
  // Methods:
   
  /**
   * Default constructor.
   */
  public LabbcatAdmin() {
  } // end of constructor
   
  /**
   * Constructor from string URL.
   * @param labbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   */
  public LabbcatAdmin(String labbcatUrl) throws MalformedURLException {
    super(labbcatUrl);
  } // end of constructor
   
  /**
   * Constructor with String attributes.
   * @param labbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   * @param username LaBB-CAT username.
   * @param password LaBB-CAT password.
   */
  public LabbcatAdmin(String labbcatUrl, String username, String password)
    throws MalformedURLException {
    super(labbcatUrl, username, password);
  } // end of constructor
   
  /**
   * Constructor from URL.
   * @param labbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   */
  public LabbcatAdmin(URL labbcatUrl) {
    super(labbcatUrl);
  } // end of constructor
   
  /**
   * Constructor with attributes.
   * @param labbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   * @param username LaBB-CAT username.
   * @param password LaBB-CAT password.
   */
  public LabbcatAdmin(URL labbcatUrl, String username, String password) {
    super(labbcatUrl, username, password);
  } // end of constructor

  /**
   * Constructs a store URL for the given resource.
   * @param resource
   * @return A URL for the given resource.
   * @throws StoreException If the URL is malformed.
   */
  public URL adminUrl(String resource) throws StoreException {
    try {
      return new URL(new URL(labbcatUrl, "api/admin/store/"), resource);
    } catch(Throwable t) {
      throw new StoreException("Could not construct request URL.", t);
    }
  } // end of editUrl()   

  // GraphStoreAdministration methods:
   
  /**
   * <em>NOT YET IMPLEMENTED</em> - Registers a transcript deserializer.
   * @param deserializer The deserializer to register.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public void registerDeserializer(GraphDeserializer deserializer)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - De-registers a transcript deserializer.
   * @param deserializer The deserializer to de-register.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public void deregisterDeserializer(GraphDeserializer deserializer)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Lists the descriptors of all registered deserializers.
   * @return A list of the descriptors of all registered deserializers.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public SerializationDescriptor[] getDeserializerDescriptors()
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }
   
  /**
   * <em>NOT YET IMPLEMENTED</em> - Registers a transcript serializer.
   * @param serializer The serializer to register.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public void registerSerializer(GraphSerializer serializer)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - De-registers a transcript serializer.
   * @param serializer The serializer to de-register.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public void deregisterSerializer(GraphSerializer serializer)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Lists the descriptors of all registered serializers.
   * @return A list of the descriptors of all registered serializers.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public SerializationDescriptor[] getSerializerDescriptors()
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }
   
  /**
   * Adds a new layer.
   * @param layer A new layer definition.
   * @return The resulting layer definition.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public Layer newLayer(Layer layer) throws StoreException, PermissionException {
    try {
      HttpRequestPost request = new HttpRequestPost(
        adminUrl("newLayer"), getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language)
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("newLayer -> " + request + " : " + layer.toJson());
      response = new Response(request.post(layer.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      Layer result = new Layer();
      result.fromJson(response.getModel().toString());
      return result;
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
  
  /**
   * Generates annotations on a given layer for all transcripts in the corpus.
   * @param layerId The ID of the layer to generate annotations for.
   * @return The thread ID of the the running generation task.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public String generateLayer(String layerId) throws StoreException, PermissionException {
    try {
      HttpRequestPost request = post("admin/layers/regenerate")
        .setHeader("Accept", "application/json")
        .setParameter(layerId, layerId)
        .setParameter("sure", "true");
      if (verbose) System.out.println("generateLayer -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      // extract the threadId from model.threadId
      JsonObject model = (JsonObject)response.getModel();
      return model.getString("threadId");
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * Saves changes to a layer.
   * @param layer A modified layer definition.
   * @return The resulting layer definition.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public Layer saveLayer(Layer layer) throws StoreException, PermissionException {
    try {
      HttpRequestPost request = new HttpRequestPost(
        adminUrl("saveLayer"), getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language)
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("saveLayer -> " + request + " : " + layer.toJson());
      response = new Response(request.post(layer.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      Layer result = new Layer();
      result.fromJson(response.getModel().toString());
      return result;
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * Deletes the given layer, and all associated annotations.
   * @param id The ID layer to delete.
   * @throws StoreException If an error prevents the transcript from being saved.
   * @throws PermissionException If saving the transcript is not permitted.
   */
  public void deleteLayer(String id) throws StoreException, PermissionException {
    try {
      URL url = adminUrl("deleteLayer");
      HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setParameter("id", id);
      if (verbose) System.out.println("deleteLayer -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
   
  // Other methods:
   
  /**
   * Creates a new corpus record.
   * @param corpus The corpus details to save.
   * @return The corpus just created.
   * @throws StoreException, PermissionException
   * @see #readCorpora()
   * @see #readCorpora(Integer,Integer)
   * @see #updateCorpus(Corpus)
   * @see #deleteCorpus(Corpus)
   * @see #deleteCorpus(String)
   */
  public Corpus createCorpus(Corpus corpus) throws StoreException, PermissionException {
    try {
      HttpRequestPost request = post("api/admin/corpora")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("createCorpus -> " + request + " : " + corpus.toJson());
      response = new Response(request.post(corpus.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new Corpus((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of createCorpus()

  /**
   * Reads a list of corpus records.
   * @return A list of corpora.
   * @throws StoreException, PermissionException
   * @see #createCorpus(Corpus)
   * @see #readCorpora(Integer,Integer)
   * @see #updateCorpus(Corpus)
   * @see #deleteCorpus(Corpus)
   * @see #deleteCorpus(String)
   */
  public Corpus[] readCorpora() throws StoreException, PermissionException {
    return readCorpora(null, null);
  }
   
  /**
   * Reads a list of corpus records.
   * @param pageNumber The zero-based  page of records to return (if null, all records
   * will be returned). 
   * @param pageLength The length of pages (if null, the default page length is 20).
   * @return A list of corpora.
   * @throws StoreException, PermissionException
   * @see #createCorpus(Corpus)
   * @see #readCorpora()
   * @see #updateCorpus(Corpus)
   * @see #deleteCorpus(Corpus)
   * @see #deleteCorpus(String)
   */
  public Corpus[] readCorpora(Integer pageNumber, Integer pageLength) throws StoreException, PermissionException {
    try {
      HttpRequestGet request = get("api/admin/corpora")
        .setHeader("Accept", "application/json");
      if (pageLength != null) request.setParameter("pageNumber", pageLength);
      if (pageNumber != null) request.setParameter("pageLength", pageNumber);
      if (verbose) System.out.println("readCorpora -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<Corpus> corpora = new Vector<Corpus>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          corpora.add(new Corpus(array.getJsonObject(i)));
        }
      }
      return corpora.toArray(new Corpus[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of readCorpora()
   
  /**
   * Updates an existing corpus record.
   * @param corpus The corpus details to save.
   * @return The corpus just updated.
   * @throws StoreException, PermissionException
   * @see #createCorpus(Corpus)
   * @see #readCorpora()
   * @see #readCorpora(Integer,Integer)
   * @see #deleteCorpus(Corpus)
   * @see #deleteCorpus(String)
   */
  public Corpus updateCorpus(Corpus corpus) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("api/admin/corpora")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("updateCorpus -> " + request);
      response = new Response(request.post(corpus.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new Corpus((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateCorpus()
   
  /**
   * Deletes an existing corpus record.
   * @param corpus The corpus to delete.
   * @throws StoreException, PermissionException
   * @see #createCorpus(Corpus)
   * @see #readCorpora()
   * @see #readCorpora(Integer,Integer)
   * @see #updateCorpus(Corpus)
   * @see #deleteCorpus(String)
   */
  public void deleteCorpus(Corpus corpus) throws StoreException, PermissionException {
    deleteCorpus(corpus.getName());
  }
   
  /**
   * Deletes an existing corpus record.
   * @param name The name/ID of the corpus to delete.
   * @throws StoreException, PermissionException
   * @see #createCorpus(Corpus)
   * @see #readCorpora()
   * @see #readCorpora(Integer,Integer)
   * @see #updateCorpus(Corpus)
   * @see #deleteCorpus(Corpus)
   */
  public void deleteCorpus(String name) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = delete("api/admin/corpora/" + name);
      if (verbose) System.out.println("deleteCorpus -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateCorpus()
   
  /**
   * Creates a new project record.
   * @param project The project details to save.
   * @return The project just created.
   * @throws StoreException, PermissionException
   * @see #readProjects()
   * @see #readProjects(Integer,Integer)
   * @see #updateProject(Project)
   * @see #deleteProject(Project)
   * @see #deleteProject(String)
   */
  public Project createProject(Project project) throws StoreException, PermissionException {
    try {
      HttpRequestPost request = post("api/admin/projects")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("createProject -> " + request);
      response = new Response(request.post(project.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new Project((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of createProject()

  /**
   * Reads a list of project records.
   * @return A list of projects.
   * @throws StoreException, PermissionException
   * @see #createProject(Project)
   * @see #readProjects(Integer,Integer)
   * @see #updateProject(Project)
   * @see #deleteProject(Project)
   * @see #deleteProject(String)
   */
  public Project[] readProjects() throws StoreException, PermissionException {
    return readProjects(null, null);
  }
   
  /**
   * Reads a list of project records.
   * @param pageNumber The zero-based  page of records to return (if null, all records
   * will be returned). 
   * @param pageLength The length of pages (if null, the default page length is 20).
   * @return A list of projects.
   * @throws StoreException, PermissionException
   * @see #createProject(Project)
   * @see #readProjects()
   * @see #updateProject(Project)
   * @see #deleteProject(Project)
   * @see #deleteProject(String)
   */
  public Project[] readProjects(Integer pageNumber, Integer pageLength)
    throws StoreException, PermissionException {
    try {
      HttpRequestGet request = get("api/admin/projects")
        .setHeader("Accept", "application/json");
      if (pageLength != null) request.setParameter("pageNumber", pageLength);
      if (pageNumber != null) request.setParameter("pageLength", pageNumber);
      if (verbose) System.out.println("readProjects -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<Project> projects = new Vector<Project>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          projects.add(new Project(array.getJsonObject(i)));
        }
      }
      return projects.toArray(new Project[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of readProjects()
   
  /**
   * Updates an existing project record.
   * @param project The project details to save.
   * @return The project just updated.
   * @throws StoreException, PermissionException
   * @see #createProject(Project)
   * @see #readProjects()
   * @see #readProjects(Integer,Integer)
   * @see #deleteProject(Project)
   * @see #deleteProject(String)
   */
  public Project updateProject(Project project) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("api/admin/projects")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("updateProject -> " + request);
      response = new Response(request.post(project.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new Project((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateProject()
   
  /**
   * Deletes an existing project record.
   * @param project The project to delete.
   * @throws StoreException, PermissionException
   * @see #createProject(Project)
   * @see #readProjects()
   * @see #readProjects(Integer,Integer)
   * @see #updateProject(Project)
   * @see #deleteProject(String)
   */
  public void deleteProject(Project project) throws StoreException, PermissionException {
    deleteProject(project.getProject());
  }
   
  /**
   * Deletes an existing project record.
   * @param name The name/ID of the project to delete.
   * @throws StoreException, PermissionException
   * @see #createProject(Project)
   * @see #readProjects()
   * @see #readProjects(Integer,Integer)
   * @see #updateProject(Project)
   * @see #deleteProject(Project)
   */
  public void deleteProject(String name) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = delete("api/admin/projects/" + name);
      if (verbose) System.out.println("deleteProject -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateProject()
   
  /**
   * Creates a new media track record.
   * @param mediaTrack The mediaTrack details to save.
   * @return The mediaTrack just created.
   * @throws StoreException, PermissionException
   * @see #readMediaTracks()
   * @see #readMediaTracks(Integer,Integer)
   * @see #updateMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(String)
   */
  public MediaTrack createMediaTrack(MediaTrack mediaTrack)
    throws StoreException, PermissionException {
    try {
      HttpRequestPost request = post("api/admin/mediatracks")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("createMediaTrack -> " + request);
      response = new Response(request.post(mediaTrack.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new MediaTrack((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of createMediaTrack()
   
  /**
   * Reads a list of mediaTrack records.
   * @return A list of mediaTracks.
   * @throws StoreException, PermissionException
   * @see #createMediaTrack(MediaTrack)
   * @see #readMediaTracks(Integer,Integer)
   * @see #updateMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(String)
   */
  public MediaTrack[] readMediaTracks() throws StoreException, PermissionException {
    return readMediaTracks(null, null);
  }
   
  /**
   * Reads a list of media track records.
   * @param pageNumber The zero-based  page of records to return (if null, all records
   * will be returned). 
   * @param pageLength The length of pages (if null, the default page length is 20).
   * @return A list of mediaTracks.
   * @throws StoreException, PermissionException
   * @see #createMediaTrack(MediaTrack)
   * @see #readMediaTracks()
   * @see #updateMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(String)
   */
  public MediaTrack[] readMediaTracks(Integer pageNumber, Integer pageLength)
    throws StoreException, PermissionException {
    try {
      HttpRequestGet request = get("api/admin/mediatracks")
        .setHeader("Accept", "application/json");
      if (pageLength != null) request.setParameter("pageNumber", pageLength);
      if (pageNumber != null) request.setParameter("pageLength", pageNumber);
      if (verbose) System.out.println("readMediaTracks -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<MediaTrack> mediaTracks = new Vector<MediaTrack>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          mediaTracks.add(new MediaTrack(array.getJsonObject(i)));
        }
      }
      return mediaTracks.toArray(new MediaTrack[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of readMediaTracks()
   
  /**
   * Updates an existing media track record.
   * @param mediaTrack The mediaTrack details to save.
   * @return The mediaTrack just updated.
   * @throws StoreException, PermissionException
   * @see #createMediaTrack(MediaTrack)
   * @see #readMediaTracks()
   * @see #readMediaTracks(Integer,Integer)
   * @see #deleteMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(String)
   */
  public MediaTrack updateMediaTrack(MediaTrack mediaTrack)
    throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("api/admin/mediatracks")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("updateMediaTrack -> " + request);
      response = new Response(request.post(mediaTrack.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new MediaTrack((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateMediaTrack()
   
  /**
   * Deletes an existing media track record.
   * @param mediaTrack The mediaTrack to delete.
   * @throws StoreException, PermissionException
   * @see #createMediaTrack(MediaTrack)
   * @see #readMediaTracks()
   * @see #readMediaTracks(Integer,Integer)
   * @see #updateMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(String)
   */
  public void deleteMediaTrack(MediaTrack mediaTrack) throws StoreException, PermissionException {
    deleteMediaTrack(mediaTrack.getSuffix());
  }
   
  /**
   * Deletes an existing media track record.
   * @param suffix The suffix of the mediaTrack to delete.
   * @throws StoreException, PermissionException
   * @see #createMediaTrack(MediaTrack)
   * @see #readMediaTracks()
   * @see #readMediaTracks(Integer,Integer)
   * @see #updateMediaTrack(MediaTrack)
   * @see #deleteMediaTrack(MediaTrack)
   */
  public void deleteMediaTrack(String suffix) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = delete("api/admin/mediatracks/" + suffix);
      if (verbose) System.out.println("deleteMediaTrack -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateMediaTrack()
   
  /**
   * Creates a new role record.
   * @param role The role details to save.
   * @return The role just created.
   * @throws StoreException, PermissionException
   * @see #readRoles()
   * @see #readRoles(Integer,Integer)
   * @see #updateRole(Role)
   * @see #deleteRole(Role)
   * @see #deleteRole(String)
   */
  public Role createRole(Role role) throws StoreException, PermissionException {
    try {
      HttpRequestPost request = post("api/admin/roles")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("createRole -> " + request);
      response = new Response(request.post(role.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new Role((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of createRole()

  /**
   * Reads a list of role records.
   * @return A list of roles.
   * @throws StoreException, PermissionException
   * @see #createRole(Role)
   * @see #readRoles(Integer,Integer)
   * @see #updateRole(Role)
   * @see #deleteRole(Role)
   * @see #deleteRole(String)
   */
  public Role[] readRoles() throws StoreException, PermissionException {
    return readRoles(null, null);
  }
   
  /**
   * Reads a list of role records.
   * @param pageNumber The zero-based  page of records to return (if null, all records
   * will be returned). 
   * @param pageLength The length of pages (if null, the default page length is 20).
   * @return A list of roles.
   * @throws StoreException, PermissionException
   * @see #createRole(Role)
   * @see #readRoles()
   * @see #updateRole(Role)
   * @see #deleteRole(Role)
   * @see #deleteRole(String)
   */
  public Role[] readRoles(Integer pageNumber, Integer pageLength)
    throws StoreException, PermissionException {
    try {
      HttpRequestGet request = get("api/admin/roles")
        .setHeader("Accept", "application/json");
      if (pageLength != null) request.setParameter("pageNumber", pageLength);
      if (pageNumber != null) request.setParameter("pageLength", pageNumber);
      if (verbose) System.out.println("readRoles -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<Role> roles = new Vector<Role>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          roles.add(new Role(array.getJsonObject(i)));
        }
      }
      return roles.toArray(new Role[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of readRoles()
   
  /**
   * Updates an existing role record.
   * @param role The role details to save.
   * @return The role just updated.
   * @throws StoreException, PermissionException
   * @see #createRole(Role)
   * @see #readRoles()
   * @see #readRoles(Integer,Integer)
   * @see #deleteRole(Role)
   * @see #deleteRole(String)
   */
  public Role updateRole(Role role) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("api/admin/roles")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("updateRole -> " + request);
      response = new Response(request.post(role.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new Role((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateRole()
   
  /**
   * Deletes an existing role record.
   * @param role The role to delete.
   * @throws StoreException, PermissionException
   * @see #createRole(Role)
   * @see #readRoles()
   * @see #readRoles(Integer,Integer)
   * @see #updateRole(Role)
   * @see #deleteRole(String)
   */
  public void deleteRole(Role role) throws StoreException, PermissionException {
    deleteRole(role.getRoleId());
  }
   
  /**
   * Deletes an existing role record.
   * @param name The name/ID of the role to delete.
   * @throws StoreException, PermissionException
   * @see #createRole(Role)
   * @see #readRoles()
   * @see #readRoles(Integer,Integer)
   * @see #updateRole(Role)
   * @see #deleteRole(Role)
   */
  public void deleteRole(String name) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = delete("api/admin/roles/" + name);
      if (verbose) System.out.println("deleteRole -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateRole()

  /**
   * Creates a new role permission record.
   * @param rolePermission The rolePermission details to save.
   * @return The rolePermission just created.
   * @throws StoreException, PermissionException
   * @see #readRolePermissions(String)
   * @see #readRolePermissions(String,Integer,Integer)
   * @see #updateRolePermission(RolePermission)
   * @see #deleteRolePermission(RolePermission)
   * @see #deleteRolePermission(String,String)
   */
  public RolePermission createRolePermission(RolePermission rolePermission)
    throws StoreException, PermissionException {
    try {
      HttpRequestPost request = post("api/admin/roles/permissions")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("createRolePermission -> " + request);
      response = new Response(request.post(rolePermission.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new RolePermission((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of createRolePermission()

  /**
   * Reads a list of role permission records.
   * @param roleId The ID of the user role to get permissions for.
   * @return A list of rolePermissions.
   * @throws StoreException, PermissionException
   * @see #createRolePermission(RolePermission)
   * @see #readRolePermissions(String,Integer,Integer)
   * @see #updateRolePermission(RolePermission)
   * @see #deleteRolePermission(RolePermission)
   * @see #deleteRolePermission(String,String)
   */
  public RolePermission[] readRolePermissions(String roleId)
    throws StoreException, PermissionException {
    return readRolePermissions(roleId, null, null);
  }
   
  /**
   * Reads a list of role permission records.
   * @param roleId The ID of the user role to get permissions for.
   * @param pageNumber The zero-based  page of records to return (if null, all records
   * will be returned). 
   * @param pageLength The length of pages (if null, the default page length is 20).
   * @return A list of rolePermissions.
   * @throws StoreException, PermissionException
   * @see #createRolePermission(RolePermission)
   * @see #readRolePermissions(String)
   * @see #updateRolePermission(RolePermission)
   * @see #deleteRolePermission(RolePermission)
   * @see #deleteRolePermission(String,String)
   */
  public RolePermission[] readRolePermissions(String roleId, Integer pageNumber, Integer pageLength)
    throws StoreException, PermissionException {
    try {
      HttpRequestGet request = get("api/admin/roles/permissions/" + roleId)
        .setHeader("Accept", "application/json");
      if (pageLength != null) request.setParameter("pageNumber", pageLength);
      if (pageNumber != null) request.setParameter("pageLength", pageNumber);
      if (verbose) System.out.println("readRolePermissions -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<RolePermission> rolePermissions = new Vector<RolePermission>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          rolePermissions.add(new RolePermission(array.getJsonObject(i)));
        }
      }
      return rolePermissions.toArray(new RolePermission[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of readRolePermissions()
   
  /**
   * Updates an existing role permission record.
   * @param rolePermission The rolePermission details to save.
   * @return The rolePermission just updated.
   * @throws StoreException, PermissionException
   * @see #createRolePermission(RolePermission)
   * @see #readRolePermissions(String)
   * @see #readRolePermissions(String,Integer,Integer)
   * @see #deleteRolePermission(RolePermission)
   * @see #deleteRolePermission(String,String)
   */
  public RolePermission updateRolePermission(RolePermission rolePermission)
    throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("api/admin/roles/permissions")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("updateRolePermission -> " + request);
      response = new Response(request.post(rolePermission.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new RolePermission((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateRolePermission()
   
  /**
   * Deletes an existing role permission record.
   * @param rolePermission The rolePermission to delete.
   * @throws StoreException, PermissionException
   * @see #createRolePermission(RolePermission)
   * @see #readRolePermissions(String)
   * @see #readRolePermissions(String,Integer,Integer)
   * @see #updateRolePermission(RolePermission)
   * @see #deleteRolePermission(String,String)
   */
  public void deleteRolePermission(RolePermission rolePermission)
    throws StoreException, PermissionException {
    deleteRolePermission(rolePermission.getRoleId(), rolePermission.getEntity());
  }
   
  /**
   * Deletes an existing role permission record.
   * @param roleId The name/ID of the role.
   * @param entity The entity of the permission.
   * @throws StoreException, PermissionException
   * @see #createRolePermission(RolePermission)
   * @see #readRolePermissions(String)
   * @see #readRolePermissions(String,Integer,Integer)
   * @see #updateRolePermission(RolePermission)
   * @see #deleteRolePermission(RolePermission)
   */
  public void deleteRolePermission(String roleId, String entity)
    throws StoreException, PermissionException {
    try{
      HttpRequestPost request = delete("api/admin/roles/permissions/" + roleId + "/" + entity);
      if (verbose) System.out.println("deleteRolePermission -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateRolePermission()

  /**
   * Reads a list of system_attribute records.
   * @return A list of system attributes.
   * @throws StoreException, PermissionException
   * @see #updateSystemAttribute(SystemAttribute)
   */
  public SystemAttribute[] readSystemAttributes()
    throws StoreException, PermissionException {
    try {
      HttpRequestGet request = get("api/admin/systemattributes")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("readSystemAttributes -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<SystemAttribute> systemAttributes = new Vector<SystemAttribute>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          systemAttributes.add(new SystemAttribute(array.getJsonObject(i)));
        }
      }
      return systemAttributes.toArray(new SystemAttribute[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of readSystemAttributes()
   
  /**
   * Updates an existing systemAttribute record.
   * @param attribute The ID of the attribute to update.
   * @param value The new value of the attribute.
   * @return The systemAttribute just updated.
   * @throws StoreException If the attribute doesn't exist or its type is "readonly".
   * @throws PermissionException
   * @see #readSystemAttributes()
   * @see #updateSystemAttribute(SystemAttribute)
   */
  public SystemAttribute updateSystemAttribute(String attribute, String value)
    throws StoreException, PermissionException {
    return updateSystemAttribute(new SystemAttribute().setAttribute(attribute).setValue(value));
  } // end of updateSystemAttribute()
   
  /**
   * Updates an existing systemAttribute record.
   * @param systemAttribute The systemAttribute details to save.
   * @return The systemAttribute just updated.
   * @throws StoreException, PermissionException
   * @see #readSystemAttributes()
   * @see #updateSystemAttribute(String,String)
   */
  public SystemAttribute updateSystemAttribute(SystemAttribute systemAttribute) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("api/admin/systemattributes")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("updateSystemAttribute -> " + request);
      response = new Response(request.post(systemAttribute.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new SystemAttribute((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateSystemAttribute()

  /**
   * Saves the store's information document.
   * @param html An HTML document with information about the corpus as a whole.
   * @throws StoreException, PermissionException
   * @see LabbcatView#getInfo()
   */
  public void updateInfo(String html) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("doc/")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("unpdateInfo -> " + request);
      response = new Response(request.post(html), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateInfo()

  /**
   * Creates a new user record.
   * @param user The user details to save.
   * @return The user just created.
   * @throws StoreException, PermissionException
   * @see #readUsers()
   * @see #readUsers(Integer,Integer)
   * @see #updateUser(User)
   * @see #deleteUser(User)
   * @see #deleteUser(String)
   */
  public User createUser(User user) throws StoreException, PermissionException {
    try {
      HttpRequestPost request = post("api/admin/users")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("createUser -> " + request + user.toJson());
      response = new Response(request.post(user.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new User((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of createUser()

  /**
   * Reads a list of user records.
   * @return A list of users.
   * @throws StoreException, PermissionException
   * @see #createUser(User)
   * @see #readUsers(Integer,Integer)
   * @see #updateUser(User)
   * @see #deleteUser(User)
   * @see #deleteUser(String)
   */
  public User[] readUsers() throws StoreException, PermissionException {
    return readUsers(null, null);
  }
   
  /**
   * Reads a list of user records.
   * @param pageNumber The zero-based  page of records to return (if null, all records
   * will be returned). 
   * @param pageLength The length of pages (if null, the default page length is 20).
   * @return A list of users.
   * @throws StoreException, PermissionException
   * @see #createUser(User)
   * @see #readUsers()
   * @see #updateUser(User)
   * @see #deleteUser(User)
   * @see #deleteUser(String)
   */
  public User[] readUsers(Integer pageNumber, Integer pageLength)
    throws StoreException, PermissionException {
    try {
      HttpRequestGet request = get("api/admin/users")
        .setHeader("Accept", "application/json");
      if (pageLength != null) request.setParameter("pageNumber", pageLength);
      if (pageNumber != null) request.setParameter("pageLength", pageNumber);
      if (verbose) System.out.println("readUsers -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<User> users = new Vector<User>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          users.add(new User(array.getJsonObject(i)));
        }
      }
      return users.toArray(new User[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of readUsers()
   
  /**
   * Updates an existing user record.
   * @param user The user details to save.
   * @return The user just updated.
   * @throws StoreException, PermissionException
   * @see #createUser(User)
   * @see #readUsers()
   * @see #readUsers(Integer,Integer)
   * @see #deleteUser(User)
   * @see #deleteUser(String)
   */
  public User updateUser(User user) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("api/admin/users")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("updateUser -> " + request);
      response = new Response(request.post(user.toJson()), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new User((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateUser()
   
  /**
   * Deletes an existing user record.
   * @param user The user to delete.
   * @throws StoreException, PermissionException
   * @see #createUser(User)
   * @see #readUsers()
   * @see #readUsers(Integer,Integer)
   * @see #updateUser(User)
   * @see #deleteUser(String)
   */
  public void deleteUser(User user) throws StoreException, PermissionException {
    deleteUser(user.getUser());
  }
   
  /**
   * Deletes an existing user record.
   * @param user The ID of the user to delete.
   * @throws StoreException, PermissionException
   * @see #createUser(User)
   * @see #readUsers()
   * @see #readUsers(Integer,Integer)
   * @see #updateUser(User)
   * @see #deleteUser(User)
   */
  public void deleteUser(String user) throws StoreException, PermissionException {
    try{
      HttpRequestPost request = delete("api/admin/users/" + user);
      if (verbose) System.out.println("deleteUser -> " + request);
      response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateUser()
   
  /**
   * Sets a given user's password.
   * @param user The ID of the user to update.
   * @param password The new password.
   * @param resetPassword Whether the user must reset their password when they next log in.
   * @throws StoreException If an error occurs, e.g. the user does not exist.
   * @throws PermissionException If the current user does not have the 'admin' role.
   */
  public void setPassword(String user, String password, boolean resetPassword)
    throws StoreException, PermissionException {
    try{
      HttpRequestPost request = put("api/admin/password")
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("updateUser -> " + request);
      JsonObjectBuilder json = Json.createObjectBuilder();
      if (user != null) json.add("user", user);
      if (password != null) json.add("password", password);
      json.add("resetPassword", resetPassword); 
      response = new Response(request.post(json.build()), verbose);
      response.checkForErrors(); // throws a StoreException on error
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of updateUser()
  
  /**
   * Upload a flat lexicon file for lexical tagging.
   * <p> By default LaBB-CAT includes a layer manager called the Flat Lexicon Tagger, which can
   * be configured to annotate words with data from a dictionary loaded from a plain text
   * file (e.g. a CSV file). The file must have a 'flat' structure in the sense that it's a
   * simple list of dictionary entries with a fixed number of columns/fields, rather than
   * having a complex structure.
   * @param file The lexicon file.
   * @param lexicon The name for the resulting lexicon. If the named lexicon already exists,
   * it will be completely replaced with the contents of the file (i.e. all existing
   * entries will be deleted befor adding new entries from the file). e.g. 'cmudict'
   * @param fieldDelimiter The character used to delimit fields in the file.
   * If this is " - ", rows are split on only the *first* space,  in line with common
   * dictionary formats. e.g. ',' for Comma Separated Values (CSV) files.
   * @param fieldNames A list of field names, delimited by fieldDelimiter,
   * e.g. 'Word,Pronunciation'.
   * @param quote The character used to quote field values (if any),
   * e.g. '"'.
   * @param comment The character used to indicate a line is a comment (not an entry) (if any)
   * e.g. '#'.
   * @param skipFirstLine Whether to ignore the first line of the file (because it
   * contains field names).
   * @throws StoreException If the lexicon could not be loaded.
   * @see #deleteLexicon(String)
   */
  public void loadLexicon(
    File file, String lexicon, String fieldDelimiter, String fieldNames,
    String quote, String comment, boolean skipFirstLine)
    throws StoreException {
    URL url = makeUrl("edit/annotator/ext/FlatLexiconTagger/loadLexicon");
    if (quote == null) quote = "";
    if (comment == null) comment = "";
    if (lexicon == null) lexicon = file.getName();
    try {
      postRequest = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
        .setUserAgent()
        //.setHeader("Accept", "application/json")
        .setParameter("lexicon", lexicon)
        .setParameter("fieldDelimiter", fieldDelimiter)
        .setParameter("quote", quote)
        .setParameter("comment", comment)
        .setParameter("fieldNames", fieldNames)
        .setParameter("skipFirstLine", skipFirstLine)
        .setParameter("file", file);
      if (verbose) System.out.println("loadLexicon -> " + postRequest);
      HttpURLConnection connection = postRequest.post();
      try {
        String response = IO.InputStreamToString​(connection.getInputStream());
        if (verbose) System.out.println("response: " + response);
      } catch(IOException exception) {
        String error = IO.InputStreamToString​(connection.getErrorStream());
        if (verbose) System.out.println("ERROR: " + error);
        throw new StoreException(
          exception.getMessage() + ": " + error);
      }
        
      // the server loads the lexicon asynchronously, wait for it to finish
      boolean running = true;
      String status = "Uploading";
      int percentComplete = 0;
      URL runningUrl = makeUrl("edit/annotator/ext/FlatLexiconTagger/getRunning");
      URL statusUrl = makeUrl("edit/annotator/ext/FlatLexiconTagger/getStatus");
      URL percentCompleteUrl = makeUrl(
        "edit/annotator/ext/FlatLexiconTagger/getPercentComplete");
      while(running) {
        try { Thread.sleep(1000); } catch(Exception x) {}
        running = IO.InputStreamToString​(
          new HttpRequestGet(runningUrl, getRequiredHttpAuthorization()).get().getInputStream())
          .equalsIgnoreCase("true");
        status = IO.InputStreamToString​(
          new HttpRequestGet(statusUrl, getRequiredHttpAuthorization()).get().getInputStream());
        percentComplete = Integer.parseInt(
          IO.InputStreamToString​(
            new HttpRequestGet(percentCompleteUrl, getRequiredHttpAuthorization()).get()
            .getInputStream()));
        if (verbose) {
          System.out.println("status: " + percentComplete + "% " + status + " - " + running);
        }
      }
      if (verbose) System.out.println("Finished.");
      if (percentComplete < 100) {
        throw new StoreException(status);
      }
    } catch (IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of loadLexicon()
  
  /**
   * Delete a previously loaded lexicon.
   * @param lexicon
   * @throws StoreException
   * @see #loadLexicon(File,String,String,String,String,String,boolean)
   */
  public void deleteLexicon(String lexicon) throws StoreException {
    try {
      HttpRequestGet request = get(
        "edit/annotator/ext/FlatLexiconTagger/deleteLexicon?"
        +URLEncoder.encode(lexicon, "UTF-8"))
        .setHeader("Accept", "text/plain");
      if (verbose) System.out.println("deleteLexicon -> " + request);
      HttpURLConnection connection = request.get();
      int httpStatus = connection.getResponseCode();
      if (verbose) System.out.println("HTTP status: " + connection.getResponseCode());
      if (httpStatus != HttpURLConnection.HTTP_OK) {
        throw new StoreException(connection.getResponseMessage());
      }
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of deleteLexicon()

  // TODO uploadAnnotator
  // TODO installAnnotator
  // TODO uninstallAnnotator
  // TODO newAnnotatorTask
  // TODO getAnnotatorTasks
  // TODO getAnnotatorTaskParameters
  // TODO saveAnnotatorTaskDescription
  // TODO saveAnnotatorTaskParameters
  // TODO deleteAnnotatorTask
} // end of class LabbcatAdmin
