
CREATE TABLE IA_biosource_alias
(       ac                      VARCHAR(30)   NOT NULL
                                               CONSTRAINT pk_biosource_alias
                                               PRIMARY KEY 
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR(30)    CONSTRAINT fk_biosource_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_biosource_alias$biosource  REFERENCES IA_biosource(ac)
    ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_biosource_alias$owner REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR(30)
    , created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_biosource_alias$parent_ac on IA_biosource_alias(parent_ac) ;
CREATE INDEX i_biosource_alias$name         on IA_biosource_alias(name)  ;
CREATE INDEX i_biosource_alias$aliastype_ac on IA_biosource_alias(aliastype_ac) ;


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
    'Unique auto-generated accession DECIMAL.';
    COMMENT ON COLUMN IA_biosource_alias.created IS
    'TIMESTAMP of the creation of the row.';
    COMMENT ON COLUMN IA_biosource_alias.updated IS
    'TIMESTAMP of the last update of the row.';
    COMMENT ON COLUMN IA_biosource_alias.userstamp IS
    'Database user who has performed the last update of the column.';




CREATE TABLE IA_controlledvocab_alias
(       ac                      VARCHAR(30)   NOT NULL
                                               CONSTRAINT pk_controlledvocab_alias
                                               PRIMARY KEY 
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR(30)    CONSTRAINT fk_cv_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_cv_alias$cv  REFERENCES IA_controlledvocab(ac)
    ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_cv_alias$owner REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR(30)
    , created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_cv_alias$parent_ac on IA_controlledvocab_alias(parent_ac) ;
CREATE INDEX i_cv_alias$name         on IA_controlledvocab_alias(name)  ;
CREATE INDEX i_cv_alias$aliastype_ac on IA_controlledvocab_alias(aliastype_ac) ;


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
    'Unique auto-generated accession DECIMAL.';
    COMMENT ON COLUMN IA_controlledvocab_alias.created IS
    'TIMESTAMP of the creation of the row.';
    COMMENT ON COLUMN IA_controlledvocab_alias.updated IS
    'TIMESTAMP of the last update of the row.';
    COMMENT ON COLUMN IA_controlledvocab_alias.userstamp IS
    'Database user who has performed the last update of the column.';





CREATE TABLE IA_component_alias
(       ac                      VARCHAR(30)   NOT NULL
                                               CONSTRAINT pk_component_alias
                                               PRIMARY KEY 
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR(30)    CONSTRAINT fk_component_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_component_alias$component  REFERENCES IA_component(ac)
    ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_component_alias$owner REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR(30)
    , created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_component_alias$parent_ac on IA_component_alias(parent_ac) ;
CREATE INDEX i_component_alias$name         on IA_component_alias(name)  ;
CREATE INDEX i_component_alias$aliastype_ac on IA_component_alias(aliastype_ac) ;


    COMMENT ON TABLE IA_component_alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_component_alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN IA_component_alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN IA_component_alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN IA_component_alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_component_alias.ac IS
    'Unique auto-generated accession DECIMAL.';
    COMMENT ON COLUMN IA_component_alias.created IS
    'TIMESTAMP of the creation of the row.';
    COMMENT ON COLUMN IA_component_alias.updated IS
    'TIMESTAMP of the last update of the row.';
    COMMENT ON COLUMN IA_component_alias.userstamp IS
    'Database user who has performed the last update of the column.';




CREATE TABLE IA_feature_alias
(       ac                      VARCHAR(30)   NOT NULL
                                               CONSTRAINT pk_feature_alias
                                               PRIMARY KEY 
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR(30)    CONSTRAINT fk_feature_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_feature_alias$feature  REFERENCES IA_feature(ac)
    ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_feature_alias$owner REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR(30)
    , created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_feature_alias$parent_ac on IA_feature_alias(parent_ac)  ;
CREATE INDEX i_feature_alias$name         on IA_feature_alias(name)  ;
CREATE INDEX i_feature_alias$aliastype_ac on IA_feature_alias(aliastype_ac) ;


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
    'Unique auto-generated accession DECIMAL.';
    COMMENT ON COLUMN IA_feature_alias.created IS
    'TIMESTAMP of the creation of the row.';
    COMMENT ON COLUMN IA_feature_alias.updated IS
    'TIMESTAMP of the last update of the row.';
    COMMENT ON COLUMN IA_feature_alias.userstamp IS
    'Database user who has performed the last update of the column.';




CREATE TABLE IA_interactor_alias
(       ac                      VARCHAR(30)   NOT NULL
                                               CONSTRAINT pk_interactor_alias
                                               PRIMARY KEY 
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR(30)    CONSTRAINT fk_interactor_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_interactor_alias$interactor  REFERENCES IA_interactor(ac)
    ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_interactor_alias$owner REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR(30)
    , created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_interactor_alias$parent_ac on IA_interactor_alias(parent_ac)   ;
CREATE INDEX i_interactor_alias$name         on IA_interactor_alias(name)  ;
CREATE INDEX i_interactor_alias$aliastype_ac on IA_interactor_alias(aliastype_ac) ;


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
    'Unique auto-generated accession DECIMAL.';
    COMMENT ON COLUMN IA_interactor_alias.created IS
    'TIMESTAMP of the creation of the row.';
    COMMENT ON COLUMN IA_interactor_alias.updated IS
    'TIMESTAMP of the last update of the row.';
    COMMENT ON COLUMN IA_interactor_alias.userstamp IS
    'Database user who has performed the last update of the column.';




CREATE TABLE IA_experiment_alias
(       ac                      VARCHAR(30)   NOT NULL
                                               CONSTRAINT pk_experiment_alias
                                               PRIMARY KEY 
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR(30)    CONSTRAINT fk_experiment_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_experiment_alias$experiment  REFERENCES IA_experiment(ac)
    ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_experiment_alias$owner REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR(30)
    , created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_experiment_alias$parent_ac on IA_experiment_alias(parent_ac)  ;
CREATE INDEX i_experiment_alias$name         on IA_experiment_alias(name)  ;
CREATE INDEX i_experiment_alias$aliastype_ac on IA_experiment_alias(aliastype_ac) ;


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
    'Unique auto-generated accession DECIMAL.';
    COMMENT ON COLUMN IA_experiment_alias.created IS
    'TIMESTAMP of the creation of the row.';
    COMMENT ON COLUMN IA_experiment_alias.updated IS
    'TIMESTAMP of the last update of the row.';
    COMMENT ON COLUMN IA_experiment_alias.userstamp IS
    'Database user who has performed the last update of the column.';





CREATE TABLE IA_publication_alias
(       ac                      VARCHAR(30)   NOT NULL
                                               CONSTRAINT pk_publication_alias
                                               PRIMARY KEY 
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR(30)    CONSTRAINT fk_publication_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_publication_alias$pub  REFERENCES IA_publication(ac)
    ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_publication_alias$owner REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR(30)
    , created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_publication_alias$parent_ac on IA_publication_alias(parent_ac) ;
CREATE INDEX i_publication_alias$name         on IA_publication_alias(name)  ;
CREATE INDEX i_publication_alias$aliastype_ac on IA_publication_alias(aliastype_ac) ;



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
    'Unique auto-generated accession DECIMAL.';
    COMMENT ON COLUMN IA_publication_alias.created IS
    'TIMESTAMP of the creation of the row.';
    COMMENT ON COLUMN IA_publication_alias.updated IS
    'TIMESTAMP of the last update of the row.';
    COMMENT ON COLUMN IA_publication_alias.userstamp IS
    'Database user who has performed the last update of the column.';
