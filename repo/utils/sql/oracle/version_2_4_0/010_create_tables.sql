PROMPT "Creating table ia_component_confidence"

create table ia_component_confidence (
             ac varchar2(30 char) not null, 
             created timestamp not null, 
             created_user varchar2(30 char) not null, 
             updated timestamp not null, 
             userstamp varchar2(30 char) not null, 
             deprecated number(1,0) not null, 
             value varchar2(4000 char), 
             confidencetype_ac varchar2(30 char), 
             component_ac varchar2(30 char), 
             primary key (ac));

alter table ia_component_confidence add constraint FKC2CA33D34549308E foreign key (confidencetype_ac) references ia_controlledvocab;

alter table ia_component_confidence add constraint FKC2CA33D3EAAEF607 foreign key (component_ac) references ia_component;


