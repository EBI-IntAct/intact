#!/bin/sh

#
# PSICQUIC Client for Unix shell
# ------------------------------
#
# usage: psicquic-client.sh <intput_file> <output_file>
#
# intput_file: set a single MIQL query per line
#
#

if [ $# -ne 2 ]; then
   echo ""
   echo "ERROR: wrong number of parameters."
   echo "usage: $0 <intput_file> <output_file>"
   echo ""
   exit 1
fi

PAGE_SIZE=200

INPUT_FILE=$1
OUTPUT_FILE=$2

echo "reading data from $INPUT_FILE"
echo "results will be saved in $OUTPUT_FILE"


downloadChunk()
{
  QUERY=$1
  FROM=$2
  SIZE=$3
  FILE=$4

  wget -q -O $FILE "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/${QUERY}?firstResult=${FROM}&maxResults=${SIZE}"

  line_count=`wc -l $FILE | awk {'print $1'}`
  let TO=$FROM+line_count
  echo "$FROM..$TO"
}


rm -f $OUTPUT_FILE
touch $OUTPUT_FILE


for line in `cat $INPUT_FILE`
do

  echo "Processing $line..."

  line_count_before=0
  line_count_after=1
  from=0
  delta=$PAGE_SIZE

  while [ "$delta" -eq "$PAGE_SIZE" ]; do

      if test -f $OUTPUT_FILE
      then
          line_count_before=`wc -l $OUTPUT_FILE | awk {'print $1'} `
      fi

      eval "downloadChunk $line $from $PAGE_SIZE tmp"

      eval "cat tmp >> $OUTPUT_FILE"

      let from=from+$PAGE_SIZE

      line_count_after=`wc -l $OUTPUT_FILE | awk {'print $1'} `

      let delta=$line_count_after-$line_count_before

  done

  rm tmp

done

echo "end"