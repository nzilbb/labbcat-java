//
// Copyright 2020 New Zealand Institute of Language, Brain and Behaviour, 
// University of Canterbury
// Written by Robert Fromont - robert.fromont@canterbury.ac.nz
//
//    This file is part of LaBB-CAT.
//
//    LaBB-CAT is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    LaBB-CAT is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with LaBB-CAT; if not, write to the Free Software
//    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
package nzilbb.labbcat.util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.json.JsonObject;
import nzilbb.ag.Annotation;
import nzilbb.labbcat.LabbcatAdmin;
import nzilbb.labbcat.PatternBuilder;
import nzilbb.labbcat.model.Match;
import nzilbb.labbcat.model.TaskStatus;
import nzilbb.util.CommandLineProgram;
import nzilbb.util.ProgramDescription;
import nzilbb.util.Switch;
import nzilbb.util.Timers;

/**
 * Command-line utility for load-testing LaBB-CAT servers.
 * <p> This simulates {@link #getClients() a number} of simultaneous clients performing
 * the same search task {@link #getRepetitions() a number} of times, to simulate load
 * conditions.
 * <p> The search task is:
 *  <ol>
 *   <li><q>search</q> - Start a search</li>
 *   <li><q>getMatches</q> - Get all matches (with 5 tokens context)</li>
 *   <li><q>getMatchAnnotations</q> - Get annotations on two layers for all matches</li>
 *   <li><q>getFragments</q> - Get TextGrids with two tiers for all matches</li>
 *   <li><q>getSoundFragments</q> - Get audio for all matches</li>
 *  </ol>
 * <p> Firstly, the total time for each of these, for one client, is measured for "idle"
 * conditions comparison. Then simultaneous clients are started, each repeating the
 * task. When all have finished, the mean time for each of the above is shown. 
 * <p> To invoke the load tester from the command line:<br>
 * <tt>java -classpath lib/nzilbb.ag.jar:bin/nzilbb.labbcat.jar nzilbb.labbcat.util.LoadTester --labbcaturl=<i>url</i> --username=<i>user</i> --password=<i>password</i></tt>
 * <p>Other command line options include:
 * <ul>
 *  <li>--clients=<i>n</i> - Number of clients to simulate (default is 3).</li>
 *  <li>--repetitions=<i>n</i> - Number of clients to simulate (default is 1)</li>
 *  <li>--clientdelay=<i>n</i> - Number of seconds to wait before starting each new client
 * (default is 5).</li> 
 *  <li>--searchfor=<i>s</i> - The orthography to search for when running searches
 * (default is <q>i</q>)</li>
 *  <li>--maxMatches=<i>n</i> - The maximum number of matches to process (default is 200). </li>
 *  <li>--verbose - Produce verbose logging.</li>
 * </ul>
 * <p>Once the utility is complete, a report is printed showing the mean time for each
 * step of the task, compared with the idle-conditions time for the same step, e.g.:
 * <pre>Getting statistics when idle.........
 * Match count from searching for "i": 910
 * Idle conditions:
 * 	search: 4.339s
 * 	getMatches: 3.208s
 * 	getMatchAnnotations: 0.133s
 * 	getFragments: 66.088s
 * 	getSoundFragments: 0.675s
 * Simulating 10 clients doing 1 search each...............................................................
 * Load conditions:
 * 	search: 14.960s (idle: 4.339s)
 * 	getMatches: 7.820s (idle: 3.208s)
 * 	getMatchAnnotations: 0.184s (idle: 0.133s)
 * 	getFragments: 292.374s (idle: 66.088s)
 * 	getSoundFragments: 0.948s (idle: 0.675s)
 * </pre>
 * @author Robert Fromont robert@fromont.net.nz
 */

@ProgramDescription("A utility for load-testing LaBB-CAT servers.")
public class LoadTester extends CommandLineProgram {
   
   public static void main(String argv[]) {
      LoadTester application = new LoadTester();
      if (application.processArguments(argv)) {
         application.start();
      }
   }

   // Attributes:

   public static DecimalFormat seconds = new DecimalFormat("#,##0.000"); 
   
   /**
    * Base URL for the LaBB-CAT server.
    * @see #getLabbcatUrl()
    * @see #setLabbcatUrl(String)
    */
   protected String labbcatUrl;
   /**
    * Getter for {@link #labbcatUrl}: Base URL for the LaBB-CAT server.
    * @return Base URL for the LaBB-CAT server.
    */
   public String getLabbcatUrl() { return labbcatUrl; }
   /**
    * Setter for {@link #labbcatUrl}: Base URL for the LaBB-CAT server.
    * @param newLabbcatUrl Base URL for the LaBB-CAT server.
    */
   @Switch(value="Base URL for the LaBB-CAT server.",compulsory=true)
   public LoadTester setLabbcatUrl(String newLabbcatUrl) { labbcatUrl = newLabbcatUrl; return this; }

   /**
    * Username for LaBB-CAT.
    * @see #getUsername()
    * @see #setUsername(String)
    */
   protected String username;
   /**
    * Getter for {@link #username}: Username for LaBB-CAT.
    * @return Username for LaBB-CAT.
    */
   public String getUsername() { return username; }
   /**
    * Setter for {@link #username}: Username for LaBB-CAT.
    * @param newUsername Username for LaBB-CAT.
    */
   @Switch("Username for LaBB-CAT.")
   public LoadTester setUsername(String newUsername) { username = newUsername; return this; }

   /**
    * Password for LaBB-CAT.
    * @see #getPassword()
    * @see #setPassword(String)
    */
   protected String password;
   /**
    * Getter for {@link #password}: Password for LaBB-CAT.
    * @return Password for LaBB-CAT.
    */
   public String getPassword() { return password; }
   /**
    * Setter for {@link #password}: Password for LaBB-CAT.
    * @param newPassword Password for LaBB-CAT.
    */
   @Switch("Password for LaBB-CAT.")
   public LoadTester setPassword(String newPassword) { password = newPassword; return this; }
   
   /**
    * Number of clients to simulate.
    * @see #getClients()
    * @see #setClients(Integer)
    */
   protected Integer clients = Integer.valueOf(3);
   /**
    * Getter for {@link #clients}: Number of clients to simulate.
    * @return Number of clients to simulate.
    */
   public Integer getClients() { return clients; }
   /**
    * Setter for {@link #clients}: Number of clients to simulate.
    * @param newClients Number of clients to simulate.
    */
   @Switch("Number of clients to simulate (default is 3).")
   public LoadTester setClients(Integer newClients) { clients = newClients; return this; }   

   /**
    * Number of times each client should repeat the test task.
    * @see #getRepetitions()
    * @see #setRepetitions(Integer)
    */
   protected Integer repetitions = Integer.valueOf(1);
   /**
    * Getter for {@link #repetitions}: Number of times each client should repeat the test task.
    * @return Number of times each client should repeat the test task.
    */
   public Integer getRepetitions() { return repetitions; }
   /**
    * Setter for {@link #repetitions}: Number of times each client should repeat the test task.
    * @param newRepetitions Number of times each client should repeat the test task.
    */
   @Switch("Number of times each client should repeat the test task (default is 1).")
   public LoadTester setRepetitions(Integer newRepetitions) { repetitions = newRepetitions; return this; }

   /**
    * Number of seconds to wait before starting each new client.
    * @see #getClientDelay()
    * @see #setClientDelay(Integer)
    */
   protected Integer clientDelay = Integer.valueOf(5);
   /**
    * Getter for {@link #clientDelay}: Number of seconds to wait before starting each new client.
    * @return Number of seconds to wait before starting each new client.
    */
   public Integer getClientDelay() { return clientDelay; }
   /**
    * Setter for {@link #clientDelay}: Number of seconds to wait before starting each new client.
    * @param newClientDelay Number of seconds to wait before starting each new client.
    */
   @Switch("Number of seconds to wait before starting each new client (default is 5).")
   public LoadTester setClientDelay(Integer newClientDelay) { clientDelay = newClientDelay; return this; }

   /**
    * The orthography to search for when running searches.
    * @see #getSearchFor()
    * @see #setSearchFor(String)
    */
   protected String searchFor = "i";
   /**
    * Getter for {@link #searchFor}: The orthography to search for when running searches.
    * @return The orthography to search for when running searches.
    */
   public String getSearchFor() { return searchFor; }
   /**
    * Setter for {@link #searchFor}: The orthography to search for when running searches.
    * @param newSearchFor The orthography to search for when running searches.
    */
   @Switch("The orthography to search for when running searches (default is \"i\").")
   public LoadTester setSearchFor(String newSearchFor) { searchFor = newSearchFor; return this; }

   /**
    * ID of other layer (apart from orthography) to get annotations from (default is "phonemes").
    * @see #getOtherLayer()
    * @see #setOtherLayer(String)
    */
   protected String otherLayer = "phonemes";
   /**
    * Getter for {@link #otherLayer}: ID of other layer (apart from orthography) to get
    * annotations from (default is "phonemes"). 
    * @return ID of other layer (apart from orthography) to get annotations from (default
    * is "phonemes"). 
    */
   public String getOtherLayer() { return otherLayer; }
   /**
    * Setter for {@link #otherLayer}: ID of other layer (apart from orthography) to get
    * annotations from (default is "phonemes"). 
    * @param newOtherLayer ID of other layer (apart from orthography) to get annotations
    * from (default is "phonemes"). 
    */
   @Switch("ID of other layer (apart from orthography) to get annotations from (default is \"phonemes\").")
   public LoadTester setOtherLayer(String newOtherLayer) { otherLayer = newOtherLayer; return this; }
   
   /**
    * The maximum number of matches to process (for annotations and fragments).
    * @see #getMaxMatches()
    * @see #setMaxMatches(Integer)
    */
   protected Integer maxMatches = Integer.valueOf(200);
   /**
    * Getter for {@link #maxMatches}: The maximum number of matches to process (for
    * annotations and fragments). 
    * @return The maximum number of matches to process (for annotations and fragments).
    */
   public Integer getMaxMatches() { return maxMatches; }
   /**
    * Setter for {@link #maxMatches}: The maximum number of matches to process (for
    * annotations and fragments). 
    * @param newMaxMatches The maximum number of matches to process (for annotations and
    * fragments). 
    */
   @Switch("The maximum number of matches to process (default is 200).")
   public LoadTester setMaxMatches(Integer newMaxMatches) { maxMatches = newMaxMatches; return this; }
   
   /**
    * Call getMatchAnnotations as part of the test.
    * @see #getMatchAnnotations()
    * @see #setMatchAnnotations(Boolean)
    */
   protected Boolean matchAnnotations = Boolean.TRUE;
   /**
    * Getter for {@link #matchAnnotations}: Call getMatchAnnotations as part of the test.
    * @return Call getMatchAnnotations as part of the test.
    */
   public Boolean getMatchAnnotations() { return matchAnnotations; }
   /**
    * Setter for {@link #matchAnnotations}: Call getMatchAnnotations as part of the test.
    * @param newMatchAnnotations Call getMatchAnnotations as part of the test.
    */
   @Switch("Call getMatchAnnotations as part of the test (default is TRUE).")
   public LoadTester setMatchAnnotations(Boolean newMatchAnnotations) { matchAnnotations = newMatchAnnotations; return this; }

   /**
    * Call getFragments as part of the test.
    * @see #getFragments()
    * @see #setFragments(Boolean)
    */
   protected Boolean fragments = Boolean.TRUE;
   /**
    * Getter for {@link #fragments}: Call getFragments as part of the test.
    * @return Call getFragments as part of the test.
    */
   public Boolean getFragments() { return fragments; }
   /**
    * Setter for {@link #fragments}: Call getFragments as part of the test.
    * @param newFragments Call getFragments as part of the test.
    */
   @Switch("Call getFragments as part of the test (default is TRUE).")
   public LoadTester setFragments(Boolean newFragments) { fragments = newFragments; return this; }

   /**
    * Call getSoundFragments as part of the test.
    * @see #getSoundFragments()
    * @see #setSoundFragments(Boolean)
    */
   protected Boolean soundFragments = Boolean.TRUE;
   /**
    * Getter for {@link #soundFragments}: Call getSoundFragments as part of the test.
    * @return Call getSoundFragments as part of the test.
    */
   public Boolean getSoundFragments() { return soundFragments; }
   /**
    * Setter for {@link #soundFragments}: Call getSoundFragments as part of the test.
    * @param newSoundFragments Call getSoundFragments as part of the test.
    */
   @Switch("Call getSoundFragments as part of the test (default is TRUE).")
   public LoadTester setSoundFragments(Boolean newSoundFragments) { soundFragments = newSoundFragments; return this; }

   /**
    * Whether to produce verbose logging.
    * @see #getVerbose()
    * @see #setVerbose(Boolean)
    */
   protected Boolean verbose = Boolean.FALSE;
   /**
    * Getter for {@link #verbose}: Whether to produce verbose logging.
    * @return Whether to produce verbose logging.
    */
   public Boolean getVerbose() { return verbose; }
   /**
    * Setter for {@link #verbose}: Whether to produce verbose logging.
    * @param newVerbose Whether to produce verbose logging.
    */
   @Switch("Whether to produce verbose logging.")
   public LoadTester setVerbose(Boolean newVerbose) { verbose = newVerbose; return this; }

   // Methods:
   
   /**
    * Default constructor.
    */
   public LoadTester() {
   } // end of constructor

   /** Start the utility */
   public void start() {

      // run one client for comparison with load conditions
      Client idleClient = new Client(0, 1);
      System.out.print("Getting statistics when idle...");
      idleClient.run();
      if (!verbose) System.out.println();
      System.out.println(
         "Match count from searching for \""+searchFor+"\": " + idleClient.matchCount);
      System.out.println("Idle conditions:");
      // now get times
      LinkedHashMap<String,Long> idleTotals = idleClient.timers.getTotals();
      for (String k : idleTotals.keySet()) {
         double total = idleTotals.get(k);
         double totalSeconds = total / 1000;
         System.out.println("\t" + k + ": " + seconds.format(totalSeconds) +"s");
      } // next key

      if (clients > 1 || repetitions > 1) {

         System.out.print(
            "Simulating " + clients + " clients doing " + repetitions
            + " search" +(repetitions==1?"":"es")+ " each...");

         // load conditions...
         
         // run lots of clients at once
         Vector<Client> clientThreads = new Vector<Client>();
         
         ExecutorService clientExecutor = Executors.newFixedThreadPool(clients);
         for (int c = 1; c <= clients; c++) {
            // start a client
            Client client = new Client(c, repetitions);
            clientThreads.add(client);
            clientExecutor.execute(client);
            // wait before looping
            try { Thread.sleep(clientDelay * 1000); } catch(Exception exception) {}
         } // next client

         // wait for them all to finish
         clientExecutor.shutdown();
         try {
            if (!clientExecutor.awaitTermination(10, TimeUnit.MINUTES)) {
               System.err.println("TIMEOUT: Not all clients have terminated");
            }
         } catch(Exception exception) {
            System.err.println("ERROR while awaiting termination: " + exception);
            exception.printStackTrace(System.err);
         }

         // total up timers
         LinkedHashMap<String,Long> totals = new LinkedHashMap<String,Long>();
         for (Client client : clientThreads) {
            for (String k : client.timers.getTotals().keySet()) {
               if (!totals.containsKey(k)) {
                  totals.put(k, client.timers.getTotals().get(k));
               } else {
                  totals.put(k, totals.get(k) + client.timers.getTotals().get(k));
               }                  
            } // next key
         } // next client
         
         // now get means
         if (!verbose) System.out.println();
         System.out.println("Load conditions:");
         double divisor = clients * repetitions;
         for (String k : totals.keySet()) {
            double total = totals.get(k);
            double mean = total / divisor;
            double meanSeconds = mean / 1000;
            System.out.println(
               "\t" + k + ": " + seconds.format(meanSeconds) +"s"
               +" (idle: " +(((double)idleTotals.get(k))/1000)+ "s)");
         } // next key
         
      } // load conditions
         
   } // end of start()
   
   class Client implements Runnable {
      Timers timers = new Timers();
      int c;
      int matchCount = 0;
      int repetitions = 0;
      public Client(int c, int repetitions) {
         this.c = c;
         this.repetitions = repetitions;
      }
      
      public void run() {
         if (verbose) System.out.println("Starting client "+c+"...");
         try {
            LabbcatAdmin labbcat = new LabbcatAdmin(labbcatUrl, username, password);
            //labbcat.setVerbose(verbose);
            JsonObject pattern = new PatternBuilder().addMatchLayer("orthography", searchFor).build();
            for (int r = 0; r < repetitions; r++) {
               if (verbose) System.out.println("Client "+c+" Repetition: " + r);
               if (!verbose) System.out.print(".");

               timers.start("search");
               TaskStatus searchTask = labbcat.waitForTask(
                  labbcat.search(pattern, null, null, false, null, null, 5), 0);
               timers.end("search");
               if (!verbose) System.out.print(".");

               try {
                  timers.start("getMatches");
                  Match[] matches = labbcat.getMatches(searchTask.getThreadId(), 5);
                  timers.end("getMatches");
                  matchCount = matches.length;
                  if (!verbose) System.out.print(".");
                  if (verbose) System.out.println("Client "+c+" "+matches.length+" matches returned.");
                  if (matches.length > maxMatches) {
                     if (verbose) System.out.println("Matches list truncated to " + maxMatches);
                     matches = Arrays.copyOf(matches, maxMatches);
                  }

                  String[] layerIds = { "orthography", otherLayer };

                  if (matches.length == 0) {
                     System.err.println(
                        "Client "+c+" No matches were returned, cannot call getMatchAnnotations");
                  } else {
                     if (matchAnnotations) {
                        timers.start("getMatchAnnotations");
                        Annotation[][] annotations = labbcat.getMatchAnnotations(matches, layerIds, 0, 1);
                        timers.end("getMatchAnnotations");
                        if (verbose) System.out.println("Client "+c+" "+annotations.length+" annotations returned.");
                        if (!verbose) System.out.print(".");
                     } else {
                        if (verbose) System.out.println("Skipping getMatchAnnotations.");
                     }

                     if (fragments) {
                        timers.start("getFragments");
                        File[] files = labbcat.getFragments(
                           matches, layerIds, "text/praat-textgrid", null);
                        timers.end("getFragments");
                        if (verbose) System.out.println("Client "+c+" "+files.length+" TextGrids returned.");
                        if (!verbose) System.out.print(".");
                        for (File file : files) if (file != null) file.delete();
                     } else {
                        if (verbose) System.out.println("Skipping getFragments.");
                     }

                     if (soundFragments) {
                        timers.start("getSoundFragments");
                        File[] files = labbcat.getSoundFragments(matches, 16000, null);
                        timers.end("getSoundFragments");
                        if (verbose) System.out.println("Client "+c+" "+files.length+" recordings returned.");
                        if (!verbose) System.out.print(".");
                        for (File file : files) if (file != null) file.delete();
                     } else {
                        if (verbose) System.out.println("Skipping getSoundFragments.");
                     }
                     
                  }
               } finally {
                  try {
                     labbcat.releaseTask(searchTask.getThreadId());
                  } catch(Exception exception) {
                     System.err.println(
                        "Client "+c+" ERROR could not release search task: " + exception);
                  }
               }
               
               // wait before looping
               try { Thread.sleep(clientDelay * 1000); } catch(Exception exception) {}
            } // next repetition
            
         } catch(Exception exception) {
            System.err.println("Client "+c+" ERROR: " + exception);
            exception.printStackTrace(System.err);
         }
         
         if (verbose) {
            System.out.println("Client "+c+":\n"+timers.toString());
         }
      }      
   }

} // end of class LoadTester

