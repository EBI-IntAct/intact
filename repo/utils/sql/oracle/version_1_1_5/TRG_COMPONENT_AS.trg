CREATE OR REPLACE TRIGGER TRG_COMPONENT_AS
AFTER INSERT OR UPDATE OR DELETE
ON IA_COMPONENT
DECLARE
BEGIN
    --dbms_output.put_line('in trigger as_interactor ');
   Process_Array_Component;
END;
/
