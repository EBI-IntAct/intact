/**************************************************************************************************************************

  Package:    IntAct PostgreSQL DDL step 2 (run step 1 first)

  Purpose:    Creates the audit_tables for Intact in a Postgres database, using dynamic SQL. Developed on 7.3.2 for Unix.

  Usage:      - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Language PLpgSQL must be activated in your postgresql database to run this.
                You can do this through the next Unix command, supposing your database name is intact :
                     createlang plpgsql intact
              - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                
              - with psql, connect to Postgres as intact (or the different account you created)

              - suppose this script resides in /tmp , then give this command in psql :    
                     \i /tmp/create_audit_tables.sql 
                     
              - to run actual trigger creation, next give this command in psql:
                     select make_audit_tables ();


              - You then have a set of Intact main tables and audit tables,
                which you can verify by typing:
                     \dt


  $Date$
  $Auth: markr@ebi.ac.uk $

  Copyright (c) 2003 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.


  **************************************************************************************************************************/
CREATE OR REPLACE FUNCTION make_audit_tables () RETURNS varchar AS '
    DECLARE
       tableNames  RECORD; 
       colNames    RECORD;
       ddl_command varchar(20000);
       varcharLen  integer;
       decimalLen  integer;
       pkSequence  varchar(255);
       pkCol       varchar(255);
       nextChar    varchar(1);
       keyElem     varchar(255):='''';
       i           integer;
       relNode     integer;
       comma       varchar(1);
    
    BEGIN
       -- Loop over all the tables that are part of the IntAct system. Criterium : tableowner=...
       ddl_command := '' '';
       FOR tableNames IN  select tablename from  pg_tables where  tableowner not like ''pg_%'' and tablename not like ''%audit'' order by tablename 
                      -- actually: selecting on pg_% should be replace by selecting on a known database user account as the tableowner
                      -- in EBI development environment evebrybody's using different owners, hence the select is done using pg_%
       LOOP
           comma := '''';
           -- make a DDL command to create an audit table for each master table
           ddl_command := ''CREATE TABLE ''|| tableNames.tablename ||''_audit (\n'';

           FOR colNames IN
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
          LOOP
              relNode := colnames.relfilenode;
              -- add the column name to ddl
              ddl_command := ddl_command ||''      ''||comma|| colNames.attname;

              -- add the correct datatype per column to ddl
              IF colNames.typname = ''_varchar'' THEN
                  varcharLen := colNames.atttypmod -4 ;  -- postGres peculiarity
                  ddl_command := ddl_command ||'' varchar(''||varcharLen||'')'';
              END IF;

              IF colNames.typname = ''_numeric'' THEN
                  decimalLen := round(colNames.atttypmod/65540); -- postGres peculiarity
                  ddl_command := ddl_command ||'' decimal(''||decimalLen||'')'';
              END IF;

              IF colNames.typname = ''_timestamp'' THEN
                  ddl_command := ddl_command ||'' timestamp'';
              END IF;

              IF colNames.typname = ''_time'' THEN
                  ddl_command := ddl_command ||'' time'';
              END IF;

              IF colNames.typname = ''_date'' THEN
                  ddl_command := ddl_command ||'' date'';
              END IF;

              IF colNames.typname like ''%float%'' THEN
                  ddl_command := ddl_command ||'' float'';
              END IF;
 
              -- add not null constraint per column to ddl             
              IF colNames.attnotnull = ''t'' THEN
                  ddl_command := ddl_command ||'' NOT NULL'';
              END IF;

              ddl_command := ddl_command ||''\n'' ;
              comma := '','';
          END LOOP;
          ddl_command := ddl_command ||'');\n''  ;

          -- Dynamic SQL: issue the DDL to make an audit table
          EXECUTE ddl_command;



          -- Now make a composite pk on the audit table, using the original pk of the master table plus a timestamp
          -- This all requires a bit of string processing, because of the content of pg_constraint.conname

          ddl_command := ''ALTER TABLE ''|| tableNames.tablename ||''_audit  ADD CONSTRAINT pk_''|| tableNames.tablename ||''_audit PRIMARY KEY  ( updated '';

          SELECT conkey INTO pkSequence FROM pg_constraint WHERE conname like ''pk%'' AND conrelid= relNode;

          nextChar:='' '';
          i:=2;
          WHILE nextChar !='''' LOOP

              nextChar := substr(pksequence,i,1);
              IF nextChar in ('','' , ''}'') THEN
          
                 SELECT attname INTO pkCol FROM pg_attribute WHERE attrelid=relNode AND attnum = keyElem;
                 ddl_command  := ddl_command || '','' || pkCol;
                 keyElem := '''';
                 IF nextChar = ''}'' THEN
                      ddl_command  := ddl_command || '');\n'' ;
                 END IF;
              ELSE
                 keyElem := keyElem||nextChar;
              END IF;
              i :=i+1;
          END LOOP;
          -- Dynamic SQL: issue the DDL to make a pk
          EXECUTE ddl_command;

       END LOOP;
       RETURN ''Audit tables created succesfully'';

    END;
' LANGUAGE 'plpgsql';

