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
import java.util.Collection;
import java.io.File;
import java.io.IOException;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.http.*;

/**
 * Labbcat client, for accessing LaBB-CAT server functions programmatically.
 * <p> e.g.
 * <pre> // create annotation store client
 * Labbcat labbcat = new Labbcat("https://labbcat.canterbury.ac.nz", "demo", "demo");
 * // get some basic information
 * String id = labbcat.getId();
 * String[] layers = labbcat.getLayerIds();
 * String[] corpora = labbcat.getCorpusIds();
 * String[] documents = labbcat.getGraphIdsInCorpus(corpora[0]);
 * </pre>
 * @author Robert Fromont robert@fromont.net.nz
 */

public class Labbcat
   extends GraphStoreAdministration
{
   // Attributes:
  
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
      throws IOException, ResponseException
   {
      throw new IOException("not implemented");
   } // end of newTranscript()

   /**
    * Uploads a new version of an existing transcript.
    * @param transcript
    * @return The taskId of the server task processing the upload. 
    * @throws IOException
    * @throws ResponseException
    */
   public String updateTranscript(File transcript)
      throws IOException, ResponseException
   {
      throw new IOException("not implemented");
   } // end of updateTranscript()
   
   /**
    * Gets the current state of the given task.
    * @param taskId The ID of the task.
    * @return The status of the task
    * @throws IOException
    * @throws ResponseException
    */
   public TaskStatus taskStatus(String taskId)
      throws IOException, ResponseException
   {
      throw new IOException("not implemented");
   } // end of taskStatus()
   
   /**
    * Release a finished task, to free up server resources.
    * @param taskId The ID of the task.
    * @throws IOException
    * @throws ResponseException
    */
   public void releaseTask(String taskId)
    throws IOException, ResponseException
   {
      throw new IOException("not implemented");
   } // end of releaseTask()

   /**
    * Gets a list of all tasks on the server.
    * @return A list of all task statuses.
    * @throws IOException, ResponseException
    */
   public TaskStatus[] getTasks()
    throws IOException, ResponseException
   {
      throw new IOException("not implemented");
   } // end of getTasks()

   // TODO String getMatches(pattern, participantId=NULL, main.participant=TRUE, words.context=0)
   // TODO getMatchLabels(matchIds, layerIds, targetOffset=0, annotationsPerLayer=1)
   // TODO String getSoundFragments(id, start, end, sampleRate = NULL)
} // end of class Labbcat
