



The scripts in this folder will set up the IntAct components in an Oracle database.
All scripts need to run using SQL*plus.
Please let your DBA create an account called 'intact' with the prilvileges to create tables, indexes etc.


The recommended way to act is this:

   - sqlplus INTACT/password @CREATE_ALL.SQL 

       CREATE_ALL.SQL is the main creation script that runs 4 other scripts. 
       It asks for tablespace choice approval. Please give this some consideration; you can / should change the DEFINES in CREATE_ALL.SQL to meet yor needs. 
       It will then run : 
            - DROP_TABLES.SQL : drops Intact tables to make the script re-runnable
            - CREATE_TABLES.SQL : makes the main tables. 
            - CREATE_AUDIT_TABLES.SQL: makes audit tables that track changes on the main tables
            - CREATE_AUDIT_TRIGGERS.SQL : triggers that feed the audit tables
            - CREATE_PRIVS.SQL : create user priviledges
            
Once this is run you will have the necessary objects.



The other scripts should be run separately and are OPTIONAL :

    - CREATE_ROLES.SQL should be run by a DBA to make roles for the IntAct system
    - CREATE_SYNS.SQL should be run by a DBA to make public synonyms for the IntAct system


    - CREATE_PRIVS is part of the testfill.sh script, but will only work if CREATE_ROLES and CREATE_SYNS
      have been run before.
