#!/bin/sh
echo "Usage: testfill.sh user/pw database small|medium|large"


# extract username from the user/pw parameter
DBUSER=${1%%/*}

# set default dataset if needed
if [ "$3" = "" ]
then
   DATASET="small"
   DEFAULT_WARN="(default)"
else
   DATASET=$3
   DEFAULT_WARN=""
fi

# display parameters summary
echo
echo "user name     : $DBUSER"
echo "database name : $2"
echo "data set      : ${DATASET} ${DEFAULT_WARN}"

echo
echo You need to create a db with:
echo initdb -D /scratch/$DBUSER
echo postmaster -i -S -D /scratch/$DBUSER
echo createdb $2
echo

# wait until server has properly started up
sleep 2

psql -p 5555 -U $DBUSER -d $2 -f sql/postgres/drop_tables.sql
psql -p 5555 -U $DBUSER -d $2 -f sql/postgres/create_tables.sql
psql -p 5555 -U $DBUSER -d $2 -f sql/postgres/create_dummy.sql

echo "Inserting controlled vocabularies"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvTopic data/controlledVocab/CvTopic.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvXrefQualifier data/controlledVocab/CvXrefQualifier.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvDatabase data/controlledVocab/CvDatabase.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvComponentRole data/controlledVocab/CvComponentRole.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvIdentification data/controlledVocab/CvIdentification.def data/controlledVocab/CvIdentification.dag
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteraction data/controlledVocab/CvInteraction.def data/controlledVocab/CvInteraction.dag

echo "Inserting Proteins and their Xrefs ..."
scripts/javaRun.sh InsertGo data/go_${DATASET}.dat "http://www.geneontology.org/doc/GO.defs"

echo "Inserting Complexes ..."
scripts/javaRun.sh InsertComplex data/ho_gavin_${DATASET}.dat

#end

