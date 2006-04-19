CREATE OR REPLACE TRIGGER trgAlias_propagate
   AFTER INSERT OR UPDATE OR DELETE
   ON IA_ALIAS
   FOR EACH ROW
DECLARE

BEGIN
    /* AC are unique over the different tables. Actually only update on table N
      in which ac is presented */
   IF INSERTING THEN
      UPDATE IA_EXPERIMENT
      SET UPDATED = SYSDATE
      WHERE AC= :NEW.parent_ac;

      UPDATE IA_INTERACTOR
      SET UPDATED = SYSDATE
      WHERE AC= :NEW.parent_ac;

      UPDATE IA_FEATURE
      SET UPDATED = SYSDATE
      WHERE AC= :NEW.parent_ac;

   ELSIF UPDATING THEN
      UPDATE IA_EXPERIMENT
      SET UPDATED = SYSDATE
      WHERE AC= :OLD.parent_ac
      OR      AC =:NEW.parent_ac;

      UPDATE IA_INTERACTOR
      SET UPDATED = SYSDATE
      WHERE AC= :OLD.parent_ac
      OR      AC =:NEW.parent_ac;

      UPDATE IA_FEATURE
      SET UPDATED = SYSDATE
      WHERE AC= :OLD.parent_ac
         OR AC =:NEW.parent_ac;

   ELSIF DELETING THEN
      UPDATE IA_EXPERIMENT
      SET UPDATED = SYSDATE
      WHERE AC= :OLD.parent_ac;

      UPDATE IA_INTERACTOR
      SET UPDATED = SYSDATE
      WHERE AC= :OLD.parent_ac;

      UPDATE IA_FEATURE
      SET UPDATED = SYSDATE
      WHERE AC= :OLD.parent_ac;
   END IF;
END;
/
