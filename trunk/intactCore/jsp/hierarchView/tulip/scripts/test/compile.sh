#!/bin/bash

 
# Root directory of the project : in intactCore
ROOT="../../../../.."



# Check CLASSPATH
echo -n "Look in your CLASSPATH for Axis jar files : "
result=`checkAxisClasspath.sh`

if [ -z "$result" ] 
then 
     echo "OK"
else
     echo "something goes wrong :"
     echo "$result"
fi


# COMPILE the source file
echo "Compile ..."
javac -classpath .:${CLASSPATH}:${ROOT}/classes/ Tulip.java
