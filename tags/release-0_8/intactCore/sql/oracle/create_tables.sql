set doc off

/**************************************************************************************************************************

  Package:    IntAct

  Purpose:    Create Oracle components for IntAct

  Usage:      sqlplus username/password @create_tables.sql


  $Date$

  $Locker$


  **************************************************************************************************************************/


PROMPT Creating table "IA_Institution"
CREATE TABLE IA_Institution
(     ac                      VARCHAR2(30)    NOT NULL
                                              CONSTRAINT pk_Institution
                                              PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    , created                 DATE            DEFAULT  SYSDATE NOT NULL
    , updated                 DATE            DEFAULT  SYSDATE NOT NULL
    , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
    , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    , owner_ac                VARCHAR2(30)    CONSTRAINT fk_Institution$owner REFERENCES IA_Institution(ac)
    , shortLabel              VARCHAR2(20)
    , fullName                VARCHAR2(250)
    , postalAddress           VARCHAR2(2000)
    , url                     VARCHAR2(255)
)
TABLESPACE &&intactMainTablespace
PCTFREE    15
;

-- too small a table ? CREATE INDEX i_Institution$shortLabel on Institution(shortLabel);

set term off
    COMMENT ON TABLE IA_Institution IS
    'Institution e.g. a university company etc.';
    COMMENT ON COLUMN IA_Institution.shortLabel IS
    'A short string identifying the object not necessarily unique. Could be e.g. a gene name. ';
    COMMENT ON COLUMN IA_Institution.fullName IS
    'The full name of the object.';
    COMMENT ON COLUMN IA_Institution.postalAddress IS
    'The postal address. Contains line breaks for formatting.';
    COMMENT ON COLUMN IA_Institution.url IS
    'The URL of the entry page of the institution.';
    COMMENT ON COLUMN IA_Institution.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Institution.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Institution.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Institution.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Institution.userstamp IS
    'Database user who has performed the last update of the column.';
set term on



/* The ControlledVocab table is the master table which will be used by
all controlled vocabularies. */

PROMPT Creating table "IA_ControlledVocab"
CREATE TABLE IA_ControlledVocab
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_ControlledVocab
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_ControlledVocab$owner REFERENCES IA_Institution(ac)
     ,  objClass                VARCHAR2(255)
     ,  shortLabel              VARCHAR2(20)
     ,  fullName                VARCHAR2(250)
)
TABLESPACE &&intactMainTablespace
;

CREATE INDEX i_ControlledVocab$shortLabel on IA_ControlledVocab(shortLabel) TABLESPACE &&intactIndexTablespace
;
CREATE UNIQUE INDEX uq_CVocab$objClass_ShortLabel on IA_ControlledVocab(objClass,shortLabel) TABLESPACE &&intactIndexTablespace
;

set term off
    COMMENT ON TABLE IA_ControlledVocab IS
    'Master table for all controlled vocabularies.';
    COMMENT ON COLUMN IA_ControlledVocab.objClass IS
    'The fully qualified classname of the object. This is needed for the OR mapping.';
    COMMENT ON COLUMN IA_ControlledVocab.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_ControlledVocab.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_ControlledVocab.shortLabel IS
    'A short version of the term. Used e.g. in selection lists. ';
    COMMENT ON COLUMN IA_ControlledVocab.fullName IS
    'The full descriptive term. ';
    COMMENT ON COLUMN IA_ControlledVocab.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_ControlledVocab.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_ControlledVocab.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_ControlledVocab.userstamp IS
    'Database user who has performed the last update of the column.';
set term on

PROMPT Creating table "IA_BioSource"
CREATE TABLE IA_BioSource
(         ac                      VARCHAR2(30)    NOT NULL
                                                  CONSTRAINT pk_BioSource
                                                  PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
        , created                 DATE            DEFAULT  SYSDATE NOT NULL
        , updated                 DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
        , taxId                   VARCHAR(30)     			 NOT NULL
        , owner_ac                VARCHAR2(30)    CONSTRAINT fk_BioSource$owner REFERENCES IA_Institution(ac)
        , shortLabel              VARCHAR2(20)
        , fullName                VARCHAR2(250)
        , tissue_ac               VARCHAR2(30)    CONSTRAINT fk_Biosource$tissue REFERENCES IA_ControlledVocab(ac)
        , celltype_ac             VARCHAR2(30)    CONSTRAINT fk_Biosource$celltype REFERENCES IA_ControlledVocab(ac)
)
TABLESPACE &&intactMainTablespace
;

set term off
    COMMENT ON TABLE IA_BioSource IS
    'BioSource normally some kind of organism. ';
    COMMENT ON COLUMN IA_BioSource.taxId IS
    'The NCBI tax ID.';
    COMMENT ON COLUMN IA_BioSource.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_BioSource.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_BioSource.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_BioSource.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_BioSource.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_BioSource.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_BioSource.tissue_ac IS
    'Refers to the tissue (Controlled Vocabulary) of the biosource.';
    COMMENT ON COLUMN IA_BioSource.Celltype_ac IS
    'Refers to the cell type (Controlled Vocabulary) of the biosource.';
set term on




/* This is the key table. The class Interactor is the parent class of
   a class hierarchy which comprises all molecular objects. All
   subclasses are mapped to this table. It's likely to be the largest and
   most queried, hence most performance-critical table.
*/

PROMPT Creating table "IA_Interactor"
CREATE TABLE IA_Interactor
(         ac                    VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_Interactor
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated            NUMBER(1)       DEFAULT  0       NOT NULL
        , created               DATE            DEFAULT  SYSDATE NOT NULL
        , updated               DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp             DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp             VARCHAR2(30)    DEFAULT  USER    NOT NULL
        /* Column belonging to Interaction */
        , kD                    FLOAT
        /* Columns belonging to Protein */
        , crc64                 VARCHAR2(16)
        , formOf                VARCHAR2(30)    CONSTRAINT fk_Interactor$formOf REFERENCES IA_Interactor(ac)
        , proteinForm_ac        VARCHAR2(30)    CONSTRAINT fk_Interactor$proteinForm_ac REFERENCES IA_ControlledVocab(ac)
        /* Colums belonging to Interactor */
        , objClass              VARCHAR2(255)
        , bioSource_ac          VARCHAR2(30)    CONSTRAINT fk_Interactor$bioSource REFERENCES IA_BioSource(ac)
        , interactionType_ac    VARCHAR2(30)    CONSTRAINT fk_Interactor$interactionType REFERENCES IA_ControlledVocab(ac)
        /* Colums belonging to AnnotatedObject */
        , shortLabel            VARCHAR2(20)
        , fullName              VARCHAR2(250)
        /* Colums belonging to BasicObject */
        , owner_ac              VARCHAR2(30)    CONSTRAINT fk_Interactor$owner REFERENCES IA_Institution(ac)
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_Interactor$crc64 on IA_Interactor(crc64) TABLESPACE &&intactIndexTablespace;
CREATE index i_Interactor$bioSource_ac on IA_Interactor(bioSource_ac) TABLESPACE &&intactIndexTablespace;
CREATE index i_Interactor$shortLabel on IA_Interactor(shortLabel) TABLESPACE &&intactIndexTablespace;
CREATE index i_Interactor$fullName on IA_Interactor(fullName) TABLESPACE &&intactIndexTablespace;
CREATE index i_Interactor$formOf on IA_Interactor(formOf) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_Interactor IS
    'Interactor. Key table. All subclasses of Interactor are mapped to it, too.';
    COMMENT ON COLUMN IA_Interactor.kD IS
    'Dissociation constant of an Interaction';
    COMMENT ON COLUMN IA_Interactor.crc64 IS
    'CRC64 checksum of the polymerSequence. Stored in hexadecimal, not integer format.';
    COMMENT ON COLUMN IA_Interactor.formOf IS
    'REFERENCES IA_another Protein which the current one is a form of. Example: A fragment.';
    COMMENT ON COLUMN IA_Interactor.objClass IS
    'The fully qualified classname of the object. This is needed for the OR mapping.';
    COMMENT ON COLUMN IA_Interactor.bioSource_ac IS
    'The biological system in which the Interactor is found.';
    COMMENT ON COLUMN IA_Interactor.interactionType_ac IS
    'The kind of interaction, e.g. covalent binding.';
    COMMENT ON COLUMN IA_Interactor.shortLabel IS
    'A short string identifying the object, not necessarily unique. Could be e.g. a gene name. Used e.g. in selection lists. ';
    COMMENT ON COLUMN IA_Interactor.fullName IS
    'The full name of the object. ';
    COMMENT ON COLUMN IA_Interactor.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Interactor.ac IS
    'Unique, auto-generated accession number.';
    COMMENT ON COLUMN IA_Interactor.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Interactor.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Interactor.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Interactor.userstamp IS
    'Database user who has performed the last update of the column.';
set term on


/* This is a table where we store chunks of sequence so there is no reason for a CLOB; the CLOB usage is limited in OJB.
   Index organization gives fast access to table data for queries involving exact match and/or range search on a primary key
   and in this case we always access the table by interactor_ac.
 */

PROMPT Creating table "IA_Sequence_Chunk"
CREATE TABLE IA_Sequence_Chunk
(       ac                      VARCHAR (30)    NOT NULL
                                                CONSTRAINT pk_Sequence_Chunk
                                                PRIMARY KEY  USING INDEX TABLESPACE &&intactIndexTablespace
     ,  timestamp               DATE            DEFAULT  SYSDATE   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  parent_ac               VARCHAR (30)    NOT NULL
                                                CONSTRAINT fk_Sequence_chunk_parent_ac REFERENCES IA_Interactor(ac)
                                                ON DELETE CASCADE
     ,  sequence_chunk          VARCHAR (1000)
     ,  sequence_index          DECIMAL (3)
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_Sequence_chunk_parent_ac on IA_Sequence_Chunk(parent_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_Sequence_Chunk IS
    'Sequence chunks; to avoid CLOB columns the sequences are split into chunks of 1000 characters.';
    COMMENT ON COLUMN IA_Sequence_Chunk.ac IS
    'chunk unique identifier.';
    COMMENT ON COLUMN IA_Sequence_Chunk.parent_ac IS
    'Refers to the Interactor to which this bit of sequence belongs.';
    COMMENT ON COLUMN IA_Sequence_Chunk.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Sequence_Chunk.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Sequence_Chunk.sequence_chunk IS
    '1000 charcacters max size Sequence chunk';
    COMMENT ON COLUMN IA_Sequence_Chunk.sequence_index IS
    'Order of the chunk within the sequence of the Interactor.';
set term on



PROMPT Creating table "IA_Component"
CREATE TABLE IA_Component
(         ac                      VARCHAR2(30)    NOT NULL
                                                  CONSTRAINT pk_Component
                                                  PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
        , created                 DATE            DEFAULT  SYSDATE NOT NULL
        , updated                 DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
        , interactor_ac           VARCHAR2(30)    CONSTRAINT fk_Component$interactor REFERENCES IA_Interactor(ac)  ON DELETE CASCADE
        , interaction_ac          VARCHAR2(30)    CONSTRAINT fk_Component$interaction REFERENCES IA_Interactor(ac) ON DELETE CASCADE
        , role                    VARCHAR2(30)    CONSTRAINT fk_Component$role REFERENCES IA_ControlledVocab(ac)
        , expressedIn_ac          VARCHAR2(30)    CONSTRAINT fk_Component$expressedIn REFERENCES IA_BioSource(ac)
        , owner_ac                VARCHAR2(30)    CONSTRAINT fk_Component$owner REFERENCES IA_Institution(ac)
        , stoichiometry           NUMBER(4,1)
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_Component$interaction_ac on IA_Component(interaction_ac) TABLESPACE &&intactIndexTablespace;
CREATE index i_Component$interactor_ac on IA_Component(interactor_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE IA_Component IS
    'Component. Link table from Interaction to Interactor. A Component is a particular instance of an Interactor which participates in an Interaction.';
    COMMENT ON COLUMN IA_Component.stoichiometry IS
    'Relative quantity of the Component participating in the Interaction.';
    COMMENT ON COLUMN IA_Component.interactor_ac IS
    'Refers to the Interactor which is participating in the Interaction.';
    COMMENT ON COLUMN IA_Component.interaction_ac IS
    'Refers to the Interaction in which the Interactor is participating.';
    COMMENT ON COLUMN IA_Component.role IS
    'The role of the Interactor in the Interaction. This is usually characterised by the experimental method. Examples: bait prey in Yeast 2-hybrid experiments.';
    COMMENT ON COLUMN IA_Component.expressedIn_ac IS
    'The biological system in which the protein has been expressed.';
    COMMENT ON COLUMN IA_Component.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Component.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Component.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Component.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Component.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Component.userstamp IS
    'Database user who has performed the last update of the column.';
set term off



PROMPT Creating table "IA_Annotation"
CREATE TABLE IA_Annotation
(         ac                      VARCHAR2(30)    NOT NULL
                                                  CONSTRAINT pk_Annotation
                                                  PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
        , created                 DATE            DEFAULT  SYSDATE NOT NULL
        , updated                 DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
        , topic_ac                VARCHAR2(30)    CONSTRAINT fk_Annotation$topic REFERENCES IA_ControlledVocab(ac)
        , owner_ac                VARCHAR2(30)    CONSTRAINT fk_Annotation$owner REFERENCES IA_Institution(ac)
        , description             VARCHAR2(4000)
)
TABLESPACE &&intactMainTablespace
;

-- too small a table ? CREATE index i_Annotation$topic on Annotation(topic_ac);

set term off
    COMMENT ON TABLE IA_Annotation IS
    'Contains the main biological annotation of the object.';
    COMMENT ON COLUMN IA_Annotation.description IS
    'The free text description of the annotation item.';
    COMMENT ON COLUMN IA_Annotation.topic_ac IS
    'Refers to the topic of the annotation item.';
    COMMENT ON COLUMN IA_Annotation.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Annotation.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Annotation.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Annotation.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Annotation.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Annotation.userstamp IS
    'Database user who has performed the last update of the column.';
set term on


PROMPT Creating table "IA_Experiment"
CREATE TABLE IA_Experiment
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_Experiment
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
      , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
      , created                 DATE            DEFAULT  SYSDATE NOT NULL
      , updated                 DATE            DEFAULT  SYSDATE NOT NULL
      , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
      , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
      , bioSource_ac            VARCHAR2(30)    CONSTRAINT fk_Experiment$bioSource REFERENCES IA_BioSource(ac)
      , detectMethod_ac         VARCHAR2(30)    CONSTRAINT fk_Experiment$detectMethod REFERENCES IA_ControlledVocab(ac)
      , identMethod_ac          VARCHAR2(30)    CONSTRAINT fk_Experiment$identMethod REFERENCES IA_ControlledVocab(ac)
      , relatedExperiment_ac    VARCHAR2(30)    CONSTRAINT fk_Experiment$relatedExp REFERENCES IA_Experiment(ac)
      , owner_ac                VARCHAR2(30)    CONSTRAINT fk_Experiment$owner REFERENCES IA_Institution(ac)
      , shortLabel              VARCHAR2(20)
      , fullName                VARCHAR2(250)
)
TABLESPACE &&intactMainTablespace
;

-- too small a table ? CREATE INDEX i_Experiment$shortLabel on Experiment(shortLabel);

set term off
    COMMENT ON TABLE IA_Experiment IS
    'Describes the experiment which has yielded information about Interactions';
    COMMENT ON COLUMN IA_Experiment.bioSource_ac IS
    'The biological system in which the experiment has been performed.';
    COMMENT ON COLUMN IA_Experiment.identMethod_ac IS
    'Refers to the method by which the Interactor has been detected as a participant in the Interaction.';
    COMMENT ON COLUMN IA_Experiment.detectMethod_ac IS
    'Refers to the method by which the interactions have been detected in the experiment.';
    COMMENT ON COLUMN IA_Experiment.detectMethod_ac IS
    'Refers to the method by which the interactions have been detected in the experiment.';
    COMMENT ON COLUMN IA_Experiment.relatedExperiment_ac IS
    'An experiment which is related to the current experiment. This serves just as a pointer all information on the type of relationship will be given in the annotation of the experiment.';
    COMMENT ON COLUMN IA_Experiment.fullName IS
    'The full name of the object. ';
    COMMENT ON COLUMN IA_Experiment.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Experiment.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Experiment.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Experiment.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Experiment.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Experiment.userstamp IS
    'Database user who has performed the last update of the column.';
set term on




PROMPT Creating table "IA_Xref"
CREATE TABLE IA_Xref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_Xref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_Xref$qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_Xref$database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    -- eh missing constraint here??
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_Xref$owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_Xref$parent_ac on IA_Xref(parent_ac) TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE IA_Xref IS
    'Represents a crossreference. Several objects may have crossREFERENCES IA_e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT ON COLUMN IA_Xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT ON COLUMN IA_Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT ON COLUMN IA_Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT ON COLUMN IA_Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT ON COLUMN IA_Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT ON COLUMN IA_Xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Xref.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Xref.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Xref.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off


/* The IntactNode table is the table which will be used for
storing informations about servers. */

PROMPT Creating table "IA_IntactNode"
CREATE TABLE IA_IntactNode
(       ac                          VARCHAR2(30) NOT NULL
                                    CONSTRAINT pk_IntactNode
                                    PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
      , lastCheckId                 NUMBER(5)      DEFAULT  0              NOT NULL
      , lastProvidedId              NUMBER(5)      DEFAULT  0              NOT NULL
      , lastProvidedDate            Date           DEFAULT  TO_DATE('01011970','DDMMYYYY')  NOT NULL
      , rejected                    NUMBER(1)      DEFAULT  0              NOT NULL
      , created                     DATE           DEFAULT  SYSDATE        NOT NULL
      , updated                     DATE           DEFAULT  SYSDATE        NOT NULL
      , timestamp                   DATE           DEFAULT  SYSDATE        NOT NULL
      , userstamp                   VARCHAR2(30)   DEFAULT  USER           NOT NULL
      , deprecated                  NUMBER(1)      DEFAULT  0              NOT NULL
      , ownerPrefix                 VARCHAR2(30)   DEFAULT  USER           NOT NULL
      , owner_ac                    VARCHAR2(30)   CONSTRAINT fk_IntactNode$owner REFERENCES IA_Institution(ac)
      , ftpAddress                  VARCHAR2(255)
      , ftpLogin                    VARCHAR2(255)
      , ftpPassword                 VARCHAR2(255)
      , ftpDirectory                VARCHAR2(255)
)
TABLESPACE &&intactMainTablespace
;




PROMPT Creating table "IA_Int2Exp"
CREATE TABLE IA_Int2Exp
(       interaction_ac          VARCHAR2(30)    NOT NULL CONSTRAINT fk_Int2Exp$interaction REFERENCES IA_Interactor(ac)  ON DELETE CASCADE
      , experiment_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Int2Exp$experiment  REFERENCES IA_Experiment(ac)  ON DELETE CASCADE
      , deprecated              NUMBER(1)       DEFAULT    0       NOT NULL
      , created                 DATE            DEFAULT    SYSDATE NOT NULL
      , userstamp               VARCHAR2(30)    DEFAULT    USER    NOT NULL
      , updated                 DATE            DEFAULT    SYSDATE NOT NULL
      , timestamp               DATE            DEFAULT    SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'IA_Int2Exp'
ALTER TABLE IA_Int2Exp
 ADD (CONSTRAINT     pk_Int2Exp
        PRIMARY KEY  (interaction_ac, experiment_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;


set term off
    COMMENT ON TABLE IA_Int2Exp IS
    'Link table from Interaction to Experiment.';
    COMMENT ON COLUMN IA_Int2Exp.interaction_ac IS
    'Refers to an Interation derived from an Experiment.';
    COMMENT ON COLUMN IA_Int2Exp.experiment_ac IS
    'Refers to an Experiment.';
    COMMENT ON COLUMN IA_Int2Exp.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Int2Exp.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Int2Exp.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Int2Exp.timestamp IS
    'Date of the last update of the column.';
set term on



PROMPT Creating table "IA_Int2Annot"
CREATE TABLE IA_Int2Annot
(       interactor_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Int2Annot$interactor REFERENCES IA_Interactor(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Int2Annot$annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'IA_Int2Annot'
ALTER TABLE IA_Int2Annot
 ADD (CONSTRAINT     pk_Int2Annot
        PRIMARY KEY  (interactor_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;

set term off
    COMMENT ON TABLE IA_Int2Annot IS
    'Int2Annot. Link table from Annotation to Interactor.';
    COMMENT ON COLUMN IA_Int2Annot.interactor_ac IS
    'Refers to an Interactor to which the Annotation is linked.';
    COMMENT ON COLUMN IA_Int2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Interactor.';
    COMMENT ON COLUMN IA_Int2Annot.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Int2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Int2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Int2Annot.timestamp IS
    'Date of the last update of the column.';
set term on


PROMPT Creating table "IA_Exp2Annot"
CREATE TABLE IA_Exp2Annot
(       experiment_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Exp2Annot$experiment REFERENCES IA_Experiment(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Exp2Annot$annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'IA_Exp2Annot'
ALTER TABLE IA_Exp2Annot
 ADD (CONSTRAINT     pk_Exp2Annot
        PRIMARY KEY  (experiment_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;

set term off
    COMMENT ON TABLE IA_Exp2Annot IS
    'Exp2Annot. Link table from Annotation to Experiment.';
    COMMENT ON COLUMN IA_Exp2Annot.Experiment_ac IS
    'Refers to an Experiment to which the Annotation is linked.';
    COMMENT ON COLUMN IA_Exp2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Experiment.';
    COMMENT ON COLUMN IA_Exp2Annot.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Exp2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Exp2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Exp2Annot.timestamp IS
    'Date of the last update of the column.';
set term on



PROMPT Creating table "IA_cvobject2Annot"
CREATE TABLE IA_cvobject2Annot
(       cvobject_ac             VARCHAR2(30)    NOT NULL CONSTRAINT fk_cvobj2Annot$cvobject   REFERENCES IA_ControlledVocab(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_cvobj2Annot$annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'IA_cvobject2Annot'
ALTER TABLE IA_cvobject2Annot
 ADD (CONSTRAINT     pk_cvobject2Annot
        PRIMARY KEY  (cvobject_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
/

set term off
    COMMENT ON TABLE IA_cvobject2Annot IS
    'cvobject2Annot. Link table from Annotation to Controlled vocabulary.';
    COMMENT ON COLUMN IA_cvobject2Annot.cvobject_ac IS
    'Refers to an Controlled vocabulary to which the Annotation is linked.';
    COMMENT ON COLUMN IA_cvobject2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Controlled vocabulary.';
    COMMENT ON COLUMN IA_cvobject2Annot.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_cvobject2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_cvobject2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_cvobject2Annot.timestamp IS
    'Date of the last update of the column.';
set term on


PROMPT Creating table "IA_Biosource2Annot"
CREATE TABLE IA_Biosource2Annot
(       biosource_ac            VARCHAR2(30)    NOT NULL CONSTRAINT fk_bio2Annot$biosource   REFERENCES IA_Biosource(ac)  ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_bio2Annot$annotation  REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'IA_Biosource2Annot'
ALTER TABLE IA_Biosource2Annot
 ADD (CONSTRAINT     pk_Biosource2Annot
        PRIMARY KEY  (biosource_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;

set term off
    COMMENT ON TABLE IA_Biosource2Annot IS
    'Biosource2Annot. Link table from Annotation to Biosource.';
    COMMENT ON COLUMN IA_Biosource2Annot.Biosource_ac IS
    'Refers to a Biosource to which the Annotation is linked.';
    COMMENT ON COLUMN IA_Biosource2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Biosource.';
    COMMENT ON COLUMN IA_Biosource2Annot.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Biosource2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Biosource2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Biosource2Annot.timestamp IS
    'Date of the last update of the column.';
set term on



/* The relation table which establishes a graph structure between CV objects */
PROMPT Creating table "IA_Cv2Cv"
CREATE TABLE IA_Cv2Cv
(
	    parent_ac		            VARCHAR2(30)	CONSTRAINT fk_Cv2Cv$parent  REFERENCES IA_ControlledVocab(ac) ON DELETE CASCADE
	 ,  child_ac		            VARCHAR2(30)	CONSTRAINT fk_Cv2Cv$child	REFERENCES IA_ControlledVocab(ac) ON DELETE CASCADE
     ,  deprecated                  NUMBER(1)       DEFAULT  0       NOT NULL
	 ,  created			            DATE		    DEFAULT  sysdate NOT NULL
	 ,  updated			            DATE		    DEFAULT  sysdate NOT NULL
	 ,  timestamp		            DATE		    DEFAULT  sysdate NOT NULL
	 ,  userstamp		            VARCHAR2(30)	DEFAULT	 USER	 NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'IA_Cv2Cv'
ALTER TABLE IA_Cv2Cv
 ADD (CONSTRAINT     pk_Cv2Cv
        PRIMARY KEY  (parent_ac, child_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;



/* No indexes defined yet. */

COMMENT ON TABLE IA_Cv2Cv IS
'Cv2Cv. Link table to establish a Directed Acyclic Graph (DAG) structure for CVs.';
COMMENT ON COLUMN IA_Cv2Cv.parent_ac IS
'Refers to the parent object.';
COMMENT ON COLUMN IA_Cv2Cv.child_ac IS
'Refers to the child term.';
COMMENT ON COLUMN IA_Cv2Cv.created IS
'Date of the creation of the row.';
COMMENT ON COLUMN IA_Cv2Cv.updated IS
'Date of the last update of the row.';
COMMENT ON COLUMN IA_Cv2Cv.timestamp IS
'Date of the last update of the column.';
COMMENT ON COLUMN IA_Cv2Cv.userstamp IS
'Database user who has performed the last update of the column.';


-- Sequences
PROMPT creating sequence Intact_ac
CREATE SEQUENCE Intact_ac start with 10;




-- components related to statisticView

-- Sequence for table's AC
PROMPT Creating sequence "Intact_statistics_seq"
CREATE SEQUENCE Intact_statistics_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;


PROMPT Creating table "IA_Statistics"
CREATE TABLE IA_Statistics
(     ac                       NUMBER(9) NOT NULL
                               CONSTRAINT pk_Statistics
                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
      , timestamp              DATE            DEFAULT  SYSDATE NOT NULL
      , protein_number         NUMBER(9)      DEFAULT  0       NOT NULL
      , interaction_number     NUMBER(9)      DEFAULT  0       NOT NULL
      , binary_interactions    NUMBER(9)      DEFAULT  0       NOT NULL
      , complex_interactions   NUMBER(9)      DEFAULT  0       NOT NULL
      , experiment_number      NUMBER(9)      DEFAULT  0       NOT NULL
      , term_number            NUMBER(9)      DEFAULT  0       NOT NULL
)
TABLESPACE &&intactIndexTablespace;


COMMENT ON TABLE IA_Statistics IS
'IA_Statistics. Stores statistics about this intact node.';
COMMENT ON COLUMN IA_Statistics.timestamp IS
'remind the moment of record for this line';
COMMENT ON COLUMN IA_Statistics.protein_number IS
'count how many proteins are stored in the database';
COMMENT ON COLUMN IA_Statistics.interaction_number IS
'count how many interactions are referred in the database';
COMMENT ON COLUMN IA_Statistics.binary_interactions IS
'how many interactions contain only 2 interactors';
COMMENT ON COLUMN IA_Statistics.complex_interactions IS
'how many interactions contain more than 2 interactors';
COMMENT ON COLUMN IA_Statistics.experiment_number IS
'how many different experiments are stored in the database';
COMMENT ON COLUMN IA_Statistics.term_number IS
'how many different controlled vocabularies terms are stored in the database';


-- Create the trigger to update the table AC
PROMPT Creating trigger "TRG_IA_Statistics"
CREATE OR REPLACE TRIGGER TRG_IA_Statistics
BEFORE INSERT
ON IA_Statistics
FOR EACH ROW
BEGIN
  select Intact_statistics_seq.nextval
  into :new.ac
  from dual;
END;
/





-- Create tables for GO Density :


PROMPT Creating table "ia_goDens_binary"
CREATE TABLE ia_goDens_binary
(   bait            VARCHAR2(10)        NOT NULL
    ,prey           VARCHAR2(10)        NOT NULL
    ,goBait         VARCHAR2(10)        NOT NULL
    ,goPrey         VARCHAR2(10)        NOT NULL
    ,PRIMARY KEY (bait, prey, goBait, goPrey) USING INDEX TABLESPACE &&intactIndexTablespace
)
TABLESPACE &&intactMainTablespace
PCTFREE    15
;

CREATE INDEX igoBaitkey ON ia_goDens_binary (goBait) reverse TABLESPACE &&intactIndexTablespace
;

CREATE INDEX igoPreykey ON ia_goDens_binary (goPrey) reverse TABLESPACE &&intactIndexTablespace
;



PROMPT Creating table "ia_goDens_GoDag"
CREATE TABLE ia_goDens_GoDag ( 
    parent          VARCHAR2(10)
    ,child          VARCHAR2(10)
    ,parentDepth    INTEGER             NOT NULL
    ,childDepth     INTEGER             NOT NULL
    ,PRIMARY KEY (parent, child) USING INDEX TABLESPACE &&intactIndexTablespace
 ) 
TABLESPACE &&intactMainTablespace
PCTFREE    15
;

CREATE INDEX iGoParent ON ia_goDens_GoDag (parent) reverse TABLESPACE &&intactIndexTablespace
;

CREATE INDEX iGoChild ON ia_goDens_GoDag (child) reverse TABLESPACE &&intactIndexTablespace
;



PROMPT Creating table "ia_goDens_GoDagDenorm"
CREATE TABLE ia_goDens_GoDagDenorm (
    parent          VARCHAR2(10)
    ,child          VARCHAR2(10)
    ,PRIMARY KEY (parent, child) USING INDEX TABLESPACE &&intactIndexTablespace
)
TABLESPACE &&intactMainTablespace
PCTFREE    15
;

CREATE INDEX iGoParentDenorm ON ia_goDens_GoDagDenorm (parent) reverse TABLESPACE &&intactIndexTablespace
;



PROMPT Creating table "ia_goDens_GoProt"
CREATE TABLE ia_goDens_GoProt (
    goid            VARCHAR2(10)
    ,interactor     VARCHAR2(10)
    ,PRIMARY KEY (goid, interactor) USING INDEX TABLESPACE &&intactIndexTablespace
)
TABLESPACE &&intactMainTablespace 
PCTFREE    15
;

CREATE INDEX iGoId ON ia_goDens_GoProt (goid) reverse TABLESPACE &&intactIndexTablespace
;



PROMPT Creating table "ia_goDens_density"
CREATE TABLE ia_goDens_density ( 
    goid1           VARCHAR2(10)        NOT NULL
    ,goid2          VARCHAR2(10)        NOT NULL
    ,pos_IA         INTEGER             NOT NULL
    ,is_IA          INTEGER             NOT NULL
    ,PRIMARY KEY (goid1, goid2) USING INDEX TABLESPACE &&intactIndexTablespace
)
TABLESPACE &&intactMainTablespace
PCTFREE    15
;

CREATE INDEX igoId1 ON ia_goDens_density(goid1) reverse TABLESPACE &&intactIndexTablespace
;
CREATE INDEX igoId2 ON ia_goDens_density(goid2) reverse TABLESPACE &&intactIndexTablespace
;



-----------
-- Alias --
-----------

PROMPT Creating table "IA_alias"
CREATE TABLE IA_alias
(       ac                      VARCHAR2(30)   NOT NULL
                                               CONSTRAINT pk_alias
                                               PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
    ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
    ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac            VARCHAR2(30)    CONSTRAINT fk_alias$qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac               VARCHAR2(30)    -- eh missing constraint here??
    ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_alias$owner REFERENCES IA_Institution(ac)
    ,  name                       VARCHAR2(30)
)
TABLESPACE &&intactIndexTablespace
;

CREATE index i_Alias$parent_ac on IA_Alias(parent_ac) TABLESPACE &&intactIndexTablespace
;
 
set term off
    COMMENT ON TABLE IA_Alias IS
    'Represents an alias. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT ON COLUMN IA_Alias.aliastype_ac IS
    'Type of the alias. ac found in the IA_ControlledVocab table.';
    COMMENT ON COLUMN IA_Alias.name IS
    'Name of the alias.';
    COMMENT ON COLUMN IA_Alias.parent_ac IS
    'Refers to the parent object this alias describes.';
    COMMENT ON COLUMN IA_Alias.owner_ac IS
    'Refers to the owner of this object.';
    COMMENT ON COLUMN IA_Alias.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN IA_Alias.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Alias.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Alias.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Alias.userstamp IS
    'Database user who has performed the last update of the column.';
set term on


PROMPT Creating table "IA_PAYG"
CREATE TABLE IA_PAYG(
      NID                 VARCHAR2(20) NOT NULL 
    , SPECIES             VARCHAR2(30) NOT NULL
    , BAIT                INTEGER
    , PREY                INTEGER
    , INDEGREE            NUMBER(6)
    , OUTDEGREE           NUMBER(6)
    , QDEGREE             FLOAT
    , ESEEN               INTEGER
    , ECONF               INTEGER
    , REALLY_USED_AS_BAIT CHAR(1)
)
TABLESPACE &&intactMainTablespace
PCTFREE    15
;


PROMPT Creating composite primary Key on 'IA_PAYG'
ALTER TABLE IA_PAYG
 ADD (CONSTRAINT     PK_IA_PAYG
        PRIMARY KEY  (NID, SPECIES)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;


set term off
   COMMENT ON TABLE IA_Payg IS
   'Table where the pay-as-you-go algorithm is executed for each species.';
   COMMENT ON COLUMN IA_Payg.nID IS
   'The Id for the node.';
   COMMENT ON COLUMN IA_Payg.bait IS
   'At what step no. the nID was used as a bait.';
   COMMENT ON COLUMN IA_Payg.prey IS
   'At what step no. the nID was first seen as prey.';
   COMMENT ON COLUMN IA_Payg.indegree IS
   'The number of times this nID has been seen as prey.';
   COMMENT ON COLUMN IA_Payg.outdegree IS
   'The number of prey associated with this nID.';
   COMMENT ON COLUMN IA_Payg.qdegree IS
   'Used as a tie-breaker in the event two or more nIDs have the same and highest indegree.';
   COMMENT ON COLUMN IA_Payg.eseen IS
   'Running totals of how many interactions have been seen.';
   COMMENT ON COLUMN IA_Payg.econf IS
   'Running totals of how many interactions have been confirmed.';
   COMMENT ON COLUMN IA_Payg.really_used_as_bait IS
   '.';
   COMMENT ON COLUMN IA_Payg.species IS
   'The tax id of the bio source.';
set term on

--PROMPT Creating table "CURRENT_EDGE"
--CREATE TABLE CURRENT_EDGE(
--     NIDA VARCHAR2(20)
--    ,NIDB VARCHAR2(20)
--    ,SEEN INTEGER
--    ,CONF INTEGER
--    ,SPECIES VARCHAR(20)
--)
--TABLESPACE &&intactMainTablespace
--PCTFREE    15
--;
--
--PROMPT Creating table "TEMP_NODE"
--CREATE TABLE TEMP_NODE(
--     NID VARCHAR(20)
--    ,SPECIES VARCHAR2(20)
--)
--TABLESPACE &&intactMainTablespace
--PCTFREE    15
--;



set term on


/* ************************************************
  	Auxiliary tables for curation 
   ************************************************
*/

PROMPT Creating table "IA_PubMed"
CREATE TABLE IA_PubMed
(	created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  primaryid               VARCHAR2(30)
     ,  status                  VARCHAR2(30)
     ,  description             VARCHAR2(100)
)
TABLESPACE INTACT_TAB
;

set term on
    COMMENT ON TABLE IA_PUBMED IS
    'Stores information on papers in processing by their pubmed is.';
    COMMENT ON COLUMN IA_Pubmed.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Pubmed.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Pubmed.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Pubmed.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN IA_Pubmed.primaryid IS
    'Pubmed id of a paper.';
    COMMENT ON COLUMN IA_Pubmed.status IS
    'Status of the paper.';
    COMMENT ON COLUMN IA_Pubmed.description IS
    'Comment.';
set term off

/* Create a view on all known pubmed ids */
	  CREATE 
 OR REPLACE VIEW ia_paperstatus AS
 SELECT DISTINCT e.ac as experiment_Ac, 
                 e.shortlabel as Shortlabel, 
                 e.userstamp as userstamp, 
                 e.updated as updated, 
                 x.primaryid as pubmedid,
	         '-' as status,
		 '-' as description
            FROM ia_experiment e, ia_xref x, ia_controlledvocab cv
           WHERE cv.shortlabel='pubmed' 
             AND cv.ac=x.database_ac
             AND x.parent_ac=e.ac
           UNION
 SELECT DISTINCT '-' as experiment_ac,
		 '-' as shortlabel,
		 p.userstamp as userstamp,
                 p.updated as updated,
	         p.primaryid as pubmedid,
	         p.status as status,
		 p.description as description
            FROM ia_pubmed p
      	ORDER BY pubmedid asc, updated desc;

