# labbcat-java

Client library for communicating with [LaBB-CAT](https://labbcat.canterbury.ac.nz/)
servers using Java.

LaBB-CAT is a web-based linguistic annotation store that stores audio or video
recordings, text transcripts, and other annotations.

Annotations of various types can be automatically generated or manually added.

LaBB-CAT servers are usually password-protected linguistic corpora, and can be
accessed manually via a web browser, or programmatically using a client library like
this one.

**Detailed documentation is available at https://nzilbb.github.io/labbcat-java/**

## Dependencies

- [nzilbb.ag.jar](https://github.com/nzilbb/ag)

## Build targets

- `mvn package` - builds target/nzilbb.labbcat-n.n.n.jar
- `mvn test` - runs unit tests, which requires a running LaBB-CAT server to work; you
   must set the URL/credentials in the unit test files in nzilbb/labbcat/test/ 
- `mvn site` - produces JavaDoc API documentation.

## Usage

The following example shows how to:
1. get some basic corpus structure information,
1. upload a transcript,
1. wait for annotation layer generation to finish,
1. extract the newly-generated annotations, and
1. search for instances of the word "and".
    
```Java
// create LaBB-CAT client
LabbcatView labbcat = new LabbcatView("https://labbcat.canterbury.ac.nz", "demo", "demo");

// get a corpus ID
String[] corpora = labbcat.getCorpusIds();
String corpus = ids[0];

// get a transcript type
Layer typeLayer = labbcat.getLayer("transcript_type");
String transcriptType = typeLayer.getValidLabels().keySet().iterator().next();

// upload a transcript
File transcript = new File("/some/transcript.txt");
String taskId = labbcat.newTranscript(transcript, null, null, transcriptType, corpus, "test");

// wait until all automatic annotations have been generated
TaskStatus layerGenerationTask = labbcat.waitForTask(taskId, 30);

// get all the POS annotations
Annotation[] pos = labbcat.getAnnotations(transcript.getName(), "pos");

// search for tokens of "and"
Matches[] matches = labbcat.getMatches(
    labbcat.search(
       new PatternBuilder().addMatchLayer("orthography", "and").build(),
       participantIds, null, true, false, null), 1);
```

## Developers

### Prerequisites

* The JDK for at least Java 8
  ```
  sudo apt install default-jdk
  ```
* Maven
  ```
  sudo apt install maven
  ```

### Build nzilbb.labbcat.jar

```
mvn package
```

### Run all unit tests

```
mvn test
```

## Build documentation site

```
mvn site
```

## Deploying to OSSRH

OSSRH is the central Maven repository where nzilbb.ag modules are deployed (published).

There are two type of deployment:

- *snapshot*: a transient deployment that can be updated during development/testing
- *release*: an official published version that cannot be changed once it's deployed

A *snapshot* deployment is done when the module version (`version` tag in pom.xml) ends with
`-SNAPSHOT`. Otherwise, any deployment is a *release*.

### Snapshot Deployment

To perform a snapshot deployment:

1. Ensure the `version` in pom.xml *is* suffixed with `-SNAPSHOT`
2. Execute the command:  
   ```
   mvn clean deploy
   ```

### Release Deployment

To perform a release deployment:

1. Ensure the `version` in pom.xml *isn't* suffixed with `-SNAPSHOT` e.g. use something
   like the following command from within the ag directory:  
   ```
   mvn versions:set -DnewVersion=1.1.0
   ```
2. Execute the command:  
   ```
   mvn clean deploy -P release
   ```
3. Happy with everything? Complete the release with:
   ```
   mvn nexus-staging:release -P release
   ```
   Otherwise:
   ```
   mvn nexus-staging:drop -P release
   ```
   ...and start again.
4. Regenerate the citation file:
   ```
   mvn cff:create
   ```
5. Commit/push all changes and create a release in GitHub
