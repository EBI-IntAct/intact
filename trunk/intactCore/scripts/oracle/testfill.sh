#!/bin/sh
echo "Usage: testfill.sh user/pw database small|medium|large"

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
echo "user/pw       : $1"
echo "database name : $2"
echo "data set      : ${DATASET} ${DEFAULT_WARN}"

# wait
sleep 2

cd sql/oracle
sqlplus $1@$2 @create_all.sql
sqlplus $1@$2 @create_dummy.sql
cd ../../

echo "Inserting controlled vocabularies"

scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvTopic data/controlledVocab/CvTopic.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvXrefQualifier data/controlledVocab/CvXrefQualifier.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvDatabase data/controlledVocab/CvDatabase.def

echo "Inserting Proteins and their Xrefs ..."
scripts/javaRun.sh InsertGo data/go_${DATASET}.dat "http://www.geneontology.org/doc/GO.defs"

echo "Inserting Complexes ..."
scripts/javaRun.sh InsertComplex data/ho_gavin_${DATASET}.dat

#end

