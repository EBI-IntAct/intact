#!/bin/sh
#
echo "Usage: insertCv.sh CvName"


if [ $? != 0 ]
then
    echo "usage: insertCv.sh CvName "
    exit 1 # if something went wrong in the previous command line, exit with an error code
fi

scripts/javaRun.sh CvDagObjectTool uk.ac.ebi.intact.model.$1

