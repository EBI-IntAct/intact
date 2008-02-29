-- Creating table "ia_interaction_parameter"
CREATE TABLE ia_interaction_parameter
(      ac                      varchar(30)    NOT NULL     CONSTRAINT pk_interaction_parameter    PRIMARY KEY
    ,  deprecated              bool      DEFAULT  false       NOT NULL
    ,  created                 timestamp            DEFAULT now()
    ,  updated                 timestamp            DEFAULT now()
    ,  userstamp               varchar(30)    DEFAULT  USER    NOT NULL
    ,  interaction_ac          varchar(30)    CONSTRAINT fk_interaction_parameter_interaction REFERENCES IA_INTERACTOR(ac)
    ,  parametertype_ac        varchar(30)    CONSTRAINT fk_interaction_parameter_qualifier  REFERENCES IA_ControlledVocab(ac)
    ,  parameterunit_ac        varchar(30)    CONSTRAINT fk_interaction_parameter_unit REFERENCES IA_ControlledVocab(ac)
    ,  owner_ac                varchar(30)    CONSTRAINT fk_interaction_parameter_owner  REFERENCES IA_Institution(ac)
    ,  experiment_ac           varchar(30)    CONSTRAINT fk_interaction_parameter_experiment REFERENCES IA_Experiment(ac)
    ,  base		       int8           DEFAULT 10
    ,  exponent		       int8           DEFAULT 0
    ,  uncertainty	       float8
    ,  factor		       float8
    ,  created_user            varchar(30)    DEFAULT  USER    NOT NULL
);

-- set term off
    COMMENT ON TABLE ia_interaction_parameter IS
    'Represents an interaction parameter.';
    COMMENT ON COLUMN ia_interaction_parameter.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN ia_interaction_parameter.deprecated IS
    'Defines if the row is still valid or not.';
    COMMENT ON COLUMN ia_interaction_parameter.created IS
    'Creation date of the row.';
    COMMENT ON COLUMN ia_interaction_parameter.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN ia_interaction_parameter.userstamp IS
    'Database user who has performed the last update of the row.';
    COMMENT ON COLUMN ia_interaction_parameter.interaction_ac IS
    'Unique accession number of the interaction the parameter is attached to.';
    COMMENT ON COLUMN ia_interaction_parameter.parametertype_ac IS
    'Unique accession number of the type of the parameter in the controlled vocabulary.';
    COMMENT ON COLUMN ia_interaction_parameter.parameterunit_ac IS
    'Unique acession number of the unit of the parameter in the controlled vocabulary.';
    COMMENT ON COLUMN ia_interaction_parameter.owner_ac IS
    'Refers to the owner of the row.';
    COMMENT ON COLUMN ia_interaction_parameter.experiment_ac IS
    'Refers to the experiment in which the parameter has been determined.';
    COMMENT ON COLUMN ia_interaction_parameter.base IS
    'Base of the parameter expression.';
    COMMENT ON COLUMN ia_interaction_parameter.exponent IS
    'Exponent of the value.';
    COMMENT ON COLUMN ia_interaction_parameter.uncertainty IS
    'Uncertainty of the value.';
    COMMENT ON COLUMN ia_interaction_parameter.factor IS
    'The "main" value of the parameter.';
--set term on

-- Creating table "ia_component_parameter"
CREATE TABLE ia_component_parameter
(      ac                      varchar(30)    NOT NULL     CONSTRAINT pk_component_parameter    PRIMARY KEY
,  deprecated              bool      DEFAULT  false       NOT NULL
,  created                 timestamp            DEFAULT now()
,  updated                 timestamp            DEFAULT now()
,  userstamp               varchar(30)    DEFAULT  USER    NOT NULL
,  component_ac            varchar(30)    CONSTRAINT fk_component_parameter_component REFERENCES IA_component(ac)
,  parametertype_ac        varchar(30)    CONSTRAINT fk_component_parameter_qualifier  REFERENCES IA_ControlledVocab(ac)
,  parameterunit_ac        varchar(30)    CONSTRAINT fk_component_parameter_unit REFERENCES IA_ControlledVocab(ac)
,  owner_ac                varchar(30)    CONSTRAINT fk_component_parameter_owner  REFERENCES IA_Institution(ac)
,  experiment_ac           varchar(30)    CONSTRAINT fk_component_parameter_experiment REFERENCES IA_Experiment(ac)
,  base		           int8           DEFAULT 10
,  exponent		   int8           DEFAULT 0
,  uncertainty	           float8
,  factor		   float8
,  created_user          varchar(30)    DEFAULT  USER    NOT NULL
);

-- set term off
COMMENT ON TABLE ia_component_parameter IS
'Represents an component parameter.';
COMMENT ON COLUMN ia_component_parameter.ac IS
'Unique auto-generated accession number.';
COMMENT ON COLUMN ia_component_parameter.deprecated IS
'Defines if the row is still valid or not.';
COMMENT ON COLUMN ia_component_parameter.created IS
'Creation date of the row.';
COMMENT ON COLUMN ia_component_parameter.updated IS
'Date of the last update of the row.';
COMMENT ON COLUMN ia_component_parameter.userstamp IS
'Database user who has performed the last update of the row.';
COMMENT ON COLUMN ia_component_parameter.component_ac IS
'Unique accession number of the component the parameter is attached to.';
COMMENT ON COLUMN ia_component_parameter.parametertype_ac IS
'Unique accession number of the type of the parameter in the controlled vocabulary.';
COMMENT ON COLUMN ia_component_parameter.parameterunit_ac IS
'Unique acession number of the unit of the parameter in the controlled vocabulary.';
COMMENT ON COLUMN ia_component_parameter.owner_ac IS
'Refers to the owner of the row.';
COMMENT ON COLUMN ia_component_parameter.experiment_ac IS
'Refers to the experiment in which the parameter has been determined.';
COMMENT ON COLUMN ia_component_parameter.base IS
'Base of the parameter expression.';
COMMENT ON COLUMN ia_component_parameter.exponent IS
'Exponent of the value.';
COMMENT ON COLUMN ia_component_parameter.uncertainty IS
'Uncertainty of the value.';
COMMENT ON COLUMN ia_component_parameter.factor IS
'The "main" value of the parameter.';
--set term on