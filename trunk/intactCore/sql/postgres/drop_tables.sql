/**************************************************************************************************************************

  Package:    IntAct PostgreSQL DDL step 0 

  Purpose:    Optional : drop components for Intact in a Postgres database, using dynamic SQL. Developed on 7.3.2 for Unix.

  Usage:      - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Language PLpgSQL must be activated in your postgresql database to run this.
                You can do this through the next Unix command, supposing your database name is intact :
                     createlang plpgsql intact
              - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                
              - with psql, connect to Postgres as intact (or the different account you created)

              - suppose this script resides in /tmp , then give this command in psql :    
                     \i /tmp/drop_tables.sql 
                     

  $Date$
  $Auth: markr@ebi.ac.uk $

  Copyright (c) 2003 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.


  **************************************************************************************************************************/

-- make it : 

CREATE OR REPLACE FUNCTION drop_tables () RETURNS varchar AS '
    DECLARE
       tableNames    RECORD; 
       triggerNames  RECORD; 
       ddl_command varchar(20000);
    
    BEGIN
       -- Loop over all the tables that are part of the IntAct system. Criterium : tableowner=USER
       ddl_command := '' '';
       FOR tableNames IN  select tablename from  pg_tables where  tableowner = user and tablename not like ''pg_%''  
       LOOP
          -- 7.3.2 : ddl_command := ''DROP TABLE ''|| tableNames.tablename ||'' CASCADE ; \n'';
          ddl_command := ''DROP TABLE ''|| tableNames.tablename ||'' ; \n'';
          EXECUTE ddl_command;

       END LOOP;

       FOR triggerNames IN  select tgname from  pg_trigger where  tgname like ''intact_%''  
       LOOP
           ddl_command := ''DROP TRIGGER ''|| triggerNames.tgname ||''; \n'';
          EXECUTE ddl_command;

       END LOOP;

       RETURN ''Components were dropped succesfully'';

    END;
' LANGUAGE 'plpgsql';



-- run it : 

select drop_tables();
