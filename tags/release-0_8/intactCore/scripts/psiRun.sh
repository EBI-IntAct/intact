#!/bin/sh --debug

CLASSPATH=`echo application/dataConversion/lib/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=application/dataConversion/classes/:$CLASSPATH

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
    $JAVA_HOME/bin/java -Xms128m -Xmx512m -classpath $CLASSPATH uk.ac.ebi.intact.application.dataConversion.$1 $2 $3 $4 $5 $6 $7 $8 $9
else
    echo Please set JAVA_HOME for this script to exceute
fi

# end

