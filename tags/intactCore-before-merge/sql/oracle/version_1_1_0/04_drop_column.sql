set serveroutput on size 1000000

spool 05_drop_column.log

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  start_date from dual;


PROMPT ALTER TABLE IA_ALIAS
alter table IA_ALIAS drop column timestamp;
PROMPT ALTER TABLE IA_ALIAS_AUDIT
alter table IA_ALIAS_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_ANNOTATION
alter table IA_ANNOTATION drop column timestamp;
PROMPT ALTER TABLE IA_ANNOTATION_AUDIT
alter table IA_ANNOTATION_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_BIOSOURCE
alter table IA_BIOSOURCE drop column timestamp;
PROMPT ALTER TABLE IA_BIOSOURCE2ANNOT
alter table IA_BIOSOURCE2ANNOT drop column timestamp;
PROMPT ALTER TABLE IA_BIOSOURCE2ANNOT_AUDIT
alter table IA_BIOSOURCE2ANNOT_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_BIOSOURCE_AUDIT
alter table IA_BIOSOURCE_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_COMPONENT
alter table IA_COMPONENT drop column timestamp;
PROMPT ALTER TABLE IA_COMPONENT_AUDIT
alter table IA_COMPONENT_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_CONTROLLEDVOCAB
alter table IA_CONTROLLEDVOCAB drop column timestamp;
PROMPT ALTER TABLE IA_CONTROLLEDVOCAB_AUDIT
alter table IA_CONTROLLEDVOCAB_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_CV2CV
alter table IA_CV2CV drop column timestamp;
PROMPT ALTER TABLE IA_CV2CV_AUDIT
alter table IA_CV2CV_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_CVOBJECT2ANNOT
alter table IA_CVOBJECT2ANNOT drop column timestamp;
PROMPT ALTER TABLE IA_CVOBJECT2ANNOT_AUDIT
alter table IA_CVOBJECT2ANNOT_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_EXP2ANNOT
alter table IA_EXP2ANNOT drop column timestamp;
PROMPT ALTER TABLE IA_EXP2ANNOT_AUDIT
alter table IA_EXP2ANNOT_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_EXPERIMENT
alter table IA_EXPERIMENT drop column timestamp;
PROMPT ALTER TABLE IA_EXPERIMENT_AUDIT
alter table IA_EXPERIMENT_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_FEATURE
alter table IA_FEATURE drop column timestamp;
PROMPT ALTER TABLE IA_FEATURE2ANNOT
alter table IA_FEATURE2ANNOT drop column timestamp;
PROMPT ALTER TABLE IA_FEATURE2ANNOT_AUDIT
alter table IA_FEATURE2ANNOT_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_FEATURE_AUDIT
alter table IA_FEATURE_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_INSTITUTION
alter table IA_INSTITUTION drop column timestamp;
PROMPT ALTER TABLE IA_INSTITUTION_AUDIT
alter table IA_INSTITUTION_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_INT2ANNOT
alter table IA_INT2ANNOT drop column timestamp;
PROMPT ALTER TABLE IA_INT2ANNOT_AUDIT
alter table IA_INT2ANNOT_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_INT2EXP
alter table IA_INT2EXP drop column timestamp;
PROMPT ALTER TABLE IA_INT2EXP_AUDIT
alter table IA_INT2EXP_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_INTACTNODE
alter table IA_INTACTNODE drop column timestamp;
PROMPT ALTER TABLE IA_INTACTNODE_AUDIT
alter table IA_INTACTNODE_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_INTERACTOR
alter table IA_INTERACTOR drop column timestamp;
PROMPT ALTER TABLE IA_INTERACTOR_AUDIT
alter table IA_INTERACTOR_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_PUBMED
alter table IA_PUBMED drop column timestamp;
PROMPT ALTER TABLE IA_PUBMED_AUDIT
alter table IA_PUBMED_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_RANGE
alter table IA_RANGE drop column timestamp;
PROMPT ALTER TABLE IA_RANGE_AUDIT
alter table IA_RANGE_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_SEQUENCE_CHUNK
alter table IA_SEQUENCE_CHUNK drop column timestamp;
PROMPT ALTER TABLE IA_SEQUENCE_CHUNK_AUDIT
alter table IA_SEQUENCE_CHUNK_AUDIT drop column timestamp;
PROMPT ALTER TABLE IA_XREF
alter table IA_XREF drop column timestamp;
PROMPT ALTER TABLE IA_XREF_AUDIT
alter table IA_XREF_AUDIT drop column timestamp;

select to_char(sysdate,'dd-mon-yyyy hh24:mi:ss')  end_date from dual;
spool off

