#!/bin/sh
echo "Usage: testfill.sh user/password database small|medium|large"


# extract username from the user/password parameter
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
echo You need to create a db with \(\${YOUR_POSTGRES_PATH} is to be replaced by the path of your postgres install\) :
echo initdb -D \${YOUR_POSTGRES_PATH}/$DBUSER             
echo postmaster -i -S -D \${YOUR_POSTGRES_PATH}/$DBUSER
echo createuser $DBUSER
echo createdb -U $DBUSER $2
echo

# wait until server has properly started up
sleep 2

psql -U $DBUSER -d $2 -f sql/postgres/drop_tables.sql
psql -U $DBUSER -d $2 -f sql/postgres/create_tables.sql
psql -U $DBUSER -d $2 -f sql/postgres/create_dummy.sql


echo ""
echo ""
echo "Inserting controlled vocabularies"

echo ""
echo "Insert CvTopic"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvTopic data/controlledVocab/CvTopic.def

echo ""
echo "Insert CvXrefQualifier"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvXrefQualifier data/controlledVocab/CvXrefQualifier.def

echo ""
echo "Insert CvAliasType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvAliasType data/controlledVocab/CvAliasType.def

echo ""
echo "Insert CvDatabase"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvDatabase data/controlledVocab/CvDatabase.def

echo ""
echo "Insert CvComponentRole"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvComponentRole data/controlledVocab/CvComponentRole.def

echo ""
echo "Insert CvIdentification"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvIdentification data/controlledVocab/CvIdentification.def data/controlledVocab/CvIdentification.dag

echo ""
echo "Insert CvInteraction"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteraction data/controlledVocab/CvInteraction.def data/controlledVocab/CvInteraction.dag

echo ""
echo "Insert CvInteractionType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteractionType data/controlledVocab/CvInteractionType.def data/controlledVocab/CvInteractionType.dag

echo ""
echo "Inserting Proteins ..."
scripts/javaRun.sh UpdateProteins file:data/yeast_test.sp

echo ""
echo "Inserting Complexes ..."

scripts/javaRun.sh InsertComplex -file data/ho_gavin_${DATASET}.dat -taxId 4932 -interactionType aggregation

#end

