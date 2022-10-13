# labbcat-java

Client library for communicating with [LaBB-CAT](https://labbcat.canterbury.ac.nz/)
servers using Java.

LaBB-CAT is a web-based linguistic annotation store that stores audio or video
recordings, text transcripts, and other annotations.

Annotations of various types can be automatically generated or manually added.

LaBB-CAT servers are usually password-protected linguistic corpora, and can be
accessed manually via a web browser, or programmatically using a client library like
this one.

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
