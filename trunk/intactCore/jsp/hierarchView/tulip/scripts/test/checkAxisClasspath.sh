#!/bin/bash

#
# test if every needed Axis jar is in the current CLASSPATH
# return 0 if everything is fine, else 1.
#

 
# list of jar filename you want to check
JARS="axis.jar commons-logging.jar tt-bytecode.jar wsdl4j.jar jaxrpc.jar xercesImpl.jar"

msg=""

for file in $JARS
do
        result=`echo $CLASSPATH | grep $file`
 
        if [ -z "$result" ]
        then
              msg="$msg $file"
        fi
done

if [ -z "$msg" ]
then
     exit 0
else
     echo "$msg seems to be missing in your CLASSPATH"
     exit 1
fi
