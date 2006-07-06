#!/bin/sh
#
echo "Usage: insertSearch3.sh user database"


if [ $? != 0 ]
then
    echo "usage: insertSearch3.sh"
    exit 1 # if something went wrong in the previous command line, exit with an error code
fi

for cv in CvIdentification CvInteraction CvInteractionType
do scripts/javaRun.sh CvDagObjectTool uk.ac.ebi.intact.model.$cv
done
