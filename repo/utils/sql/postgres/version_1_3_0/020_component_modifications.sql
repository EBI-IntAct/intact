-- new fields in IA_Component table


ALTER TABLE IA_Component ADD COLUMN shortLabel VARCHAR(20) NULL;
ALTER TABLE IA_Component ADD COLUMN fullName VARCHAR(250) NULL;


    COMMENT ON COLUMN IA_Institution.shortLabel IS
        'A short string identifying the object not necessarily unique. Could be e.g. a gene name. ';
    COMMENT ON COLUMN IA_Institution.fullName IS
        'The full name of the object.';



-- new component-related tables


CREATE TABLE IA_Component_Xref
(       ac                      VARCHAR(30)    NOT NULL
                                                CONSTRAINT pk_ComponentXref
                                                PRIMARY KEY 
     ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP            DEFAULT  now() NOT NULL
     ,  updated                 TIMESTAMP            DEFAULT  now() NOT NULL
     ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR(30)    CONSTRAINT fk_ComponentXref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR(30)    CONSTRAINT fk_ComponentXref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR(30)    CONSTRAINT fk_ComponentXref$component  REFERENCES IA_Component(ac)
     ,  owner_ac                VARCHAR(30)    CONSTRAINT fk_ComponentXref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR(30)
     ,  secondaryId             VARCHAR(30)
     ,  dbRelease               VARCHAR(10)
     , created_user            VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_ComponentXref$parent_ac on IA_Component_Xref(parent_ac) ;
CREATE INDEX i_ComponentXref$database_ac ON IA_Component_Xref(database_ac) ;
CREATE INDEX i_ComponentXref$primaryid   ON IA_Component_Xref(primaryid)   ;


    COMMENT ON TABLE IA_Component_Xref IS
    'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_Component_Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Component_Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Component_Xref.dbRelease IS
    'Highest release DECIMAL of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_Component_Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_Component_Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_Component_Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_Component_Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Component_Xref.ac IS
    'Unique auto-generated accession DECIMAL.';
    COMMENT ON COLUMN IA_Component_Xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Component_Xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Component_Xref.userstamp IS
    'Database user who has performed the last update of the column.';




CREATE TABLE IA_Component2Annot
(       component_ac            VARCHAR(30)    NOT NULL CONSTRAINT fk_Component2Annot$feature REFERENCES IA_Component(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR(30)    NOT NULL CONSTRAINT fk_Component2Annot$annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
)

;

ALTER TABLE IA_Component2Annot
 ADD CONSTRAINT     pk_Component2Annot
        PRIMARY KEY  (component_ac, annotation_ac)        
;


    COMMENT ON TABLE IA_Component2Annot IS
    'Component2Annot. Link table from Annotation to Component.';
    COMMENT ON COLUMN IA_Component2Annot.component_ac IS
    'Refers to a Feature to which the Annotation is linked.';
    COMMENT ON COLUMN IA_Component2Annot.annotation_ac IS
    'Refers to the annotation object linked to the component.';

