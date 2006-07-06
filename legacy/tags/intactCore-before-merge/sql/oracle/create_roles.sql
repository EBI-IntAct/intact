CLEAR BUFFER
CLEAR SCREEN

SET SERVEROUT ON
SET DOC OFF
SET VERIFY OFF
SET LINES 150
SET PAGES 20000

PROMPT
PROMPT  
PROMPT  INTACT schema creation: roles for Intact
--      author : Mark Rijnbeek, EBI
--      date   : 24/02/2003
PROMPT  -----------------------------------------------------------------------------------------------------------------------------

PROMPT
PROMPT  This script will make two database roles for the Intact system
PROMPT  
PROMPT  Run this script as a user with CREATE ROLE privilege. 
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

CREATE ROLE INTACT_SELECT;

CREATE ROLE INTACT_CURATOR;

