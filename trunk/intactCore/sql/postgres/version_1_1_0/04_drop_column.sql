--
-- Drop column timestamp from all main tables.
--

alter table IA_ALIAS drop column timestamp;
alter table IA_ANNOTATION drop column timestamp;
alter table IA_BIOSOURCE drop column timestamp;
alter table IA_BIOSOURCE2ANNOT drop column timestamp;
alter table IA_COMPONENT drop column timestamp;
alter table IA_CONTROLLEDVOCAB drop column timestamp;
alter table IA_CV2CV drop column timestamp;
alter table IA_CVOBJECT2ANNOT drop column timestamp;
alter table IA_EXP2ANNOT drop column timestamp;
alter table IA_EXPERIMENT drop column timestamp;
alter table IA_FEATURE drop column timestamp;
alter table IA_FEATURE2ANNOT drop column timestamp;
alter table IA_INSTITUTION drop column timestamp;
alter table IA_INT2ANNOT drop column timestamp;
alter table IA_INT2EXP drop column timestamp;
alter table IA_INTACTNODE drop column timestamp;
alter table IA_INTERACTOR drop column timestamp;
alter table IA_RANGE drop column timestamp;
alter table IA_SEQUENCE_CHUNK drop column timestamp;
alter table IA_XREF drop column timestamp;
