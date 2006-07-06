#!/bin/sh

###############################################################################
# This script tests the download functionality of GoTools. It does this by:
#
# 1. Initialize the database with go_initdb.sh. This will take care of
#    inserting data to the database.
# 2. Download to temp directory (uses TEMP_DIR variable)
# 3. Reinitialize the database again but this time using the downloaded data
# 4. Run the GoTools test (this will test the database contents using the
#    new data).
#
# Following parameters are required:
# user: the user to access the database (assumes passwords are empty)
# database: the name of the database
#
# Author: Sugath Mudali
# Version: $Id$
###############################################################################

# This is where the downloaded files are written to.
TEMP_DIR=/tmp

if [ $# != 2 ]
then
   echo "usage: `basename $0 .sh` user database"
   exit 1
fi

./scripts/postgres/test/go_initdb.sh $1 $2 data/controlledVocab

######################################################################################
# Download starts here
######################################################################################

echo ""
echo ""
echo "Downloading controlled vocabularies to $TEMP_DIR"

echo ""
echo "Download CvTopic"
scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.CvTopic - $TEMP_DIR/CvTopic.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Download CvXrefQualifier"
scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.CvXrefQualifier - \
$TEMP_DIR/CvXrefQualifier.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Download CvAliasType"
scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.CvAliasType - \
$TEMP_DIR/CvAliasType.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Download CvDatabase"
scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.CvDatabase - \
$TEMP_DIR/CvDatabase.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Download CvInteraction"
scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.CvInteraction psi-mi \
$TEMP_DIR/CvInteraction.def $TEMP_DIR/CvInteraction.dag

echo ""
echo "Download CvInteractionType"
scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.CvInteractionType psi-mi \
$TEMP_DIR/CvInteractionType.def $TEMP_DIR/CvInteractionType.dag

echo ""
echo "Download CvFeatureType"
scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.CvFeatureType psi-mi \
$TEMP_DIR/CvFeatureType.def $TEMP_DIR/CvFeatureType.dag

echo ""
echo "Download CvFeatureIdentification"
scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.CvFeatureIdentification psi-mi \
$TEMP_DIR/CvFeatureIdentification.def $TEMP_DIR/CvFeatureIdentification.dag

######################################################################################
# Insert using the downloaded files
######################################################################################

./scripts/postgres/test/go_initdb.sh $1 $2 $TEMP_DIR

######################################################################################
# Run the test to verify the new inserting
######################################################################################

ant -Dtest.package=uk.ac.ebi.intact.util.go test-package-stdout

