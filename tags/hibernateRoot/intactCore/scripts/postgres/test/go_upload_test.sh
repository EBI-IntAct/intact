#!/bin/sh

###############################################################################
# This script tests the upload functionality of GoTools. It does this by:
#
# 1. Initialize the database with go_initdb.sh. This will take care of
#    inserting data to the database.
# 2. Run the GoTools test (this will test the database contents using the
#    new data).
#
# Following parameters are required:
# user: the user to access the database (assumes passwords are empty)
# database: the name of the database
# root: where to upload from; defaults to data/controlledVocab if none given
#
# Author: Sugath Mudali
# Version: $Id$
###############################################################################

if [ $# == 2 ]
then
   ROOT="data/controlledVocab"
else if [ $# != 3 ]
   then
      echo "usage: `basename $0 .sh` user database (root)"
      exit 1
   fi
fi

######################################################################################
# Upload here
######################################################################################
./scripts/postgres/test/go_initdb.sh $1 $2 $ROOT


######################################################################################
# Run the test to verify the new inserting
######################################################################################

ant -Dtest.package=uk.ac.ebi.intact.util.go test-package-stdout
