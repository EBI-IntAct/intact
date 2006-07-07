set doc off
/*
  Copyright (c) 2003 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct core

  Purpose:    Drop all Oracle components for IntAct

  Usage:      is called from 'create_all.sql' 
              individual usage : sqlplus username/password@INSTANCE @drop_tables.sql
              

  $Date$
  $Locker$

  *************************************************************/


-- Tables
PROMPT Dropping tables and sequences..

BEGIN
   IF user in ('SYS' , 'SYSTEM' ) then
      raise_application_error (-20001, 'You are using a DBA account ! Use the intact account. ');
   END IF;
   
   FOR r_tab in (select table_name from user_tables) loop
      EXECUTE IMMEDIATE 'DROP TABLE '||r_tab.table_name||' CASCADE CONSTRAINTS ';
   END LOOP;
   
   FOR r_seq in (select sequence_name from user_sequences) loop
      EXECUTE IMMEDIATE 'DROP SEQUENCE '||r_seq.sequence_name||' ';
   END LOOP;

END;
/

set doc on






