-- add multiple xref tables

CREATE TABLE IA_BioSource_Xref
(       ac                 VARCHAR (30)    NOT NULL
                                           CONSTRAINT pk_BioSourceXref
                                           PRIMARY KEY
     ,  deprecated         DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp          TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp          VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac       VARCHAR (30)    CONSTRAINT fk_BioSourceXref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac        VARCHAR (30)    CONSTRAINT fk_BioSourceXref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac          VARCHAR (30)    CONSTRAINT fk_BioSourceXref_biosource  REFERENCES IA_BioSource(ac)
     ,  owner_ac           VARCHAR (30)    CONSTRAINT fk_BioSourceXref_owner REFERENCES IA_Institution(ac)
     ,  primaryId          VARCHAR (30)
     ,  secondaryId        VARCHAR (30)
     ,  dbRelease          VARCHAR (10)
     ,  created_user       VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_BioSourceXref_parent_ac on IA_BioSource_Xref(parent_ac);
CREATE INDEX i_BioSourceXref_database_ac ON IA_BioSource_Xref(database_ac);
CREATE INDEX i_BioSourceXref_primaryid   ON IA_BioSource_Xref(primaryid);


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




CREATE TABLE IA_Experiment_Xref
(       ac                 VARCHAR (30)    NOT NULL
                                           CONSTRAINT pk_ExperimentXref
                                           PRIMARY KEY
     ,  deprecated         DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp          TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp          VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac       VARCHAR (30)    CONSTRAINT fk_ExperimentXref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac        VARCHAR (30)    CONSTRAINT fk_ExperimentXref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac          VARCHAR (30)    CONSTRAINT fk_ExperimentXref_experiment  REFERENCES IA_Experiment(ac)
     ,  owner_ac           VARCHAR (30)    CONSTRAINT fk_ExperimentXref_owner REFERENCES IA_Institution(ac)
     ,  primaryId          VARCHAR (30)
     ,  secondaryId        VARCHAR (30)
     ,  dbRelease          VARCHAR (10)
     ,  created_user       VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_ExperimentXref_parent_ac on IA_BioSource_Xref(parent_ac);
CREATE INDEX i_ExperimentXref_database_ac ON IA_BioSource_Xref(database_ac);
CREATE INDEX i_ExperimentXref_primaryid   ON IA_BioSource_Xref(primaryid);


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



 CREATE TABLE IA_ControlledVocab_Xref
(       ac                 VARCHAR (30)    NOT NULL
                                           CONSTRAINT pk_ControlledVocabXref
                                           PRIMARY KEY
     ,  deprecated         DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp          TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp          VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac       VARCHAR (30)    CONSTRAINT fk_ControlledVocabXref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac        VARCHAR (30)    CONSTRAINT fk_ControlledVocabXref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac          VARCHAR (30)    CONSTRAINT fk_ControlledVocabXref_ControlledVocab  REFERENCES IA_ControlledVocab(ac)
     ,  owner_ac           VARCHAR (30)    CONSTRAINT fk_ControlledVocabXref_owner REFERENCES IA_Institution(ac)
     ,  primaryId          VARCHAR (30)
     ,  secondaryId        VARCHAR (30)
     ,  dbRelease          VARCHAR (10)
     ,  created_user       VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_ControlledVocabXref_parent_ac on IA_ControlledVocab_Xref(parent_ac);
CREATE INDEX i_ControlledVocabref_database_ac ON IA_ControlledVocab_Xref(database_ac);
CREATE INDEX i_ControlledVocabXref_primaryid   ON IA_ControlledVocab_Xref(primaryid);

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




CREATE TABLE IA_Feature_Xref
(       ac                 VARCHAR (30)    NOT NULL
                                           CONSTRAINT pk_FeatureXref
                                           PRIMARY KEY
     ,  deprecated         DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp          TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp          VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac       VARCHAR (30)    CONSTRAINT fk_FeatureXref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac        VARCHAR (30)    CONSTRAINT fk_FeatureXref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac          VARCHAR (30)    CONSTRAINT fk_FeatureXref_Feature  REFERENCES IA_Feature(ac)
     ,  owner_ac           VARCHAR (30)    CONSTRAINT fk_FeatureXref_owner REFERENCES IA_Institution(ac)
     ,  primaryId          VARCHAR (30)
     ,  secondaryId        VARCHAR (30)
     ,  dbRelease          VARCHAR (10)
     ,  created_user       VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_FeatureXref_parent_ac on IA_Feature_Xref(parent_ac);
CREATE INDEX i_FeatureXref_database_ac ON IA_Feature_Xref(database_ac);
CREATE INDEX i_FeatureXref_primaryid   ON IA_Feature_Xref(primaryid);


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



CREATE TABLE IA_Interactor_Xref
(       ac                 VARCHAR (30)    NOT NULL
                                           CONSTRAINT pk_InteractorXref
                                           PRIMARY KEY
     ,  deprecated         DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp          TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp          VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac       VARCHAR (30)    CONSTRAINT fk_InteractorXref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac        VARCHAR (30)    CONSTRAINT fk_InteractorXref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac          VARCHAR (30)    CONSTRAINT fk_InteractorXref_Interactor  REFERENCES IA_Interactor(ac)
     ,  owner_ac           VARCHAR (30)    CONSTRAINT fk_InteractorXref_owner REFERENCES IA_Institution(ac)
     ,  primaryId          VARCHAR (30)
     ,  secondaryId        VARCHAR (30)
     ,  dbRelease          VARCHAR (10)
     ,  created_user       VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_InteractorXref_parent_ac on IA_Interactor_Xref(parent_ac);
CREATE INDEX i_InteractorXref_database_ac ON IA_Interactor_Xref(database_ac);
CREATE INDEX i_InteractorXref_primaryid   ON IA_Interactor_Xref(primaryid);


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




CREATE TABLE IA_Publication_Xref
(       ac                 VARCHAR (30)    NOT NULL
                                           CONSTRAINT pk_PublicationXref
                                           PRIMARY KEY
     ,  deprecated         DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp          TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp          VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac       VARCHAR (30)    CONSTRAINT fk_PublicationXref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac        VARCHAR (30)    CONSTRAINT fk_PublicationXref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac          VARCHAR (30)    CONSTRAINT fk_PublicationXref_Publication  REFERENCES IA_Publication(ac)
     ,  owner_ac           VARCHAR (30)    CONSTRAINT fk_PublicationXref_owner REFERENCES IA_Institution(ac)
     ,  primaryId          VARCHAR (30)
     ,  secondaryId        VARCHAR (30)
     ,  dbRelease          VARCHAR (10)
     ,  created_user       VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_PublicationXref_parent_ac on IA_Publication_Xref(parent_ac);
CREATE INDEX i_PublicationXref_database_ac ON IA_Publication_Xref(database_ac);
CREATE INDEX i_PublicationXref_primaryid   ON IA_Publication_Xref(primaryid);

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

