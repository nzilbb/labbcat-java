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
import java.util.Vector;
import nzilbb.ag.StoreException;
import nzilbb.util.IO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class representing the JSON response of a LaBB-CAT request.
 * @author Robert Fromont robert@fromont.net.nz
 */

public class Response
{
   // Attributes:
   
   /**
    * The HTTP status code, or -1 if not known.
    * @see #getHttpStatus()
    * @see #setHttpStatus(int)
    */
   protected int httpStatus = -1;
   /**
    * Getter for {@link #httpStatus}: The HTTP status code, or -1 if not known.
    * @return The HTTP status code, or -1 if not known.
    */
   public int getHttpStatus() { return httpStatus; }

   /**
    * Server title.
    * @see #getTitle()
    * @see #setTitle(String)
    */
   protected String title;
   /**
    * Getter for {@link #title}: Server title.
    * @return Server title.
    */
   public String getTitle() { return title; }

   /**
    * The request code.
    * @see #getCode()
    * @see #setCode(int)
    */
   protected int code = -1;
   /**
    * Getter for {@link #code}: The request code.
    * @return The request code.
    */
   public int getCode() { return code; }

   /**
    * Errors returned.
    * @see #getErrors()
    * @see #setErrors(Vector)
    */
   protected Vector<String> errors;
   /**
    * Getter for {@link #errors}: Errors returned.
    * @return Errors returned.
    */
   public Vector<String> getErrors() { return errors; }

   /**
    * Messages returned.
    * @see #getMessages()
    * @see #setMessages(Vector<String>)
    */
   protected Vector<String> messages;
   /**
    * Getter for {@link #messages}: Messages returned.
    * @return Messages returned.
    */
   public Vector<String> getMessages() { return messages; }

   /**
    * The model or result returned.
    * @see #getModel()
    * @see #setModel(Object)
    */
   protected Object model;
   /**
    * Getter for {@link #model}: The model or result returned.
    * @return The model or result returned.
    */
   public Object getModel() { return model; }
   /**
    * Setter for {@link #model}: The model or result returned.
    * @param newModel The model or result returned.
    */
   public Response setModel(Object newModel) { model = newModel; return this; }

   /**
    * Raw response text.
    * @see #getRaw()
    * @see #setRaw(String)
    */
   protected String raw;
   /**
    * Getter for {@link #raw}: Raw response text.
    * @return Raw response text.
    */
   public String getRaw() { return raw; }   

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
   
   // Methods:
   
   /**
    * Default constructor.
    */
   public Response()
   {
   } // end of constructor

   /**
    * Constructor from InputStream.
    * @param input The stream to read from.
    */
   public Response(InputStream input)
      throws StoreException
   {
      try
      {
         load(input);
      }
      catch(Exception exception)
      {
         throw new StoreException(exception);
      }
   } // end of constructor
   
   /**
    * Constructor from InputStream.
    * @param input The stream to read from.
    * @param verbose The verbosity setting to use.
    */
   public Response(InputStream input, boolean verbose)
      throws StoreException
   {
      this.verbose = verbose;
      try
      {
         load(input);
      }
      catch(Exception exception)
      {
         throw new StoreException(exception);
      }
   } // end of constructor
   
   /**
    * Constructor from HttpURLConnection.
    * @param input The stream to read from.
    * @param verbose The verbosity setting to use.
    */
   public Response(HttpURLConnection connection, boolean verbose)
      throws StoreException
   {
      this.verbose = verbose;
      try
      {
         httpStatus = connection.getResponseCode();
      }
      catch(IOException exception)
      {
         throw new StoreException(exception);
      }
      if (httpStatus == HttpURLConnection.HTTP_OK)
      {
         try
         {
            load(connection.getInputStream());
         }
         catch(Exception exception)
         {
            throw new StoreException(exception);
         }
      }
      else
      {
         if (verbose)
         {
            try
            {
               System.out.println("HTTP error: " + httpStatus + ": " + connection.getResponseMessage());
            }
            catch(IOException exception)
            {
               System.out.println("HTTP error: " + httpStatus);
            }
         }
         try
         {
            load(connection.getErrorStream());
         }
         catch(Exception exception)
         {
            throw new StoreException(exception);
         }
      }
   } // end of constructor
   
   /**
    * Loads the response from the given stream.
    * @param input The stream to read from.
    * @return A reference to this object,
    * @throws IOException, JSONException
    */
   public Response load(InputStream input)
    throws IOException, JSONException
   {
      return load(IO.InputStreamToString(input));
   } // end of load()
   
   /**
    * Loads the response from the given stream.
    * @param text The raw text of the response.
    * @return A reference to this object,
    * @throws IOException, JSONException
    */
   public Response load(String text)
    throws JSONException
   {      
      raw = text;
      if (verbose) System.out.println("raw: " + raw);
      JSONObject json = new JSONObject(raw);
      
      title = json.getString("title");
      if (verbose) System.out.println("title: " + title);
      code = json.getInt("code");
      if (verbose) System.out.println("code: " + code);
      model = json.get("model");      
      if (verbose) System.out.println("model: " + model);
      if (verbose && model != null) System.out.println("model type: " + model.getClass().getName());
      
      JSONArray array = json.getJSONArray("messages");
      messages = new Vector<String>();
      if (array != null)
      {
         for (int i = 0; i < array.length(); i++)
         {
            if (verbose) System.out.println("messages["+i+"]: " + array.getString(i));
            messages.add(array.getString(i));
         }
      }
      
      array = json.getJSONArray("errors");
      errors = new Vector<String>();
      if (array != null)
      {
         for (int i = 0; i < array.length(); i++)
         {
            if (verbose) System.out.println("errors["+i+"]: " + array.getString(i));
            errors.add(array.getString(i));
         }
      }
      return this;
   } // end of load()
   
   /**
    * Convenience method for checking whether the response any errors. If so, a
    * corresponding StoreException will be thrown.
    * @return A reference to this object,
    * @throws StoreException
    */
   public Response checkForErrors()
      throws ResponseException
   {
      if ((errors != null && errors.size() > 0)
          || code > 0
          || (httpStatus > 0 && httpStatus != HttpURLConnection.HTTP_OK))
      {
         throw new ResponseException(this);
      }
      return this;
   } // end of checkForErrors()

   
   /**
    * Determines whether the model is null.
    * @return true if the model returned is null, false otherwise.
    */
   public boolean isModelNull()
   {
      return model == null || model.equals(null);
   } // end of isModelNull()

   
} // end of class Response
