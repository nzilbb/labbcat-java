# 1.1.0

- Function changes
  + maxOrdinal parameter of StoreQuery.countAnnotations
  + maxOrdinal parameter of StoreQuery.getAnnotations

# 1.0.0

- Implementations
  + LabbcatView.getSchema()
  + LabbcatView.getTranscript()
  + LabbcatEdit.deleteParticipant()
  + LabbcatAdmin user CRUD functions
  + LabbcatAdmin.setPassword()
- Function changes
  + layerIds parameter of StoreQuery.getParticipant
  + overlapThreshold parameter for search() and getMatches().
- New functions
  + LabbcatView.getMediaFile() convenience function
- Maven-compatible versioning/package structure
- Documentation is CC-BY-SA

# 20200909.2039

- Uses javax.json.* instead of org.json.*

# 20200826.1513

- New LabbcatAdmin CRUD operations for
  + corpora
  + projects
  + roles
  + permissions
  + system attributes (RU operations only)
  + tracks
  + *saveLayer* (CU operations)
- New LabbcatView functions
  + *getSerializerDescriptors*
  + *getDeserializerDescriptors*
  + *getSystemAttribute*
  + *getUserInfo*
- Support for localization of server messages

# 20200612.1635

Refactored to remove confusing terminology and use class names that reflect LaBB-CAT user
authorization level.

# 20200611.2005

Requests include specify user-agent that identifies the library/version.

# 20200608.1944

- support for LaBB-CAT version 20200608.1507 API
- Labbcat.getTranscriptAttributes
- Labbcat.getParticipantAttributes

