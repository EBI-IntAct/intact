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
PROMPT  INTACT schema creation : public synonyms for Intact
--      author : Mark Rijnbeek, EBI
--      date   : 24/02/2003
PROMPT  -----------------------------------------------------------------------------------------------------------------------------

PROMPT
PROMPT  This script will make public synonyms for the Intact system.
PROMPT  
PROMPT  Run this script as a user with CREATE PUBLIC SYNONYM / ANY SYNONYM privilege. 
PROMPT  This normally requires a DBA account !

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
PROMPT This script will spool a creation script to make public synonyms for Intact components, and then run it.
PROMPT 
PROMPT Enter the name of the spool file. For instance /tmp/syns.sql or c:\temp\syns.sql
ACCEPT synonymSpoolFile PROMPT >>>
PROMPT
PROMPT Type the Oracle username of the IntAct account (normally 'intact', without quotes)
ACCEPT intactAccount PROMPT >>>
PROMPT


exec dbms_output.enable(1000000000000);
set term off

spool &synonymSpoolFile

begin
   for r_tab in (select 'create public synonym '||table_name||' for &intactAccount'||'.'||table_name||';' syn from all_tables where owner=upper('&intactAccount')  ) loop
      dbms_output.put_line (r_tab.syn);
   end loop;

   for r_seq in (select 'create public synonym '||sequence_name||' for &intactAccount'||'.'||sequence_name||';' syn from all_sequences where sequence_owner=upper('&intactAccount')  ) loop
      dbms_output.put_line (r_seq.syn);
   end loop;
 
end;
/



SPOOL OFF
SET TERM ON
SET FEEDBACK ON;

@&synonymSpoolFile ;


--exit;