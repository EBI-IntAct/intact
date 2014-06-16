set doc off

PROMPT Creating sequence "DEFAULT_ID_SEQl"

create sequence DEFAULT_ID_SEQ;

PROMPT Creating table "ia_exp_causal_relations"

create table ia_exp_causal_relations (id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), relation_type_ac varchar2(30 char) not null, experimental_target_ac varchar2(30 char), modelled_target_ac varchar2(30 char), source_ac varchar2(30 char), primary key (id));

set term off
    COMMENT ON TABLE ia_exp_causal_relations IS
    'Causal relationship between a source participant  and a target participant in the context of an interaction evidence';
    COMMENT ON COLUMN ia_exp_causal_relations.relation_type_ac IS
    'Controlled vocabulary term that describes the causal statement of this relationship';
    COMMENT ON COLUMN ia_exp_causal_relations.experimental_target_ac IS
    'The target participant if it is a participant evidence.';
    COMMENT ON COLUMN ia_exp_causal_relations.modelled_target_ac IS
    'The target participant if it is a modelled participant part of a biological complex.';
    COMMENT ON COLUMN ia_exp_causal_relations.id IS
    'The primary identifier for the causal relationship.';
    COMMENT ON COLUMN ia_exp_causal_relations.source_ac IS
    'The source participant of this causal relationship which points to a participant evidence.';
set term on

alter table ia_exp_causal_relations add constraint FK_exprelationship2expTarget foreign key (experimental_target_ac) references ia_component;
alter table ia_exp_causal_relations add constraint FK_exprelationship2modTarget foreign key (modelled_target_ac) references ia_component;
alter table ia_exp_causal_relations add constraint FK_exprelationship2source foreign key (source_ac) references ia_component;
alter table ia_exp_causal_relations add constraint FK_exprelationship2type foreign key (relation_type_ac) references ia_controlledvocab;

PROMPT Creating table "ia_mod_causal_relations"

create table ia_mod_causal_relations (id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), relation_type_ac varchar2(30 char) not null, target_ac varchar2(30 char) not null, source_ac varchar2(30 char), primary key (id));

set term off
    COMMENT ON TABLE ia_mod_causal_relations IS
    'Causal relationship between a source participant  and a target participant in the context of a biological complex';
    COMMENT ON COLUMN ia_mod_causal_relations.relation_type_ac IS
    'Controlled vocabulary term that describes the causal statement of this relationship';
    COMMENT ON COLUMN ia_mod_causal_relations.target_ac IS
    'The target participant which is part of the biological complex.';
    COMMENT ON COLUMN ia_mod_causal_relations.id IS
    'The primary identifier for the causal relationship.';
    COMMENT ON COLUMN ia_mod_causal_relations.source_ac IS
    'The source participant of this causal relationship which points to a participant of a biological complex.';
set term on

alter table ia_mod_causal_relations add constraint FK_modrelationship2target foreign key (target_ac) references ia_component;
alter table ia_mod_causal_relations add constraint FK_modrelationship2source foreign key (source_ac) references ia_component;
alter table ia_mod_causal_relations add constraint FK_modrelationship2type foreign key (relation_type_ac) references ia_controlledvocab;

PROMPT Creating table "ia_modfeature2feature"
create table ia_modfeature2feature (created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), modelled_feature_ac varchar2(30 char) not null, linked_feature_ac varchar2(30 char) not null);

set term off
    COMMENT ON TABLE ia_modfeature2feature IS
    'Join table to list all the modelled features that are linked to a specific modelled feature (features attached to complexes)';
    COMMENT ON COLUMN ia_modfeature2feature.modelled_feature_ac IS
    'Refers to the parent modelled feature';
    COMMENT ON COLUMN ia_modfeature2feature.linked_feature_ac IS
    'Refers to the modelled feature which is linked to the parent modelled feature';
set term on

alter table ia_modfeature2feature add constraint FK_linked2modelled foreign key (linked_feature_ac) references ia_feature;
alter table ia_modfeature2feature add constraint FK_linked2parent foreign key (modelled_feature_ac) references ia_feature;

PROMPT Creating table "ia_expfeature2feature"
create table ia_expfeature2feature (created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), feature_evidence_ac varchar2(30 char) not null, linked_feature_ac varchar2(30 char) not null);

set term off
    COMMENT ON TABLE ia_expfeature2feature IS
    'Join table to list all the features evidences that are linked to a specific feature evidence (features attached to interaction evidences)';
    COMMENT ON COLUMN ia_expfeature2feature.feature_evidence_ac IS
    'Refers to the parent feature evidence';
    COMMENT ON COLUMN ia_expfeature2feature.linked_feature_ac IS
    'Refers to the feature evidence which is linked to the parent feature evidence';
set term on

alter table ia_expfeature2feature add constraint FK_linked2evidence foreign key (linked_feature_ac) references ia_feature;
alter table ia_expfeature2feature add constraint FK_linked2parent foreign key (feature_evidence_ac) references ia_feature;

PROMPT Creating table "ia_feature2method"
create table ia_feature2method (created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), feature_ac varchar2(30 char) not null, method_ac varchar2(30 char) not null);

set term off
    COMMENT ON TABLE ia_feature2method IS
    'Join table to list all the feature detection methods used for a specific feature evidence (features attached to interaction evidences)';
    COMMENT ON COLUMN ia_feature2method.method_ac IS
    'Refers to the feature detection method which is s controlledvocabulary term';
    COMMENT ON COLUMN ia_feature2method.feature_ac IS
    'Refers to the feature evidence';
set term on

alter table ia_feature2method add constraint FK_feature2method foreign key (method_ac) references ia_controlledvocab;
alter table ia_feature2method add constraint FK_feature2feature foreign key (feature_ac) references ia_feature;

PROMPT Creating table "ia_feature_parameter"
create table ia_feature_parameter (ac varchar2(30 char) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), base number(10,0), uncertainty double precision, exponent number(10,0), factor double precision, parametertype_ac varchar2(30 char) not null, parameterunit_ac varchar2(30 char), parent_ac varchar2(30 char), primary key (ac));

set term off
    COMMENT ON TABLE ia_feature_parameter IS
    'Experimental parameters attached to features in the context of an interaction evidence';
    COMMENT ON COLUMN ia_feature_parameter.ac IS
    'Primary key for feature parameter';
    COMMENT ON COLUMN ia_feature_parameter.base IS
    'Parameter base value. The default value is 10'; 
    COMMENT ON COLUMN ia_feature_parameter.uncertainty IS
    'Parameter uncertainty';
    COMMENT ON COLUMN ia_feature_parameter.exponent IS
    'Parameter exponent value. The default value is 0';
    COMMENT ON COLUMN ia_feature_parameter.factor IS
    'Parameter factor value.';
    COMMENT ON COLUMN ia_feature_parameter.parametertype_ac IS
    'Refers to a parameter type which is a controlled vocabulary term.';
    COMMENT ON COLUMN ia_feature_parameter.parameterunit_ac IS
    'Refers to a parameter unit which is a controlled vocabulary term.';
    COMMENT ON COLUMN ia_feature_parameter.parent_ac IS
    'Refers to the parent feature evidence.';
set term on

alter table ia_feature_parameter add constraint FK_featureparam2type foreign key (parametertype_ac) references ia_controlledvocab;
alter table ia_feature_parameter add constraint FK_featureparam2unit foreign key (parameterunit_ac) references ia_controlledvocab;
alter table ia_feature_parameter add constraint FK_featureparam2feature foreign key (parent_ac) references ia_feature;

PROMPT Creating table "ia_mod_sequence_xref"
create table ia_mod_sequence_xref (ac varchar2(30 char) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), id varchar2(50 char) not null, secondaryId varchar2(256 char), dbrelease varchar2(255 char), database_ac varchar2(30 char), qualifier_ac varchar2(30 char), parent_ac varchar2(30 char), primary key (ac));

set term off
    COMMENT ON TABLE ia_mod_sequence_xref IS
    'Database cross references for range resulting sequences occuring in biological complexes';
    COMMENT ON COLUMN ia_mod_sequence_xref.ac IS
    'Primary key for resulting sequence xref';
    COMMENT ON ia_mod_sequence_xref.id IS
    'Database primary identifier'; 
    COMMENT ON COLUMN ia_mod_sequence_xref.dbrelease IS
    'Database cross reference version';
    COMMENT ON COLUMN ia_mod_sequence_xref.database_ac IS
    'Refers to the database of this cross reference which is a controlled vocabulary term';
    COMMENT ON COLUMN ia_mod_sequence_xref.qualifier_ac IS
    'Refers to the qualifier of this cross reference which is a controlled vocabulary term';
    COMMENT ON COLUMN ia_mod_sequence_xref.parent_ac IS
    'Refers to the parent range resulting sequence (ia_range table).';
set term on

alter table ia_mod_sequence_xref add constraint FK_modresxref2database foreign key (database_ac) references ia_controlledvocab;
alter table ia_mod_sequence_xref add constraint FK_modresxref2qualifier foreign key (qualifier_ac) references ia_controlledvocab;
alter table ia_mod_sequence_xref add constraint FK_modresxref2range foreign key (parent_ac) references ia_range;

PROMPT Creating table "ia_exp_sequence_xref"
create table ia_exp_sequence_xref (ac varchar2(30 char) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), id varchar2(50 char) not null, secondaryId varchar2(256 char), dbrelease varchar2(255 char), database_ac varchar2(30 char), qualifier_ac varchar2(30 char), parent_ac varchar2(30 char), primary key (ac));

set term off
    COMMENT ON TABLE ia_exp_sequence_xref IS
    'Database cross references for range resulting sequences occuring in interaction evidences (mutations, etc.)';
    COMMENT ON COLUMN ia_exp_sequence_xref.ac IS
    'Primary key for resulting sequence xref';
    COMMENT ON ia_exp_sequence_xref.id IS
    'Database primary identifier'; 
    COMMENT ON COLUMN ia_exp_sequence_xref.dbrelease IS
    'Database cross reference version';
    COMMENT ON COLUMN ia_exp_sequence_xref.database_ac IS
    'Refers to the database of this cross reference which is a controlled vocabulary term';
    COMMENT ON COLUMN ia_exp_sequence_xref.qualifier_ac IS
    'Refers to the qualifier of this cross reference which is a controlled vocabulary term';
    COMMENT ON COLUMN ia_exp_sequence_xref.parent_ac IS
    'Refers to the parent range resulting sequence (ia_range table).';
set term on

alter table ia_exp_sequence_xref add constraint FK_expresxref2database foreign key (database_ac) references ia_controlledvocab;
alter table ia_exp_sequence_xref add constraint FK_expresxref2qualifier foreign key (qualifier_ac) references ia_controlledvocab;
alter table ia_exp_sequence_xref add constraint FK_expresxref2range foreign key (parent_ac) references ia_range;

PROMPT Creating table "ia_pool2interactor"
create table ia_pool2interactor (created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), interactor_pool_ac varchar2(30 char) not null, interactor_ac varchar2(30 char) not null);

set term off
    COMMENT ON TABLE ia_pool2interactor IS
    'Join table to list all interactors that are part of a pool/set of interactors (open set, candidate set, etc.)';
    COMMENT ON COLUMN ia_pool2interactor.interactor_pool_ac IS
    'Refers to the interactor pool present in the ia_interactor table';
    COMMENT ON COLUMN ia_pool2interactor.interactor_ac IS
    'Refers to the interactor which is part of the pool in the ia_interactor table';
set term on

alter table ia_pool2interactor add constraint FK_pool2interactor foreign key (interactor_ac) references ia_interactor;
alter table ia_pool2interactor add constraint FK_pool2interactorpool foreign key (interactor_pool_ac) references ia_interactor;

PROMPT Creating table "ia_variable_parameter"
create table ia_variable_parameter (id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), description varchar2(255 char), experiment_ac varchar2(30 char), unit_ac varchar2(30 char), primary key (id));

set term off
    COMMENT ON TABLE ia_variable_parameter IS
    'Variable parameters that have been used in an experiment to study dynamic interactions';
    COMMENT ON COLUMN ia_variable_parameter.id IS
    'Primary key for the variable parameter';
    COMMENT ON COLUMN ia_variable_parameter.description IS
    'Description of the variable parameter';
    COMMENT ON COLUMN ia_variable_parameter.experiment_ac IS
    'Refers to the experiment where this variable parameter has been used';
    COMMENT ON COLUMN ia_variable_parameter.unit_ac IS
    'Refers to the unit of this variable parameter which is a controlled vocabulary term';
set term on

alter table ia_variable_parameter add constraint FK_vparam2experiment foreign key (experiment_ac) references ia_experiment;
alter table ia_variable_parameter add constraint FK_vparam2unit foreign key (unit_ac) references ia_controlledvocab;

PROMPT Creating table "ia_var_parameter_value"
create table ia_var_parameter_value (id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), variableorder number(10,0), value varchar2(255 char), parameter_id number(19,0), primary key (id));

set term off
    COMMENT ON TABLE ia_var_parameter_value IS
    'Parameter value associated to an experimental variable parameter used in an experiment for studying dynamic interactions';
    COMMENT ON COLUMN ia_var_parameter_value.id IS
    'Primary key for the variable parameter value';
    COMMENT ON COLUMN ia_var_parameter_value.variableorder IS
    'Order for this parameter value if it makes sense. When the parameter value is not ordered, the order is 0.';
    COMMENT ON COLUMN ia_var_parameter_value.value IS
    'Value of the variable parameter';
    COMMENT ON COLUMN ia_var_parameter_value.parameter_id IS
    'Refers to the variable parameter description used in an experiment';
set term on

alter table ia_var_parameter_value add constraint FK_i3tapwtfymhhx0ucchfco47ih foreign key (parameter_id) references ia_variable_parameter;

PROMPT Creating table "ia_interaction_varparam"
create table ia_interaction_varparam (id number(19,0) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), parent_ac varchar2(30 char), primary key (id))

set term off
    COMMENT ON TABLE ia_interaction_varparam IS
    'Set of variable conditions for which the interaction occurs';
    COMMENT ON COLUMN ia_interaction_varparam.id IS
    'Primary key for the set of variable conditions';
    COMMENT ON COLUMN ia_interaction_varparam.parent_ac IS
    'Refers to the interaction evidence that occurs with this specific set of conditions';
set term on

alter table ia_interaction_varparam add constraint FK_varset2interaction foreign key (parent_ac) references ia_interactor;

PROMPT Creating table "ia_varset2paramvalue"
create table ia_varset2paramvalue (created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), varset_id number(19,0) not null, parametervalue_id number(19,0) not null, primary key (varset_id, parametervalue_id));

set term off
    COMMENT ON TABLE ia_varset2paramvalue IS
    'Join table to list of variable value conditions for which the interaction occurs';
    COMMENT ON COLUMN ia_varset2paramvalue.parametervalue_id IS
    'Refers to the variable value set of an interaction evidence (ia_interaction_var_parameters table)';
    COMMENT ON COLUMN ia_varset2paramvalue.varset_id IS
    'Refers to the variable parameter value/condition (ia_variable_parameter_value)';
set term on

alter table ia_varset2paramvalue add constraint FK_varset2parametervalue foreign key (parametervalue_id) references ia_variable_parameter_value;
alter table ia_varset2paramvalue add constraint FK_varset2varset foreign key (varset_id) references ia_interaction_var_parameters;

PROMPT Creating table "ia_complex_lcycle_evt"
create table ia_complex_lcycle_evt (ac varchar2(30 char) not null, created timestamp, created_user varchar2(30 char), updated timestamp, userstamp varchar2(30 char), note clob, when_date timestamp, event_ac varchar2(30 char) not null, user_ac varchar2(30 char) not null, complex_ac varchar2(30 char), primary key (ac))

set term off
    COMMENT ON TABLE ia_complex_lcycle_evt IS
    'Life cycle events associated with a biological complex';
    COMMENT ON COLUMN ia_complex_lcycle_evt.ac IS
    'Primary key for the lifecycle event';
    COMMENT ON COLUMN ia_complex_lcycle_evt.note IS
    'Note about the event. Gives some free text description';
    COMMENT ON COLUMN ia_complex_lcycle_evt.when_date IS
    'Date or the event';
    COMMENT ON COLUMN ia_complex_lcycle_evt.event_ac IS
    'Event type which is a controlled vocabulary term';
    COMMENT ON COLUMN ia_complex_lcycle_evt.user_ac IS
    'User who triggered the event.';
    COMMENT ON COLUMN ia_complex_lcycle_evt.note IS
    'Referes to the biological complex';
set term on

create index ia_complex_lifecycle_eventidx_event_event on ia_complex_lcycle_evt (event_ac)
create index ia_complex_lifecycle_eventidx_event_who on ia_complex_lcycle_evt (user_ac)
alter table ia_complex_lcycle_evt add constraint FK_LIFECYCLE_EVENT_COMPLEX foreign key (complex_ac) references ia_interactor;
alter table ia_complex_lcycle_evt add constraint FK_event2type foreign key (event_ac) references ia_controlledvocab;
alter table ia_complex_lcycle_evt add constraint FK_event2user foreign key (user_ac) references ia_user;
