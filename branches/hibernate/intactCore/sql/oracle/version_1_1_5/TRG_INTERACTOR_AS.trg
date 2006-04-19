CREATE OR REPLACE TRIGGER TRG_INTERACTOR_AS
AFTER UPDATE OR DELETE
ON IA_INTERACTOR
DECLARE
BEGIN
	 --dbms_output.put_line('in trigger as_interactor ');
   Process_Array_Interactor;
END;
/
