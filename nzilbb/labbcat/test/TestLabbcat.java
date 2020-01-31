//
// Copyright 2020 New Zealand Institute of Language, Brain and Behaviour, 
// University of Canterbury
// Written by Robert Fromont - robert.fromont@canterbury.ac.nz
//
//    This file is part of LaBB-CAT.
//
//    LaBB-CAT is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 3 of the License, or
//    (at your option) any later version.
//
//    LaBB-CAT is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with nzilbb.ag; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package nzilbb.labbcat.test;
	      
import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import nzilbb.ag.Anchor;
import nzilbb.ag.Annotation;
import nzilbb.ag.Layer;
import nzilbb.ag.MediaFile;
import nzilbb.ag.MediaTrackDefinition;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.*;

/**
 * Unit tests for Labbcat.
 * <p>These tests are general in nature. They assume the existence of a valid LaBB-CAT
 * instance (configured by labbcatUrl) but do not assume specific corpus content. For the
 * tests to work, the first graph listed in LaBB-CAT must have some words and some media,
 * and the first participant listed must have some transcripts.
 *
 */
public class TestLabbcat 
{
   // YOU MUST ENSURE THE FOLLOWING SETTINGS ARE VALID FOR YOU TEST LABBCAT SERVER:
   static String labbcatUrl = "http://localhost:8080/labbcat/";
   static String username = "labbcat";
   static String password = "labbcat";
   static Labbcat labbcat;

   @BeforeClass public static void createStore()
   {
      try
      {
         labbcat = new Labbcat(labbcatUrl, username, password);
         labbcat.setBatchMode(true);
      }
      catch(MalformedURLException exception)
      {
         fail("Could not create Labbcat object");
      }
   }

   @After public void notVerbose()
   {
      labbcat.setVerbose(false);
   }
   
   @Test(expected = StoreException.class) public void invalidCredentials()
      throws Exception
   {
      Labbcat labbcat = new Labbcat(labbcatUrl, "xxx", "xxx");
      labbcat.setBatchMode(true);
      labbcat.getId();
   }

   @Test(expected = StoreException.class) public void credentialsRequired()
      throws Exception
   {
      Labbcat labbcat = new Labbcat(labbcatUrl);
      labbcat.setBatchMode(true);
      labbcat.getId();
   }
   
   @Test(expected = MalformedURLException.class) public void malformedURLException()
      throws Exception
   {
      Labbcat labbcat = new Labbcat("xxx", username, password);
      labbcat.setBatchMode(true);
      labbcat.getId();
   }

   @Test(expected = StoreException.class) public void nonLabbcatUrl()
      throws Exception
   {
      Labbcat labbcat = new Labbcat("http://tld/", username, password);
      labbcat.setBatchMode(true);
      labbcat.getId();
   }

   @Test public void inheritedGraphStoreQueryFunctions()
      throws Exception
   {
      String id = labbcat.getId();
      assertEquals("getId: ID matches the url",
                   labbcatUrl, id);

      String[] ids = labbcat.getLayerIds();
      //for (String id : ids) System.out.println("layer " + id);
      assertTrue("getLayerIds: Some IDs are returned",
                 ids.length > 0);
      Set<String> idSet = Arrays.asList(ids).stream().collect(Collectors.toSet());
      assertTrue("getLayerIds: Has transcript layer",
                 idSet.contains("transcript"));

      Layer[] layers = labbcat.getLayers();
      //for (String id : ids) System.out.println("layer " + id);
      assertTrue("getLayers: Some IDs are returned",
                 layers.length > 0);

      ids = labbcat.getCorpusIds();
      // for (String id : ids) System.out.println("corpus " + id);
      assertTrue("getCorpusIds: Some IDs are returned",
                 ids.length > 0);
      String corpus = ids[0];

      ids = labbcat.getParticipantIds();
      // for (String id : ids) System.out.println("participant " + id);
      assertTrue("getParticipantIds: Some IDs are returned",
                 ids.length > 0);
      String participantId = ids[0];

      ids = labbcat.getGraphIds();
      // for (String id : ids) System.out.println("graph " + id);
      assertTrue("getGraphIds: Some IDs are returned",
                 ids.length > 0);

      long count = labbcat.countMatchingParticipantIds("id MATCHES '.+'");
      assertTrue("countMatchingParticipantIds: There are some matches",
                 count > 0);

      ids = labbcat.getMatchingParticipantIds("id MATCHES '.+'");
      assertTrue("getMatchingParticipantIds: Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2)
      {
         System.out.println("getMatchingParticipantIds: Too few participants to test pagination");
      }
      else
      {
         ids = labbcat.getMatchingParticipantIds("id MATCHES '.+'", 2, 0);
         assertEquals("getMatchingParticipantIds: Two IDs are returned",
                      2, ids.length);
      }

      ids = labbcat.getGraphIdsInCorpus(corpus);
      assertTrue("getGraphIdsInCorpus: Some IDs are returned for corpus " + corpus,
                 ids.length > 0);

      ids = labbcat.getGraphIdsWithParticipant(participantId);
      assertTrue("getGraphIdsWithParticipant: Some IDs are returned for participant " + participantId,
                 ids.length > 0);

      count = labbcat.countMatchingGraphIds("id MATCHES '.+'");
      assertTrue("countMatchingGraphIds: There are some matches",
                 count > 0);

      ids = labbcat.getMatchingGraphIds("id MATCHES '.+'");
      assertTrue("countMatchingGraphIds: Some IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      if (ids.length < 2)
      {
         System.out.println("countMatchingGraphIds: Too few graphs to test pagination");
      }
      else
      {
         ids = labbcat.getMatchingGraphIds("id MATCHES '.+'", 2, 0, "id DESC");
         assertEquals("getMatchingGraphIds: Two IDs are returned",
                      2, ids.length);
      }         
      
      count = labbcat.countAnnotations(graphId, "orthography");
      assertTrue("countAnnotations: There are some matches",
                 count > 0);
      
      Annotation[] annotations = labbcat.getAnnotations(graphId, "orthography", 2, 0);
      if (count < 2)
      {
         System.out.println("getAnnotations: Too few annotations to test pagination");
      }
      else
      {
         assertEquals("getAnnotations: Two annotations are returned",
                      2, annotations.length);
      }
      if (annotations.length == 0)
      {
         System.out.println("getAnchors: Can't test getAnchors() - no annotations in " + graphId);
      }
      else
      {
         // create an array of anchorIds
         String[] anchorIds = new String[annotations.length];
         for (int i = 0; i < annotations.length; i++) anchorIds[i] = annotations[i].getStartId();

         // finally, get the anchors
         Anchor[] anchors = labbcat.getAnchors(graphId, anchorIds);         
         assertEquals("getAnchors: Correct number of anchors is returned",
                      anchorIds.length, anchors.length);
      }

      String url = labbcat.getMedia(graphId, "", "audio/wav");
      assertNotNull("getMedia: There is some media",
                    url);

      Layer layer = labbcat.getLayer("orthography");
      assertEquals("getLayer: Correct layer",
                   "orthography", layer.getId());

      Annotation participant = labbcat.getParticipant(participantId);
      assertEquals("getParticipant: Correct participant",
                   participantId, participant.getLabel()); // not getId()

      count = labbcat.countMatchingAnnotations(
         "layer.id = 'orthography' AND label MATCHES 'and'");
      assertTrue("countMatchingAnnotations: There are some matches",
                 count > 0);

      annotations = labbcat.getMatchingAnnotations(
         "layer.id = 'orthography' AND label MATCHES 'and'", 2, 0);
      assertEquals("getMatchingAnnotations: Two annotations are returned",
                   2, annotations.length);

      MediaTrackDefinition[] tracks = labbcat.getMediaTracks();
      assertTrue("getMediaTracks: Some tracks are returned",
                 tracks.length > 0);

      // get some annotations so we have valid anchor IDs
      MediaFile[] files = labbcat.getAvailableMedia(graphId);
      assertTrue("getAvailableMedia: " + graphId + " has some tracks",
                 files.length > 0);

      // get some annotations so we have valid anchor IDs
      MediaFile[] docs = labbcat.getEpisodeDocuments(graphId);
      if (docs.length == 0)
      {
         System.out.println("getEpisodeDocuments: " + graphId + " has no documents");
      }
   }

   @Test public void getTasks()
      throws Exception
   {
      Map<String,TaskStatus> tasks = labbcat.getTasks();
      // not sure what we expect, but let's just print out what we got
      for (String id : tasks.keySet()) System.out.println("task " + id + ": " + tasks.get(id));
   }

   @Test public void taskStatus()
      throws Exception
   {
      // first get a list of tasks
      Map<String,TaskStatus> tasks = labbcat.getTasks();
      if (tasks.size() == 0)
      {
         System.out.println("There are no tasks, so can't test getTask");
      }
      else
      {
         String threadId = tasks.keySet().iterator().next();
         TaskStatus task = labbcat.taskStatus(threadId);
         assertEquals("Correct task",
                      threadId, task.getThreadId());
      }
   }

   @Test public void waitForTask()
      throws Exception
   {
      // first get a list of tasks
      Map<String,TaskStatus> tasks = labbcat.getTasks();
      if (tasks.size() == 0)
      {
         System.out.println("There are no tasks, so can't test getTask");
      }
      else
      {
         String threadId = tasks.keySet().iterator().next();
         TaskStatus task = labbcat.waitForTask(threadId, 1);
         assertEquals("Correct task",
                      threadId, task.getThreadId());
      }
   }

   @Test public void newTranscriptUpdateTranscriptAndDeleteGraph()
      throws Exception
   {
      // first get a corpus and transcript type
      String[] ids = labbcat.getCorpusIds();
      // for (String id : ids) System.out.println("corpus " + id);
      assertTrue("There is at least one corpus", ids.length > 0);
      String corpus = ids[0];
      Layer typeLayer = labbcat.getLayer("transcript_type");
      assertTrue("There is at least one transcript type", typeLayer.getValidLabels().size() > 0);
      String transcriptType = typeLayer.getValidLabels().keySet().iterator().next();

      File transcript = new File("nzilbb/labbcat/test/nzilbb.labbcat.test.txt");
      assertTrue("Test transcript exists", transcript.exists());
      try
      {
         String threadId = labbcat.newTranscript(
            transcript, null, null, transcriptType, corpus, "test");
         
         TaskStatus task = labbcat.waitForTask(threadId, 30);
         assertFalse("Upload task finished in a timely manner",
                     task.getRunning());
         
         labbcat.releaseTask(threadId);
         
         // ensure the transcript exists
         assertEquals("Transcript is in the store",
                      1, labbcat.countMatchingGraphIds("id = '"+transcript.getName()+"'"));

         // re-upload it
         threadId = labbcat.updateTranscript(transcript);
         
         task = labbcat.waitForTask(threadId, 30);
         assertFalse("Re-upload task finished in a timely manner",
                     task.getRunning());
         
         labbcat.releaseTask(threadId);
         
         // ensure the transcript exists
         assertEquals("Transcript is still in the store",
                      1, labbcat.countMatchingGraphIds("id = '"+transcript.getName()+"'"));
      }
      finally
      {
         // delete it
         labbcat.deleteGraph(transcript.getName());
         
         // ensure the transcript no longer exists
         assertEquals("Transcript has been deleted from the store",
                      0, labbcat.countMatchingGraphIds("id = '"+transcript.getName()+"'"));
      }

   }

   @Test(expected = StoreException.class) public void getTaskInvalidNumericId()
      throws Exception
   {
      TaskStatus task = labbcat.taskStatus("99999");
   }

   @Test(expected = StoreException.class) public void getTaskInvalidAlphaId()
      throws Exception
   {
      TaskStatus task = labbcat.taskStatus("invalid taskId");
   }

   public static void main(String args[]) 
   {
      org.junit.runner.JUnitCore.main("nzilbb.labbcat.test.TestLabbcat");
   }
}
