/**************************************************************************************************************************

  Package:    IntAct PostgreSQL DDL step 5 (run steps 1,2,3,4 first)

  Purpose:    Creates the privileges for Intact groups in a Postgres database, using dynamic SQL. Developed on 7.3.2 for Unix.

  Usage:      - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                Language PLpgSQL must be activated in your postgresql database to run this.
                You can do this through the next Unix command, supposing your database name is intact :
                     createlang plpgsql intact
              - !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
               
              - if you use an account that owns the intact tables different than 'intact', replace the occurence of 
                ''intact'' in this script with ''yourusernamehere''


              - with psql, connect to Postgres as intact (or the different account you created)

              - suppose this script resides in /tmp , then give this command in psql :    
                     \i /tmp/create_privs.sql 
                     
                     
              - type /dp to verify you have created the privileges



  $Date$
  $Auth: markr@ebi.ac.uk $

  Copyright (c) 2003 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.


  **************************************************************************************************************************/

CREATE OR REPLACE FUNCTION make_privs () RETURNS varchar AS '
    DECLARE
       tablenames RECORD; -- Declare a generic record to be used in a FOR

       ddl_command varchar(4000);
       comma varchar(1);
        

    BEGIN
       -- Loop over all the tables that are part of the IntAct system. Criterium : tableowner=... 
       FOR tablenames IN  select tablename from pg_tables where tableowner=''intact''  order by tablename
       LOOP
            ddl_command := ''GRANT SELECT ON ''|| tablenames.tablename ||'' TO GROUP intact_select ; '';
            EXECUTE ddl_command;
 
            ddl_command := ''GRANT SELECT,INSERT,UPDATE,DELETE ON ''|| tablenames.tablename ||'' TO GROUP intact_curator ; '';
            EXECUTE ddl_command;
          

       END LOOP;
       RETURN ''Privileges created succesfully'';


    END;
' LANGUAGE 'plpgsql';                                                              

select make_privs (); 