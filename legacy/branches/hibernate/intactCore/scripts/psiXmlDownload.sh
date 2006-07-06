#!/bin/sh

# *******************************************************************
#
# Purpose: Generate PSI MI XML files from prepared input file
#
# Usage:
# psiXmlDownload.sh file_prefix 2.5 cv.map< sections_file >& output.log
# *******************************************************************


CV_MAPPING_FILENAME=application/dataConversion/controlledvocab/reverseMapping.txt


#
# TODO comment that method parameters
#
processLine()
{
  # get parameters
  filePrefix=$1
  shift
  psiVersion=$1
  shift
  filename=$1
  shift
  searchPattern=$@

  optionalParam=""
  if [ $psiVersion == "1" ]; then
     optionalParam="-cvMapping $CV_MAPPING_FILENAME"
  fi

  # Run the script
  outputFile=$filePrefix$filename
  echo $outputFile  
  scripts/psiRun.sh FileGenerator -pattern $searchPattern \
                                  -output $outputFile \
                                  -psiVersion $psiVersion \
                                  $optionalParam
}


# the two first params are mandatory
if [ $# -ne 2 && $# -ne 3 ]; then
   echo ""
   echo "ERROR: wrong number of parameters."
   echo "usage: psiXmlDownload.sh <file_prefix> <psiVersion(1, 2, 25)> [mapping.file] < sections_file"
   echo ""
   exit 1
fi

filePrefix=$1
psiVersion=$2

if [ $# -e 3 ]; then
   CV_MAPPING_FILENAME=$3
fi

echo "filePrefix:    $filePrefix"
comment=""
if [ $psiVersion == "1" ]; then
     "( using CV mapping file: $CV_MAPPING_FILENAME )"
fi
echo "PSI version:   $psiVersion $comment"
echo "filename:      $filename"
echo "searchPattern: $searchPattern"

# read line by line the input file
while read line
do
    echo "Processing $line"
    processLine $filePrefix $psiVersion $line
done

echo ""
echo "done."


