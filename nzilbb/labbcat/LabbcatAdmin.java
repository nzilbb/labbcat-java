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

import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.util.Vector;
import nzilbb.ag.IGraphStoreAdministration;
import nzilbb.ag.PermissionException;
import nzilbb.ag.StoreException;
import nzilbb.ag.serialize.IDeserializer;
import nzilbb.ag.serialize.ISerializer;
import nzilbb.ag.serialize.SerializationDescriptor;
import nzilbb.labbcat.http.*;
import nzilbb.labbcat.model.Corpus;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Client-side implementation of 
 * <a href="https://nzilbb.github.io/ag/javadoc/nzilbb/ag/IGraphStoreAdministration.html">nzilbb.ag.IGraphStoreAdminitration</a>.
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

public class LabbcatAdmin extends LabbcatEdit implements IGraphStoreAdministration {
   
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
    * Constructs a URL for the given resource.
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

   // IGraphStoreAdministration methods:
   
   /**
    * <em>NOT YET IMPLEMENTED</em> - Registers a transcript deserializer.
    * @param deserializer The deserializer to register.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public void registerDeserializer(IDeserializer deserializer)
      throws StoreException, PermissionException {
      
      throw new StoreException("Not implemented");
   }

   /**
    * <em>NOT YET IMPLEMENTED</em> - De-registers a transcript deserializer.
    * @param deserializer The deserializer to de-register.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public void deregisterDeserializer(IDeserializer deserializer)
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
    * <em>NOT YET IMPLEMENTED</em> - Gets the deserializer for the given MIME type.
    * @param mimeType The MIME type.
    * @return The deserializer for the given MIME type, or null if none is registered.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public IDeserializer deserializerForMimeType(String mimeType)
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
   public IDeserializer deserializerForFilesSuffix(String suffix)
      throws StoreException, PermissionException {
      
      throw new StoreException("Not implemented");
   }

   /**
    * <em>NOT YET IMPLEMENTED</em> - Registers a transcript serializer.
    * @param serializer The serializer to register.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public void registerSerializer(ISerializer serializer)
      throws StoreException, PermissionException {
      
      throw new StoreException("Not implemented");
   }

   /**
    * <em>NOT YET IMPLEMENTED</em> - De-registers a transcript serializer.
    * @param serializer The serializer to de-register.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public void deregisterSerializer(ISerializer serializer)
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
    * <em>NOT YET IMPLEMENTED</em> - Gets the serializer for the given MIME type.
    * @param mimeType The MIME type.
    * @return The serializer for the given MIME type, or null if none is registered.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public ISerializer serializerForMimeType(String mimeType)
      throws StoreException, PermissionException {
      
      throw new StoreException("Not implemented");
   }

   /**
    * <em>NOT YET IMPLEMENTED</em> - Gets the serializer for the given file suffix (extension).
    * @param suffix The file extension.
    * @return The serializer for the given suffix, or null if none is registered.
    * @throws StoreException If an error prevents the operation.
    * @throws PermissionException If the operation is not permitted.
    */
   public ISerializer serializerForFilesSuffix(String suffix)
      throws StoreException, PermissionException {
      
      throw new StoreException("Not implemented");
   }

   // Other methods:

   /**
    * Creates a new corpus record.
    * @param corpus The corpus details to save.
    * @return The corpus just created.
    * @throws StoreException, PermissionException
    */
   public Corpus createCorpus(Corpus corpus) throws StoreException, PermissionException {
      try {
         HttpRequestPost request = post("api/admin/corpora")
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("createCorpus -> " + request);
         response = new Response(request.post(corpus.toJSON()), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         return new Corpus((JSONObject)response.getModel());
      } catch(IOException x) {
         throw new StoreException("Could not get response.", x);
      }
   } // end of createCorpus()

   /**
    * Reads a list of corpus records.
    * @return A list of corpora.
    * @throws StoreException, PermissionException
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
         JSONArray array = (JSONArray)response.getModel();
         Vector<Corpus> corpora = new Vector<Corpus>();
         if (array != null) {
            for (int i = 0; i < array.length(); i++) {
               corpora.add(new Corpus(array.getJSONObject(i)));
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
    */
   public Corpus updateCorpus(Corpus corpus) throws StoreException, PermissionException {
      try{
         HttpRequestPost request = put("api/admin/corpora")
            .setHeader("Accept", "application/json");
         if (verbose) System.out.println("updateCorpus -> " + request);
         response = new Response(request.post(corpus.toJSON()), verbose);
         response.checkForErrors(); // throws a StoreException on error
         if (response.isModelNull()) return null;
         return new Corpus((JSONObject)response.getModel());
      } catch(IOException x) {
         throw new StoreException("Could not get response.", x);
      }
   } // end of updateCorpus()
   
   /**
    * Deletes an existing corpus record.
    * @param corpus The corpus to delete.
    * @throws StoreException, PermissionException
    */
   public void deleteCorpus(Corpus corpus) throws StoreException, PermissionException {
      deleteCorpus(corpus.getName());
   }
   
   /**
    * Deletes an existing corpus record.
    * @param name The name/ID of the corpus to delete.
    * @throws StoreException, PermissionException
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
} // end of class LabbcatAdmin
