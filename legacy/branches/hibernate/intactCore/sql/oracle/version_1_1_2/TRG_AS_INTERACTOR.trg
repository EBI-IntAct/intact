CREATE OR REPLACE TRIGGER TRG_AS_INTERACTOR
AFTER UPDATE
ON IA_INTERACTOR
DECLARE
BEGIN
	 --dbms_output.put_line('in trigger as_interactor '); 
   Process_Array;
END;
/
