PROMPT Creating table "IA_experimental_causal_relationship_AUDIT"
create table ia_experimental_causal_relationship_audit (event char(1) not null, id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), relation_type_ac varchar2(30 char) not null, experimental_target_ac varchar2(30 char), modelled_target_ac varchar2(30 char), source_ac varchar2(30 char), primary key (id));

PROMPT Creating table "IA_modelled_causal_relationship_AUDIT"
create table ia_modelled_causal_relationship_audit (event char(1) not null, id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), relation_type_ac varchar2(30 char) not null, target_ac varchar2(30 char) not null, source_ac varchar2(30 char), primary key (id));

PROMPT Creating table "ia_modelled_feature2linked_feature_AUDIT"
create table ia_modelled_feature2linked_feature_audit (event char(1) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), modelled_feature_ac varchar2(30 char) not null, linked_feature_ac varchar2(30 char) not null);

PROMPT Creating table "ia_feature_evidence2linkedfeature_AUDIT"
create table ia_feature_evidence2linkedfeature_audit (event char(1) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), feature_evidence_ac varchar2(30 char) not null, linked_feature_ac varchar2(30 char) not null);

PROMPT Creating table "ia_feature2detection_method_AUDIT"
create table ia_feature2detection_method_audit (event char(1) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), feature_ac varchar2(30 char) not null, method_ac varchar2(30 char) not null);

PROMPT Creating table "ia_feature_parameter_AUDIT"
create table ia_feature_parameter_audit (event char(1) not null, ac varchar2(30 char) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), base number(10,0), uncertainty double precision, exponent number(10,0), factor double precision, parametertype_ac varchar2(30 char) not null, parameterunit_ac varchar2(30 char), parent_ac varchar2(30 char), primary key (ac));

PROMPT Creating table "ia_modelled_resulting_sequence_xref_AUDIT"
create table ia_modelled_resulting_sequence_xref_audit (event char(1) not null, ac varchar2(30 char) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), id varchar2(50 char) not null, secondaryId varchar2(256 char), dbrelease varchar2(255 char), database_ac varchar2(30 char), qualifier_ac varchar2(30 char), parent_ac varchar2(30 char), primary key (ac));

PROMPT Creating table "ia_experimental_resulting_sequence_xref_AUDIT"
create table ia_experimental_resulting_sequence_xref_audit (event char(1) not null, ac varchar2(30 char) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), id varchar2(50 char) not null, secondaryId varchar2(256 char), dbrelease varchar2(255 char), database_ac varchar2(30 char), qualifier_ac varchar2(30 char), parent_ac varchar2(30 char), primary key (ac));

PROMPT Creating table "ia_interactor_pool2interactor_AUDIT"
create table ia_interactor_pool2interactor_audit (event char(1) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), interactor_pool_ac varchar2(30 char) not null, interactor_ac varchar2(30 char) not null);

PROMPT Creating table "ia_variable_parameter_AUDIT"
create table ia_variable_parameter_audit (event char(1) not null, id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), description varchar2(255 char), experiment_ac varchar2(30 char), unit_ac varchar2(30 char), primary key (id))

PROMPT Creating table "ia_variable_parameter_value_AUDIT"
create table ia_variable_parameter_value_audit (event char(1) not null, id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), variableorder number(10,0), value varchar2(255 char), parameter_id number(19,0), primary key (id));

PROMPT Creating table "ia_interaction_var_parameters_AUDIT"
create table ia_interaction_var_parameters_audit (event char(1) not null, id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), parent_ac varchar2(30 char), primary key (id))

PROMPT Creating table "ia_varset2parametervalue_AUDIT"
create table ia_varset2parametervalue_audit (event char(1) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), varset_id number(19,0) not null, parametervalue_id number(19,0) not null, primary key (varset_id, parametervalue_id));

PROMPT Creating table "ia_complex_lifecycle_event_AUDIT"
create table ia_complex_lifecycle_event_audit (event char(1) not null, ac varchar2(30 char) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), note clob, when_date timestamp, event_ac varchar2(30 char) not null, user_ac varchar2(30 char) not null, complex_ac varchar2(30 char), primary key (ac))



TABLESPACE &&intactMainTablespace;
