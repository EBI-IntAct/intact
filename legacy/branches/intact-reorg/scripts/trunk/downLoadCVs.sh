#!/bin/sh
#
# Purpose:
# Download controlled vocabularies.
# 
# Usage:
# downLoadCVs.sh targetDirectory
#

if [ $# -ne 1 ]; then
   echo ""
   echo "ERROR: wrong number of parameters."
   echo "usage: downLoadCVs.sh <output directory>"
   echo ""
   exit 1
fi


OUTPUT_DIR=$1

# check that the directory exits, if not create it.
if [ -d $OUTPUT_DIR ]
then
        echo "Found directory $OUTPUT_DIR"
else
        echo "Directory $OUTPUT_DIR doesn't exist!"
        echo "Creating it..."
        mkdir $OUTPUT_DIR
        echo "done."
fi

# export CVs into a single OBO file.
scripts/javaRun.sh controlledVocab.DownloadCVs $OUTPUT_DIR/intact.obo

# end

