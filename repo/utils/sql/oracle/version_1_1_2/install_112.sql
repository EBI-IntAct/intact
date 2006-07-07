set serveroutput on size 1000000

spool install_112.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;


@@Pck_Mutating_Tables.pck
@@Process_Array.prc
@@TRG_AR_INTERACTOR.trg
@@TRG_AS_INTERACTOR.trg
@@TRG_BS_INTERACTOR.trg
@@trgAlias_propagate.trg
@@trgAnnotation_propagate.trg
@@trgComponent_propagate.trg
@@trgExp2Annot_propagate.trg
@@trgFeature2annot_propagate.trg 
@@trgFeature_propagate.trg
@@trgInt2Annot_propagate.trg
@@trgINT2EXP_propagate.trg
@@trgInteractor_propagate.trg
@@trgRange_propagate.trg
@@trgXref_propagate.trg

ALTER TABLE IA_RANGE_AUDIT
MODIFY (deprecated  	  	NULL
  		,CREATED            NULL
  		,UPDATED            NULL
  		,USERSTAMP          NULL
  		,UNDETERMINED       NULL
  		,LINK               NULL
  		,FEATURE_AC         NULL);

UPDATE ia_db_info
set value = '1.1.2'
where UPPER(dbi_key) ='SCHEMA_VERSION';
commit;


select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;


spool off