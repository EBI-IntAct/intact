#!/bin/sh
echo "Usage: testfill.sh user/password database onlyCV|small|medium|large"


# extract username from the user/password parameter
DBUSER=${1%%/*}

PG_OPTIONS=""

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
       DATAFILE="data/ho_gavin_$3.dat"
       if [ -f $DATAFILE ]
       then
          DATASET=$3
          DEFAULT_WARN=""
       else
          echo "'$3' is not a recognized option, expected values are: onlyCV|small|medium|large."
          echo "abort."
          exit 1
       fi
   fi
fi

# display parameters summary
echo
echo "user name     : $DBUSER"
echo "database name : $2"
echo "data set      : ${DATASET} ${DEFAULT_WARN}"
echo "additional Postgres options: $PG_OPTIONS"

echo
echo You need to create a db with \(\${YOUR_POSTGRES_PATH} is to be replaced by the path of your postgres install\) :
echo initdb -D \${YOUR_POSTGRES_PATH}/$DBUSER             
echo postmaster -i -S -D \${YOUR_POSTGRES_PATH}/$DBUSER
echo createuser --createdb --no-adduser $DBUSER
echo createdb -U $DBUSER $2
echo




# wait until server has properly started up
sleep 2

psql $PG_OPTIONS -U $DBUSER -d $2 -f sql/postgres/drop_tables.sql
if [ $? != 0 ]
then
    exit 1 # if something went wrong in the previous command line, exit with an error code
fi

psql $PG_OPTIONS -U $DBUSER -d $2 -f sql/postgres/create_tables.sql
if [ $? != 0 ]
then
    exit 1
fi

# add plpgsql in case it's not already done
createlang $PG_OPTIONS -U $DBUSER -d $2 plpgsql

psql $PG_OPTIONS -U $DBUSER -d $2 -f sql/postgres/create_xref_trigger.sql
if [ $? != 0 ]
then
    exit 1
fi

psql $PG_OPTIONS -U $DBUSER -d $2 -f sql/postgres/create_dummy.sql
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo ""
echo "Inserting controlled vocabularies"

echo ""
echo "Insert CvTopic"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvTopic - \
                                  data/controlledVocab/CvTopic.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvXrefQualifier"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvXrefQualifier psi-mi \
                                  data/controlledVocab/CvXrefQualifier.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvAliasType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvAliasType psi-mi \
                                  data/controlledVocab/CvAliasType.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvDatabase"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvDatabase psi-mi \
                                  data/controlledVocab/CvDatabase.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvComponentRole"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvComponentRole psi-mi \
                                  data/controlledVocab/CvComponentRole.def
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvIdentification"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvIdentification psi-mi \
                                  data/controlledVocab/CvIdentification.def \
                                  data/controlledVocab/CvIdentification.dag
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvInteraction"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteraction psi-mi \
                                  data/controlledVocab/CvInteraction.def \
                                  data/controlledVocab/CvInteraction.dag
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvInteractionType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteractionType psi-mi \
                                  data/controlledVocab/CvInteractionType.def \
                                  data/controlledVocab/CvInteractionType.dag
if [ $? != 0 ]
then
    exit 1
fi


echo ""
echo "Insert CvFuzzyType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvFuzzyType - \
                                  data/controlledVocab/CvFuzzyType.def
if [ $? != 0 ]
then
    exit 1
fi


echo ""
echo "Insert CvFeatureType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvFeatureType psi-mi \
                                  data/controlledVocab/CvFeatureType.def \
                                  data/controlledVocab/CvFeatureType.dag
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvFeatureIdentification"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvFeatureIdentification psi-mi \
                                  data/controlledVocab/CvFeatureIdentification.def \
                                  data/controlledVocab/CvFeatureIdentification.dag
if [ $? != 0 ]
then
    exit 1
fi

echo ""
echo "Insert CvInteractorType"
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvInteractorType psi-mi \
                                  data/controlledVocab/CvInteractorType.def \
                                  data/controlledVocab/CvInteractorType.dag
if [ $? != 0 ]
then
    exit 1
fi



echo ""
echo "Update CVs using the latest PSI-MI CV definition"
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
    echo "Inserting Proteins ..."
    scripts/javaRun.sh UpdateProteins file:data/yeast_test.sp
    if [ $? != 0 ]
    then
        exit 1
    fi

    echo ""
    echo "Inserting Complexes ..."
    scripts/javaRun.sh InsertComplex -file data/ho_gavin_${DATASET}.dat -taxId 4932 -interactionType aggregation
    if [ $? != 0 ]
    then
        exit 1
    fi
fi

#end
exit 0
