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
                     

  $Date$
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
)
;

CREATE INDEX i_ControlledVocab_shortLabel on IA_ControlledVocab(shortLabel) 
;
CREATE UNIQUE INDEX uq_CVocab_objClass_ShortLabel on IA_ControlledVocab(objClass,shortLabel) 
;


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
        , taxId                   VARCHAR (30)    CONSTRAINT uq_BioSource_taxId UNIQUE 
        , owner_ac                VARCHAR (30)    CONSTRAINT fk_BioSource_owner REFERENCES IA_Institution(ac)
        , shortLabel              VARCHAR (20)
        , fullName                VARCHAR (250)
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



CREATE TABLE IA_Sequence_Chunk
(         interactor_ac         VARCHAR(30)    NOT NULL CONSTRAINT fk_sequence_interactor REFERENCES IA_Interactor(ac)
        , order_in_sequence     DECIMAL(3)     NOT NULL
        , sequence_chunk        VARCHAR(1000)  NOT NULL
        , created               TIMESTAMP      DEFAULT  now()   NOT NULL  
        , updated               TIMESTAMP      DEFAULT  now()   NOT NULL  
        , timestamp             TIMESTAMP      DEFAULT  now()   NOT NULL  
        , userstamp             VARCHAR(30)    DEFAULT  USER    NOT NULL  
        , PRIMARY KEY           (interactor_ac , order_in_sequence)
)
;



    COMMENT ON TABLE IA_Sequence_Chunk IS
    'Sequence chunk. To avoid CLOB columns the sequences are split into chunks of 1000 characters';
    COMMENT ON COLUMN IA_Sequence_Chunk.interactor_ac IS
    'Refers to the Interactor to which this bit of sequence belongs.';
    COMMENT ON COLUMN IA_Sequence_Chunk.order_in_sequence IS
    'Order of the chunk within the sequence of the Interactor';
    COMMENT ON COLUMN IA_Sequence_Chunk.sequence_chunk IS
    '1000 charcacters max size Sequence chunk';
    COMMENT ON COLUMN IA_Sequence_Chunk.created IS
    'Date of the creation of the row.';
    COMMENT ON COLUMN IA_Sequence_Chunk.updated IS
    'Date of the last update of the row.';
    COMMENT ON COLUMN IA_Sequence_Chunk.timestamp IS
    'Date of the last update of the column.';
    COMMENT ON COLUMN IA_Sequence_Chunk.userstamp IS
    'Database user who has performed the last update of the column.';

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
(       ac                      VARCHAR (30)    NOT NULL
                                                CONSTRAINT pk_Xref
                                                PRIMARY KEY 
     ,  deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR (30)    CONSTRAINT fk_Xref_qualifier REFERENCES IA_ControlledVocab(ac)
     ,  database_ac             VARCHAR (30)    CONSTRAINT fk_Xref_database  REFERENCES IA_ControlledVocab(ac)
     ,  parent_ac               VARCHAR (30)    -- eh missing constraint here??
     ,  owner_ac                VARCHAR (30)    CONSTRAINT fk_Xref_owner REFERENCES IA_Institution(ac)
     ,  primaryId               VARCHAR (30)
     ,  secondaryId             VARCHAR (30)
     ,  dbRelease               VARCHAR (10)
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
(       ac                          VARCHAR (30) NOT NULL
                                    CONSTRAINT pk_IntactNode
                                    PRIMARY KEY 
      , lastCheckId                 DECIMAL(5)     DEFAULT  0              NOT NULL
      , lastProvidedId              DECIMAL(5)     DEFAULT  0              NOT NULL
      , lastProvidedDate            Date           DEFAULT  TIMESTAMP '1970-01-01'  NOT NULL
      , rejected                    DECIMAL(1)     DEFAULT  0              NOT NULL
      , created                     TIMESTAMP      DEFAULT  now()          NOT NULL
      , updated                     TIMESTAMP      DEFAULT  now()          NOT NULL
      , timestamp                   TIMESTAMP      DEFAULT  now()          NOT NULL
      , userstamp                   VARCHAR (30)   DEFAULT  USER           NOT NULL
      , deprecated                  DECIMAL(1)     DEFAULT  0              NOT NULL
      , ownerPrefix                 VARCHAR (30)   DEFAULT  USER           NOT NULL
      , owner_ac                    VARCHAR (30)   CONSTRAINT fk_IntactNode_owner REFERENCES IA_Institution(ac)
      , ftpAddress                  VARCHAR (255)
      , ftpLogin                    VARCHAR (255)
      , ftpPassword                 VARCHAR (255)
      , ftpDirectory                VARCHAR (255)
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
(       biosource_ac            VARCHAR (30)    NOT NULL CONSTRAINT fk_bio2Annot_biosource   REFERENCES IA_Biosource(ac)  ON DELETE CASCADE
     ,  annotation_ac           VARCHAR (30)    NOT NULL CONSTRAINT fk_bio2Annot_annotation  REFERENCES IA_Annotation(ac) ON DELETE CASCADE
     ,  deprecated              DECIMAL(1)      DEFAULT  0       NOT NULL
     ,  created                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  userstamp               VARCHAR (30)    DEFAULT  USER    NOT NULL  
     ,  updated                 TIMESTAMP       DEFAULT  now()   NOT NULL
     ,  timestamp               TIMESTAMP       DEFAULT  now()   NOT NULL
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


