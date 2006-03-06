#!/bin/sh
 
CLASSPATH=`echo lib/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=application/synchron/classes:classes/:$CLASSPATH

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
 
$JAVA_HOME/bin/java -classpath $CLASSPATH -Xms500m -Xmx500m  -Dconfig=./config/Properties.pro  uk.ac.ebi.intact.application.synchron.XmlDumper $1

# end
#!/usr/local/bin/tcsh 


#setenv CLASSPATH ./classes:./application/synchron/classes:./lib/castor-0.9.3.9-xml.jar:./lib/castor-0.9.3.9.jar:./lib/jdbc-se2.0.jar:./lib/jdbc_oracle8i_thin_8.1.6.2.0.jar:./lib/jta1.0.1.jar:./config:./lib/jakarta-ojb-0.9.7.jar:./lib/log4j-1.2.5.jar:./lib/commons-pool.jar:./lib/commons-collections-2.0.jar:./lib/commons-lang-1.0.jar:./lib/jdori.jar:./lib/jmxri.jar:./lib/jta-spec1_0_1.jar:./lib/optional.jar:./lib/p6spy.jar:./lib/ftp.jar

#$JAVA_HOME/bin/java -Xms500m -Xmx500m  -Dconfig=./config/Properties.pro  uk.ac.ebi.intact.application.synchron.XmlDumper $1
