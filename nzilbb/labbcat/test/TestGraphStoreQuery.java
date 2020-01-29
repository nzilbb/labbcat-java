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
import nzilbb.ag.StoreException;
import nzilbb.labbcat.*;

public class TestGraphStoreQuery 
{
   // YOU MUST ENSURE THE FOLLOWING SETTINGS ARE VALID FOR YOU TEST LABBCAT SERVER:
   String labbcatUrl = "http://localhost:8080/labbcat/";
   String username = "labbcat";
   String password = "labbcat";
   
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

   @Test public void getLayerIds()
      throws Exception
   {
      GraphStoreQuery store = new GraphStoreQuery(labbcatUrl, username, password)
         .setBatchMode(true);
      String[] ids = store.getLayerIds();
      
      assertTrue("Some IDs are returned",
                 ids.length > 0);
   }

   public static void main(String args[]) 
   {
      org.junit.runner.JUnitCore.main("nzilbb.labbcat.TestGraphStoreQuery");
   }
}
