CREATE OR REPLACE TRIGGER TRG_COMPONENT_AR
 BEFORE INSERT OR UPDATE OR DELETE
 ON IA_COMPONENT
 FOR EACH ROW

BEGIN
     
   IF INSERTING THEN
      Pck_Mutating_Tables_Component.add_id(:NEW.interaction_ac);
   ELSIF UPDATING THEN
        Pck_Mutating_Tables_Component.add_id(:OLD.interaction_ac);
        IF NVL(:NEW.interaction_ac,' ') <> NVL(:OLD.interaction_ac,' ')
        THEN
            Pck_Mutating_Tables_Component.add_id(:NEW.interaction_ac);
        END IF; 
   ELSIF DELETING THEN
        Pck_Mutating_Tables_Component.add_id(:OLD.interaction_ac);
   END IF ;
   
END;
/
