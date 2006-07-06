set serveroutput on size 1000000

spool install_115.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;

drop trigger trgcomponent_propagate /* now a package for to prevent mutating table */
/
drop package Pck_Mutating_Tables /* will be renamed */
/
drop trigger TRG_AR_INTERACTOR   /* will be renamed */
/
drop trigger TRG_AS_INTERACTOR   /* will be renamed */
/
drop trigger TRG_BS_INTERACTOR   /* will be renamed */
/
drop procedure Process_Array     /* will be renamed */
/

@@Pck_Mutating_Tables_Interactor.pck
@@Pck_Mutating_Tables_Component.pck
@@Process_Array_Interactor.prc
@@Process_Array_Component.prc
@@TRG_INTERACTOR_AR.trg
@@TRG_INTERACTOR_AS.trg
@@TRG_INTERACTOR_BS.trg
@@TRG_COMPONENT_AR.trg
@@TRG_COMPONENT_AS.trg
@@TRG_COMPONENT_BS.trg

@@trgAlias_propagate.trg
@@trgAnnotation_propagate.trg
@@trgExp2Annot_propagate.trg
@@trgFeature2annot_propagate.trg 
@@trgFeature_propagate.trg
@@trgInt2Annot_propagate.trg
@@trgINT2EXP_propagate.trg
@@trgInteractor_propagate.trg
@@trgRange_propagate.trg
@@trgXref_propagate.trg

UPDATE ia_db_info
set value = '1.1.5'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;


select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;


spool off