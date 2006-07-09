set serveroutput on size 1000000

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;


/* to prevent repeating header */
set pagesize 9999 

/* "set tab on" does not work for outputflies tabs  */

      
spool list_deleted_acs.txt

select shortlabel ||chr(9)||ac||chr(9)|| deleted_date||chr(9)|| deleted_by
from 
(select shortlabel 		
       ,substr(ac,1,15) ac	
       ,type		
       ,to_char(deleted_date,'dd-mon-yyyy') deleted_date /* hh24:mi:ss */
       ,deleted_by
from ia_deleted_acs);

spool off

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;


