#!/bin/sh
echo "Usage: testfill.sh user/pw database small|large"

sqlplus $1@$2 @sql/oracle/drop_tables.sql
sqlplus $1@$2 @sql/oracle/create_tables.sql
sqlplus $1@$2 @sql/oracle/create_dummy.sql


if [ "$3" = "" ]
then
   $3="small"
fi

echo "Inserting Proteins and their Xrefs ..."
scripts/javaRun.sh InsertGo data/go_$3.dat

echo "Inserting Complexes ..."
scripts/javaRun.sh InsertComplex data/ho_gavin_$3.dat

echo "Try querying for Experiment, ac, EBI-17 or EBI-18 now:"
scripts/javaRun.sh IntactCoreApp

#end

