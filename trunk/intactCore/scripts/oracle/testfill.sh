#!/bin/sh
echo "Usage: testfill.sh user/pw database small|large"

cd sql/oracle
sqlplus $1@$2 @create_all.sql
sqlplus $1@$2 @create_dummy.sql
cd ../../

echo "Inserting controlled vocabularies"

scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvTopic data/controlledVocab/CvTopic.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvXrefQualifier data/controlledVocab/CvXrefQualifier.def
scripts/javaRun.sh GoTools upload uk.ac.ebi.intact.model.CvDatabase data/controlledVocab/CvDatabase.def

if [ "$3" = "" ]
then
   $3="small"
fi

echo "Inserting Proteins and their Xrefs ..."
scripts/javaRun.sh InsertGo data/go_$3.dat "http://www.geneontology.org/doc/GO.defs"

echo "Inserting Complexes ..."
scripts/javaRun.sh InsertComplex data/ho_gavin_$3.dat

#end

