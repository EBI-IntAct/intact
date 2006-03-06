CREATE OR REPLACE TRIGGER trgInteractor_propagate
   AFTER UPDATE
   ON IA_INTERACTOR
   FOR EACH ROW
BEGIN
    /*in case of INSERTING there cannot be already an record in IA_INT2EXP
       .. because of foreign key constraint with Cascade from IA_INT2EXP to IA_INTERACTOR
      AND ac is primary key in IA_INTERACTOR, so there cannot be already a record
      with this value
      * In case of Interactor
        a) there will be no entry in INT2EXP
        b) no update see point 18, should otherwise have gone to IA_COMPONENT
      */
   IF UPDATING THEN /* If it is an protein, than it will not be in the INT2EXP */
      UPDATE IA_INT2EXP
      SET UPDATED = SYSDATE
      WHERE INTERACTION_AC= :OLD.ac
      OR      INTERACTION_AC =:NEW.ac;
   END IF ;
   -- IN CASE OF DELETING, ALREADY CASCADE VIA FOREIGN KEY
END;
/
