CREATE OR REPLACE TRIGGER TRG_INTERACTOR_BS
BEFORE UPDATE OR DELETE
ON IA_INTERACTOR
DECLARE
BEGIN
   Pck_Mutating_Tables_Interactor.init_array;
END;
/
