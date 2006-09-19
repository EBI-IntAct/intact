-- new fields in IA_Component table

PROMPT Altering table "IA_Component"
ALTER TABLE IA_Component ADD(shortLabel VARCHAR2(20) NULL);
ALTER TABLE IA_Component ADD(fullName VARCHAR2(250) NULL);

COMMENT ON COLUMN IA_Institution.shortLabel IS
    'A short string identifying the object not necessarily unique. Could be e.g. a gene name. ';
COMMENT ON COLUMN IA_Institution.fullName IS
    'The full name of the object.';


-- new component-related tables

DEFINE intactMainTablespace         = INTACT_TAB
DEFINE intactIndexTablespace        = INTACT_IDX

PROMPT Creating table "IA_Component_Xref"
CREATE TABLE IA_Component_Xref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_ComponentXref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_ComponentXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_ComponentXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    CONSTRAINT fk_ComponentXref$component  REFERENCES IA_Component(ac)
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_ComponentXref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
     , created_user            VARCHAR2(30)    DEFAULT  USER    NOT NULL
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_ComponentXref$parent_ac on IA_Component_Xref(parent_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ComponentXref$database_ac ON IA_Component_Xref(database_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_ComponentXref$primaryid   ON IA_Component_Xref(primaryid)   TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_Component_Xref IS
    'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_Component_Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Component_Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Component_Xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_Component_Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_Component_Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_Component_Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_Component_Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Component_Xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Component_Xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Component_Xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Component_Xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off


PROMPT Creating table "IA_Component2Annot"
CREATE TABLE IA_Feature2Annot
(       component_ac            VARCHAR2(30)    NOT NULL CONSTRAINT fk_Component2Annot$feature REFERENCES IA_Component(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Component2Annot$annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'IA_Component2Annot'
ALTER TABLE IA_Component2Annot
 ADD (CONSTRAINT     pk_Component2Annot
        PRIMARY KEY  (component_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;

set term off
    COMMENT ON TABLE IA_Component2Annot IS
    'Feature2Annot. Link table from Annotation to Feature.';
    COMMENT ON COLUMN IA_Component2Annot.feature_ac IS
    'Refers to a Feature to which the Annotation is linked.';
    COMMENT ON COLUMN IA_Component2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Feature.';
set term on
