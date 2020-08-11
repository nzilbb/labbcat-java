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
import nzilbb.ag.serialize.SerializationDescriptor;
import nzilbb.labbcat.*;
import nzilbb.labbcat.model.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Unit tests for LabbcatView.
 * <p>These tests test the functionality of the client library, not the server. 
 * <p>They assume the existence of a valid LaBB-CAT instance (configured by
 * <var>labbcatUrl</var>) which responds correctly to requests, but do not generally test
 * that the server behaves correctly , nor assume specific corpus content. For the tests
 * to work, the first graph listed in LaBB-CAT must have some words and some media, and
 * the first participant listed must have some transcripts. 
 */
public class TestLabbcatView {
   
   // YOU MUST ENSURE THE FOLLOWING SETTINGS ARE VALID FOR YOU TEST LABBCAT SERVER:
   static String labbcatUrl = "http://localhost:8080/labbcat/";
   static String username = "labbcat";
   static String password = "labbcat";
   static LabbcatView labbcat;

   @BeforeClass public static void createStore() {
      try {
         labbcat = new LabbcatView(labbcatUrl, username, password).setBatchMode(true);
      } catch(MalformedURLException exception) {
         fail("Could not create LabbcatView object");
      }
   }

   @After public void notVerbose() {
      labbcat.setVerbose(false);
   }

   @Test(expected = StoreException.class) public void invalidCredentials()
      throws Exception {
      LabbcatView labbcat = new LabbcatView(labbcatUrl, "xxx", "xxx")
         .setBatchMode(true);
      labbcat.getId();
   }

   @Test(expected = StoreException.class) public void credentialsRequired()
      throws Exception {
      LabbcatView labbcat = new LabbcatView(labbcatUrl)
         .setBatchMode(true);
      labbcat.getId();
   }
   
   @Test(expected = MalformedURLException.class) public void malformedURLException()
      throws Exception {
      LabbcatView labbcat = new LabbcatView("xxx", username, password)
         .setBatchMode(true);
      labbcat.getId();
   }

   @Test(expected = StoreException.class) public void nonLabbcatUrl()
      throws Exception {
      LabbcatView labbcat = new LabbcatView("http://tld/", username, password)
         .setBatchMode(true);
      labbcat.getId();
   }

   @Test public void getId()
      throws Exception {
      String id = labbcat.getId();
      assertEquals("ID matches the url",
                   labbcatUrl, id);
   }

   @Test public void getLayerIds() throws Exception {
      String[] ids = labbcat.getLayerIds();
      //for (String id : ids) System.out.println("layer " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      Set<String> idSet = Arrays.asList(ids).stream().collect(Collectors.toSet());
      assertTrue("Has transcript layer",
                 idSet.contains("transcript"));
      assertTrue("Has turns layer",
                 idSet.contains("turns"));
      assertTrue("Has utterances layer",
                 idSet.contains("utterances"));
      assertTrue("Has transcript_type layer",
                 idSet.contains("transcript_type"));
   }

   @Test public void getLayers() throws Exception {
      Layer[] layers = labbcat.getLayers();
      //for (String id : ids) System.out.println("layer " + id);
      assertTrue("Some IDs are returned",
                 layers.length > 0);
      Set<Object> idSet = Arrays.asList(layers).stream()
         .map(l->l.getId())
         .collect(Collectors.toSet());
      assertTrue("Has transcript layer",
                 idSet.contains("transcript"));
      assertTrue("Has turns layer",
                 idSet.contains("turns"));
      assertTrue("Has utterances layer",
                 idSet.contains("utterances"));
      assertTrue("Has transcript_type layer",
                 idSet.contains("transcript_type"));
   }

   @Test public void getCorpusIds() throws Exception {
      String[] ids = labbcat.getCorpusIds();
      // for (String id : ids) System.out.println("corpus " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void getParticipantIds() throws Exception {
      String[] ids = labbcat.getParticipantIds();
      // for (String id : ids) System.out.println("participant " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void getTranscriptIds() throws Exception {
      String[] ids = labbcat.getTranscriptIds();
      // for (String id : ids) System.out.println("graph " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void countMatchingParticipantIds() throws Exception {
      int count = labbcat.countMatchingParticipantIds("/.+/.test(id)");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMatchingParticipantIds() throws Exception {
      String[] ids = labbcat.getMatchingParticipantIds("/.+/.test(id)");
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2) {
         System.out.println("Too few participants to test pagination");
      } else {
         ids = labbcat.getMatchingParticipantIds("/.+/.test(id)", 2, 0);
         assertEquals("Two IDs are returned",
                      2, ids.length);
      }         
   }

   @Test public void getTranscriptIdsInCorpus() throws Exception {
      String[] ids = labbcat.getCorpusIds();
      assertTrue("There's at least one corpus",
                 ids.length > 0);
      String corpus = ids[0];
      ids = labbcat.getTranscriptIdsInCorpus(corpus);
      assertTrue("Some IDs are returned for corpus " + corpus,
                 ids.length > 0);
   }

   @Test public void getTranscriptIdsWithParticipant() throws Exception {
      String[] ids = labbcat.getParticipantIds();
      assertTrue("There's at least one participant",
                 ids.length > 0);
      String participant = ids[0];
      ids = labbcat.getTranscriptIdsWithParticipant(participant);
      assertTrue("Some IDs are returned for participant " + participant,
                 ids.length > 0);
   }

   @Test public void countMatchingTranscriptIds() throws Exception {
      int count = labbcat.countMatchingTranscriptIds("/.+/.test(id)");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMatchingTranscriptIds() throws Exception {
      String[] ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)");
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2) {
         System.out.println("Too few graphs to test pagination");
      } else {
         ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)", 2, 0, "id DESC");
         assertEquals("Two IDs are returned",
                      2, ids.length);
      }         
   }

   @Test public void countAnnotations() throws Exception {
      String[] ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      long count = labbcat.countAnnotations(graphId, "orthography");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getAnnotations() throws Exception {
      String[] ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      
      long count = labbcat.countAnnotations(graphId, "orthography");
      Annotation[] annotations = labbcat.getAnnotations(graphId, "orthography", 2, 0);
      if (count < 2) {
         System.out.println("Too few annotations to test pagination");
      } else {
         assertEquals("Two annotations are returned",
                      2, annotations.length);
      }
   }

   @Test public void getAnchors() throws Exception {
      // get a graph to work with
      String[] ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];

      // get some annotations so we have valid anchor IDs
      Annotation[] annotations = labbcat.getAnnotations(graphId, "orthography", 2, 0);
      if (annotations.length == 0) {
         System.out.println("Can't test getAnchors() - no annotations in " + graphId);
      } else {
         // create an array of anchorIds
         String[] anchorIds = new String[annotations.length];
         for (int i = 0; i < annotations.length; i++) anchorIds[i] = annotations[i].getStartId();

         // finally, get the anchors
         Anchor[] anchors = labbcat.getAnchors(graphId, anchorIds);         
         assertEquals("Correct number of anchors is returned",
                      anchorIds.length, anchors.length);
      }
   }
   
   @Test public void getMedia() throws Exception {
      String[] ids = labbcat.getMatchingTranscriptIds("/AP511.+\\.eaf/.test(id)", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      String url = labbcat.getMedia(graphId, "", "audio/wav");
      assertNotNull("There is some media (check the first graph listed, "+graphId+")",
                    url);
   }

   @Test public void getMediaFragment() throws Exception {
      String[] ids = labbcat.getMatchingTranscriptIds("/AP511.+\\.eaf/.test(id)", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      String url = labbcat.getMedia(graphId, "", "audio/wav", 1.0, 2.0);
      assertNotNull("There is some media",
                    url);
   }

   @Test public void getLayer() throws Exception {
      Layer layer = labbcat.getLayer("orthography");
      assertEquals("Correct layer",
                   "orthography", layer.getId());
   }

   @Test public void getParticipant() throws Exception {
      // find a participant ID to use
      String[] ids = labbcat.getParticipantIds();
      // for (String id : ids) System.out.println("participant " + id);
      assertTrue("Some participant IDs exist",
                 ids.length > 0);
      String participantId = ids[0];
      Annotation participant = labbcat.getParticipant(participantId);
      assertEquals("Correct participant",
                   participantId, participant.getLabel()); // not getId()
   }

   @Test public void countMatchingAnnotations() throws Exception {
      int count = labbcat.countMatchingAnnotations(
         "layer.id == 'orthography' && label == 'and'");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMatchingAnnotations() throws Exception {
      Annotation[] annotations = labbcat.getMatchingAnnotations(
         "layer.id == 'orthography' && label == 'and'", 2, 0);
      assertEquals("Two annotations are returned",
                   2, annotations.length);
   }

   @Test public void getMediaTracks() throws Exception {
      MediaTrackDefinition[] tracks = labbcat.getMediaTracks();
      //for (String track : tracks) System.out.println("track " + track);
      assertTrue("Some tracks are returned",
                 tracks.length > 0);
      Set<Object> idSet = Arrays.asList(tracks).stream()
         .map(l->l.getSuffix())
         .collect(Collectors.toSet());
      assertTrue("Has default track",
                 idSet.contains(""));
   }
   
   @Test public void getAvailableMedia() throws Exception {
      // get a graph to work with
      String[] ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];

      // get some annotations so we have valid anchor IDs
      MediaFile[] files = labbcat.getAvailableMedia(graphId);
      assertTrue(graphId + " has some tracks",
                 files.length > 0);
   }
   
   @Test public void getEpisodeDocuments() throws Exception {
      // get a graph to work with
      String[] ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];

      // get some annotations so we have valid anchor IDs
      MediaFile[] files = labbcat.getEpisodeDocuments(graphId);
      if (files.length == 0) System.out.println(graphId + " has no documents");
   }
   
   @Test(expected = StoreException.class) public void getTaskInvalidNumericId()
      throws Exception {
      TaskStatus task = labbcat.taskStatus("99999");
   }

   @Test(expected = StoreException.class) public void getTaskInvalidAlphaId()
      throws Exception {
      TaskStatus task = labbcat.taskStatus("invalid taskId");
   }

   @Test public void getTasks()
      throws Exception {
      Map<String,TaskStatus> tasks = labbcat.getTasks();
      // not sure what we expect, but let's just print out what we got
      System.out.println("Some tasks:");
      for (String id : tasks.keySet()) System.out.println("task " + id + ": " + tasks.get(id));
   }

   @Test public void taskStatus()
      throws Exception {
      // first get a list of tasks
      Map<String,TaskStatus> tasks = labbcat.getTasks();
      if (tasks.size() == 0) {
         System.out.println("There are no tasks, so can't test getTask");
      } else {
         String threadId = tasks.keySet().iterator().next();
         TaskStatus task = labbcat.taskStatus(threadId);
         assertEquals("Correct task",
                      threadId, task.getThreadId());
      }
   }

   @Test public void waitForTask()
      throws Exception {
      // first get a list of tasks
      Map<String,TaskStatus> tasks = labbcat.getTasks();
      if (tasks.size() == 0) {
         System.out.println("There are no tasks, so can't test waitForTask");
      } else {
         String threadId = tasks.keySet().iterator().next();
         TaskStatus task = labbcat.waitForTask(threadId, 1);
         assertEquals("Correct task",
                      threadId, task.getThreadId());
      }
   }

   @Test(expected = StoreException.class) public void searchInvalidPattern()
      throws Exception {
      String threadId = labbcat.search(new JSONObject(), null, null, false, false, null);
   }

   @Test public void searchAndCancelTask()
      throws Exception {
      // start a long-running search - all words
      JSONObject pattern = new JSONObject()
         .put("columns", new JSONArray()
              .put(new JSONObject()
                   .put("layers", new JSONObject()
                        .put("orthography", new JSONObject()
                             .put("pattern", ".*")))));
      String threadId = labbcat.search(pattern, null, null, false, false, null);
      labbcat.cancelTask(threadId);
   }

   @Test public void searchAndGetMatchesAndGetMatchAnnotations()
      throws Exception {
      // get a participant ID to use
      String[] ids = labbcat.getParticipantIds();
      assertTrue("getParticipantIds: Some IDs are returned",
                 ids.length > 0);
      String[] participantId = { ids[0] };

      // all instances of "and"
      JSONObject pattern = new PatternBuilder().addMatchLayer("orthography", "and").build();
      String threadId = labbcat.search(pattern, participantId, null, false, false, null);
      try {
         TaskStatus task = labbcat.waitForTask(threadId, 30);
         // if the task is still running, it's taking too long, so cancel it
         if (task.getRunning()) try { labbcat.cancelTask(threadId); } catch(Exception exception) {}
         assertFalse("Search task finished in a timely manner",
                     task.getRunning());
         
         Match[] matches = labbcat.getMatches(threadId, 2);
         if (matches.length == 0) {
            System.out.println(
               "getMatches: No matches were returned, cannot test getMatchAnnotations");
         } else {
            int upTo = Math.min(10, matches.length);
            // for (int m = 0; m < upTo; m++) System.out.println("Match: " + matches[m]);

            matches = labbcat.getMatches(threadId, 2, upTo, 0);
            assertEquals("pagination works ("+upTo+")",
                         upTo, matches.length);

            String[] layerIds = { "orthography" };
            Annotation[][] annotations = labbcat.getMatchAnnotations(matches, layerIds, 0, 1);
            assertEquals("annotations array is same size as matches array",
                         matches.length, annotations.length);
            assertEquals("row arrays are the right size",
                         1, annotations[0].length);

            layerIds[0] = "invalid layer ID";
            try {
               labbcat.getMatchAnnotations(matches, layerIds, 0, 1);
               fail("getMatchAnnotations with invalid layerId should fail");
            } catch(StoreException exception) {}
         }
      } finally {
         labbcat.releaseTask(threadId);
      }
   }

   @Test public void getSoundFragments()
      throws Exception {
      // get a participant ID to use
      String[] ids = labbcat.getParticipantIds();
      assertTrue("getParticipantIds: Some IDs are returned",
                 ids.length > 0);
      String[] participantId = { ids[0] };      

      // all instances of "and"
      JSONObject pattern = new PatternBuilder().addMatchLayer("orthography", "and").build();
      String threadId = labbcat.search(pattern, participantId, null, false, false, null);
      try {
         TaskStatus task = labbcat.waitForTask(threadId, 30);
         // if the task is still running, it's taking too long, so cancel it
         if (task.getRunning()) try { labbcat.cancelTask(threadId); } catch(Exception exception) {}
         assertFalse("Search task finished in a timely manner",
                     task.getRunning());
         
         Match[] matches = labbcat.getMatches(threadId, 2);
         if (matches.length == 0) {
            System.out.println(
               "getMatches: No matches were returned, cannot test getSoundFragments");
         } else {
            int upTo = Math.min(5, matches.length);
            Match[] subset = Arrays.copyOfRange(matches, 0, upTo);

            File[] wavs = labbcat.getSoundFragments(subset, null, null);
            try {
               assertEquals("files array is same size as matches array",
                            subset.length, wavs.length);
               
               for (int m = 0; m < upTo; m++) {
                  assertNotNull("Non-null sized file: " + subset[m],
                                wavs[m]);
                  assertTrue("Non-zero sized file: " + subset[m],
                             wavs[m].length() > 0);
                  // System.out.println(wavs[m].getPath());
               }
            } finally {
               for (File wav : wavs) if (wav != null) wav.delete(); 
            }
         }
      } finally {
         labbcat.releaseTask(threadId);
      }
   }

   @Test public void getFragments()
      throws Exception {
      // get a participant ID to use
      String[] ids = labbcat.getParticipantIds();
      assertTrue("getParticipantIds: Some IDs are returned",
                 ids.length > 0);
      String[] participantId = { ids[0] };      

      // all instances of "and"
      JSONObject pattern = new PatternBuilder().addMatchLayer("orthography", "and").build();
      String threadId = labbcat.search(pattern, participantId, null, false, false, null);
      try {
         TaskStatus task = labbcat.waitForTask(threadId, 30);
         // if the task is still running, it's taking too long, so cancel it
         if (task.getRunning()) try { labbcat.cancelTask(threadId); } catch(Exception exception) {}
         assertFalse("Search task finished in a timely manner",
                     task.getRunning());
         
         Match[] matches = labbcat.getMatches(threadId, 2);
         if (matches.length == 0) {
            System.out.println(
               "getMatches: No matches were returned, cannot test getFragments");
         } else {
            int upTo = Math.min(5, matches.length);
            Match[] subset = Arrays.copyOfRange(matches, 0, upTo);

            File dir = new File("getFragments");
            String[] layerIds = { "orthography" };
            File[] fragments = labbcat.getFragments(subset, layerIds, "text/praat-textgrid", dir);
            try {
               assertEquals("files array is same size as matches array",
                            subset.length, fragments.length);
               
               for (int m = 0; m < upTo; m++) {
                  assertNotNull("Non-null sized file: " + subset[m],
                                fragments[m]);
                  assertTrue("Non-zero sized file: " + subset[m],
                             fragments[m].length() > 0);
                  // System.out.println(fragments[m].getPath());
               }
            } finally {
               for (File fragment : fragments) if (fragment != null) fragment.delete();
               dir.delete();
            }
         }
      } finally {
         labbcat.releaseTask(threadId);
      }
   }

   @Test public void getTranscriptAttributes()
      throws Exception {
      // get a participant ID to use
      String[] ids = labbcat.getMatchingTranscriptIds("/BR.+/.test(id)");
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      String[] layerIds = { "transcript_type", "corpus" };      

      File csv = labbcat.getTranscriptAttributes(ids, layerIds);
      assertNotNull("File returned", csv);
      assertTrue("File exists", csv.exists());
      csv.delete();
   }

   @Test public void getParticipantAttributes()
      throws Exception {
      // get a participant ID to use
      String[] ids = labbcat.getMatchingParticipantIds("/BR.+/.test(id)");
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      String[] layerIds = { "participant_gender", "participant_nodes" };      

      File csv = labbcat.getParticipantAttributes(ids, layerIds);
      assertNotNull("File returned", csv);
      assertTrue("File exists", csv.exists());
      csv.delete();
   }

   @Test public void getSerializerDescriptors() throws Exception {
      SerializationDescriptor[] descriptors = labbcat.getSerializerDescriptors();
      // for (SerializationDescriptor descriptor : descriptors) System.out.println("descriptor " + descriptor);
      assertTrue("Some descriptors are returned",
                 descriptors.length > 0);
      Set<Object> mimeTypeSet = Arrays.asList(descriptors).stream()
         .map(l->l.getMimeType())
         .collect(Collectors.toSet());
      assertTrue("Has plain text serialization: " + mimeTypeSet,
                 mimeTypeSet.contains("text/plain"));
   }
   
   @Test public void getDeserializerDescriptors() throws Exception {
      SerializationDescriptor[] descriptors = labbcat.getDeserializerDescriptors();
      for (SerializationDescriptor descriptor : descriptors) System.out.println("descriptor " + descriptor);
      assertTrue("Some descriptors are returned",
                 descriptors.length > 0);
      Set<Object> mimeTypeSet = Arrays.asList(descriptors).stream()
         .map(l->l.getMimeType())
         .collect(Collectors.toSet());
      assertTrue("Has plain text serialization: " + mimeTypeSet,
                 mimeTypeSet.contains("text/plain"));
   }
   
   @Test public void getSystemAttribute() throws Exception {
      String value = labbcat.getSystemAttribute("title");
      assertNotNull("Value returned", value);
      labbcat.setVerbose(true);
      value = labbcat.getSystemAttribute("doesn't exist");
      assertNull("Value returned", value);
   }
   
   public static void main(String args[]) {
      org.junit.runner.JUnitCore.main("nzilbb.labbcat.test.TestLabbcatView");
   }
}
