/**************************************************************************************************************************

  Package:    IntAct PostgreSQL DDL step 3 (run steps 1 and 2 first)

  Purpose:    Creates the triggers for Intact in a Postgres database, using dynamic SQL. Developed on 7.3.2 for Unix.

  Usage:      - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Language PLpgSQL must be activated in your postgresql database to run this.
                You can do this through the next Unix command, supposing your database name is intact :
                     createlang plpgsql intact
              - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                

              - if you use an account that owns the intact tables different than 'intact', replace the occurence of 
                ''intact'' in this script with ''yourusernamehere''


              - with psql, connect to Postgres as intact (or the different account you created)

              - suppose this script resides in /tmp , then give this command in psql :    
                     \i /tmp/create_triggers.sql 
                     
              - you can check for the succesful creation of the triggers by querying pg_trigger and pg_proc system tables.


  $Date: 2004-11-04 11:54:38 +0000 (dj, 04 nov 2004) $
  $Auth: markr@ebi.ac.uk $

  Copyright (c) 2003 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.


  **************************************************************************************************************************/

CREATE OR REPLACE FUNCTION make_triggers () RETURNS varchar AS '
    DECLARE
       tablenames RECORD; -- Declare a generic record to be used in a FOR

       ddl_command varchar(4000);
       comma varchar(1);
        
       cursColnames CURSOR FOR 
                select c.relname
                      ,c.relfilenode
                      ,a.attname
                      ,a.atttypmod
                      ,a.attnotnull 
                      ,t.typname
                from   pg_attribute a    -- system table with the columns
                      ,pg_class     c    -- system table to link between tables and columns
                      ,pg_type      t    -- system table with pg data types
                where  t.typelem = a.atttypid
                and    c.relfilenode = a.attrelid
                and    a.attnum > 0                           -- to avoid system columns
                and    t.typname not in (''point'', ''line'') -- postGres peculiarity
                and    c.relname = tableNames.tablename
                order by c.relname
                        ,a.attnum
       ;
       colnames   RECORD; -- Declare a generic record to be used as cursor target record

    BEGIN
       -- Loop over all the tables that are part of the IntAct system. Criterium : tableowner=... 
       FOR tablenames IN  select tablename from pg_tables where tableowner=''intact'' and tablename not like ''%audit'' order by tablename
       LOOP
            -- 1 : Make a trigger body (i.e. a function) , using each tablename
            ddl_command := 
               ''CREATE OR REPLACE FUNCTION ''|| tablenames.tablename ||''_trg_body () RETURNS TRIGGER AS ''''
                  BEGIN
                      NEW.updated   := now();
                      NEW.userstamp := user;


                      insert into ''|| tablenames.tablename ||''_audit                                                                                                                         
                      ( \n'';
            comma :='' '';

            OPEN  cursColnames;
            FETCH cursColnames into colNames;
            WHILE FOUND LOOP
                 ddl_command := ddl_command ||''                         ''||comma|| colNames.attname||''\n'';
                 comma :='','';
                 FETCH cursColnames into colNames;
            END LOOP; 
            CLOSE cursColNames;

            comma :='' '';
            ddl_command := ddl_command ||''
                      )
                      VALUES
                      (\n'';

            OPEN  cursColnames;
            FETCH cursColnames into colNames;
            WHILE FOUND LOOP
                 ddl_command := ddl_command ||''                         ''||comma||''OLD.''|| colNames.attname||''\n'';
                 comma :='','';
                 FETCH cursColnames into colNames;
            END LOOP; 
            CLOSE cursColNames;
                     
            ddl_command := ddl_command ||''
                      );
                      RETURN NEW;
                   END;
              '''' LANGUAGE ''''plpgsql'''';\n''  
            ;
           

            -- Dynamic SQL: issue the DDL
            EXECUTE ddl_command;



            -- 2: Make the trigger itself
            ddl_command := 
 
              ''CREATE TRIGGER intact_''|| tablenames.tablename ||''audit_trg BEFORE UPDATE ON ''|| tablenames.tablename ||
              '' FOR EACH ROW EXECUTE PROCEDURE ''|| tablenames.tablename ||''_trg_body () ; ''
            ;
            -- Dynamic SQL: issue the DDL
            EXECUTE ddl_command;
           

       END LOOP;
       RETURN ''Triggers created succesfully'';


    END;
' LANGUAGE 'plpgsql';                                                              


-- run it;
select make_triggers ();
