#!/bin/sh
echo "Usage: testfill.sh user/pw database onlyCV|small|medium|large"

# set default dataset if needed
if [ "$3" = "" ]
then
   DATASET="small"
   DEFAULT_WARN="(default)"
else
   if [ "$3" = "onlyCV" ]
   then
       DATASET="Only insert the Controlled Vocabulary"
   else
       DATASET=$3
       DEFAULT_WARN=""
   fi
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
if [ $? != 0 ]
then
    exit 1 # if something went wrong in the previous command line, exit with an error code
fi

sqlplus $1@$2 @create_dummy.sql
if [ $? != 0 ]
then
    exit 1
fi

sqlplus $1@$2 @create_privs.sql
if [ $? != 0 ]
then
    exit 1
fi
cd ../../


echo ""
echo ""
echo "Inserting controlled vocabularies"
scripts/javaRun.sh controlledVocab.UpdateCVs data/controlledVocab/intact.obo \
                                             data/controlledVocab/CvObject-annotation-update.txt


if [ "$3" = "onlyCV" ]
then
    echo ""
    echo "No data insertion requested."
    echo "Processing finished."
    echo ""
else
    echo ""
    echo "Inserting Complexes ..."
    scripts/javaRun.sh InsertComplex -file data/ho_gavin_${DATASET}.dat -taxId 4932 -interactionType aggregation
    if [ $? != 0 ]
    then
        exit 1
    fi
fi

#end
