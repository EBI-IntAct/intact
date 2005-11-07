set doc off

/**************************************************************************************************************************

  Package:    IntAct

  Purpose:    Update scripts to go from database version to database version.

  Usage:      sqlplus username/password @update_history.sql


  $Date$

  $Locker$


  **************************************************************************************************************************/

/* From 2004-02-29 to 2004-03-15 */

alter table ia_biosource
add (tissue_ac VARCHAR2(30) CONSTRAINT fk_Biosource$tissue REFERENCES   
IA_ControlledVocab(ac) )
;

alter table ia_biosource add ( celltype_ac VARCHAR2(30) CONSTRAINT
fk_Biosource$celltype REFERENCES IA_ControlledVocab(ac) )
;

/* Taxid is not anymore unique */
alter table ia_payg drop CONSTRAINT FK_PAYG_SPECIES;
alter table ia_biosource drop constraint uq_BioSource$taxId;

alter table ia_biosource_audit add (tissue_ac VARCHAR2(30))
;


alter table ia_biosource_audit add ( celltype_ac VARCHAR2(30))
;



CREATE OR REPLACE TRIGGER TRGAUD_IA_BIOSOURCE
 BEFORE UPDATE OR DELETE
 ON ia_biosource
 FOR EACH ROW
declare
        l_event char(1);
begin
        if deleting then
                l_event := 'D';
        elsif updating then
                l_event := 'U';
                :new.timestamp := sysdate;
                :new.userstamp := user;
        end if ;


        insert into ia_biosource_audit
                ( event
                , ac
                , deprecated
                , created
                , updated
                , timestamp
                , userstamp
                , taxid
                , owner_ac
                , shortlabel
                , fullname
                , tissue_ac
                , celltype_ac
                )
        values
                ( l_event
                , :old.ac
                , :old.deprecated
                , :old.created
                , :old.updated
                , :old.timestamp
                , :old.userstamp
                , :old.taxid
                , :old.owner_ac
                , :old.shortlabel
                , :old.fullname
                , :old.tissue_ac
                , :old.celltype_ac
                );
end;
/







/* From 2004-03-15 to 2004-06-14 - Extension of the schema: Feature */

/* This is a table where we store which features are linked to a component. */

PROMPT Creating table "IA_Feature"
CREATE TABLE IA_Feature
(         ac                    VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_Feature
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated            NUMBER(1)       DEFAULT  0       NOT NULL
        , created               DATE            DEFAULT  SYSDATE NOT NULL
        , updated               DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp             DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp             VARCHAR2(30)    DEFAULT  USER    NOT NULL
        , component_ac          VARCHAR2(30)    NOT NULL CONSTRAINT fk_Feature$component REFERENCES IA_Component(ac) ON DELETE CASCADE
        , identification_ac     VARCHAR2(30)    CONSTRAINT fk_Feature$identification_ac REFERENCES IA_ControlledVocab(ac)
        , featureType_ac        VARCHAR2(30)    CONSTRAINT fk_Feature$featureType_ac REFERENCES IA_ControlledVocab(ac)
        , linkedfeature_ac      VARCHAR2(30)    CONSTRAINT fk_Feature$feature REFERENCES IA_Feature(ac)
        , shortLabel            VARCHAR2(20)
        , fullName              VARCHAR2(250)
        , owner_ac              VARCHAR2(30)    CONSTRAINT fk_Feature$owner REFERENCES IA_Institution(ac)
)
TABLESPACE &&intactMainTablespace
;

CREATE INDEX i_Feature$component_ac on IA_Feature(component_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_Feature$linkedfeature_ac on IA_Feature(linkedfeature_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_Feature$identification_ac on IA_Feature(identification_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_Feature$featureType_ac on IA_Feature(featureType_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_Feature IS
    'Feature. Define a set of Ranges.';
    COMMENT ON COLUMN IA_Feature.ac IS
    'Unique, auto-generated accession number.';
    COMMENT ON COLUMN IA_Alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_Feature.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Feature.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Feature.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Feature.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Feature.fullName IS
    'The full name of the object. ';
    COMMENT ON COLUMN IA_Feature.shortlabel IS
    'The Shortlabel of the object. ';
    COMMENT ON COLUMN IA_Feature.component_ac IS
    'the component to which relates that feature.';
    COMMENT ON COLUMN IA_Feature.linkedfeature_ac IS
    'The feature that bind the one we are describing.';
set term on



/* This is a table where we store where is situated an interaction at the protein sequence level. */

PROMPT Creating table "IA_Range"
CREATE TABLE IA_Range
(         ac                    VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_Range
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated            NUMBER(1)       DEFAULT  0       NOT NULL
        , created               DATE            DEFAULT  SYSDATE NOT NULL
        , updated               DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp             DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp             VARCHAR2(30)    DEFAULT  USER    NOT NULL
        , undetermined          CHAR            NOT NULL CHECK ( undetermined IN ('N','Y') )
        , link                  CHAR            NOT NULL CHECK ( link IN ('N','Y') )
        , feature_ac            VARCHAR2(30)    NOT NULL CONSTRAINT fk_Range$feature REFERENCES IA_Feature(ac) ON DELETE CASCADE
        , owner_ac              VARCHAR2(30)    CONSTRAINT fk_Range$owner REFERENCES IA_Institution(ac)
        , fromIntervalStart     NUMBER(5)
        , fromIntervalEnd       NUMBER(5)
        , fromFuzzyType_ac      VARCHAR2(30)    CONSTRAINT fk_Range$fromFuzzyType_ac REFERENCES IA_ControlledVocab(ac)
        , toIntervalStart       NUMBER(5)
        , toIntervalEnd         NUMBER(5)
        , toFuzzyType_ac        VARCHAR2(30)    CONSTRAINT fk_Range$toFuzzyType_ac REFERENCES IA_ControlledVocab(ac)
        , sequence              VARCHAR(100)
)
TABLESPACE &&intactMainTablespace
;

CREATE INDEX i_Range$fromFuzzyType_ac on IA_Range(fromFuzzyType_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_Range$toFuzzyType_ac on IA_Range(toFuzzyType_ac) TABLESPACE &&intactIndexTablespace;
CREATE INDEX i_Range$feature_ac on IA_Range(feature_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_Range IS
    'Range. Represents a location on a sequence.';
    COMMENT ON COLUMN IA_Range.ac IS
    'Unique, auto-generated accession number.';
    COMMENT ON COLUMN IA_Alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_Range.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Range.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Range.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Range.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Range.fromIntervalStart IS
    'Lower bound of the interval start.';
    COMMENT ON COLUMN IA_Range.fromIntervalEnd IS
    'Higher bound of the interval start. Can be equal to the lower bound.';
    COMMENT ON COLUMN IA_Range.fromFuzzyType_ac IS
    'Defines a type of fuzzy range (before, after ...).';
    COMMENT ON COLUMN IA_Range.toIntervalStart IS
    'Lower bound of the interval end.';
    COMMENT ON COLUMN IA_Range.toIntervalEnd IS
    'Higher bound of the interval end. Can be equal to the lower bound';
    COMMENT ON COLUMN IA_Range.toFuzzyType_ac IS
    'Defines a type of fuzzy range (before, after ...).';
    COMMENT ON COLUMN IA_Range.sequence IS
    'The first 100 amino acid of the protein sequence that binds.';
    COMMENT ON COLUMN IA_Range.undetermined IS
    'Answer the question: does that range defines boundaries on the sequence?';
    COMMENT ON COLUMN IA_Range.link IS
    'Answer the question: does that range (from and to) are related to different location of the sequence that are interacting together ?';
set term on


/* Indirection table in which we stores which feature is linked to which annotations. */

PROMPT Creating table "IA_Feature2Annot"
CREATE TABLE IA_Feature2Annot
(       feature_ac              VARCHAR2(30)    NOT NULL CONSTRAINT fk_Feature2Annot$feature REFERENCES IA_Feature(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Feature2Annot$annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'IA_Feature2Annot'
ALTER TABLE IA_Feature2Annot
 ADD (CONSTRAINT     pk_Feature2Annot
        PRIMARY KEY  (feature_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;

set term off
    COMMENT ON TABLE IA_Feature2Annot IS
    'Feature2Annot. Link table from Annotation to Feature.';
    COMMENT ON COLUMN IA_Feature2Annot.feature_ac IS
    'Refers to a Feature to which the Annotation is linked.';
    COMMENT ON COLUMN IA_Feature2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Feature.';
    COMMENT ON COLUMN IA_Feature2Annot.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Feature2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Feature2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Feature2Annot.timestamp IS
    'Date of the last update of the column.';
set term on



/* From 2004-06-14 to 2004-07-16: Extension of the schema: MiNe application */

/* ************************************************
  	Interactions table for mine
   *************************************************/

PROMPT Creating table "IA_Interactions"
CREATE TABLE ia_interactions
(       protein1_ac     VARCHAR2(30)    CONSTRAINT fk_interactions$protein1_ac REFERENCES IA_Interactor(ac) ON DELETE CASCADE
      , shortlabel1     VARCHAR2(20)
      , protein2_ac     VARCHAR2(30)    CONSTRAINT fk_interactions$protein2_ac REFERENCES IA_Interactor(ac) ON DELETE CASCADE
      , shortlabel2     VARCHAR2(20)
      , taxid           VARCHAR2(30)
      , interaction_ac  VARCHAR2(30)    CONSTRAINT fk_interactions$interaction REFERENCES IA_Interactor(ac) ON DELETE CASCADE
      , weight          NUMBER(4,3)
      , graphid         INTEGER
)
TABLESPACE &&intactMainTablespace;

set term on
    COMMENT ON TABLE ia_interactions IS
    'Stores all binary interactions based on the content of ia_interactor, ia_component, ia_biosource. This is autogenerated.';
    COMMENT ON COLUMN ia_interactions.protein1_ac IS
    'One of the interacting partner.';
    COMMENT ON COLUMN ia_interactions.protein2_ac IS
    'The other interacting partner.';
    COMMENT ON COLUMN ia_interactions.taxid IS
    'BioSource taxid in which that interaction takes place.';
    COMMENT ON COLUMN ia_interactions.interaction_ac IS
    'interaction in which those two proteins interacts';
    COMMENT ON COLUMN ia_interactions.weight IS
    'Weight of that interraction.';
    COMMENT ON COLUMN ia_interactions.graphid IS
    'Graph in which that interraction takes place.';
set term off

                                                          s
/* ************************************************
   materialized view for search3
   *************************************************/


CREATE OR REPLACE MATERIALIZED VIEW IA_SEARCH (
    SELECT ac,
           shortlabel as value,
           objclass,
           'shortlabel' as type
       FROM ia_interactor
UNION
    SELECT ac,
       LOWER(fullname) as value,
       objclass,
       'fullname' as type
       FROM ia_interactor
UNION
    SELECT I.ac,
       X.primaryid as value,
       objclass,
       'xref' as type
       FROM ia_interactor I, ia_xref X
       WHERE I.ac = X.parent_ac
UNION
    SELECT I.ac,
       A.name as value,
       I.objclass,
       CV.shortlabel as type
       FROM ia_interactor I, ia_alias A, ia_controlledvocab CV
       WHERE I.ac = A.parent_ac and
             A.aliastype_ac = CV.ac
UNION
    SELECT E.ac,
       X.primaryid as value,
       'uk.ac.ebi.intact.model.Experiment',
       'xref' as type
       FROM ia_experiment E, ia_xref X
       WHERE E.ac = X.parent_ac
UNION
    SELECT ac,
       shortlabel as value,
       'uk.ac.ebi.intact.model.Experiment',
       'shortlabel' as type
       FROM ia_experiment
UNION
    SELECT ac,
       LOWER(fullname) as value,
       'uk.ac.ebi.intact.model.Experiment',
       'fullname' as type
       FROM ia_experiment
UNION
    SELECT ac,
       LOWER(fullname) as value,
       objclass,
       'fullname' as type
       FROM ia_controlledvocab
UNION
    SELECT ac,
       shortlabel as value,
       objclass,
       'shortlabel' as type
       FROM ia_controlledvocab
UNION
    SELECT CV.ac,
       X.primaryid as value,
       objclass,
       'xref' as type
       FROM ia_controlledvocab CV, ia_xref X
       WHERE CV.ac = X.parent_ac
UNION
    SELECT CV.ac,
       A.name as value,
       objclass,
       'alias' as type
       FROM ia_controlledvocab CV, ia_alias A


-- TO UPGRADE TO 1.1.0 see directory version_1_1_0 for scripts


