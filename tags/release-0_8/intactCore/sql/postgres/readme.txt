

The scripts in this folder will set up the IntAct components in a Potsgresql database.
All scripts need to run using psql.

>>> More detailed info can be found in each script.


The order in which to run the scripts is as follows:

   script name                  Oracle user to run the script    purpose of script
   ----------------------       -----------------------------    --------------------------------
1  create_tables.sql            the intact account               create main tables 
2  create_audit_tables.sql      the intact account               create audit tables
3  create_audit_triggers.sql    the intact account               create audit triggers
4  create_groups.sql            a dba account                    create roles
6  create_privs.sql             the intact account               grant privileges to the roles


