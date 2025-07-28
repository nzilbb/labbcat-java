//
// Copyright 2020-2025 New Zealand Institute of Language, Brain and Behaviour, 
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

package nzilbb.labbcat;
	      
import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArrayBuilder;
import nzilbb.ag.Anchor;
import nzilbb.ag.Annotation;
import nzilbb.ag.Graph;
import nzilbb.ag.Layer;
import nzilbb.ag.MediaFile;
import nzilbb.ag.MediaTrackDefinition;
import nzilbb.ag.Schema;
import nzilbb.ag.StoreException;
import nzilbb.ag.serialize.SerializationDescriptor;
import nzilbb.ag.automation.util.AnnotatorDescriptor;
import nzilbb.labbcat.model.*;
import nzilbb.labbcat.http.HttpRequestGet;
import nzilbb.util.IO;

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
    // labbcat.setVerbose(true);
    String id = labbcat.getId();
    assertEquals("ID matches the url",
                 labbcatUrl, id);
  }

  @Test public void versionInfo()
    throws Exception {
    // labbcat.setVerbose(true);
    Map<String,Map<String,String>> versions = labbcat.versionInfo();
    assertNotNull("Versions returned", versions);
    assertTrue("System info present", versions.containsKey("System"));
    assertTrue("Format info present", versions.containsKey("Formats"));
    assertTrue("LaBB-CAT version present", versions.get("System").containsKey("LaBB-CAT"));
  }

  @Test public void readAgreement()
    throws Exception {
    String agreement = labbcat.readAgreement();
    // could be a string, or null. The test is that no exception was thrown.
    System.out.println("Data access agreement: " + agreement);
  }

  @Test public void getInfo()
    throws Exception {
    String info = labbcat.getInfo();
    // could be a string, or null. The test is that no exception was thrown.
    System.out.println("Corpus info: " + info);
  }

  @Test public void getLayerIds() throws Exception {
    String[] ids = labbcat.getLayerIds();
    //for (String id : ids) System.out.println("layer " + id);
    assertTrue("Some IDs are returned",
               ids.length > 0);
    Set<String> idSet = Arrays.asList(ids).stream().collect(Collectors.toSet());
    assertTrue("Has word layer",
               idSet.contains("word"));
    assertTrue("Has turns layer",
               idSet.contains("turn"));
    assertTrue("Has utterances layer",
               idSet.contains("utterance"));
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
    assertTrue("Has word layer",
               idSet.contains("word"));
    assertTrue("Has turns layer",
               idSet.contains("turn"));
    assertTrue("Has utterances layer",
               idSet.contains("utterance"));
    assertTrue("Has transcript_type layer",
               idSet.contains("transcript_type"));
  }

  @Test public void getCorpusIds() throws Exception {
    String[] ids = labbcat.getCorpusIds();
    // for (String id : ids) System.out.println("corpus " + id);
    assertTrue("Some IDs are returned",
               ids.length > 0);
  }

  /** Ensure corpus statistics can be retrieved. */
  @Test public void getCorpusInfo() throws Exception {
    String[] ids = labbcat.getCorpusIds();
    assertTrue("Some IDs are returned", ids.length > 0);
    Map<String,String> stats = labbcat.getCorpusInfo(ids[0]);
    assertTrue("Some stats are returned", stats.size() > 0);
    // System.out.println(""+stats);
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

  @Test public void getSchema() throws Exception {
    Schema schema = labbcat.getSchema();
    assertNotNull("A schema was returned",
                  schema);
    assertEquals("Schema has word layer set correctly",
                 "word", schema.getWordLayerId());
  }

  @Test public void getTranscript() throws Exception {
    String[] ids = labbcat.getMatchingTranscriptIds("/AP511.+\\.eaf/.test(id)", 1, 0);
    assertTrue("Some graph IDs are returned",
               ids.length > 0);
    String graphId = ids[0];
    String[] layers = {"word"};
    Graph transcript = labbcat.getTranscript(graphId, layers);
    assertNotNull("A graph was returned",
                  transcript);
    Annotation[] words = transcript.all("word");
    assertTrue("Graph includes annotations",
               words.length > 0);
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

  @Test public void getParticipantWithAttributes() throws Exception {
    // find a participant ID to use
    String[] ids = labbcat.getParticipantIds();
    // for (String id : ids) System.out.println("participant " + id);
    assertTrue("Some participant IDs exist",
               ids.length > 0);
    String participantId = ids[0];
    String[] attributes = { "participant_gender" };
    Annotation participant = labbcat.getParticipant(participantId, attributes);
    assertEquals("Correct participant",
                 participantId, participant.getLabel()); // not getId()
    assertTrue("Includes attribute",
               participant.getAnnotations(attributes[0]).size() > 0);
  }

  @Test public void getParticipantDoesntExist() throws Exception {
    // find a participant ID to use
    Annotation participant = labbcat.getParticipant("A nonexistent participant");
    assertNull("No exception, null returned", participant);
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

  /**
   * Ensure that aggregateMatchingAnnotations works.
   */
  @Test public void aggregateMatchingAnnotations() throws Exception {
    String[] count = labbcat.aggregateMatchingAnnotations(
      "COUNT", "layer.id == 'orthography' && label == 'and'");
    assertEquals("A single value was returned",
                 1, count.length);
    assertTrue("It looks like a number",
               count[0].matches("[0-9]+"));

    String[] distinctCount = labbcat.aggregateMatchingAnnotations(
      "DISTINCT,COUNT", "layer.id == 'orthography' && /a.*/.test(label)");
    assertTrue("Some values returned",
               distinctCount.length > 0);
    assertEquals("Even number of values returned",
                 0, distinctCount.length % 2);
    assertTrue("First value looks like an orthography",
               distinctCount[0].matches("[a-z0-9]+"));
    assertTrue("Second value looks like a number",
               distinctCount[1].matches("[0-9]+"));
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
    // ensure there's at least one task
    JsonObject pattern = Json.createObjectBuilder()
      .add("columns", Json.createArrayBuilder()
           .add(Json.createObjectBuilder()
                .add("layers", Json.createObjectBuilder()
                     .add("orthography", Json.createObjectBuilder()
                          .add("pattern", "xxx")))))
      .build();
    String threadId = labbcat.search(pattern, null, null, false, null, null, null);
    try {
   
      String[] tasks = labbcat.getTasks();
      assertNotNull("tasks were returned", tasks);
      assertTrue("At least one task was returned", tasks.length > 0);
      
    } finally {
      labbcat.releaseTask(threadId);
    }    
  }

  @Test public void taskStatus()
    throws Exception {
    // ensure there's a task
    JsonObject pattern = Json.createObjectBuilder()
      .add("columns", Json.createArrayBuilder()
           .add(Json.createObjectBuilder()
                .add("layers", Json.createObjectBuilder()
                     .add("orthography", Json.createObjectBuilder()
                          .add("pattern", "xxx")))))
      .build();
    String threadId = labbcat.search(pattern, null, null, false, null, null, null);
    try {
      TaskStatus task = labbcat.taskStatus(threadId);
      assertEquals("Correct task", threadId, task.getThreadId());
      assertNotNull("Has a status", task.getStatus());
      assertNull("Has no log", task.getLog());

      // ask for log
      task = labbcat.taskStatus(threadId, true, false);
      assertEquals("Correct task", threadId, task.getThreadId());
      assertNotNull("Has a status", task.getStatus());
      assertNotNull("Has log", task.getLog());
      
    } finally {
      labbcat.releaseTask(threadId);
    }
  }

  @Test public void waitForTask()
    throws Exception {
    // ensure there's at least one task
    JsonObject pattern = Json.createObjectBuilder()
      .add("columns", Json.createArrayBuilder()
           .add(Json.createObjectBuilder()
                .add("layers", Json.createObjectBuilder()
                     .add("orthography", Json.createObjectBuilder()
                          .add("pattern", "xxx")))))
      .build();
    String threadId = labbcat.search(pattern, null, null, false, null, null, null);
    try {  
      TaskStatus task = labbcat.waitForTask(threadId, 1);
      assertEquals("Correct task", threadId, task.getThreadId());
      
    } finally {
      labbcat.releaseTask(threadId);
    }    
  }

  /** Ensure searching with an invalid pattern correctly fails. */
  @Test(expected = StoreException.class) public void searchInvalidPattern()
    throws Exception {
    String threadId = labbcat.search(
      Json.createObjectBuilder().build(), null, null, false, null, null, null);
  }

  /** Ensure searches can be cancelled. */
  @Test public void searchAndCancelTask()
    throws Exception {
    // start a long-running search - all words
    JsonObject pattern = Json.createObjectBuilder()
      .add("columns", Json.createArrayBuilder()
           .add(Json.createObjectBuilder()
                .add("layers", Json.createObjectBuilder()
                     .add("orthography", Json.createObjectBuilder()
                          .add("pattern", ".*")))))
      .build();
    String threadId = labbcat.search(pattern, null, null, false, null, null, null);
    labbcat.cancelTask(threadId);
  }

  /** Ensure workflow of searching works, from specifying the search to retrieving results */
  @Test public void searchAndGetMatchesAndGetMatchAnnotations()
    throws Exception {
    // get a participant ID to use
    String[] ids = labbcat.getParticipantIds();
    assertTrue("getParticipantIds: Some IDs are returned",
               ids.length > 0);
    String[] participantId = { ids[0] };

    // all instances of "and"
    JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", "and").build();
    String threadId = labbcat.search(pattern, participantId, null, false, null, null, null);
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

  /** Ensure exclusion of overlapping speech in search results works. */
  @Test public void searchExcludingOverlappingSpeech()
    throws Exception {

    // all instances of "mmm", which are frequently used in overlapping speech
    JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", "mmm").build();
    Match[] includingOverlapping = labbcat.getMatches(
      pattern, null, null, false, null, null, null, 0);
    Match[] excludingOverlapping = labbcat.getMatches(
      pattern, null, null, false, null, null, 5, 0);
    assertTrue("There are fewer matches when overlapping speech is excluded",
               includingOverlapping.length > excludingOverlapping.length);
  }

  @Test public void getSoundFragments()
    throws Exception {
    // get a participant ID to use
    String[] ids = labbcat.getParticipantIds();
    assertTrue("getParticipantIds: Some IDs are returned",
               ids.length > 0);
    String[] participantId = { ids[0] };      

    // all instances of "and"
    JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", "and").build();
    String threadId = labbcat.search(pattern, participantId, null, false, null, null, null);
    try {
      TaskStatus task = labbcat.waitForTask(threadId, 30);
      // if the task is still running, it's taking too long, so cancel it
      if (task.getRunning()) try { labbcat.cancelTask(threadId); } catch(Exception exception) {}
      assertFalse("Search task finished in a timely manner",
                  task.getRunning());
         
      Match[] matches = labbcat.getMatches(threadId, 2);
      if (matches.length == 0) {
        fail("getMatches: No matches were returned, cannot test getSoundFragments");
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
    JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", "and").build();
    String threadId = labbcat.search(pattern, participantId, null, false, null, null, null);
    try {
      TaskStatus task = labbcat.waitForTask(threadId, 30);
      // if the task is still running, it's taking too long, so cancel it
      if (task.getRunning()) try { labbcat.cancelTask(threadId); } catch(Exception exception) {}
      assertFalse("Search task finished in a timely manner",
                  task.getRunning());
         
      Match[] matches = labbcat.getMatches(threadId, 2);
      if (matches.length == 0) {
        fail("getMatches: No matches were returned, cannot test getFragments");
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
            assertNotNull("Non-null file: " + subset[m],
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

  @Test public void processWithPraat()
    throws Exception {
    // get a participant ID to use
    String[] ids = labbcat.getParticipantIds();
    assertTrue("getParticipantIds: Some IDs are returned",
               ids.length > 0);
    String[] participantId = { ids[0] };      

    // all instances of any segment
    JsonObject pattern = new PatternBuilder().addMatchLayer("segment", ".*").build();
    String searchThreadId = labbcat.search(pattern, participantId, null, false, null, null, null);
    try {
      TaskStatus task = labbcat.waitForTask(searchThreadId, 30);
      // if the task is still running, it's taking too long, so cancel it
      if (task.getRunning()) {
        try { labbcat.cancelTask(searchThreadId); } catch(Exception exception) {}
      }
      assertFalse("Search task finished in a timely manner",
                  task.getRunning());
      
      Match[] matches = labbcat.getMatches(searchThreadId, 2);
      if (matches.length == 0) {
        fail("getMatches: No matches were returned, cannot test processWithPraat");
      } else {
        int upTo = Math.min(5, matches.length);
        Match[] subset = Arrays.copyOfRange(matches, 0, upTo);
        String[] matchIds = Arrays.stream(subset)
          .map(match -> match.getMatchId())
          .collect(Collectors.toList()).toArray(new String[0]);
        Double[] startOffsets = Arrays.stream(subset)
          .map(match -> match.getLine())
          .collect(Collectors.toList()).toArray(new Double[0]);
        Double[] endOffsets = Arrays.stream(subset)
          .map(match -> match.getLineEnd())
          .collect(Collectors.toList()).toArray(new Double[0]);
        String script = "test$ = \"test\"\nprint 'test$' 'newline$'";
        String praatThreadId = labbcat.processWithPraat(
          matchIds, startOffsets, endOffsets, script, 0.0, null);
        try {
          task = labbcat.waitForTask(praatThreadId, 30);
          // if the task is still running, it's taking too long, so cancel it
          if (task.getRunning()) {
            try { labbcat.cancelTask(praatThreadId); } catch(Exception exception) {}
          }
          assertFalse("Search task finished in a timely manner",
                      task.getRunning());
          
          assertTrue("Output is a CSV URL: " + task.getResultUrl(),
                     task.getResultUrl().endsWith(".csv"));

          // download URL
          HttpRequestGet request = new HttpRequestGet(
            task.getResultUrl(), labbcat.getRequiredHttpAuthorization());
          String csv = IO.InputStreamToString(request.get().getInputStream());
          assertEquals("CSV result is correct: " + csv,
                       "test,Error\n\"test \",\n\"test \",\n\"test \",\n\"test \",\n\"test \",",
                       csv);          
        } finally {
          labbcat.releaseTask(praatThreadId);
        }
      }
    } finally {
      labbcat.releaseTask(searchThreadId);
    }
  }

  /** Test that annotation labels between start/end times can be extracted. */
  @Test public void intervalAnnotations()
    throws Exception {
    // get a participant ID to use
    String[] ids = labbcat.getParticipantIds();
    assertTrue("getParticipantIds: Some IDs are returned",
               ids.length > 0);
    String[] participantId = { ids[0] };      
    
    // all instances of and
    JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", "and").build();
    String searchThreadId = labbcat.search(pattern, participantId, null, false, null, null, null);
    try {
      TaskStatus task = labbcat.waitForTask(searchThreadId, 30);
      // if the task is still running, it's taking too long, so cancel it
      if (task.getRunning()) {
        try { labbcat.cancelTask(searchThreadId); } catch(Exception exception) {}
      }
      assertFalse("Search task finished in a timely manner", task.getRunning());

      String[] layerIds = { "orthography" };
      Match[] matches = labbcat.getMatches(searchThreadId, 2);
      if (matches.length == 0) {
        fail("getMatches: No matches were returned, cannot test processWithPraat");
      } else {
        int upTo = Math.min(5, matches.length);
        Match[] subset = Arrays.copyOfRange(matches, 0, upTo);
        String[] transcriptIds = Arrays.stream(subset)
          .map(match -> match.getTranscript())
          .collect(Collectors.toList()).toArray(new String[0]);
        String[] participantIds = Arrays.stream(subset)
          .map(match -> match.getParticipant())
          .collect(Collectors.toList()).toArray(new String[0]);
        Double[] startOffsets = Arrays.stream(subset)
          .map(match -> match.getLine())
          .collect(Collectors.toList()).toArray(new Double[0]);
        Double[] endOffsets = Arrays.stream(subset)
          .map(match -> match.getLineEnd())
          .collect(Collectors.toList()).toArray(new Double[0]);
        String extractionThreadId = labbcat.intervalAnnotations(
          transcriptIds, participantIds, startOffsets, endOffsets, layerIds, " ", false);
        try {
          task = labbcat.waitForTask(extractionThreadId, 30);
          // if the task is still running, it's taking too long, so cancel it
          if (task.getRunning()) {
            try { labbcat.cancelTask(extractionThreadId); } catch(Exception exception) {}
          }
          assertFalse("Extraction task finished in a timely manner",
                      task.getRunning());
          
          assertTrue("Output is a CSV URL: " + task.getResultUrl(),
                     task.getResultUrl().endsWith(".csv"));
          
          // download URL
          HttpRequestGet request = new HttpRequestGet(
            task.getResultUrl(), labbcat.getRequiredHttpAuthorization());
          String csv = IO.InputStreamToString(request.get().getInputStream());
          String[] rows = csv.split("\n");
          assertEquals("CSV header is correct: " + csv,
                       "orthography,orthography start,orthography end", rows[0]);          
        } finally {
          labbcat.releaseTask(extractionThreadId);
        }
      }
    } finally {
      labbcat.releaseTask(searchThreadId);
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
    String[] layerIds = { "participant_gender", "participant_notes" };      

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
    // for (SerializationDescriptor descriptor : descriptors) System.out.println("descriptor " + descriptor);
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
    value = labbcat.getSystemAttribute("doesn't exist");
    assertNull("Value returned", value);
  }
   
  @Test public void getUserInfo() throws Exception {
    User user = labbcat.getUserInfo();
    assertNotNull("Value returned", user);
    // user.getUser() might be null, if there's no user auth 
    assertNotNull("Roles returned", user.getRoles());
    assertTrue("There are roles", user.getRoles().length > 0);
  }

  @Test public void getAnnotatorDescriptor() throws Exception {
    AnnotatorDescriptor descriptor = labbcat.getAnnotatorDescriptor("OrthographyStandardizer");
    assertNotNull("Descriptor returned", descriptor);
    assertFalse("No config webapp", descriptor.hasConfigWebapp());
    assertFalse("No ext webapp", descriptor.hasExtWebapp());
    assertTrue("Has tasl webapp", descriptor.hasTaskWebapp());
    assertNotNull("Has info", descriptor.getInfo());
    assertNotNull("Has task parameter info", descriptor.getTaskParameterInfo());
    assertNotNull("Has version", descriptor.getVersion());
  }

  /** Test the /api/password request is generally handled. */
  @Test public void changePassword() throws Exception {
    // make a valid request (but leave the password the same)
    labbcat.changePassword(password, password);
    
    // send an invalid current password (but leave the password the same)
    try {
      labbcat.changePassword("wrong current password", password);
      fail("Incorrect password throws exception");
    } catch(StoreException exception) {
      // System.out.println(""+exception);
    }
  }

  /** Ensure dashboard items can be retrieved. */
  @Test public void getDashboardItems() throws Exception {
    DashboardItem[] items = labbcat.getDashboardItems("home");
    assertTrue("Some items are returned", items.length > 0);
  }
  
  /** Ensure a single dashboard item value can be retrieved. */
  @Test public void getDashboardItem() throws Exception {
    // get items
    DashboardItem[] items = labbcat.getDashboardItems("statistics");
    assertTrue("Some items are returned", items.length > 0);
    // now get the first item's value
    String value = labbcat.getDashboardItem(items[0].getItemId());
    assertNotNull("Value was returned", value);
    assertTrue("Value is not empty", value.length() > 0);
  }
  
  /** Ensure a list of categories can be retrieved. */
  @Test public void readCategories() throws Exception {
    Category[] categories = labbcat.readCategories("transcript");
    assertNotNull("Categories returned", categories);
    assertTrue("At least one should be returned", categories.length > 0);
    assertEquals("Class ID correct", "transcript", categories[0].getClassId());
    assertNotNull("Name set", categories[0].getCategory());
  }
  
  /**
   * Directory for text files.
   * @see #getDir()
   * @see #setDir(File)
   */
  protected File fDir;
  /**
   * Getter for {@link #fDir}: Directory for text files.
   * @return Directory for text files.
   */
  public File getDir() { 
    if (fDir == null) {
      try {
        URL urlThisClass = getClass().getResource(getClass().getSimpleName() + ".class");
        File fThisClass = new File(urlThisClass.toURI());
        fDir = fThisClass.getParentFile();
      } catch(Throwable t) {
        System.out.println("" + t);
      }
    }
    return fDir; 
  }
  /**
   * Setter for {@link #fDir}: Directory for text files.
   * @param fNewDir Directory for text files.
   */
  public void setDir(File fNewDir) { fDir = fNewDir; }
   
  public static void main(String args[]) {
    org.junit.runner.JUnitCore.main("nzilbb.labbcat.TestLabbcatView");
  }
}
