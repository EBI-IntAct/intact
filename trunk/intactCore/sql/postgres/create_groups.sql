/**************************************************************************************************************************

  Package:    IntAct PostgreSQL DDL step 4 (run steps 1,2,3 first)

  Purpose:    Creates the groups for Intact in a Postgres database
              Group intact_select will be granted read privileges by the create_privs script.
              Group intact_curator will be granted read/insert/update/delete privileges by the create_privs script.




  Usage:      - this script should be run using a dba account ('admin' or so) to avoid "ERROR:  CREATE GROUP: permission denied "

              - with psql, connect to Postgres as an administrator
   
              - suppose this script resides in /tmp , then give this command in psql :    
                     \i /tmp/create_groups.sql 
                     
              - administrator can add users to one of these newly created groups using :
                     ALTER GROUP intact_select ADD USER <username>  ;   
                     or
                     ALTER GROUP intact_curator ADD USER <username>  ;   
                     

  $Date$
  $Auth: markr@ebi.ac.uk $

  Copyright (c) 2003 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.


  **************************************************************************************************************************/

  create group intact_select;
  create group intact_curator;  
  