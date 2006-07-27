-- add multiple xref tables

PROMPT Creating table "IA_BioSourceXref"
CREATE TABLE IA_BioSourceXref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_BioSourceXref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_BioSourceXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_BioSourceXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_BioSourceXref$biosource  REFERENCES IA_BioSource(ac)
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_BioSourceXref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
     , created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_BioSourceXref$parent_ac on IA_BioSourceXref(parent_ac) TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_BioSourceXref IS
    'Represents a crossreference. Several objects may have crossREFERENCES IA_e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_BioSourceXref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_BioSourceXref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_BioSourceXref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_BioSourceXref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_BioSourceXref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_BioSourceXref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_BioSourceXref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_BioSourceXref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_BioSourceXref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_BioSourceXref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_BioSourceXref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off


PROMPT Creating table "IA_ExperimentXref"
CREATE TABLE IA_ExperimentXref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_ExperimentXref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_ExperimentXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_ExperimentXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_ExperimentXref$experiment  REFERENCES IA_Experiment(ac)
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_ExperimentXref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
     , created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_ExperimentXref$parent_ac on IA_ExperimentXref(parent_ac) TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_ExperimentXref IS
    'Represents a crossreference. Several objects may have crossREFERENCES IA_e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_ExperimentXref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_ExperimentXref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_ExperimentXref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_ExperimentXref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_ExperimentXref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_ExperimentXref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_ExperimentXref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_ExperimentXref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_ExperimentXref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_ExperimentXref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_ExperimentXref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off


PROMPT Creating table "IA_ControlledVocabXref"
CREATE TABLE IA_ControlledVocabXref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_ControlledVocabXref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_ControlledVocabXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_ControlledVocabXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_ControlledVocabXref$controlledVocab  REFERENCES IA_ControlledVocab(ac)
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_ControlledVocabXref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
     , created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_ControlledVocabXref$parent_ac on IA_ControlledVocabXref(parent_ac) TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_ControlledVocabXref IS
    'Represents a crossreference. Several objects may have crossREFERENCES IA_e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_ControlledVocabXref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_ControlledVocabXref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_ControlledVocabXref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_ControlledVocabXref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_ControlledVocabXref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_ControlledVocabXref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_ControlledVocabXref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_ControlledVocabXref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_ControlledVocabXref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_ControlledVocabXref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_ControlledVocabXref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off


PROMPT Creating table "IA_FeatureXref"
CREATE TABLE IA_FeatureXref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_FeatureXref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_FeatureXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_FeatureXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_FeatureXref$feature  REFERENCES IA_Feature(ac)
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_FeatureXref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
     , created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_FeatureXref$parent_ac on IA_FeatureXref(parent_ac) TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_FeatureXref IS
    'Represents a crossreference. Several objects may have crossREFERENCES IA_e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_FeatureXref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_FeatureXref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_FeatureXref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_FeatureXref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_FeatureXref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_FeatureXref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_FeatureXref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_FeatureXref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_FeatureXref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_FeatureXref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_FeatureXref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off


PROMPT Creating table "IA_InteractorXref"
CREATE TABLE IA_InteractorXref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_InteractorXref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_InteractorXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_InteractorXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_InteractorXref$interactor  REFERENCES IA_Interactor(ac)
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_InteractorXref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
     , created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_InteractorXref$parent_ac on IA_InteractorXref(parent_ac) TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_InteractorXref IS
    'Represents a crossreference. Several objects may have crossREFERENCES IA_e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_InteractorXref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_InteractorXref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_InteractorXref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_InteractorXref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_InteractorXref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_InteractorXref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_InteractorXref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_InteractorXref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_InteractorXref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_InteractorXref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_InteractorXref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off


PROMPT Creating table "IA_PublicationXref"
CREATE TABLE IA_PublicationXref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_PublicationXref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_PublicationXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_PublicationXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_PublicationXref$publication  REFERENCES IA_Publication(ac)
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_PublicationXref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
     , created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_PublicationXref$parent_ac on IA_PublicationXref(parent_ac) TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_PublicationXref IS
    'Represents a crossreference. Several objects may have crossREFERENCES IA_e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_PublicationXref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_PublicationXref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_PublicationXref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_PublicationXref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_PublicationXref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_PublicationXref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_PublicationXref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_PublicationXref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_PublicationXref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_PublicationXref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_PublicationXref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off