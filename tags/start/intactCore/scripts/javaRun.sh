#!/bin/sh
 
CLASSPATH=`echo lib/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=classes/:$CLASSPATH
 
java -classpath $CLASSPATH uk.ac.ebi.intact.util.$1 $2

# end
 
