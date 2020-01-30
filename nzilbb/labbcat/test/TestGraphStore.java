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
 * <p>These tests are general in nature. They assume the existence of a valid LaBB-CAT
 * instance (configured by labbcatUrl) but do not assume specific corpus content. For the
 * tests to work, the first graph listed in LaBB-CAT must have some words and some media,
 * and the first participant listed must have some transcripts.
 *
 */
public class TestGraphStore 
{
   // YOU MUST ENSURE THE FOLLOWING SETTINGS ARE VALID FOR YOU TEST LABBCAT SERVER:
   static String labbcatUrl = "http://localhost:8080/labbcat/";
   static String username = "labbcat";
   static String password = "labbcat";
   static GraphStore store;

   @BeforeClass public static void createStore()
   {
      try
      {
         store = new GraphStore(labbcatUrl, username, password);
         store.setBatchMode(true);
      }
      catch(MalformedURLException exception)
      {
         fail("Could not create GraphStore object");
      }
   }

   @After public void notVerbose()
   {
      store.setVerbose(false);
   }
   
   @Test(expected = StoreException.class) public void invalidCredentials()
      throws Exception
   {
      GraphStore store = new GraphStore(labbcatUrl, "xxx", "xxx");
      store.setBatchMode(true);
      store.getId();
   }

   @Test(expected = StoreException.class) public void credentialsRequired()
      throws Exception
   {
      GraphStore store = new GraphStore(labbcatUrl);
      store.setBatchMode(true);
      store.getId();
   }
   
   @Test(expected = MalformedURLException.class) public void malformedURLException()
      throws Exception
   {
      GraphStore store = new GraphStore("xxx", username, password);
      store.setBatchMode(true);
      store.getId();
   }

   @Test(expected = StoreException.class) public void nonLabbcatUrl()
      throws Exception
   {
      GraphStore store = new GraphStore("http://tld/", username, password);
      store.setBatchMode(true);
      store.getId();
   }

   @Test public void inheritedGraphStoreQueryFunctions()
      throws Exception
   {
      String id = store.getId();
      assertEquals("getId: ID matches the url",
                   labbcatUrl, id);

      String[] ids = store.getLayerIds();
      //for (String id : ids) System.out.println("layer " + id);
      assertTrue("getLayerIds: Some IDs are returned",
                 ids.length > 0);
      Set<String> idSet = Arrays.asList(ids).stream().collect(Collectors.toSet());
      assertTrue("getLayerIds: Has transcript layer",
                 idSet.contains("transcript"));

      Layer[] layers = store.getLayers();
      //for (String id : ids) System.out.println("layer " + id);
      assertTrue("getLayers: Some IDs are returned",
                 layers.length > 0);

      ids = store.getCorpusIds();
      // for (String id : ids) System.out.println("corpus " + id);
      assertTrue("getCorpusIds: Some IDs are returned",
                 ids.length > 0);
      String corpus = ids[0];

      ids = store.getParticipantIds();
      // for (String id : ids) System.out.println("participant " + id);
      assertTrue("getParticipantIds: Some IDs are returned",
                 ids.length > 0);
      String participantId = ids[0];

      ids = store.getGraphIds();
      // for (String id : ids) System.out.println("graph " + id);
      assertTrue("getGraphIds: Some IDs are returned",
                 ids.length > 0);

      long count = store.countMatchingParticipantIds("id MATCHES '.+'");
      assertTrue("countMatchingParticipantIds: There are some matches",
                 count > 0);

      ids = store.getMatchingParticipantIds("id MATCHES '.+'");
      assertTrue("getMatchingParticipantIds: Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2)
      {
         System.out.println("getMatchingParticipantIds: Too few participants to test pagination");
      }
      else
      {
         ids = store.getMatchingParticipantIds("id MATCHES '.+'", 2, 0);
         assertEquals("getMatchingParticipantIds: Two IDs are returned",
                      2, ids.length);
      }

      ids = store.getGraphIdsInCorpus(corpus);
      assertTrue("getGraphIdsInCorpus: Some IDs are returned for corpus " + corpus,
                 ids.length > 0);

      ids = store.getGraphIdsWithParticipant(participantId);
      assertTrue("getGraphIdsWithParticipant: Some IDs are returned for participant " + participantId,
                 ids.length > 0);

      count = store.countMatchingGraphIds("id MATCHES '.+'");
      assertTrue("countMatchingGraphIds: There are some matches",
                 count > 0);

      ids = store.getMatchingGraphIds("id MATCHES '.+'");
      assertTrue("countMatchingGraphIds: Some IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      if (ids.length < 2)
      {
         System.out.println("countMatchingGraphIds: Too few graphs to test pagination");
      }
      else
      {
         ids = store.getMatchingGraphIds("id MATCHES '.+'", 2, 0, "id DESC");
         assertEquals("getMatchingGraphIds: Two IDs are returned",
                      2, ids.length);
      }         
      
      count = store.countAnnotations(graphId, "orthography");
      assertTrue("countAnnotations: There are some matches",
                 count > 0);
      
      Annotation[] annotations = store.getAnnotations(graphId, "orthography", 2, 0);
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
         Anchor[] anchors = store.getAnchors(graphId, anchorIds);         
         assertEquals("getAnchors: Correct number of anchors is returned",
                      anchorIds.length, anchors.length);
      }

      String url = store.getMedia(graphId, "", "audio/wav");
      assertNotNull("getMedia: There is some media",
                    url);

      Layer layer = store.getLayer("orthography");
      assertEquals("getLayer: Correct layer",
                   "orthography", layer.getId());

      Annotation participant = store.getParticipant(participantId);
      assertEquals("getParticipant: Correct participant",
                   participantId, participant.getLabel()); // not getId()

      count = store.countMatchingAnnotations(
         "layer.id = 'orthography' AND label MATCHES 'and'");
      assertTrue("countMatchingAnnotations: There are some matches",
                 count > 0);

      annotations = store.getMatchingAnnotations(
         "layer.id = 'orthography' AND label MATCHES 'and'", 2, 0);
      assertEquals("getMatchingAnnotations: Two annotations are returned",
                   2, annotations.length);

      MediaTrackDefinition[] tracks = store.getMediaTracks();
      assertTrue("getMediaTracks: Some tracks are returned",
                 tracks.length > 0);

      // get some annotations so we have valid anchor IDs
      MediaFile[] files = store.getAvailableMedia(graphId);
      assertTrue("getAvailableMedia: " + graphId + " has some tracks",
                 files.length > 0);

      // get some annotations so we have valid anchor IDs
      MediaFile[] docs = store.getEpisodeDocuments(graphId);
      if (docs.length == 0)
      {
         System.out.println("getEpisodeDocuments: " + graphId + " has no documents");
      }
   }

   @Test public void deleteGraphNotExists()
      throws Exception
   {
      try
      {
         // get some annotations so we have valid anchor IDs
         store.deleteGraph("nonexistent graph ID");
         fail("deleteGraph should fail for nonexistant graph ID");
      }
      catch(ResponseException exception)
      {
         assertEquals("404 not found",
                      404, exception.getResponse().getHttpStatus());
         assertEquals("Failure code",
                      1, exception.getResponse().getCode());
      }
   }

   public static void main(String args[]) 
   {
      org.junit.runner.JUnitCore.main("nzilbb.labbcat.test.TestGraphStore");
   }
}
