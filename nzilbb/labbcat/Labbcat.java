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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.http.*;
import org.json.JSONObject;

/**
 * Labbcat client, for accessing LaBB-CAT server functions programmatically.
 * <p> e.g.
 * <pre> // create LaBB-CAT client
 * Labbcat labbcat = new {@link #Labbcat(String,String,String) Labbcat("https://labbcat.canterbury.ac.nz", "demo", "demo")};
 * 
 * // get a corpus ID
 * String[] corpora = labbcat.{@link GraphStoreQuery#getCorpusIds() getCorpusIds()};
 * String corpus = ids[0];
 *
 * // get a transcript type
 * Layer typeLayer = labbcat.{@link GraphStoreQuery#getLayer(String) getLayer("transcript_type")};
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
 * Annotation[] pos = store.{@link GraphStoreQuery#getAnnotations(String,String,Integer,Integer) getAnnotations(transcript.getName(), "pos")};
 * </pre>
 * @author Robert Fromont robert@fromont.net.nz
 */

public class Labbcat
   extends GraphStoreAdministration
{
   // Attributes:
   
   /** Current request, if any */
   HttpRequestPostMultipart postRequest;   
  
   // Methods:
   
   /**
    * Default constructor.
    */
   public Labbcat()
   {
   } // end of constructor
   
   /**
    * Constructor from string URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public Labbcat(String labbcatUrl)
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
   public Labbcat(String labbcatUrl, String username, String password)
      throws MalformedURLException
   {
      super(labbcatUrl, username, password);
   } // end of constructor
   
   /**
    * Constructor from URL.
    * @param labbcatUrl The base URL of the LaBB-CAT server -
    * e.g. https://labbcat.canterbury.ac.nz/demo/
    */
   public Labbcat(URL labbcatUrl)
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
   public Labbcat(URL labbcatUrl, String username, String password)
   {
      super(labbcatUrl, username, password);
   } // end of constructor

   /**
    * Constructs a URL for the given resource.
    * @param resource
    * @return A URL for the given resource.
    * @throws StoreException If the URL is malformed.
    */
   public URL makeUrl(String resource)
      throws StoreException
   {
      try
      {
         return new URL(labbcatUrl, resource);
      }
      catch(Throwable t)
      {
         throw new StoreException("Could not construct request URL.", t);
      }
   } // end of editUrl()

   // LaBB-CAT functions beyond the graph store ones:
   
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
      throws IOException, StoreException
   {
      cancelling = false;
      URL url = makeUrl("edit/transcript/new");
      HttpRequestPostMultipart request = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
         .setHeader("Accept", "application/json")
         .setParameter("todo", "new")
         .setParameter("auto", true)
         .setParameter("transcriptType", transcriptType)
         .setParameter("corpus", corpus)
         .setParameter("episode", episode)
         .setParameter("uploadfile1_0", transcript);
      if (media != null && media.length > 0)
      {
         if (mediaSuffix == null) mediaSuffix = "";
         for (int f = 0; f < media.length; f++)
         {
            request.setParameter("uploadmedia"+mediaSuffix+"1", media[f]);
         } // next file
      }
      if (verbose) System.out.println("taskStatus -> " + request);
      Response response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a ResponseException on error

      // extract the threadId from model.result.id
      JSONObject model = (JSONObject)response.getModel();
      JSONObject result = model.getJSONObject("result");
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
      throws IOException, StoreException
   {
      cancelling = false;
      URL url = makeUrl("edit/transcript/new");
      HttpRequestPostMultipart request = new HttpRequestPostMultipart(url, getRequiredHttpAuthorization())
         .setHeader("Accept", "application/json")
         .setParameter("todo", "update")
         .setParameter("auto", true)
         .setParameter("uploadfile1_0", transcript);
      if (verbose) System.out.println("taskStatus -> " + request);
      Response response = new Response(request.post(), verbose);
      response.checkForErrors(); // throws a ResponseException on error
      
      // extract the threadId from model.result.id
      JSONObject model = (JSONObject)response.getModel();
      JSONObject result = model.getJSONObject("result");
      return result.getString(transcript.getName());
   } // end of updateTranscript()
   
   /**
    * Gets the current state of the given task.
    * @param threadId The ID of the task.
    * @return The status of the task
    * @throws IOException
    * @throws ResponseException
    */
   public TaskStatus taskStatus(String threadId)
      throws IOException, StoreException
   {
      cancelling = false;
      URL url = makeUrl("thread");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
         .setHeader("Accept", "application/json")
         .setParameter("threadId", threadId);
      if (verbose) System.out.println("taskStatus -> " + request);
      Response response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a ResponseException on error
      if (response.isModelNull()) return null;
      return new TaskStatus((JSONObject)response.getModel());
   } // end of taskStatus()
   
   /**
    * Wait for the given task to finish.
    * @param threadId
    * @param maxSeconds The maximum time to wait for the task, or 0 for forever.
    * @return The final task status.
    * @throws IOException
    * @throws ResponseException
    */
   public TaskStatus waitForTask(String threadId, int maxSeconds)
    throws IOException, StoreException
   {
      cancelling = false;
      TaskStatus status = taskStatus(threadId);
      
      long endTime = 0;
      if (maxSeconds > 0) endTime = new Date().getTime() + (maxSeconds * 1000);
      
      while (status.getRunning() && !cancelling)
      {
         long ms = status.getRefreshSeconds() * 1000;
         if (ms <= 0) ms = 2000;
         try { Thread.sleep(ms); } catch(Exception exception) {}
         
         if (endTime > 0 && new Date().getTime() > endTime)
         { // is time up?
            cancelling = true;
         }
         
         if (!cancelling)
         { // are we stopping now?
            status = taskStatus(threadId);
         }
      } // loop
      return status;
   } // end of waitForTask()
   
   /**
    * Release a finished task, to free up server resources.
    * @param threadId The ID of the task.
    * @throws IOException
    * @throws ResponseException
    */
   public void releaseTask(String threadId)
    throws IOException, StoreException
   {
      cancelling = false;
      URL url = makeUrl("threads");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
         .setHeader("Accept", "application/json")
         .setParameter("threadId", threadId)
         .setParameter("command", "release");
      if (verbose) System.out.println("taskStatus -> " + request);
      Response response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a ResponseException on error
   } // end of releaseTask()

   /**
    * Gets a list of all tasks on the server.
    * @return A list of all task statuses.
    * @throws IOException, ResponseException
    */
   public Map<String,TaskStatus> getTasks()
      throws IOException, StoreException
   {
      cancelling = false;
      URL url = makeUrl("threads");
      HttpRequestGet request = new HttpRequestGet(url, getRequiredHttpAuthorization())
         .setHeader("Accept", "application/json");
      if (verbose) System.out.println("getTasks -> " + request);
      Response response = new Response(request.get(), verbose);
      response.checkForErrors(); // throws a ResponseException on error
      if (response.isModelNull()) return null;
      JSONObject model = (JSONObject)response.getModel();
      HashMap<String,TaskStatus> result = new HashMap<String,TaskStatus>();
      for (String threadId : model.keySet())
      {
         result.put(threadId,
                    new TaskStatus(model.getJSONObject(threadId)));
      } // next task
      return result;
   } // end of getTasks()

   boolean cancelling = false;
   /**
    * Cancel the current request, if possible.
    */
   public void cancel()
   {
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
   public boolean isCancelling()
   {
      if (postRequest == null)
      {
	 return cancelling;
      }
      else
      {
	 return postRequest.isCancelling();
      }
   } // end of isCancelling()
   
   // TODO String getMatches(JSONObject pattern, participantId=NULL, main.participant=TRUE, words.context=0)
   // TODO getMatchLabels(matchIds, layerIds, targetOffset=0, annotationsPerLayer=1)
   // TODO String getSoundFragments(id, start, end, sampleRate = NULL)
   // TODO getFragments(id, start, end, layerIds, mimeType = "text/praat-textgrid")

   // TODO clearLayer(id)
   
} // end of class Labbcat
