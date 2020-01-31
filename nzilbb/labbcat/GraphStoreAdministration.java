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
import nzilbb.ag.IGraphStoreAdministration;
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
 * Client-side implementation of IGraphStoreAdministration.
 * <p> e.g.
 * <pre> // create annotation store client
 * GraphStoreAdministrationQuery store = new GraphStoreAdministration("https://labbcat.canterbury.ac.nz", "demo", "demo");
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

public class GraphStoreAdministration
   extends GraphStore
   implements IGraphStoreAdministration
{
   // Attributes:
  
   // Methods:
   
   /**
    * Default constructor.
    */
   public GraphStoreAdministration()
   {
   } // end of constructor
   
   /**
    * Constructor from string URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public GraphStoreAdministration(String labbcatUrl)
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
   public GraphStoreAdministration(String labbcatUrl, String username, String password)
      throws MalformedURLException
   {
      super(labbcatUrl, username, password);
   } // end of constructor
   
   /**
    * Constructor from URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public GraphStoreAdministration(URL labbcatUrl)
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
   public GraphStoreAdministration(URL labbcatUrl, String username, String password)
   {
      super(labbcatUrl, username, password);
   } // end of constructor

   /**
    * Constructs a URL for the given resource.
    * @param resource
    * @return A URL for the given resource.
    * @throws StoreException If the URL is malformed.
    */
   public URL adminUrl(String resource)
      throws StoreException
   {
      try
      {
         return new URL(new URL(labbcatUrl, "admin/store/"), resource);
      }
      catch(Throwable t)
      {
         throw new StoreException("Could not construct request URL.", t);
      }
   } // end of editUrl()   

   // IGraphStoreAdministration methods:
   
   /**
    * Registers a graph deserializer.
    * @param deserializer The deserializer to register.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public void registerDeserializer(IDeserializer deserializer) throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * De-registers a graph deserializer.
    * @param deserializer The deserializer to de-register.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public void deregisterDeserializer(IDeserializer deserializer) throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Lists the descriptors of all registered deserializers.
    * @return A list of the descriptors of all registered deserializers.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public SerializationDescriptor[] getDeserializerDescriptors() throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }
   
   /**
    * Gets the deserializer for the given MIME type.
    * @param mimeType The MIME type.
    * @return The deserializer for the given MIME type, or null if none is registered.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public IDeserializer deserializerForMimeType(String mimeType) throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Gets the deserializer for the given file suffix (extension).
    * @param suffix The file extension.
    * @return The deserializer for the given suffix, or null if none is registered.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public IDeserializer deserializerForFilesSuffix(String suffix) throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Registers a graph serializer.
    * @param serializer The serializer to register.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public void registerSerializer(ISerializer serializer) throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * De-registers a graph serializer.
    * @param serializer The serializer to de-register.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public void deregisterSerializer(ISerializer serializer) throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Lists the descriptors of all registered serializers.
    * @return A list of the descriptors of all registered serializers.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public SerializationDescriptor[] getSerializerDescriptors() throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }
   
   /**
    * Gets the serializer for the given MIME type.
    * @param mimeType The MIME type.
    * @return The serializer for the given MIME type, or null if none is registered.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public ISerializer serializerForMimeType(String mimeType) throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

   /**
    * Gets the serializer for the given file suffix (extension).
    * @param suffix The file extension.
    * @return The serializer for the given suffix, or null if none is registered.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public ISerializer serializerForFilesSuffix(String suffix) throws StoreException, PermissionException
   {
      throw new StoreException("Not implemented");
   }

} // end of class GraphStoreAdministration