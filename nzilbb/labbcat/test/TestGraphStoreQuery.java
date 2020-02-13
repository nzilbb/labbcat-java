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

import java.net.MalformedURLException;
import java.util.Arrays;
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
 * Unit tests for GraphStoreQuery.
 * <p>These tests test the functionality of the client library, not the server. 
 * <p>They assume the existence of a valid LaBB-CAT instance (configured by
 * <var>labbcatUrl</var>) which responds correctly to requests, but do not generally test
 * that the server behaves correctly , nor assume specific corpus content. For the tests
 * to work, the first graph listed in LaBB-CAT must have some words and some media, and
 * the first participant listed must have some transcripts. 
 */
public class TestGraphStoreQuery {
   
   // YOU MUST ENSURE THE FOLLOWING SETTINGS ARE VALID FOR YOU TEST LABBCAT SERVER:
   static String labbcatUrl = "http://localhost:8080/labbcat/";
   static String username = "labbcat";
   static String password = "labbcat";
   static GraphStoreQuery store;

   @BeforeClass public static void createStore() {
      try {
         store = new GraphStoreQuery(labbcatUrl, username, password).setBatchMode(true);
      } catch(MalformedURLException exception) {
         fail("Could not create GraphStoreQuery object");
      }
   }

   @After public void notVerbose() {
      store.setVerbose(false);
   }

   @Test(expected = StoreException.class) public void invalidCredentials()
      throws Exception {
      GraphStoreQuery store = new GraphStoreQuery(labbcatUrl, "xxx", "xxx")
         .setBatchMode(true);
      store.getId();
   }

   @Test(expected = StoreException.class) public void credentialsRequired()
      throws Exception {
      GraphStoreQuery store = new GraphStoreQuery(labbcatUrl)
         .setBatchMode(true);
      store.getId();
   }
   
   @Test(expected = MalformedURLException.class) public void malformedURLException()
      throws Exception {
      GraphStoreQuery store = new GraphStoreQuery("xxx", username, password)
         .setBatchMode(true);
      store.getId();
   }

   @Test(expected = StoreException.class) public void nonLabbcatUrl()
      throws Exception {
      GraphStoreQuery store = new GraphStoreQuery("http://tld/", username, password)
         .setBatchMode(true);
      store.getId();
   }

   @Test public void getId()
      throws Exception {
      String id = store.getId();
      assertEquals("ID matches the url",
                   labbcatUrl, id);
   }

   @Test public void getLayerIds() throws Exception {
      String[] ids = store.getLayerIds();
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
      Layer[] layers = store.getLayers();
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
      String[] ids = store.getCorpusIds();
      // for (String id : ids) System.out.println("corpus " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void getParticipantIds() throws Exception {
      String[] ids = store.getParticipantIds();
      // for (String id : ids) System.out.println("participant " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void getGraphIds() throws Exception {
      String[] ids = store.getGraphIds();
      // for (String id : ids) System.out.println("graph " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void countMatchingParticipantIds() throws Exception {
      int count = store.countMatchingParticipantIds("id MATCHES '.+'");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMatchingParticipantIds() throws Exception {
      String[] ids = store.getMatchingParticipantIds("id MATCHES '.+'");
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2) {
         System.out.println("Too few participants to test pagination");
      } else {
         ids = store.getMatchingParticipantIds("id MATCHES '.+'", 2, 0);
         assertEquals("Two IDs are returned",
                      2, ids.length);
      }         
   }

   @Test public void getGraphIdsInCorpus() throws Exception {
      String[] ids = store.getCorpusIds();
      assertTrue("There's at least one corpus",
                 ids.length > 0);
      String corpus = ids[0];
      ids = store.getGraphIdsInCorpus(corpus);
      assertTrue("Some IDs are returned for corpus " + corpus,
                 ids.length > 0);
   }

   @Test public void getGraphIdsWithParticipant() throws Exception {
      String[] ids = store.getParticipantIds();
      assertTrue("There's at least one participant",
                 ids.length > 0);
      String participant = ids[0];
      ids = store.getGraphIdsWithParticipant(participant);
      assertTrue("Some IDs are returned for participant " + participant,
                 ids.length > 0);
   }

   @Test public void countMatchingGraphIds() throws Exception {
      int count = store.countMatchingGraphIds("id MATCHES '.+'");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMatchingGraphIds() throws Exception {
      String[] ids = store.getMatchingGraphIds("id MATCHES '.+'");
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2) {
         System.out.println("Too few graphs to test pagination");
      } else {
         ids = store.getMatchingGraphIds("id MATCHES '.+'", 2, 0, "id DESC");
         assertEquals("Two IDs are returned",
                      2, ids.length);
      }         
   }

   @Test public void countAnnotations() throws Exception {
      String[] ids = store.getMatchingGraphIds("id MATCHES '.+'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      long count = store.countAnnotations(graphId, "orthography");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getAnnotations() throws Exception {
      String[] ids = store.getMatchingGraphIds("id MATCHES '.+'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      
      long count = store.countAnnotations(graphId, "orthography");
      Annotation[] annotations = store.getAnnotations(graphId, "orthography", 2, 0);
      if (count < 2) {
         System.out.println("Too few annotations to test pagination");
      } else {
         assertEquals("Two annotations are returned",
                      2, annotations.length);
      }
   }

   @Test public void getAnchors() throws Exception {
      // get a graph to work with
      String[] ids = store.getMatchingGraphIds("id MATCHES '.+'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];

      // get some annotations so we have valid anchor IDs
      Annotation[] annotations = store.getAnnotations(graphId, "orthography", 2, 0);
      if (annotations.length == 0) {
         System.out.println("Can't test getAnchors() - no annotations in " + graphId);
      } else {
         // create an array of anchorIds
         String[] anchorIds = new String[annotations.length];
         for (int i = 0; i < annotations.length; i++) anchorIds[i] = annotations[i].getStartId();

         // finally, get the anchors
         Anchor[] anchors = store.getAnchors(graphId, anchorIds);         
         assertEquals("Correct number of anchors is returned",
                      anchorIds.length, anchors.length);
      }
   }
   
   @Test public void getMedia() throws Exception {
      String[] ids = store.getMatchingGraphIds("id MATCHES 'Agnes.+\\.trs'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      String url = store.getMedia(graphId, "", "audio/wav");
      assertNotNull("There is some media (check the first graph listed, "+graphId+")",
                    url);
   }

   @Test public void getMediaFragment() throws Exception {
      String[] ids = store.getMatchingGraphIds("id MATCHES 'Agnes.+\\.trs'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      String url = store.getMedia(graphId, "", "audio/wav", 1.0, 2.0);
      assertNotNull("There is some media",
                    url);
   }

   @Test public void getLayer() throws Exception {
      Layer layer = store.getLayer("orthography");
      assertEquals("Correct layer",
                   "orthography", layer.getId());
   }

   @Test public void getParticipant() throws Exception {
      // find a participant ID to use
      String[] ids = store.getParticipantIds();
      // for (String id : ids) System.out.println("participant " + id);
      assertTrue("Some participant IDs exist",
                 ids.length > 0);
      String participantId = ids[0];
      Annotation participant = store.getParticipant(participantId);
      assertEquals("Correct participant",
                   participantId, participant.getLabel()); // not getId()
   }

   @Test public void countMatchingAnnotations() throws Exception {
      int count = store.countMatchingAnnotations(
         "layer.id = 'orthography' AND label MATCHES 'and'");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMatchingAnnotations() throws Exception {
      Annotation[] annotations = store.getMatchingAnnotations(
         "layer.id = 'orthography' AND label MATCHES 'and'", 2, 0);
      assertEquals("Two annotations are returned",
                   2, annotations.length);
   }

   @Test public void getMediaTracks() throws Exception {
      MediaTrackDefinition[] tracks = store.getMediaTracks();
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
      String[] ids = store.getMatchingGraphIds("id MATCHES '.+'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];

      // get some annotations so we have valid anchor IDs
      MediaFile[] files = store.getAvailableMedia(graphId);
      assertTrue(graphId + " has some tracks",
                 files.length > 0);
   }
   
   @Test public void getEpisodeDocuments() throws Exception {
      // get a graph to work with
      String[] ids = store.getMatchingGraphIds("id MATCHES '.+'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];

      // get some annotations so we have valid anchor IDs
      MediaFile[] files = store.getEpisodeDocuments(graphId);
      if (files.length == 0) System.out.println(graphId + " has no documents");
   }
   
   public static void main(String args[]) {
      org.junit.runner.JUnitCore.main("nzilbb.labbcat.test.TestGraphStoreQuery");
   }
}
