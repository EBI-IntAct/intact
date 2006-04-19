#!/bin/sh

#NB This script assumes it will be run from the IntactCore directory; all
# the classpath information is relative to that.

CLASSPATH=`echo lib/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=classes/:$CLASSPATH

#if cygwin used (ie on a Windows machine), make sure the paths
#are converted from Unix to run correctly with the windows JVM
cygwin=false;

case "`uname`" in

CYGWIN*) cygwin=true
         echo "running in a Windows JVM (from cygwin).." ;;
*) echo "running in a Unix JVM..." ;;

esac

if $cygwin; then
CLASSPATH=`cygpath --path --windows "$CLASSPATH"`

fi

if [ "$JAVA_HOME" ]; then
    $JAVA_HOME/bin/java -classpath $CLASSPATH uk.ac.ebi.intact.util.$1 $2 $3 $4 $5 $6 $7 $8 $9
else
    echo Please set JAVA_HOME for this script to exceute
fi

# end

