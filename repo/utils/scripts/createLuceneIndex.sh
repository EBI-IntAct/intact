#!/bin/sh
#
# Usage:
# createLuceneIndex.sh <targetDirectory>
#

if [ $# -ne 1 ]; then
   echo ""
   echo "ERROR: wrong number of parameters."
   echo "usage: createLuceneIndex.sh <luceneIndexDirectory>"
   echo ""
   exit 1
fi

# to be sure that the Indexer is compiled
ant compile-search

ROOT_CLASSPATH=`echo lib/*.jar | tr ' ' ':'`
SEARCH_CLASSPATH=`echo application/lib/*.jar | tr ' ' ':'`

CLASSPATH=classes/:application/search3/WEB-INF/classes:$ROOT_CLASSPATH:$SEARCH_CLASSPATH:$CLASSPATH

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
    echo "The Lucene index will be created in: $1"
    $JAVA_HOME/bin/java -Xms256m -Xmx1024m -classpath $CLASSPATH uk.ac.ebi.intact.searchengine.lucene.Indexer $1
else
    echo Please set JAVA_HOME for this script to exceute
fi

# end
