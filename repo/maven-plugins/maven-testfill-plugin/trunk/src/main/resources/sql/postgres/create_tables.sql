/**************************************************************************************************************************

  Package:    IntAct PostgreSQL DDL step 1

  Purpose:    Create PostgreSQL components (tables and sequence) for IntAct

  Usage:      Prepare :
              - connect to Postgres as an administrator using the utility 'psql'
              - create a user ('intact' for example) to own the intact components

              Run :
              - with psql, connect to Postgres using the intact account (or the account you created)
              - suppose this script resides in /tmp , then give this command in psql :
                     \i /tmp/create_tables.sql

              Evaluate:
              - You then have a set of Intact main tables,
                which you can verify by typing:
                     \dt


  $Date: 2006-04-05 17:19:19 +0100 (dc, 05 abr 2006) $
  $Auth: hhe / markr  $

  Change Log :
  26/03/2003   MR  : - changed composite primary key syntax to comply with version 7.0.x
                     - changed polymerSeq to datatype TEXT


  Copyright (c) 2003 The European Bioinformatics Institute, and others.
  All rights reserved. Please see the file LICENSE
  in the root directory of this distribution.

  **************************************************************************************************************************/


CREATE TABLE IA_Institution
(     ac                      VARCHAR  (30)    NOT NULL
                                               CONSTRAINT pk_Institution
                                               PRIMARY KEY
    , deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    , created                 TIMESTAMP        DEFAULT  now()   NOT NULL
    , updated                 TIMESTAMP        DEFAULT  now()   NOT NULL
    , TIMESTAMP               TIMESTAMP        DEFAULT  now()   NOT NULL
    , userstamp               VARCHAR  (30)    DEFAULT  USER    NOT NULL
    , owner_ac                VARCHAR  (30)    CONSTRAINT fk_Institution_owner REFERENCES IA_Institution(ac)
    , shortLabel              VARCHAR  (20)
    , fullName                VARCHAR  (250)
    , postalAddress           VARCHAR  (2000)
    , url                     VARCHAR  (255)
    , created_user            VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;
CREATE INDEX i_InstitutionShortLabel on IA_Institution(shortLabel);

COMMENT ON TABLE IA_Institution IS
'Institution, e.g. a university, company, etc.';
COMMENT ON COLUMN IA_Institution.shortLabel IS
'A short string identifying the object, not necessarily unique. Could be e.g. a gene name. ';
COMMENT ON COLUMN IA_Institution.fullName IS
'The full name of the object.';
COMMENT ON COLUMN IA_Institution.postalAddress IS
'The postal address. Contains line breaks for formatting.';
COMMENT ON COLUMN IA_Institution.url IS
'The URL of the entry page of the institution.';
COMMENT ON COLUMN IA_Institution.ac IS
'Unique, auto-generated accession number.';
COMMENT ON COLUMN IA_Institution.created IS
'Date of the creation of the row.';
COMMENT ON COLUMN IA_Institution.updated IS
'Date of the last update of the row.';
COMMENT ON COLUMN IA_Institution.timestamp IS
'Date of the last update of the column.';
COMMENT ON COLUMN IA_Institution.userstamp IS
'Database user who has performed the last update of the column.';




/* The ControlledVocab table is the master table which will be used by
all controlled vocabularies. */


CREATE TABLE IA_ControlledVocab
(       ac                      VARCHAR (30)    NOT NULL
                                                CONSTRAINT pk_ControlledVocab
                                                PRIMARY KEY
     ,  deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  owner_ac                VARCHAR (30)    CONSTRAINT fk_ControlledVocab_owner REFERENCES IA_Institution(ac)
     ,  objClass                VARCHAR (255)
     ,  shortLabel              VARCHAR (20)
     ,  fullName                VARCHAR (250)
     , created_user             VARCHAR (30)    DEFAULT  USER    NOT NULL
)
;

CREATE INDEX i_ControlledVocab_shortLabel on IA_ControlledVocab(shortLabel) ;
CREATE UNIQUE INDEX uq_CVocab_objClass_ShortLabel on IA_ControlledVocab(objClass,shortLabel) ;


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



CREATE TABLE IA_BioSource
(         ac                      VARCHAR (30)    NOT NULL
                                                  CONSTRAINT pk_BioSource
                                                  PRIMARY KEY
        , deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
        , created                 TIMESTAMP       DEFAULT  now()   NOT NULL
        , updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
        , timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
        , userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
        , taxId                   VARCHAR (30)                     NOT NULL
        , owner_ac                VARCHAR (30)    CONSTRAINT fk_BioSource_owner REFERENCES IA_Institution(ac)
        , shortLabel              VARCHAR (20)
        , fullName                VARCHAR (250)
        , tissue_ac               VARCHAR (30)    CONSTRAINT fk_Biosource_tissue REFERENCES IA_ControlledVocab(ac)
        , celltype_ac             VARCHAR (30)    CONSTRAINT fk_Biosource_celltype REFERENCES IA_ControlledVocab(ac)
        , created_user            VARCHAR (30)    DEFAULT  USER    NOT NULL
)
;

-- too small a table ?  CREATE INDEX i_BioSource_shortLabel on BioSource(shortLabel);

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


/* This is the key table. The class Interactor is the parent class of
   a class hierarchy which comprises all molecular objects. All
   subclasses are mapped to this table. It's likely to be the largest and
   most queried, hence most performance-critical table.
*/

CREATE TABLE IA_Interactor
(         ac                    VARCHAR (30)    NOT NULL
                                                CONSTRAINT pk_Interactor
                                                PRIMARY KEY
        , deprecated            DECIMAL(1)      DEFAULT  0       NOT NULL
        , created               TIMESTAMP       DEFAULT  now()   NOT NULL
        , updated               TIMESTAMP       DEFAULT  now()   NOT NULL
        , timestamp             TIMESTAMP       DEFAULT  now()   NOT NULL
        , userstamp             VARCHAR (30)    DEFAULT  USER    NOT NULL
        /* Column belonging to Interaction */
        , kD                    FLOAT
        /* Columns belonging to Protein */
        , crc64                 VARCHAR (16)
        , formOf                VARCHAR (30)    CONSTRAINT fk_Interactor_formOf REFERENCES IA_Interactor(ac)
        , proteinForm_ac        VARCHAR (30)    CONSTRAINT fk_Interactor_proteinForm_ac REFERENCES IA_ControlledVocab(ac)
        /* Colums belonging to Interactor */
        , objClass              VARCHAR (255)
        , bioSource_ac          VARCHAR (30)    CONSTRAINT fk_Interactor_bioSource REFERENCES IA_BioSource(ac)
        , interactionType_ac    VARCHAR (30)    CONSTRAINT fk_Interactor_interactionType REFERENCES IA_ControlledVocab(ac)
        /* Colums belonging to AnnotatedObject */
        , shortLabel            VARCHAR (20)
        , fullName              VARCHAR (250)
        /* Colums belonging to BasicObject */
        , owner_ac              VARCHAR (30)    CONSTRAINT fk_Interactor_owner REFERENCES IA_Institution(ac)
        /* Colums belonging to InteractorType */
        , interactorType_ac     VARCHAR (30)    CONSTRAINT fk_Interactor_type_ac REFERENCES IA_ControlledVocab(ac)
        , created_user          VARCHAR  (30)   DEFAULT  USER    NOT NULL
)
;

CREATE index i_Interactor_crc64 on IA_Interactor(crc64) ;
CREATE index i_Interactor_bioSource_ac on IA_Interactor(bioSource_ac) ;
CREATE index i_Interactor_shortLabel on IA_Interactor(shortLabel) ;
CREATE index i_Interactor_fullName on IA_Interactor(fullName) ;
CREATE index i_Interactor_formOf on IA_Interactor(formOf) ;


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
COMMENT ON COLUMN IA_Interactor.interactionType_ac IS
'Holds the accession number for the interactor type.';



/* The table which contains proteins' chunked sequence */

CREATE TABLE IA_Sequence_Chunk
(       ac                      VARCHAR (30)    NOT NULL
                                                CONSTRAINT pk_Sequence_Chunk
                                                PRIMARY KEY
     ,  timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  parent_ac               VARCHAR (30)    NOT NULL
                                                CONSTRAINT fk_Sequence_chunk_parent_ac REFERENCES IA_Interactor(ac)
                                                ON DELETE CASCADE
     ,  sequence_chunk          VARCHAR (1000)
     ,  sequence_index          DECIMAL (3)
     ,  created_user            VARCHAR (30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_Sequence_chunk_parent_ac on IA_Sequence_Chunk(parent_ac) ;

COMMENT ON TABLE IA_Sequence_Chunk IS
'Sequence chunk. To avoid CLOB columns the sequences are split into chunks of 1000 characters';
COMMENT ON COLUMN IA_Sequence_Chunk.ac IS
'chunk unique identifier.';
COMMENT ON COLUMN IA_Sequence_Chunk.parent_ac IS
'Refers to the Interactor to which this bit of sequence belongs.';
COMMENT ON COLUMN IA_Sequence_Chunk.timestamp IS
'Date of the last update of the column.';
COMMENT ON COLUMN IA_Sequence_Chunk.userstamp IS
'Database user who has performed the last update of the column.';
COMMENT ON COLUMN IA_Sequence_Chunk.sequence_chunk IS
'1000 charcacters max size Sequence chunk.';
COMMENT ON COLUMN IA_Sequence_Chunk.sequence_index IS
'Order of the chunk within the sequence of the Interactor.';




CREATE TABLE IA_Component
(         ac                      VARCHAR (30)    NOT NULL
                                                  CONSTRAINT pk_Component
                                                  PRIMARY KEY
        , deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
        , created                 TIMESTAMP       DEFAULT  now()   NOT NULL
        , updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
        , timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
        , userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
        , interactor_ac           VARCHAR (30)    CONSTRAINT fk_Component_interactor REFERENCES IA_Interactor(ac)  ON DELETE CASCADE
        , interaction_ac          VARCHAR (30)    CONSTRAINT fk_Component_interaction REFERENCES IA_Interactor(ac) ON DELETE CASCADE
        , role                    VARCHAR (30)    CONSTRAINT fk_Component_role REFERENCES IA_ControlledVocab(ac)
        , expressedIn_ac          VARCHAR (30)    CONSTRAINT fk_Component_expressedIn REFERENCES IA_BioSource(ac)
        , owner_ac                VARCHAR (30)    CONSTRAINT fk_Component_owner REFERENCES IA_Institution(ac)
        , stoichiometry           DECIMAL (4,1)
        , created_user            VARCHAR (30)    DEFAULT  USER    NOT NULL
);

CREATE index i_Component_interaction_ac on IA_Component(interaction_ac) ;
CREATE index i_Component_interactor_ac on IA_Component(interactor_ac) ;


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





CREATE TABLE IA_Annotation
(         ac                      VARCHAR (30)    NOT NULL
                                                  CONSTRAINT pk_Annotation
                                                  PRIMARY KEY
        , deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
        , created                 TIMESTAMP       DEFAULT  now()   NOT NULL
        , updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
        , timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
        , userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
        , topic_ac                VARCHAR (30)    CONSTRAINT fk_Annotation_topic REFERENCES IA_ControlledVocab(ac)
        , owner_ac                VARCHAR (30)    CONSTRAINT fk_Annotation_owner REFERENCES IA_Institution(ac)
        , description             VARCHAR (4000)
        , created_user            VARCHAR (30)    DEFAULT  USER    NOT NULL
)
;

-- too small a table ? CREATE index i_Annotation_topic on Annotation(topic_ac);

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




CREATE TABLE IA_Experiment
(       ac                      VARCHAR (30)    NOT NULL
                                                CONSTRAINT pk_Experiment
                                                PRIMARY KEY
      , deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
      , created                 TIMESTAMP       DEFAULT  now()   NOT NULL
      , updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
      , timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
      , userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
      , bioSource_ac            VARCHAR (30)    CONSTRAINT fk_Experiment_bioSource REFERENCES IA_BioSource(ac)
      , detectMethod_ac         VARCHAR (30)    CONSTRAINT fk_Experiment_detectMethod REFERENCES IA_ControlledVocab(ac)
      , identMethod_ac          VARCHAR (30)    CONSTRAINT fk_Experiment_identMethod REFERENCES IA_ControlledVocab(ac)
      , relatedExperiment_ac    VARCHAR (30)    CONSTRAINT fk_Experiment_relatedExp REFERENCES IA_Experiment(ac)
      , owner_ac                VARCHAR (30)    CONSTRAINT fk_Experiment_owner REFERENCES IA_Institution(ac)
      , shortLabel              VARCHAR (20)
      , fullName                VARCHAR (250)
      , created_user            VARCHAR (30)    DEFAULT  USER    NOT NULL
)
;

-- too small a table ? CREATE INDEX i_Experiment_shortLabel on Experiment(shortLabel);


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


CREATE TABLE IA_Xref
(       ac                 VARCHAR (30)    NOT NULL
                                           CONSTRAINT pk_Xref
                                           PRIMARY KEY
     ,  deprecated         DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated            TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp          TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp          VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac       VARCHAR (30)    CONSTRAINT fk_Xref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac        VARCHAR (30)    CONSTRAINT fk_Xref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac          VARCHAR (30)    -- checked via trigger
     ,  owner_ac           VARCHAR (30)    CONSTRAINT fk_Xref_owner REFERENCES IA_Institution(ac)
     ,  primaryId          VARCHAR (30)
     ,  secondaryId        VARCHAR (30)
     ,  dbRelease          VARCHAR (10)
     ,  created_user       VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;

CREATE index i_Xref_parent_ac on IA_Xref(parent_ac) ;

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



/* The IntactNode table is the table which will be used for
storing informations about servers. */


CREATE TABLE IA_IntactNode
(       ac                     VARCHAR (30) NOT NULL
                               CONSTRAINT pk_IntactNode
                               PRIMARY KEY
      , lastCheckId            DECIMAL(5)     DEFAULT  0              NOT NULL
      , lastProvidedId         DECIMAL(5)     DEFAULT  0              NOT NULL
      , lastProvidedDate       Date           DEFAULT  TIMESTAMP '1970-01-01'  NOT NULL
      , rejected               DECIMAL(1)     DEFAULT  0              NOT NULL
      , created                TIMESTAMP      DEFAULT  now()          NOT NULL
      , updated                TIMESTAMP      DEFAULT  now()          NOT NULL
      , timestamp              TIMESTAMP      DEFAULT  now()          NOT NULL
      , userstamp              VARCHAR (30)   DEFAULT  USER           NOT NULL
      , deprecated             DECIMAL(1)     DEFAULT  0              NOT NULL
      , ownerPrefix            VARCHAR (30)   DEFAULT  USER           NOT NULL
      , owner_ac               VARCHAR (30)   CONSTRAINT fk_IntactNode_owner REFERENCES IA_Institution(ac)
      , ftpAddress             VARCHAR (255)
      , ftpLogin               VARCHAR (255)
      , ftpPassword            VARCHAR (255)
      , ftpDirectory           VARCHAR (255)
      , created_user           VARCHAR  (30)    DEFAULT  USER    NOT NULL
)
;



CREATE TABLE IA_Int2Exp
(       interaction_ac          VARCHAR (30)    NOT NULL   CONSTRAINT fk_Int2Exp_interaction REFERENCES IA_Interactor(ac)  ON DELETE CASCADE
      , experiment_ac           VARCHAR (30)    NOT NULL   CONSTRAINT fk_Int2Exp_experiment  REFERENCES IA_Experiment(ac)  ON DELETE CASCADE
      , deprecated              DECIMAL(1)      DEFAULT    0       NOT NULL
      , created                 TIMESTAMP       DEFAULT    now()   NOT NULL
      , userstamp               VARCHAR (30)    DEFAULT    USER    NOT NULL
      , updated                 TIMESTAMP       DEFAULT    now()   NOT NULL
      , timestamp               TIMESTAMP       DEFAULT    now()   NOT NULL
      , created_user            VARCHAR (30)    DEFAULT    USER    NOT NULL
      , PRIMARY KEY             (interaction_ac, experiment_ac)
)
;

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




CREATE TABLE IA_Int2Annot
(       interactor_ac           VARCHAR (30)    NOT NULL CONSTRAINT fk_Int2Annot_interactor REFERENCES IA_Interactor(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR (30)    NOT NULL CONSTRAINT fk_Int2Annot_annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  created_user            VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  PRIMARY KEY             (interactor_ac, annotation_ac)
)
;

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



CREATE TABLE IA_Exp2Annot
(       experiment_ac           VARCHAR (30)    NOT NULL CONSTRAINT fk_Exp2Annot_experiment REFERENCES IA_Experiment(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR (30)    NOT NULL CONSTRAINT fk_Exp2Annot_annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  created_user            VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  PRIMARY KEY             (experiment_ac, annotation_ac)
)
;

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





CREATE TABLE IA_cvobject2Annot
(       cvobject_ac             VARCHAR (30)    NOT NULL CONSTRAINT fk_cvobj2Annot_cvobject   REFERENCES IA_ControlledVocab(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR (30)    NOT NULL CONSTRAINT fk_cvobj2Annot_annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  created_user            VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  PRIMARY KEY             (cvobject_ac, annotation_ac)
)
;


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




CREATE TABLE IA_Biosource2Annot
(       biosource_ac            VARCHAR(30)    NOT NULL CONSTRAINT fk_bio2Annot_biosource   REFERENCES IA_Biosource(ac)  ON DELETE CASCADE
     ,  annotation_ac           VARCHAR(30)    NOT NULL CONSTRAINT fk_bio2Annot_annotation  REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              DECIMAL(1)     DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP      DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR(30)    DEFAULT  USER    NOT NULL
     ,  updated                 TIMESTAMP      DEFAULT  now()   NOT NULL
     ,  timestamp               TIMESTAMP      DEFAULT  now()   NOT NULL
     ,  created_user            VARCHAR (30)   DEFAULT  USER    NOT NULL
     ,  PRIMARY KEY             (biosource_ac, annotation_ac)
)
;

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


/* The relation table which establishes a graph structure between CV objects */
CREATE TABLE IA_Cv2Cv
(
       parent_ac               VARCHAR(30)      NOT NULL CONSTRAINT fk_Cv2Cv_parent REFERENCES IA_ControlledVocab(ac) ON DELETE CASCADE
    ,  child_ac                VARCHAR(30)      NOT NULL CONSTRAINT fk_Cv2Cv_child REFERENCES IA_ControlledVocab(ac) ON DELETE CASCADE
    ,  deprecated              DECIMAL(1)       DEFAULT  0       NOT NULL
    ,  created                 TIMESTAMP        DEFAULT  now()   NOT NULL
    ,  updated                 TIMESTAMP        DEFAULT  now()   NOT NULL
    ,  timestamp               TIMESTAMP        DEFAULT  now()   NOT NULL
    ,  userstamp               VARCHAR(30)      DEFAULT  USER    NOT NULL
    ,  created_user            VARCHAR (30)     DEFAULT  USER    NOT NULL
    ,  PRIMARY KEY             (parent_ac, child_ac)
)
;


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

CREATE SEQUENCE Intact_ac start 10;

CREATE SEQUENCE cvobject_id MINVALUE 1 INCREMENT 1 START WITH 51;




/* first create the sequence which allows to specify the ac field as an autoincremented field */
CREATE SEQUENCE statistics_seq;

/* This table will record the amount of some specific data in the IntAct database, at a given time */
CREATE TABLE IA_Statistics
(     ac                       INT4
                               DEFAULT nextval('statistics_seq')
                               CONSTRAINT pk_Statistics
                               PRIMARY KEY
      , timestamp              TIMESTAMP DEFAULT  now()   NOT NULL
      , protein_number         INT4      DEFAULT  0       NOT NULL
      , interaction_number     INT4      DEFAULT  0       NOT NULL
      , binary_interactions    INT4      DEFAULT  0       NOT NULL
      , complex_interactions   INT4      DEFAULT  0       NOT NULL
      , experiment_number      INT4      DEFAULT  0       NOT NULL
      , term_number            INT4      DEFAULT  0       NOT NULL
);

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



 
 
/* Alias */
 
CREATE TABLE IA_alias
(       ac                VARCHAR(30)    NOT NULL
                                         CONSTRAINT pk_alias
                                         PRIMARY KEY
    ,  deprecated         DECIMAL(1)     DEFAULT  0       NOT NULL
    ,  created            TIMESTAMP      DEFAULT  now()   NOT NULL
    ,  updated            TIMESTAMP      DEFAULT  now()   NOT NULL
    ,  timestamp          TIMESTAMP      DEFAULT  now()   NOT NULL
    ,  userstamp          VARCHAR(30)    DEFAULT  USER    NOT NULL
    ,  aliastype_ac       VARCHAR(30)    CONSTRAINT fk_alias_qualifier REFERENCES IA_ControlledVocab(ac)
    ,  parent_ac          VARCHAR(30)    -- constraint managed by trigger.
    ,  owner_ac           VARCHAR(30)    CONSTRAINT fk_alias_owner REFERENCES IA_Institution(ac)
    ,  name               VARCHAR(30)
    ,  created_user       VARCHAR (30)   DEFAULT  USER    NOT NULL
)
;
 
CREATE index i_Alias_parent_ac on IA_Alias(parent_ac);
 
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



/* Feature related tables */

/* This is a table where we store which features are linked to a component. */

CREATE TABLE IA_Feature
(         ac                    VARCHAR(30)    NOT NULL
                                               CONSTRAINT pk_Feature
                                               PRIMARY KEY
        , deprecated            DECIMAL(1)     DEFAULT  0     NOT NULL
        , created               TIMESTAMP      DEFAULT  now() NOT NULL
        , updated               TIMESTAMP      DEFAULT  now() NOT NULL
        , timestamp             TIMESTAMP      DEFAULT  now() NOT NULL
        , userstamp             VARCHAR(30)    DEFAULT  USER  NOT NULL
        , component_ac          VARCHAR(30)    NOT NULL CONSTRAINT fk_Feature_component REFERENCES IA_Component(ac) ON DELETE CASCADE
        , identification_ac     VARCHAR(30)    CONSTRAINT fk_Feature_identification_ac REFERENCES IA_ControlledVocab(ac)
        , featureType_ac        VARCHAR(30)    CONSTRAINT fk_Feature_featureType_ac REFERENCES IA_ControlledVocab(ac)
        , linkedfeature_ac      VARCHAR(30)    CONSTRAINT fk_Feature_feature REFERENCES IA_Feature(ac)
        , owner_ac              VARCHAR(30)    CONSTRAINT fk_Feature_owner REFERENCES IA_Institution(ac)
        , shortLabel            VARCHAR(20)
        , fullName              VARCHAR(250)
        ,  created_user         VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE INDEX i_Feature_owner_ac on IA_Feature(owner_ac);
CREATE INDEX i_Feature_component_ac on IA_Feature(component_ac);
CREATE INDEX i_Feature_linkedfeature_ac on IA_Feature(linkedfeature_ac);
CREATE INDEX i_Feature_identification_ac on IA_Feature(identification_ac);
CREATE INDEX i_Feature_featureType_ac on IA_Feature(featureType_ac);

COMMENT ON TABLE IA_Feature IS
'Feature. Define a set of Ranges.';
COMMENT ON COLUMN IA_Feature.ac IS
'Unique, auto-generated accession number.';
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



/* This is a table where we store where is situated an interaction at the protein sequence level. */

CREATE TABLE IA_Range
(         ac                    VARCHAR(30)    NOT NULL
                                               CONSTRAINT pk_Range
                                               PRIMARY KEY
        , deprecated            DECIMAL(1)     DEFAULT  0       NOT NULL
        , created               TIMESTAMP      DEFAULT  now() NOT NULL
        , updated               TIMESTAMP      DEFAULT  now() NOT NULL
        , timestamp             TIMESTAMP      DEFAULT  now() NOT NULL
        , userstamp             VARCHAR(30)    DEFAULT  USER    NOT NULL
        , undetermined          CHAR           NOT NULL CHECK ( undetermined IN ('N','Y') )
        , link                  CHAR           NOT NULL CHECK ( link IN ('N','Y') )
        , feature_ac            VARCHAR(30)    NOT NULL CONSTRAINT fk_Range_feature REFERENCES IA_Feature(ac) ON DELETE CASCADE
        , owner_ac              VARCHAR (30)   CONSTRAINT fk_Range_owner REFERENCES IA_Institution(ac)
        , fromIntervalStart     DECIMAL(5)
        , fromIntervalEnd       DECIMAL(5)
        , fromFuzzyType_ac      VARCHAR(30)    CONSTRAINT fk_Range_fromFuzzyType_ac REFERENCES IA_ControlledVocab(ac)
        , toIntervalStart       DECIMAL(5)
        , toIntervalEnd         DECIMAL(5)
        , toFuzzyType_ac        VARCHAR(30)    CONSTRAINT fk_Range_toFuzzyType_ac REFERENCES IA_ControlledVocab(ac)
        , sequence              VARCHAR(100)
        , created_user          VARCHAR(30)    DEFAULT  USER    NOT NULL
)
;

CREATE INDEX i_Range_owner_ac on IA_Range(owner_ac);
CREATE INDEX i_Range_fromFuzzyType_ac on IA_Range(fromFuzzyType_ac);
CREATE INDEX i_Range_toFuzzyType_ac on IA_Range(toFuzzyType_ac);
CREATE INDEX i_Range_feature_ac on IA_Range(feature_ac);

COMMENT ON TABLE IA_Range IS
'Range. Represents a location on a sequence.';
COMMENT ON COLUMN IA_Range.ac IS
'Unique, auto-generated accession number.';
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


/* Indirection table in which we stores which feature is linked to which annotations. */

CREATE TABLE IA_Feature2Annot
(       feature_ac              VARCHAR(30)    NOT NULL CONSTRAINT fk_Feature2Annot_feature REFERENCES IA_Feature(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR(30)    NOT NULL CONSTRAINT fk_Feature2Annot_annotation REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              DECIMAL(1)     DEFAULT  0     NOT NULL
     ,  created                 TIMESTAMP      DEFAULT  now() NOT NULL
     ,  userstamp               VARCHAR(30)    DEFAULT  USER  NOT NULL
     ,  updated                 TIMESTAMP      DEFAULT  now() NOT NULL
     ,  timestamp               TIMESTAMP      DEFAULT  now() NOT NULL
     ,  created_user            VARCHAR  (30)    DEFAULT  USER    NOT NULL
     ,  PRIMARY KEY             ( feature_ac, annotation_ac )
)
;

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




/* Tables for predict (targets) application. */

CREATE table IA_Payg(
	  nID VARCHAR(20) NOT NULL
        , bait INTEGER
        , prey INTEGER
        , indegree SMALLINT
        , outdegree SMALLINT
        , qdegree numeric
        , eseen INTEGER
        , econf INTEGER
        , really_used_as_bait CHAR(1)
	, species VARCHAR(20) NOT NULL 
	, PRIMARY KEY (nID, species)
);

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
    'Used as a tie-breaker in the event two or more nIDs have the same & highest indegree.';
    COMMENT ON COLUMN IA_Payg.eseen IS
    'Running totals of how many interactions have been seen.';
    COMMENT ON COLUMN IA_Payg.econf IS
    'Running totals of how many interactions have been confirmed.';
    COMMENT ON COLUMN IA_Payg.really_used_as_bait IS
    '.';
    COMMENT ON COLUMN IA_Payg.species IS
    'The tax id of the bio source.';

/* The following two tables are needed to create ia_payg table. They are not used
   once ia_payg is created.
*/

CREATE table current_edge(
	nidA VARCHAR(20),
	nidB VARCHAR(20),
	seen INTEGER, conf INTEGER, species VARCHAR(20));

CREATE table temp_node(
	nid VARCHAR(20), species VARCHAR(20));


/* ************************************************
  	Interactions table for mine
   ************************************************/

CREATE TABLE ia_interactions (
        protein1_ac     VARCHAR(30)    CONSTRAINT fk_interactions_protein1_ac REFERENCES IA_Interactor(ac) ON DELETE CASCADE
      , shortlabel1     VARCHAR(20)
      , protein2_ac     VARCHAR(30)    CONSTRAINT fk_interactions_protein2_ac REFERENCES IA_Interactor(ac) ON DELETE CASCADE
      , shortlabel2     VARCHAR(20)
      , taxid           VARCHAR(30)
      , interaction_ac  VARCHAR(30)    CONSTRAINT fk_interactions_interaction REFERENCES IA_Interactor(ac) ON DELETE CASCADE
      , weight          DECIMAL(4,3)
      , graphid         INTEGER
) ;

COMMENT ON TABLE ia_interactions IS
'Stores all binary interactions based on the content of ia_interactor, ia_component, ia_biosource. This is autogenerated.';
COMMENT ON COLUMN ia_interactions.protein1_ac IS
'One of the interacting partner.';
COMMENT ON COLUMN ia_interactions.shortlabel1 IS
'The shortlabel of the first interacting partner.';
COMMENT ON COLUMN ia_interactions.protein2_ac IS
'The other interacting partner.';
COMMENT ON COLUMN ia_interactions.shortlabel2 IS
'The shortlabel of the second interacting partner.';
COMMENT ON COLUMN ia_interactions.taxid IS
'BioSource taxid in which that interaction takes place.';
COMMENT ON COLUMN ia_interactions.interaction_ac IS
'interaction in which those two proteins interacts';
COMMENT ON COLUMN ia_interactions.weight IS
'Weight of that interraction.';
COMMENT ON COLUMN ia_interactions.graphid IS
'Graph in which that interraction takes place.';
