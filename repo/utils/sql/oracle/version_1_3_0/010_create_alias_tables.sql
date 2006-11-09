-- add multiple alias tables

PROMPT Creating table "IA_biosource_alias"
CREATE TABLE IA_biosource_alias
(       ac                     VARCHAR2(30)    NOT NULL
                                               CONSTRAINT pk_biosource_alias
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR2(30)    CONSTRAINT fk_biosource_alias$qualifier  REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_biosource_alias$biosource  REFERENCES IA_biosource(ac)
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_biosource_alias$owner      REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR2(30)
    , created_user             VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactIndexTablespace;

CREATE INDEX i_biosource_alias$parent_ac    on IA_biosource_alias(parent_ac)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_biosource_alias$name         on IA_biosource_alias(name)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_biosource_alias$aliastype_ac on IA_biosource_alias(aliastype_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_biosource_alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_biosource_alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN IA_biosource_alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN IA_biosource_alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN IA_biosource_alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_biosource_alias.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_biosource_alias.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_biosource_alias.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_biosource_alias.userstamp IS
    'Database user who has performed the last update of the column.';
set term on



PROMPT Creating table "IA_controlledvocab_alias"
CREATE TABLE IA_controlledvocab_alias
(      ac                      VARCHAR2(30)    NOT NULL
                                               CONSTRAINT pk_controlledvocab_alias
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR2(30)    CONSTRAINT fk_cv_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_cv_alias$cv        REFERENCES IA_controlledvocab(ac)
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_cv_alias$owner     REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR2(30)
    ,  created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactIndexTablespace;

CREATE INDEX i_cv_alias$parent_ac       on IA_controlledvocab_alias(parent_ac)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_IA_cv_alias$name         on IA_controlledvocab_alias(name)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_IA_cv_alias$aliastype_ac on IA_controlledvocab_alias(aliastype_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_controlledvocab_alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_controlledvocab_alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN IA_controlledvocab_alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN IA_controlledvocab_alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN IA_controlledvocab_alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_controlledvocab_alias.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_controlledvocab_alias.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_controlledvocab_alias.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_controlledvocab_alias.userstamp IS
    'Database user who has performed the last update of the column.';
set term on



PROMPT Creating table "IA_feature_alias"
CREATE TABLE IA_feature_alias
(      ac                     VARCHAR2(30)    NOT NULL
                                               CONSTRAINT pk_feature_alias
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR2(30)    CONSTRAINT fk_feature_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_feature_alias$feature   REFERENCES IA_feature(ac)
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_feature_alias$owner     REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR2(30)
    ,  created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactIndexTablespace;

CREATE INDEX i_feature_alias$parent_ac    on IA_feature_alias(parent_ac)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_feature_alias$name         on IA_feature_alias(name)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_feature_alias$aliastype_ac on IA_feature_alias(aliastype_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_feature_alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_feature_alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN IA_feature_alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN IA_feature_alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN IA_feature_alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_feature_alias.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_feature_alias.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_feature_alias.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_feature_alias.userstamp IS
    'Database user who has performed the last update of the column.';
set term on



PROMPT Creating table "IA_interactor_alias"
CREATE TABLE IA_interactor_alias
(      ac                      VARCHAR2(30)    NOT NULL
                                               CONSTRAINT pk_interactor_alias
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR2(30)    CONSTRAINT fk_interactor_alias$qualifier  REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_interactor_alias$interactor REFERENCES IA_interactor(ac)
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_interactor_alias$owner      REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR2(30)
    ,  created_user             VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactIndexTablespace;

CREATE INDEX i_interactor_alias$parent_ac   on IA_interactor_alias(parent_ac)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_interactor_alias$name        on IA_interactor_alias(name)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_interactor_alias$aliastyp_ac on IA_interactor_alias(aliastype_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_interactor_alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_interactor_alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN IA_interactor_alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN IA_interactor_alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN IA_interactor_alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_interactor_alias.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_interactor_alias.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_interactor_alias.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_interactor_alias.userstamp IS
    'Database user who has performed the last update of the column.';
set term on



PROMPT Creating table "IA_experiment_alias"
CREATE TABLE IA_experiment_alias
(      ac                     VARCHAR2(30)    NOT NULL
                                               CONSTRAINT pk_experiment_alias
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR2(30)    CONSTRAINT fk_experiment_alias$qualifier  REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_experiment_alias$experiment REFERENCES IA_experiment(ac)
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_experiment_alias$owner      REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR2(30)
    ,  created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactIndexTablespace;

CREATE INDEX i_experiment_alias$parent_ac   on IA_experiment_alias(parent_ac)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_experiment_alias$name        on IA_experiment_alias(name)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_experiment_alias$aliastyp_ac on IA_experiment_alias(aliastype_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_experiment_alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_experiment_alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN IA_experiment_alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN IA_experiment_alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN IA_experiment_alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_experiment_alias.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_experiment_alias.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_experiment_alias.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_experiment_alias.userstamp IS
    'Database user who has performed the last update of the column.';
set term on



PROMPT Creating table "IA_publication_alias"
CREATE TABLE IA_publication_alias
(      ac                      VARCHAR2(30)    NOT NULL
                                               CONSTRAINT pk_publication_alias
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR2(30)    CONSTRAINT fk_publication_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_publication_alias$pub       REFERENCES IA_publication(ac)
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_publication_alias$owner     REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR2(30)
    ,  created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactIndexTablespace
;

CREATE INDEX i_publication_alias$parent_ac  on IA_publication_alias(parent_ac)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_publication_alias$name       on IA_publication_alias(name)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_publication_alias$aliastp_ac on IA_publication_alias(aliastype_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_publication_alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_publication_alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN IA_publication_alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN IA_publication_alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN IA_publication_alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_publication_alias.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_publication_alias.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_publication_alias.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_publication_alias.userstamp IS
    'Database user who has performed the last update of the column.';
set term on


