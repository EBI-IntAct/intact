#!/bin/sh
 
CLASSPATH=`echo lib/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=classes/:$CLASSPATH

#if cygwin used (ie on a Windows machine), make sure the paths
#are converted from Unix to run correctly with the windows JVM
if [ $CYGWIN ] ; then
CLASSPATH=`cygpath --path --windows "$CLASSPATH"`

fi
 
java -classpath $CLASSPATH uk.ac.ebi.intact.util.$1 $2

# end
 
