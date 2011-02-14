PROMPT Creating table "IA_COMPONENT_CONFIDENCE_AUDIT"
create table ia_component_confidence_audit (
             event char(1) not null,
             ac varchar2(30 char) not null, 
             created timestamp not null, 
             created_user varchar2(30 char) not null, 
             updated timestamp not null, 
             userstamp varchar2(30 char) not null, 
             deprecated number(1,0) not null, 
             value varchar2(4000 char), 
             confidencetype_ac varchar2(30 char), 
             component_ac varchar2(30 char), 
             primary key (ac)
       )
TABLESPACE &&intactMainTablespace;

