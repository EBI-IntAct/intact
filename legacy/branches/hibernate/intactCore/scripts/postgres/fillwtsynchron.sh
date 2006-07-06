#!/bin/sh
echo "Usage: fillwtsynchron.sh nameOfYourNode ownerPrefix "

echo
#echo You need to create a db with:
#echo initdb -D /scratch/$USER
#echo postmaster -i -S -D /scratch/$USER
#echo createdb $USER
#echo

# wait until server has properly started up
sleep 2

psql -f sql/postgres/drop_tables.sql
psql -f sql/postgres/create_tables.sql
#psql -f sql/postgres/create_dummy.sql


echo
echo create the institution EBI
psql -c "INSERT INTO Institution (shortLabel, fullName, postalAddress, url, ac)
    VALUES ( 'EBI',
	      'European Bioinformatics Institute',
	      'European Bioinformatics Institute\n' ||
	      'Wellcome Trust Genome Campus\n' ||
              'Hinxton, Cambridge CB10 1SD\n' ||
             'United Kingdom',
	      'http://www.ebi.ac.uk',
	      'EBI-' || nextval('Intact_ac'));"


# EBI node
echo
echo create the node for EBI
psql -c "INSERT INTO intactnode ( ac, ownerprefix, ftplogin, ftppassword, ftpaddress, ftpdirectory, owner_ac) SELECT 'EBI-' || nextval('Intact_ac'), 'EBI',
 'anonymous', '', 'ftp.ebi.ac.uk', 'pub/contrib/swissprot/tmp',ac FROM Institution WHERE shortLabel='EBI';"

# local node
echo create a dummy institution for the local node
psql -c "INSERT INTO Institution (shortLabel, fullName, ac) VALUES ('"$2"' , '"$1"', 'EBI-dummyInstitution');"

echo
echo create intactnode
psql -c "INSERT INTO intactnode ( ac, ownerprefix, owner_ac) SELECT 'EBI-dummyNode', '"$2"', ac FROM Institution WHERE shortLabel='EBI';"

echo
echo "Collect data ..."
scripts/postgres/Collector.csh

#end
