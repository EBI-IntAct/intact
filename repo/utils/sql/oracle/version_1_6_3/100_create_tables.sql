
PROMPT Creating table "ia_institution_alias"
CREATE TABLE ia_institution_alias
(      ac                      VARCHAR2(30)    NOT NULL
                                               CONSTRAINT pk_institution_alias
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR2(30)    CONSTRAINT fk_institution_alias$qualifier  REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_institution_alias$institut   REFERENCES IA_Institution(ac)
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_institution_alias$owner      REFERENCES IA_Institution(ac)
    ,  name                    VARCHAR2(30)
    ,  created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactIndexTablespace;

CREATE INDEX i_institution_alias$parent_ac   on IA_institution_alias(parent_ac)    TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_institution_alias$name        on IA_institution_alias(name)         TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_institution_alias$aliastype   on IA_institution_alias(aliastype_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE ia_institution_alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN ia_institution_alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN ia_institution_alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN ia_institution_alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN ia_institution_alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN ia_institution_alias.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN ia_institution_alias.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN ia_institution_alias.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN ia_institution_alias.userstamp IS
    'Database user who has performed the last update of the column.';
set term on









PROMPT Creating table "ia_institution_xref"
CREATE TABLE ia_institution_xref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_institution_Xref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_InstitutionXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_InstitutionXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_InstitutionXref$biosource REFERENCES IA_Institution(ac)
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_InstitutionXref$owner     REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
     ,  created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace;

CREATE index i_InstitutionXref$parent_ac   ON ia_institution_xref(parent_ac)   TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_InstitutionXref$database_ac ON ia_institution_xref(database_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_InstitutionXref$primaryid   ON ia_institution_xref(primaryid)   TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE ia_institution_xref IS
    'Represents a crossreference.';
    COMMENT ON COLUMN ia_institution_xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN ia_institution_xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN ia_institution_xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN ia_institution_xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN ia_institution_xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN ia_institution_xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN ia_institution_xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN ia_institution_xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN ia_institution_xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN ia_institution_xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN ia_institution_xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off









PROMPT Creating table "IA_institution2Annot"
CREATE TABLE IA_institution2Annot
(       institution_ac          VARCHAR2(30)    NOT NULL CONSTRAINT fk_institution2Annot$institut  REFERENCES IA_institution(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_institution2Annot$annotati  REFERENCES IA_Annotation(ac)  ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace;

PROMPT Creating composite primary Key on 'IA_institution2Annot'
ALTER TABLE IA_institution2Annot
 ADD (CONSTRAINT     pk_Institution2Annot
        PRIMARY KEY  (institution_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;

set term off
    COMMENT ON TABLE IA_institution2Annot IS
    'IA_Pub2Annot. Link table from Annotation to Publication.';
    COMMENT ON COLUMN IA_institution2Annot.publication_ac IS
    'Refers to a Publication to which the Annotation is linked.';
    COMMENT ON COLUMN IA_institution2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Publication.';
    COMMENT ON COLUMN IA_institution2Annot.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_institution2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_institution2Annot.updated IS
    'Date of the last update of the row.';
set term on









PROMPT Creating table "ia_component2exp_preps"
CREATE TABLE ia_component2exp_preps
(       component_ac          VARCHAR2(30)    NOT NULL CONSTRAINT fk_component2exp_preps$comp   REFERENCES IA_component(ac) ON DELETE CASCADE
     ,  cvobject_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_component2exp_preps$cvobj  REFERENCES IA_controlledvocab(ac)  ON DELETE CASCADE
     ,  deprecated            NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created               DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp             VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  updated               DATE            DEFAULT  SYSDATE NOT NULL
     ,  created_user          VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace;

PROMPT Creating composite primary Key on 'ia_component2exp_preps'
ALTER TABLE ia_component2exp_preps
 ADD (CONSTRAINT     pk_component2exp_preps
        PRIMARY KEY  (component_ac, cvobject_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;

set term off
    COMMENT ON TABLE ia_component2exp_preps IS
    'ia_component2exp_preps. Link table from Component to Experimental preparations.';
    COMMENT ON COLUMN ia_component2exp_preps.component_ac IS
    'Refers to a Component to which the Experimental preparation is linked.';
    COMMENT ON COLUMN ia_component2exp_preps.cvobject_ac IS
    'Refers to the Experimental preparation linked to the Component.';
    COMMENT ON COLUMN ia_component2exp_preps.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN ia_component2exp_preps.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN ia_component2exp_preps.updated IS
    'Date of the last update of the row.';
set term on







PROMPT Creating table "ia_component2part_detect"
CREATE TABLE ia_component2part_detect
(       component_ac          VARCHAR2(30)    NOT NULL CONSTRAINT fk_comp2part_detect$component REFERENCES IA_component(ac) ON DELETE CASCADE
     ,  cvobject_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_comp2part_detect$cvobject  REFERENCES IA_controlledvocab(ac)  ON DELETE CASCADE
     ,  deprecated            NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created               DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp             VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  updated               DATE            DEFAULT  SYSDATE NOT NULL
     ,  created_user          VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'ia_component2part_detect'
ALTER TABLE ia_component2part_detect
 ADD (CONSTRAINT     pk_comp2part_detect
        PRIMARY KEY  (component_ac, cvobject_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;

set term off
    COMMENT ON TABLE ia_component2part_detect IS
    'ia_component2part_detect. Link table from Component to Participant Detections.';
    COMMENT ON COLUMN ia_component2part_detect.component_ac IS
    'Refers to a Component to which the Participant Detection is linked.';
    COMMENT ON COLUMN ia_component2part_detect.cvobject_ac IS
    'Refers to the Participant Detection linked to the Component.';
    COMMENT ON COLUMN ia_component2part_detect.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN ia_component2part_detect.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN ia_component2part_detect.updated IS
    'Date of the last update of the row.';
set term on


