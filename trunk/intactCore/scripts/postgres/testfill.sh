#!/bin/sh
echo "Usage: testfill.sh user/pw database small|large"

echo
echo You need to create a db with:
echo initdb -D /scratch/$USER
echo postmaster -i -S -D /scratch/$USER
echo createdb $USER
echo

# wait until server has properly started up
sleep 2

psql -f sql/postgres/drop_tables.sql          
psql -f sql/postgres/create_tables.sql          
psql -f sql/postgres/create_dummy.sql          

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

