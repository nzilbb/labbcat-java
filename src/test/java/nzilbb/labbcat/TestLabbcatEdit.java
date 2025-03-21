//
// Copyright 2020-2024 New Zealand Institute of Language, Brain and Behaviour, 
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
import java.util.Set;
import java.util.stream.Collectors;
import nzilbb.ag.Anchor;
import nzilbb.ag.Annotation;
import nzilbb.ag.Graph;
import nzilbb.ag.Layer;
import nzilbb.ag.MediaFile;
import nzilbb.ag.MediaTrackDefinition;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.model.*;

/**
 * Unit tests for LabbcatEdit.
 * <p>These tests are general in nature. They assume the existence of a valid LaBB-CAT
 * instance (configured by labbcatUrl) but do not assume specific corpus content. For the
 * tests to work, the first graph listed in LaBB-CAT must have some words and some media,
 * and the first participant listed must have some transcripts.
 *
 */
public class TestLabbcatEdit {
   
  // YOU MUST ENSURE THE FOLLOWING SETTINGS ARE VALID FOR YOU TEST LABBCAT SERVER:
  static String labbcatUrl = "http://localhost:8080/labbcat/";
  static String username = "labbcat";
  static String password = "labbcat";
  static String readonly_username = "readonly";
  static String readonly_password = "labbcat";
  static LabbcatEdit labbcat;

  @BeforeClass public static void createStore() {
    try {
      labbcat = new LabbcatEdit(labbcatUrl, username, password);
      labbcat.setBatchMode(true);
    } catch(MalformedURLException exception) {
      fail("Could not create LabbcatEdit object");
    }
  }

  @After public void notVerbose() {
    labbcat.setVerbose(false);
  }

  /** Test failure of invalid credentials. */
  @Test(expected = StoreException.class) public void invalidCredentials() throws Exception {
    LabbcatEdit labbcat = new LabbcatEdit(labbcatUrl, "xxx", "xxx");
    labbcat.setBatchMode(true);
    labbcat.getId();
  }

  /** Test failure of missing credentials. */
  @Test(expected = StoreException.class) public void credentialsRequired() throws Exception {
    LabbcatEdit labbcat = new LabbcatEdit(labbcatUrl);
    labbcat.setBatchMode(true);
    labbcat.getId();
  }
   
  /** Test failure of invalid URL. */
  @Test(expected = MalformedURLException.class) public void malformedURLException() throws Exception {
    LabbcatEdit labbcat = new LabbcatEdit("xxx", username, password);
    labbcat.setBatchMode(true);
    labbcat.getId();
  }

  /** Test failure of non-LaBB-CAT URL. */
  @Test(expected = StoreException.class) public void nonLabbcatUrl() throws Exception {
    LabbcatEdit labbcat = new LabbcatEdit("http://tld/", username, password);
    labbcat.setBatchMode(true);
    labbcat.getId();
  }

  /** Test rejection of unauthorized requests. */
  @Test() public void unauthorizedRequest()
    throws Exception {
    LabbcatEdit labbcat = new LabbcatEdit(labbcatUrl, readonly_username, readonly_password);
    labbcat.setBatchMode(true);
    try {
      labbcat.deleteTranscript("non-existent transcript");
      fail("Can't deleteTranscript as non-edit user");
    } catch(ResponseException x) {
      // check it's for the right reason
      assertEquals("Read failed for lack of auth: "
                   + x.getResponse().getHttpStatus() + " " + x.getResponse().getRaw(),
                   403, x.getResponse().getHttpStatus());
    }
  }

  /** Test general methods inherited from {@link LabbcatView} */
  @Test public void inheritedLabbcatViewFunctions() throws Exception {
    String id = labbcat.getId();
    assertEquals("getId: ID matches the url",
                 labbcatUrl, id);

    String[] ids = labbcat.getLayerIds();
    //for (String id : ids) System.out.println("layer " + id);
    assertTrue("getLayerIds: Some IDs are returned",
               ids.length > 0);
    Set<String> idSet = Arrays.asList(ids).stream().collect(Collectors.toSet());
    assertTrue("getLayerIds: Has word layer",
               idSet.contains("word"));

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

  /** Test failure of deleting a transcript that doesn't exist. */
  @Test public void deleteTranscriptNotExists() throws Exception {
    try {
      // get some annotations so we have valid anchor IDs
      labbcat.deleteTranscript("nonexistent graph ID");
      fail("deleteTranscript should fail for nonexistant graph ID");
    } catch(ResponseException exception) {
      assertEquals("404 not found",
                   404, exception.getResponse().getHttpStatus());
      assertEquals("Failure code",
                   1, exception.getResponse().getCode());
    }
  }

  /**
   * Test /api/edit/transcript/* API, specifically:
   * <ul>
   *  <li> transcriptUpload </li>
   *  <li> transcriptUploadDelete </li>
   * </ul>
   */
  @Test public void transcriptUploadDelete()
    throws Exception {
    
    File transcript = new File(getDir(), "nzilbb.labbcat.test.txt");
    assertTrue("Ensure transcript exists: " + transcript.getPath(), transcript.exists());
    try {
      
      // upload transcript
      labbcat.setVerbose(true);
      Upload upload = labbcat.transcriptUpload(transcript, false);
      System.out.println("ID " + upload.getId());

      // delete upload
      labbcat.transcriptUploadDelete(upload);
      
    } finally {
      labbcat.setVerbose(false);
    }
  }

  /**
   * Test /api/edit/transcript/* API, specifically:
   * <ul>
   *  <li> transcriptUpload </li>
   *  <li> transcriptUploadParameters </li>
   * </ul>
   */
  @Test public void transcriptUpload()
    throws Exception {
    
    File transcript = new File(getDir(), "nzilbb.labbcat.test.txt");
    File[] media = {
      new File(getDir(), "nzilbb.labbcat.test.wav")
    };
    File document = new File(getDir(), "nzilbb.labbcat.test.doc");
    String participantId = "UnitTester";
    assertTrue("Ensure transcript exists: " + transcript.getPath(), transcript.exists());
    assertTrue("Ensure media exists: " + media[0].getPath(), media[0].exists());
    assertTrue("Ensure document exists: " + document.getPath(), document.exists());
    try {
      
      // ensure transcript/participant don't already exist
      try {
        labbcat.deleteTranscript(transcript.getName());
      } catch(ResponseException exception) {}
      try {
        labbcat.deleteParticipant(participantId);
      } catch(ResponseException exception) {}

      // upload transcript
      labbcat.setVerbose(true);
      Upload upload = labbcat.transcriptUpload(transcript, media, false);
      assertNotNull("ID", upload.getId());

      // finalize parameters
      upload = labbcat.transcriptUploadParameters(upload);
      assertNotNull("ID ", upload.getId());
      assertNotNull("transcript threads returned", upload.getTranscripts());

      String threadId = upload.getTranscripts().get(transcript.getName());
      assertNotNull("transcript thread is specified " +upload.getTranscripts(),
                    threadId);
      // cancel layer generation, we don't care about it         
      labbcat.cancelTask(threadId);
      labbcat.releaseTask(threadId);

      assertEquals("Transcript has been added to the store",
                   1, labbcat.countMatchingTranscriptIds("id = '"+transcript.getName()+"'"));

      // now the transcript exists, try updating it...
      upload = labbcat.transcriptUpload(transcript, true);
      assertTrue("generate parameter" + upload.getParameters(),
                 upload.getParameters().containsKey("labbcat_generate"));
      assertTrue("generation enabled by default",
                 (Boolean)upload.getParameters().get("labbcat_generate").getValue());

      upload = labbcat.transcriptUploadParameters(upload);
      assertNotNull("transcript thread is specified " +upload.getTranscripts(),
                    threadId);
      // cancel layer generation, we don't care about it         
      labbcat.cancelTask(threadId);
      labbcat.releaseTask(threadId);

      
    } finally {
      labbcat.setVerbose(false);
      try {
        // delete transcript/participant
        labbcat.deleteTranscript(transcript.getName());
        labbcat.deleteParticipant(participantId);
            
        // ensure the transcript/participant no longer exist
        assertEquals("Transcript has been deleted from the store",
                     0, labbcat.countMatchingTranscriptIds("id = '"+transcript.getName()+"'"));
        assertEquals("Participant has been deleted from the store",
                     0, labbcat.countMatchingParticipantIds("id = '"+participantId+"'"));
      } catch (Exception x) {
        System.err.println("Unexpectedly can't delete test transcript: " + x);
      }
    }
  }

  /**
   * Test transcript upload, media upload, and transcript/participant deletion, specifically:
   * <ul>
   *  <li> newTranscript </li>
   *  <li> updateTranscript </li>
   *  <li> saveMedia </li>
   *  <li> saveEpisodeDocument </li>
   *  <li> deleteMedia </li>
   *  <li> getParticipant </li>
   *  <li> saveParticipant </li>
   *  <li> deleteTranscript </li>
   *  <li> deleteParticipant </li>
   * </ul>
   */
  @Test public void transcriptParticipantAndMediaCRUD()
    throws Exception {
      
    // first get a corpus and transcript type
    String[] ids = labbcat.getCorpusIds();
    // for (String id : ids) System.out.println("corpus " + id);
    assertTrue("There is at least one corpus", ids.length > 0);
    String corpus = ids[0];
    Layer typeLayer = labbcat.getLayer("transcript_type");
    assertTrue("There is at least one transcript type", typeLayer.getValidLabels().size() > 0);
    String transcriptType = typeLayer.getValidLabels().keySet().iterator().next();

    File transcript = new File(getDir(), "nzilbb.labbcat.test.txt");
    File media = new File(getDir(), "nzilbb.labbcat.test.wav");
    File document = new File(getDir(), "nzilbb.labbcat.test.doc");
    String participantId = "UnitTester";
    String changedParticipantId = "UnitTester-changed";
    assertTrue("Ensure transcript exists: " + transcript.getPath(), transcript.exists());
    assertTrue("Ensure media exists: " + media.getPath(), media.exists());
    assertTrue("Ensure document exists: " + document.getPath(), document.exists());
    try {

      // ensure transcript/participant don't already exist
      try {
        labbcat.deleteTranscript(transcript.getName());
      } catch(ResponseException exception) {}
      try {
        labbcat.deleteParticipant(participantId);
      } catch(ResponseException exception) {}
         
      String threadId = labbcat.newTranscript(
        transcript, null, null, transcriptType, corpus, "test");
         
      TaskStatus task = labbcat.waitForTask(threadId, 30);
      assertFalse("Upload task finished in a timely manner",
                  task.getRunning());
         
      labbcat.releaseTask(threadId);
         
      // ensure the transcript/participant exist
      assertEquals("Transcript is in the store",
                   1, labbcat.countMatchingTranscriptIds("id = '"+transcript.getName()+"'"));
      assertEquals("Participant is in the store",
                   1, labbcat.countMatchingParticipantIds("id = '"+participantId+"'"));
      
      // ensure there is no media
      MediaFile[] files = labbcat.getAvailableMedia(transcript.getName());
      assertTrue("No media is present: " + Arrays.asList(files), files.length == 0);
      // upload media
      MediaFile file = labbcat.saveMedia(transcript.getName(), media.toURI().toString(), null);
      assertNotNull("File returned", file);
      assertNotNull("File name returned", file.getName());
      // ensure there is now media
      files = labbcat.getAvailableMedia(transcript.getName());
      assertTrue("Media is now present", files.length >= 1);
      // delete media
      labbcat.deleteMedia(transcript.getName(), file.getName());
      // ensure the media is now gone
      files = labbcat.getAvailableMedia(transcript.getName());
      assertTrue("Media was deleted: " + Arrays.asList(files), files.length == 0);

      // ensure there are no episode documents
      files = labbcat.getEpisodeDocuments(transcript.getName());
      assertTrue("No docs present: " + Arrays.asList(files), files.length == 0);
      // upload document
      file = labbcat.saveEpisodeDocument(transcript.getName(), document.toURI().toString());
      assertNotNull("Document returned", file);
      assertNotNull("Document name returned", file.getName());
      // ensure there is now a document
      files = labbcat.getEpisodeDocuments(transcript.getName());
      assertTrue("Document is now present", files.length > 0);
      // delete document
      labbcat.deleteMedia(transcript.getName(), file.getName());
      // ensure the docuemtn is now gone
      files = labbcat.getEpisodeDocuments(transcript.getName());
      assertTrue("Document was deleted: " + Arrays.asList(files), files.length == 0);

      // re-upload transcript
      threadId = labbcat.updateTranscript(transcript);
         
      task = labbcat.waitForTask(threadId, 30);
      assertFalse("Re-upload task finished in a timely manner",
                  task.getRunning());
         
      labbcat.releaseTask(threadId);
         
      // ensure the transcript exists
      assertEquals("Transcript is still in the store",
                   1, labbcat.countMatchingTranscriptIds("id = '"+transcript.getName()+"'"));

      // save participant with no changes
      Annotation participant = labbcat.getParticipant(participantId);
         
      // change the participant ID
      participant.setLabel(changedParticipantId);
      assertTrue("Changes saved", labbcat.saveParticipant(participant));
      participant = labbcat.getParticipant(participantId);
      assertNull("Participant not available under old ID", participant);
      participant = labbcat.getParticipant(changedParticipantId);
      assertNotNull("Participant available under new ID", participant);
    } finally {
      try {
        // delete transcript/participant
        labbcat.deleteTranscript(transcript.getName());
        labbcat.deleteParticipant(changedParticipantId);
            
        // ensure the transcript/participant no longer exist
        assertEquals("Transcript has been deleted from the store",
                     0, labbcat.countMatchingTranscriptIds("id = '"+transcript.getName()+"'"));
        assertEquals("Participant has been deleted from the store",
                     0, labbcat.countMatchingParticipantIds("id = '"+participantId+"'"));
      } catch (Exception x) {
        System.err.println("Unexpectedly can't delete test transcript: " + x);
      }
    }
  }

  /** Test transcript attributes can be saved */
  @Test public void saveTranscript() throws Exception {

    // get the language of a transcript
    String[] ids = labbcat.getMatchingTranscriptIds("/AP511.+\\.eaf/.test(id)", 1, 0);
    assertTrue("Some graph IDs are returned",
               ids.length > 0);
    String graphId = ids[0];
    String[] attributes = { "transcript_language" };
    Graph graph = labbcat.getTranscript(graphId, attributes);
    assertNotNull("Graph retrieved: " + graphId, graph);

    // change the language
    Annotation language = graph.first("transcript_language");
    String originalLanguage = language != null?language.getLabel():null;
    graph.createTag(graph, "transcript_language", "test-value");
    labbcat.saveTranscript(graph);

    // check the new value was saved
    graph = labbcat.getTranscript(graphId, attributes);
    language = graph.first("transcript_language");
    assertNotNull("language attribute exists", language);
    assertEquals("new label applied", "test-value", language.getLabel());

    // save original value
    if (originalLanguage != null) {
      language.setLabel(originalLanguage);
    } else { // delete the annotation
      graph.trackChanges();
      language.destroy();
      graph.commit();
    }
    labbcat.saveTranscript(graph);
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
    org.junit.runner.JUnitCore.main("nzilbb.labbcat.TestLabbcatEdit");
  }
}
