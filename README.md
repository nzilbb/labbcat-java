# labbcat-java

Client library for communicating with LaBB-CAT servers

## Dependencies

- [nzilbb.ag.jar](https://github.com/nzilbb/ag)

## Build targets

- `ant` - builds bin/nzilbb.labbcat.server.jar
- `ant test` - also runs unit tests, which requires a running LaBB-CAT server to work; you
   must set the URL/credentials in the unit test files in nzilbb/labbcat/test/ 
- `ant javadoc` - produces JavaDoc API documentation.

## Documentation

More documentation is available [here](https://nzilbb.github.io/labbcat-java/)