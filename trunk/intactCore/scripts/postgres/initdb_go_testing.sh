#!/bin/sh

###############################################################################
# This script updates the sptr jar files from the latest jar files.
#
# You must supply the editor war file as the first argument.
# Configurable variables:
# SPTR_DIR - points to where to fetch the jar files from.
# EDITOR_UNPACK - the directory to copy the war file and do the necessary
# updates. The upto date WAR file resides in this directory.
#
# Author: Sugath Mudali, Date: 18/10/2004
###############################################################################

if [ $# != 2 ]
then
   echo "usage: `basename $0 .sh` user database"
   exit 1
fi

# Initialize tables
psql -U $1 -d $2 -f sql/postgres/reset_tables.sql
if [ $? != 0 ]
then
    exit 1 # if something went wrong in the previous command line, exit with an error code
fi

# Needs dummy records to insert basic data
psql -U $1 -d $2 -f sql/postgres/create_dummy.sql
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo ""
echo "Inserting controlled vocabularies"

echo ""
echo "Insert CvTopic"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvTopic - data/controlledVocab/CvTopic.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvXrefQualifier"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvXrefQualifier - data/controlledVocab/CvXrefQualifier.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvDatabase"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvDatabase - data/controlledVocab/CvDatabase.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvInteraction"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteraction psi-mi data/controlledVocab//CvInteraction.def data/controlledVocab/CvInteraction.dag

echo ""
echo "Insert CvInteractionType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteractionType psi-mi data/controlledVocab//CvInteractionType.def data/controlledVocab/CvInteractionType.dag

echo ""
echo "Insert CvFeatureType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvFeatureType psi-mi data/controlledVocab//CvFeatureType.def data/controlledVocab/CvFeatureType.dag

echo ""
echo "Insert CvFeatureIdentification"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvFeatureIdentification psi-mi data/controlledVocab//CvFeatureIdentification.def data/controlledVocab/CvFeatureIdentification.dag

