#!/bin/sh
echo "Usage: testfill.sh user/password database onlyCV|small|medium|large"


# extract username from the user/password parameter
DBUSER=${1%%/*}

DATABASE=$2

# Please change that value if your postgres server is running on a another machine.
PG_OPTIONS="-h localhost"

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
echo "database name : $DATABASE"
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

psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/drop_tables.sql
if [ $? != 0 ]
then
    exit 1 # if something went wrong in the previous command line, exit with an error code
fi

psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/create_tables.sql
if [ $? != 0 ]
then
    exit 1
fi

# add plpgsql in case it's not already done
createlang $PG_OPTIONS -U $DBUSER -d $DATABASE plpgsql

psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/create_xref_trigger.sql
if [ $? != 0 ]
then
    exit 1
fi

psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/create_dummy.sql
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
