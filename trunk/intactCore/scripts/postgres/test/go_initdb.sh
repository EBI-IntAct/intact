#!/bin/sh

###############################################################################
# This script initializes the database for go testing.
#
# It accepts three parameters:
# user: the user to access the database (assumes passwords are empty)
# database: the name of the database
# root: the root where the data files exist (e.g., data/controlledVocab)
#
# Author: Sugath Mudali
# Version: $Id$
###############################################################################

if [ $# != 3 ]
then
   echo "usage: `basename $0 .sh` user database root"
   exit 1
fi

echo "Warning: This will delete your database and initialise it again!"
sleep 2

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
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvTopic - $3/CvTopic.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvXrefQualifier"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvXrefQualifier - $3/CvXrefQualifier.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvAliasType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvAliasType - $3/CvAliasType.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvDatabase"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvDatabase - $3/CvDatabase.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvInteraction"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteraction psi-mi \
$3/CvInteraction.def $3/CvInteraction.dag

echo ""
echo "Insert CvInteractionType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteractionType psi-mi \
$3/CvInteractionType.def $3/CvInteractionType.dag

echo ""
echo "Insert CvFeatureType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvFeatureType psi-mi \
$3/CvFeatureType.def $3/CvFeatureType.dag

echo ""
echo "Insert CvFeatureIdentification"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvFeatureIdentification \
psi-mi $3/CvFeatureIdentification.def $3/CvFeatureIdentification.dag
