
SELECT 'Creating table "ia_institution_alias"';
CREATE TABLE ia_institution_alias
(      ac                      VARCHAR(30)    NOT NULL
                                               CONSTRAINT pk_institution_alias
                                               PRIMARY KEY
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
    ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR(30)    CONSTRAINT fk_institution_alias_qualifier  REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_institution_alias_institut   REFERENCES IA_Institution(ac)
    ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_institution_alias_owner      REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR(30)
    ,  created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE INDEX i_institution_alias_parent_ac   on IA_institution_alias(parent_ac)    ;
CREATE INDEX i_institution_alias_name        on IA_institution_alias(name)         ;
CREATE INDEX i_institution_alias_aliastype   on IA_institution_alias(aliastype_ac) ;


SELECT 'Creating table "ia_institution_xref"';
CREATE TABLE ia_institution_xref
(       ac                      VARCHAR(30)    NOT NULL
                                                CONSTRAINT pk_institution_Xref
                                                PRIMARY KEY
     ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
     ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
     ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR(30)    CONSTRAINT fk_InstitutionXref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR(30)    CONSTRAINT fk_InstitutionXref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_InstitutionXref_biosource REFERENCES IA_Institution(ac)
     ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_InstitutionXref_owner     REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR(30)
     ,  secondaryId             VARCHAR(30)
     ,  dbRelease               VARCHAR(10)
     ,  created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_InstitutionXref_parent_ac   ON ia_institution_xref(parent_ac)   ;
CREATE INDEX i_InstitutionXref_database_ac ON ia_institution_xref(database_ac) ;
CREATE INDEX i_InstitutionXref_primaryid   ON ia_institution_xref(primaryid)   ;


SELECT 'Creating table "IA_institution2Annot"';
CREATE TABLE IA_institution2Annot
(       institution_ac          VARCHAR(30)    NOT NULL CONSTRAINT fk_institution2Annot_institut  REFERENCES IA_institution(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR(30)    NOT NULL CONSTRAINT fk_institution2Annot_annotati  REFERENCES IA_Annotation(ac)  ON DELETE CASCADE
     ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
     ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
     ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
     ,  created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

SELECT 'Creating composite primary Key on "IA_institution2Annot"';
ALTER TABLE IA_institution2Annot
 ADD CONSTRAINT     pk_Institution2Annot
        PRIMARY KEY  (institution_ac, annotation_ac);



SELECT 'Creating table "ia_component2exp_preps"';
CREATE TABLE ia_component2exp_preps
(       component_ac          VARCHAR(30)    NOT NULL CONSTRAINT fk_component2exp_preps_comp   REFERENCES IA_component(ac) ON DELETE CASCADE
     ,  cvobject_ac           VARCHAR(30)    NOT NULL CONSTRAINT fk_component2exp_preps_cvobj  REFERENCES IA_controlledvocab(ac)  ON DELETE CASCADE
     ,  deprecated            DECIMAL(1)       DEFAULT  0       NOT NULL
     ,  created               TIMESTAMP            DEFAULT  now() NOT NULL
     ,  userstamp             VARCHAR(30)    DEFAULT  USER    NOT NULL
     ,  updated               TIMESTAMP            DEFAULT  now() NOT NULL
     ,  created_user          VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

SELECT 'Creating composite primary Key on "ia_component2exp_preps"';
ALTER TABLE ia_component2exp_preps
 ADD CONSTRAINT     pk_component2exp_preps
        PRIMARY KEY  (component_ac, cvobject_ac);




SELECT 'Creating table "ia_component2part_detect"';
CREATE TABLE ia_component2part_detect
(       component_ac          VARCHAR(30)    NOT NULL CONSTRAINT fk_comp2part_detect_component REFERENCES IA_component(ac) ON DELETE CASCADE
     ,  cvobject_ac           VARCHAR(30)    NOT NULL CONSTRAINT fk_comp2part_detect_cvobject  REFERENCES IA_controlledvocab(ac)  ON DELETE CASCADE
     ,  deprecated            DECIMAL(1)       DEFAULT  0       NOT NULL
     ,  created               TIMESTAMP            DEFAULT  now() NOT NULL
     ,  userstamp             VARCHAR(30)    DEFAULT  USER    NOT NULL
     ,  updated               TIMESTAMP            DEFAULT  now() NOT NULL
     ,  created_user          VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

SELECT 'Creating composite primary Key on "ia_component2part_detect"';
ALTER TABLE ia_component2part_detect
 ADD CONSTRAINT     pk_comp2part_detect
        PRIMARY KEY  (component_ac, cvobject_ac);

