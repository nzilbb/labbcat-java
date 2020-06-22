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
import java.util.Set;
import java.util.stream.Collectors;
import nzilbb.ag.Anchor;
import nzilbb.ag.Annotation;
import nzilbb.ag.Layer;
import nzilbb.ag.MediaFile;
import nzilbb.ag.MediaTrackDefinition;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.*;
import nzilbb.labbcat.model.*;

/**
 * Unit tests for LabbcatAdmin.
 * <p>These tests are general in nature. They assume the existence of a valid LaBB-CAT
 * instance (configured by labbcatUrl) but do not assume specific corpus content. For the
 * tests to work, the first graph listed in LaBB-CAT must have some words and some media,
 * and the first participant listed must have some transcripts.
 *
 */
public class TestLabbcatAdmin {
   
   // YOU MUST ENSURE THE FOLLOWING SETTINGS ARE VALID FOR YOU TEST LABBCAT SERVER:
   static String labbcatUrl = "http://localhost:8080/labbcat/";
   static String username = "labbcat";
   static String password = "labbcat";
   static LabbcatAdmin labbcat;

   @BeforeClass public static void createStore() {
      try {
         labbcat = new LabbcatAdmin(labbcatUrl, username, password);
         labbcat.setBatchMode(true);
      } catch(MalformedURLException exception) {
         fail("Could not create LabbcatAdmin object");
      }
   }

   @After public void notVerbose() {
      labbcat.setVerbose(false);
   }
   
   @Test(expected = StoreException.class) public void invalidCredentials() throws Exception {
      LabbcatAdmin labbcat = new LabbcatAdmin(labbcatUrl, "xxx", "xxx");
      labbcat.setBatchMode(true);
      labbcat.getId();
   }

   @Test(expected = StoreException.class) public void credentialsRequired() throws Exception {
      LabbcatAdmin labbcat = new LabbcatAdmin(labbcatUrl);
      labbcat.setBatchMode(true);
      labbcat.getId();
   }
   
   @Test(expected = MalformedURLException.class) public void malformedURLException() throws Exception {
      LabbcatAdmin labbcat = new LabbcatAdmin("xxx", username, password);
      labbcat.setBatchMode(true);
      labbcat.getId();
   }

   @Test(expected = StoreException.class) public void nonLabbcatUrl() throws Exception {
      LabbcatAdmin labbcat = new LabbcatAdmin("http://tld/", username, password);
      labbcat.setBatchMode(true);
      labbcat.getId();
   }

   @Test public void inheritedLabbcatViewFunctions() throws Exception {
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

      ids = labbcat.getTranscriptIds();
      // for (String id : ids) System.out.println("graph " + id);
      assertTrue("getTranscriptIds: Some IDs are returned",
                 ids.length > 0);

      long count = labbcat.countMatchingParticipantIds("/.+/.test(id)");
      assertTrue("countMatchingParticipantIds: There are some matches",
                 count > 0);

      ids = labbcat.getMatchingParticipantIds("/.+/.test(id)");
      assertTrue("getMatchingParticipantIds: Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2) {
         System.out.println("getMatchingParticipantIds: Too few participants to test pagination");
      } else {
         ids = labbcat.getMatchingParticipantIds("/.+/.test(id)", 2, 0);
         assertEquals("getMatchingParticipantIds: Two IDs are returned",
                      2, ids.length);
      }

      ids = labbcat.getTranscriptIdsInCorpus(corpus);
      assertTrue("getTranscriptIdsInCorpus: Some IDs are returned for corpus " + corpus,
                 ids.length > 0);

      ids = labbcat.getTranscriptIdsWithParticipant(participantId);
      assertTrue("getTranscriptIdsWithParticipant: Some IDs are returned for participant " + participantId,
                 ids.length > 0);

      count = labbcat.countMatchingTranscriptIds("/.+/.test(id)");
      assertTrue("countMatchingTranscriptIds: There are some matches",
                 count > 0);

      ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)");
      assertTrue("countMatchingTranscriptIds: Some IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      if (ids.length < 2) {
         System.out.println("countMatchingTranscriptIds: Too few graphs to test pagination");
      } else {
         ids = labbcat.getMatchingTranscriptIds("/.+/.test(id)", 2, 0, "id DESC");
         assertEquals("getMatchingTranscriptIds: Two IDs are returned",
                      2, ids.length);
      }         
      
      count = labbcat.countAnnotations(graphId, "orthography");
      assertTrue("countAnnotations: There are some matches",
                 count > 0);
      
      Annotation[] annotations = labbcat.getAnnotations(graphId, "orthography", 2, 0);
      if (count < 2) {
         System.out.println("getAnnotations: Too few annotations to test pagination");
      } else {
         assertEquals("getAnnotations: Two annotations are returned",
                      2, annotations.length);
      }
      if (annotations.length == 0) {
         System.out.println("getAnchors: Can't test getAnchors() - no annotations in " + graphId);
      } else {
         // create an array of anchorIds
         String[] anchorIds = new String[annotations.length];
         for (int i = 0; i < annotations.length; i++) anchorIds[i] = annotations[i].getStartId();

         // finally, get the anchors
         Anchor[] anchors = labbcat.getAnchors(graphId, anchorIds);         
         assertEquals("getAnchors: Correct number of anchors is returned",
                      anchorIds.length, anchors.length);
      }

      String url = labbcat.getMedia(graphId, "", "audio/wav");
      assertNotNull("getMedia: There is some media (check the first graph listed, "+graphId+")",
                    url);

      Layer layer = labbcat.getLayer("orthography");
      assertEquals("getLayer: Correct layer",
                   "orthography", layer.getId());

      Annotation participant = labbcat.getParticipant(participantId);
      assertEquals("getParticipant: Correct participant",
                   participantId, participant.getLabel()); // not getId()

      count = labbcat.countMatchingAnnotations(
         "layer.id == 'orthography' && label == 'and'");
      assertTrue("countMatchingAnnotations: There are some matches",
                 count > 0);

      annotations = labbcat.getMatchingAnnotations(
         "layer.id == 'orthography' && label == 'and'", 2, 0);
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
      if (docs.length == 0) {
         System.out.println("getEpisodeDocuments: " + graphId + " has no documents");
      }
   }
   
   @Test public void newCorpusUpdateCorpusAndDeleteCorpus() throws Exception {
      Corpus originalCorpus = new Corpus()
         .setName("unit-test")
         .setLanguage("en")
         .setDescription("Temporary corpus for unit testing");
      
      try {
         Corpus newCorpus = labbcat.createCorpus(originalCorpus);
         assertNotNull("Corpus returned", newCorpus);
         assertEquals("Name correct",
                      originalCorpus.getName(), newCorpus.getName());
         assertEquals("Language correct",
                      originalCorpus.getLanguage(), newCorpus.getLanguage());
         assertEquals("Description correct",
                      originalCorpus.getDescription(), newCorpus.getDescription());

         try {
            labbcat.createCorpus(originalCorpus);
            fail("Can't create a corpus with existing name");
         }
         catch(Exception exception) {}

         Corpus[] corpora = labbcat.readCorpora();
         // ensure the corpus exists
         assertTrue("There's at least one corpus", corpora.length >= 1);
         boolean found = false;
         for (Corpus c : corpora) {
            if (c.getName().equals(originalCorpus.getName())) {
               found = true;
               break;
            }
         }
         assertTrue("Corpus was added", found);

         // update it
         Corpus updatedCorpus = new Corpus()
            .setName("unit-test")
            .setLanguage("es")
            .setDescription("Temporary Spanish corpus for unit testing");

         Corpus changedCorpus = labbcat.updateCorpus(updatedCorpus);
         assertNotNull("Corpus returned", changedCorpus);
         assertEquals("Updated Name correct",
                      updatedCorpus.getName(), changedCorpus.getName());
         assertEquals("Updated Language correct",
                      updatedCorpus.getLanguage(), changedCorpus.getLanguage());
         assertEquals("Updated Description correct",
                      updatedCorpus.getDescription(), changedCorpus.getDescription());

         // delete it
         labbcat.deleteCorpus(originalCorpus.getName());

         Corpus[] corporaAfter = labbcat.readCorpora();
         // ensure the corpus no longer exists
         boolean foundAfter = false;
         for (Corpus c : corporaAfter) {
            if (c.getName().equals(originalCorpus.getName())) {
               foundAfter = true;
               break;
            }
         }
         assertFalse("Corpus is gone", foundAfter);

         try {
            // can't delete it again
            labbcat.deleteCorpus(originalCorpus);
            fail("Can't delete corpus that doesn't exist");
         } catch(Exception exception) {
         }

      } finally {
         // ensure it's not there
         try {
            labbcat.deleteCorpus(originalCorpus);
         } catch(Exception exception) {}         
     }
   }

   public static void main(String args[]) {
      org.junit.runner.JUnitCore.main("nzilbb.labbcat.test.TestLabbcatAdmin");
   }
}