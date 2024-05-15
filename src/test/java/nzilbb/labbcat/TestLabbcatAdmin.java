//
// Copyright 2020-2022 New Zealand Institute of Language, Brain and Behaviour, 
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
import java.io.FileInputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import nzilbb.ag.Anchor;
import nzilbb.ag.Annotation;
import nzilbb.ag.Constants;
import nzilbb.ag.Layer;
import nzilbb.ag.MediaFile;
import nzilbb.ag.MediaTrackDefinition;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.model.*;
import nzilbb.util.IO;

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

  @Test public void messageLocalization() throws Exception {
    try {
      // can't delete it again
      labbcat.setLanguage("en");
      labbcat.deleteCorpus("this-corpus-doesn't-exist");
      fail("Can't delete corpus that doesn't exist");
    } catch(ResponseException exception) {
      String error = exception.getResponse().getErrors().elementAt(0);
      assertTrue("Error is in English: " + error,
                 error.matches(".*not found.*"));
    }
      
    try {
      // can't delete it again
      labbcat.setLanguage("es");
      labbcat.deleteCorpus("this-corpus-doesn't-exist");
      fail("Can't delete corpus that doesn't exist");
    } catch(ResponseException exception) {
      String error = exception.getResponse().getErrors().elementAt(0);
      assertTrue("Error is Spanish: " + error,
                 error.matches(".*no existe.*"));
    }
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

  /** Test deprecated project CRUD operations. */
  @Test public void newProjectUpdateProjectAndDeleteProject() throws Exception {
    Project originalProject = (Project)new Project()
      .setProject("unit-test")
      .setDescription("Temporary project for unit testing");
      
    try {
      Project newProject = labbcat.createProject(originalProject);
      assertNotNull("Project returned", newProject);
      assertEquals("Name correct",
                   originalProject.getProject(), newProject.getProject());
      assertEquals("Description correct",
                   originalProject.getDescription(), newProject.getDescription());
         
      try {
        labbcat.createProject(originalProject);
        fail("Can't create a project with existing name");
      }
      catch(Exception exception) {}
         
      Project[] projects = labbcat.readProjects();
      // ensure the project exists
      assertTrue("There's at least one project", projects.length >= 1);
      boolean found = false;
      for (Project c : projects) {
        if (c.getProject().equals(originalProject.getProject())) {
          found = true;
          break;
        }
      }
      assertTrue("Project was added", found);

      // update it
      Project updatedProject = (Project)new Project()
        .setProject("unit-test")
        .setDescription("Changed description");
         
      Project changedProject = labbcat.updateProject(updatedProject);
      assertNotNull("Project returned", changedProject);
      assertEquals("Updated Name correct",
                   updatedProject.getProject(), changedProject.getProject());
      assertEquals("Updated Description correct",
                   updatedProject.getDescription(), changedProject.getDescription());

      // delete it
      labbcat.deleteProject(originalProject.getProject());

      Project[] projectsAfter = labbcat.readProjects();
      // ensure the project no longer exists
      boolean foundAfter = false;
      for (Project c : projectsAfter) {
        if (c.getProject().equals(originalProject.getProject())) {
          foundAfter = true;
          break;
        }
      }
      assertFalse("Project is gone", foundAfter);

      try {
        // can't delete it again
        labbcat.deleteProject(originalProject);
        fail("Can't delete project that doesn't exist");
      } catch(Exception exception) {
      }

    } finally {
      // ensure it's not there
      try {
        labbcat.deleteProject(originalProject);
      } catch(Exception exception) {}         
    }
  }

  @Test public void newCategoryUpdateCategoryAndDeleteCategory() throws Exception {
    Category originalCategory = new Category()
      .setClassId("transcript")
      .setCategory("unit-test")
      .setDescription("Temporary category for unit testing")
      .setDisplayOrder(999);
      
    try {
      Category newCategory = labbcat.createCategory(originalCategory);
      assertNotNull("Category returned", newCategory);
      assertEquals("Class correct",
                   originalCategory.getClassId(), newCategory.getClassId());
      assertEquals("Name correct",
                   originalCategory.getCategory(), newCategory.getCategory());
      assertEquals("Description correct",
                   originalCategory.getDescription(), newCategory.getDescription());
      assertEquals("displayOrder correct",
                   originalCategory.getDisplayOrder(), newCategory.getDisplayOrder());
         
      try {
        labbcat.createCategory(originalCategory);
        fail("Can't create a category with existing name");
      }
      catch(Exception exception) {}
         
      Category[] categories = labbcat.readCategories("transcript");
      // ensure the category exists
      assertTrue("There's at least one category", categories.length >= 1);
      boolean found = false;
      for (Category c : categories) {
        if (c.getCategory().equals(originalCategory.getCategory())) {
          found = true;
          break;
        }
      }
      assertTrue("Category was added", found);

      // update it
      Category updatedCategory = new Category()
        .setClassId("transcript")
        .setCategory("unit-test")
        .setDescription("Changed description")
        .setDisplayOrder(888);
      
      Category changedCategory = labbcat.updateCategory(updatedCategory);
      assertNotNull("Category returned", changedCategory);
      assertEquals("Updated Name correct",
                   updatedCategory.getCategory(), changedCategory.getCategory());
      assertEquals("Updated Description correct",
                   updatedCategory.getDescription(), changedCategory.getDescription());
      assertEquals("Updated displayOrder correct",
                   updatedCategory.getDisplayOrder(), changedCategory.getDisplayOrder());

      // delete it
      labbcat.deleteCategory("transcript", originalCategory.getCategory());
      
      Category[] categoriesAfter = labbcat.readCategories("transcript");
      // ensure the category no longer exists
      boolean foundAfter = false;
      for (Category c : categoriesAfter) {
        if (c.getCategory().equals(originalCategory.getCategory())) {
          foundAfter = true;
          break;
        }
      }
      assertFalse("Category is gone", foundAfter);

      try {
        // can't delete it again
        labbcat.deleteCategory(originalCategory);
        fail("Can't delete category that doesn't exist");
      } catch(Exception exception) {
      }

    } finally {
      // ensure it's not there
      try {
        labbcat.deleteCategory(originalCategory);
      } catch(Exception exception) {}         
    }
  }

  @Test public void newMediaTrackUpdateMediaTrackAndDeleteMediaTrack() throws Exception {
    MediaTrack originalMediaTrack = new MediaTrack()
      .setSuffix("unit-test")
      .setDescription("Temporary mediaTrack for unit testing")
      .setDisplayOrder(99);
      
    try {
      MediaTrack newMediaTrack = labbcat.createMediaTrack(originalMediaTrack);
      assertNotNull("MediaTrack returned", newMediaTrack);
      assertEquals("Name correct",
                   originalMediaTrack.getSuffix(), newMediaTrack.getSuffix());
      assertEquals("Description correct",
                   originalMediaTrack.getDescription(), newMediaTrack.getDescription());
      assertEquals("Display order correct",
                   originalMediaTrack.getDisplayOrder(), newMediaTrack.getDisplayOrder());
         
      try {
        labbcat.createMediaTrack(originalMediaTrack);
        fail("Can't create a mediaTrack with existing name");
      }
      catch(Exception exception) {}
         
      MediaTrack[] mediaTracks = labbcat.readMediaTracks();
      // ensure the mediaTrack exists
      assertTrue("There's at least one mediaTrack", mediaTracks.length >= 1);
      boolean found = false;
      for (MediaTrack c : mediaTracks) {
        if (c.getSuffix().equals(originalMediaTrack.getSuffix())) {
          found = true;
          break;
        }
      }
      assertTrue("MediaTrack was added", found);

      // update it
      MediaTrack updatedMediaTrack = new MediaTrack()
        .setSuffix("unit-test")
        .setDescription("Changed description")
        .setDisplayOrder(100);
         
      MediaTrack changedMediaTrack = labbcat.updateMediaTrack(updatedMediaTrack);
      assertNotNull("MediaTrack returned", changedMediaTrack);
      assertEquals("Updated Name correct",
                   updatedMediaTrack.getSuffix(), changedMediaTrack.getSuffix());
      assertEquals("Updated Description correct",
                   updatedMediaTrack.getDescription(), changedMediaTrack.getDescription());
      assertEquals("Updated Display order correct",
                   updatedMediaTrack.getDisplayOrder(), changedMediaTrack.getDisplayOrder());

      // delete it
      labbcat.deleteMediaTrack(originalMediaTrack.getSuffix());

      MediaTrack[] mediaTracksAfter = labbcat.readMediaTracks();
      // ensure the mediaTrack no longer exists
      boolean foundAfter = false;
      for (MediaTrack c : mediaTracksAfter) {
        if (c.getSuffix().equals(originalMediaTrack.getSuffix())) {
          foundAfter = true;
          break;
        }
      }
      assertFalse("MediaTrack is gone", foundAfter);

      try {
        // can't delete it again
        labbcat.deleteMediaTrack(originalMediaTrack);
        fail("Can't delete mediaTrack that doesn't exist");
      } catch(Exception exception) {
      }

    } finally {
      // ensure it's not there
      try {
        labbcat.deleteMediaTrack(originalMediaTrack);
      } catch(Exception exception) {}         
    }
  }

  @Test public void newRoleUpdateRoleAndDeleteRole() throws Exception {
    Role originalRole = new Role()
      .setRoleId("unit-test")
      .setDescription("Temporary role for unit testing");
      
    try {
      Role newRole = labbcat.createRole(originalRole);
      assertNotNull("Role returned", newRole);
      assertEquals("Name correct",
                   originalRole.getRoleId(), newRole.getRoleId());
      assertEquals("Description correct",
                   originalRole.getDescription(), newRole.getDescription());
         
      try {
        labbcat.createRole(originalRole);
        fail("Can't create a role with existing name");
      }
      catch(Exception exception) {}
         
      Role[] roles = labbcat.readRoles();
      // ensure the role exists
      assertTrue("There's at least one role", roles.length >= 1);
      boolean found = false;
      for (Role c : roles) {
        if (c.getRoleId().equals(originalRole.getRoleId())) {
          found = true;
          break;
        }
      }
      assertTrue("Role was added", found);

      // update it
      Role updatedRole = new Role()
        .setRoleId("unit-test")
        .setDescription("Changed description");
         
      Role changedRole = labbcat.updateRole(updatedRole);
      assertNotNull("Role returned", changedRole);
      assertEquals("Updated Name correct",
                   updatedRole.getRoleId(), changedRole.getRoleId());
      assertEquals("Updated Description correct",
                   updatedRole.getDescription(), changedRole.getDescription());

      // delete it
      labbcat.deleteRole(originalRole.getRoleId());

      Role[] rolesAfter = labbcat.readRoles();
      // ensure the role no longer exists
      boolean foundAfter = false;
      for (Role c : rolesAfter) {
        if (c.getRoleId().equals(originalRole.getRoleId())) {
          foundAfter = true;
          break;
        }
      }
      assertFalse("Role is gone", foundAfter);

      try {
        // can't delete it again
        labbcat.deleteRole(originalRole);
        fail("Can't delete role that doesn't exist");
      } catch(Exception exception) {
      }
         
    } finally {
      // ensure it's not there
      try {
        labbcat.deleteRole(originalRole);
      } catch(Exception exception) {}         
    }
  }

  @Test public void newRolePermissionUpdateRolePermissionAndDeleteRolePermission()
    throws Exception {
    RolePermission originalRolePermission = new RolePermission()
      .setRoleId("admin")
      .setEntity("t")
      .setLayerId("corpus")
      .setValuePattern("unit-test.*");
      
    // ensure the record doesn't exist to start with
    try {
      labbcat.deleteRolePermission(originalRolePermission);
    } catch(Exception exception) {}
      
    try
    {
      RolePermission newRolePermission = labbcat.createRolePermission(originalRolePermission);
      assertNotNull("RolePermission returned", newRolePermission);
      assertEquals("roleId correct",
                   originalRolePermission.getRoleId(), newRolePermission.getRoleId());
      assertEquals("entity correct",
                   originalRolePermission.getEntity(), newRolePermission.getEntity());
      assertEquals("layerId correct",
                   originalRolePermission.getLayerId(), newRolePermission.getLayerId());
      assertEquals("valudPattern correct",
                   originalRolePermission.getValuePattern(), newRolePermission.getValuePattern());
         
      try {
        labbcat.createRolePermission(originalRolePermission);
        fail("Can't create a rolePermission with existing name");
      }
      catch(Exception exception) {}
         
      RolePermission[] rolePermissions
        = labbcat.readRolePermissions(originalRolePermission.getRoleId());
      // ensure the rolePermission exists
      assertTrue("There's at least one rolePermission", rolePermissions.length >= 1);
      boolean found = false;
      for (RolePermission c : rolePermissions) {
        assertEquals("Only correct role listed",
                     originalRolePermission.getRoleId(), c.getRoleId());
        if (c.getRoleId().equals(originalRolePermission.getRoleId())
            && c.getEntity().equals(originalRolePermission.getEntity())) {
          found = true;
        }
      }
      assertTrue("RolePermission was added", found);

      // update it
      RolePermission updatedRolePermission = new RolePermission()
        .setRoleId("admin")
        .setEntity("t")
        .setLayerId("transcript_language")
        .setValuePattern("en.*");
         
      RolePermission changedRolePermission = labbcat.updateRolePermission(updatedRolePermission);
      assertNotNull("RolePermission returned", changedRolePermission);
      assertEquals("roleId unchanged",
                   originalRolePermission.getRoleId(), changedRolePermission.getRoleId());
      assertEquals("entity unchanged",
                   originalRolePermission.getEntity(), changedRolePermission.getEntity());
      assertEquals("layerId updated",
                   updatedRolePermission.getLayerId(), changedRolePermission.getLayerId());
      assertEquals("valudPattern updated",
                   updatedRolePermission.getValuePattern(), changedRolePermission.getValuePattern());
      // delete it
      labbcat.deleteRolePermission(
        originalRolePermission.getRoleId(), originalRolePermission.getEntity());
         
      RolePermission[] rolePermissionsAfter = labbcat.readRolePermissions(
        originalRolePermission.getRoleId());
      // ensure the rolePermission no longer exists
      boolean foundAfter = false;
      for (RolePermission c : rolePermissionsAfter) {
        if (c.getRoleId().equals(originalRolePermission.getRoleId())
            && c.getEntity().equals(originalRolePermission.getEntity())) {
          foundAfter = true;
          break;
        }
      }
      assertFalse("RolePermission is gone", foundAfter);

      try {
        // can't delete it again
        labbcat.deleteRolePermission(originalRolePermission);
        fail("Can't delete rolePermission that doesn't exist");
      } catch(Exception exception) {
      }
         
    } finally {
      // ensure it's not there
      try {
        labbcat.deleteRolePermission(originalRolePermission);
      } catch(Exception exception) {}         
    }
  }
   
  @Test public void readSystemAttributeAndUpdateSystemAttribute() throws Exception {
    SystemAttribute[] systemAttributes = labbcat.readSystemAttributes();
    // ensure the systemAttribute exists
    assertTrue("There's at least one systemAttribute", systemAttributes.length >= 1);
    SystemAttribute originalTitle = null;
    SystemAttribute originalTranscriptSubdir = null;
    for (SystemAttribute a : systemAttributes) {
      if (a.getAttribute().equals("title")) {
        originalTitle = a;
      } else if (a.getAttribute().equals("transcriptSubdir")) {
        originalTranscriptSubdir = a;
      }
    }
    assertNotNull("title attribute returned", originalTitle);
    assertNotNull("title attribute has value", originalTitle.getValue());
    assertNotNull("transcriptSubdir attribute returned", originalTranscriptSubdir);
    assertNotNull("transcriptSubdir attribute has value", originalTranscriptSubdir.getValue());
    assertEquals("transcriptSubdir attribute is read-only",
                 "readonly", originalTranscriptSubdir.getType());

    try {
      // update it
      SystemAttribute updatedTitle = new SystemAttribute()
        .setAttribute("title")
        .setValue("unit-test") // should be updated
        .setType("Changed type") // should not be updated
        .setStyle("Changed style") // should not be updated
        .setLabel("Changed label"); // should not be updated
      
      SystemAttribute changedTitle = labbcat.updateSystemAttribute(updatedTitle);
      assertNotNull("SystemAttribute returned after object update", changedTitle);
      assertEquals("Updated Value correct after object update",
                   updatedTitle.getValue(), changedTitle.getValue());

      SystemAttribute[] systemAttributesAfter = labbcat.readSystemAttributes();
      // ensure only the value has been updated
      SystemAttribute listedTitle = null;
      for (SystemAttribute a : systemAttributesAfter) {
        if (a.getAttribute().equals("title")) {
          listedTitle = a;
          break;
        }
      }
      assertNotNull("SystemAttribute is still there", listedTitle);
      assertEquals("Updated Value correct", updatedTitle.getValue(), listedTitle.getValue());
      assertEquals("type unchanged", originalTitle.getType(), listedTitle.getType());
      assertEquals("style unchanged", originalTitle.getStyle(), listedTitle.getStyle());
      assertEquals("label unchanged", originalTitle.getLabel(), listedTitle.getLabel());
      assertEquals("description unchanged",
                   originalTitle.getDescription(), listedTitle.getDescription());

      changedTitle = labbcat.updateSystemAttribute("title", "updated-value");
      assertNotNull("SystemAttribute returned after string update", changedTitle);
      assertEquals("Updated Value correct after string update",
                   "updated-value", changedTitle.getValue());
         
      try {
        SystemAttribute updatedTranscriptSubdir = new SystemAttribute()
          .setAttribute("transcriptSubdir")
          .setValue("unit-test");
        // can't update read-only attribute
        labbcat.updateSystemAttribute(updatedTranscriptSubdir);
        // if we got here, it was incorrectly updated, so put back the original value
        labbcat.updateSystemAttribute(originalTranscriptSubdir);
        // ... and then fail
        fail("Can't update read-only attribute");
      } catch(Exception exception) {
      }
         
      try {
        SystemAttribute nonexistentAttribute = new SystemAttribute()
          .setAttribute("unit-test")
          .setValue("unit-test");
        // can't update read-only attribute
        labbcat.updateSystemAttribute(nonexistentAttribute);
        fail("Can't update nonexistent attribute");
      } catch(Exception exception) {
      }
         
    } finally {
      // put back original title
      try {
        labbcat.updateSystemAttribute(originalTitle);
      } catch(Exception exception) {
        System.out.println("ERROR restoring title: " + exception);
      }
    }
  }

  @Test public void updateInfo() throws Exception {
    String originalInfo = labbcat.getInfo();
    assertNotNull("There is info", originalInfo);

    try {
      // update it
      String changedInfo = originalInfo + " <div>unit-test</div>";
      labbcat.updateInfo(changedInfo);
         
      String newInfo = labbcat.getInfo();
      assertEquals("Updated info correct", changedInfo, newInfo);         
         
    } finally {
      labbcat.updateInfo(originalInfo);
    }
  }

  @Test public void saveTranscriptTypeOptions() throws Exception {
    Layer originalTranscriptType = labbcat.getLayer("transcript_type");
    assertNotNull("There's a transcript_type layer",
                  originalTranscriptType);
    assertTrue("There's at least one transcript type",
               originalTranscriptType.getValidLabels().size() > 0);

    try {

      Layer editedTranscriptType1 = (Layer)originalTranscriptType.clone();
         
      // add an option
      String newOption1 = "unit-test-1";
      editedTranscriptType1.getValidLabels().put(newOption1, newOption1);

      Layer editedTranscriptType2 = labbcat.saveLayer(editedTranscriptType1);

      assertTrue("new option 1 is there: " + editedTranscriptType2.getValidLabels(),
                 editedTranscriptType2.getValidLabels().keySet().contains(newOption1));
      assertEquals("All options are what we expect",
                   editedTranscriptType1.getValidLabels(),
                   editedTranscriptType2.getValidLabels());
      // remove an option
      editedTranscriptType2.getValidLabels().remove(newOption1);      
         
      // add an option
      String newOption2 = "unit-test-2";
      editedTranscriptType2.getValidLabels().put(newOption2, newOption2);
      
      Layer finalTranscriptType = labbcat.saveLayer(editedTranscriptType2);

      assertFalse("old option 1 isn't there",
                  finalTranscriptType.getValidLabels().keySet().contains(newOption1));
      assertTrue("new option 2 is there",
                 finalTranscriptType.getValidLabels().keySet().contains(newOption2));
      assertEquals("All options are what we expect",
                   editedTranscriptType2.getValidLabels(),
                   finalTranscriptType.getValidLabels());
         
    } finally {
      // put back original options
      try {
        labbcat.saveLayer(originalTranscriptType);
      } catch(Exception exception) {
        System.out.println("ERROR restoring title: " + exception);
      }
    }
  }
   
  @Test public void newSaveDeleteLayer() throws Exception {
    Layer testLayer = new Layer("unit-test", "Unit test layer")
      .setParentId("word")
      .setAlignment(Constants.ALIGNMENT_NONE)
      .setPeers(true)
      .setPeersOverlap(true)
      .setParentIncludes(true)
      .setSaturated(true)
      .setType(Constants.TYPE_STRING);
    // TODO validLabels

    try {
      labbcat.getLayer(testLayer.getId());
      fail("Test layer doesn't already exist: " + testLayer.getId());
    } catch (StoreException x) {
    }
    try {

      // create the layer
      Layer newLayer = labbcat.newLayer(testLayer);
      assertNotNull("new layer returned", newLayer);
      assertEquals("created ID",
                   newLayer.getId(), testLayer.getId());
      assertEquals("created Description",
                   newLayer.getDescription(), testLayer.getDescription());
      assertEquals("created parent",
                   newLayer.getParentId(), testLayer.getParentId());
      assertEquals("created alignment",
                   newLayer.getAlignment(), testLayer.getAlignment());
      assertEquals("created peers",
                   newLayer.getPeers(), testLayer.getPeers());
      assertEquals("created peersOverlap",
                   newLayer.getPeersOverlap(), testLayer.getPeersOverlap());
      assertEquals("created parentIncludes",
                   newLayer.getParentIncludes(), testLayer.getParentIncludes());
      assertEquals("created saturated",
                   newLayer.getSaturated(), testLayer.getSaturated());
      assertEquals("created Type",
                   newLayer.getType(), testLayer.getType());
      // TODO validLabels

      // ensure it exists
      newLayer = labbcat.getLayer(testLayer.getId());
      assertNotNull("new layer returned", newLayer);
      assertEquals("created ID",
                   newLayer.getId(), testLayer.getId());
      assertEquals("created Description",
                   newLayer.getDescription(), testLayer.getDescription());
      assertEquals("created parent",
                   newLayer.getParentId(), testLayer.getParentId());
      assertEquals("created alignment",
                   newLayer.getAlignment(), testLayer.getAlignment());
      assertEquals("created peers",
                   newLayer.getPeers(), testLayer.getPeers());
      assertEquals("created peersOverlap",
                   newLayer.getPeersOverlap(), testLayer.getPeersOverlap());
      assertEquals("created parentIncludes",
                   newLayer.getParentIncludes(), testLayer.getParentIncludes());
      assertEquals("created saturated",
                   newLayer.getSaturated(), testLayer.getSaturated());
      assertEquals("created Type",
                   newLayer.getType(), testLayer.getType());
      // TODO validLabels

      // edit it
      testLayer.setDescription("Changed description")
        .setParentId("turns") // this shouldn't be updated
        .setAlignment(Constants.ALIGNMENT_INTERVAL)
        .setPeers(false)
        .setPeersOverlap(false)
        .setParentIncludes(false)
        .setSaturated(false)
        .setType(Constants.TYPE_NUMBER);
      // TODO validLabels
      newLayer = labbcat.saveLayer(testLayer);
      assertNotNull("new layer returned", newLayer);
      assertEquals("saved ID",
                   newLayer.getId(), testLayer.getId());
      assertEquals("saved Description",
                   newLayer.getDescription(), testLayer.getDescription());
      assertEquals("parent not saved",
                   newLayer.getParentId(), "word");
      assertEquals("saved alignment",
                   newLayer.getAlignment(), testLayer.getAlignment());
      assertEquals("saved peers",
                   newLayer.getPeers(), testLayer.getPeers());
      assertEquals("saved peersOverlap",
                   newLayer.getPeersOverlap(), testLayer.getPeersOverlap());
      assertEquals("saved parentIncludes",
                   newLayer.getParentIncludes(), testLayer.getParentIncludes());
      assertEquals("saved saturated",
                   newLayer.getSaturated(), testLayer.getSaturated());
      assertEquals("saved Type",
                   newLayer.getType(), testLayer.getType());
      // TODO validLabels
         
      // ensure changes are saved
      newLayer = labbcat.getLayer(testLayer.getId());
      assertNotNull("new layer returned", newLayer);
      assertEquals("saved ID",
                   newLayer.getId(), testLayer.getId());
      assertEquals("saved Description",
                   newLayer.getDescription(), testLayer.getDescription());
      assertEquals("parent not saved",
                   newLayer.getParentId(), "word");
      assertEquals("saved alignment",
                   newLayer.getAlignment(), testLayer.getAlignment());
      assertEquals("saved peers",
                   newLayer.getPeers(), testLayer.getPeers());
      assertEquals("saved peersOverlap",
                   newLayer.getPeersOverlap(), testLayer.getPeersOverlap());
      assertEquals("saved parentIncludes",
                   newLayer.getParentIncludes(), testLayer.getParentIncludes());
      assertEquals("saved saturated",
                   newLayer.getSaturated(), testLayer.getSaturated());
      assertEquals("saved Type",
                   newLayer.getType(), testLayer.getType());
      // TODO validLabels

      // delete it
      labbcat.deleteLayer(testLayer.getId());

      // ensure it's been deleted
      try {
        labbcat.getLayer(testLayer.getId());
        fail("Should not be able to get layer that has been deleted: " + testLayer.getId());
      } catch (StoreException x) {
      }
         
    } finally {
      // ensure layer is deleted
      try {
        labbcat.deleteLayer(testLayer.getId());
      } catch(Exception exception) {
      }
    }
  }
   
  @Test public void newUserUpdateUserDeleteUserAndChangePassword() throws Exception {
    Role testRole = new Role()
      .setRoleId("unit-test")
      .setDescription("Temporary role for unit testing");
    labbcat.createRole(testRole);

    String[] roles = { testRole.getRoleId() };
    User originalUser = new User()
      .setUser("unit-test")
      .setEmail("unit-test@tld.org")
      .setResetPassword(true)
      .setRoles(roles);
      
    try {
      User newUser = labbcat.createUser(originalUser);
      assertNotNull("User returned", newUser);
      assertEquals("ID correct",
                   originalUser.getUser(), newUser.getUser());
      assertEquals("Email correct",
                   originalUser.getEmail(), newUser.getEmail());
      assertEquals("Reset Password correct",
                   originalUser.getResetPassword(), newUser.getResetPassword());
      assertArrayEquals("Roles correct",
                        originalUser.getRoles(), newUser.getRoles());
         
      try {
        labbcat.createUser(originalUser);
        fail("Can't create a user with existing ID");
      }
      catch(Exception exception) {}
         
      User[] users = labbcat.readUsers();
      // ensure the user exists
      assertTrue("There's at least one user", users.length >= 1);
      boolean found = false;
      for (User c : users) {
        if (c.getUser().equals(originalUser.getUser())) {
          found = true;
          break;
        }
      }
      assertTrue("User was added", found);

      // update it
      String[] editedRoles = { "view" };
      User updatedUser = new User()
        .setUser("unit-test")
        .setEmail("new@tld.org")
        .setResetPassword(true)
        .setRoles(roles);
         
      User changedUser = labbcat.updateUser(updatedUser);
      assertNotNull("User returned", changedUser);
      assertEquals("ID correct",
                   updatedUser.getUser(), changedUser.getUser());
      assertEquals("Email correct",
                   updatedUser.getEmail(), changedUser.getEmail());
      assertEquals("Reset Password correct",
                   updatedUser.getResetPassword(), changedUser.getResetPassword());
      assertArrayEquals("Roles correct",
                        updatedUser.getRoles(), changedUser.getRoles());

      // change password
      labbcat.setPassword(originalUser.getUser(), new java.util.Date().toString(), true);

      // delete it
      labbcat.deleteUser(originalUser.getUser());

      User[] usersAfter = labbcat.readUsers();
      // ensure the user no longer exists
      boolean foundAfter = false;
      for (User c : usersAfter) {
        if (c.getUser().equals(originalUser.getUser())) {
          foundAfter = true;
          break;
        }
      }
      assertFalse("User is gone", foundAfter);

      try {
        // can't delete it again
        labbcat.deleteUser(originalUser);
        fail("Can't delete user that doesn't exist");
      } catch(Exception exception) {
      }
         
      try {
        // can't set password again
        labbcat.setPassword(originalUser.getUser(), new java.util.Date().toString(), true);
        fail("Can't set passord for user that doesn't exist");
      } catch(Exception exception) {
      }
         
    } finally {
      // ensure it's not there
      try {
        labbcat.deleteUser(originalUser);
      } catch(Exception exception) {}
      // remove the test role
      try {
        labbcat.deleteRole(testRole);
      } catch(Exception exception) {}         
    }
  }
  
  @Test public void layerDictionaryManagement() throws Exception {
    File lexiconPath = new File(getDir(), "lexicon.txt");

    try {
      // upload a lexicon
      labbcat.loadLexicon(lexiconPath, "unit-test", ",", "word,definition", null, null, false);

      String[] targetEntries = { "test-word", "DictionaryEntry", "LayerDictionaryEntry" };
      File csv = labbcat.getDictionaryEntries(
        "FlatFileDictionary", "unit-test:word->definition", targetEntries);
      Map<String,List<String>> entries = readEntries(csv);
      assertEquals("Word has two entries " + entries.get("test-word"),
                   2, entries.get("test-word").size());
      assertEquals("First entry correct",
                   "test-word-1", entries.get("test-word").get(0));
      assertEquals("Second entry correct",
                   "test-word-2", entries.get("test-word").get(1));
      assertEquals("DictionaryEntry has no entries " + entries.get("DictionaryEntry"),
                   0, entries.get("DictionaryEntry").size());
      assertEquals("LayerDictionaryEntry has no entries " + entries.get("LayerDictionaryEntry"),
                   0, entries.get("LayerDictionaryEntry").size());
            
      // get annotator descriptor TODO
      // descriptor = labbcat.getAnnotatorDescriptor("FlatLexiconTagger")
      // self.assertIsNotNone(descriptor, "There is a descriptor")
      // for key in ["annotatorId", "info", "extApiInfo"]:
      //     with self.subTest(key=key):
      //         self.assertIn(key, descriptor, "Has " + key)
       
      // test annotatorExt
      String lexiconListJSON = labbcat.annotatorExt("FlatLexiconTagger", "listLexicons");
      // JsonArray lexiconList = Json.createReader(new StringReader(lexiconListJSON)).readArray();
      assertTrue("Has unit-text lexicon: " + lexiconListJSON,
                 lexiconListJSON.indexOf("\"unit-test\"") >= 0);
      labbcat.addDictionaryEntry(
        "FlatFileDictionary", "unit-test:word->definition", "DictionaryEntry", "new-entry-1");
        
      // now there's a definition
      csv = labbcat.getDictionaryEntries(
        "FlatFileDictionary", "unit-test:word->definition", targetEntries);
      entries = readEntries(csv);
      assertEquals("DictionaryEntry has one entries " + entries.get("DictionaryEntry"),
                   1, entries.get("DictionaryEntry").size());
      assertEquals("DictionaryEntry entry is correct",
                   "new-entry-1", entries.get("DictionaryEntry").get(0));
            
      labbcat.removeDictionaryEntry(
        "FlatFileDictionary", "unit-test:word->definition", "DictionaryEntry", null);
       
      // now there's no definition
      csv = labbcat.getDictionaryEntries(
        "FlatFileDictionary", "unit-test:word->definition", targetEntries);
      entries = readEntries(csv);
      assertEquals("DictionaryEntry has no entries again " + entries.get("DictionaryEntry"),
                   0, entries.get("DictionaryEntry").size());
            
      // newLayer...
      Layer testLayer = new Layer("unit-test", "Unit test layer")
        .setParentId("word")
        .setAlignment(Constants.ALIGNMENT_NONE)
        .setPeers(true)
        .setPeersOverlap(true)
        .setParentIncludes(true)
        .setSaturated(true)
        .setType(Constants.TYPE_STRING);
      testLayer.put("layer_manager_id", "FlatFileDictionary");
      testLayer.put(
        "extra",
        "tokenLayerId=orthography&tagLayerId=test&dictionary=unit-test:word->definition");
      labbcat.newLayer(testLayer);
            
      labbcat.addLayerDictionaryEntry("unit-test", "LayerDictionaryEntry", "new-layer-entry-1");
            
      // now there's a definition
      csv = labbcat.getDictionaryEntries(
        "FlatFileDictionary", "unit-test:word->definition", targetEntries);
      entries = readEntries(csv);
      assertEquals("LayerDictionaryEntry has one entries " + entries.get("LayerDictionaryEntry"),
                   1, entries.get("LayerDictionaryEntry").size());
      assertEquals("LayerDictionaryEntry entry is correct",
                   "new-layer-entry-1", entries.get("LayerDictionaryEntry").get(0));
      labbcat.removeLayerDictionaryEntry("unit-test", "LayerDictionaryEntry", null);
            
      // now there's no definition
      csv = labbcat.getDictionaryEntries(
        "FlatFileDictionary", "unit-test:word->definition", targetEntries);
      entries = readEntries(csv);
      assertEquals(
        "LayerDictionaryEntry has no entries again " + entries.get("LayerDictionaryEntry"),
        0, entries.get("LayerDictionaryEntry").size());

      labbcat.deleteLexicon("unit-test");

      // lexicon not there any more
      lexiconListJSON = labbcat.annotatorExt("FlatLexiconTagger", "listLexicons");
      assertTrue("unit-test lexicon is gone: " + lexiconListJSON,
                 lexiconListJSON.indexOf("\"unit-test\"") < 0);
            
      // there are no entries
      try {
        csv = labbcat.getDictionaryEntries(
          "FlatFileDictionary", "unit-test:word->definition", targetEntries);
        csv.delete();
        fail("Getting entries for deleted lexicon should fail");
      } catch (ResponseException x) {
      }
    } finally {
      try {
        labbcat.deleteLayer("unit-test");
      } catch(Exception exception) {
        System.err.println(""+exception);
      }
    }
  }
  
  /**
   * Reads a CSV file into a map of lists, and then deletes the file.
   * @param csv Dictionary file.
   * @return A map of keys to values.
   * @throws IOException
   */
  public Map<String,List<String>> readEntries(File csv) throws Exception {
    // load csv file into map
    String csvString = IO.InputStreamToString​(new FileInputStream(csv));
    Map<String,List<String>> entries = new HashMap<String,List<String>>();
    for (String line : csvString.split("\n")) {
      if (line.length() > 0) {
        Vector<String> values = new Vector<String>(Arrays.asList(line.split(",")));
        String key = values.remove(0);
        entries.put(key, values);
      }
    } // next line
    csv.delete();
    return entries;
  } // end of readEntries()

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
    org.junit.runner.JUnitCore.main("nzilbb.labbcat.TestLabbcatAdmin");
  }
}
