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
sqlplus $1@$2 @create_privs.sql
cd ../../

echo "Inserting controlled vocabularies"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvTopic data/controlledVocab/CvTopic.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvXrefQualifier data/controlledVocab/CvXrefQualifier.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvDatabase data/controlledVocab/CvDatabase.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvComponentRole data/controlledVocab/CvComponentRole.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvIdentification data/controlledVocab/CvIdentification.def data/controlledVocab/CvIdentification.dag
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteraction data/controlledVocab/CvInteraction.def data/controlledVocab/CvInteraction.dag
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteractionType data/controlledVocab/CvInteractionType.def data/controlledVocab/CvInteractionType.dag

echo "Inserting Proteins ..."
scripts/javaRun.sh UpdateProteins file:data/yeast_test.sp

echo "Inserting Complexes ..."
scripts/javaRun.sh InsertComplex data/ho_gavin_${DATASET}.dat 4932

#end

