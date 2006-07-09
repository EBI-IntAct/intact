CREATE OR REPLACE PROCEDURE Process_Array_Component
IS
   mutating_table EXCEPTION;
   PRAGMA EXCEPTION_INIT(mutating_table, -4091);

   l_id        VARCHAR2(30);
   l_info      VARCHAR2(25);

BEGIN
   Pck_Mutating_Tables_Component.get_id(l_id, l_info);
   WHILE l_id IS NOT NULL
   LOOP
       /* dbms_output.put_line('l_id: ' || l_id); */

       UPDATE IA_INTERACTOR
       SET updated = SYSDATE
       WHERE ac = l_id;

       l_id := null;
      Pck_Mutating_Tables_Component.get_id(l_id, l_info);
   END LOOP;


   Pck_Mutating_Tables_Component.clear_array;

EXCEPTION
   WHEN OTHERS
   THEN
     Pck_Mutating_Tables_Component.clear_array;
     RAISE;
END;
/
