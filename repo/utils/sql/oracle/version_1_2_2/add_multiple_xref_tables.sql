-- add multiple xref tables
DEFINE intactMainTablespace         = INTACT_TAB
DEFINE intactIndexTablespace        = INTACT_IDX

PROMPT Creating table "IA_BioSource_Xref"
CREATE TABLE IA_BioSource_Xref
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

CREATE index i_BioSourceXref$parent_ac on IA_BioSource_Xref(parent_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_BioSourceXref$database_ac ON IA_BioSource_Xref(database_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_BioSourceXref$primaryid   ON IA_BioSource_Xref(primaryid)   TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_BioSource_Xref IS
    'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_BioSource_Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_BioSource_Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_BioSource_Xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_BioSource_Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_BioSource_Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_BioSource_Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_BioSource_Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_BioSource_Xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_BioSource_Xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_BioSource_Xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_BioSource_Xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off



PROMPT Creating table "IA_Experiment_Xref"
CREATE TABLE IA_Experiment_Xref
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

CREATE index i_ExperimentXref$parent_ac on IA_Experiment_Xref(parent_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ExperimentXref$database_ac ON IA_Experiment_Xref(database_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ExperimentXref$primaryid   ON IA_Experiment_Xref(primaryid)   TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_Experiment_Xref IS
    'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_Experiment_Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Experiment_Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Experiment_Xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_Experiment_Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_Experiment_Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_Experiment_Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_Experiment_Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Experiment_Xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Experiment_Xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Experiment_Xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Experiment_Xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off



 PROMPT Creating table "IA_ControlledVocab_Xref"
 CREATE TABLE IA_ControlledVocab_Xref
 (       ac                      VARCHAR2(30)    NOT NULL
                                                 CONSTRAINT pk_CvObjectXref
                                                 PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
      ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
      ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
      ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
      ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
      ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_CvObjectXref$qualifier REFERENCES IA_ControlledVocab(ac)
      ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_CvObjectXref$database  REFERENCES IA_ControlledVocab(ac)
      ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_CvXref$controlledVocab  REFERENCES IA_ControlledVocab(ac)
      ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_CvObjectXref$owner REFERENCES IA_Institution(ac)
      ,  primaryId               VARCHAR2(30)
      ,  secondaryId             VARCHAR2(30)
      ,  dbRelease               VARCHAR2(10)
      , created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
 )
 TABLESPACE &&intactMainTablespace
 ;

 CREATE index i_CvObjectXref$parent_ac on IA_ControlledVocab_Xref(parent_ac) TABLESPACE &&intactIndexTablespace;
 CREATE INDEX i_CvObjectXref$database_ac ON IA_ControlledVocab_Xref(database_ac) TABLESPACE &&intactIndexTablespace;
 CREATE INDEX i_CvObjectXref$primaryid   ON IA_ControlledVocab_Xref(primaryid)   TABLESPACE &&intactIndexTablespace;

 set term on
     COMMENT ON TABLE IA_ControlledVocab_Xref IS
     'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.primaryId IS
     'The primary id of the object referred to in the external database.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.secondaryId IS
     'The secondary id of the object referred to in the external database.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.dbRelease IS
     'Highest release number of the external database in which the xref was known to be correct.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.qualifier_ac IS
     'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.database_ac IS
     'Refers to the object describing the external database.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.parent_ac IS
     'Refers to the parent object this crossreference belongs to.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.owner_ac IS
     'Refers to the owner of this object. ';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.ac IS
     'Unique auto-generated accession number.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.created IS
     'Date of the creation of the row.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.updated IS
     'Date of the last update of the row.';
     COMMENT ON COLUMN IA_ControlledVocab_Xref.userstamp IS
     'Database user who has performed the last update of the column.';
 set term off




PROMPT Creating table "IA_Feature_Xref"
CREATE TABLE IA_Feature_Xref
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

CREATE index i_FeatureXref$parent_ac on IA_Feature_Xref(parent_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_FeatureXref$database_ac ON IA_Feature_Xref(database_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_FeatureXref$primaryid   ON IA_Feature_Xref(primaryid)   TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_Feature_Xref IS
    'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_Feature_Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Feature_Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Feature_Xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_Feature_Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_Feature_Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_Feature_Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_Feature_Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Feature_Xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Feature_Xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Feature_Xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Feature_Xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off



PROMPT Creating table "IA_Interactor_Xref"
CREATE TABLE IA_Interactor_Xref
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

CREATE index i_InteractorXref$parent_ac on IA_Interactor_Xref(parent_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_InteractorXref$database_ac ON IA_Interactor_Xref(database_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_InteractorXref$primaryid   ON IA_Interactor_Xref(primaryid)   TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_Interactor_Xref IS
    'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_Interactor_Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Interactor_Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Interactor_Xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_Interactor_Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_Interactor_Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_Interactor_Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_Interactor_Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Interactor_Xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Interactor_Xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Interactor_Xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Interactor_Xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off



PROMPT Creating table "IA_Publication_Xref"
CREATE TABLE IA_Publication_Xref
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

CREATE index i_PublicationXref$parent_ac on IA_Publication_Xref(parent_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_PublicationXref$database_ac ON IA_Publication_Xref(database_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_PublicationXref$primaryid   ON IA_Publication_Xref(primaryid)   TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_Publication_Xref IS
    'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_Publication_Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Publication_Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Publication_Xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_Publication_Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_Publication_Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_Publication_Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_Publication_Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Publication_Xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Publication_Xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Publication_Xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Publication_Xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off
