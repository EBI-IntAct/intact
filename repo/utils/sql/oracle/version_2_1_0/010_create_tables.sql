PROMPT "Creating table ia_imex_imp_release"
CREATE TABLE ia_imex_imp_release
(      id                      VARCHAR2(30)                    NOT NULL   CONSTRAINT pk_imex_imp_release  PRIMARY KEY

    ,  created                 DATE           DEFAULT  SYSDATE NOT NULL
    ,  created_user            VARCHAR2(30)   DEFAULT  USER    NOT NULL
    ,  updated                 DATE           DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)   DEFAULT  USER    NOT NULL
    ,  deleted                 DATE           DEFAULT  SYSDATE NOT NULL
    ,  deletor                 VARCHAR2(30)   DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace;

set term off
    COMMENT ON TABLE ia_imex_imp_release IS
    'Represents the import of an IMEx release into the intact database.';
    COMMENT ON COLUMN ia_imex_imp_release.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN ia_imex_imp_release.deprecated IS
    'Defines if the row is still valid or not.';
    COMMENT ON COLUMN ia_imex_imp_release.created IS
    'Creation date of the row.';
    COMMENT ON COLUMN ia_imex_imp_release.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN ia_imex_imp_release.userstamp IS
    'Database user who has performed the last update of the row.';
    COMMENT ON COLUMN ia_imex_imp_release.interaction_ac IS
    'Unique accession number of the interaction the parameter is attached to.';
    COMMENT ON COLUMN ia_imex_imp_release.parametertype_ac IS
    'Unique accession number of the type of the parameter in the controlled vocabulary.';
    COMMENT ON COLUMN ia_imex_imp_release.parameterunit_ac IS
    'Unique acession number of the unit of the parameter in the controlled vocabulary.';
    COMMENT ON COLUMN ia_imex_imp_release.owner_ac IS
    'Refers to the owner of the row.';
    COMMENT ON COLUMN ia_imex_imp_release.experiment_ac IS
    'Refers to the experiment in which the parameter has been determined.';
    COMMENT ON COLUMN ia_imex_imp_release.base IS
    'Base of the parameter expression.';
    COMMENT ON COLUMN ia_imex_imp_release.exponent IS
    'Exponent of the value.';
    COMMENT ON COLUMN ia_imex_imp_release.uncertainty IS
    'Uncertainty of the value.';
    COMMENT ON COLUMN ia_imex_imp_release.factor IS
    'The "main" value of the parameter.';
set term on


