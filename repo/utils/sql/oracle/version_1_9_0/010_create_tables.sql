PROMPT "Creating table "ia_interaction_parameter"
CREATE TABLE ia_interaction_parameter
(      ac                      VARCHAR2(30)                    NOT NULL   CONSTRAINT pk_interaction_parameter    PRIMARY KEY
    ,  deprecated              NUMBER(1)      DEFAULT  0       NOT NULL
    ,  created                 DATE           DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE           DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)   DEFAULT  USER    NOT NULL
    ,  interaction_ac          VARCHAR2(30)                               CONSTRAINT fk_int_param$intrctn REFERENCES IA_INTERACTOR(ac)
    ,  parametertype_ac        VARCHAR2(30)                               CONSTRAINT fk_int_param$qulfr   REFERENCES IA_ControlledVocab(ac)
    ,  parameterunit_ac        VARCHAR2(30)                               CONSTRAINT fk_int_param$unit    REFERENCES IA_ControlledVocab(ac)
    ,  owner_ac                VARCHAR2(30)                               CONSTRAINT fk_int_param$owner   REFERENCES IA_Institution(ac)
    ,  experiment_ac           VARCHAR2(30)                               CONSTRAINT fk_int_param$exprmnt REFERENCES IA_Experiment(ac)
    ,  base		               INT            DEFAULT 10
    ,  exponent		           INT            DEFAULT 0
    ,  uncertainty	           FLOAT
    ,  factor		           FLOAT
    ,  created_user            VARCHAR2(30)   DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace;

set term off
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
set term on



PROMPT Creating table "ia_component_parameter"
CREATE TABLE ia_component_parameter
(      ac                  VARCHAR2(30)                    NOT NULL   CONSTRAINT pk_component_parameter    PRIMARY KEY
,  deprecated              NUMBER(1)      DEFAULT  0       NOT NULL
,  created                 DATE           DEFAULT  SYSDATE NOT NULL
,  updated                 DATE           DEFAULT  SYSDATE NOT NULL
,  userstamp               VARCHAR2(30)   DEFAULT  USER    NOT NULL
,  component_ac            VARCHAR2(30)                               CONSTRAINT fk_comp_param$intrct  REFERENCES IA_INTERACTOR(ac)
,  parametertype_ac        VARCHAR2(30)                               CONSTRAINT fk_comp_param$qulfr   REFERENCES IA_ControlledVocab(ac)
,  parameterunit_ac        VARCHAR2(30)                               CONSTRAINT fk_comp_param$unit    REFERENCES IA_ControlledVocab(ac)
,  owner_ac                VARCHAR2(30)                               CONSTRAINT fk_comp_param$owner   REFERENCES IA_Institution(ac)
,  experiment_ac           VARCHAR2(30)                               CONSTRAINT fk_comp_param$exprmt  REFERENCES IA_Experiment(ac)
,  base		               INT            DEFAULT 10
,  exponent		           INT            DEFAULT 0
,  uncertainty	           FLOAT
,  factor		           FLOAT
,  created_user            VARCHAR2(30)   DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace;

set term off
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
set term on