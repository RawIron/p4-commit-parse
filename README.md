## Parse commit logs from Perforce

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
