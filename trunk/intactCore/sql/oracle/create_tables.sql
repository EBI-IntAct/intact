set doc off

/**************************************************************************************************************************

  Package:    IntAct

  Purpose:    Create Oracle components for IntAct

  Usage:      sqlplus username/password @create_tables.sql


  $Date$

  $Locker$


  **************************************************************************************************************************/


PROMPT Creating table "Institution"
CREATE TABLE Institution
(     ac                      VARCHAR2(30)    NOT NULL
                                              CONSTRAINT pk_Institution
                                              PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
    , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
    , created                 DATE            DEFAULT  SYSDATE NOT NULL
    , updated                 DATE            DEFAULT  SYSDATE NOT NULL
    , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
    , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
    , owner_ac                VARCHAR2(30)    CONSTRAINT fk_Institution$owner REFERENCES Institution(ac)
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
    COMMENT ON TABLE Institution IS
    'Institution e.g. a university company etc.';
    COMMENT ON COLUMN Institution.shortLabel IS
    'A short string identifying the object not necessarily unique. Could be e.g. a gene name. ';
    COMMENT on COLUMN Institution.fullName IS
    'The full name of the object.';
    COMMENT on COLUMN Institution.postalAddress IS
    'The postal address. Contains line breaks for formatting.';
    COMMENT on COLUMN Institution.url IS
    'The URL of the entry page of the institution.';
    COMMENT on COLUMN Institution.ac IS
    'Unique auto-generated accession number.';
    COMMENT on COLUMN Institution.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Institution.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Institution.timestamp IS
    'Date of the last update of the column.';
    COMMENT on COLUMN Institution.userstamp IS
    'Database user who has performed the last update of the column.';
set term on



/* The ControlledVocab table is the master table which will be used by
all controlled vocabularies. */

PROMPT Creating table "ControlledVocab"
CREATE TABLE ControlledVocab
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_ControlledVocab 
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_ControlledVocab$owner REFERENCES Institution(ac)
     ,  objClass                VARCHAR2(255)   
     ,  shortLabel              VARCHAR2(20)    
     ,  fullName                VARCHAR2(250)
)
TABLESPACE &&intactMainTablespace
;

CREATE INDEX i_ControlledVocab$shortLabel on ControlledVocab(shortLabel) TABLESPACE &&intactIndexTablespace
;
CREATE UNIQUE INDEX uq_CVocab$objClass_ShortLabel ON ControlledVocab(objClass,shortLabel) TABLESPACE &&intactIndexTablespace 
;

set term off
    COMMENT ON TABLE ControlledVocab IS
    'Master table for all controlled vocabularies.';
    COMMENT ON COLUMN ControlledVocab.objClass IS
    'The fully qualified classname of the object. This is needed for the OR mapping.';
    COMMENT on COLUMN ControlledVocab.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT on COLUMN ControlledVocab.ac IS
    'Unique auto-generated accession number.';
    COMMENT ON COLUMN ControlledVocab.shortLabel IS
    'A short version of the term. Used e.g. in selection lists. ';
    COMMENT on COLUMN ControlledVocab.fullName IS
    'The full descriptive term. ';
    COMMENT on COLUMN ControlledVocab.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN ControlledVocab.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN ControlledVocab.timestamp IS
    'Date of the last update of the column.';
    COMMENT on COLUMN ControlledVocab.userstamp IS
    'Database user who has performed the last update of the column.';
set term on 

PROMPT Creating table "BioSource"
CREATE TABLE BioSource
(         ac                      VARCHAR2(30)    NOT NULL
                                                  CONSTRAINT pk_BioSource 
                                                  PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
        , created                 DATE            DEFAULT  SYSDATE NOT NULL
        , updated                 DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
        , taxId                   VARCHAR(30)     CONSTRAINT uq_BioSource$taxId UNIQUE USING INDEX TABLESPACE &&intactIndexTablespace
        , owner_ac                VARCHAR2(30)    CONSTRAINT fk_BioSource$owner REFERENCES Institution(ac)
        , shortLabel              VARCHAR2(20)
        , fullName                VARCHAR2(250)
)
TABLESPACE &&intactMainTablespace
;

-- too small a table ?  CREATE INDEX i_BioSource$shortLabel on BioSource(shortLabel);


set term off
    COMMENT ON TABLE BioSource IS
    'BioSource normally some kind of organism. ';
    COMMENT on COLUMN BioSource.taxId IS
    'The NCBI tax ID.';
    COMMENT on COLUMN BioSource.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT on COLUMN BioSource.ac IS
    'Unique auto-generated accession number.';
    COMMENT on COLUMN BioSource.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN BioSource.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN BioSource.timestamp IS
    'Date of the last update of the column.';
    COMMENT on COLUMN BioSource.userstamp IS
    'Database user who has performed the last update of the column.';
set term on




/* This is the key table. The class Interactor is the parent class of
   a class hierarchy which comprises all molecular objects. All
   subclasses are mapped to this table. It's likely to be the largest and
   most queried, hence most performance-critical table.
*/

PROMPT Creating table "Interactor"
CREATE TABLE Interactor
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
        , formOf                VARCHAR2(30)    CONSTRAINT fk_Interactor$formOf REFERENCES Interactor(ac)
        , proteinForm_ac        VARCHAR2(30)    CONSTRAINT fk_Interactor$proteinForm_ac REFERENCES ControlledVocab(ac)
        /* Colums belonging to Interactor */
        , objClass              VARCHAR2(255)   
        , bioSource_ac          VARCHAR2(30)    CONSTRAINT fk_Interactor$bioSource REFERENCES BioSource(ac)
        , interactionType_ac    VARCHAR2(30)    CONSTRAINT fk_Interactor$interactionType REFERENCES ControlledVocab(ac)
        /* Colums belonging to AnnotatedObject */
        , shortLabel            VARCHAR2(20)
        , fullName              VARCHAR2(250)
        /* Colums belonging to BasicObject */
        , owner_ac              VARCHAR2(30)    CONSTRAINT fk_Interactor$owner REFERENCES Institution(ac)
        , polymerSeq            CLOB
)
TABLESPACE &&intactMainTablespace
lob (polymerSeq) 
   STORE AS SEGNAME (TABLESPACE &&intactLobTablespace
                     PCTVERSION 0
                     CACHE READS
                     NOLOGGING
                    )
;

CREATE index i_Interactor$crc64 on Interactor(crc64) TABLESPACE &&intactIndexTablespace;
CREATE index i_Interactor$bioSource_ac on Interactor(bioSource_ac) TABLESPACE &&intactIndexTablespace;
CREATE index i_Interactor$shortLabel on Interactor(shortLabel) TABLESPACE &&intactIndexTablespace;
CREATE index i_Interactor$fullName on Interactor(fullName) TABLESPACE &&intactIndexTablespace;
CREATE index i_Interactor$formOf on Interactor(formOf) TABLESPACE &&intactIndexTablespace; 

set term off
    COMMENT ON TABLE Interactor IS
    'Interactor. Key table. All subclasses of Interactor are mapped to it, too.';
    COMMENT ON COLUMN Interactor.kD IS
    'Dissociation constant of an Interaction';
    COMMENT ON COLUMN Interactor.crc64 IS
    'CRC64 checksum of the polymerSequence. Stored in hexadecimal, not integer format.';
    COMMENT ON COLUMN Interactor.formOf IS
    'References another Protein which the current one is a form of. Example: A fragment.';
    COMMENT ON COLUMN Interactor.objClass IS
    'The fully qualified classname of the object. This is needed for the OR mapping.';
    COMMENT ON COLUMN Interactor.bioSource_ac IS
    'The biological system in which the Interactor is found.';
    COMMENT ON COLUMN Interactor.interactionType_ac IS
    'The kind of interaction, e.g. covalent binding.';
    COMMENT ON COLUMN Interactor.shortLabel IS
    'A short string identifying the object, not necessarily unique. Could be e.g. a gene name. Used e.g. in selection lists. ';
    COMMENT on COLUMN Interactor.fullName IS
    'The full name of the object. ';
    COMMENT on COLUMN Interactor.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT on COLUMN Interactor.ac IS
    'Unique, auto-generated accession number.';
    COMMENT on COLUMN Interactor.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Interactor.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Interactor.timestamp IS
    'Date of the last update of the column.';
    COMMENT on COLUMN Interactor.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT ON COLUMN Interactor.polymerSeq IS
    'The polymer sequence, usually DNA or amino acid.';
set term on


PROMPT Creating table "Component"
CREATE TABLE Component
(         ac                      VARCHAR2(30)    NOT NULL
                                                  CONSTRAINT pk_Component
                                                  PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
        , created                 DATE            DEFAULT  SYSDATE NOT NULL
        , updated                 DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL 
        , interactor_ac           VARCHAR2(30)    CONSTRAINT fk_Component$interactor REFERENCES Interactor(ac)  ON DELETE CASCADE
        , interaction_ac          VARCHAR2(30)    CONSTRAINT fk_Component$interaction REFERENCES Interactor(ac) ON DELETE CASCADE
        , role                    VARCHAR2(30)    CONSTRAINT fk_Component$role REFERENCES ControlledVocab(ac)   
        , expressedIn_ac          VARCHAR2(30)    CONSTRAINT fk_Component$expressedIn REFERENCES BioSource(ac)
        , owner_ac                VARCHAR2(30)    CONSTRAINT fk_Component$owner REFERENCES Institution(ac)
        , stoichiometry           NUMBER(4,1)
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_Component$interaction_ac on Component(interaction_ac) TABLESPACE &&intactIndexTablespace;
CREATE index i_Component$interactor_ac on Component(interactor_ac) TABLESPACE &&intactIndexTablespace;

set term off
    COMMENT ON TABLE Component IS
    'Component. Link table from Interaction to Interactor. A Component is a particular instance of an Interactor which participates in an Interaction.';
    COMMENT on COLUMN Component.stoichiometry IS
    'Relative quantity of the Component participating in the Interaction.';
    COMMENT on COLUMN Component.interactor_ac IS
    'Refers to the Interactor which is participating in the Interaction.';
    COMMENT on COLUMN Component.interaction_ac IS
    'Refers to the Interaction in which the Interactor is participating.';
    COMMENT on COLUMN Component.role IS
    'The role of the Interactor in the Interaction. This is usually characterised by the experimental method. Examples: bait prey in Yeast 2-hybrid experiments.';
    COMMENT on COLUMN Component.expressedIn_ac IS
    'The biological system in which the protein has been expressed.';
    COMMENT on COLUMN Component.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT on COLUMN Component.ac IS
    'Unique auto-generated accession number.';
    COMMENT on COLUMN Component.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Component.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Component.timestamp IS
    'Date of the last update of the column.';
    COMMENT on COLUMN Component.userstamp IS
    'Database user who has performed the last update of the column.';
set term off



PROMPT Creating table "Annotation"
CREATE TABLE Annotation
(         ac                      VARCHAR2(30)    NOT NULL
                                                  CONSTRAINT pk_Annotation
                                                  PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
        , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
        , created                 DATE            DEFAULT  SYSDATE NOT NULL
        , updated                 DATE            DEFAULT  SYSDATE NOT NULL
        , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
        , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
        , topic_ac                VARCHAR2(30)    CONSTRAINT fk_Annotation$topic REFERENCES ControlledVocab(ac)
        , owner_ac                VARCHAR2(30)    CONSTRAINT fk_Annotation$owner REFERENCES Institution(ac)
        , description             VARCHAR2(4000)  
)
TABLESPACE &&intactMainTablespace
;

-- too small a table ? CREATE index i_Annotation$topic on Annotation(topic_ac);

set term off
    COMMENT ON TABLE Annotation IS
    'Contains the main biological annotation of the object.';
    COMMENT on COLUMN Annotation.description IS
    'The free text description of the annotation item.';
    COMMENT on COLUMN Annotation.topic_ac IS
    'Refers to the topic of the annotation item.';
    COMMENT on COLUMN Annotation.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT on COLUMN Annotation.ac IS
    'Unique auto-generated accession number.';
    COMMENT on COLUMN Annotation.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Annotation.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Annotation.timestamp IS
    'Date of the last update of the column.';
    COMMENT on COLUMN Annotation.userstamp IS
    'Database user who has performed the last update of the column.';
set term on


PROMPT Creating table "Experiment"
CREATE TABLE Experiment
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_Experiment
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
      , deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
      , created                 DATE            DEFAULT  SYSDATE NOT NULL
      , updated                 DATE            DEFAULT  SYSDATE NOT NULL
      , timestamp               DATE            DEFAULT  SYSDATE NOT NULL
      , userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
      , bioSource_ac            VARCHAR2(30)    CONSTRAINT fk_Experiment$bioSource REFERENCES BioSource(ac)
      , detectMethod_ac         VARCHAR2(30)    CONSTRAINT fk_Experiment$detectMethod REFERENCES ControlledVocab(ac)
      , identMethod_ac          VARCHAR2(30)    CONSTRAINT fk_Experiment$identMethod REFERENCES ControlledVocab(ac)
      , relatedExperiment_ac    VARCHAR2(30)    CONSTRAINT fk_Experiment$relatedExp REFERENCES Experiment(ac)
      , owner_ac                VARCHAR2(30)    CONSTRAINT fk_Experiment$owner REFERENCES Institution(ac)
      , shortLabel              VARCHAR2(20)
      , fullName                VARCHAR2(250) 
)
TABLESPACE &&intactMainTablespace
;

-- too small a table ? CREATE INDEX i_Experiment$shortLabel on Experiment(shortLabel);

set term off
    COMMENT ON TABLE Experiment IS
    'Describes the experiment which has yielded information about Interactions';
    COMMENT ON COLUMN Experiment.bioSource_ac IS
    'The biological system in which the experiment has been performed.';
    COMMENT on COLUMN Experiment.identMethod_ac IS
    'Refers to the method by which the Interactor has been detected as a participant in the Interaction.';
    COMMENT ON COLUMN Experiment.detectMethod_ac IS
    'Refers to the method by which the interactions have been detected in the experiment.';
    COMMENT ON COLUMN Experiment.detectMethod_ac IS
    'Refers to the method by which the interactions have been detected in the experiment.';
    COMMENT ON COLUMN Experiment.relatedExperiment_ac IS
    'An experiment which is related to the current experiment. This serves just as a pointer all information on the type of relationship will be given in the annotation of the experiment.';
    COMMENT on COLUMN Experiment.fullName IS
    'The full name of the object. ';
    COMMENT on COLUMN Experiment.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT on COLUMN Experiment.ac IS
    'Unique auto-generated accession number.';
    COMMENT on COLUMN Experiment.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Experiment.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Experiment.timestamp IS
    'Date of the last update of the column.';
    COMMENT on COLUMN Experiment.userstamp IS
    'Database user who has performed the last update of the column.';
set term on




PROMPT Creating table "Xref"
CREATE TABLE Xref
(       ac                      VARCHAR2(30)    NOT NULL
                                                CONSTRAINT pk_Xref
                                                PRIMARY KEY USING INDEX TABLESPACE &&intactIndexTablespace
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL
     ,  qualifier_ac            VARCHAR2(30)    CONSTRAINT fk_Xref$qualifier REFERENCES ControlledVocab(ac)
     ,  database_ac             VARCHAR2(30)    CONSTRAINT fk_Xref$database  REFERENCES ControlledVocab(ac)
     ,  parent_ac               VARCHAR2(30)    -- eh missing constraint here??
     ,  owner_ac                VARCHAR2(30)    CONSTRAINT fk_Xref$owner REFERENCES Institution(ac)
     ,  primaryId               VARCHAR2(30)
     ,  secondaryId             VARCHAR2(30)
     ,  dbRelease               VARCHAR2(10)
)
TABLESPACE &&intactMainTablespace
;

CREATE index i_Xref$parent_ac on Xref(parent_ac) TABLESPACE &&intactIndexTablespace;

set term on
    COMMENT ON TABLE Xref IS
    'Represents a crossreference. Several objects may have crossreferences e.g. Interactor Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
    COMMENT on COLUMN Xref.primaryId IS
    'The primary id of the object referred to in the external database.';
    COMMENT on COLUMN Xref.secondaryId IS
    'The secondary id of the object referred to in the external database.';
    COMMENT on COLUMN Xref.dbRelease IS
    'Highest release number of the external database in which the xref was known to be correct.';
    COMMENT on COLUMN Xref.qualifier_ac IS
    'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity generalisation.';
    COMMENT on COLUMN Xref.database_ac IS
    'Refers to the object describing the external database.';
    COMMENT on COLUMN Xref.parent_ac IS
    'Refers to the parent object this crossreference belongs to.';
    COMMENT on COLUMN Xref.owner_ac IS
    'Refers to the owner of this object. ';
    COMMENT on COLUMN Xref.ac IS
    'Unique auto-generated accession number.';
    COMMENT on COLUMN Xref.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Xref.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Xref.timestamp IS
    'Date of the last update of the column.';
    COMMENT on COLUMN Xref.userstamp IS
    'Database user who has performed the last update of the column.';
set term off


/* The IntactNode table is the table which will be used for
storing informations about servers. */

PROMPT Creating table "IntactNode"
CREATE TABLE IntactNode
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
      , owner_ac                    VARCHAR2(30)   CONSTRAINT fk_IntactNode$owner REFERENCES Institution(ac)
      , ftpAddress                  VARCHAR2(255)
      , ftpLogin                    VARCHAR2(255)
      , ftpPassword                 VARCHAR2(255)
      , ftpDirectory                VARCHAR2(255)
)
TABLESPACE &&intactMainTablespace
;




PROMPT Creating table "Int2Exp"
CREATE TABLE Int2Exp
(       interaction_ac          VARCHAR2(30)    NOT NULL CONSTRAINT fk_Int2Exp$interaction REFERENCES Interactor(ac)  ON DELETE CASCADE
      , experiment_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Int2Exp$experiment  REFERENCES Experiment(ac)  ON DELETE CASCADE
      , deprecated              NUMBER(1)       DEFAULT    0       NOT NULL
      , created                 DATE            DEFAULT    SYSDATE NOT NULL
      , userstamp               VARCHAR2(30)    DEFAULT    USER    NOT NULL
      , updated                 DATE            DEFAULT    SYSDATE NOT NULL
      , timestamp               DATE            DEFAULT    SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'Int2Exp'
ALTER TABLE Int2Exp
 ADD (CONSTRAINT     pk_Int2Exp
        PRIMARY KEY  (interaction_ac, experiment_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
/


set term off
    COMMENT ON TABLE Int2Exp IS
    'Link table from Interaction to Experiment.';
    COMMENT on COLUMN Int2Exp.interaction_ac IS
    'Refers to an Interation derived from an Experiment.';
    COMMENT on COLUMN Int2Exp.experiment_ac IS
    'Refers to an Experiment.';
    COMMENT on COLUMN Int2Exp.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Int2Exp.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT on COLUMN Int2Exp.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Int2Exp.timestamp IS
    'Date of the last update of the column.';
set term on



PROMPT Creating table "Int2Annot"
CREATE TABLE Int2Annot
(       interactor_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Int2Annot$interactor REFERENCES Interactor(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Int2Annot$annotation REFERENCES Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL  
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'Int2Annot'
ALTER TABLE Int2Annot
 ADD (CONSTRAINT     pk_Int2Annot
        PRIMARY KEY  (interactor_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
/

set term off
    COMMENT ON TABLE Int2Annot IS
    'Int2Annot. Link table from Annotation to Interactor.';
    COMMENT on COLUMN Int2Annot.interactor_ac IS
    'Refers to an Interactor to which the Annotation is linked.';
    COMMENT on COLUMN Int2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Interactor.';
    COMMENT on COLUMN Int2Annot.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Int2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT on COLUMN Int2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Int2Annot.timestamp IS
    'Date of the last update of the column.';
set term on


PROMPT Creating table "Exp2Annot"
CREATE TABLE Exp2Annot
(       experiment_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Exp2Annot$experiment REFERENCES Experiment(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_Exp2Annot$annotation REFERENCES Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL  
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'Exp2Annot'
ALTER TABLE Exp2Annot
 ADD (CONSTRAINT     pk_Exp2Annot
        PRIMARY KEY  (experiment_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
/

set term off
    COMMENT ON TABLE Exp2Annot IS
    'Exp2Annot. Link table from Annotation to Experiment.';
    COMMENT on COLUMN Exp2Annot.Experiment_ac IS
    'Refers to an Experiment to which the Annotation is linked.';
    COMMENT on COLUMN Exp2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Experiment.';
    COMMENT on COLUMN Exp2Annot.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Exp2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT on COLUMN Exp2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Exp2Annot.timestamp IS
    'Date of the last update of the column.';
set term on



PROMPT Creating table "cvobject2Annot"
CREATE TABLE cvobject2Annot
(       cvobject_ac             VARCHAR2(30)    NOT NULL CONSTRAINT fk_cvobj2Annot$cvobject   REFERENCES ControlledVocab(ac) ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_cvobj2Annot$annotation REFERENCES Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL  
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'cvobject2Annot'
ALTER TABLE cvobject2Annot
 ADD (CONSTRAINT     pk_cvobject2Annot
        PRIMARY KEY  (cvobject_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
/

set term off
    COMMENT ON TABLE cvobject2Annot IS
    'cvobject2Annot. Link table from Annotation to Controlled vocabulary.';
    COMMENT on COLUMN cvobject2Annot.cvobject_ac IS
    'Refers to an Controlled vocabulary to which the Annotation is linked.';
    COMMENT on COLUMN cvobject2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Controlled vocabulary.';
    COMMENT on COLUMN cvobject2Annot.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN cvobject2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT on COLUMN cvobject2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN cvobject2Annot.timestamp IS
    'Date of the last update of the column.';
set term on


PROMPT Creating table "Biosource2Annot"
CREATE TABLE Biosource2Annot
(       biosource_ac            VARCHAR2(30)    NOT NULL CONSTRAINT fk_bio2Annot$biosource   REFERENCES Biosource(ac)  ON DELETE CASCADE
     ,  annotation_ac           VARCHAR2(30)    NOT NULL CONSTRAINT fk_bio2Annot$annotation  REFERENCES Annotation(ac) ON DELETE CASCADE
     ,  deprecated              NUMBER(1)       DEFAULT  0       NOT NULL
     ,  created                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  userstamp               VARCHAR2(30)    DEFAULT  USER    NOT NULL  
     ,  updated                 DATE            DEFAULT  SYSDATE NOT NULL
     ,  timestamp               DATE            DEFAULT  SYSDATE NOT NULL
)
TABLESPACE &&intactMainTablespace
;

PROMPT Creating composite primary Key on 'Biosource2Annot'
ALTER TABLE Biosource2Annot
 ADD (CONSTRAINT     pk_Biosource2Annot
        PRIMARY KEY  (biosource_ac, annotation_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
/

set term off
    COMMENT ON TABLE Biosource2Annot IS
    'Biosource2Annot. Link table from Annotation to Biosource.';
    COMMENT on COLUMN Biosource2Annot.Biosource_ac IS
    'Refers to a Biosource to which the Annotation is linked.';
    COMMENT on COLUMN Biosource2Annot.annotation_ac IS
    'Refers to the annotation object linked to the Biosource.';
    COMMENT on COLUMN Biosource2Annot.created IS
    'Date of the creation of the row.';
    COMMENT on COLUMN Biosource2Annot.userstamp IS
    'Database user who has performed the last update of the column.';
    COMMENT on COLUMN Biosource2Annot.updated IS
    'Date of the last update of the row.';
    COMMENT on COLUMN Biosource2Annot.timestamp IS
    'Date of the last update of the column.';
set term on



/* The relation table which establishes a graph structure between CV objects */
PROMPT Creating table "Cv2Cv"
CREATE TABLE Cv2Cv
(
	    parent_ac		            VARCHAR2(30)	CONSTRAINT fk_Cv2Cv$parent  REFERENCES ControlledVocab(ac) ON DELETE CASCADE
	 ,  child_ac		            VARCHAR2(30)	CONSTRAINT fk_Cv2Cv$child	REFERENCES ControlledVocab(ac) ON DELETE CASCADE
     ,  deprecated                  NUMBER(1)       DEFAULT  0       NOT NULL
	 ,  created			            DATE		    DEFAULT  sysdate NOT NULL
	 ,  updated			            DATE		    DEFAULT  sysdate NOT NULL
	 ,  timestamp		            DATE		    DEFAULT  sysdate NOT NULL
	 ,  userstamp		            VARCHAR2(30)	DEFAULT	 USER	 NOT NULL  
)
TABLESPACE &&intactMainTablespace 
;

PROMPT Creating composite primary Key on 'Cv2Cv'
ALTER TABLE Cv2Cv
 ADD (CONSTRAINT     pk_Cv2Cv
        PRIMARY KEY  (parent_ac, child_ac)
        USING INDEX
        TABLESPACE   &&intactIndexTablespace
     )
;



/* No indexes defined yet. */

COMMENT ON TABLE Cv2Cv IS
'Cv2Cv. Link table to establish a Directed Acyclic Graph (DAG) structure for CVs.';
COMMENT on COLUMN Cv2Cv.parent_ac IS
'Refers to the parent object.';
COMMENT on COLUMN Cv2Cv.child_ac IS
'Refers to the child term.';
COMMENT on COLUMN Cv2Cv.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Cv2Cv.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Cv2Cv.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Cv2Cv.userstamp IS
'Database user who has performed the last update of the column.';




-- Sequences
PROMPT creating sequence Intact_ac
CREATE SEQUENCE Intact_ac start with 10;

set term on