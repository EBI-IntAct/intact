set doc off

/**************************************************************************************************************************

  Package:    IntAct

  Purpose:    Update scripts to go from database version to database version.

  Usage:      sqlplus username/password @update_history.sql


  $Date$

  $Locker$


  **************************************************************************************************************************/

/* From 2004-02-29 to 2004-03-15 */

alter table ia_biosource
add (tissue_ac VARCHAR2(30) CONSTRAINT fk_Biosource$tissue REFERENCES   
IA_ControlledVocab(ac) )
;

alter table ia_biosource add ( celltype_ac VARCHAR2(30) CONSTRAINT
fk_Biosource$celltype REFERENCES IA_ControlledVocab(ac) )
;

/* Taxid is not anymore unique */
alter table ia_payg drop CONSTRAINT FK_PAYG_SPECIES;
alter table ia_biosource drop constraint uq_BioSource$taxId;

alter table ia_biosource_audit add (tissue_ac VARCHAR2(30))
;


alter table ia_biosource_audit add ( celltype_ac VARCHAR2(30))
;



CREATE OR REPLACE TRIGGER TRGAUD_IA_BIOSOURCE
 BEFORE UPDATE OR DELETE
 ON ia_biosource
 FOR EACH ROW
declare
        l_event char(1);
begin
        if deleting then
                l_event := 'D';
        elsif updating then
                l_event := 'U';
                :new.timestamp := sysdate;
                :new.userstamp := user;
        end if ;


        insert into ia_biosource_audit
                ( event
                , ac
                , deprecated
                , created
                , updated
                , timestamp
                , userstamp
                , taxid
                , owner_ac
                , shortlabel
                , fullname
                , tissue_ac
                , celltype_ac
                )
        values
                ( l_event
                , :old.ac
                , :old.deprecated
                , :old.created
                , :old.updated
                , :old.timestamp
                , :old.userstamp
                , :old.taxid
                , :old.owner_ac
                , :old.shortlabel
                , :old.fullname
                , :old.tissue_ac
                , :old.celltype_ac
                );
end;
/


