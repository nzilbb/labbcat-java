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
import java.util.HashSet;
import nzilbb.ag.StoreException;
import nzilbb.labbcat.*;

public class TestGraphStoreQuery 
{
   // YOU MUST ENSURE THE FOLLOWING SETTINGS ARE VALID FOR YOU TEST LABBCAT SERVER:
   static String labbcatUrl = "http://localhost:8080/labbcat/";
   static String username = "labbcat";
   static String password = "labbcat";
   static GraphStoreQuery store;

   @BeforeClass public static void createStore()
   {
      try
      {
         store = new GraphStoreQuery(labbcatUrl, username, password).setBatchMode(true);
      }
      catch(MalformedURLException exception)
      {
         fail("Could not create GraphStoreQuery object");
      }
   }
   
   @Test(expected = StoreException.class) public void invalidCredentials()
      throws Exception
   {
      GraphStoreQuery store = new GraphStoreQuery(labbcatUrl, "xxx", "xxx")
         .setBatchMode(true);
      store.getId();
   }

   @Test(expected = StoreException.class) public void credentialsRequired()
      throws Exception
   {
      GraphStoreQuery store = new GraphStoreQuery(labbcatUrl)
         .setBatchMode(true);
      store.getId();
   }
   
   @Test(expected = MalformedURLException.class) public void malformedURLException()
      throws Exception
   {
      GraphStoreQuery store = new GraphStoreQuery("xxx", username, password)
         .setBatchMode(true);
      store.getId();
   }

   @Test(expected = StoreException.class) public void nonLabbcatUrl()
      throws Exception
   {
      GraphStoreQuery store = new GraphStoreQuery("http://tld/", username, password)
         .setBatchMode(true);
      store.getId();
   }

   @Test public void getId()
      throws Exception
   {
      String id = store.getId();
      assertEquals("ID matches the url",
                   labbcatUrl, id);
   }

   @Test public void getLayerIds()
      throws Exception
   {
      String[] ids = store.getLayerIds();
      //for (String id : ids) System.out.println("layer " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      HashSet<String> idSet = new HashSet<String>(Arrays.asList(ids));
      assertTrue("Has transcript layer",
                 idSet.contains("transcript"));
      assertTrue("Has turns layer",
                 idSet.contains("turns"));
      assertTrue("Has utterances layer",
                 idSet.contains("utterances"));
      assertTrue("Has transcript_type layer",
                 idSet.contains("transcript_type"));
   }

   @Test public void getCorpusIds()
      throws Exception
   {
      String[] ids = store.getCorpusIds();
      // for (String id : ids) System.out.println("corpus " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void getParticipantIds()
      throws Exception
   {
      String[] ids = store.getParticipantIds();
      // for (String id : ids) System.out.println("participant " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void getGraphIds()
      throws Exception
   {
      String[] ids = store.getGraphIds();
      // for (String id : ids) System.out.println("graph " + id);
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   @Test public void countMatchingParticipantIds()
      throws Exception
   {
      int count = store.countMatchingParticipantIds("id MATCHES '.+'");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMatchingParticipantIds()
      throws Exception
   {
      String[] ids = store.getMatchingParticipantIds("id MATCHES '.+'");
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2)
      {
         System.out.println("Too few participants to test pagination");
      }
      else
      {
         ids = store.getMatchingParticipantIds("id MATCHES '.+'", 2, 0);
         assertEquals("Two IDs are returned",
                      2, ids.length);
      }         
   }

   @Test public void getGraphIdsInCorpus()
      throws Exception
   {
      String[] ids = store.getCorpusIds();
      assertTrue("There's at least one corpus",
                 ids.length > 0);
      String corpus = ids[0];
      ids = store.getGraphIdsInCorpus(corpus);
      assertTrue("Some IDs are returned for corpus " + corpus,
                 ids.length > 0);
   }

   @Test public void getGraphIdsWithParticipant()
      throws Exception
   {
      String[] ids = store.getParticipantIds();
      assertTrue("There's at least one participant",
                 ids.length > 0);
      String participant = ids[0];
      ids = store.getGraphIdsWithParticipant(participant);
      assertTrue("Some IDs are returned for participant " + participant,
                 ids.length > 0);
   }

   @Test public void countMatchingGraphIds()
      throws Exception
   {
      int count = store.countMatchingGraphIds("id MATCHES '.+'");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMatchingGraphIds()
      throws Exception
   {
      String[] ids = store.getMatchingGraphIds("id MATCHES '.+'");
      assertTrue("Some IDs are returned",
                 ids.length > 0);
      if (ids.length < 2)
      {
         System.out.println("Too few graphs to test pagination");
      }
      else
      {
         ids = store.getMatchingGraphIds("id MATCHES '.+'", 2, 0, "id DESC");
         assertEquals("Two IDs are returned",
                      2, ids.length);
      }         
   }

   @Test public void countAnnotations()
      throws Exception
   {
      String[] ids = store.getMatchingGraphIds("id MATCHES '.+'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      long count = store.countAnnotations(graphId, "orthography");
      assertTrue("There are some matches",
                 count > 0);
   }

   @Test public void getMedia()
      throws Exception
   {
      String[] ids = store.getMatchingGraphIds("id MATCHES 'Agnes.+\\.trs'", 1, 0);
      assertTrue("Some graph IDs are returned",
                 ids.length > 0);
      String graphId = ids[0];
      String url = store.getMedia(graphId, "", "audio/wav");
      assertNotNull("There is some media",
                    url);
   }

   // @Test public void countMatchingAnnotations()
   //    throws Exception
   // {
   //    store.setVerbose(true);
   //    int count = store.countMatchingAnnotations("layer.id = 'orthography' AND label MATCHES 'and'");
   //    assertTrue("There are some matches",
   //               count > 0);
   // }

   // @Test public void getMatchingAnnotations()
   //    throws Exception
   // {
   //    String[] ids = store.countMatchingAnnotations("id MATCHES '.+'");
   //    assertTrue("Some IDs are returned",
   //               ids.length > 0);
   //    if (ids.length < 2)
   //    {
   //       System.out.println("Too few graphs to test pagination");
   //    }
   //    else
   //    {
   //       ids = store.countMatchingAnnotations("id MATCHES '.+'", 2, 0);
   //       assertEquals("Two IDs are returned",
   //                    2, ids.length);
   //    }         
   // }

   public static void main(String args[]) 
   {
      org.junit.runner.JUnitCore.main("nzilbb.labbcat.TestGraphStoreQuery");
   }
}
