# The Editor #

This is the tool used to curate IntAct information.

  * Current phase: Production.

# How to run a local instance #

You can run a local instance of the editor on your machine by following this short guideline:

Before you get started, please make sure you have Java 6 and Maven 3 installed on the computer  you are planning to run the Editor on.

These instruction assume that you are running a Linux system and using bash shell (if not you should be able to adapt it easily):
```
svn co https://intact.googlecode.com/svn/repo/service/trunk/editor editor-standalone
cd editor-standalone
export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx1024m"
mvn clean jetty:run -P standalone -D projectStage=Production
```

This will start up 2 things:
  * A stand alone database (H2) and the data will be stored in the current user's home directory (you should find some file looking like editor-db.**)
  * A web server (Jetty) running on port 9192, the editor should be available at this address: http://localhost:9192/editor (please give the server a couple of minutes to start up and keep an eye on the logs for any unusual error messages)**

To stop the servers (database/web), just press CTRL+C in the terminal where you started it. Please remember that this is a demonstration system and any production grade application should be deployed on appropriate database, web server. The IntAct team is not liable for any data loss resulting of the use of this system.

We have a preliminary [User Manual](http://intact.googlecode.com/svn/wiki/documentation/editor-user_guide.pdf) that you may find useful to get started.

Send you feedback/comments to our dev list: intact-developers@googlegroups.com



# Issues and use cases #

Please, use the "Issues" section to submit the bugs you find or tell us about your use cases. You can use the following shortcuts:

  * [Report a bug](http://code.google.com/p/intact/issues/entry?template=Editor%20bug%20report).
  * [Submit use case](http://code.google.com/p/intact/issues/entry?template=Curator%20use%20case).
  * [General feedback](http://code.google.com/p/intact/issues/entry?template=Editor%20mockup%20feedback)

Or list the existing issues:

  * [Open issues](http://code.google.com/p/intact/issues/list?can=2&q=label:Component-Editor)
  * [All issues](http://code.google.com/p/intact/issues/list?can=1&q=label:Component-Editor)
  * [Reported by you](http://code.google.com/p/intact/issues/list?can=1&q=label:Component-Editor%20reporter:me) (you need to be logged in)

# For Developers #

  * [Editor source code](http://code.google.com/p/intact/source/browse/repo#repo/service/trunk/editor/editor-app)