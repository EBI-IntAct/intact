CREATE OR REPLACE TRIGGER TRG_INTERACTOR_AR
 BEFORE UPDATE /* propagation for inserts of interactors as proteins first as insert in component 
               .. propagation of deletes of interactors is handled via FK cascade delete towards component
               .. which will fire propagation trigger on component */
 ON IA_INTERACTOR
 FOR EACH ROW
 
BEGIN

    /* dbms_output.put_line('Start trigger AR:'); */
    -- only put it on the stack if it is  protein
    IF UPPER(:OLD.OBJCLASS) <> 'UK.AC.EBI.INTACT.MODEL.INTERACTIONIMPL'
   THEN
      if (NVL(:NEW.CRC64,' ')   <>   NVL(:OLD.CRC64,' ')
        )
        OR
         (-- TIME (and perhaps userstamp) HAS CHANGED BUT NOT OTHER ATTRIBUTES
              :NEW.updated <> :OLD.updated
         AND  :NEW.AC   =   :OLD.AC
         AND  NVL(:NEW.DEPRECATED,-1)            =   NVL(:OLD.DEPRECATED,-1)
         AND  :NEW.CREATED                       =   :OLD.CREATED
         AND  NVL(:NEW.KD,-1)                    =   NVL(:OLD.KD,-1)
         AND  NVL(:NEW.CRC64,' ')                =   NVL(:OLD.CRC64,' ')
         AND  NVL(:NEW.FORMOF,' ')               =   NVL(:OLD.FORMOF,' ')
         AND  NVL(:NEW.PROTEINFORM_AC,' ')       =   NVL(:OLD.PROTEINFORM_AC,' ')
         AND  NVL(:NEW.OBJCLASS,' ')             =   NVL(:OLD.OBJCLASS,' ')
         AND  NVL(:NEW.BIOSOURCE_AC,' ')         =   NVL(:OLD.BIOSOURCE_AC,' ')
         AND  NVL(:NEW.INTERACTIONTYPE_AC,' ')   =   NVL(:OLD.INTERACTIONTYPE_AC,' ')
         AND  NVL(:NEW.SHORTLABEL,' ')           =   NVL(:OLD.SHORTLABEL,' ')
         AND  NVL(:NEW.FULLNAME,' ')             =   NVL(:OLD.FULLNAME,' ')
         AND  NVL(:NEW.OWNER_AC,' ')             =   NVL(:OLD.OWNER_AC,' ')
         AND  NVL(:NEW.INTERACTORTYPE_AC,' ')    =   NVL(:OLD.INTERACTORTYPE_AC,' ')
       )
      THEN
          Pck_Mutating_Tables_Interactor.add_id(:OLD.ac);
      END IF; 
   END IF;
    
END;
/
