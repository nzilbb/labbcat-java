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
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Vector;
import java.util.function.Consumer;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import nzilbb.ag.Anchor;
import nzilbb.ag.Annotation;
import nzilbb.ag.Graph;
import nzilbb.ag.GraphNotFoundException;
import nzilbb.ag.GraphStoreQuery;
import nzilbb.ag.Layer;
import nzilbb.ag.MediaFile;
import nzilbb.ag.MediaTrackDefinition;
import nzilbb.ag.PermissionException;
import nzilbb.ag.Schema;
import nzilbb.ag.StoreException;
import nzilbb.ag.automation.util.AnnotatorDescriptor;
import nzilbb.ag.serialize.GraphDeserializer;
import nzilbb.ag.serialize.GraphSerializer;
import nzilbb.ag.serialize.SerializationDescriptor;
import nzilbb.labbcat.http.*;
import nzilbb.labbcat.model.Match;
import nzilbb.labbcat.model.MatchId;
import nzilbb.labbcat.model.TaskStatus;
import nzilbb.labbcat.model.User;
import nzilbb.util.IO;
import nzilbb.util.MonitorableSeries;

/**
 * Client-side implementation of 
 * <a href="https://nzilbb.github.io/ag/apidocs/nzilbb/ag/GraphStoreQuery.html">nzilbb.ag.GraphStoreQuery</a>.
 * <p>This class provides only <em>read-only</em> operations, i.e. those that can be
 * performed by users with <q>view</q> permission. 
 * <p> e.g.
 * <pre> // create LaBB-CAT client
 * LabbcatView labbcat = new {@link #LabbcatView(String,String,String) LabbcatView}("https://labbcat.canterbury.ac.nz", "demo", "demo");
 * // get some basic information
 * String id = lbbcat.{@link #getId()};
 * String[] layers = labbcat.{@link #getLayerIds()};
 * String[] corpora = labbcat.{@link #getCorpusIds()};
 * String[] documents = labbcat.{@link #getTranscriptIdsInCorpus(String) getTranscriptIdsInCorpus}(corpora[0]);
 * // search for tokens of "and"
 * Matches[] matches = labbcat.{@link #getMatches(String,int) getMatches}(
 *     labbcat.{@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer) search}(
 *        new {@link PatternBuilder}().addMatchLayer("orthography", "and").build(),
 *        participantIds, null, true, false, null, 5), 1);
 * </pre>
 * @author Robert Fromont robert@fromont.net.nz
 */
public class LabbcatView implements GraphStoreQuery {
   
  // Attributes:
  
  /**
   * The base URL of the LaBB-CAT server - e.g. https://labbcat.canterbury.ac.nz/demo/
   * @see #getLabbcatUrl()
   * @see #setLabbcatUrl(URL)
   */
  protected URL labbcatUrl;
  /**
   * Getter for {@link #labbcatUrl}: The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   * @return The base URL of the LaBB-CAT server - e.g. https://labbcat.canterbury.ac.nz/demo/
   */
  public URL getLabbcatUrl() { return labbcatUrl; }
  /**
   * Setter for {@link #labbcatUrl}: The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/ 
   * @param newLabbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/ 
   */
  public LabbcatView setLabbcatUrl(URL newLabbcatUrl) { labbcatUrl = newLabbcatUrl; return this; }
   
  /**
   * LaBB-CAT username.
   * @see #getUsername()
   * @see #setUsername(String)
   */
  protected String username;
  /**
   * Getter for {@link #username}: LaBB-CAT username.
   * @return LaBB-CAT username.
   */
  public String getUsername() { return username; }
  /**
   * Setter for {@link #username}: LaBB-CAT username.
   * @param newUsername LaBB-CAT username.
   */
  public LabbcatView setUsername(String newUsername) { username = newUsername; return this; }

  /**
   * LaBB-CAT password.
   * @see #getPassword()
   * @see #setPassword(String)
   */
  protected String password;
  /**
   * Getter for {@link #password}: LaBB-CAT password.
   * @return LaBB-CAT password.
   */
  public String getPassword() { return password; }
  /**
   * Setter for {@link #password}: LaBB-CAT password.
   * @param newPassword LaBB-CAT password.
   */
  public LabbcatView setPassword(String newPassword) { password = newPassword; return this; }

  /**
   * Whether to run in batch mode or not. If false, the user may be asked to enter
   * username/password if required. Default is false.
   * @see #getBatchMode()
   * @see #setBatchMode(Boolean)
   */
  protected boolean batchMode = false;
  /**
   * Getter for {@link #batchMode}: Whether to run in batch mode or not. If false, the
   * user may be asked to enter username/password if required.  Default is false.
   * @return Whether to run in batch mode or not. If false, the user may be asked to
   * enter username/password if required. 
   */
  public boolean getBatchMode() { return batchMode; }
  /**
   * Setter for {@link #batchMode}: Whether to run in batch mode or not. If false, the
   * user may be asked to enter username/password if required. 
   * @param newBatchMode Whether to run in batch mode or not. If false, the user may be
   * asked to enter username/password if required. 
   */
  public LabbcatView setBatchMode(boolean newBatchMode) { batchMode = newBatchMode; return this; }

  /**
   * Whether to print verbose output or not.
   * @see #getVerbose()
   * @see #setVerbose(boolean)
   */
  protected boolean verbose;
  /**
   * Getter for {@link #verbose}: Whether to print verbose output or not.
   * @return Whether to print verbose output or not.
   */
  public boolean getVerbose() { return verbose; }
  /**
   * Setter for {@link #verbose}: Whether to print verbose output or not.
   * @param newVerbose Whether to print verbose output or not.
   */
  public LabbcatView setVerbose(boolean newVerbose) { verbose = newVerbose; return this; }

  /**
   * Minimum server version required for this API to work properly.
   * @see #getMinLabbcatVersion()
   * @see #setMinLabbcatVersion(String)
   */
  protected String minLabbcatVersion = "20210210.2032";
  /**
   * Getter for {@link #minLabbcatVersion}: Minimum server version required for this API
   * to work properly. 
   * @return Minimum server version required for this API to work properly.
   */
  public String getMinLabbcatVersion() { return minLabbcatVersion; }

  /**
   * The last response received from the server.
   * @see #getResponse()
   * @see #setResponse(Response)
   */
  protected Response response;
  /**
   * Getter for {@link #response}: The last response received from the server.
   * @return The last response received from the server.
   */
  public Response getResponse() { return response; }
  /**
   * Setter for {@link #response}: The last response received from the server.
   * @param newResponse The last response received from the server.
   */
  public LabbcatView setResponse(Response newResponse) { response = newResponse; return this; }

  /** Current request, if any */
  protected HttpRequestPostMultipart postRequest;   
  
  /**
   * The language code for server message localization, e.g. "es-AR" for Argentine Spanish.
   * @see #getLanguage()
   * @see #setLanguage(String)
   */
  protected String language;   
  /**
   * Getter for {@link #language}: The language code for server message localization,
   * e.g. "es-AR" for Argentine Spanish.
   * @return The language code for server message localization, e.g. "es-AR" for Argentine Spanish.
   */
  public String getLanguage() { return language; }   
  /**
   * Setter for {@link #language}: The language code for server message localization,
   * e.g. "es-AR" for Argentine Spanish. 
   * @param newLanguage The language code for server message localization, e.g. "es-AR"
   * for Argentine Spanish. 
   */
  public LabbcatView setLanguage(String newLanguage) { language = newLanguage; return this; }
  // Methods:
   
  /**
   * Default constructor.
   */
  public LabbcatView() {
  } // end of constructor
   
  /**
   * Constructor from string URL.
   * @param labbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   */
  public LabbcatView(String labbcatUrl) throws MalformedURLException {
    if (!labbcatUrl.endsWith("/")) labbcatUrl += "/";
    setLabbcatUrl(new URL(labbcatUrl));
  } // end of constructor
   
  /**
   * Constructor with String attributes.
   * @param labbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   * @param username LaBB-CAT username.
   * @param password LaBB-CAT password.
   */
  public LabbcatView(String labbcatUrl, String username, String password)
    throws MalformedURLException {
    if (!labbcatUrl.endsWith("/")) labbcatUrl += "/";
    setLabbcatUrl(new URL(labbcatUrl));
    setUsername(username);
    setPassword(password);
  } // end of constructor
   
  /**
   * Constructor from URL.
   * @param labbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   */
  public LabbcatView(URL labbcatUrl) {      
    setLabbcatUrl(labbcatUrl);
  } // end of constructor
   
  /**
   * Constructor with attributes.
   * @param labbcatUrl The base URL of the LaBB-CAT server -
   * e.g. https://labbcat.canterbury.ac.nz/demo/
   * @param username LaBB-CAT username.
   * @param password LaBB-CAT password.
   */
  public LabbcatView(URL labbcatUrl, String username, String password) {      
    setLabbcatUrl(labbcatUrl);
    setUsername(username);
    setPassword(password);
  } // end of constructor

  private String authorization = null;
  /**
   * Determines whether an authorization string is required for HTTP requests
   * (i.e. whether a username/password is required)
   * @return The authorization string that's required, if any
   * @throws Exception if the user cancels while begin prompted for credentials
   */
  public String getRequiredHttpAuthorization()
    throws IOException, StoreException {
      
    if (authorization != null) return authorization;
      
    URL testUrl = url(""); // store URL with no path
    HttpURLConnection testConnection = (HttpURLConnection)testUrl.openConnection();
    response = null;
    try {
      InputStream is = testConnection.getInputStream();
      response = new Response(is, verbose);
    } catch (IOException x) {
      if (verbose) {
        System.out.println(
          "First connection test status ("+(batchMode?"batch":"interacive")+" mode): "
          + testConnection.getResponseCode());
      }            
      if (testConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
        if (batchMode) { // can only try with username/password once
          if (username != null && password != null) {
            authorization = "Basic " + new String(
              Base64.getMimeEncoder().encode(
                (username+":"+password).getBytes()), StandardCharsets.UTF_8);
            testConnection.disconnect();
            testConnection = (HttpURLConnection)testUrl.openConnection();
            testConnection.setRequestProperty("Authorization", authorization);
            try { 
              InputStream is = testConnection.getInputStream(); 
              response = new Response(is, verbose);
            } catch (IOException xx) {
              if (verbose) {
                System.out.println(
                  "Second connection test status in batch mode: "
                  + testConnection.getResponseCode());
              }            
              if (testConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                authorization = null;
                username = null;
                password = null;
                throw new IOException("Username/password invalid");
              } else {
                throw xx;
              }
            }
          } else {
            throw new IOException("Username/password required");
          }
        } else { // not batchMode
          JPasswordField txtPassword = new JPasswordField();
          // loop until a username/password works
          while (authorization == null) {
            if (username == null) {
              username = JOptionPane.showInputDialog(null, "Username", username);
            }
            if (username == null) throw new IOException("Cancelled");
            txtPassword.setText("");
            if (password == null || password.length() == 0) {
              JOptionPane.showMessageDialog(
                null, txtPassword, "Password", JOptionPane.QUESTION_MESSAGE);
              password = new String(txtPassword.getPassword());
            }
            authorization = "Basic " + new String(
              Base64.getMimeEncoder().encode(
                (username+":"+password).getBytes()), StandardCharsets.UTF_8);
            testConnection.disconnect();
            testConnection = (HttpURLConnection)testUrl.openConnection();
            testConnection.setRequestProperty("Authorization", authorization);
            try { 
              InputStream is = testConnection.getInputStream(); 
              response = new Response(is, verbose);
            } catch (Exception xx) {
              if (verbose) {
                System.out.println(
                  "Next connection test status in interactive mode: "
                  + testConnection.getResponseCode());
              }
              if (testConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                authorization = null;
                username = null;
                password = null;
              }
            }
          } // next attempt
        } // not batchMode
      } else { // not HTTP_UNAUTHORIZED returned
        throw x;
      }
    } // exception getting content

    if (response != null) { // got a response
      // check server version
      if (response.getVersion() == null
          || response.getVersion().compareTo(minLabbcatVersion) < 0) {
        throw new StoreException(
          "Server is version " + response.getVersion()
          + " but the minimum required version is " + minLabbcatVersion);
      }
    }
      
    return authorization;
  } // end of getRequiredHttpAuthorization()
   
  /**
   * Constructs a URL for the given resource.
   * @param resource
   * @return A URL for the given resource.
   * @throws StoreException If the URL is malformed.
   */
  public URL url(String resource)
    throws StoreException {
      
    try {
      return new URL(new URL(labbcatUrl, "api/store/"), resource);
    } catch(Throwable t) {
      throw new StoreException("Could not construct request URL.", t);
    }
  } // end of makeUrl()   

  /**
   * Constructs a URL for the given resource.
   * @param resource The resource, which must be URLEncoded if necessary.
   * @return A URL for the given resource.
   * @throws StoreException If the URL is malformed.
   */
  public URL makeUrl(String resource) throws StoreException {
    try {
      return new URL(labbcatUrl, resource);
    } catch(Throwable t) {
      throw new StoreException("Could not construct request URL.", t);
    }
  } // end of editUrl()
   
  /**
   * Constructs a GET request for the given resource. The resulting request will be
   * authorized if required, but otherwise has no headers or parameters set. 
   * @param resource The path to the resource.
   * @return The request.
   * @throws IOException
   * @throws ResponseException
   */
  public HttpRequestGet get(String resource) throws IOException, StoreException {
    return new HttpRequestGet(makeUrl(resource), getRequiredHttpAuthorization())
      .setUserAgent().setLanguage(language);
  } // end of get()

  /**
   * Constructs a POST request for the given resource. The resulting request will be
   * authorized if required, but otherwise has no headers or parameters set. 
   * @param resource The path to the resource.
   * @return The request.
   * @throws IOException
   * @throws ResponseException
   */
  public HttpRequestPost post(String resource) throws IOException, StoreException {
    return new HttpRequestPost(makeUrl(resource), getRequiredHttpAuthorization())
      .setUserAgent().setLanguage(language);
  } // end of post()

  /**
   * Constructs a PUT request for the given resource. The resulting request will be
   * authorized if required, but otherwise has no headers or parameters set. 
   * @param resource The path to the resource.
   * @return The request.
   * @throws IOException
   * @throws ResponseException
   */
  public HttpRequestPost put(String resource) throws IOException, StoreException {
    return new HttpRequestPost(makeUrl(resource), getRequiredHttpAuthorization())
      .setMethod("PUT")
      .setUserAgent().setLanguage(language);
  } // end of post()

  /**
   * Constructs a DELETE request for the given resource. The resulting request will be
   * authorized if required, but otherwise has no headers or parameters set. 
   * @param resource The path to the resource.
   * @return The request.
   * @throws IOException
   * @throws ResponseException
   */
  public HttpRequestPost delete(String resource) throws IOException, StoreException {
    return new HttpRequestPost(makeUrl(resource), getRequiredHttpAuthorization())
      .setMethod("DELETE")
      .setUserAgent().setLanguage(language);
  } // end of post()

  /**
   * Constructs a multipart POST request for the given resource. The resulting request
   * will be authorized if required, but otherwise has no headers or parameters set. 
   * <p> The post-request is remembered, so subsequent calls to {@link #cancel()} will
   * cancel the request if it's in-course.
   * @param resource The path to the resource.
   * @return The request.
   * @throws IOException
   * @throws ResponseException
   */
  public HttpRequestPostMultipart postMultipart(String resource)
    throws IOException, StoreException {
      
    postRequest = new HttpRequestPostMultipart(
      makeUrl(resource), getRequiredHttpAuthorization())
      .setUserAgent().setLanguage(language);
    return postRequest;
  } // end of postMultipart()

  // GraphStoreQuery methods:
   
  /**
   * Gets the store's ID.
   * @return The annotation store's ID.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String getId()
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getId");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getId -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return ((JsonString)response.getModel()).getString();
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
   
  /**
   * Gets the store's information document.
   * @return An HTML document providing information about the corpus.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String getInfo()
    throws StoreException, PermissionException {
      
    try {
      URL url = makeUrl("doc/");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "text/html");
      if (verbose) System.out.println("getInfo -> " + request);
      HttpURLConnection connection = request.get();
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        throw new StoreException(
          "Error " + connection.getResponseCode()
          + " " + connection.getResponseMessage() + " - " + request);
      } else {
        return IO.InputStreamToString(connection.getInputStream());
      } // response ok
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
   
  /**
   * Gets a list of layer IDs (annotation 'types').
   * @return A list of layer IDs.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String[] getLayerIds()
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getLayerIds");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getLayerIds -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<String> ids = new Vector<String>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          ids.add(array.getString(i));
        }
      }
      return ids.toArray(new String[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 
   
  /**
   * Gets a list of layer definitions.
   * @return A list of layer definitions.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public Layer[] getLayers()
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getLayers");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getLayers -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<Layer> layers = new Vector<Layer>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          layers.add(new Layer(array.getJsonObject(i)));
        }
      }
      return layers.toArray(new Layer[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets the layer schema.
   * @return A schema defining the layers and how they relate to each other.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public Schema getSchema()
    throws StoreException, PermissionException {
    throw new StoreException("Not implemented");
  }

  /**
   * Gets a layer definition.
   * @param id ID of the layer to get the definition for.
   * @return The definition of the given layer.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public Layer getLayer(String id)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getLayer");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id);
      if (verbose) System.out.println("getLayer -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return new Layer((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }   

  /**
   * Gets a list of corpus IDs.
   * @return A list of corpus IDs.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String[] getCorpusIds()
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getCorpusIds");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getCorpusIds -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<String> ids = new Vector<String>();
      if (array != null)
      {
        for (int i = 0; i < array.size(); i++)
        {
          ids.add(array.getString(i));
        }
      }
      return ids.toArray(new String[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 
   
  /**
   * Gets a list of participant IDs.
   * @return A list of participant IDs.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String[] getParticipantIds()
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getParticipantIds");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getParticipantIds -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<String> ids = new Vector<String>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          ids.add(array.getString(i));
        }
      }
      return ids.toArray(new String[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 

  /**
   * Gets the participant record specified by the given identifier.
   * @param id The ID of the participant, which could be their name or their database annotation
   * ID. 
   * @param layerIds The IDs of the participant attribute layers to load, or null if only
   * participant data is required. 
   * @return An annotation representing the participant, or null if the participant was
   * not found.
   * @throws StoreException
   * @throws PermissionException
   */
  public Annotation getParticipant(String id, String[] layerIds)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getParticipant");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id);
      if (layerIds != null) request.setParameter("layerIds", layerIds);
      if (verbose) System.out.println("getParticipant -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return (Annotation)new Annotation().fromJson((JsonObject)response.getModel());
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * Counts the number of participants that match a particular pattern.
   * @param expression An expression that determines which participants match.
   * <p> The expression language is loosely based on JavaScript; expressions such as the
   * following can be used: 
   * <ul>
   *  <li><code>/Ada.+/.test(id)</code></li>
   *  <li><code>labels('corpus').includes('CC')</code></li>
   *  <li><code>labels('participant_languages').includes('en')</code></li>
   *  <li><code>labels('transcript_language').includes('en')</code></li>
   *  <li><code>!/Ada.+/.test(id) &amp;&amp; first('corpus').label == 'CC'</code></li>
   *  <li><code>all('transcript_rating').length &gt; 2</code></li>
   *  <li><code>all('participant_rating').length = 0</code></li>
   *  <li><code>!annotators('transcript_rating').includes('labbcat')</code></li>
   *  <li><code>first('participant_gender').label == 'NA'</code></li>
   * </ul>
   * @return The number of matching participants.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public int countMatchingParticipantIds(String expression)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("countMatchingParticipantIds");
      if (verbose) System.out.println("countMatchingParticipantIds -> " + url);
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("expression", expression);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      return ((JsonNumber)response.getModel()).intValue();
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 
   
  /**
   * Gets a list of IDs of participants that match a particular pattern.
   * @param expression An expression that determines which participants match.
   * <p> The expression language is loosely based on JavaScript; expressions such as the
   * following can be used: 
   * <ul>
   *  <li><code>/Ada.+/.test(id)</code></li>
   *  <li><code>labels('corpus').includes('CC')</code></li>
   *  <li><code>labels('participant_languages').includes('en')</code></li>
   *  <li><code>labels('transcript_language').includes('en')</code></li>
   *  <li><code>!/Ada.+/.test(id) &amp;&amp; first('corpus').label == 'CC'</code></li>
   *  <li><code>all('transcript_rating').length &gt; 2</code></li>
   *  <li><code>all('participant_rating').length = 0</code></li>
   *  <li><code>!annotators('transcript_rating').includes('labbcat')</code></li>
   *  <li><code>first('participant_gender').label == 'NA'</code></li>
   * </ul>
   * @param pageLength The maximum number of IDs to return, or null to return all.
   * @param pageNumber The zero-based page number to return, or null to return the first page.
   * @return A list of participant IDs.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String[] getMatchingParticipantIds(
    String expression, Integer pageLength, Integer pageNumber)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getMatchingParticipantIds");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("expression", expression);
      if (pageLength != null) request.setParameter("pageLength", pageLength);
      if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
      if (verbose) System.out.println("getMatchingParticipantIds -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<String> ids = new Vector<String>();
      if (array != null)
      {
        for (int i = 0; i < array.size(); i++)
        {
          ids.add(array.getString(i));
        }
      }
      return ids.toArray(new String[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 

  /**
   * Gets a list of transcript IDs.
   * @return A list of transcript IDs.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String[] getTranscriptIds()
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getTranscriptIds");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getTranscriptIds -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<String> ids = new Vector<String>();
      if (array != null)
      {
        for (int i = 0; i < array.size(); i++)
        {
          ids.add(array.getString(i));
        }
      }
      return ids.toArray(new String[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 
   
  /**
   * Gets a list of transcript IDs in the given corpus.
   * @param id A corpus ID.
   * @return A list of transcript IDs.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String[] getTranscriptIdsInCorpus(String id)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getTranscriptIdsInCorpus");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id);
      if (verbose) System.out.println("getTranscriptIdsInCorpus -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<String> ids = new Vector<String>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          ids.add(array.getString(i));
        }
      }
      return ids.toArray(new String[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 

  /**
   * Gets a list of IDs of transcripts that include the given participant.
   * @param id A participant ID.
   * @return A list of transcript IDs.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String[] getTranscriptIdsWithParticipant(String id)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getTranscriptIdsWithParticipant");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id);
      if (verbose) System.out.println("getTranscriptIdsWithParticipant -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<String> ids = new Vector<String>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          ids.add(array.getString(i));
        }
      }
      return ids.toArray(new String[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 

  /**
   * Counts the number of transcripts that match a particular pattern.
   * @param expression An expression that determines which transcripts match.
   * <p> The expression language is loosely based on JavaScript; expressions such as
   * the following can be used: 
   *  <li><code>/Ada.+/.test(id)</code></li>
   *  <li><code>labels('participant').includes('Robert')</code></li>
   *  <li><code>('CC', 'IA', 'MU').includes(first('corpus').label)</code></li>
   *  <li><code>first('episode').label == 'Ada Aitcheson'</code></li>
   *  <li><code>first('transcript_scribe').label == 'Robert'</code></li>
   *  <li><code>first('participant_languages').label == 'en'</code></li>
   *  <li><code>first('noise').label == 'bell'</code></li>
   *  <li><code>labels('transcript_languages').includes('en')</code></li>
   *  <li><code>labels('participant_languages').includes('en')</code></li>
   *  <li><code>labels('noise').includes('bell')</code></li>
   *  <li><code>all('transcript_languages').length gt; 1</code></li>
   *  <li><code>all('participant_languages').length gt; 1</code></li>
   *  <li><code>all('transcript').length gt; 100</code></li>
   *  <li><code>annotators('transcript_rating').includes('Robert')</code></li>
   *  <li><code>!/Ada.+/.test(id) &amp;&amp; first('corpus').label == 'CC' &amp;&amp;
   * labels('participant').includes('Robert')</code></li> 
   * </ul>
   * @return The number of matching transcripts.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public int countMatchingTranscriptIds(String expression)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("countMatchingTranscriptIds");
      if (verbose) System.out.println("countMatchingTranscriptIds -> " + url);
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("expression", expression);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      return ((JsonNumber)response.getModel()).intValue();
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }    

  /**
   * <p>Gets a list of IDs of transcripts that match a particular pattern.
   * <p>The results can be exhaustive, by omitting pageLength and pageNumber, or they
   * can be a subset (a 'page') of results, by given pageLength and pageNumber values.</p>
   * <p>The order of the list can be specified.  If ommitted, the transcripts are listed in ID
   * order.</p> 
   * @param expression An expression that determines which transcripts match.
   * <p> The expression language is loosely based on JavaScript; expressions such as
   * the following can be used:
   *  <li><code>/Ada.+/.test(id)</code></li>
   *  <li><code>labels('participant').includes('Robert')</code></li>
   *  <li><code>('CC', 'IA', 'MU').includes(first('corpus').label)</code></li>
   *  <li><code>first('episode').label == 'Ada Aitcheson'</code></li>
   *  <li><code>first('transcript_scribe').label == 'Robert'</code></li>
   *  <li><code>first('participant_languages').label == 'en'</code></li>
   *  <li><code>first('noise').label == 'bell'</code></li>
   *  <li><code>labels('transcript_languages').includes('en')</code></li>
   *  <li><code>labels('participant_languages').includes('en')</code></li>
   *  <li><code>labels('noise').includes('bell')</code></li>
   *  <li><code>all('transcript_languages').length gt; 1</code></li>
   *  <li><code>all('participant_languages').length gt; 1</code></li>
   *  <li><code>all('transcript').length gt; 100</code></li>
   *  <li><code>annotators('transcript_rating').includes('Robert')</code></li>
   *  <li><code>!/Ada.+/.test(id) &amp;&amp; first('corpus').label == 'CC' &amp;&amp;
   * labels('participant').includes('Robert')</code></li> 
   * </ul>
   * @param pageLength The maximum number of IDs to return, or null to return all.
   * @param pageNumber The zero-based page number to return, or null to return the first page.
   * @param order The ordering for the list of IDs, a string containing a comma-separated list of
   * expressions, which may be appended by " ASC" or " DESC", or null for transcript ID order. 
   * @return A list of transcript IDs.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public String[] getMatchingTranscriptIds(
    String expression, Integer pageLength, Integer pageNumber, String order)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getMatchingTranscriptIds");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("expression", expression);
      if (pageLength != null) request.setParameter("pageLength", pageLength);
      if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
      if (verbose) System.out.println("getMatchingTranscriptIds -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<String> ids = new Vector<String>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          ids.add(array.getString(i));
        }
      }
      return ids.toArray(new String[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 

  /**
   * Counts the number of annotations that match a particular pattern.
   * @param expression An expression that determines which participants match.
   * <p> The expression language is loosely based on JavaScript; expressions such as
   * the following can be used:
   * <ul>
   *  <li><code>id == 'ew_0_456'</code></li>
   *  <li><code>!/th[aeiou].&#47;/.test(label)</code></li>
   *  <li><code>first('participant').label == 'Robert' &amp;&amp; first('utterances').start.offset ==
   * 12.345</code></li> 
   *  <li><code>graph.id == 'AdaAicheson-01.trs' &amp;&amp; layer.id == 'orthography'
   * &amp;&amp; start.offset &gt; 10.5</code></li> 
   *  <li><code>previous.id == 'ew_0_456'</code></li>
   * </ul>
   * <p><em>NB</em> all expressions must match by either id or layer.id.
   * @return The number of matching annotations.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public int countMatchingAnnotations(String expression)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("countMatchingAnnotations");
      if (verbose) System.out.println("countMatchingAnnotations -> " + url);
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("expression", expression);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      return ((JsonNumber)response.getModel()).intValue();
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 

  /**
   * Gets a list of annotations that match a particular pattern.
   * @param expression An expression that determines which transcripts match.
   * <p> The expression language is loosely based on JavaScript; expressions such as the
   * following can be used: 
   * <ul>
   *  <li><code>id == 'ew_0_456'</code></li>
   *  <li><code>!/th[aeiou].&#47;/.test(label)</code></li>
   *  <li><code>first('participant').label == 'Robert' &amp;&amp; first('utterances').start.offset ==
   * 12.345</code></li> 
   *  <li><code>graph.id == 'AdaAicheson-01.trs' &amp;&amp; layer.id == 'orthography'
   * &amp;&amp; start.offset 
   * &gt; 10.5</code></li> 
   *  <li><code>previous.id == 'ew_0_456'</code></li>
   * </ul>
   * <p><em>NB</em> all expressions must match by either id or layer.id.
   * @param pageLength The maximum number of annotations to return, or null to return all.
   * @param pageNumber The zero-based page number to return, or null to return the first page.
   * @return A list of matching {@link Annotation}s.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public Annotation[] getMatchingAnnotations(
    String expression, Integer pageLength, Integer pageNumber)
    throws StoreException, PermissionException {
      
    try {
      URL url = url("getMatchingAnnotations");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("expression", expression);
      if (pageLength != null) request.setParameter("pageLength", pageLength);
      if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
      if (verbose) System.out.println("getMatchingTranscriptIds -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<Annotation> annotations = new Vector<Annotation>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          annotations.add((Annotation)new Annotation().fromJson(array.getJsonObject(i)));
        }
      }
      return annotations.toArray(new Annotation[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } 

  /**
   * Gets the number of annotations on the given layer of the given transcript.
   * @param id The ID of the transcript.
   * @param layerId The ID of the layer.
   * @return A (possibly empty) array of annotations.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public long countAnnotations(String id, String layerId)
    throws StoreException, PermissionException, GraphNotFoundException {
      
    try {
      URL url = url("countAnnotations");
      if (verbose) System.out.println("countAnnotations -> " + url);
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id)
        .setParameter("layerId", layerId);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      return ((JsonNumber)response.getModel()).longValue();
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * Gets the annotations on the given layer of the given transcript.
   * @param id The ID of the transcript.
   * @param layerId The ID of the layer.
   * @param pageLength The maximum number of IDs to return, or null to return all.
   * @param pageNumber The zero-based page number to return, or null to return the first page.
   * @return A (possibly empty) array of annotations.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public Annotation[] getAnnotations(
    String id, String layerId, Integer pageLength, Integer pageNumber)
    throws StoreException, PermissionException, GraphNotFoundException {
      
    try {
      URL url = url("getAnnotations");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id)
        .setParameter("layerId", layerId);
      if (pageLength != null) request.setParameter("pageLength", pageLength);
      if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
      if (verbose) System.out.println("getAnnotations -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<Annotation> annotations = new Vector<Annotation>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          annotations.add((Annotation)new Annotation().fromJson(array.getJsonObject(i)));
        }
      }
      return annotations.toArray(new Annotation[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
   
  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets the annotations on given layers for a set of match IDs.
   * @param matchIds An iterator that supplies match IDs - these may be the contents of
   * the MatchId column in exported search results, token URLs, or annotation IDs. 
   * @param layerIds The layer IDs of the layers to get.
   * @param targetOffset Which token to get the annotations of;  0 means the match target
   * itself, 1 means the token after the target, -1 means the token before the target, etc. 
   * @param annotationsPerLayer The number of annotations per layer to get; if there's a
   * smaller number of annotations available, the unfilled array elements will be null.
   * @param consumer A consumer for handling the resulting
   * annotations. Consumer.accept() will be invoked once for each element returned by the
   * <var>matchIds</var> iterator, with an array of {@link Annotation} objects. The size
   * of this array will be <var>layerIds.length</var> * <var>annotationsPerLayer</var>,
   * and will be filled in with the available annotations for each layer; when
   * annotations are not available, null is supplied.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   */
  public void getMatchAnnotations(
    Iterator<String> matchIds, String[] layerIds, int targetOffset, int annotationsPerLayer,
    Consumer<Annotation[]> consumer)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }
   
  /**
   * Gets the given anchors in the given transcript.
   * @param id The ID of the transcript.
   * @param anchorIds A list of anchor IDs.
   * @return A (possibly empty) array of anchors.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public Anchor[] getAnchors(String id, String[] anchorIds)
    throws StoreException, PermissionException, GraphNotFoundException {
      
    try {
      URL url = url("getAnchors");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id)
        .setParameter("anchorIds", anchorIds);
      if (verbose) System.out.println("getAnchors -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<Anchor> anchors = new Vector<Anchor>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          anchors.add(new Anchor(array.getJsonObject(i)));
        }
      }
      return anchors.toArray(new Anchor[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets a transcript given its ID.
   * @param id The given transcript ID.
   * @return The identified transcript.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public Graph getTranscript(String id) 
    throws StoreException, PermissionException, GraphNotFoundException {
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets a transcript given its ID, containing only the given layers.
   * @param id The given transcript ID.
   * @param layerIds The IDs of the layers to load, or null if only transcript data is required.
   * @return The identified transcript.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public Graph getTranscript(String id, String[] layerIds) 
    throws StoreException, PermissionException, GraphNotFoundException {
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets a fragment of a transcript, given its ID and the ID of an annotation in it that defines the
   * desired fragment.
   * @param transcriptId The ID of the transcript.
   * @param annotationId The ID of an annotation that defines the bounds of the fragment.
   * @return The identified transcript fragment.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public Graph getFragment(String transcriptId, String annotationId) 
    throws StoreException, PermissionException, GraphNotFoundException {
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets a fragment of a graph, given its ID and the ID of an annotation in it that defines the 
   * desired fragment, and containing only the given layers.
   * @param transcriptId The ID of the transcript.
   * @param annotationId The ID of an annotation that defines the bounds of the fragment.
   * @param layerIds The IDs of the layers to load, or null if only transcript data is required.
   * @return The identified transcript fragment.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public Graph getFragment(String transcriptId, String annotationId, String[] layerIds) 
    throws StoreException, PermissionException, GraphNotFoundException {
    throw new StoreException("Not implemented");
  }
   
  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets a fragment of a transcript, given its ID and the start/end offsets that define the
   * desired fragment, and containing only the given layers.
   * @param transcriptId The ID of the transcript.
   * @param start The start offset of the fragment.
   * @param end The end offset of the fragment.
   * @param layerIds The IDs of the layers to load, or null if only transcript data is required.
   * @return The identified transcript fragment.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public Graph getFragment(String transcriptId, double start, double end, String[] layerIds) 
    throws StoreException, PermissionException, GraphNotFoundException {
    throw new StoreException("Not implemented");
  }
   
  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets a series of fragments, given the series' ID, and only the given layers.
   * @param seriesId The ID of the series.
   * @param layerIds The IDs of the layers to load, or null if only transcript data is required.
   * @return An enumeratable series of fragments.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public MonitorableSeries<Graph> getFragmentSeries(String seriesId, String[] layerIds) 
    throws StoreException, PermissionException, GraphNotFoundException {
    throw new StoreException("Not implemented");
  }
   
  /**
   * List the predefined media tracks available for transcripts.
   * @return An ordered list of media track definitions.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted. 
   */
  public MediaTrackDefinition[] getMediaTracks() 
    throws StoreException, PermissionException {
    try {
      URL url = url("getMediaTracks");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getMediaTracks -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<MediaTrackDefinition> tracks = new Vector<MediaTrackDefinition>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          tracks.add(new MediaTrackDefinition(array.getJsonObject(i)));
        }
      }
      return tracks.toArray(new MediaTrackDefinition[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
   
  /**
   * List the media available for the given transcript.
   * @param id The transcript ID.
   * @return List of media files available for the given transcript.
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public MediaFile[] getAvailableMedia(String id) 
    throws StoreException, PermissionException, GraphNotFoundException {
      
    try {
      URL url = url("getAvailableMedia");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id);
      if (verbose) System.out.println("getAvailableMedia -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<MediaFile> files = new Vector<MediaFile>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          files.add(new MediaFile(array.getJsonObject(i)));
        }
      }
      return files.toArray(new MediaFile[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * Gets a given media track for a given transcript.
   * @param id The transcript ID.
   * @param trackSuffix The track suffix of the media - see {@link MediaTrackDefinition#suffix}.
   * @param mimeType The MIME type of the media, which may include parameters for type
   * conversion, e.g. "text/wav; samplerate=16000".
   * @return A URL to the given media for the given transcript, or null if the given media doesn't
   * exist. 
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public String getMedia(String id, String trackSuffix, String mimeType) 
    throws StoreException, PermissionException, GraphNotFoundException {
      
    try {
      URL url = url("getMedia");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id)
        .setParameter("trackSuffix", trackSuffix)
        .setParameter("mimeType", mimeType);
      if (verbose) System.out.println("getMedia -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return response.getModel().toString();
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * Gets a given media track for a given transcript.
   * @param id The transcript ID.
   * @param trackSuffix The track suffix of the media - see {@link MediaTrackDefinition#suffix}.
   * @param mimeType The MIME type of the media, which may include parameters for type
   * conversion, e.g. "text/wav; samplerate=16000"
   * @param startOffset The start offset of the media sample, or null for the start of the whole
   * recording. 
   * @param endOffset The end offset of the media sample, or null for the end of the whole
   * recording. 
   * @return A URL to the given media for the given transcript, or null if the given media doesn't
   * exist. 
   * @throws StoreException If an error occurs.
   * @throws PermissionException If the operation is not permitted.
   * @throws GraphNotFoundException If the transcript was not found in the store.
   */
  public String getMedia(
    String id, String trackSuffix, String mimeType, Double startOffset, Double endOffset) 
    throws StoreException, PermissionException, GraphNotFoundException {
      
    try {
      URL url = url("getMedia");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id)
        .setParameter("trackSuffix", trackSuffix)
        .setParameter("mimeType", mimeType)
        .setParameter("startOffset", startOffset)
        .setParameter("endOffset", endOffset);
      if (verbose) System.out.println("getMedia -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      return response.getModel().toString();
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * Get a list of documents associated with the episode of the given transcript.
   * @param id The transcript ID.
   * @return List of URLs to documents.
   * @throws StoreException If an error prevents the media from being saved.
   * @throws PermissionException If saving the media is not permitted.
   * @throws GraphNotFoundException If the transcript doesn't exist.
   */
  public MediaFile[] getEpisodeDocuments(String id)
    throws StoreException, PermissionException, GraphNotFoundException {
      
    try {
      URL url = url("getEpisodeDocuments");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json")
        .setParameter("id", id);
      if (verbose) System.out.println("getEpisodeDocuments -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<MediaFile> files = new Vector<MediaFile>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          files.add(new MediaFile(array.getJsonObject(i)));
        }
      }
      return files.toArray(new MediaFile[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
   
  // Other methods:

  /**
   * Gets the current state of the given task.
   * @param threadId The ID of the task.
   * @return The status of the task
   * @throws IOException
   * @throws ResponseException
   */
  public TaskStatus taskStatus(String threadId)
    throws IOException, StoreException {
      
    cancelling = false;
    URL url = makeUrl("thread");
    HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "application/json")
      .setParameter("threadId", threadId);
    if (verbose) System.out.println("taskStatus -> " + request);
    response = new Response(request.get(), verbose);
    response.checkForErrors(); // throws a ResponseException on error
    if (response.isModelNull()) return null;
    return new TaskStatus((JsonObject)response.getModel());
  } // end of taskStatus()
   
  /**
   * Wait for the given task to finish.
   * @param threadId
   * @param maxSeconds The maximum time to wait for the task, or 0 for forever.
   * @return The final task status.
   * @throws IOException
   * @throws StoreException
   */
  public TaskStatus waitForTask(String threadId, int maxSeconds)
    throws IOException, StoreException {
      
    cancelling = false;
    TaskStatus status = taskStatus(threadId);
      
    long endTime = 0;
    if (maxSeconds > 0) endTime = new Date().getTime() + (maxSeconds * 1000);
      
    while (status.getRunning() && !cancelling) {
      long ms = status.getRefreshSeconds() * 1000;
      if (ms <= 0) ms = 2000;
      try { Thread.sleep(ms); } catch(Exception exception) {}
         
      if (endTime > 0 && new Date().getTime() > endTime) { // is time up?
        cancelling = true;
      }
         
      if (!cancelling) { // are we stopping now?
        status = taskStatus(threadId);
      }
    } // loop
    return status;
  } // end of waitForTask()
   
  /**
   * Cancels a running task.
   * @param threadId The ID of the task.
   * @throws IOException
   * @throws StoreException
   */
  public void cancelTask(String threadId) throws IOException, StoreException {
      
    cancelling = false;
    URL url = makeUrl("threads");
    HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "application/json")
      .setParameter("threadId", threadId)
      .setParameter("command", "cancel");
    if (verbose) System.out.println("taskStatus -> " + request);
    response = new Response(request.get(), verbose);
    response.checkForErrors(); // throws a ResponseException on error
  } // end of cancelTask()

  /**
   * Release a finished task, to free up server resources.
   * @param threadId The ID of the task.
   * @throws IOException
   * @throws StoreException
   */
  public void releaseTask(String threadId) throws IOException, StoreException {
      
    cancelling = false;
    URL url = makeUrl("threads");
    HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "application/json")
      .setParameter("threadId", threadId)
      .setParameter("command", "release");
    if (verbose) System.out.println("taskStatus -> " + request);
    response = new Response(request.get(), verbose);
    response.checkForErrors(); // throws a ResponseException on error
  } // end of releaseTask()

  /**
   * Gets a list of all tasks on the server.
   * @return A list of all task statuses.
   * @throws IOException
   * @throws StoreException
   */
  public Map<String,TaskStatus> getTasks() throws IOException, StoreException {
      
    cancelling = false;
    URL url = makeUrl("threads");
    HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "application/json");
    if (verbose) System.out.println("getTasks -> " + request);
    response = new Response(request.get(), verbose);
    response.checkForErrors(); // throws a ResponseException on error
    if (response.isModelNull()) return null;
    JsonObject model = (JsonObject)response.getModel();
    HashMap<String,TaskStatus> result = new HashMap<String,TaskStatus>();
    for (String threadId : model.keySet()) {
      result.put(threadId,
                 new TaskStatus(model.getJsonObject(threadId)));
    } // next task
    return result;
  } // end of getTasks()
   
  boolean cancelling = false;
  /**
   * Cancel the current request, if possible.
   */
  public void cancel() {
      
    cancelling = true;
    if (postRequest != null)
    {
      postRequest.cancel();
    }
  } // end of cancel()

  /**
   * Determines whether or not the request is being cancelled.
   * @return true, if the last request has been asked to cancel, false otherwise
   */
  public boolean isCancelling() {
      
    if (postRequest == null)
    {
      return cancelling;
    }
    else
    {
      return postRequest.isCancelling();
    }
  } // end of isCancelling()
   
  /**
   * Searches for tokens that match the given pattern.
   * <p> The <var>pattern</var> must match the structure of the search matrix in the
   * browser interface of LaBB-CAT. This is a JSON object with one attribute called
   * <q>columns</q>, which is an array of JSON objects.
   * <p>Each element in the <q>columns</q> array contains a JSON object named
   * <q>layers</q>, whose value is a JSON object for patterns to match on each layer, and
   * optionally an element named <q>adj</q>, whose value is a number representing the
   * maximum distance, in tokens, between this column and the next column - if <q>adj</q>
   * is not specified, the value defaults to 1, so tokens are contiguous.
   * Each element in the <q>layers</q> JSON object is named after the layer it matches, and
   * the value is a named list with the following possible attributes:
   * <dl>
   *  <dt>pattern</dt> <dd>A regular expression to match against the label</dd>
   *  <dt>min</dt> <dd>An inclusive minimum numeric value for the label</dd>
   *  <dt>max</dt> <dd>An exclusive maximum numeric value for the label</dd>
   *  <dt>not</dt> <dd>TRUE to negate the match</dd>
   *  <dt>anchorStart</dt> <dd>TRUE to anchor to the start of the annotation on this layer
   *     (i.e. the matching word token will be the first at/after the start of the matching
   *     annotation on this layer)</dd>
   *  <dt>anchorEnd</dt> <dd>TRUE to anchor to the end of the annotation on this layer
   *     (i.e. the matching word token will be the last before/at the end of the matching
   *     annotation on this layer)</dd>
   *  <dt>target</dt> <dd>TRUE to make this layer the target of the search; the results will
   *     contain one row for each match on the target layer</dd>
   * </dl>
   *
   * <p>Examples of valid pattern objects include:
   * <pre>// words starting with 'ps...'
   *  JsonObject pattern = new JsonObject()
   *     .put("columns", new JsonArray()
   *          .put(new JsonObject()
   *               .put("layers", new JsonObject()
   *                    .put("orthography", new JsonObject()
   *                         .put("pattern", "ps.*")))));
   * 
   * // the word 'the' followed immediately or with one intervening word by
   * // a hapax legomenon (word with a frequency of 1) that doesn't start with a vowel
   * JsonObject pattern2 = new JsonObject()
   *    .put("columns", new JsonArray()
   *         .put(new JsonObject()
   *              .put("layers", new JsonObject()
   *                   .put("orthography", new JsonObject()
   *                        .put("pattern", "the"))),
   *              .put("adj", 2)),
   *         .put(new JsonObject()
   *              .put("layers", new JsonObject()
   *                   .put("phonemes", new JsonObject()
   *                        .put("not", Boolean.TRUE)
   *                        .put("pattern","[cCEFHiIPqQuUV0123456789~#\\$@].*")),
   *                   .put("frequency", new JsonObject()
   *                        .put("max", "2")))));
   * </pre>
   * <p>The PatternBuilder class is designed to make constructing valid patterns easier:
   * <pre> // words starting with 'ps...'
   * JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", "ps.*").build();
   * 
   * // the word 'the' followed immediately or with one intervening word by
   * // a hapax legomenon (word with a frequency of 1) that doesn't start with a vowel
   * JsonObject pattern2 = new PatternBuilder()
   *    .addColumn()
   *    .addMatchLayer("orthography", "the")
   *    .addColumn()
   *    .addNotMatchLayer("phonemes", "[cCEFHiIPqQuUV0123456789~#\\$@].*")
   *    .addMaxLayer("frequency", 2)
   *    .build();
   * </pre>
   * @param pattern An object representing the pattern to search for, which mirrors the
   * Search Matrix in the browser interface.
   * @param participantIds An optional list of participant IDs to search the utterances
   * of. If null, all utterances in the corpus will be searched.
   * @param transcriptTypes An optional list of transcript types to limit the results
   * to. If null, all transcript types will be searched. 
   * @param mainParticipant true to search only main-participant utterances, false to
   * search all utterances. 
   * @param aligned true to include only words that are aligned (i.e. have anchor
   * confidence &ge; 50, false to search include un-aligned words as well. 
   * @param matchesPerTranscript Optional maximum number of matches per transcript to
   * return. <tt>null</tt> means all matches.
   * @param overlapThreshold Optional percentage overlap with other utterances before
   * simultaneous speech is excluded. <tt>null</tt> means include all overlapping utterances.
   * @return The threadId of the resulting task, which can be passed in to
   * {@link #getMatches(String,int)}, {@link #taskStatus(String)},
   * {@link #waitForTask(String,int)}, etc.
   * @see #getMatches(String,int)
   * @see PatternBuilder
   * @throws IOException
   * @throws StoreException
   */
  public String search(
    JsonObject pattern, String[] participantIds, String[] transcriptTypes,
    boolean mainParticipant, boolean aligned, Integer matchesPerTranscript,
    Integer overlapThreshold)
    throws IOException, StoreException {
      
    cancelling = false;
    if (pattern == null) throw new StoreException("No pattern specified.");
    URL url = makeUrl("search");
    HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "application/json")
      .setParameter("command", "search")
      .setParameter("searchJson", pattern.toString())
      .setParameter("words_context", 0);
    if (mainParticipant) {
      request.setParameter("only_main_speaker", true);
    }
    if (aligned) {
      request.setParameter("only_aligned", true);
    }
    if (matchesPerTranscript != null) {
      request.setParameter("matches_per_transcript", matchesPerTranscript);
    }
    if (participantIds != null) {
      request.setParameter("participant_id", participantIds);
    }
    if (transcriptTypes != null) {
      request.setParameter("transcript_type", transcriptTypes);
    }
    if (overlapThreshold != null) {
      request.setParameter("overlap_threshold", overlapThreshold);
    }
      
    if (verbose) System.out.println("search -> " + request);
    response = new Response(request.get(), verbose);
    response.checkForErrors(); // throws a ResponseException on error
      
    // extract the threadId from model.threadId
    JsonObject model = (JsonObject)response.getModel();
    return model.getString("threadId");
  } // end of search()
   
  /**
   * Gets a list of tokens that were matched by
   * {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}.
   * <p>If the task is still running, then this function will wait for it to finish.
   * <p>This means calls can be stacked like this:
   *  <pre>Matches[] matches = labbcat.getMatches(
   *     labbcat.search(
   *        new PatternBuilder().addMatchLayer("orthography", "and").build(),
   *        participantIds, true, true, null, null), 1);</pre>
   * @param threadId A task ID returned by
   * {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}.
   * @param wordsContext Number of words context to include in the <q>Before Match</q>
   * and <q>After Match</q> columns in the results.
   * @return A list of IDs that can be used to identify utterances/tokens that were matched by
   * {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}, or null if
   * the task was cancelled. 
   * @throws IOException
   * @throws StoreException
   * @see #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}
   */
  public Match[] getMatches(String threadId, int wordsContext)
    throws IOException, StoreException {      
    return getMatches(threadId, wordsContext, null, null);
  } // end of getMatchIds()

  /**
   * Gets a list of tokens that were matched by
   * {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}.
   * <p>If the task is still running, then this function will wait for it to finish.
   * <p>This means calls can be stacked like this:
   *  <pre>Matches[] matches = labbcat.getMatches(
   *     labbcat.search(
   *        new PatternBuilder().addMatchLayer("orthography", "and").build(),
   *        participantIds, true, false, null, 5), 1);</pre>
   * @param threadId A task ID returned by 
   * {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}.
   * @param wordsContext Number of words context to include in the <q>Before Match</q>
   * and <q>After Match</q> columns in the results.
   * @param pageLength The maximum number of matches to return, or null to return all.
   * @param pageNumber The zero-based page number to return, or null to return the first page.
   * @return A list of IDs that can be used to identify utterances/tokens that were matched by
   * {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}, or null if
   * the task was cancelled. 
   * @throws IOException
   * @throws StoreException
   * @see #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}
   */
  public Match[] getMatches(
    String threadId, int wordsContext, Integer pageLength, Integer pageNumber)
    throws IOException, StoreException {
      
    // ensure it's finished
    waitForTask(threadId, 0);
    if (cancelling == true) return null;
      
    cancelling = false;
    URL url = makeUrl("resultsStream");
    HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "application/json")
      .setParameter("threadId", threadId)
      .setParameter("words_context", wordsContext);
    if (pageLength != null) request.setParameter("pageLength", pageLength);
    if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
    if (verbose) System.out.println("getMatches -> " + request);
    response = new Response(request.get(), verbose);
    response.checkForErrors(); // throws a ResponseException on error
      
    // extract the MatchIds from model
    JsonObject model = (JsonObject)response.getModel();
    JsonArray array = model.getJsonArray("matches");
    Vector<Match> matches = new Vector<Match>();
    if (array != null)
    {
      for (int i = 0; i < array.size(); i++)
      {
        matches.add(new Match(array.getJsonObject(i)));
      }
    }
      
    return matches.toArray(new Match[0]);
  } // end of getMatchIds()

  /**
   * Searches for tokens that match the givem pattern and returns a list of matches.
   * <p>This is similar to invoking:
   * <pre> Matches[] matches = labbcat.getMatches(
   *     labbcat.search(pattern, participantIds, mainParticipant, aligned,
   *                    matchesPerTranscript, overlapThreshold),
   *     wordsContext);</pre>
   * <p>As with {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)} the
   * <var>pattern</var> must 
   * match the structure of the search matrix in the browser interface of LaBB-CAT.
   * <p>The PatternBuilder class is designed to make constructing valid patterns easier:
   * <pre> // words starting with 'ps...'
   * JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", "ps.*").build();
   * 
   * // the word 'the' followed immediately or with one intervening word by
   * // a hapax legomenon (word with a frequency of 1) that doesn't start with a vowel
   * JsonObject pattern2 = new PatternBuilder()
   *    .addColumn()
   *    .addMatchLayer("orthography", "the")
   *    .addColumn()
   *    .addNotMatchLayer("phonemes", "[cCEFHiIPqQuUV0123456789~#\\$@].*")
   *    .addMaxLayer("frequency", 2)
   *    .build();
   * </pre>
   * @param pattern An object representing the pattern to search for, which mirrors the
   * Search Matrix in the browser interface.
   * @param participantIds An optional list of participant IDs to search the utterances
   * of. If not null, all utterances in the corpus will be searched.
   * @param transcriptTypes An optional list of transcript types to limit the results
   * to. If null, all transcript types will be searched. 
   * @param mainParticipant true to search only main-participant utterances, false to
   * search all utterances. 
   * @param aligned true to include only words that are aligned (i.e. have anchor
   * confidence &ge; 50, false to search include un-aligned words as well. 
   * @param matchesPerTranscript Optional maximum number of matches per transcript to
   * return. <tt>null</tt> means all matches.
   * @param overlapThreshold Optional percentage overlap with other utterances before
   * simultaneous speech is excluded. <tt>null</tt> means include all overlapping utterances.
   * @param wordsContext Number of words context to include in the <q>Before Match</q>
   * and <q>After Match</q> columns in the results.
   * @return A list of IDs that can be used to identify utterances/tokens that were matched by
   * {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}, or null if
   * the task was cancelled. 
   * @throws IOException
   * @throws StoreException
   * @see #getMatches(String,int)}
   */
  public Match[] getMatches(
    JsonObject pattern, String[] participantIds, String[] transcriptTypes,
    boolean mainParticipant, boolean aligned, Integer matchesPerTranscript,
    Integer overlapThreshold, int wordsContext)
    throws IOException, StoreException {
      
    String threadId = search(
      pattern, participantIds, transcriptTypes, mainParticipant, aligned, matchesPerTranscript,
      overlapThreshold);
    try {
      return  getMatches(threadId, wordsContext);
    } finally { // release the task to save server resources
      try { releaseTask(threadId); } catch(Exception exception) {}
    }
  }

  /**
   * Searches for tokens that match the given pattern and returns the first
   * <var>maxMatches</var> matches. 
   * <p>This is similar to invoking:
   * <pre> Matches[] matches = labbcat.getMatches(
   *     labbcat.search(pattern, participantIds, mainParticipant, aligned,
   *                    matchesPerTranscript, overlapThreshold), 
   *     wordsContext);</pre>
   * <p>As with {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)} the
   * <var>pattern</var> must 
   * match the structure of the search matrix in the browser interface of LaBB-CAT.
   * <p>The PatternBuilder class is designed to make constructing valid patterns easier:
   * <pre> // words starting with 'ps...'
   * JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", "ps.*").build();
   * 
   * // the word 'the' followed immediately or with one intervening word by
   * // a hapax legomenon (word with a frequency of 1) that doesn't start with a vowel
   * JsonObject pattern2 = new PatternBuilder()
   *    .addColumn()
   *    .addMatchLayer("orthography", "the")
   *    .addColumn()
   *    .addNotMatchLayer("phonemes", "[cCEFHiIPqQuUV0123456789~#\\$@].*")
   *    .addMaxLayer("frequency", 2)
   *    .build();
   * </pre>
   * @param pattern An object representing the pattern to search for, which mirrors the
   * Search Matrix in the browser interface.
   * @param participantIds An optional list of participant IDs to search the utterances
   * of. If not null, all utterances in the corpus will be searched.
   * @param transcriptTypes An optional list of transcript types to limit the results
   * to. If null, all transcript types will be searched. 
   * @param mainParticipant true to search only main-participant utterances, false to
   * search all utterances. 
   * @param aligned true to include only words that are aligned (i.e. have anchor
   * confidence &ge; 50, false to search include un-aligned words as well. 
   * @param matchesPerTranscript Optional maximum number of matches per transcript to
   * return. <tt>null</tt> means all matches.
   * @param overlapThreshold Optional percentage overlap with other utterances before
   * simultaneous speech is excluded. <tt>null</tt> means include all overlapping utterances.
   * @param wordsContext Number of words context to include in the <q>Before Match</q>
   * and <q>After Match</q> columns in the results.
   * @param maxMatches The maximum number of matches to return, or null to return all.
   * @return A list of IDs that can be used to identify utterances/tokens that were matched by
   * {@link #search(JsonObject,String[],String[],boolean,boolean,Integer,Integer)}, or null if
   * the task was cancelled. 
   * @throws IOException
   * @throws StoreException
   * @see #getMatches(String,int)}
   */
  public Match[] getMatches(
    JsonObject pattern, String[] participantIds, String[] transcriptTypes,
    boolean mainParticipant, boolean aligned, Integer matchesPerTranscript,
    Integer overlapThreshold, int wordsContext, Integer maxMatches)
    throws IOException, StoreException {
      
    String threadId = search(
      pattern, participantIds, transcriptTypes, mainParticipant, aligned, matchesPerTranscript,
      overlapThreshold);
    try {
      return  getMatches(threadId, wordsContext, maxMatches, 0);
    } finally { // release the task to save server resources
      try { releaseTask(threadId); } catch(Exception exception) {}
    }
  }

  /**
   * Gets annotations on selected layers related to search results returned by a previous
   * call to {@link #getMatches(String,int)}.
   * @param matches A list of {@link Match}es. 
   * @param layerIds A vector of layer IDs.
   * @param targetOffset The distance from the original target of the match, e.g.
   * <ul>
   *  <li>0 - find annotations of the match target itself</li>
   *  <li>1 - find annotations of the token immediately <em>after</em> match target</li>
   *  <li>-1 - find annotations of the token immediately <em>before</em> match target</li>
   * </ul>
   * @param annotationsPerLayer The number of annotations on the given layer to
   * retrieve. In most cases, there's only one annotation available. However, tokens may,
   * for example, be annotated with `all possible phonemic transcriptions', in which case
   * using a value of greater than 1 for this parameter provides other phonemic
   * transcriptions, for tokens that have more than one.
   * @return An array of arrays of Annotations, of dimensions <var>matchIds</var>.length
   * &times; (<var>layerIds</var>.length * <var>annotationsPerLayer</var>). The first
   * index matches the corresponding index in <var>matchIds</var>. 
   * @throws IOException
   * @throws StoreException
   * @see #getMatches(String,int)}
   */
  public Annotation[][] getMatchAnnotations(
    Match[] matches, String[] layerIds, int targetOffset, int annotationsPerLayer)
    throws IOException, StoreException {
      
    String[] matchIds = new String[matches.length];
    for (int m = 0; m < matches.length; m++) matchIds[m] = matches[m].getMatchId();
    return getMatchAnnotations(matchIds, layerIds, targetOffset, annotationsPerLayer);
  }
   
  /**
   * Gets annotations on selected layers related to search results returned by a previous
   * call to {@link #getMatches(String,int)}, {@link #taskStatus(String)}.
   * @param matchIds A list of {@link Match#getMatchId()}s. 
   * @param layerIds A vector of layer IDs.
   * @param targetOffset The distance from the original target of the match, e.g.
   * <ul>
   *  <li>0 - find annotations of the match target itself</li>
   *  <li>1 - find annotations of the token immediately <em>after</em> match target</li>
   *  <li>-1 - find annotations of the token immediately <em>before</em> match target</li>
   * </ul>
   * @param annotationsPerLayer The number of annotations on the given layer to
   * retrieve. In most cases, there's only one annotation available. However, tokens may,
   * for example, be annotated with `all possible phonemic transcriptions', in which case
   * using a value of greater than 1 for this parameter provides other phonemic
   * transcriptions, for tokens that have more than one.
   * @return An array of arrays of Annotations, of dimensions <var>matchIds</var>.length
   * &times; (<var>layerIds</var>.length * <var>annotationsPerLayer</var>). The first
   * index matches the corresponding index in <var>matchIds</var>. 
   * @throws IOException
   * @throws StoreException
   * @see #getMatches(String,int)}
   */
  public Annotation[][] getMatchAnnotations(
    String[] matchIds, String[] layerIds, int targetOffset, int annotationsPerLayer)
    throws IOException, StoreException {
      
    cancelling = false;

    // write the IDs to a temporary file for upload
    File csvUpload = File.createTempFile("getMatchAnnotations_",".csv");
    try {
      csvUpload.deleteOnExit();
      PrintWriter csvOut = new PrintWriter(csvUpload, "UTF-8");
      csvOut.println("MatchId");
      for (String matchId : matchIds) csvOut.println(matchId);
      csvOut.close();
      if (verbose) System.out.println("matchIds written to: " + csvUpload.getPath());
         
      URL url = makeUrl("api/getMatchAnnotations");
      postRequest = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "application/json")
        .setParameter("layer", layerIds)
        .setParameter("targetOffset", targetOffset)
        .setParameter("annotationsPerLayer", annotationsPerLayer)
        .setParameter("csvFieldDelimiter", ",")
        .setParameter("targetColumn", 0)
        .setParameter("copyColumns", false)
        .setParameter("uploadfile", csvUpload);
      if (verbose) System.out.println("getMatchAnnotations -> " + postRequest);
      response = new Response(postRequest.post(), verbose);
      response.checkForErrors(); // throws a ResponseException on error
         
      // extract the MatchIds from model
      JsonArray model = (JsonArray)response.getModel();
      int annotationsPerMatch = layerIds.length*annotationsPerLayer;
      Annotation[][] result = new Annotation[matchIds.length][annotationsPerMatch];
      for (int m = 0; m < matchIds.length; m++) {
        JsonArray annotations = model.getJsonArray(m);
        for (int a = 0; a < annotationsPerMatch; a++)
        {
          Annotation annotation = null;
          if (!annotations.isNull(a))
          {
            annotation = (Annotation)new Annotation().fromJson(annotations.getJsonObject(a));
          }
          result[m][a] = annotation;
        } // next annotation
      } // next match
      return result;
    } finally {
      // delete temporary file
      csvUpload.delete();
    }
  } // end of getMatchIds()

  /**
   * Downloads WAV sound fragments.
   * <p> This utility method translates a {@link Match} array of the kind returned by 
   * {@link #getMatches(String,int)} to the parallel arrays required by
   * {@link #getSoundFragments(String[],Double[],Double[],Integer,File)}, using {@link MatchId}.
   * @param matches A list of {@link Match}es, perhaps returned by
   * {@link #getMatches(String,int)}. 
   * @param sampleRate The desired sample rate, or null for no preference.
   * @param dir A directory in which the files should be stored, or null for a temporary
   * folder.  If specified, and the directory doesn't exist, it will be created. 
   * @return A list of WAV files. If <var>dir</var> is null, these files will be stored
   * under the system's temporary directory, so once processing is finished, they should
   * be deleted by the caller, or moved to a more permanent location. 
   * @throws IOException
   * @throws StoreException
   */
  public File[] getSoundFragments(Match[] matches, Integer sampleRate, File dir)
    throws IOException, StoreException {

    // convert matches into three parallel arrays of IDs/offsets
    String[] transcriptIds = new String[matches.length];
    Double[] startOffsets = new Double[matches.length];
    Double[] endOffsets = new Double[matches.length];

    for (int i = 0; i < matches.length; i++) {
      transcriptIds[i] = matches[i].getTranscript();
      startOffsets[i] = matches[i].getLine();
      endOffsets[i] = matches[i].getLineEnd();
    } // next match
      
    return getSoundFragments(transcriptIds, startOffsets, endOffsets, sampleRate, dir);      
  }
   
  /**
   * Downloads WAV sound fragments.
   * @param transcriptIds A list of transcript IDs (transcript names).
   * @param startOffsets A list of start offsets, with one element for each element in
   * <var>transcriptIds</var>. 
   * @param endOffsets A list of end offsets, with one element for each element in
   * <var>transcriptIds</var>. 
   * @param sampleRate The desired sample rate, or null for no preference.
   * @param dir A directory in which the files should be stored, or null for a temporary
   * folder.  If specified, and the directory doesn't exist, it will be created. 
   * @return A list of WAV files. If <var>dir</var> is null, these files will be stored
   * under the system's temporary directory, so once processing is finished, they should
   * be deleted by the caller, or moved to a more permanent location. 
   * @throws IOException
   * @throws StoreException
   */
  public File[] getSoundFragments(
    String[] transcriptIds, Double[] startOffsets, Double[] endOffsets, Integer sampleRate,
    File dir)
    throws IOException, StoreException {
      
    if (transcriptIds.length != startOffsets.length || transcriptIds.length != endOffsets.length) {
      throw new StoreException(
        "transcriptIds ("+transcriptIds.length +"), startOffsets ("+startOffsets.length
        +"), and endOffsets ("+endOffsets.length+") must be arrays of equal size.");
    }
    File[] fragments = new File[transcriptIds.length];
      
    boolean tempFiles = false;
    if (dir == null) {
      dir = File.createTempFile("getSoundFragments_", "_wav");
      dir.delete();
      dir.mkdir();
      dir.deleteOnExit();
      tempFiles = true;
    } else {
      if (!dir.exists()) Files.createDirectory(dir.toPath());
    }

    // loop through each triple, getting fragments individually
    for (int i = 0; i < transcriptIds.length; i++) {
      if (cancelling) break;
      if (transcriptIds[i] == null || startOffsets[i] == null || endOffsets[i] == null) continue;

      URL url = makeUrl("soundfragment");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "audio/wav")
        .setParameter("id", transcriptIds[i])
        .setParameter("start", startOffsets[i])
        .setParameter("end", endOffsets[i]);
      if (sampleRate != null) request.setParameter("sampleRate", sampleRate);
      if (verbose) System.out.println("getSoundFragments -> " + request);
      HttpURLConnection connection = request.get();
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {
          System.err.println(
            "getSoundFragments: Error " + connection.getResponseCode()
            + " " + connection.getResponseMessage() + " - " + request);
        } 
        continue;
      } else {
        // use the name given by the server, if any
        String contentDisposition = connection.getHeaderField("content-disposition");
        if (contentDisposition != null) {
          // something like attachment; filename=blah.wav
          int equals = contentDisposition.indexOf("=");
          if (equals > 0) {
            String fileName = contentDisposition.substring(equals + 1);
            if (fileName.length() > 0) fragments[i] = new File(dir, fileName);
          }
        }
        if (fragments[i] == null) { // no name was suggested
          // invent a name
          fragments[i] = new File(
            dir, Graph.FragmentId(transcriptIds[i], startOffsets[i], endOffsets[i]) + ".wav");
        }
        if (tempFiles) fragments[i].deleteOnExit();
        IO.SaveUrlConnectionToFile(connection, fragments[i]);           
      } // response ok
    } // next triple

    return fragments;
  } // end of getSoundFragments()

  /**
   * Get transcript fragments in a specified format.
   * <p> This utility method translates a {@link Match} array of the kind returned by 
   * {@link #getMatches(String,int)} to the parallel arrays required by
   * {@link #getFragments(String[],Double[],Double[],String[],String,File)}, 
   * using {@link MatchId}.
   * @param matches A list of {@link Match}es, perhaps returned by
   * {@link #getMatches(String,int)}. 
   * @param layerIds A list of IDs of annotation layers to include in the fragment.
   * @param mimeType The desired format, for example "text/praat-textgrid" for Praat
   * TextGrids, "text/plain" for plain text, etc.
   * @param dir A directory in which the files should be stored, or null for a temporary
   * folder.  If specified, and the directory doesn't exist, it will be created. 
   * @return A list of files. If <var>dir</var> is null, these files will be stored under the
   * system's temporary directory, so once processing is finished, they should be deleted
   * by the caller, or moved to a more permanent location. 
   * @throws IOException
   * @throws StoreException
   */
  public File[] getFragments(Match[] matches, String[] layerIds, String mimeType, File dir)
    throws IOException, StoreException {
      
    // convert matches into three parallel arrays of IDs/offsets
    String[] transcriptIds = new String[matches.length];
    Double[] startOffsets = new Double[matches.length];
    Double[] endOffsets = new Double[matches.length];
      
    for (int i = 0; i < matches.length; i++) {
      transcriptIds[i] = matches[i].getTranscript();
      startOffsets[i] = matches[i].getLine();
      endOffsets[i] = matches[i].getLineEnd();
    } // next match
            
    return getFragments(transcriptIds, startOffsets, endOffsets, layerIds, mimeType, dir);      
  } // end of getFragments()

  /**
   * Get transcript fragments in a specified format.
   * @param transcriptIds A list of transcript IDs (transcript names).
   * @param startOffsets A list of start offsets, with one element for each element in
   * <var>transcriptIds</var>. 
   * @param endOffsets A list of end offsets, with one element for each element in
   * <var>transcriptIds</var>. 
   * @param layerIds A list of IDs of annotation layers to include in the fragment.
   * @param mimeType The desired format, for example "text/praat-textgrid" for Praat
   * TextGrids, "text/plain" for plain text, etc.
   * @param dir A directory in which the files should be stored, or null for a temporary
   * folder.  If specified, and the directory doesn't exist, it will be created. 
   * @return A list of files. If <var>dir</var> is null, these files will be stored under the
   * system's temporary directory, so once processing is finished, they should be deleted
   * by the caller, or moved to a more permanent location. 
   * @throws IOException
   * @throws StoreException
   */
  public File[] getFragments(
    String[] transcriptIds, Double[] startOffsets, Double[] endOffsets, String[] layerIds,
    String mimeType, File dir)
    throws IOException, StoreException {
      
    if (transcriptIds.length != startOffsets.length || transcriptIds.length != endOffsets.length) {
      throw new StoreException(
        "transcriptIds ("+transcriptIds.length +"), startOffsets ("+startOffsets.length
        +"), and endOffsets ("+endOffsets.length+") must be arrays of equal size.");
    }
    File[] fragments = new File[transcriptIds.length];
      
    boolean tempFiles = false;
    if (dir == null) {
      dir = File.createTempFile("getFragments_", "_frag");
      dir.delete();
      dir.mkdir();
      dir.deleteOnExit();
      tempFiles = true;
    } else {
      if (!dir.exists()) Files.createDirectory(dir.toPath());
    }

    // loop through each triple, getting fragments individually
    for (int i = 0; i < transcriptIds.length; i++) {
      if (cancelling) break;
      if (transcriptIds[i] == null || startOffsets[i] == null || endOffsets[i] == null) continue;

      URL url = makeUrl("api/serialize/fragment");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", mimeType)
        .setParameter("id", transcriptIds[i])
        .setParameter("start", startOffsets[i])
        .setParameter("end", endOffsets[i])
        .setParameter("mimeType", mimeType)
        .setParameter("layerId", layerIds);
      if (verbose) System.out.println("getFragments -> " + request);
      HttpURLConnection connection = request.get();
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {
          System.err.println(
            "getFragments: Error " + connection.getResponseCode()
            + " " + connection.getResponseMessage() + " - " + request);
        } 
        continue;
      } else {
        // use the name given by the server, if any
        String contentDisposition = connection.getHeaderField("content-disposition");
        if (contentDisposition != null) {
          // something like attachment; filename=blah.wav
          int equals = contentDisposition.indexOf("=");
          if (equals > 0) {
            String fileName = contentDisposition.substring(equals + 1);
            if (fileName.length() > 0) fragments[i] = new File(dir, fileName);
          }
        }
        if (fragments[i] == null) { // no suggested name
          // invent a name
          fragments[i] = new File(
            dir, Graph.FragmentId(transcriptIds[i], startOffsets[i], endOffsets[i]));
        }
        if (tempFiles) fragments[i].deleteOnExit();
        IO.SaveUrlConnectionToFile(connection, fragments[i]);           
      } // response ok
    } // next triple

    return fragments;
  } // end of getFragments()
   
  /**
   * Gets transcript attribute values for given transcript IDs.
   * @param transcriptIds A list of transcript IDs (transcript names).
   * @param layerIds A list of layer IDs corresponding to transcript attributes. In
   * general, these are layers whose ID is prefixed 'transcript_', however formally it's
   * any layer where 
   *  layer.getParentId().equals("graph") &amp;&amp; layer.getAlignment() == 0, 
   * which includes "corpus" as well as transcript attribute layers. 
   * @return A CSV file with the attribute values, which it is the caller's
   * responsibility to delete once processing is finished.
   * @throws IOException
   * @throws StoreException
   */
  public File getTranscriptAttributes(String[] transcriptIds, String[] layerIds) 
    throws IOException, StoreException {
      
    URL url = makeUrl("api/attributes");
    HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "text/csv")
      .setParameter("layer", "transcript")
      .setParameter("id", transcriptIds)
      .setParameter("layer", layerIds);
    if (verbose) System.out.println("getTranscriptAttributes -> " + request);
    HttpURLConnection connection = request.post();
    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      response = new Response(connection, verbose);
      response.checkForErrors(); // throws a ResponseException on error
    }
      
    // use the name given by the server, if any
    File csv = File.createTempFile("getTranscriptAttributes_",".csv");
    csv.deleteOnExit();
    IO.SaveUrlConnectionToFile(connection, csv);
    return csv;
  }
   
  /**
   * Gets participant attribute values for given participant IDs.
   * @param participantIds A list of participant IDs (participant names).
   * @param layerIds A list of layer IDs corresponding to participant attributes. In
   * general, these are layers whose ID is prefixed 'participant_', however formally it's
   * any layer where 
   *  layer.getParentId().equals("parent") &amp;&amp; layer.getAlignment() == 0. 
   * @return A CSV file with the attribute values, which it is the caller's
   * responsibility to delete once processing is finished.
   * @throws IOException
   * @throws StoreException
   */
  public File getParticipantAttributes(String[] participantIds, String[] layerIds) 
    throws IOException, StoreException {
      
    URL url = makeUrl("participants");
    HttpRequestPost request = new HttpRequestPost(url, getRequiredHttpAuthorization())
      .setUserAgent()
      .setHeader("Accept", "text/csv")
      .setParameter("type", "participant")
      .setParameter("content-type", "text/csv")
      .setParameter("csvFieldDelimiter", ",")
      .setParameter("participantId", participantIds)
      .setParameter("layer", layerIds);
    if (verbose) System.out.println("getParticipantAttributes -> " + request);
    HttpURLConnection connection = request.post();
    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      response = new Response(connection, verbose);
      response.checkForErrors(); // throws a ResponseException on error
    }
      
    // use the name given by the server, if any
    File csv = File.createTempFile("getParticipantAttributes_",".csv");
    csv.deleteOnExit();
    IO.SaveUrlConnectionToFile(connection, csv);
    return csv;
  }

  /**
   * Lists the descriptors of all registered serializers.
   * <p> Serializers are modules that export annotation structures as a specific file
   * format, e.g. Praat TextGrid, plain text, etc., so the
   * {@link SerializationDescriptor#getMimeType()} of descriptors reflects what 
   * <var>mimeType</var>s can be specified for  
   * {@link #getFragments(String[],Double[],Double[],String[],String,File)}
   * and {@link #getFragments(Match[],String[],String,File)}.
   * @return A list of the descriptors of all registered serializers.
   * @throws StoreException If an error prevents the operation from completing.
   * @throws PermissionException If the operation is not permitted.
   */
  public SerializationDescriptor[] getSerializerDescriptors()
    throws StoreException, PermissionException {
    try {
      URL url = url("getSerializerDescriptors");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getSerializerDescriptors -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<SerializationDescriptor> descriptors = new Vector<SerializationDescriptor>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          descriptors.add(new SerializationDescriptor(array.getJsonObject(i)));
        }
      }
      return descriptors.toArray(new SerializationDescriptor[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
   
  /**
   * Lists the descriptors of all registered deserializers.
   * <p> Deserializers are modules that import annotation structures from a specific file
   * format, e.g. Praat TextGrid, plain text, etc.
   * @return A list of the descriptors of all registered deserializers.
   * @throws StoreException If an error prevents the descriptors from being listed.
   * @throws PermissionException If listing the deserializers is not permitted.
   */
  public SerializationDescriptor[] getDeserializerDescriptors()
    throws StoreException, PermissionException {
    try {
      URL url = url("getDeserializerDescriptors");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getDeserializerDescriptors -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonArray array = (JsonArray)response.getModel();
      Vector<SerializationDescriptor> descriptors = new Vector<SerializationDescriptor>();
      if (array != null) {
        for (int i = 0; i < array.size(); i++) {
          descriptors.add(new SerializationDescriptor(array.getJsonObject(i)));
        }
      }
      return descriptors.toArray(new SerializationDescriptor[0]);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets the serializer for the given file suffix (extension).
   * @param suffix The file extension.
   * @return The serializer for the given suffix, or null if none is registered.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public GraphSerializer serializerForFilesSuffix(String suffix)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }
   
  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets the deserializer for the given MIME type.
   * @param mimeType The MIME type.
   * @return The deserializer for the given MIME type, or null if none is registered.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public GraphDeserializer deserializerForMimeType(String mimeType)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets the deserializer for the given file suffix (extension).
   * @param suffix The file extension.
   * @return The deserializer for the given suffix, or null if none is registered.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public GraphDeserializer deserializerForFilesSuffix(String suffix)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }

  /**
   * <em>NOT YET IMPLEMENTED</em> - Gets the serializer for the given MIME type.
   * @param mimeType The MIME type.
   * @return The serializer for the given MIME type, or null if none is registered.
   * @throws StoreException If an error prevents the operation.
   * @throws PermissionException If the operation is not permitted.
   */
  public GraphSerializer serializerForMimeType(String mimeType)
    throws StoreException, PermissionException {
      
    throw new StoreException("Not implemented");
  }

  /**
   * Lists the descriptors of all registered annotators.
   * <p> Annotators are modules that perform automated annotation of transcripts.
   * @return A list of the descriptors of all registered annotators.
   * @throws StoreException If an error prevents the descriptors from being listed.
   * @throws PermissionException If listing the deserializers is not permitted.
   */
/*   public AnnotatorDescriptor[] getAnnotatorDescriptors() // TODO
     throws StoreException, PermissionException {
     try {
     URL url = url("getAnnotatorDescriptors");
     HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
     .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
     if (verbose) System.out.println("getDeserializerDescriptors -> " + request);
     response = new Response(request.get(), verbose);
     response.checkForErrors(); // throws a StoreException on error
     if (response.isModelNull()) return null;
     JsonArray array = (JsonArray)response.getModel();
     Vector<AnnotatorDescriptor> descriptors = new Vector<AnnotatorDescriptor>();
     if (array != null) {
     for (int i = 0; i < array.size(); i++) {
     descriptors.add(new AnnotatorDescriptor(array.getJsonObject(i)));
     }
     }
     return descriptors.toArray(new AnnotatorDescriptor[0]);
     } catch(IOException x) {
     throw new StoreException("Could not get response.", x);
     }
     }
*/
  
  /**
   * Lists descriptors of all transcribers that are installed.
   * @return A list of descriptors of all transcribers that are installed.
   */
  public AnnotatorDescriptor[] getTranscriberDescriptors() {
    return new AnnotatorDescriptor[0]; // TODO not implemented
  }

  /**
   * Gets the value of the given system attribute.
   * @param attribute Name of the attribute.
   * @return The value of the given attribute.
   * @throws StoreException If an error prevents the descriptors from being listed.
   * @throws PermissionException If listing the deserializers is not permitted.
   */
  public String getSystemAttribute(String attribute) throws StoreException, PermissionException {
    try {
      URL url = makeUrl("api/systemattributes/" + URLEncoder.encode(attribute, "UTF-8"));
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getSystemAttribute -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonObject model = (JsonObject)response.getModel();
      return model.getString("value");
    } catch(ResponseException rx) {
      if (rx.getResponse().getHttpStatus() == HttpURLConnection.HTTP_NOT_FOUND) return null;
      throw rx;
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  }
   
  /**
   * Gets information about the current user, including the roles or groups they are in.
   * @return The user record.
   * @throws StoreException If an error occurs while trying to retrieve the user information.
   */
  public User getUserInfo() throws StoreException {
    try {
      URL url = makeUrl("api/user");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
        .setUserAgent().setLanguage(language).setHeader("Accept", "application/json");
      if (verbose) System.out.println("getUserInfo -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonObject model = (JsonObject)response.getModel();
      return new User(model);
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of getUserInfo()

  /**
   * List dictionaries available.
   * @return A map of layer manager IDs to lists of dictionary IDs.
   * @throws StoreException
   */
  public Map<String,List<String>> getDictionaries() throws StoreException {
    try {
      HttpRequestGet request = get("dictionaries") 
        .setHeader("Accept", "application/json");
      if (verbose) System.out.println("getDictionaries -> " + request);
      response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a StoreException on error
      if (response.isModelNull()) return null;
      JsonObject model = (JsonObject)response.getModel();
      Map<String,List<String>> layerManagerDictionaries = new TreeMap<String,List<String>>();
      for (String layerManagerId : model.keySet()) {
        List<String> ids = new Vector<String>();
        JsonArray array = model.getJsonArray(layerManagerId);
        for (int i = 0; i < array.size(); i++) {
          ids.add(array.getString(i));
        }
        layerManagerDictionaries.put(layerManagerId, ids);
      }
      return layerManagerDictionaries;
    } catch(IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of getDictionaries()
  
  /**
   * Lookup entries in a dictionary.
   * @param managerId
   * @param dictionaryId
   * @param keys
   * @return A CSV file with the entries, which it is the caller's
   * responsibility to delete once processing is finished.
   * @throws StoreException
   */
  public File getDictionaryEntries(
    String managerId, String dictionaryId, String[] keys) throws StoreException {
    
    // save keys to file
    File uploadfile = null;
    try {
      uploadfile = File.createTempFile("getDictionaryEntries_",".csv");
      PrintWriter csv = new PrintWriter(uploadfile);
      for (String key : keys) csv.println(key);
      csv.close();
    } catch(IOException x) {
      throw new StoreException("Could not save keys to local file.", x);
    }
    
    URL url = makeUrl("dictionary");
    try {
      postRequest = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
        .setUserAgent()
        .setHeader("Accept", "text/csv")
        .setParameter("managerId", managerId)
        .setParameter("dictionaryId", dictionaryId)
        .setParameter("uploadfile", uploadfile);
      if (verbose) System.out.println("getDictionaryEntries -> " + postRequest);
      HttpURLConnection connection = postRequest.post();
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
        response = new Response(connection, verbose);
        response.checkForErrors(); // throws a ResponseException on error
      }
      
      // use the name given by the server, if any
      File csv = File.createTempFile("getTranscriptAttributes_",".csv");
      csv.deleteOnExit();
      IO.SaveUrlConnectionToFile(connection, csv);
      return csv;
    } catch (IOException x) {
      throw new StoreException("Could not get response.", x);
    }
  } // end of getDictionaryEntries()

} // end of class LabbcatView
