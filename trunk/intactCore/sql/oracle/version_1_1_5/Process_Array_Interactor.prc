CREATE OR REPLACE PROCEDURE Process_Array_Interactor
IS
   mutating_table EXCEPTION;
   PRAGMA EXCEPTION_INIT(mutating_table, -4091);

   l_id        VARCHAR2(30);
   l_info      VARCHAR2(25);

BEGIN
   Pck_Mutating_Tables_Interactor.get_id(l_id, l_info);
   WHILE l_id IS NOT NULL
   LOOP
       /* dbms_output.put_line('l_id: ' || l_id); */

       UPDATE IA_INTERACTOR
       SET updated = SYSDATE
       WHERE ac IN
          (SELECT interaction_ac
          FROM IA_COMPONENT
          WHERE interactor_ac = l_id);

       l_id := null;
      Pck_Mutating_Tables_Interactor.get_id(l_id, l_info);
   END LOOP;
   -- dbms_output.put_line('TRIGGER STACK ID: ' || Pck_Aa_Mutating_Tables.Get_Trigger_Stack_Id);

   Pck_Mutating_Tables_Interactor.clear_array;

EXCEPTION
   WHEN OTHERS
   THEN
     Pck_Mutating_Tables_Interactor.clear_array;
     RAISE;
END;
/
