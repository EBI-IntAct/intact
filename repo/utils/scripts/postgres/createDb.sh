#!/bin/sh
echo "Usage: testfill.sh user/password database"

# extract username from the user/password parameter
DBUSER=${1%%/*}
# extract database name from database parameter
DATABASE=$2

# Please change that value if your postgres server is running on a another machine.
PG_OPTIONS="-h localhost"

echo
echo "user name     : $DBUSER"
echo "database name : $DATABASE"
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

#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/create_xref_trigger.sql
#if [ $? != 0 ]
#then
#    exit 1
#fi

psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/create_dummy.sql
if [ $? != 0 ]
then
    exit 1
fi


#######################
# Schema update

# version 1.1.0
echo "Upgrading schema to version 1.1.0 ..."
# psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_1_0/02_add_column.sql
psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_1_0/04_drop_column.sql
psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_1_0/05_create_metadata_table.sql

# version 1.7.0
echo "Upgrading schema to version 1.7.0 ..."
psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_1_7/add_indexes.sql

# version 1.2.0
echo "Upgrading schema to version 1.2.0 ..."
psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_2_0/create_tables.sql
#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_2_0/update_privileges.sql

#version 1.2.2
#echo "Upgrading schema to version 1.2.2"
#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_2_2/createXrefSplitProcedure.sql

# version 1.3.0
echo "Upgrading schema to version 1.3.0"
psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_3_0/020_component_modifications.sql
psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_3_0/030_create_alias_deletion_triggers.sql
#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_3_0/040_create_xref_deletion_triggers.sql
#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_3_0/090_grant_permissions.sql
#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_3_0/110_create_procedure_split_alias.sql
#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_3_0/130_create_procedure_split_xref.sql
#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_3_0/150_run_alias_table_split.sql
#psql $PG_OPTIONS -U $DBUSER -d $DATABASE -f sql/postgres/version_1_3_0/160_run_xref_table_split.sql
