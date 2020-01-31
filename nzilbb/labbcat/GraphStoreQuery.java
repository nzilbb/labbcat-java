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
import nzilbb.ag.IGraphStoreQuery;
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
 * Client-side implementation of IGraphStoreQuery.
 * <p> e.g.
 * <pre> // create annotation store client
 * GraphStoreQuery store = new GraphStoreQuery("https://labbcat.canterbury.ac.nz", "demo", "demo");
 * // get some basic information
 * String id = store.getId();
 * String[] layers = store.getLayerIds();
 * String[] corpora = store.getCorpusIds();
 * String[] documents = store.getGraphIdsInCorpus(corpora[0]);
 * </pre>
 * @author Robert Fromont robert@fromont.net.nz
 */

public class GraphStoreQuery
   implements IGraphStoreQuery
{
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
   public GraphStoreQuery setLabbcatUrl(URL newLabbcatUrl) { labbcatUrl = newLabbcatUrl; return this; }
   
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
   public GraphStoreQuery setUsername(String newUsername) { username = newUsername; return this; }

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
   public GraphStoreQuery setPassword(String newPassword) { password = newPassword; return this; }

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
   public GraphStoreQuery setBatchMode(boolean newBatchMode) { batchMode = newBatchMode; return this; }

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
   public GraphStoreQuery setVerbose(boolean newVerbose) { verbose = newVerbose; return this; }

   /**
    * Minimum server version required for this API to work properly.
    * @see #getMinLabbcatVersion()
    * @see #setMinLabbcatVersion(String)
    */
   protected String minLabbcatVersion = "20200129.1901";
   /**
    * Getter for {@link #minLabbcatVersion}: Minimum server version required for this API to work properly.
    * @return Minimum server version required for this API to work properly.
    */
   public String getMinLabbcatVersion() { return minLabbcatVersion; }

   // Methods:
   
   /**
    * Default constructor.
    */
   public GraphStoreQuery()
   {
   } // end of constructor
   
   /**
    * Constructor from string URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public GraphStoreQuery(String labbcatUrl)
      throws MalformedURLException
   {
      setLabbcatUrl(new URL(labbcatUrl));
   } // end of constructor
   
   /**
    * Constructor with String attributes.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    * @param username LaBB-CAT username.
    * @param password LaBB-CAT password.
    */
   public GraphStoreQuery(String labbcatUrl, String username, String password)
      throws MalformedURLException
   {
      setLabbcatUrl(new URL(labbcatUrl));
      setUsername(username);
      setPassword(password);
   } // end of constructor
   
   /**
    * Constructor from URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public GraphStoreQuery(URL labbcatUrl)
   {
      setLabbcatUrl(labbcatUrl);
   } // end of constructor
   
   /**
    * Constructor with attributes.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    * @param username LaBB-CAT username.
    * @param password LaBB-CAT password.
    */
   public GraphStoreQuery(URL labbcatUrl, String username, String password)
   {
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
      throws IOException, StoreException
   {
      if (authorization != null) return authorization;
      
      URL testUrl = url(""); // store URL with no path
      HttpURLConnection testConnection = (HttpURLConnection)testUrl.openConnection();
      Response response = null;
      try
      {
	 InputStream is = testConnection.getInputStream();
	 response = new Response(is, verbose);
      }
      catch (IOException x)
      {
         if (verbose)
         {
            System.out.println(
               "First connection test status ("+(batchMode?"batch":"interacive")+" mode): "
               + testConnection.getResponseCode());
         }            
	 if (testConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
	 {
            if (batchMode)
            { // batchMode - can only try with username/password once
               if (username != null && password != null)
               {
                  authorization = "Basic " + new String(
                     Base64.getMimeEncoder().encode(
                        (username+":"+password).getBytes()), StandardCharsets.UTF_8);
                  testConnection.disconnect();
                  testConnection = (HttpURLConnection)testUrl.openConnection();
                  testConnection.setRequestProperty("Authorization", authorization);
                  try 
                  { 
                     InputStream is = testConnection.getInputStream(); 
                     response = new Response(is, verbose);
                  }
                  catch (IOException xx)
                  {
                     if (verbose)
                     {
                        System.out.println(
                           "Second connection test status in batch mode: "
                           + testConnection.getResponseCode());
                     }            
                     if (testConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
                     {
                        authorization = null;
                        username = null;
                        password = null;
                        throw new IOException("Username/password invalid");
                     }
                     else
                     {
                        throw xx;
                     }
                  }
               }
               else
               {
                  throw new IOException("Username/password required");
               }
            } // batchMode
            else
            { // not batchMode
               JPasswordField txtPassword = new JPasswordField();
               // loop until a username/password works
               while (authorization == null)
               {
                  if (username == null)
                  {
                     username = JOptionPane.showInputDialog(null, "Username", username);
                  }
                  if (username == null) throw new IOException("Cancelled");
                  txtPassword.setText("");
                  if (password == null || password.length() == 0)
                  {
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
                  try 
                  { 
                     InputStream is = testConnection.getInputStream(); 
                     response = new Response(is, verbose);
                  }
                  catch (Exception xx)
                  {
                     if (verbose)
                     {
                        System.out.println(
                           "Next connection test status in interactive mode: "
                           + testConnection.getResponseCode());
                     }
                     if (testConnection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
                     {
                        authorization = null;
                        username = null;
                        password = null;
                     }
                  }
               } // next attempt
            } // not batchMode
	 } // HTTP_UNAUTHORIZED returned
         else
         {
            throw x;
         }
      } // exception getting content

      if (response != null)
      { // got a response
         // check server version
         if (response.getVersion() == null
             || response.getVersion().compareTo(minLabbcatVersion) < 0)
         {
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
      throws StoreException
   {
      try
      {
         return new URL(new URL(labbcatUrl, "store/"), resource);
      }
      catch(Throwable t)
      {
         throw new StoreException("Could not construct request URL.", t);
      }
   } // end of makeUrl()   

   // IGraphStoreQuery methods:
   
   /**
    * Gets the store's ID.
    * @return The annotation store's ID.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public String getId()
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getId");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getId -> " + request);
         Response response = new Response(request.get(), verbose);
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
    * Gets a list of layer IDs (annotation 'types').
    * @return A list of layer IDs.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public String[] getLayerIds()
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getLayerIds");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getLayerIds -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<String> ids = new Vector<String>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               ids.add(array.getString(i));
            }
         }
         return ids.toArray(new String[0]);
      }
      catch(IOException x)
      {
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
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getLayers");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getLayers -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<Layer> layers = new Vector<Layer>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               layers.add(new Layer(array.getJSONObject(i)));
            }
         }
         return layers.toArray(new Layer[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }

   /**
    * Gets the layer schema - <em>NOT YET IMPLEMENTED</em>.
    * @return A schema defining the layers and how they relate to each other.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public Schema getSchema()
      throws StoreException, PermissionException
   {
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
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getLayer");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json")
            .setParameter("id", id);
         if (verbose) System.out.println("getLayer -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         return new Layer((JSONObject)response.getModel());
      }
      catch(IOException x)
      {
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
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getCorpusIds");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getCorpusIds -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<String> ids = new Vector<String>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               ids.add(array.getString(i));
            }
         }
         return ids.toArray(new String[0]);
      }
      catch(IOException x)
      {
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
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getParticipantIds");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getParticipantIds -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<String> ids = new Vector<String>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               ids.add(array.getString(i));
            }
         }
         return ids.toArray(new String[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 

   /**
    * Gets the participant record specified by the given identifier.
    * @param id The ID of the participant, which could be their name or their database annotation
    * ID. 
    * @return An annotation representing the participant, or null if the participant was not found.
    * @throws StoreException
    * @throws PermissionException
    */
   public Annotation getParticipant(String id)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getParticipant");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json")
            .setParameter("id", id);
         if (verbose) System.out.println("getParticipant -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         return new Annotation((JSONObject)response.getModel());
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }

   /**
    * Counts the number of participants that match a particular pattern.
    * @param expression An expression that determines which participants match.
    * <p> The expression language is currently not well defined, but expressions such as the
    * following can be used: 
    * <ul>
    *  <li><code>id MATCHES 'Ada.+'</code></li>
    *  <li><code>'CC' IN labels('corpus')</code></li>
    *  <li><code>'en' IN labels('participant_languages')</code></li>
    *  <li><code>'en' IN labels('transcript_language')</code></li>
    *  <li><code>id NOT MATCHES 'Ada.+' AND my('corpus').label = 'CC'</code></li>
    *  <li><code>list('transcript_rating').length &gt; 2</code></li>
    *  <li><code>list('participant_rating').length = 0</code></li>
    *  <li><code>'labbcat' NOT IN annotators('transcript_rating')</code></li>
    *  <li><code>my('participant_gender').label = 'NA'</code></li>
    * </ul>
    * @return The number of matching participants.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public int countMatchingParticipantIds(String expression)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("countMatchingParticipantIds");
         if (verbose) System.out.println("countMatchingParticipantIds -> " + url);
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setParameter("expression", expression)
            .setHeader("Accept", "application/json");
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         return (Integer)response.getModel();
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 
   
   /**
    * Gets a list of IDs of participants that match a particular pattern.
    * @param expression An expression that determines which participants match.
    * <p> The expression language is currently not well defined, but expressions such as the
    * following can be used: 
    * <ul>
    *  <li><code>id MATCHES 'Ada.+'</code></li>
    *  <li><code>'CC' IN labels('corpus')</code></li>
    *  <li><code>'en' IN labels('participant_languages')</code></li>
    *  <li><code>'en' IN labels('transcript_language')</code></li>
    *  <li><code>id NOT MATCHES 'Ada.+' AND my('corpus').label = 'CC'</code></li>
    *  <li><code>list('transcript_rating').length &gt; 2</code></li>
    *  <li><code>list('participant_rating').length = 0</code></li>
    *  <li><code>'labbcat' NOT IN annotators('transcript_rating')</code></li>
    *  <li><code>my('participant_gender').label = 'NA'</code></li>
    * </ul>
    * @param pageLength The maximum number of IDs to return, or null to return all.
    * @param pageNumber The zero-based page number to return, or null to return the first page.
    * @return A list of participant IDs.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public String[] getMatchingParticipantIds(String expression, Integer pageLength, Integer pageNumber)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getMatchingParticipantIds");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json")
            .setParameter("expression", expression);
         if (verbose) System.out.println("getMatchingParticipantIds -> " + request);
         if (pageLength != null) request.setParameter("pageLength", pageLength);
         if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<String> ids = new Vector<String>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               ids.add(array.getString(i));
            }
         }
         return ids.toArray(new String[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 

   /**
    * Gets a list of graph IDs.
    * @return A list of graph IDs.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public String[] getGraphIds()
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getGraphIds");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getGraphIds -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<String> ids = new Vector<String>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               ids.add(array.getString(i));
            }
         }
         return ids.toArray(new String[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 
   
   /**
    * Gets a list of graph IDs in the given corpus.
    * @param id A corpus ID.
    * @return A list of graph IDs.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public String[] getGraphIdsInCorpus(String id)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getGraphIdsInCorpus");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setParameter("id", id)
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getGraphIdsInCorpus -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<String> ids = new Vector<String>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               ids.add(array.getString(i));
            }
         }
         return ids.toArray(new String[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 

   /**
    * Gets a list of IDs of graphs that include the given participant.
    * @param id A participant ID.
    * @return A list of graph IDs.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public String[] getGraphIdsWithParticipant(String id)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getGraphIdsWithParticipant");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setParameter("id", id)
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getGraphIdsWithParticipant -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<String> ids = new Vector<String>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               ids.add(array.getString(i));
            }
         }
         return ids.toArray(new String[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 

   /**
    * Counts the number of graphs that match a particular pattern.
    * @param expression An expression that determines which graphs match.
    * <p> The expression language is currently not well defined, but expressions such as
    * the following can be used: 
    * <ul>
    *  <li><code>id MATCHES 'Ada.+'</code></li>
    *  <li><code>'Robert' IN labels('who')</code></li>
    *  <li><code>my('corpus').label IN ('CC', 'IA', 'MU')</code></li>
    *  <li><code>my('episode').label = 'Ada Aitcheson'</code></li>
    *  <li><code>my('transcript_scribe').label = 'Robert'</code></li>
    *  <li><code>my('participant_languages').label = 'en'</code></li>
    *  <li><code>my('noise').label = 'bell'</code></li>
    *  <li><code>'en' IN labels('transcript_languages')</code></li>
    *  <li><code>'en' IN labels('participant_languages')</code></li>
    *  <li><code>'bell' IN labels('noise')</code></li>
    *  <li><code>list('transcript_languages').length &gt; 1</code></li>
    *  <li><code>list('participant_languages').length &gt; 1</code></li>
    *  <li><code>list('transcript').length &gt; 100</code></li>
    *  <li><code>'Robert' IN annotators('transcript_rating')</code></li>
    *  <li><code>id NOT MATCHES 'Ada.+' AND my('corpus').label = 'CC' AND 'Robert' IN
    * labels('who')</code></li> 
    * </ul>
    * @return The number of matching graphs.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public int countMatchingGraphIds(String expression)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("countMatchingGraphIds");
         if (verbose) System.out.println("countMatchingGraphIds -> " + url);
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setParameter("expression", expression)
            .setHeader("Accept", "application/json");
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         return (Integer)response.getModel();
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }    

   /**
    * <p>Gets a list of IDs of graphs that match a particular pattern.
    * <p>The results can be exhaustive, by omitting pageLength and pageNumber, or they
    * can be a subset (a 'page') of results, by given pageLength and pageNumber values.</p>
    * <p>The order of the list can be specified.  If ommitted, the graphs are listed in ID
    * order.</p> 
    * @param expression An expression that determines which graphs match.
    * <p> The expression language is currently not well defined, but expressions such as
    * the following can be used:
    * <ul>
    *  <li><code>id MATCHES 'Ada.+'</code></li>
    *  <li><code>'Robert' IN labels('who')</code></li>
    *  <li><code>my('corpus').label IN ('CC', 'IA', 'MU')</code></li>
    *  <li><code>my('episode').label = 'Ada Aitcheson'</code></li>
    *  <li><code>my('transcript_scribe').label = 'Robert'</code></li>
    *  <li><code>my('participant_languages').label = 'en'</code></li>
    *  <li><code>my('noise').label = 'bell'</code></li>
    *  <li><code>'en' IN labels('transcript_languages')</code></li>
    *  <li><code>'en' IN labels('participant_languages')</code></li>
    *  <li><code>'bell' IN labels('noise')</code></li>
    *  <li><code>list('transcript_languages').length &gt; 1</code></li>
    *  <li><code>list('participant_languages').length &gt; 1</code></li>
    *  <li><code>list('transcript').length &gt; 100</code></li>
    *  <li><code>'Robert' IN annotators('transcript_rating')</code></li>
    *  <li><code>id NOT MATCHES 'Ada.+' AND my('corpus').label = 'CC' AND 'Robert' IN
    * labels('who')</code></li> 
    * </ul>
    * @param pageLength The maximum number of IDs to return, or null to return all.
    * @param pageNumber The zero-based page number to return, or null to return the first page.
    * @param order The ordering for the list of IDs, a string containing a comma-separated list of
    * expressions, which may be appended by " ASC" or " DESC", or null for graph ID order. 
    * @return A list of graph IDs.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public String[] getMatchingGraphIds(String expression, Integer pageLength, Integer pageNumber, String order)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getMatchingGraphIds");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json")
            .setParameter("expression", expression);
         if (pageLength != null) request.setParameter("pageLength", pageLength);
         if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
         if (verbose) System.out.println("getMatchingGraphIds -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<String> ids = new Vector<String>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               ids.add(array.getString(i));
            }
         }
         return ids.toArray(new String[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 

   /**
    * Counts the number of annotations that match a particular pattern.
    * @param expression An expression that determines which participants match.
    * <p> The expression language is currently not well defined, but expressions such as
    * the following can be used:
    * <ul>
    *  <li><code>id = 'ew_0_456'</code></li>
    *  <li><code>label NOT MATCHES 'th[aeiou].*'</code></li>
    *  <li><code>layer.id = 'orthography' AND my('who').label = 'Robert' AND
    * my('utterances').start.offset = 12.345</code></li> 
    *  <li><code>graph.id = 'AdaAicheson-01.trs' AND layer.id = 'orthography' AND start.offset
    * &gt; 10.5</code></li> 
    * </ul>
    * <p><em>NB</em> all expressions must match by either id or layer.id.
    * @return The number of matching annotations.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public int countMatchingAnnotations(String expression)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("countMatchingAnnotations");
         if (verbose) System.out.println("countMatchingAnnotations -> " + url);
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setParameter("expression", expression)
            .setHeader("Accept", "application/json");
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         return (Integer)response.getModel();
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 

   /**
    * Gets a list of annotations that match a particular pattern.
    * @param expression An expression that determines which graphs match.
    * <p> The expression language is currently not well defined, but expressions such as the
    * following can be used: 
    * <ul>
    *  <li><code>id = 'ew_0_456'</code></li>
    *  <li><code>label NOT MATCHES 'th[aeiou].*'</code></li>
    *  <li><code>my('who').label = 'Robert' AND my('utterances').start.offset = 12.345</code></li>
    *  <li><code>graph.id = 'AdaAicheson-01.trs' AND layer.id = 'orthography' AND start.offset
    * &gt; 10.5</code></li> 
    *  <li><code>previous.id = 'ew_0_456'</code></li>
    * </ul>
    * <p><em>NB</em> all expressions must match by either id or layer.id.
    * @param pageLength The maximum number of annotations to return, or null to return all.
    * @param pageNumber The zero-based page number to return, or null to return the first page.
    * @return A list of matching {@link Annotation}s.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    */
   public Annotation[] getMatchingAnnotations(String expression, Integer pageLength, Integer pageNumber)
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getMatchingAnnotations");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json")
            .setParameter("expression", expression);
         if (pageLength != null) request.setParameter("pageLength", pageLength);
         if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
         if (verbose) System.out.println("getMatchingGraphIds -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<Annotation> annotations = new Vector<Annotation>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               annotations.add(new Annotation(array.getJSONObject(i)));
            }
         }
         return annotations.toArray(new Annotation[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   } 

   /**
    * Gets the number of annotations on the given layer of the given graph.
    * @param id The ID of the graph.
    * @param layerId The ID of the layer.
    * @return A (possibly empty) array of annotations.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public long countAnnotations(String id, String layerId)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = url("countAnnotations");
         if (verbose) System.out.println("countAnnotations -> " + url);
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setParameter("id", id)
            .setParameter("layerId", layerId)
            .setHeader("Accept", "application/json");
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.getModel() instanceof Integer) return (Integer)response.getModel();
         return (Long)response.getModel();
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }

   /**
    * Gets the annotations on the given layer of the given graph.
    * @param id The ID of the graph.
    * @param layerId The ID of the layer.
    * @param pageLength The maximum number of IDs to return, or null to return all.
    * @param pageNumber The zero-based page number to return, or null to return the first page.
    * @return A (possibly empty) array of annotations.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public Annotation[] getAnnotations(String id, String layerId, Integer pageLength, Integer pageNumber)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = url("getAnnotations");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json")
            .setParameter("id", id)
            .setParameter("layerId", layerId);
         if (pageLength != null) request.setParameter("pageLength", pageLength);
         if (pageNumber != null) request.setParameter("pageNumber", pageNumber);
         if (verbose) System.out.println("getAnnotations -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<Annotation> annotations = new Vector<Annotation>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               annotations.add(new Annotation(array.getJSONObject(i)));
            }
         }
         return annotations.toArray(new Annotation[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }
   
   /**
    * Gets the annotations on given layers for a set of match IDs - <em>NOT YET IMPLEMENTED</em>.
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
   public void getMatchAnnotations(Iterator<String> matchIds, String[] layerIds, int targetOffset, int annotationsPerLayer, Consumer<Annotation[]> consumer)
      throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }
   
   /**
    * Gets the given anchors in the given graph.
    * @param id The ID of the graph.
    * @param anchorIds A list of anchor IDs.
    * @return A (possibly empty) array of anchors.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public Anchor[] getAnchors(String id, String[] anchorIds)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = url("getAnchors");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json")
            .setParameter("id", id)
            .setParameter("anchorIds", anchorIds);
         if (verbose) System.out.println("getAnchors -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<Anchor> anchors = new Vector<Anchor>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               anchors.add(new Anchor(array.getJSONObject(i)));
            }
         }
         return anchors.toArray(new Anchor[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }

   /**
    * Gets a graph given its ID - <em>NOT YET IMPLEMENTED</em>.
    * @param id The given graph ID.
    * @return The identified graph.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public Graph getGraph(String id) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Gets a graph given its ID, containing only the given layers - <em>NOT YET IMPLEMENTED</em>.
    * @param id The given graph ID.
    * @param layerIds The IDs of the layers to load, or null if only graph data is required.
    * @return The identified graph.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public Graph getGraph(String id, String[] layerIds) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Gets a fragment of a graph, given its ID and the ID of an annotation in it that defines the - <em>NOT YET IMPLEMENTED</em> 
    * desired fragment.
    * @param graphId The ID of the graph.
    * @param annotationId The ID of an annotation that defines the bounds of the fragment.
    * @return The identified graph fragment.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public Graph getFragment(String graphId, String annotationId) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Gets a fragment of a graph, given its ID and the ID of an annotation in it that defines the - <em>NOT YET IMPLEMENTED</em> 
    * desired fragment, and containing only the given layers.
    * @param graphId The ID of the graph.
    * @param annotationId The ID of an annotation that defines the bounds of the fragment.
    * @param layerIds The IDs of the layers to load, or null if only graph data is required.
    * @return The identified graph fragment.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public Graph getFragment(String graphId, String annotationId, String[] layerIds) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }
   
   /**
    * Gets a fragment of a graph, given its ID and the start/end offsets that define the - <em>NOT YET IMPLEMENTED</em> 
    * desired fragment, and containing only the given layers.
    * @param graphId The ID of the graph.
    * @param start The start offset of the fragment.
    * @param end The end offset of the fragment.
    * @param layerIds The IDs of the layers to load, or null if only graph data is required.
    * @return The identified graph fragment.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public Graph getFragment(String graphId, double start, double end, String[] layerIds) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }
   
   /**
    * Gets a series of fragments, given the series' ID, and only the given layers - <em>NOT YET IMPLEMENTED</em>.
    * @param seriesId The ID of the series.
    * @param layerIds The IDs of the layers to load, or null if only graph data is required.
    * @return An enumeratable series of fragments.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public MonitorableSeries<Graph> getFragmentSeries(String seriesId, String[] layerIds) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      throw new StoreException("Not implemented");
   }
   
   /**
    * List the predefined media tracks available for transcripts.
    * @return An ordered list of media track definitions.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted. 
    */
   public MediaTrackDefinition[] getMediaTracks() 
      throws StoreException, PermissionException
   {
      try
      {
         URL url = url("getMediaTracks");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("getMediaTracks -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<MediaTrackDefinition> tracks = new Vector<MediaTrackDefinition>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               tracks.add(new MediaTrackDefinition(array.getJSONObject(i)));
            }
         }
         return tracks.toArray(new MediaTrackDefinition[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }
   
   /**
    * List the media available for the given graph.
    * @param id The graph ID.
    * @return List of media files available for the given graph.
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public MediaFile[] getAvailableMedia(String id) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = url("getAvailableMedia");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json")
            .setParameter("id", id);
         if (verbose) System.out.println("getAvailableMedia -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<MediaFile> files = new Vector<MediaFile>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               files.add(new MediaFile(array.getJSONObject(i)));
            }
         }
         return files.toArray(new MediaFile[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }

   /**
    * Gets a given media track for a given graph.
    * @param id The graph ID.
    * @param trackSuffix The track suffix of the media - see {@link MediaTrackDefinition#suffix}.
    * @param mimeType The MIME type of the media, which may include parameters for type
    * conversion, e.g. "text/wav; samplerate=16000".
    * @return A URL to the given media for the given graph, or null if the given media doesn't
    * exist. 
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public String getMedia(String id, String trackSuffix, String mimeType) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = url("getMedia");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json")
            .setParameter("id", id)
            .setParameter("trackSuffix", trackSuffix)
            .setParameter("mimeType", mimeType);
         if (verbose) System.out.println("getMedia -> " + request);
         Response response = new Response(request.get(), verbose);
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
    * Gets a given media track for a given graph.
    * @param id The graph ID.
    * @param trackSuffix The track suffix of the media - see {@link MediaTrackDefinition#suffix}.
    * @param mimeType The MIME type of the media, which may include parameters for type
    * conversion, e.g. "text/wav; samplerate=16000"
    * @param startOffset The start offset of the media sample, or null for the start of the whole
    * recording. 
    * @param endOffset The end offset of the media sample, or null for the end of the whole
    * recording. 
    * @return A URL to the given media for the given graph, or null if the given media doesn't
    * exist. 
    * @throws StoreException If an error occurs.
    * @throws PermissionException If the operation is not permitted.
    * @throws GraphNotFoundException If the graph was not found in the store.
    */
   public String getMedia(String id, String trackSuffix, String mimeType, Double startOffset, Double endOffset) 
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = url("getMedia");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
            .setHeader("Accept", "application/json")
            .setParameter("id", id)
            .setParameter("trackSuffix", trackSuffix)
            .setParameter("mimeType", mimeType)
            .setParameter("startOffset", startOffset)
            .setParameter("endOffset", endOffset);
         if (verbose) System.out.println("getMedia -> " + request);
         Response response = new Response(request.get(), verbose);
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
    * Get a list of documents associated with the episode of the given graph.
    * @param id The graph ID.
    * @return List of URLs to documents.
    * @throws StoreException If an error prevents the media from being saved.
    * @throws PermissionException If saving the media is not permitted.
    * @throws GraphNotFoundException If the graph doesn't exist.
    */
   public MediaFile[] getEpisodeDocuments(String id)
      throws StoreException, PermissionException, GraphNotFoundException
   {
      try
      {
         URL url = url("getEpisodeDocuments");
         HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization()) 
            .setHeader("Accept", "application/json")
            .setParameter("id", id);
         if (verbose) System.out.println("getEpisodeDocuments -> " + request);
         Response response = new Response(request.get(), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         JSONArray array = (JSONArray)response.getModel();
         Vector<MediaFile> files = new Vector<MediaFile>();
         if (array != null)
         {
            for (int i = 0; i < array.length(); i++)
            {
               files.add(new MediaFile(array.getJSONObject(i)));
            }
         }
         return files.toArray(new MediaFile[0]);
      }
      catch(IOException x)
      {
         throw new StoreException("Could not get response.", x);
      }
   }

} // end of class GraphStoreQuery
