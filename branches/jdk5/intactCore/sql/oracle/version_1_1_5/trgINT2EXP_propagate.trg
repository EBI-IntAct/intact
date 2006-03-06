CREATE OR REPLACE TRIGGER trgINT2EXP_propagate
   AFTER INSERT OR UPDATE OR DELETE
   ON IA_INT2EXP
   FOR EACH ROW
DECLARE

BEGIN
   IF INSERTING THEN
      UPDATE IA_EXPERIMENT
      SET UPDATED = SYSDATE
      WHERE AC= :NEW.experiment_ac;
   ELSIF UPDATING THEN
      UPDATE IA_EXPERIMENT
      SET UPDATED = SYSDATE
      WHERE AC= :OLD.experiment_ac
      OR      AC =:NEW.experiment_ac;
   ELSIF DELETING THEN
      BEGIN
        UPDATE IA_EXPERIMENT
        SET UPDATED = SYSDATE
        WHERE AC= :OLD.experiment_ac;
      EXCEPTION
         WHEN OTHERS THEN
           IF SQLCODE = -04091 /* because of cascade delete in IA_EXPERIMENT   */
           THEN NULL;
           ELSE
               RAISE;
           END IF;
       END;
   END IF ;

END;
/
