CLEAR BUFFER
CLEAR SCREEN

SET SERVEROUT ON
SET DOC OFF
SET VERIFY OFF
SET LINES 150
SET PAGES 20000
SET FEEDBACK OFF

PROMPT
PROMPT  
PROMPT  INTACT schema creation : privileges for Intact
--      author : Mark Rijnbeek, EBI
--      date   : 24/02/2003
PROMPT  -----------------------------------------------------------------------------------------------------------------------------


PROMPT
PROMPT  ! Run this script as the user that owns the Intact components (for example user 'intact')

begin
   for r_usr in (select user from dual) loop
      dbms_output.put_line ('You are currently connected as user : '||r_usr.user );
   end loop;
end;
/
PROMPT
PROMPT  --> To ABORT press CTRL+C.  Press ENTER to continue.
ACCEPT Okay PROMPT >>>




PROMPT
PROMPT This script will spool a creation script to make Intact privileges assigned to the roles, and then run it.
PROMPT 
PROMPT Enter the name of the spool file. For instance /tmp/privs.sql or c:\temp\privs.sql
ACCEPT privilegeSpoolFile PROMPT >>>
PROMPT

exec dbms_output.enable(1000000000000);
set term off

spool &privilegeSpoolFile

begin
   for r_tab in (select 'grant select on '||table_name||' to INTACT_SELECT ;' ddl from user_tables) loop
      dbms_output.put_line (r_tab.ddl);
   end loop;
   
   dbms_output.put_line (chr(10));
   dbms_output.put_line (chr(10));

   for r_tab in (select 'grant select, insert,update,delete on '||table_name||' to INTACT_CURATOR ;' ddl from user_tables) loop
      dbms_output.put_line (r_tab.ddl);
   end loop;


   dbms_output.put_line (chr(10));
   dbms_output.put_line (chr(10));

   for r_seq in (select 'grant select on '||sequence_name||' to INTACT_CURATOR ;' ddl from user_sequences ) loop
      dbms_output.put_line (r_seq.ddl);
   end loop;



end;
/



SPOOL OFF
SET TERM ON
SET FEEDBACK ON;

@&privilegeSpoolFile ;


exit;