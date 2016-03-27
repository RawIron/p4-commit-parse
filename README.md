## Parse commit logs from Perforce

quick tool hack which allows to parse the output of the `p4 changes -l` command of the  Perforce Client.

extracts commited changes from a depot with
```
p4 changes -l -s submitted -p <host> //depot/projects/services/security/ml...@99,199
```

* each line starts with one of the keywords
* a new record starts with the _Change_ keyword
* data that is the content to a keyword is on the same line or on multiple lines until a line is found which starts with a different keyword

P4 commit records
```
Change 971 on 2013/02/05 by coder1@team

  TASK ID: 3778
  REASON FOR CHANGE: update the new check in file enables the Load Test to work
  IMPLEMENTATION:update admin.put.updateAccountStatus
  TFC:
  RELEASE NOTE: Yes

Change 921 on 2013/02/05 by coder2@team

  TASK ID:3779
  REASON FOR CHANGE:enables the Test to work
  IMPLEMENTATION:update admin.runTest
  TFC:
  RELEASE NOTE: Yes
```

The tool supports reading the commited changes from Perforce via `p4` and from text files (one per depot).
It is also possible to write the commit records to a file while reading them via `p4`.
This is useful if you would like to run the parser multiple times on the same input data.

The result of the parse is list of commited changes encoded as `ArrayList<P4Change>`.
With this it is now easy to write *Reporter* classes which create the required output, for example a *CSV* file.

### Run
Change the settings for your depots in `p4CL.properties`.
Add a new `RANGE` entry with today's date.
Changes committed by users listed in `IGNORE.NAMES` will not be parsed.

#### Package to *jar* and run *jar*
```
mvn package
java -jar parser.jar
```

#### Using Maven
```
# compile the classes first
mvn compile
# run main
mvn exec:java -Dexec.mainClass="parser.Runner"
```

#### Tests
to run all tests
```
mvn test
```

## Do Better

* use Dependency Injection
* coverage
* too many things are hard-coded, not a flexible tool
